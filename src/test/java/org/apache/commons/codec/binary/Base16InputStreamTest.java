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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @since 1.15
 */
public class Base16InputStreamTest {

    /**
     * Decodes to {202, 254, 186, 190, 255, 255}
     */
    private static final String ENCODED_B16 = "CAFEBABEFFFF";

    private static final String STRING_FIXTURE = "Hello World";

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testAvailable() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            assertEquals(1, b16Stream.available());
            assertEquals(6, b16Stream.skip(10));
            // End of stream reached
            assertEquals(0, b16Stream.available());
            assertEquals(-1, b16Stream.read());
            assertEquals(-1, b16Stream.read());
            assertEquals(0, b16Stream.available());
        }
    }

    /**
     * Tests the Base16InputStream implementation against empty input.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testBase16EmptyInputStream() throws IOException {
        final byte[] emptyEncoded = new byte[0];
        final byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded);
        testByChunk(emptyEncoded, emptyDecoded);
    }

    /**
     * Tests the Base16InputStream implementation.
     *
     * @throws IOException
     *             for some failure scenarios.
     */
    @Test
    public void testBase16InputStreamByChunk() throws IOException {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("48656C6C6F20576F726C64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[] { (byte) 0x41 };
        testByChunk(encoded, decoded);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base16TestData.ENCODED_UTF8_UPPERCASE);
        decoded = BaseNTestData.DECODED;
        testByChunk(encoded, decoded);

        // test random data of sizes 0 thru 150
        final BaseNCodec codec = new Base16(true);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, true);
        }
    }

    /**
     * Tests the Base16InputStream implementation.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testBase16InputStreamByteByByte() throws IOException {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("48656C6C6F20576F726C64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[] { (byte) 0x41 };
        testByteByByte(encoded, decoded);

        // OpenSSL interop test.
        encoded = StringUtils.getBytesUtf8(Base16TestData.ENCODED_UTF8_UPPERCASE);
        decoded = BaseNTestData.DECODED;
        testByteByByte(encoded, decoded);

        // test random data of sizes 0 thru 150
        final BaseNCodec codec = new Base16(true);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, true);
        }
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded Base16 encoded data
     * @param decoded the data from above, but decoded
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded) throws IOException {
        testByChunk(encoded, decoded, false);
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded Base16 encoded data
     * @param decoded the data from above, but decoded
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final boolean lowerCase) throws IOException {

        // Start with encode.
        try (final InputStream in = new Base16InputStream(new ByteArrayInputStream(decoded), true, lowerCase)) {
            final byte[] output = BaseNTestData.streamToBytes(in);

            assertEquals("EOF", -1, in.read());
            assertEquals("Still EOF", -1, in.read());
            assertArrayEquals("Streaming Base16 encode", encoded, output);
        }

        // Now let's try decode.
        try (final InputStream in = new Base16InputStream(new ByteArrayInputStream(encoded), false, lowerCase)) {
            final byte[] output = BaseNTestData.streamToBytes(in);

            assertEquals("EOF", -1, in.read());
            assertEquals("Still EOF", -1, in.read());
            assertArrayEquals("Streaming Base16 decode", decoded, output);
        }

        // wrap encoder with decoder
        try (final InputStream in = new ByteArrayInputStream(decoded);
                final InputStream inEncode = new Base16InputStream(in, true, lowerCase);
                final InputStream inDecode = new Base16InputStream(inEncode, false, lowerCase)) {

            final byte[] output = BaseNTestData.streamToBytes(inDecode);

            assertEquals("EOF", -1, inDecode.read());
            assertEquals("Still EOF", -1, inDecode.read());
            assertArrayEquals("Streaming Base16 wrap-wrap!", decoded, output);
        }
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded Base16 encoded data
     * @param decoded the data from above, but decoded
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded) throws IOException {
        testByteByByte(encoded, decoded, false);
    }

    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16InputStream wraps itself in encode and decode mode over and over
     * again.
     *
     * @param encoded Base16 encoded data
     * @param decoded the data from above, but decoded
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final boolean lowerCase) throws IOException {

        // Start with encode.
        try (final InputStream in = new Base16InputStream(new ByteArrayInputStream(decoded), true, lowerCase)) {
            final byte[] output = new byte[encoded.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = (byte) in.read();
            }

            assertEquals("EOF", -1, in.read());
            assertEquals("Still EOF", -1, in.read());
            assertArrayEquals("Streaming Base16 encode", encoded, output);
        }

        // Now let's try decode.
        try (final InputStream in = new Base16InputStream(new ByteArrayInputStream(encoded), false, lowerCase)) {
            final byte[] output = new byte[decoded.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = (byte) in.read();
            }

            assertEquals("EOF", -1, in.read());
            assertEquals("Still EOF", -1, in.read());
            assertArrayEquals("Streaming Base16 decode", decoded, output);
        }

        // wrap encoder with decoder
        try (final InputStream in = new ByteArrayInputStream(decoded);
                final InputStream inEncode = new Base16InputStream(in, true, lowerCase);
                final InputStream inDecode = new Base16InputStream(inEncode, false, lowerCase)) {

            final byte[] output = new byte[decoded.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = (byte) inDecode.read();
            }

            assertEquals("EOF", -1, inDecode.read());
            assertEquals("Still EOF", -1, inDecode.read());
            assertArrayEquals("Streaming Base16 wrap-wrap!", decoded, output);
        }
    }

    /**
     * Tests markSupported.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testMarkSupported() throws IOException {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base16InputStream in = new Base16InputStream(bin, true)) {
            // Always returns false for now.
            assertFalse("Base16InputStream.markSupported() is false", in.markSupported());
        }
    }

    /**
     * Tests read returning 0
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testRead0() throws IOException {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        int bytesRead = 0;
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base16InputStream in = new Base16InputStream(bin, true)) {
            bytesRead = in.read(buf, 0, 0);
            assertEquals("Base16InputStream.read(buf, 0, 0) returns 0", 0, bytesRead);
        }
    }

    /**
     * Tests read with null.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testReadNull() throws IOException {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base16InputStream in = new Base16InputStream(bin, true)) {
            in.read(null, 0, 0);
            fail("Base16InputStream.read(null, 0, 0) to throw a NullPointerException");
        } catch (final NullPointerException e) {
            // Expected
        }
    }

    /**
     * Tests read throwing IndexOutOfBoundsException
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testReadOutOfBounds() throws IOException {
        final byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        final byte[] buf = new byte[1024];
        final ByteArrayInputStream bin = new ByteArrayInputStream(decoded);
        try (final Base16InputStream in = new Base16InputStream(bin, true)) {

            try {
                in.read(buf, -1, 0);
                fail("Expected Base16InputStream.read(buf, -1, 0) to throw IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException e) {
                // Expected
            }

            try {
                in.read(buf, 0, -1);
                fail("Expected Base16InputStream.read(buf, 0, -1) to throw IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException e) {
                // Expected
            }

            try {
                in.read(buf, buf.length + 1, 0);
                fail("Base16InputStream.read(buf, buf.length + 1, 0) throws IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException e) {
                // Expected
            }

            try {
                in.read(buf, buf.length - 1, 2);
                fail("Base16InputStream.read(buf, buf.length - 1, 2) throws IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException e) {
                // Expected
            }
        }
    }

    /**
     * Tests skipping number of characters larger than the internal buffer.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testSkipBig() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            assertEquals(6, b16Stream.skip(Integer.MAX_VALUE));
            // End of stream reached
            assertEquals(-1, b16Stream.read());
            assertEquals(-1, b16Stream.read());
        }
    }

    /**
     * Tests skipping as a noop
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testSkipNone() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            final byte[] actualBytes = new byte[6];
            assertEquals(0, b16Stream.skip(0));
            b16Stream.read(actualBytes, 0, actualBytes.length);
            assertArrayEquals(actualBytes, new byte[] {(byte)202, (byte)254, (byte)186, (byte)190, (byte)255, (byte)255});
            // End of stream reached
            assertEquals(-1, b16Stream.read());
        }
    }

    /**
     * Tests skipping past the end of a stream.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testSkipPastEnd() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(6, b16Stream.skip(10));
            // End of stream reached
            assertEquals(-1, b16Stream.read());
            assertEquals(-1, b16Stream.read());
        }
    }

    /**
     * Tests skipping to the end of a stream.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testSkipToEnd() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
            assertEquals(6, b16Stream.skip(6));
            // End of stream reached
            assertEquals(-1, b16Stream.read());
            assertEquals(-1, b16Stream.read());
        }
    }

    /**
     * Tests if negative arguments to skip are handled correctly.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSkipWrongArgument() throws IOException {
        final InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_B16));
        try (final Base16InputStream b16Stream = new Base16InputStream(ins)) {
            b16Stream.skip(-10);
        }
    }
}
