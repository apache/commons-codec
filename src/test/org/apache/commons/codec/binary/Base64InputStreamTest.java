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


import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Apache Software Foundation
 * @version $Id $
 */
public class Base64InputStreamTest extends TestCase {

    private final static byte[] CRLF = {(byte) '\r', (byte) '\n'};
    private final static byte[] LF = {(byte) '\n'};

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public Base64InputStreamTest(String name) {
        super(name);
    }

    /**
     * Test the Base64InputStream implementation against empty input.
     *
     * @throws Exception for some failure scenarios.
     */    
    public void testBase64EmptyInputStream() throws Exception {
        byte[] emptyEncoded = new byte[0];
        byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded, 76, CRLF);
        testByChunk(emptyEncoded, emptyDecoded, 76, CRLF);
    }

    /**
     * Test the Base64InputStream implementation.
     *
     * @throws Exception for some failure scenarios.
     */
    public void testBase64InputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = "SGVsbG8gV29ybGQ=\r\n".getBytes("UTF-8");
        byte[] decoded = "Hello World".getBytes("UTF-8");
        testByteByByte(encoded, decoded, 76, CRLF);

        // Single Byte test.
        encoded = "AA==\r\n".getBytes("UTF-8");
        decoded = new byte[]{(byte) 0};
        testByteByByte(encoded, decoded, 76, CRLF);

        // OpenSSL interop test.
        encoded = Base64TestData.ENCODED.getBytes("UTF-8");
        decoded = Base64TestData.DECODED;
        testByteByByte(encoded, decoded, 64, LF);

        // Single Line test.
        String singleLine = Base64TestData.ENCODED.replaceAll("\n", "");
        encoded = singleLine.getBytes("UTF-8");
        decoded = Base64TestData.DECODED;
        testByteByByte(encoded, decoded, 0, LF);
    }

    /**
     * Test the Base64InputStream implementation.
     *
     * @throws Exception for some failure scenarios.
     */
    public void testBase64InputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = "SGVsbG8gV29ybGQ=\r\n".getBytes("UTF-8");
        byte[] decoded = "Hello World".getBytes("UTF-8");
        testByChunk(encoded, decoded, 76, CRLF);

        // Single Byte test.
        encoded = "AA==\r\n".getBytes("UTF-8");
        decoded = new byte[]{(byte) 0};
        testByChunk(encoded, decoded, 76, CRLF);

        // OpenSSL interop test.
        encoded = Base64TestData.ENCODED.getBytes("UTF-8");
        decoded = Base64TestData.DECODED;
        testByChunk(encoded, decoded, 64, LF);

        // Single Line test.
        String singleLine = Base64TestData.ENCODED.replaceAll("\n", "");
        encoded = singleLine.getBytes("UTF-8");
        decoded = Base64TestData.DECODED;
        testByChunk(encoded, decoded, 0, LF);
    }


    /**
     * Test method does three tests on the supplied data:
     * 1. encoded ---[DECODE]--> decoded
     * 2. decoded ---[ENCODE]--> encoded
     * 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the
     * Base64InputStream wraps itself in encode and decode mode
     * over and over again.
     *
     * @param encoded   base64 encoded data
     * @param decoded   the data from above, but decoded
     * @param chunkSize chunk size (line-length) of the base64 encoded data.
     * @param seperator Line separator in the base64 encoded data.
     * @throws Exception Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByteByByte(
            byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator
    ) throws Exception {

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

        // I always wanted to do this!  (wrap encoder with decoder etc etc).
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
     * Test method does three tests on the supplied data:
     * 1. encoded ---[DECODE]--> decoded
     * 2. decoded ---[ENCODE]--> encoded
     * 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the
     * Base64InputStream wraps itself in encode and decode mode
     * over and over again.
     *
     * @param encoded   base64 encoded data
     * @param decoded   the data from above, but decoded
     * @param chunkSize chunk size (line-length) of the base64 encoded data.
     * @param seperator Line separator in the base64 encoded data.
     * @throws Exception Usually signifies a bug in the Base64 commons-codec implementation.
     */
    private void testByChunk(
            byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator
    ) throws Exception {

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

        // I always wanted to do this!  (wrap encoder with decoder etc etc).
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
}
