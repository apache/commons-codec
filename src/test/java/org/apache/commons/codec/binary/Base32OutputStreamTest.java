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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.CodecPolicy;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Base32OutputStreamTest {

    private final static byte[] CR_LF = {(byte) '\r', (byte) '\n'};

    private final static byte[] LF = {(byte) '\n'};



//    /**
//     * Test the Base32OutputStream implementation against the special NPE inducing input
//     * identified in the CODEC-98 bug.
//     *
//     * @throws Exception for some failure scenarios.
//     */
//    @Test
//    public void testCodec98NPE() throws Exception {
//        byte[] codec98 = StringUtils.getBytesUtf8(Base32TestData.CODEC_98_NPE);
//        byte[] codec98_1024 = new byte[1024];
//        System.arraycopy(codec98, 0, codec98_1024, 0, codec98.length);
//        ByteArrayOutputStream data = new ByteArrayOutputStream(1024);
//        Base32OutputStream stream = new Base32OutputStream(data, false);
//        stream.write(codec98_1024, 0, 1024);
//        stream.close();
//
//        byte[] decodedBytes = data.toByteArray();
//        String decoded = StringUtils.newStringUtf8(decodedBytes);
//        assertEquals(
//            "codec-98 NPE Base32OutputStream", Base32TestData.CODEC_98_NPE_DECODED, decoded
//        );
//    }


    /**
     * Test the Base32OutputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32EmptyOutputStreamMimeChunkSize() throws Exception {
        testBase32EmptyOutputStream(BaseNCodec.MIME_CHUNK_SIZE);
    }

    /**
     * Test the Base32OutputStream implementation against empty input.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32EmptyOutputStreamPemChunkSize() throws Exception {
        testBase32EmptyOutputStream(BaseNCodec.PEM_CHUNK_SIZE);
    }

    private void testBase32EmptyOutputStream(final int chunkSize) throws Exception {
        final byte[] emptyEncoded = new byte[0];
        final byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
        testByChunk(emptyEncoded, emptyDecoded, chunkSize, CR_LF);
    }

    /**
     * Test the Base32OutputStream implementation
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32OutputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8(Base32TestData.BASE32_FIXTURE);
        byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        testByChunk(encoded, decoded, BaseNCodec.MIME_CHUNK_SIZE, CR_LF);

//        // Single Byte test.
//        encoded = StringUtils.getBytesUtf8("AA==\r\n");
//        decoded = new byte[]{(byte) 0};
//        testByChunk(encoded, decoded, Base32.MIME_CHUNK_SIZE, CRLF);


//        // Single Line test.
//        String singleLine = Base32TestData.ENCODED_64_CHARS_PER_LINE.replaceAll("\n", "");
//        encoded = StringUtils.getBytesUtf8(singleLine);
//        decoded = Base32TestData.DECODED;
//        testByChunk(encoded, decoded, 0, LF);

        // test random data of sizes 0 thru 150
        final BaseNCodec codec = new Base32();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, 0, LF);
        }
    }

    /**
     * Test the Base32OutputStream implementation
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testBase32OutputStreamByteByByte() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8(Base32TestData.BASE32_FIXTURE);
        byte[] decoded = StringUtils.getBytesUtf8(Base32TestData.STRING_FIXTURE);
        testByteByByte(encoded, decoded, 76, CR_LF);

//        // Single Byte test.
//        encoded = StringUtils.getBytesUtf8("AA==\r\n");
//        decoded = new byte[]{(byte) 0};
//        testByteByByte(encoded, decoded, 76, CRLF);


//        // Single Line test.
//        String singleLine = Base32TestData.ENCODED_64_CHARS_PER_LINE.replaceAll("\n", "");
//        encoded = StringUtils.getBytesUtf8(singleLine);
//        decoded = Base32TestData.DECODED;
//        testByteByByte(encoded, decoded, 0, LF);

        // test random data of sizes 0 thru 150
        final BaseNCodec codec = new Base32();
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, 0, LF);
        }
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            Base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the Base32 encoded data.
     * @param separator
     *            Line separator in the Base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        OutputStream out = new Base32OutputStream(byteOut, true, chunkSize, separator);
        out.write(decoded);
        out.close();
        byte[] output = byteOut.toByteArray();
        assertArrayEquals("Streaming chunked Base32 encode", encoded, output);

        // Now let's try decode.
        byteOut = new ByteArrayOutputStream();
        out = new Base32OutputStream(byteOut, false);
        out.write(encoded);
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming chunked Base32 decode", decoded, output);

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        byteOut = new ByteArrayOutputStream();
        out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base32OutputStream(out, false);
            out = new Base32OutputStream(out, true, chunkSize, separator);
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals("Streaming chunked Base32 wrap-wrap-wrap!", decoded, output);
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            Base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the Base32 encoded data.
     * @param separator
     *            Line separator in the Base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final int chunkSize, final byte[] separator) throws Exception {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        OutputStream out = new Base32OutputStream(byteOut, true, chunkSize, separator);
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        byte[] output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte Base32 encode", encoded, output);

        // Now let's try decode.
        byteOut = new ByteArrayOutputStream();
        out = new Base32OutputStream(byteOut, false);
        for (final byte element : encoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte Base32 decode", decoded, output);

        // Now let's try decode with tonnes of flushes.
        byteOut = new ByteArrayOutputStream();
        out = new Base32OutputStream(byteOut, false);
        for (final byte element : encoded) {
            out.write(element);
            out.flush();
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte flush() Base32 decode", decoded, output);

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        byteOut = new ByteArrayOutputStream();
        out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base32OutputStream(out, false);
            out = new Base32OutputStream(out, true, chunkSize, separator);
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals("Streaming byte-by-byte Base32 wrap-wrap-wrap!", decoded, output);
    }

    /**
     * Tests Base32OutputStream.write for expected IndexOutOfBoundsException conditions.
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testWriteOutOfBounds() throws Exception {
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (final Base32OutputStream out = new Base32OutputStream(bout)) {

            try {
                out.write(buf, -1, 1);
                fail("Expected Base32OutputStream.write(buf, -1, 1) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, 1, -1);
                fail("Expected Base32OutputStream.write(buf, 1, -1) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, buf.length + 1, 0);
                fail("Expected Base32OutputStream.write(buf, buf.length + 1, 0) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, buf.length - 1, 2);
                fail("Expected Base32OutputStream.write(buf, buf.length - 1, 2) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }
        }
    }

    /**
     * Tests Base32OutputStream.write(null).
     *
     * @throws Exception
     *             for some failure scenarios.
     */
    @Test
    public void testWriteToNullCoverage() throws Exception {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (final Base32OutputStream out = new Base32OutputStream(bout)) {
            out.write(null, 0, 0);
            fail("Expcted Base32OutputStream.write(null) to throw a NullPointerException");
        } catch (final NullPointerException e) {
            // Expected
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
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Base32OutputStream out = new Base32OutputStream(bout, false);
            // Default is lenient decoding; it should not throw
            assertFalse(out.isStrictDecoding());
            out.write(encoded);
            out.close();
            assertTrue(bout.size() > 0);

            // Strict decoding should throw
            bout = new ByteArrayOutputStream();
            out = new Base32OutputStream(bout, false, 0, null, CodecPolicy.STRICT);
            assertTrue(out.isStrictDecoding());
            try {
                out.write(encoded);
                out.close();
                fail();
            } catch (final IllegalArgumentException ex) {
                // expected
            }
        }
    }
}
