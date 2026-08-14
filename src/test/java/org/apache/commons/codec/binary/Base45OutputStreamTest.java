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

package org.apache.commons.codec.binary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base45OutputStream}.
 */
class Base45OutputStreamTest extends AbstractBaseNOutputStreamTest {

    private static final byte[] CR_LF = { (byte) '\r', (byte) '\n' };
    private static final byte[] LF = { (byte) '\n' };

    @Override
    OutputStream newOutputStream() {
        return new Base45OutputStream(new ByteArrayOutputStream());
    }

    private void testBase45EmptyOutputStream(final int chunkSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
        testByChunk(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
    }

    @Test
    void testBase45EmptyOutputStreamMimeChunkSize() throws Exception {
        testBase45EmptyOutputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    @Test
    void testBase45EmptyOutputStreamPemChunkSize() throws Exception {
        testBase45EmptyOutputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    @Test
    void testBase45OutputStreamByChunk() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8("Hello World");
        byte[] encoded = new Base45().encode(decoded);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CR_LF);
        final BaseNCodec codec = new Base45();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    @Test
    void testBase45OutputStreamByteByByte() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8("Hello World");
        byte[] encoded = new Base45().encode(decoded);
        testByteByByte(encoded, decoded, 76, CR_LF);
        final BaseNCodec codec = new Base45();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, 0, LF);
        }
    }

    @Test
    void testBuilder() {
        assertNotNull(Base45OutputStream.builder().getBaseNCodec());
    }

    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base45OutputStream.builder().setOutputStream(byteOut).setEncode(true).get()) {
            out.write(decoded);
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming chunked Base45 encode");
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base45OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            out.write(encoded);
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming chunked Base45 decode");
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = Base45OutputStream.builder().setOutputStream(out).setEncode(false).get();
            out = Base45OutputStream.builder().setOutputStream(out).setEncode(true).get();
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, byteOut.toByteArray(), "Streaming chunked Base45 wrap-wrap-wrap!");
    }

    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base45OutputStream.builder().setOutputStream(byteOut).setEncode(true).get()) {
            for (final byte element : decoded) {
                out.write(element);
            }
        }
        final byte[] output0 = byteOut.toByteArray();
        assertArrayEquals(encoded, output0, "Streaming byte-by-byte Base45 encode");
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base45OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            for (final byte element : encoded) {
                out.write(element);
            }
        }
        final byte[] output1 = byteOut.toByteArray();
        assertArrayEquals(decoded, output1,
                () -> String.format("Streaming byte-by-byte Base45 decode, chunkSize=%d, separator=%s, encoded=%s, decoded=%s, output=%s", chunkSize,
                        Arrays.toString(separator), Arrays.toString(encoded), Arrays.toString(decoded), Arrays.toString(output1)));
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base45OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            for (final byte element : encoded) {
                out.write(element);
                out.flush();
            }
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte flush() Base45 decode");
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = Base45OutputStream.builder().setOutputStream(out).setEncode(false).get();
            out = Base45OutputStream.builder().setOutputStream(out).setEncode(true).get();
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte Base45 wrap-wrap-wrap!");
    }

    @Test
    void testWriteOutOfBounds() throws Exception {
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base45OutputStream out = new Base45OutputStream(bout)) {
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, -1, 1), "Base45OutputStream.write(buf, -1, 1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, 1, -1), "Base45OutputStream.write(buf, 1, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length + 1, 0), "Base45OutputStream.write(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length - 1, 2), "Base45OutputStream.write(buf, buf.length - 1, 2)");
        }
    }

    @Test
    void testWriteToNullCoverage() throws Exception {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base45OutputStream out = new Base45OutputStream(bout)) {
            assertThrows(NullPointerException.class, () -> out.write(null, 0, 0));
        }
    }
}
