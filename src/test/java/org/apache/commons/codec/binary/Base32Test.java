/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class Base32Test {


    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private static final String [][] BASE32_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"MY======"},
        {"fo"     ,"MZXQ===="},
        {"foo"    ,"MZXW6==="},
        {"foob"   ,"MZXW6YQ="},
        {"fooba"  ,"MZXW6YTB"},
        {"foobar" ,"MZXW6YTBOI======"},
    };

    /**
     * Example test cases with valid characters but impossible combinations of
     * trailing characters (i.e. cannot be created during encoding).
     */
    static final String[] BASE32_IMPOSSIBLE_CASES = {
        "MC======",
        "MZXE====",
        "MZXWB===",
        "MZXW6YB=",
        "MZXW6YTBOC======",
        "AB======"
        };

    private static final String[] BASE32_IMPOSSIBLE_CASES_CHUNKED = {
        "M2======\r\n",
        "MZX0====\r\n",
        "MZXW0===\r\n",
        "MZXW6Y2=\r\n",
        "MZXW6YTBO2======\r\n"
    };

    private static final String[] BASE32HEX_IMPOSSIBLE_CASES = {
        "C2======",
        "CPN4====",
        "CPNM1===",
        "CPNMUO1=",
        "CPNMUOJ1E2======"
    };

    /**
     * Copy of the standard base-32 encoding table. Used to test decoding the final
     * character of encoded bytes.
     */
    private static final byte[] ENCODE_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7',
    };

    private static final Object[][] BASE32_BINARY_TEST_CASES;

    //            { null, "O0o0O0o0" }
