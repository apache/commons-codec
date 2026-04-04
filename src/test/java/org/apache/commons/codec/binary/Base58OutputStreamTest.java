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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayFill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link Base58OutputStream}.
 */
class Base58OutputStreamTest extends AbstractBaseNOutputStreamTest {

    private static final byte[] CR_LF = { (byte) '\r', (byte) '\n' };

    private static final byte[] LF = { (byte) '\n' };

    @Override
    OutputStream newOutputStream() {
        return new Base58OutputStream(new ByteArrayOutputStream());
    }

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
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(true).get()) {
            out.write(decoded);
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(encoded, output, "Streaming chunked Base58 encode");
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            out.write(encoded);
        }
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming chunked Base58 decode");
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = Base58OutputStream.builder().setOutputStream(out).setEncode(false).get();
            out = Base58OutputStream.builder().setOutputStream(out).setEncode(true).get();
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, byteOut.toByteArray(), "Streaming chunked Base58 wrap-wrap-wrap!");
    }

    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(true).get()) {
            for (final byte element : decoded) {
                out.write(element);
            }
        }
        final byte[] output0 = byteOut.toByteArray();
        assertArrayEquals(encoded, output0, "Streaming byte-by-byte Base58 encode");
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            for (final byte element : encoded) {
                out.write(element);
            }
        }
        final byte[] output1 = byteOut.toByteArray();
        assertArrayEquals(decoded, output1,
                () -> String.format("Streaming byte-by-byte Base58 decode, chunkSize=%d, separator=%s, encoded=%s, decoded=%s, output=%s", chunkSize,
                        Arrays.toString(separator), Arrays.toString(encoded), Arrays.toString(decoded), Arrays.toString(output1)));
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            for (final byte element : encoded) {
                out.write(element);
                out.flush();
            }
        }
        byte[] output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte flush() Base58 decode");
        byteOut = new ByteArrayOutputStream();
        OutputStream out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = Base58OutputStream.builder().setOutputStream(out).setEncode(false).get();
            out = Base58OutputStream.builder().setOutputStream(out).setEncode(true).get();
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals(decoded, output, "Streaming byte-by-byte Base58 wrap-wrap-wrap!");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testDecodeByte49(final int len) throws IOException {
        // Sanity check, each step from scratch:
        final byte[] zeros = new byte[len];
        final byte[] encoded0s = ArrayFill.fill(zeros.clone(), (byte) '1');
        assertArrayEquals(encoded0s, Base58.builder().get().encode(zeros));
        final byte[] decoded = Base58.builder().get().decode(encoded0s);
        assertArrayEquals(zeros, decoded, () -> String.format("zeros=%s, decoded=%s", Arrays.toString(zeros), Arrays.toString(decoded)));
        // Version 1.21.1:
        // AssertionFailedError: Streaming byte-by-byte Base58 decode, chunkSize=0, separator=[10], encoded=[49], decoded=[0], output=[0, 0] ==> array lengths
        // differ, expected: <1> but was: <2>
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final byte[] byteData = new byte[len];
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(true).get()) {
            for (final byte element : byteData) {
                out.write(element);
            }
        }
        final byte[] output0 = byteOut.toByteArray();
        assertArrayEquals(encoded0s, output0, () -> String.format("Streaming byte-by-byte Base58 decode, encoded=%s, decoded=%s, output=%s",
                Arrays.toString(encoded0s), Arrays.toString(byteData), Arrays.toString(output0)));
        byteOut = new ByteArrayOutputStream();
        try (OutputStream out = Base58OutputStream.builder().setOutputStream(byteOut).setEncode(false).get()) {
            for (final byte element : encoded0s) {
                out.write(element);
            }
        }
        final byte[] output1 = byteOut.toByteArray();
        assertArrayEquals(byteData, output1, () -> String.format("Streaming byte-by-byte Base58 decode, encoded=%s, decoded=%s, output=%s",
                Arrays.toString(encoded0s), Arrays.toString(byteData), Arrays.toString(output1)));
    }

    @Test
    void testRfcTestVector1() {
        assertEquals("2NEpo7TZRRrLZSi2U", Base58.builder().get().encodeToString("Hello World!".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void testRfcTestVector2() {
        assertEquals("USm3fpXnKG5EUBx2ndxBDMPVciP5hGey2Jh4NDv6gmeo1LkMeiKrLJUUBk6Z",
                Base58.builder().get().encodeToString("The quick brown fox jumps over the lazy dog.".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void testRfcTestVector3() {
        assertEquals("11233QC4", Base58.builder().get().encodeToString(new byte[] { 00, 00, 0x28, 0x7f, (byte) 0xb4, (byte) 0xcd }));
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
