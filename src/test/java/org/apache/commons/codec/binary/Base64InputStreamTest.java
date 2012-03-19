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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.junit.Test;

/**
 * @version $Id $
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Base64OutputStream base64os = new Base64OutputStream(bos);

        base64os.write(StringUtils.getBytesUtf8(STRING_FIXTURE));
        base64os.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        Base64InputStream ins = new Base64InputStream(bis);

        // we skip the first character read from the reader
        ins.skip(1);
        byte[] decodedBytes = Base64TestData.streamToBytes(ins, new byte[64]);
        String str = StringUtils.newStringUtf8(decodedBytes);

        assertEquals(STRING_FIXTURE.substring(1), str);
    }

    /**
     * Tests the bug reported in CODEC-105. Bad interactions with InputStream when reading one byte at a time.
     */
    @Test
    public void testCodec105() throws IOException {
        Base64InputStream in = new Base64InputStream(new Codec105ErrorInputStream(), true, 0, null);
        try {
            for (int i = 0; i < 5; i++) {
                in.read();
            }
        } finally {
            in.close();
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
        byte[] codec101 = StringUtils.getBytesUtf8(Base64TestData.CODEC_101_MULTIPLE_OF_3);
        ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
        Base64InputStream in = new Base64InputStream(bais);
        byte[] result = new byte[8192];
        int c = in.read(result);
        assertTrue("Codec101: First read successful [c=" + c + "]", c > 0);

        c = in.read(result);
        assertTrue("Codec101: Second read should report end-of-stream [c=" + c + "]", c < 0);
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
        byte[] codec101 = StringUtils.getBytesUtf8(Base64TestData.CODEC_101_MULTIPLE_OF_3);
        ByteArrayInputStream bais = new ByteArrayInputStream(codec101);
        Base64InputStream in = new Base64InputStream(bais);
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        assertNotNull("Codec101:  InputStreamReader works!", line);
    }

    /**
     * Test the Base64InputStream implementation against the special NPE inducing input identified in the CODEC-98 bug.
     * 
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testCodec98NPE() throws Exception {
        byte[] codec98 = StringUtils.getBytesUtf8(Base64TestData.CODEC_98_NPE);
        ByteArrayInputStream data = new ByteArrayInputStream(codec98);
        Base64InputStream stream = new Base64InputStream(data);

        // This line causes an NPE in commons-codec-1.4.jar:
        byte[] decodedBytes = Base64TestData.streamToBytes(stream, new byte[1024]);

        String decoded = StringUtils.newStringUtf8(decodedBytes);
        assertEquals("codec-98 NPE Base64InputStream", Base64TestData.CODEC_98_NPE_DECODED, decoded);
    }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws Throwable
     */
    @Test
    public void testAvailable() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        assertEquals(1, b64stream.available());
        assertEquals(6, b64stream.skip(10));
        // End of stream reached
        assertEquals(0, b64stream.available());
        assertEquals(-1, b64stream.read());
        assertEquals(-1, b64stream.read());
        assertEquals(0, b64stream.available());
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

    private void testBase64EmptyInputStream(int chuckSize) throws Exception {
        byte[] emptyEncoded = new byte[0];
        byte[] emptyDecoded = new byte[0];
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
        decoded = Base64TestData.DECODED;
        testByChunk(encoded, decoded, BaseNCodec.PEM_CHUNK_SIZE, LF);

        // Single Line test.
        String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replaceAll("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = Base64TestData.DECODED;
        testByChunk(encoded, decoded, 0, LF);

        // test random data of sizes 0 thru 150
        for (int i = 0; i <= 150; i++) {
            byte[][] randomData = Base64TestData.randomData(i, false);
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
        decoded = Base64TestData.DECODED;
        testByteByByte(encoded, decoded, BaseNCodec.PEM_CHUNK_SIZE, LF);

        // Single Line test.
        String singleLine = Base64TestData.ENCODED_64_CHARS_PER_LINE.replaceAll("\n", "");
        encoded = StringUtils.getBytesUtf8(singleLine);
        decoded = Base64TestData.DECODED;
        testByteByByte(encoded, decoded, 0, LF);

        // test random data of sizes 0 thru 150
        for (int i = 0; i <= 150; i++) {
            byte[][] randomData = Base64TestData.randomData(i, false);
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
     * @param seperator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByChunk(byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator) throws Exception {

        // Start with encode.
        InputStream in = new ByteArrayInputStream(decoded);
        in = new Base64InputStream(in, true, chunkSize, seperator);
        byte[] output = Base64TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 encode", Arrays.equals(output, encoded));

        // Now let's try decode.
        in = new ByteArrayInputStream(encoded);
        in = new Base64InputStream(in);
        output = Base64TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 decode", Arrays.equals(output, decoded));

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base64InputStream(in, true, chunkSize, seperator);
            in = new Base64InputStream(in, false);
        }
        output = Base64TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 wrap-wrap-wrap!", Arrays.equals(output, decoded));
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
     * @param seperator
     *            Line separator in the base64 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByteByByte(byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator) throws Exception {

        // Start with encode.
        InputStream in = new ByteArrayInputStream(decoded);
        in = new Base64InputStream(in, true, chunkSize, seperator);
        byte[] output = new byte[encoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 encode", Arrays.equals(output, encoded));

        // Now let's try decode.
        in = new ByteArrayInputStream(encoded);
        in = new Base64InputStream(in);
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 decode", Arrays.equals(output, decoded));

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base64InputStream(in, true, chunkSize, seperator);
            in = new Base64InputStream(in, false);
        }
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base64 wrap-wrap-wrap!", Arrays.equals(output, decoded));
    }

    /**
     * Tests markSupported.
     * 
     * @throws Exception
     */
    @Test
    public void testMarkSupported() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 });
        // Always returns false for now.
        assertFalse("Base64InputStream.markSupported() is false", in.markSupported());
    }

    /**
     * Tests read returning 0
     * 
     * @throws Exception
     */
    @Test
    public void testRead0() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] buf = new byte[1024];
        int bytesRead = 0;
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 });
        bytesRead = in.read(buf, 0, 0);
        assertEquals("Base64InputStream.read(buf, 0, 0) returns 0", 0, bytesRead);
    }

    /**
     * Tests read with null.
     * 
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testReadNull() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 });
        try {
            in.read(null, 0, 0);
            fail("Base64InputStream.read(null, 0, 0) to throw a NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Tests read throwing IndexOutOfBoundsException
     * 
     * @throws Exception
     */
    @Test
    public void testReadOutOfBounds() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] buf = new byte[1024];
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[] { 0, 0, 0 });

        try {
            in.read(buf, -1, 0);
            fail("Expected Base64InputStream.read(buf, -1, 0) to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            in.read(buf, 0, -1);
            fail("Expected Base64InputStream.read(buf, 0, -1) to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            in.read(buf, buf.length + 1, 0);
            fail("Base64InputStream.read(buf, buf.length + 1, 0) throws IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            in.read(buf, buf.length - 1, 2);
            fail("Base64InputStream.read(buf, buf.length - 1, 2) throws IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * Tests skipping number of characters larger than the internal buffer.
     *
     * @throws Throwable
     */
    @Test
    public void testSkipBig() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        assertEquals(6, b64stream.skip(1024));
        // End of stream reached
        assertEquals(-1, b64stream.read());
        assertEquals(-1, b64stream.read());
    }

    /**
     * Tests skipping as a noop
     * 
     * @throws Throwable
     */
    @Test
    public void testSkipNone() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        byte[] actualBytes = new byte[6];
        assertEquals(0, b64stream.skip(0));
        b64stream.read(actualBytes, 0, actualBytes.length);
        assertArrayEquals(actualBytes, new byte[] { 0, 0, 0, (byte) 255, (byte) 255, (byte) 255 });
        // End of stream reached
        assertEquals(-1, b64stream.read());
    }

    /**
     * Tests skipping past the end of a stream.
     * 
     * @throws Throwable
     */
    @Test
    public void testSkipPastEnd() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
        assertEquals(6, b64stream.skip(10));
        // End of stream reached
        assertEquals(-1, b64stream.read());
        assertEquals(-1, b64stream.read());
    }

    /**
     * Tests skipping to the end of a stream.
     * 
     * @throws Throwable
     */
    @Test
    public void testSkipToEnd() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
        assertEquals(6, b64stream.skip(6));
        // End of stream reached
        assertEquals(-1, b64stream.read());
        assertEquals(-1, b64stream.read());
    }

    /**
     * Tests if negative arguments to skip are handled correctly.
     *
     * @throws Throwable
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSkipWrongArgument() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B64));
        Base64InputStream b64stream = new Base64InputStream(ins);
        b64stream.skip(-10);
    }
}
