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

package org.apache.commons.codec.binary;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.CodecPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 1.4
 */
public class Base64InputStreamTest {

    /**
     * Decodes to {0, 0, 0, 255, 255, 255}
     */
    private static final String ENCODED_B64 = "AAAA////";

    private final static byte[] CRLF = { (byte) '\r', (byte) '\n' };

    private final static byte[] LF = { (byte) '\n' };

    private static final String STRING_FIXTURE = "Hello World";

    /**
     * Tests the problem reported in CODEC-130. Missing / wrong implementation of skip.
     */
    @Test
    public void testCodec130() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final Base64OutputStream base64os = new Base64OutputStream(bos)) {
            base64os.write(StringUtils.getBytesUtf8(STRING_FIXTURE));
        }

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final Base64InputStream ins = new Base64InputStream(bis);

        // we skip the first character read from the reader
        ins.skip(1);
        final byte[] decodedBytes = BaseNTestData.streamToBytes(ins, new byte[64]);
        final String str = StringUtils.newStringUtf8(decodedBytes);

        assertEquals(STRING_FIXTURE.substring(1), str);
    }

    /**
     * Tests the bug reported in CODEC-105. Bad interactions with InputStream when reading one byte at a time.
     */
    @Test
    public void testCodec105() throws IOException {
        try (final Base64InputStream in = new Base64InputStream(new Codec105ErrorInputStream(), true, 0, null)) {
            for (int i = 0; i < 5; i++) {
                in.read();
            }
        }
    }

    /**
     * Test for the CODEC-101 bug: InputStream.read(byte[]) should never return 0 because Java's builtin InputStreamReader hates that.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testCodec101() throws Exception {
        final byte[] codec101 = StringUtils.getBytesUtf8(Base64TestData.CODEC_101_INPUT_LENGTH_IS_MULTIPLE_OF_3);
        final ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
        try (final Base64InputStream in = new Base64InputStream(bais)) {
            final byte[] result = new byte[8192];
            int c = in.read(result);
            assertTrue(c > 0, "Codec101: First read successful [c=" + c + "]");

            c = in.read(result);
            assertTrue(c < 0, "Codec101: Second read should report end-of-stream [c=" + c + "]");
        }
    }

    /**
     * Another test for the CODEC-101 bug: In commons-codec-1.4 this test shows InputStreamReader explicitly hating an
     * InputStream.read(byte[]) return of 0:
     *
     * java.io.IOException: Underlying input stream returned zero bytes at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:268) at
     * sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:306) at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:158) at
     * java.io.InputStreamReader.read(InputStreamReader.java:167) at java.io.BufferedReader.fill(BufferedReader.java:136) at
     * java.io.BufferedReader.readLine(BufferedReader.java:299) at java.io.BufferedReader.readLine(BufferedReader.java:362) at
     * org.apache.commons.codec.binary.Base64InputStreamTest.testInputStreamReader(Base64InputStreamTest.java:75)
     *
     * But in commons-codec-1.5 it's fixed. :-)
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testInputStreamReader() throws Exception {
        final byte[] codec101 = StringUtils.getBytesUtf8(Base64TestData.CODEC_101_INPUT_LENGTH_IS_MULTIPLE_OF_3);
        final ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
        final Base64InputStream in = new Base64InputStream(bais);
        final InputStreamReader isr = new InputStreamReader(in);
        try (final BufferedReader br = new BufferedReader(isr)) {
            final String line = br.readLine();
            assertNotNull(line, "Codec101:  InputStreamReader works!");
        }
    }

    /**
     * Test the Base64InputStream implementation against the special NPE inducing input identified in the CODEC-98 bug.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testCodec98NPE() throws Exception {
        final byte[] codec98 = StringUtils.getBytesUtf8(Base64TestData.CODEC_98_NPE);
        final ByteArrayInputStream data = new ByteArrayInputStream(codec98);
        final Base64InputStream stream = new Base64InputStream(data);

        // This line causes an NPE in commons-codec-1.4.jar:
        final byte[] decodedBytes = BaseNTestData.streamToBytes(stream, new byte[1024]);

        final String decoded = StringUtils.newStringUtf8(decodedBytes);
        assertEquals(Base64TestData.CODEC_98_NPE_DECODED, decoded, "codec-98 NPE Base64InputStream");
    }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testAvailable() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            assertEquals(1, b64stream.available());
            assertEquals(6, b64stream.skip(10));
            // End of stream reached
            assertEquals(0, b64stream.available());
            assertEquals(-1, b64stream.read());
            assertEquals(-1, b64stream.read());
            assertEquals(0, b64stream.available());
        }
    }

    /**
     * Tests the Base64InputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase64EmptyInputStreamMimeChuckSize() throws Exception {
        testBase64EmptyInputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    /**
     * Tests the Base64InputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase64EmptyInputStreamPemChuckSize() throws Exception {
        testBase64EmptyInputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    private void testBase64EmptyInputStream(final int chuckSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded, chuckSize, CRLF);
        testByChunk(emptyEncoded, emptyDecoded, chuckSize, CRLF);
    }

    /**
     * Tests the Base64InputStream implementation.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase64InputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[] { (byte) 0 };
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = BaseNTestData.DECODED;
        testByChunk(encoded, decoded, BaseNCodec.PEM_CHUNK_SIZE, LF);

        // Single Line test.
        final String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replace("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = BaseNTestData.DECODED;
        testByChunk(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base64(0, null, false);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    /**
     * Tests the Base64InputStream implementation.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase64InputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[] { (byte) 0 };
        testByteByByte(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = BaseNTestData.DECODED;
        testByteByByte(encoded, decoded, BaseNCodec.PEM_CHUNK_SIZE, LF);

        // Single Line test.
        final String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replace("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = BaseNTestData.DECODED;
        testByteByByte(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base64(0, null, false);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, 0, LF);
        }
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded
     *            base64 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base64 encoded data.
     * @param separator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        InputStream in;
        in = new Base64InputStream(new ByteArrayInputStream(decoded), true, chunkSize, separator);
        byte[] output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(encoded, output, "Streaming base64 encode");

        in.close();

        // Now let's try to decode.
        in = new Base64InputStream(new ByteArrayInputStream(encoded));
        output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base64 decode");

        // I always wanted to do this! (wrap encoder with decoder etc.).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base64InputStream(in, true, chunkSize, separator);
            in = new Base64InputStream(in, false);
        }
        output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base64 wrap-wrap-wrap!");
        in.close();
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded
     *            base64 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base64 encoded data.
     * @param separator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        InputStream in;
        in = new Base64InputStream(new ByteArrayInputStream(decoded), true, chunkSize, separator);
        byte[] output = new byte[encoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(encoded, output, "Streaming base64 encode");

        in.close();
        // Now let's try to decode.
        in = new Base64InputStream(new ByteArrayInputStream(encoded));
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base64 decode");

        in.close();

        // I always wanted to do this! (wrap encoder with decoder etc.).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base64InputStream(in, true, chunkSize, separator);
            in = new Base64InputStream(in, false);
        }
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base64 wrap-wrap-wrap!");
        in.close();
    }

    /**
     * Tests markSupported.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testMarkSupported() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            // Always returns false for now.
            assertFalse(in.markSupported(), "Base64InputStream.markSupported() is false");
        }
    }

    /**
     * Tests read using different buffer sizes
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testReadMultipleBufferSizes() throws Exception {
        final byte[][] randomData = BaseNTestData.randomData(new Base64(0, null, false), 1024 * 64);
        final byte[] encoded = randomData[1];
        final byte[] decoded = randomData[0];
        final ByteArrayInputStream bin = new ByteArrayInputStream(encoded);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final Base64InputStream in = new Base64InputStream(bin)) {
            for (final int i : new int[] { 4 * 1024, 4 * 1024, 8 * 1024, 8 * 1024, 16 * 1024, 16 * 1024, 8 * 1024 }) {
                final byte[] buf = new byte[i];
                final int bytesRead = in.read(buf);
                assertEquals(i, bytesRead);
                out.write(buf, 0, bytesRead);
            }
        }
        assertArrayEquals(decoded, out.toByteArray());
    }

    /**
     * Tests read returning 0
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testRead0() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        int bytesRead = 0;
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            bytesRead = in.read(buf, 0, 0);
            assertEquals(0, bytesRead, "Base64InputStream.read(buf, 0, 0) returns 0");
        }
    }

    /**
     * Tests read with null.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testReadNull() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            assertThrows(NullPointerException.class, () -> in.read(null, 0, 0));
        }
    }

    /**
     * Tests read throwing IndexOutOfBoundsException
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testReadOutOfBounds() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] {0, 0, 0})) {
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, -1, 0), "Base64InputStream.read(buf, -1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, 0, -1), "Base64InputStream.read(buf, 0, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length + 1, 0), "Base64InputStream.read(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length - 1, 2), "Base64InputStream.read(buf, buf.length - 1, 2)");
        }
    }

    /**
     * Tests skipping number of characters larger than the internal buffer.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testSkipBig() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            assertEquals(6, b64stream.skip(Integer.MAX_VALUE));
            // End of stream reached
            assertEquals(-1, b64stream.read());
            assertEquals(-1, b64stream.read());
        }
    }

    /**
     * Tests skipping as a noop
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testSkipNone() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            final byte[] actualBytes = new byte[6];
            assertEquals(0, b64stream.skip(0));
            b64stream.read(actualBytes, 0, actualBytes.length);
            assertArrayEquals(actualBytes, new byte[] { 0, 0, 0, (byte) 255, (byte) 255, (byte) 255 });
            // End of stream reached
            assertEquals(-1, b64stream.read());
        }
    }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testSkipPastEnd() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(6, b64stream.skip(10));
            // End of stream reached
            assertEquals(-1, b64stream.read());
            assertEquals(-1, b64stream.read());
        }
    }

    /**
     * Tests skipping to the end of a stream.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testSkipToEnd() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(6, b64stream.skip(6));
            // End of stream reached
            assertEquals(-1, b64stream.read());
            assertEquals(-1, b64stream.read());
        }
    }

    /**
     * Tests if negative arguments to skip are handled correctly.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testSkipWrongArgument() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        try (final Base64InputStream b64stream = new Base64InputStream(ins)) {
            assertThrows(IllegalArgumentException.class, () -> b64stream.skip(-10));
        }
    }

    /**
     * Test strict decoding.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testStrictDecoding() throws Exception {
        for (final String s : Base64Test.BASE64_IMPOSSIBLE_CASES) {
            final byte[] encoded = StringUtils.getBytesUtf8(s);
            final Base64InputStream in = new Base64InputStream(new ByteArrayInputStream(encoded), false);
            // Default is lenient decoding; it should not throw
            assertFalse(in.isStrictDecoding());
            BaseNTestData.streamToBytes(in);

            // Strict decoding should throw
            final Base64InputStream in2 = new Base64InputStream(new ByteArrayInputStream(encoded), false, 0, null, CodecPolicy.STRICT);
            assertTrue(in2.isStrictDecoding());
            assertThrows(IllegalArgumentException.class, () -> BaseNTestData.streamToBytes(in2));
        }
    }
}
