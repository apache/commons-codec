/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.digest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class XXHash32Test {

    private static long copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        return IOUtils.copyLarge(input, output, new byte[bufferSize]);
    }

    public static Stream<Arguments> data() {
        // @formatter:off
        return Stream.of(
            // reference checksums created with xxh32sum
            // https://cyan4973.github.io/xxHash/
            Arguments.of("org/apache/commons/codec/bla.tar", "fbb5c8d1"),
            Arguments.of("org/apache/commons/codec/bla.tar.xz", "4106a208"),
            Arguments.of("org/apache/commons/codec/small.bin", "f66c26f8")
        );
        // @formatter:on
    }

    private static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, 10240);
        return output.toByteArray();
    }

    private Path file;

    private String expectedChecksum;

    public void initData(final String path, final String c) throws Exception {
        final URL url = XXHash32Test.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("couldn't find " + path);
        }
        file = Paths.get(url.toURI());
        expectedChecksum = c;
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyChecksum(final String path, final String c) throws Exception {
        initData(path, c);
        final XXHash32 hasher = new XXHash32();
        try (InputStream in = Files.newInputStream(file)) {
            final byte[] bytes = toByteArray(in);
            hasher.update(bytes, 0, bytes.length);
        }
        assertEquals(expectedChecksum, Long.toHexString(hasher.getValue()), "checksum for " + file);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyIncrementalChecksum(final String path, final String c) throws Exception {
        initData(path, c);
        final XXHash32 hasher = new XXHash32();
        try (InputStream in = Files.newInputStream(file)) {
            final byte[] bytes = toByteArray(in);
            // Hit the case where the hash should be reset
            hasher.update(bytes[0]);
            hasher.reset();
            // Pass in chunks
            hasher.update(bytes[0]);
            hasher.update(bytes, 1, bytes.length - 2);
            hasher.update(bytes, bytes.length - 1, 1);
            // Check the hash ignores negative length
            hasher.update(bytes, 0, -1);
        }
        assertEquals(expectedChecksum, Long.toHexString(hasher.getValue()), "checksum for " + file);
    }
}
