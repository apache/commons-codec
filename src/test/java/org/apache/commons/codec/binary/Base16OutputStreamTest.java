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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final byte[] emptyEncoded = {};
        final byte[] emptyDecoded = {};
        testByteByByte(emptyEncoded, emptyDecoded);
        testByChunk(emptyEncoded, emptyDecoded);
    }

    /**
     * Test the Base16OutputStream implementation
     *
     * @throws IOException for some failure scenarios.
     */
    @Test
    public void testBase16OutputStreamByChunk() throws IOException {
        // Hello World test.
        byte[] encoded = StringUtils.getBytesUtf8("48656C6C6F20576F726C64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByChunk(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[]{(byte) 0x41};
        testByChunk(encoded, decoded);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base16(true);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByChunk(encoded, decoded, true);
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
        byte[] encoded = StringUtils.getBytesUtf8("48656C6C6F20576F726C64");
        byte[] decoded = StringUtils.getBytesUtf8(STRING_FIXTURE);
        testByteByByte(encoded, decoded);

        // Single Byte test.
        encoded = StringUtils.getBytesUtf8("41");
        decoded = new byte[]{(byte) 0x41};
        testByteByByte(encoded, decoded);

        // test random data of sizes 0 through 150
        final BaseNCodec codec = new Base16(true);
        for (int i = 0; i <= 150; i++) {
            final byte[][] randomData = BaseNTestData.randomData(codec, i);
            encoded = randomData[1];
            decoded = randomData[0];
            testByteByByte(encoded, decoded, true);
        }
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded base16 encoded data
     * @param decoded the data from above, but decoded
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded) throws IOException {
        testByChunk(encoded, decoded, false);
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded base16 encoded data
     * @param decoded the data from above, but decoded
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByChunk(final byte[] encoded, final byte[] decoded, final boolean lowerCase) throws IOException {

        // Start with encode.
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream out = new Base16OutputStream(byteOut, true, lowerCase)) {
            out.write(decoded);
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(encoded, output, "Streaming chunked base16 encode");
        }

        // Now let's try to decode.
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream out = new Base16OutputStream(byteOut, false, lowerCase)) {
            out.write(encoded);
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(decoded, output, "Streaming chunked base16 decode");
        }

        // wrap encoder with decoder
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             final OutputStream decoderOut = new Base16OutputStream(byteOut, false, lowerCase);
             final OutputStream encoderOut = new Base16OutputStream(decoderOut, true, lowerCase)) {

            encoderOut.write(decoded);
            final byte[] output = byteOut.toByteArray();

            assertArrayEquals(decoded, output, "Streaming chunked base16 wrap-wrap!");
        }
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded base16 encoded data
     * @param decoded the data from above, but decoded
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded) throws IOException {
        testByteByByte(encoded, decoded, false);
    }

    /**
     * Test method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]-->
     * encoded 3. decoded ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base16OutputStream wraps itself in encode and decode
     * mode over and over again.
     *
     * @param encoded base16 encoded data
     * @param decoded the data from above, but decoded
     * @throws IOException Usually signifies a bug in the Base16 commons-codec implementation.
     */
    private void testByteByByte(final byte[] encoded, final byte[] decoded, final boolean lowerCase) throws IOException {

        // Start with encode.
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream out = new Base16OutputStream(byteOut, true, lowerCase)) {
            for (final byte element : decoded) {
                out.write(element);
            }
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(encoded, output, "Streaming byte-by-byte base16 encode");
        }

        // Now let's try to decode.
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream out = new Base16OutputStream(byteOut, false, lowerCase)) {
            for (final byte element : encoded) {
                out.write(element);
            }
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(decoded, output, "Streaming byte-by-byte base16 decode");
        }

        // Now let's try to decode with tonnes of flushes.
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream out = new Base16OutputStream(byteOut, false, lowerCase)) {
            for (final byte element : encoded) {
                out.write(element);
                out.flush();
            }
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(decoded, output, "Streaming byte-by-byte flush() base16 decode");
        }

        // wrap encoder with decoder
        try (final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                final OutputStream decoderOut = new Base16OutputStream(byteOut, false, lowerCase);
                final OutputStream encoderOut = new Base16OutputStream(decoderOut, true, lowerCase)) {
            for (final byte element : decoded) {
                encoderOut.write(element);
            }
            final byte[] output = byteOut.toByteArray();
            assertArrayEquals(decoded, output, "Streaming byte-by-byte base16 wrap-wrap!");
        }
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
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, -1, 1), "Base16InputStream.write(buf, -1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, 1, -1), "Base16InputStream.write(buf, 1, -1)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length + 1, 0), "Base16InputStream.write(buf, buf.length + 1, 0)");
            assertThrows(IndexOutOfBoundsException.class, () -> out.write(buf, buf.length - 1, 2), "Base16InputStream.write(buf, buf.length - 1, 2)");
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
            assertThrows(NullPointerException.class, () -> out.write(null, 0, 0));
        }
    }
}
