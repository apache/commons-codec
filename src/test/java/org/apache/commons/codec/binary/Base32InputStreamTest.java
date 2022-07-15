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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.CodecPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Base32InputStreamTest {

    private static final String ENCODED_FOO = "MZXW6===";

    private final static byte[] CRLF = { (byte) '\r', (byte) '\n' };

    private final static byte[] LF = { (byte) '\n' };

    private static final String STRING_FIXTURE = "Hello World";

    /**
     * Tests the problem reported in CODEC-130. Missing / wrong implementation of skip.
     */
    @Test
    public void testCodec130() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final Base32OutputStream base32os = new Base32OutputStream(bos)) {
            base32os.write(StringUtils.getBytesUtf8(STRING_FIXTURE));
        }

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final Base32InputStream ins = new Base32InputStream(bis);

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
        try (final Base32InputStream in = new Base32InputStream(new Codec105ErrorInputStream(), true, 0, null)) {
            for (int i = 0; i < 5; i++) {
                in.read();
            }
        }
    }

    // /**
    // * Test for the CODEC-101 bug: InputStream.read(byte[]) should never return 0
    // * because Java's builtin InputStreamReader hates that.
    // *
    // * @throws Exception for some failure scenarios.
    // */
    // @Test
    // public void testCodec101() throws Exception {
    // byte[] codec101 = StringUtils.getBytesUtf8(Base32TestData.CODEC_101_MULTIPLE_OF_3);
    // ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
    // Base32InputStream in = new Base32InputStream(bais);
    // byte[] result = new byte[8192];
    // int c = in.read(result);
    // assertTrue("Codec101: First read successful [c=" + c + "]", c > 0);
    //
    // c = in.read(result);
    // assertTrue("Codec101: Second read should report end-of-stream [c=" + c + "]", c < 0);
    // }

    /**
     * Another test for the CODEC-101 bug: In commons-codec-1.4 this test shows InputStreamReader explicitly hating an
     * InputStream.read(byte[]) return of 0:
     *
     * java.io.IOException: Underlying input stream returned zero bytes at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:268) at
     * sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:306) at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:158) at
     * java.io.InputStreamReader.read(InputStreamReader.java:167) at java.io.BufferedReader.fill(BufferedReader.java:136) at
     * java.io.BufferedReader.readLine(BufferedReader.java:299) at java.io.BufferedReader.readLine(BufferedReader.java:362) at
     * org.apache.commons.codec.binary.Base32InputStreamTest.testInputStreamReader(Base32InputStreamTest.java:75)
     *
     * But in commons-codec-1.5 it's fixed. :-)
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    // @Test
    // public void testInputStreamReader() throws Exception {
    // byte[] codec101 = StringUtils.getBytesUtf8(Base32TestData.CODEC_101_MULTIPLE_OF_3);
    // ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
    // Base32InputStream in = new Base32InputStream(bais);
    // InputStreamReader isr = new InputStreamReader(in);
    // BufferedReader br = new BufferedReader(isr);
    // String line = br.readLine();
    // assertNotNull("Codec101:  InputStreamReader works!", line);
    // }

    /**
     * Test the Base32InputStream implementation against the special NPE inducing input identified in the CODEC-98 bug.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    // @Test
    // public void testCodec98NPE() throws Exception {
    // byte[] codec98 = StringUtils.getBytesUtf8(Base32TestData.CODEC_98_NPE);
    // ByteArrayInputStream data = new ByteArrayInputStream(codec98);
    // Base32InputStream stream = new Base32InputStream(data);
    //
    // // This line causes an NPE in commons-codec-1.4.jar:
    // byte[] decodedBytes = Base32TestData.streamToBytes(stream, new byte[1024]);
    //
    // String decoded = StringUtils.newStringUtf8(decodedBytes);
    // assertEquals(
    // "codec-98 NPE Base32InputStream", Base32TestData.CODEC_98_NPE_DECODED, decoded
    // );
    // }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws Throwable
     *             for some failure scenarios.
     */
    @Test
    public void testAvailable() throws Throwable {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            assertEquals(1, b32stream.available());
            assertEquals(3, b32stream.skip(10));
            // End of stream reached
            assertEquals(0, b32stream.available());
            assertEquals(-1, b32stream.read());
            assertEquals(-1, b32stream.read());
            assertEquals(0, b32stream.available());
        }
    }

    /**
     * Tests the Base32InputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32EmptyInputStreamMimeChuckSize() throws Exception {
        testBase32EmptyInputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    /**
     * Tests the Base32InputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32EmptyInputStreamPemChuckSize() throws Exception {
        testBase32EmptyInputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    private void testBase32EmptyInputStream(final int chuckSize) throws Exception {
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded, chuckSize, CRLF);
        testByChunk(emptyEncoded, emptyDecoded, chuckSize, CRLF);
    }

    /**
     * Tests the Base32InputStream implementation.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32InputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8(Base32TestData.BASE32_FIXTURE);
        byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA======\r\n");
        decoded = new byte[] { (byte) 0 };
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // // OpenSSL interop test.
        // encoded = StringUtils.getBytesUtf8(Base32TestData.ENCODED_32_CHARS_PER_LINE);
        // decoded = Base32TestData.DECODED;
        // testByChunk(encoded, decoded, Base32.PEM_CHUNK_SIZE, LF);
        //
        // // Single Line test.
        // String singleLine = Base32TestData.ENCODED_32_CHARS_PER_LINE.replaceAll("\n", "");
        // encoded = StringUtils.getBytesUtf8(singleLine);
        // decoded = Base32TestData.DECODED;
        // testByChunk(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base32();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    /**
     * Tests the Base32InputStream implementation.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32InputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8(Base32TestData.BASE32_FIXTURE);
        byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        testByteByByte(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA======\r\n");
        decoded = new byte[] { (byte) 0 };
        testByteByByte(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CRLF);

        // // Single Line test.
        // String singleLine = Base32TestData.ENCODED_32_CHARS_PER_LINE.replaceAll("\n", "");
        // encoded = StringUtils.getBytesUtf8(singleLine);
        // decoded = Base32TestData.DECODED;
        // testByteByByte(encoded, decoded, 0, LF);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base32();
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
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded
     *            base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base32 encoded data.
     * @param separator
     *            Line separator in the base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        InputStream in;

        in = new Base32InputStream(new ByteArrayInputStream(decoded), true, chunkSize, separator);
        byte[] output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(encoded, output, "Streaming base32 encode");

        // Now let's try to decode.
        in = new Base32InputStream(new ByteArrayInputStream(encoded));
        output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base32 decode");

        // I always wanted to do this! (wrap encoder with decoder etc.).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base32InputStream(in, true, chunkSize, separator);
            in = new Base32InputStream(in, false);
        }
        output = BaseNTestData.streamToBytes(in);

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base32 wrap-wrap-wrap!");
        in.close();
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded
     *            base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base32 encoded data.
     * @param separator
     *            Line separator in the base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        InputStream in;
        in = new Base32InputStream(new ByteArrayInputStream(decoded), true, chunkSize, separator);
        byte[] output = new byte[encoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(encoded, output, "Streaming base32 encode");

        in.close();

        // Now let's try to decode.
        in = new Base32InputStream(new ByteArrayInputStream(encoded));
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base32 decode");

        in.close();

        // I always wanted to do this! (wrap encoder with decoder etc.).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base32InputStream(in, true, chunkSize, separator);
            in = new Base32InputStream(in, false);
        }
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals(-1, in.read(), "EOF");
        assertEquals(-1, in.read(), "Still EOF");
        assertArrayEquals(decoded, output, "Streaming base32 wrap-wrap-wrap!");
    }

    /**
     * Tests markSupported.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testMarkSupported() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base32InputStream in = new Base32InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            // Always returns false for now.
            assertFalse(in.markSupported(), "Base32InputStream.markSupported() is false");
        }
    }

    /**
     * Tests read returning 0
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testRead0() throws Exception {
        final byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        int bytesRead = 0;
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base32InputStream in = new Base32InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            bytesRead = in.read(buf, 0, 0);
            assertEquals(0, bytesRead, "Base32InputStream.read(buf, 0, 0) returns 0");
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
        final byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base32InputStream in = new Base32InputStream(bin, true, 4, new byte[] {0, 0, 0})) {
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
        final byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base32InputStream in = new Base32InputStream(bin, true, 4, new byte[] { 0, 0, 0 })) {
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, -1, 0), "Base32InputStream.read(buf, -1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, 0, -1), "Base32InputStream.read(buf, 0, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length + 1, 0), "Base32InputStream.read(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> in.read(buf, buf.length - 1, 2), "Base32InputStream.read(buf, buf.length - 1, 2)");
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
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            final byte[] actualBytes = new byte[6];
            assertEquals(0, b32stream.skip(0));
            b32stream.read(actualBytes, 0, actualBytes.length);
            assertArrayEquals(actualBytes, new byte[] { 102, 111, 111, 0, 0, 0 });
            // End of stream reached
            assertEquals(-1, b32stream.read());
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
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            assertEquals(3, b32stream.skip(1024));
            // End of stream reached
            assertEquals(-1, b32stream.read());
            assertEquals(-1, b32stream.read());
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
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(3, b32stream.skip(10));
            // End of stream reached
            assertEquals(-1, b32stream.read());
            assertEquals(-1, b32stream.read());
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
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(3, b32stream.skip(3));
            // End of stream reached
            assertEquals(-1, b32stream.read());
            assertEquals(-1, b32stream.read());
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
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        try (final Base32InputStream b32stream = new Base32InputStream(ins)) {
            assertThrows(IllegalArgumentException.class, () -> b32stream.skip(-10));
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
        for (final String s : Base32Test.BASE32_IMPOSSIBLE_CASES) {
            final byte[] encoded = StringUtils.getBytesUtf8(s);
            final Base32InputStream in = new Base32InputStream(new ByteArrayInputStream(encoded), false);
            // Default is lenient decoding; it should not throw
            assertFalse(in.isStrictDecoding());
            BaseNTestData.streamToBytes(in);

            // Strict decoding should throw
            final Base32InputStream in2 = new Base32InputStream(new ByteArrayInputStream(encoded), false, 0, null, CodecPolicy.STRICT);
            assertTrue(in2.isStrictDecoding());
            assertThrows(IllegalArgumentException.class, () -> BaseNTestData.streamToBytes(in2));
        }
    }
}
