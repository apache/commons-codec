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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * @since 1.15
 */
public class Base16OutputStreamTest {

    private static final String STRING_FIXTURE = "Hello World";

    /**
     * Test the Base16OutputStream implementation against empty input.
     *
     * @throws IOException for some failure scenarios..
     */
    @Test
    public void testBase16EmptyOutputStream() throws IOException {
        final byte[] emptyEncoded = new byte[0];
        final byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded);
        testByChunk(emptyEncoded, emptyDecoded);
    }

    /**
     * Test the Base16OutputStream implementation
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testBase16OutputStreamByChunk() throws Exception {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("48656c6c6f20576f726c64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[]{(byte) 0x41};
        testByChunk(encoded, decoded);

        // test random data of sizes 0 thru 150
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = Base16TestData.randomData(i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded);
        }
    }

    /**
     * Test the Base16OutputStream implementation
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testBase16OutputStreamByteByByte() throws IOException {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("48656c6c6f20576f726c64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[]{(byte) 0x41};
        testByteByByte(encoded, decoded);

        // test random data of sizes 0 thru 150
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = Base16TestData.randomData(i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded);
        }
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            base16 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @throws IOException
     *             Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded) throws IOException {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        OutputStream out = new Base16OutputStream(byteOut, true);
        out.write(decoded);
        out.close();
        byte[] output = byteOut.toByteArray();
        assertArrayEquals("Streaming chunked base16 encode", encoded, output);

        // Now let's try decode.
        byteOut = new ByteArrayOutputStream();
        out = new Base16OutputStream(byteOut, false);
        out.write(encoded);
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming chunked base16 decode", decoded, output);

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        byteOut = new ByteArrayOutputStream();
        out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base16OutputStream(out, false);
            out = new Base16OutputStream(out, true);
        }
        out.write(decoded);
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals("Streaming chunked base16 wrap-wrap-wrap!", decoded, output);
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded
     *            base16 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @throws IOException
     *             Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded) throws IOException {

        // Start with encode.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        OutputStream out = new Base16OutputStream(byteOut, true);
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        byte[] output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte base16 encode", encoded, output);

        // Now let's try decode.
        byteOut = new ByteArrayOutputStream();
        out = new Base16OutputStream(byteOut, false);
        for (final byte element : encoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte base16 decode", decoded, output);

        // Now let's try decode with tonnes of flushes.
        byteOut = new ByteArrayOutputStream();
        out = new Base16OutputStream(byteOut, false);
        for (final byte element : encoded) {
            out.write(element);
            out.flush();
        }
        out.close();
        output = byteOut.toByteArray();
        assertArrayEquals("Streaming byte-by-byte flush() base16 decode", decoded, output);

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        byteOut = new ByteArrayOutputStream();
        out = byteOut;
        for (int i = 0; i < 10; i++) {
            out = new Base16OutputStream(out, false);
            out = new Base16OutputStream(out, true);
        }
        for (final byte element : decoded) {
            out.write(element);
        }
        out.close();
        output = byteOut.toByteArray();

        assertArrayEquals("Streaming byte-by-byte base16 wrap-wrap-wrap!", decoded, output);
    }

    /**
     * Tests Base16OutputStream.write for expected IndexOutOfBoundsException conditions.
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testWriteOutOfBounds() throws IOException {
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (final Base16OutputStream out = new Base16OutputStream(bout)) {

            try {
                out.write(buf, -1, 1);
                fail("Expected Base16OutputStream.write(buf, -1, 1) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, 1, -1);
                fail("Expected Base16OutputStream.write(buf, 1, -1) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, buf.length + 1, 0);
                fail("Expected Base16OutputStream.write(buf, buf.length + 1, 0) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }

            try {
                out.write(buf, buf.length - 1, 2);
                fail("Expected Base16OutputStream.write(buf, buf.length - 1, 2) to throw a IndexOutOfBoundsException");
            } catch (final IndexOutOfBoundsException ioobe) {
                // Expected
            }
        }
    }

    /**
     * Tests Base16OutputStream.write(null).
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testWriteToNullCoverage() throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (final Base16OutputStream out = new Base16OutputStream(bout)) {
            out.write(null, 0, 0);
            fail("Expcted Base16OutputStream.write(null) to throw a NullPointerException");
        } catch (final NullPointerException e) {
            // Expected
        }
    }
}
