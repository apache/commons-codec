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

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base58OutputStream}.
 */
class Base58OutputStreamTest {

    private static final byte[] CR_LF = {(byte) '\r', (byte) '\n'};

    private static final byte[] LF = {(byte) '\n'};

    private void testBase58EmptyOutputStream(final int chunkSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
        testByChunk(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
    }

    @Test
    void testBase58EmptyOutputStreamMimeChunkSize() throws Exception {
        testBase58EmptyOutputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    @Test
    void testBase58EmptyOutputStreamPemChunkSize() throws Exception {
        testBase58EmptyOutputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    @Test
    void testBase58OutputStreamByChunk() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8("Hello World");
        byte[] encoded = new Base58().encode(decoded);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CR_LF);

        final BaseNCodec codec = new Base58();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    @Test
    void testBase58OutputStreamByteByByte() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8("Hello World");
        byte[] encoded = new Base58().encode(decoded);
        testByteByByte(encoded, decoded, 76, CR_LF);

        final BaseNCodec codec = new Base58();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, 0, LF);
        }
    }

    @Test
    void testBuilder() {
        assertNotNull(Base58OutputStream.builder().getBaseNCodec());
    }

    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base58OutputStream(byteOut, true)) {
            out.write(decoded);
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming chunked Base58 encode");

        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base58OutputStream(byteOut, false)) {
            out.write(encoded);
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming chunked Base58 decode");

        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base58OutputStream(out, false);
            out = new Base58OutputStream(out, true);
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals(decoded, byteOut.toByteArray(), "Streaming chunked Base58 wrap-wrap-wrap!");
    }

    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base58OutputStream(byteOut, true)) {
            for (final byte element : decoded) {
                out.write(element);
            }
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming byte-by-byte Base58 encode");

        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base58OutputStream(byteOut, false)) {
            for (final byte element : encoded) {
                out.write(element);
            }
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte Base58 decode");

        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new Base58OutputStream(byteOut, false)) {
            for (final byte element : encoded) {
                out.write(element);
                out.flush();
            }
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte flush() Base58 decode");

        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base58OutputStream(out, false);
            out = new Base58OutputStream(out, true);
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals(decoded, output, "Streaming byte-by-byte Base58 wrap-wrap-wrap!");
    }

    @Test
    void testWriteOutOfBounds() throws Exception {
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base58OutputStream out = new Base58OutputStream(bout)) {
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, -1, 1), "Base58OutputStream.write(buf, -1, 1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, 1, -1), "Base58OutputStream.write(buf, 1, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length + 1, 0), "Base58OutputStream.write(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length - 1, 2), "Base58OutputStream.write(buf, buf.length - 1, 2)");
        }
    }

    @Test
    void testWriteToNullCoverage() throws Exception {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Base58OutputStream out = new Base58OutputStream(bout)) {
            assertThrows(NullPointerException.class, () -> out.write(null, 0, 0));
        }
    }
}
