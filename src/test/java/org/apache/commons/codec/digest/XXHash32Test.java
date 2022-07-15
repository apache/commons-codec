/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.digest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XXHash32Test {

    private File file;
    private String expectedChecksum;

    public void initData(final String path, final String c) throws IOException {
        final URL url = XXHash32Test.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("couldn't find " + path);
        }
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (final java.net.URISyntaxException ex) {
            throw new IOException(ex);
        }
        file = new File(uri);
        expectedChecksum = c;
    }

   public static Stream<Arguments> data() {
        return Stream.of(
            // reference checksums created with xxh32sum
            // http://cyan4973.github.io/xxHash/
            Arguments.of( "org/apache/commons/codec/bla.tar", "fbb5c8d1" ),
            Arguments.of( "org/apache/commons/codec/bla.tar.xz", "4106a208" ),
            Arguments.of( "org/apache/commons/codec/small.bin", "f66c26f8" )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyChecksum(final String path, final String c) throws IOException {
        initData(path, c);
        final XXHash32 h = new XXHash32();
        try (final FileInputStream s = new FileInputStream(file)) {
            final byte[] b = toByteArray(s);
            h.update(b, 0, b.length);
        }
        assertEquals(expectedChecksum, Long.toHexString(h.getValue()), "checksum for " + file.getName());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyIncrementalChecksum(final String path, final String c) throws IOException {
        initData(path, c);
        final XXHash32 h = new XXHash32();
        try (final FileInputStream s = new FileInputStream(file)) {
            final byte[] b = toByteArray(s);
            // Hit the case where the hash should be reset
            h.update(b[0]);
            h.reset();
            // Pass in chunks
            h.update(b[0]);
            h.update(b, 1, b.length - 2);
            h.update(b, b.length - 1, 1);
            // Check the hash ignores negative length
            h.update(b, 0, -1);
        }
        assertEquals(expectedChecksum, Long.toHexString(h.getValue()), "checksum for " + file.getName());
    }

    private static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, 10240);
        return output.toByteArray();
    }

    private static long copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        long count=0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
