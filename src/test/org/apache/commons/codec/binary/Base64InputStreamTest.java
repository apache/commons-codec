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
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author Apache Software Foundation
 * @version $Id $
 * @since 1.4
 */
public class Base64InputStreamTest extends TestCase {

    private final static byte[] CRLF = {(byte) '\r', (byte) '\n'};

    private final static byte[] LF = {(byte) '\n'};

    private static final String STRING_FIXTURE = "Hello World";

    /**
     * Construct a new instance of this test case.
     * 
     * @param name
     *            Name of the test case
     */
    public Base64InputStreamTest(String name) {
        super(name);
    }

    /**
     * Tests the Base64InputStream implementation against empty input.
     * 
     * @throws Exception
     *             for some failure scenarios.
     */
    public void testBase64EmptyInputStream() throws Exception {
        byte[] emptyEncoded = new byte[0];
        byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded, 76, CRLF);
        testByChunk(emptyEncoded, emptyDecoded, 76, CRLF);
    }

    /**
     * Tests the Base64InputStream implementation.
     * 
     * @throws Exception
     *             for some failure scenarios.
     */
    public void testBase64InputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded, 76, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[]{(byte) 0};
        testByChunk(encoded, decoded, 76, CRLF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = Base64TestData.DECODED;
        testByChunk(encoded, decoded, 64, LF);

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
    public void testBase64InputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("SGVsbG8gV29ybGQ=\r\n");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded, 76, CRLF);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("AA==\r\n");
        decoded = new byte[]{(byte) 0};
        testByteByByte(encoded, decoded, 76, CRLF);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base64TestData.ENCODED_64_CHARS_PER_LINE);
        decoded = Base64TestData.DECODED;
        testByteByByte(encoded, decoded, 64, LF);

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
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64InputStream wraps itself in encode and decode mode
     * over and over again.
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
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base64InputStream wraps itself in encode and decode mode
     * over and over again.
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
    public void testMarkSupported() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[]{0, 0, 0});
        // Always returns false for now.
        assertFalse("Base64InputStream.markSupported() is false", in.markSupported());
    }

    /**
     * Tests read returning 0
     * 
     * @throws Exception
     */
    public void testRead0() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] buf = new byte[1024];
        int bytesRead = 0;
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[]{0, 0, 0});
        bytesRead = in.read(buf, 0, 0);
        assertEquals("Base64InputStream.read(buf, 0, 0) returns 0", 0, bytesRead);
    }

    /**
     * Tests read with null.
     * 
     * @throws Exception
     *             for some failure scenarios.
     */
    public void testReadNull() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[]{0, 0, 0});
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
    public void testReadOutOfBounds() throws Exception {
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        byte[] buf = new byte[1024];
        ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        Base64InputStream in = new Base64InputStream(bin, true, 4, new byte[]{0, 0, 0});

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
}
