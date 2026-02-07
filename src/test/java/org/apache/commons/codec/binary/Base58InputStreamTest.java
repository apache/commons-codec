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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base58InputStream}.
 */
class Base58InputStreamTest {

    private static final byte[] CRLF = { (byte) '\r', (byte) '\n' };

    private static final byte[] LF = { (byte) '\n' };

    private static final String STRING_FIXTURE = "Hello World";

    @Test
    void testAvailable() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            final int initialAvailable = b58stream.available();
            assertTrue(initialAvailable > 0, "Initial available should be greater than 0");
            assertEquals(3, b58stream.skip(10), "Skip should return 3 (decoded bytes)");
            assertEquals(0, b58stream.available());
            assertEquals(-1, b58stream.read());
            assertEquals(-1, b58stream.read());
        }
    }

    private void testBase58EmptyInputStream(final int chuckSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByChunk(emptyEncoded, emptyDecoded, chuckSize, CRLF);
        testByteByByte(emptyEncoded, emptyDecoded, chuckSize, CRLF);
    }

    /**
     * Tests the Base58InputStream implementation against empty input.
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testBase58EmptyInputStreamMimeChuckSize() throws Exception {
        testBase58EmptyInputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    /**
     * Tests the Base58InputStream implementation against empty input.
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testBase58EmptyInputStreamPemChuckSize() throws Exception {
        testBase58EmptyInputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    @Test
    void testBase58InputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] encoded = new Base58().encode(decoded);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);
        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base58();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    @Test
    void testBase58InputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] encoded = new Base58().encode(decoded);
        testByteByByte(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);
        // test random data of sizes 0 through 150
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
        assertNotNull(Base58InputStream.builder().getBaseNCodec());
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base58InputStream wraps itself in encode and decode mode over and over again.
     *
     * @param encoded   base58 encoded data
     * @param decoded   the data from above, but decoded
     * @param chunkSize chunk size (line-length) of the base58 encoded data.
     * @param separator Line separator in the base58 encoded data.
     * @throws Exception Usually signifies a bug in the Base58 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        try (InputStream in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get()) {
            final byte[] output = BaseNTestData.streamToBytes(in);
            assertEquals(-1, in.read(), "EOF");
            assertEquals(-1, in.read(), "Still EOF");
            assertArrayEquals(encoded, output, "Streaming base58 encode");
        }
        try (InputStream in = new Base58InputStream(new ByteArrayInputStream(encoded))) {
            final byte[] output = BaseNTestData.streamToBytes(in);
            assertEquals(-1, in.read(), "EOF");
            assertEquals(-1, in.read(), "Still EOF");
            assertArrayEquals(decoded, output, "Streaming base58 decode");
        }
        InputStream in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = Base58InputStream.builder().setInputStream(in).setEncode(true).get();
            in = Base58InputStream.builder().setInputStream(in).setEncode(false).get();
        }
        final byte[] output = BaseNTestData.streamToBytes(in);
        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base58 wrap-wrap-wrap!");
        in.close();
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base58InputStream wraps itself in encode and decode mode over and over again.
     *
     * @param encoded   base58 encoded data
     * @param decoded   the data from above, but decoded
     * @param chunkSize chunk size (line-length) of the base58 encoded data.
     * @param separator Line separator in the base58 encoded data.
     * @throws Exception Usually signifies a bug in the Base58 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {
        InputStream in;
        in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get();
        byte[] output = BaseNTestData.streamToBytes(in);
        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(encoded, output, "Streaming base58 encode");
        in.close();
        in = new Base58InputStream(new ByteArrayInputStream(encoded));
        output = BaseNTestData.streamToBytes(in);
        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base58 decode");
        in.close();
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = Base58InputStream.builder().setInputStream(in).setEncode(true).get();
            in = Base58InputStream.builder().setInputStream(in).setEncode(false).get();
        }
        output = BaseNTestData.streamToBytes(in);
        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base58 wrap-wrap-wrap!");
    }

    /**
     * Tests markSupported.
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testMarkSupported() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        try (Base58InputStream in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get()) {
            // Always returns false for now.
            assertFalse(in.markSupported(), "Base58InputStream.markSupported() is false");
        }
    }

    /**
     * Tests read returning 0
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testRead0() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        int bytesRead = 0;
        try (Base58InputStream in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get()) {
            bytesRead = in.read(buf, 0, 0);
            assertEquals(0, bytesRead, "Base58InputStream.read(buf, 0, 0) returns 0");
        }
    }

    /**
     * Tests read with null.
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testReadNull() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        try (Base58InputStream in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get()) {
            assertThrows(NullPointerException.class, () -> in.read(null, 0, 0));
        }
    }

    /**
     * Tests read throwing IndexOutOfBoundsException
     *
     * @throws Exception for some failure scenarios.
     */
    @Test
    void testReadOutOfBounds() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        try (Base58InputStream in = Base58InputStream.builder().setByteArray(decoded).setEncode(true).get()) {
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, -1, 0), "Base58InputStream.read(buf, -1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, 0, -1), "Base58InputStream.read(buf, 0, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length + 1, 0), "Base58InputStream.read(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length - 1, 2), "Base58InputStream.read(buf, buf.length - 1, 2)");
        }
    }

    /**
     * Tests skipping number of characters larger than the internal buffer.
     *
     * @throws Throwable for some failure scenarios.
     */
    @Test
    void testSkipBig() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            assertEquals(3, b58stream.skip(1024));
            // End of stream reached
            assertEquals(-1, b58stream.read());
            assertEquals(-1, b58stream.read());
        }
    }

    /**
     * Tests skipping as a noop
     *
     * @throws Throwable for some failure scenarios.
     */
    @Test
    void testSkipNone() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            final byte[] actualBytes = new byte[3];
            assertEquals(0, b58stream.skip(0));
            b58stream.read(actualBytes, 0, actualBytes.length);
            assertArrayEquals(actualBytes, new byte[] { 102, 111, 111 });
            // End of stream reached
            assertEquals(-1, b58stream.read());
        }
    }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws Throwable for some failure scenarios.
     */
    @Test
    void testSkipPastEnd() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            // skip correctly decoded characters
            assertEquals(3, b58stream.skip(10));
            // End of stream reached
            assertEquals(-1, b58stream.read());
            assertEquals(-1, b58stream.read());
        }
    }

    /**
     * Tests skipping to the end of a stream.
     *
     * @throws Throwable for some failure scenarios.
     */
    @Test
    void testSkipToEnd() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            // skip correctly decoded characters
            assertEquals(3, b58stream.skip(3));
            assertEquals(-1, b58stream.read());
        }
    }

    /**
     * Tests if negative arguments to skip are handled correctly.
     *
     * @throws Throwable for some failure scenarios.
     */
    @Test
    void testSkipWrongArgument() throws Throwable {
        final String encoded = new String(new Base58().encode(StringUtils.getBytesUtf8("foo")));
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(encoded));
        try (Base58InputStream b58stream = new Base58InputStream(ins)) {
            assertThrows(IllegalArgumentException.class, () -> b58stream.skip(-1));
        }
    }
}
