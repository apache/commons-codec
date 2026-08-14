/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.binary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link Base45} as defined by <a href="https://www.rfc-editor.org/rfc/rfc9285">RFC 9285</a>.
 * <p>
 * Test vectors are taken from RFC 9285 Section 4.4 and independently verified.
 * </p>
 */
class Base45Test {

    /**
     * RFC 9285 Section 4.4 official test vectors: {plain-text, base45-encoded}. Verified against the RFC 9285 specification.
     */
    // @formatter:off
    private static final String[][] RFC9285_TEST_VECTORS = {
        // RFC 9285 Section 4.4 Test Vectors
        { "",        ""            },  // empty
        { "ietf!",   "QED8WEX0"    },  // RFC 9285 Test Vector 1
        { "base-45", "UJCLQE7W581" },  // RFC 9285 Test Vector 2
        // Independently verified test vectors
        { "AB",      "BB8"         },  // classic two-byte example
    };
    // @formatter:on

    private void compare(final byte[] input, final ByteArrayOutputStream baos) throws IOException {
        final byte[] encoded = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        final ByteArrayOutputStream decodedBaos = new ByteArrayOutputStream();
        try (Base45InputStream in = new Base45InputStream(bais)) {
            IOUtils.copy(in, decodedBaos);
        }
        assertArrayEquals(input, decodedBaos.toByteArray());
    }