//            BASE32_BINARY_TEST_CASES[2][0] = new Hex().decode("739ce739ce");

    static {
        final Hex hex = new Hex();
        try {
            BASE32_BINARY_TEST_CASES = new Object[][] {
                    new Object[] { hex.decode("623a01735836e9a126e12fbf95e013ee6892997c"),
                                   "MI5AC42YG3U2CJXBF67ZLYAT5ZUJFGL4" },
                    new Object[] { hex.decode("623a01735836e9a126e12fbf95e013ee6892997c"),
                                   "mi5ac42yg3u2cjxbf67zlyat5zujfgl4" },
                    new Object[] { hex.decode("739ce42108"),
                                   "OOOOIIII" }
            };
        } catch (final DecoderException de) {
            throw new Error(":(", de);
        }
    }
    private static final String [][] BASE32HEX_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"CO======"},
        {"fo"     ,"CPNG===="},
        {"foo"    ,"CPNMU==="},
        {"foob"   ,"CPNMUOG="},
        {"fooba"  ,"CPNMUOJ1"},
        {"foobar" ,"CPNMUOJ1E8======"},
    };

    private static final String [][] BASE32_TEST_CASES_CHUNKED = { //Chunked
        {""       ,""},
        {"f"      ,"MY======\r\n"},
        {"fo"     ,"MZXQ====\r\n"},
        {"foo"    ,"MZXW6===\r\n"},
        {"foob"   ,"MZXW6YQ=\r\n"},
        {"fooba"  ,"MZXW6YTB\r\n"},
        {"foobar" ,"MZXW6YTBOI======\r\n"},
    };

    private static final String [][] BASE32_PAD_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"MY%%%%%%"},
        {"fo"     ,"MZXQ%%%%"},
        {"foo"    ,"MZXW6%%%"},
        {"foob"   ,"MZXW6YQ%"},
        {"fooba"  ,"MZXW6YTB"},
        {"foobar" ,"MZXW6YTBOI%%%%%%"},
    };

    @Test
    public void testBase64AtBufferStart() {
        testBase64InBuffer(0, 100);
    }

    @Test
    public void testBase64AtBufferEnd() {
        testBase64InBuffer(100, 0);
    }

    @Test
    public void testBase64AtBufferMiddle() {
        testBase64InBuffer(100, 100);
    }

    private void testBase64InBuffer(final int startPasSize, final int endPadSize) {
        final Base32 codec = new Base32();
        for (final String[] element : BASE32_TEST_CASES) {
            final byte[] bytes = element[0].getBytes(CHARSET_UTF8);
            byte[] buffer = ArrayUtils.addAll(bytes, new byte[endPadSize]);
            buffer = ArrayUtils.addAll(new byte[startPasSize], buffer);
            assertEquals(element[1], StringUtils.newStringUtf8(codec.encode(buffer, startPasSize, bytes.length)));
        }
    }

    @Test
    public void testBase32Chunked () throws Exception {
        final Base32 codec = new Base32(20);
        for (final String[] element : BASE32_TEST_CASES_CHUNKED) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(CHARSET_UTF8)));
        }
    }

    @Test
    public void testBase32HexSamples() throws Exception {
        final Base32 codec = new Base32(true);
        for (final String[] element : BASE32HEX_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(CHARSET_UTF8)));
        }
    }

    @Test
    public void testBase32HexSamplesReverse() throws Exception {
        final Base32 codec = new Base32(true);
        for (final String[] element : BASE32HEX_TEST_CASES) {
            assertEquals(element[0], new String(codec.decode(element[1]), CHARSET_UTF8));
        }
    }

    @Test
    public void testBase32HexSamplesReverseLowercase() throws Exception {
        final Base32 codec = new Base32(true);
        for (final String[] element : BASE32HEX_TEST_CASES) {
            assertEquals(element[0], new String(codec.decode(element[1].toLowerCase()), CHARSET_UTF8));
        }
    }

    @Test
    public void testBase32Samples() throws Exception {
        final Base32 codec = new Base32();
        for (final String[] element : BASE32_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(CHARSET_UTF8)));
        }
    }

    @Test
    public void testBase32BinarySamples() throws Exception {
        final Base32 codec = new Base32();
        for (final Object[] element : BASE32_BINARY_TEST_CASES) {
            String expected;
            if(element.length > 2) {
                expected = (String)element[2];
            } else {
                expected = (String)element[1];
            }
                assertEquals(expected.toUpperCase(), codec.encodeAsString((byte[])element[0]));
        }
    }

    @Test
    public void testBase32BinarySamplesReverse() throws Exception {
        final Base32 codec = new Base32();
        for (final Object[] element : BASE32_BINARY_TEST_CASES) {
            assertArrayEquals((byte[])element[0], codec.decode((String)element[1]));
        }
    }

    @Test
    public void testBase32SamplesNonDefaultPadding() throws Exception {
        final Base32 codec = new Base32((byte)0x25); // '%' <=> 0x25

        for (final String[] element : BASE32_PAD_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(CHARSET_UTF8)));
        }
    }

    @Test
    public void testCodec200() {
        final Base32 codec = new Base32(true, (byte)'W'); // should be allowed
        assertNotNull(codec);
    }

    @Test
    public void testRandomBytes() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32();
            final byte[][] b = BaseNTestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testRandomBytesChunked() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32(10);
            final byte[][] b = BaseNTestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testRandomBytesHex() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32(true);
            final byte[][] b = BaseNTestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testSingleCharEncoding() {
        for (int i = 0; i < 20; i++) {
            Base32 codec = new Base32();
            final BaseNCodec.Context context = new BaseNCodec.Context();
            final byte unencoded[] = new byte[i];
            final byte allInOne[] = codec.encode(unencoded);
            codec = new Base32();
            for (int j=0; j< unencoded.length; j++) {
                codec.encode(unencoded, j, 1, context);
            }
            codec.encode(unencoded, 0, -1, context);
            final byte singly[] = new byte[allInOne.length];
            codec.readResults(singly, 0, 100, context);
            if (!Arrays.equals(allInOne, singly)){
                fail();
            }
        }
    }

    @Test
    public void testBase32ImpossibleSamples() {
        testImpossibleCases(new Base32(0, null, false, BaseNCodec.PAD_DEFAULT, CodecPolicy.STRICT),
            BASE32_IMPOSSIBLE_CASES);
    }

    @Test
    public void testBase32ImpossibleChunked() {
        testImpossibleCases(
            new Base32(20, BaseNCodec.CHUNK_SEPARATOR, false, BaseNCodec.PAD_DEFAULT, CodecPolicy.STRICT),
            BASE32_IMPOSSIBLE_CASES_CHUNKED);
    }

    @Test
    public void testBase32HexImpossibleSamples() {
        testImpossibleCases(new Base32(0, null, true, BaseNCodec.PAD_DEFAULT, CodecPolicy.STRICT),
            BASE32HEX_IMPOSSIBLE_CASES);
    }

    private void testImpossibleCases(final Base32 codec, final String[] impossible_cases) {
        for (final String impossible : impossible_cases) {
            try {
                codec.decode(impossible);
                fail();
            } catch (final IllegalArgumentException ex) {
                // expected
            }
        }
    }

    @Test
    public void testBase32DecodingOfTrailing5Bits() {
        assertBase32DecodingOfTrailingBits(5);
    }

    @Test
    public void testBase32DecodingOfTrailing10Bits() {
        assertBase32DecodingOfTrailingBits(10);
    }

    @Test
    public void testBase32DecodingOfTrailing15Bits() {
        assertBase32DecodingOfTrailingBits(15);
    }

    @Test
    public void testBase32DecodingOfTrailing20Bits() {
        assertBase32DecodingOfTrailingBits(20);
    }

    @Test
    public void testBase32DecodingOfTrailing25Bits() {
        assertBase32DecodingOfTrailingBits(25);
    }

    @Test
    public void testBase32DecodingOfTrailing30Bits() {
        assertBase32DecodingOfTrailingBits(30);
    }

    @Test
    public void testBase32DecodingOfTrailing35Bits() {
        assertBase32DecodingOfTrailingBits(35);
    }

    /**
     * Test base 32 decoding of the final trailing bits. Trailing encoded bytes
     * cannot fit exactly into 5-bit characters so the last character has a limited
     * alphabet where the final bits are zero. This asserts that illegal final
     * characters throw an exception when decoding.
     *
     * @param nbits the number of trailing bits (must be a factor of 5 and {@code <40})
     */
    private static void assertBase32DecodingOfTrailingBits(final int nbits) {
        // Requires strict decoding
        final Base32 codec = new Base32(0, null, false, BaseNCodec.PAD_DEFAULT, CodecPolicy.STRICT);
        assertTrue(codec.isStrictDecoding());
        assertEquals(CodecPolicy.STRICT, codec.getCodecPolicy());
        // A lenient decoder should not re-encode to the same bytes
        final Base32 defaultCodec = new Base32();
        assertFalse(defaultCodec.isStrictDecoding());
        assertEquals(CodecPolicy.LENIENT, defaultCodec.getCodecPolicy());

        // Create the encoded bytes. The first characters must be valid so fill with 'zero'
        // then pad to the block size.
        final int length = nbits / 5;
        final byte[] encoded = new byte[8];
        Arrays.fill(encoded, 0, length, ENCODE_TABLE[0]);
        Arrays.fill(encoded, length, encoded.length, (byte) '=');
        // Compute how many bits would be discarded from 8-bit bytes
        final int discard = nbits % 8;
        final int emptyBitsMask = (1 << discard) - 1;
        // Special case when an impossible number of trailing characters
        final boolean invalid = length == 1 || length == 3 || length == 6;
        // Enumerate all 32 possible final characters in the last position
        final int last = length - 1;
        for (int i = 0; i < 32; i++) {
            encoded[last] = ENCODE_TABLE[i];
            // If the lower bits are set we expect an exception. This is not a valid
            // final character.
            if (invalid || (i & emptyBitsMask) != 0) {
                try {
                    codec.decode(encoded);
                    fail("Final base-32 digit should not be allowed");
                } catch (final IllegalArgumentException ex) {
                    // expected
                }
                // The default lenient mode should decode this
                final byte[] decoded = defaultCodec.decode(encoded);
                // Re-encoding should not match the original array as it was invalid
                assertFalse(Arrays.equals(encoded, defaultCodec.encode(decoded)));
            } else {
                // Otherwise this should decode
                final byte[] decoded = codec.decode(encoded);
                // Compute the bits that were encoded. This should match the final decoded byte.
                final int bitsEncoded = i >> discard;
                assertEquals("Invalid decoding of last character", bitsEncoded, decoded[decoded.length - 1]);
                // Re-encoding should match the original array (requires the same padding character)
                assertArrayEquals(encoded, codec.encode(decoded));
            }
        }
    }
}