    /**
     * Tests that the builder creates a functional Base45 codec.
     */
    @Test
    void testBuilder() {
        final Base45 codec = Base45.builder().get();
        assertNotNull(codec);
        assertEquals("QED8WEX0", codec.encodeToString("ietf!".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void testBuilderLenientDecodingPolicyThrows() {
        assertThrows(IllegalArgumentException.class, () -> Base45.builder().setDecodingPolicy(CodecPolicy.LENIENT));
        assertEquals(CodecPolicy.STRICT, Base45.builder().setDecodingPolicy(null).get().getCodecPolicy());
    }

    /**
     * Tests that {@code setEncodeTable} derives a matching decode table, so a codec with a custom alphabet can always decode its own output.
     */
    @Test
    void testBuilderSetEncodeTableDerivesDecodeTable() {
        final byte[] custom = new byte[45];
        int k = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            custom[k++] = (byte) c;
        }
        for (char c = 'A'; c <= 'I'; c++) {
            custom[k++] = (byte) c;
        }
        for (char c = '0'; c <= '9'; c++) {
            custom[k++] = (byte) c;
        }
        final Base45 codec = Base45.builder().setEncodeTable(custom).get();
        final byte[] input = "hello world".getBytes(StandardCharsets.US_ASCII);
        final byte[] encoded = codec.encode(input);
        for (final byte b : encoded) {
            assertTrue(codec.isInAlphabet(b), "Encoded byte '" + (char) b + "' must be in the custom alphabet");
        }
        assertArrayEquals(input, codec.decode(encoded), "A custom-alphabet codec must be able to decode its own output");
        // null resets to the default table.
        assertArrayEquals("QED8WEX0".getBytes(StandardCharsets.US_ASCII),
                Base45.builder().setEncodeTable((byte[]) null).get().encode("ietf!".getBytes(StandardCharsets.US_ASCII)));
    }

    /**
     * Tests that {@code setEncodeTable} rejects tables that are not exactly 45 unique entries.
     */
    @Test
    void testBuilderSetEncodeTableRejectsInvalidTable() {
        assertThrows(IllegalArgumentException.class, () -> Base45.builder().setEncodeTable(new byte[] { 'a', 'b', 'c' }), "wrong size");
        assertThrows(IllegalArgumentException.class, () -> Base45.builder().setEncodeTable(), "zero entries");
        final byte[] duplicates = new byte[45];
        Arrays.fill(duplicates, (byte) 'a');
        assertThrows(IllegalArgumentException.class, () -> Base45.builder().setEncodeTable(duplicates), "duplicates");
    }

    @Test
    void testBuilderSetLineLength() {
        assertThrows(UnsupportedOperationException.class, () -> Base45.builder().setLineLength(0));
    }

    @Test
    void testBuilderSetLineSeparator() {
        assertThrows(UnsupportedOperationException.class, () -> Base45.builder().setLineSeparator((byte) 0));
    }

    @Test
    void testBuilderSetPadding() {
        assertThrows(UnsupportedOperationException.class, () -> Base45.builder().setPadding((byte) 0));
    }

    /**
     * Tests the builder with strict decoding policy.
     */
    @Test
    void testBuilderStrictDecoding() {
        final Base45 strict = Base45.builder().setDecodingPolicy(CodecPolicy.STRICT).get();
        assertTrue(strict.isStrictDecoding());
        assertTrue(new Base45().isStrictDecoding());
        // Valid data should still decode successfully
        assertArrayEquals("ietf!".getBytes(StandardCharsets.US_ASCII), strict.decode("QED8WEX0"));
    }

    /**
     * Tests that strict decoding rejects trailing characters that represent invalid values.
     */
    @Test
    void testBuilderStrictDecodingRejectsHighPair() {
        final Base45 strict = Base45.builder().setDecodingPolicy(CodecPolicy.STRICT).get();
        // ':' ':' = 2024 > 255; this should be rejected in both lenient and strict modes
        assertThrows(IllegalArgumentException.class, () -> strict.decode("::"), "Strict mode: ':' ':' decodes to 2024 > 255, should be rejected");
    }

    /**
     * Tests the codec type constants match RFC 9285 requirements.
     */
    @Test
    void testCodecConstants() {
        assertEquals(3, Base45.BYTES_PER_ENCODED_BLOCK, "BYTES_PER_ENCODED_BLOCK should be 3 (3 Base45 chars per 2 bytes)");
        assertEquals(2, Base45.BYTES_PER_UNENCODED_BLOCK, "BYTES_PER_UNENCODED_BLOCK should be 2 (2 bytes per 3 Base45 chars)");
    }

    /**
     * Tests that an empty string decodes to an empty byte array.
     */
    @Test
    void testDecodeEmpty() {
        final Base45 codec = new Base45();
        assertArrayEquals(new byte[0], codec.decode(""));
        assertArrayEquals(new byte[0], codec.decode(new byte[0]));
    }

    /**
     * Tests that invalid characters cause an exception. RFC 9285: "Receivers MUST reject any input string that is not valid Base45 encoding."
     */
    @ParameterizedTest
    @ValueSource(chars = { '!', '"', '#', '&', '\'', '(', ')', ',', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', 'a', 'z' })
    void testDecodeInvalidCharacters(final char c) {
        final Base45 codec = new Base45();
        // Characters not in the Base45 alphabet (excluding whitespace and space)
        final String input = "Q" + c + "D";
        assertThrows(IllegalArgumentException.class, () -> codec.decode(input), () -> "Should reject character '" + c + "' (ASCII " + (int) c + ")");
    }

    /**
     * Tests that an encoded input length modulo 3 equal to 1 is rejected. RFC 9285: "It is an error if the remaining string is 1 character long."
     */
    @Test
    void testDecodeInvalidLengthMod3Equals1() {
        final Base45 codec = new Base45();
        assertThrows(IllegalArgumentException.class, () -> codec.decode("Q"), "Single character input should be rejected");
        assertThrows(IllegalArgumentException.class, () -> codec.decode("QEDB"), "4-character input (length % 3 == 1) should be rejected");
        assertThrows(IllegalArgumentException.class, () -> codec.decode("QEDBWEC"), "7-character input (length % 3 == 1) should be rejected");
    }

    /**
     * Tests decoding of "FGW" -> [0xFF, 0xFF].
     */
    @Test
    void testDecodeMaxMax() {
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, new Base45().decode("FGW"));
    }

    /**
     * Tests that {@link Base45} implements BinaryDecoder correctly via the {@code decode(Object)} method.
     */
    @Test
    void testDecodeObject() throws DecoderException {
        final Base45 codec = new Base45();
        final byte[] encoded = "QED8WEX0".getBytes(StandardCharsets.US_ASCII);
        Object result = codec.decode((Object) encoded);
        assertArrayEquals("ietf!".getBytes(StandardCharsets.US_ASCII), (byte[]) result);
        // Also test with String input
        result = codec.decode((Object) "QED8WEX0");
        assertArrayEquals("ietf!".getBytes(StandardCharsets.US_ASCII), (byte[]) result);
    }

    /**
     * Tests that decoding an unsupported Object type throws DecoderException.
     */
    @Test
    void testDecodeObjectThrowsForUnsupportedType() {
        final Base45 codec = new Base45();
        assertThrows(DecoderException.class, () -> codec.decode(Integer.valueOf(42)));
    }

    /**
     * Tests that a 2-character pair that decodes to a value exceeding 255 is rejected. The maximum valid single-byte encoding decodes to at most 255. However,
     * some 2-char combos decode to values 256-2024.
     */
    @Test
    void testDecodePairExceedingMaxByte() {
        final Base45 codec = new Base45();
        // ':' ':' = 44 + 44*45 = 44 + 1980 = 2024 > 255 -> invalid
        assertThrows(IllegalArgumentException.class, () -> codec.decode("::"), "Pair ':' ':' decodes to 2024 which exceeds 255 and should be rejected");
        // ':' '6' = 44 + 6*45 = 44 + 270 = 314 > 255 -> invalid
        assertThrows(IllegalArgumentException.class, () -> codec.decode(":6"), "Pair ':' '6' decodes to 314 which exceeds 255 and should be rejected");
    }

    /**
     * Tests the RFC 9285 Section 4.4 decoding test vectors.
     */
    @Test
    void testDecodeRfc9285TestVectors() {
        final Base45 codec = new Base45();
        for (final String[] testCase : RFC9285_TEST_VECTORS) {
            final String plainText = testCase[0];
            final String encoded = testCase[1];
            final byte[] expected = plainText.getBytes(StandardCharsets.ISO_8859_1);
            final byte[] actual = codec.decode(encoded.getBytes(StandardCharsets.US_ASCII));
            assertArrayEquals(expected, actual, "RFC 9285 decode failed for: '" + encoded + "'");
        }
    }

    /**
     * Tests decoding of "U5" -> [0xFF] (single byte 255).
     */
    @Test
    void testDecodeSingleMax() {
        assertArrayEquals(new byte[] { (byte) 0xFF }, new Base45().decode("U5"));
    }

    /**
     * Tests decoding of "00" -> [0x00] (single byte 0).
     */
    @Test
    void testDecodeSingleZero() {
        assertArrayEquals(new byte[] { 0 }, new Base45().decode("00"));
    }

    /**
     * Tests that whitespace (CR, LF, TAB) characters not in the Base45 alphabet are silently skipped during decoding. This supports line-wrapped encoded data.
     */
    @Test
    void testDecodeSkipsNonAlphabetWhitespace() {
        final Base45 codec = new Base45();
        // "QED8WEX0" split across lines with CR+LF
        final byte[] expected = "ietf!".getBytes(StandardCharsets.US_ASCII);
        // With CR LF between groups
        assertArrayEquals(expected, codec.decode("QED\r\n8WEX0"), "Should skip CR+LF");
        // With LF only
        assertArrayEquals(expected, codec.decode("QED\n8WEX0"), "Should skip LF");
        // With TAB
        assertArrayEquals(expected, codec.decode("QED\t8WEX0"), "Should skip TAB");
    }

    /**
     * Tests that spaces (ASCII 32) ARE in the Base45 alphabet and are NOT skipped during decoding. Space has alphabet value 36.
     */
    @Test
    void testDecodeSpaceIsInAlphabet() {
        final Base45 codec = new Base45();
        // ' ' has value 36 in Base45 alphabet
        // Verify space is in alphabet
        assertTrue(codec.isInAlphabet((byte) ' '), "Space should be in Base45 alphabet");
        // Round-trip test for data that encodes to/contains a space
        final byte[] input = { (byte) 0xF0, (byte) 0xF0 }; // Some value that produces a space in output
        final byte[] encoded = codec.encode(input);
        final byte[] decoded = codec.decode(encoded);
        assertArrayEquals(input, decoded, "Round-trip with space in encoding failed");
    }

    /**
     * Tests decoding via the String overload of {@code decode}.
     */
    @Test
    void testDecodeStringRfc9285TestVectors() {
        final Base45 codec = new Base45();
        for (final String[] testCase : RFC9285_TEST_VECTORS) {
            final String plainText = testCase[0];
            final String encoded = testCase[1];
            final byte[] expected = plainText.getBytes(StandardCharsets.ISO_8859_1);
            final byte[] actual = codec.decode(encoded);
            assertArrayEquals(expected, actual, () -> "RFC 9285 decode(String) failed for: '" + encoded + "'");
        }
    }

    /**
     * Tests that the DECODE_TABLE has exactly 128 entries (covering the full ASCII range).
     */
    @Test
    void testDecodeTableLength() {
        assertEquals(128, Base45.DECODE_TABLE.length);
    }

    /**
     * Tests that an encoded 3-character triple that decodes to a value exceeding 65535 is rejected. The maximum valid 3-character value is 44 + 44*45 + 44*2025
     * = 91124, which exceeds 65535.
     */
    @Test
    void testDecodeTripleExceedingMaxValue() {
        final Base45 codec = new Base45();
        // ':' ':' ':' = value 44, 44*45, 44*2025 = 44 + 1980 + 89100 = 91124 > 65535
        // In the alphabet ':' = 44 (last entry)
        assertThrows(IllegalArgumentException.class, () -> codec.decode(":::"),
                "Triple ':' ':' ':' decodes to 91124 which exceeds 65535 and should be rejected");
    }

    /**
     * Tests decoding of "000" -> [0x00, 0x00].
     */
    @Test
    void testDecodeZeroZero() {
        assertArrayEquals(new byte[] { 0, 0 }, new Base45().decode("000"));
    }

    /**
     * Tests encoding of "AB" -> "BB8". A=65, B=66: n = 65*256+66 = 16706; c=11('B'), d=11('B'), e=8('8') -> "BB8"
     */
    @Test
    void testEncodeAB() {
        final byte[] input = "AB".getBytes(StandardCharsets.US_ASCII);
        assertEquals("BB8", new Base45().encodeToString(input));
        assertArrayEquals(input, new Base45().decode("BB8"));
    }

    /**
     * Tests the {@link Base45#encodeAsString(byte[])} convenience method.
     */
    @Test
    void testEncodeAsString() {
        final Base45 codec = new Base45();
        final byte[] input = "ietf!".getBytes(StandardCharsets.US_ASCII);
        assertEquals("QED8WEX0", codec.encodeAsString(input));
        assertEquals(codec.encodeToString(input), codec.encodeAsString(input));
    }

    /**
     * Tests encoding of all possible single-byte values for round-trip correctness.
     */
    @Test
    void testEncodeDecodeSingleByteRoundTrip() {
        final Base45 codec = new Base45();
        for (int i = 0; i <= 255; i++) {
            final byte[] input = { (byte) i };
            final byte[] encoded = codec.encode(input);
            assertEquals(2, encoded.length, "Single byte should encode to 2 chars, byte value: " + i);
            final byte[] decoded = codec.decode(encoded);
            assertArrayEquals(input, decoded, "Round-trip failed for byte value: " + i);
        }
    }

    /**
     * Verifies that all 45 ENCODE_TABLE entries are in the DECODE_TABLE with the correct index.
     */
    @Test
    void testEncodeDecodeTableConsistency() {
        final byte[] encodeTable = Base45.ENCODE_TABLE;
        final byte[] decodeTable = Base45.DECODE_TABLE;
        for (int i = 0; i < encodeTable.length; i++) {
            final int encoded = encodeTable[i] & 0xFF;
            assertTrue(encoded < decodeTable.length, "Encode table char " + (char) encoded + " at index " + i + " exceeds decode table length");
            assertEquals(i, decodeTable[encoded], "Decode table mismatch for char '" + (char) encoded + "' at encode index " + i);
        }
    }

    /**
     * Tests encoding of all possible two-byte values for round-trip correctness. Checks a sample to avoid exhaustive O(65536) iterations being slow.
     */
    @Test
    void testEncodeDecodeTwoByteRoundTrip() {
        final Base45 codec = new Base45();
        // Test specific important values
        final int[] interestingValues = { 0, 1, 44, 45, 254, 255, 256, 2024, 2025, 65534, 65535 };
        for (final int n : interestingValues) {
            final byte[] input = { (byte) (n >> 8), (byte) (n & 0xFF) };
            final byte[] encoded = codec.encode(input);
            assertEquals(3, encoded.length, "Two bytes should encode to 3 chars, n=" + n);
            final byte[] decoded = codec.decode(encoded);
            assertArrayEquals(input, decoded, "Round-trip failed for two-byte value n=" + n);
        }
    }

    /**
     * Tests that the codec correctly handles inputs where encoded output contains spaces (space = Base45 value 36), ensuring they are preserved through the
     * encode-decode cycle.
     */
    @Test
    void testEncodeDecodeWithSpaceInOutput() {
        final Base45 codec = new Base45();
        // Find byte pairs that encode to include a space (' ' = value 36):
        // We need n such that n % 45 == 36, or (n/45) % 45 == 36, or n/2025 == 36.
        // For n % 45 == 36: e.g., n = 36 -> b0=0, b1=36
        final byte[] input = { 0, 36 }; // n = 36, first char = ' '
        final byte[] encoded = codec.encode(input);
        assertTrue(codec.isInAlphabet(encoded[0]), "First encoded char should be in alphabet");
        assertEquals((byte) ' ', encoded[0], "First encoded char should be space (value 36)");
        assertArrayEquals(input, codec.decode(encoded));
    }

    /**
     * Tests the relationship between input length and encoded length. Per RFC 9285: encoded_length = (n / 2) * 3 + (n % 2 != 0 ? 2 : 0).
     */
    @Test
    void testEncodedLength() {
        final Base45 codec = new Base45();
        final int[] inputLengths = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000 };
        for (final int n : inputLengths) {
            final byte[] input = new byte[n];
            final long expected = (long) n / 2 * 3 + (n % 2 != 0 ? 2 : 0);
            assertEquals(expected, codec.getEncodedLength(input), "getEncodedLength incorrect for input length " + n);
        }
    }

    /**
     * Tests that encoded output contains only valid Base45 characters.
     */
    @Test
    void testEncodedOutputIsInAlphabet() {
        final Base45 codec = new Base45();
        final Random rng = new Random(42L);
        for (int len = 0; len <= 100; len++) {
            final byte[] input = new byte[len];
            rng.nextBytes(input);
            final byte[] encoded = codec.encode(input);
            for (final byte b : encoded) {
                assertTrue(codec.isInAlphabet(b), "Encoded byte " + b + " ('" + (char) b + "') is not in Base45 alphabet");
            }
        }
    }

    /**
     * Tests that an empty byte array encodes to an empty byte array.
     */
    @Test
    void testEncodeEmpty() {
        final Base45 codec = new Base45();
        assertArrayEquals(new byte[0], codec.encode(new byte[0]));
        assertEquals("", codec.encodeToString(new byte[0]));
    }

    /**
     * Tests that [0xFF, 0xFF] encodes to "FGW". n=65535: c=15('F'), d=16('G'), e=32('W') -> "FGW"
     */
    @Test
    void testEncodeMaxMax() {
        final byte[] input = { (byte) 0xFF, (byte) 0xFF };
        assertEquals("FGW", new Base45().encodeToString(input));
    }

    /**
     * Tests that {@link Base45} implements BinaryEncoder correctly via the {@code encode(Object)} method.
     */
    @Test
    void testEncodeObject() throws EncoderException {
        final Base45 codec = new Base45();
        final byte[] input = "ietf!".getBytes(StandardCharsets.US_ASCII);
        final Object result = codec.encode((Object) input);
        assertArrayEquals("QED8WEX0".getBytes(StandardCharsets.US_ASCII), (byte[]) result);
    }

    /**
     * Tests that encoding a non-byte-array Object throws EncoderException.
     */
    @Test
    void testEncodeObjectThrowsForNonByteArray() {
        final Base45 codec = new Base45();
        assertThrows(EncoderException.class, () -> codec.encode("not a byte array"));
    }

    /**
     * Pins the {@code encode} postcondition that {@code Context#ibitWorkArea} is reset after each complete 2-byte group is emitted.
     * <p>
     * Regression guard: without the reset, the work area would hold stale high bits from previous groups and output correctness would depend solely on the
     * {@code & 0xFFFF}/{@code & 0xFF} output masks.
     * </p>
     */
    @Test
    void testEncodeResetsWorkAreaAfterCompleteGroup() {
        final Base45 codec = new Base45();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        final byte[] data = { 1, 2, 3, 4, 5, 6 }; // three complete 2-byte groups
        codec.encode(data, 0, data.length, context);
        assertEquals(0, context.modulus, "modulus must be 0 after an even number of encoded bytes");
        assertEquals(0, context.ibitWorkArea, "encode() must reset ibitWorkArea after emitting each complete group, not leave stale accumulator data");
    }

    /**
     * Tests the RFC 9285 Section 4.4 encoding test vectors.
     */
    @Test
    void testEncodeRfc9285TestVectors() {
        final Base45 codec = new Base45();
        for (final String[] testCase : RFC9285_TEST_VECTORS) {
            final String plainText = testCase[0];
            final String expected = testCase[1];
            final byte[] input = plainText.getBytes(StandardCharsets.ISO_8859_1);
            final String actual = codec.encodeToString(input);
            assertEquals(expected, actual, "RFC 9285 encode failed for: '" + plainText + "'");
        }
    }

    /**
     * Tests encoding of a single byte with value 65 ('A'). n=65: 65%45=20 ('K'), 65/45=1 ('1') -> "K1"
     */
    @Test
    void testEncodeSingleByteLetterA() {
        final byte[] input = "A".getBytes(StandardCharsets.US_ASCII); // 'A' = 65
        assertEquals("K1", new Base45().encodeToString(input));
        assertArrayEquals(input, new Base45().decode("K1"));
    }

    /**
     * Tests encoding of a single byte with value 1. n=1: 1%45=1 ('1'), 1/45=0 ('0') -> "10"
     */
    @Test
    void testEncodeSingleByteOne() {
        final byte[] input = { 1 };
        assertEquals("10", new Base45().encodeToString(input));
        assertArrayEquals(input, new Base45().decode("10"));
    }

    /**
     * Tests encoding of a single max-value byte (0xFF = 255). n=255: 255%45=30('U'), 255/45=5('5') -> "U5"
     */
    @Test
    void testEncodeSingleMaxByte() {
        final byte[] input = { (byte) 0xFF };
        assertEquals("U5", new Base45().encodeToString(input));
    }

    /**
     * Tests encoding of a single zero byte. n=0: c=0('0'), d=0('0') -> "00"
     */
    @Test
    void testEncodeSingleZeroByte() {
        final byte[] input = { 0 };
        assertEquals("00", new Base45().encodeToString(input));
    }

    /**
     * Verifies the first 10 entries (digits 0-9) of the ENCODE_TABLE.
     */
    @Test
    void testEncodeTableDigits() {
        for (int i = 0; i <= 9; i++) {
            assertEquals((byte) ('0' + i), Base45.ENCODE_TABLE[i], "ENCODE_TABLE[" + i + "] should be digit '" + (char) ('0' + i) + "'");
        }
    }

    /**
     * Verifies that the ENCODE_TABLE has exactly 45 entries.
     */
    @Test
    void testEncodeTableHas45Entries() {
        assertEquals(45, Base45.ENCODE_TABLE.length);
    }

    /**
     * Verifies the special-character entries 36-44 of the ENCODE_TABLE.
     */
    @Test
    void testEncodeTableSpecialChars() {
        assertEquals((byte) ' ', Base45.ENCODE_TABLE[36], "ENCODE_TABLE[36] should be space");
        assertEquals((byte) '$', Base45.ENCODE_TABLE[37], "ENCODE_TABLE[37] should be '$'");
        assertEquals((byte) '%', Base45.ENCODE_TABLE[38], "ENCODE_TABLE[38] should be '%'");
        assertEquals((byte) '*', Base45.ENCODE_TABLE[39], "ENCODE_TABLE[39] should be '*'");
        assertEquals((byte) '+', Base45.ENCODE_TABLE[40], "ENCODE_TABLE[40] should be '+'");
        assertEquals((byte) '-', Base45.ENCODE_TABLE[41], "ENCODE_TABLE[41] should be '-'");
        assertEquals((byte) '.', Base45.ENCODE_TABLE[42], "ENCODE_TABLE[42] should be '.'");
        assertEquals((byte) '/', Base45.ENCODE_TABLE[43], "ENCODE_TABLE[43] should be '/'");
        assertEquals((byte) ':', Base45.ENCODE_TABLE[44], "ENCODE_TABLE[44] should be ':'");
    }

    /**
     * Verifies entries 10-35 (uppercase A-Z) of the ENCODE_TABLE.
     */
    @Test
    void testEncodeTableUppercase() {
        for (int i = 0; i < 26; i++) {
            assertEquals((byte) ('A' + i), Base45.ENCODE_TABLE[10 + i], "ENCODE_TABLE[" + (10 + i) + "] should be letter '" + (char) ('A' + i) + "'");
        }
    }

    /**
     * Tests compatibility with the {@code encode(byte[], int, int)} overload.
     */
    @Test
    void testEncodeWithOffsetAndLength() {
        final Base45 codec = new Base45();
        final byte[] buffer = new byte[10];
        // Fill with "ietf!" bytes at offset 2
        final byte[] input = "ietf!".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(input, 0, buffer, 2, input.length);
        final byte[] encoded = codec.encode(buffer, 2, 5);
        assertEquals("QED8WEX0", new String(encoded, StandardCharsets.US_ASCII));
    }

    /**
     * Companion to {@link #testEncodeResetsWorkAreaAfterCompleteGroup}: with a pending (odd) byte, the work area must hold exactly that pending byte, nothing
     * more.
     * <p>
     * Regression guard for the {@code Base45#encode} work-area reset.
     * </p>
     */
    @Test
    void testEncodeWorkAreaHoldsOnlyPendingByte() {
        final Base45 codec = new Base45();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        final byte[] data = { 1, 2, 3, 4, 99 }; // two complete groups + one pending byte (99)
        codec.encode(data, 0, data.length, context);
        assertEquals(1, context.modulus, "one pending byte must leave modulus == 1");
        assertEquals(99, context.ibitWorkArea, "after two complete groups, ibitWorkArea must hold only the pending byte, not stale high bits");
    }

    /**
     * Tests that two zero bytes encode to "000". n=0: c=0('0'), d=0('0'), e=0('0') -> "000"
     */
    @Test
    void testEncodeZeroZero() {
        final byte[] input = { 0, 0 };
        assertEquals("000", new Base45().encodeToString(input));
    }

    /**
     * Tests {@link Base45#getEncodedLength(byte[])} for known input lengths.
     */
    @Test
    void testGetEncodedLength() {
        final Base45 codec = new Base45();
        assertEquals(0L, codec.getEncodedLength(new byte[0])); // 0 bytes -> 0 chars
        assertEquals(2L, codec.getEncodedLength(new byte[1])); // 1 byte -> 2 chars
        assertEquals(3L, codec.getEncodedLength(new byte[2])); // 2 bytes -> 3 chars
        assertEquals(5L, codec.getEncodedLength(new byte[3])); // 3 bytes -> 5 chars
        assertEquals(6L, codec.getEncodedLength(new byte[4])); // 4 bytes -> 6 chars
        assertEquals(8L, codec.getEncodedLength(new byte[5])); // 5 bytes -> 8 chars
        assertEquals(9L, codec.getEncodedLength(new byte[6])); // 6 bytes -> 9 chars
        assertEquals(11L, codec.getEncodedLength(new byte[7])); // 7 bytes -> 11 chars
    }

    /**
     * Tests that the actual encoded length matches the value returned by getEncodedLength.
     */
    @Test
    void testGetEncodedLengthMatchesActual() {
        final Base45 codec = new Base45();
        for (int len = 0; len <= 30; len++) {
            final byte[] input = new byte[len];
            Arrays.fill(input, (byte) 0xAB);
            final byte[] encoded = codec.encode(input);
            assertEquals(codec.getEncodedLength(input), encoded.length, "getEncodedLength disagrees with actual length for input length " + len);
        }
    }

    /**
     * Tests {@link Base45#isInAlphabet(byte)} for all 45 valid alphabet characters.
     */
    @Test
    void testIsInAlphabet_allValidChars() {
        final Base45 codec = new Base45();
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";
        assertEquals(45, alphabet.length());
        for (final char c : alphabet.toCharArray()) {
            assertTrue(codec.isInAlphabet((byte) c), "Character '" + c + "' (ASCII " + (int) c + ") should be in Base45 alphabet");
        }
    }

    /**
     * Tests {@link Base45#isInAlphabet(byte)} for characters NOT in the Base45 alphabet.
     */
    @Test
    void testIsInAlphabet_invalidChars() {
        final Base45 codec = new Base45();
        // Control characters
        for (int i = 0; i < 32; i++) {
            if (i != ' ') { // space is at 32
                // None of 0-31 are in the alphabet
                assertFalse(codec.isInAlphabet((byte) i), "Control char " + i + " should not be in Base45 alphabet");
            }
        }
        // Characters between valid ranges
        assertFalse(codec.isInAlphabet((byte) '!'), "! should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '"'), "\" should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '#'), "# should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '&'), "& should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '\''), "' should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '('), "( should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) ')'), ") should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) ','), ", should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) ';'), "; should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '<'), "< should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '='), "= should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '>'), "> should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '?'), "? should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '@'), "@ should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '['), "[ should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '\\'), "\\ should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) ']'), "] should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '^'), "^ should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '_'), "_ should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) '`'), "` should not be in Base45 alphabet");
        // lowercase letters
        for (char c = 'a'; c <= 'z'; c++) {
            assertFalse(codec.isInAlphabet((byte) c), "Lowercase '" + c + "' should not be in Base45 alphabet");
        }
        // High bytes (> 127)
        assertFalse(codec.isInAlphabet((byte) 0x80), "Byte 0x80 should not be in Base45 alphabet");
        assertFalse(codec.isInAlphabet((byte) 0xFF), "Byte 0xFF should not be in Base45 alphabet");
    }

    /**
     * Tests that the pad character is NOT treated as part of the alphabet, because Base45 (RFC 9285) has no padding, while whitespace is still honored per the
     * {@code allowWhitespacePad} flag (consistent with {@code decode} skipping it).
     */
    @Test
    void testIsInAlphabetArrayDoesNotAllowPad() {
        final Base45 codec = new Base45();
        assertFalse(codec.isInAlphabet("QED="), "'=' (inherited pad) must not be treated as in-alphabet for padding-less Base45");
        assertFalse(codec.isInAlphabet("QED=".getBytes(StandardCharsets.US_ASCII), true));
        assertFalse(codec.isInAlphabet("QED=".getBytes(StandardCharsets.US_ASCII), false));
        assertTrue(codec.isInAlphabet("QED\t8WE".getBytes(StandardCharsets.US_ASCII), true));
        assertFalse(codec.isInAlphabet("QED\t8WE".getBytes(StandardCharsets.US_ASCII), false));
        assertTrue(codec.isInAlphabet("QED8WEX0".getBytes(StandardCharsets.US_ASCII), true));
    }

    /**
     * Tests {@link Base45#isInAlphabet(byte[])} for valid and invalid arrays.
     */
    @Test
    void testIsInAlphabetByteArray() {
        final Base45 codec = new Base45();
        assertTrue(codec.isInAlphabet("QED8WEX0".getBytes(StandardCharsets.US_ASCII), false));
        assertTrue(codec.isInAlphabet(new byte[0], false));
        assertFalse(codec.isInAlphabet("QED!WEX0".getBytes(StandardCharsets.US_ASCII), false));
        assertFalse(codec.isInAlphabet("abc".getBytes(StandardCharsets.US_ASCII), false));
    }

    /**
     * Tests that long inputs (more than 2 encoding blocks) encode and decode correctly.
     */
    @Test
    void testLongInputRoundTrip() {
        final Base45 codec = new Base45();
        final byte[] input = new byte[1000];
        new Random(99999L).nextBytes(input);
        final byte[] encoded = codec.encode(input);
        assertEquals(codec.getEncodedLength(input), encoded.length);
        assertArrayEquals(input, codec.decode(encoded));
    }

    /**
     * Verifies RFC 9285 Test Vector 1: "ietf!" encodes to "QED8WEX0".
     * <p>
     * Manually verified:
     * <ul>
     * <li>Group [i=105, e=101]: n=26981; c=26 (Q), d=14 (E), e=13 (D) -> "QED"</li>
     * <li>Group [t=116, f=102]: n=29798; c=8 (8), d=32 (W), e=14 (E) -> "8WE"</li>
     * <li>Tail [!=33]: n=33; c=33 (X), d=0 (0) -> "X0"</li>
     * </ul>
     */
    @Test
    void testRfc9285TestVector1_ietf() {
        final byte[] input = "ietf!".getBytes(StandardCharsets.US_ASCII);
        assertEquals("QED8WEX0", new Base45().encodeToString(input));
        assertArrayEquals(input, new Base45().decode("QED8WEX0"));
    }

    /**
     * Verifies RFC 9285 Test Vector 2: "base-45" encodes to "UJCLQE7W581".
     * <p>
     * Manually verified:
     * <ul>
     * <li>Group [b=98, a=97]: n=25185; c=30 (U), d=19 (J), e=12 (C) -> "UJC"</li>
     * <li>Group [s=115, e=101]: n=29541; c=21 (L), d=26 (Q), e=14 (E) -> "LQE"</li>
     * <li>Group [-=45, 4=52]: n=11572; c=7 (7), d=32 (W), e=5 (5) -> "7W5"</li>
     * <li>Tail [5=53]: n=53; c=8 (8), d=1 (1) -> "81"</li>
     * </ul>
     */
    @Test
    void testRfc9285TestVector2_base45() {
        final byte[] input = "base-45".getBytes(StandardCharsets.US_ASCII);
        assertEquals("UJCLQE7W581", new Base45().encodeToString(input));
        assertArrayEquals(input, new Base45().decode("UJCLQE7W581"));
    }

    /**
     * Tests round-trip for binary data with all byte values.
     */
    @Test
    void testRoundTripAllByteValues() {
        final Base45 codec = new Base45();
        final byte[] allBytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            allBytes[i] = (byte) i;
        }
        final byte[] encoded = codec.encode(allBytes);
        final byte[] decoded = codec.decode(encoded);
        assertArrayEquals(allBytes, decoded, "Round-trip failed for all byte values");
    }

    /**
     * Tests round-trip encoding/decoding of all byte lengths from 0 to 50.
     */
    @Test
    void testRoundTripAllLengths() {
        final Base45 codec = new Base45();
        final Random rng = new Random(12345L);
        for (int len = 0; len <= 50; len++) {
            final byte[] input = new byte[len];
            rng.nextBytes(input);
            final byte[] encoded = codec.encode(input);
            final byte[] decoded = codec.decode(encoded);
            assertArrayEquals(input, decoded, "Round-trip failed for length " + len);
        }
    }

    /**
     * Tests round-trip for various well-known strings.
     */
    @Test
    void testRoundTripStrings() {
        final Base45 codec = new Base45();
        // @formatter:off
        final String[] inputs = {
            "Hello, World!",
            "The quick brown fox jumps over the lazy dog",
            "Apache Commons Codec",
            "0123456789",
            "\u0000\u0001\u0002",
            "Base45 (RFC 9285)",
        };
        // @formatter:on
        for (final String input : inputs) {
            final byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            final byte[] decoded = codec.decode(codec.encode(bytes));
            assertArrayEquals(bytes, decoded, () -> "Round-trip failed for: " + input);
        }
    }

    /**
     * Tests that encoding a single byte and then decoding gives the correct value for boundary cases around multiples of 45.
     */
    @Test
    void testSingleByteAroundBase45Multiples() {
        final Base45 codec = new Base45();
        // Test byte values at multiples of 45: 0, 45, 90, 135, 180, 225
        for (int i = 0; i <= 255; i += 45) {
            final byte[] input = { (byte) i };
            final byte[] encoded = codec.encode(input);
            assertEquals(2, encoded.length, "Encoded length should be 2 for single byte, value=" + i);
            assertArrayEquals(input, codec.decode(encoded), "Round-trip failed for single byte value=" + i);
        }
    }

    /**
     * Tests streaming encode/decode with non-aligned chunk sizes to verify the encoder accumulator is correctly reset between blocks.
     */
    @Test
    void testStreamingEncodeDecodeIncremental() throws IOException {
        final byte[] input = "The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Base45OutputStream out = new Base45OutputStream(baos)) {
            // Write in 3-byte chunks, which is not aligned to the 2-byte Base45 block size
            for (int i = 0; i < input.length; i += 3) {
                final int len = Math.min(3, input.length - i);
                out.write(input, i, len);
            }
        }
        compare(input, baos);
    }

    /**
     * Tests streaming encode/decode with 1-byte writes to verify the encoder accumulator is correctly reset between blocks.
     */
    @Test
    void testStreamingEncodeDecodeOneByteChunks() throws IOException {
        final byte[] input = "The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Base45OutputStream out = new Base45OutputStream(baos)) {
            for (final byte element : input) {
                out.write(element);
            }
        }
        compare(input, baos);
    }

    /**
     * Tests the encoding/decoding of two-byte pairs that produce all three encoded characters at the extremes of the Base45 alphabet (e.g., '0' and ':').
     */
    @Test
    void testTwoByteExtremeValues() {
        final Base45 codec = new Base45();
        // Value 0: n=0 -> "000"
        assertArrayEquals(new byte[] { 0, 0 }, codec.decode("000"));
        assertEquals("000", codec.encodeToString(new byte[] { 0, 0 }));
        // Value 65535: n=65535 -> "FGW" (verified: 15 + 16*45 + 32*2025 = 65535)
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, codec.decode("FGW"));
        assertEquals("FGW", codec.encodeToString(new byte[] { (byte) 0xFF, (byte) 0xFF }));
    }
}
