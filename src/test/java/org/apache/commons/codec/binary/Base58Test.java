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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.ArrayFill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link Base58}.
 */
public class Base58Test {

    private static final int BOUND = 10_000;

    private static final String DEFAULT_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private static void assertArrayEqualsAt(final byte[] data, final byte[] dec, final int i) {
        final AtomicInteger counter = new AtomicInteger(i);
        assertArrayEquals(data, dec, () -> String.format("Failed for length %,d: %s", counter.get(), Arrays.toString(data)));
    }

    private static byte[] fromHex(final String hex) {
        try {
            return Hex.decodeHex(hex);
        } catch (final DecoderException e) {
            throw new AssertionError("Invalid test-vector hex: " + hex, e);
        }
    }

    private static byte[] newEncodeTable() {
        return DEFAULT_ALPHABET.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] newSwappedEncodeTable() {
        final byte[] encodeTable = newEncodeTable();
        final byte tmp = encodeTable[0];
        encodeTable[0] = encodeTable[1];
        encodeTable[1] = tmp;
        return encodeTable;
    }

    private final Random random = new Random();

    @Test
    void testBase58() {
        final String content = "Hello World";
        final byte[] encodedBytes = new Base58().encode(StringUtils.getBytesUtf8(content));
        final String encodedContent = StringUtils.newStringUtf8(encodedBytes);
        assertEquals("JxF12TrwUP45BMd", encodedContent, "encoding hello world");
        final byte[] decodedBytes = new Base58().decode(encodedBytes);
        final String decodedContent = StringUtils.newStringUtf8(decodedBytes);
        assertEquals(content, decodedContent, "decoding hello world");
    }

    @Test
    void testBuilderCustomEncodeTableAffectsEncodeAndDecode() {
        final Base58 base58 = Base58.builder().setEncodeTable(newSwappedEncodeTable()).get();
        assertEquals("1", new String(base58.encode(new byte[] { 1 }), StandardCharsets.US_ASCII));
        assertArrayEquals(new byte[] { 1 }, base58.decode("1".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void testBuilderCustomEncodeTableAffectsIsInAlphabet() {
        final byte[] encodeTable = newEncodeTable();
        encodeTable[0] = '0';
        final Base58 base58 = Base58.builder().setEncodeTable(encodeTable).get();
        assertTrue(base58.isInAlphabet((byte) '0'));
        assertFalse(base58.isInAlphabet((byte) '1'));
        assertEquals("0", new String(base58.encode(new byte[] { 0 }), StandardCharsets.US_ASCII));
        assertArrayEquals(new byte[] { 0 }, base58.decode("0".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void testBuilderCustomEncodeTableAffectsLeadingZeros() {
        final Base58 base58 = Base58.builder().setEncodeTable(newSwappedEncodeTable()).get();
        final byte[] data = { 0, 0, 1 };
        final byte[] encoded = base58.encode(data);
        assertEquals("221", new String(encoded, StandardCharsets.US_ASCII));
        assertArrayEquals(data, base58.decode(encoded));
    }

    @Test
    void testBuilderCustomEncodeTableRejectsDuplicateEntries() {
        final byte[] encodeTable = newEncodeTable();
        encodeTable[1] = encodeTable[0];
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setEncodeTable(encodeTable));
    }

    @Test
    void testBuilderCustomEncodeTableRejectsInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setEncodeTable(Arrays.copyOf(newEncodeTable(), DEFAULT_ALPHABET.length() - 1)));
    }

    @ParameterizedTest
    @ValueSource(ints = { 20_000, 40_000, 80_000, 160_000, 320_000 })
    void testDecodeLargeInput(final int n) {
        // any valid non-'1' Base58 char
        new Base58().decode(ArrayFill.fill(new byte[n], (byte) 'z'));
    }

    /**
     * Verifies that characters not in the Base58 alphabet (whitespace, punctuation, excluded
     * letters) are rejected during decoding.
     */
    @ParameterizedTest
    @ValueSource(strings = { "0", "O", "I", "l", "+", "/", " ", "=", "~" })
    void testDecodeRejectsNonAlphabetCharacters(final String badChar) {
        // Wrap in a valid prefix/suffix so only the bad char triggers the error.
        final byte[] input = ("1" + badChar + "1").getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class, () -> new Base58().decode(input),
            "expected rejection of character: " + badChar);
    }

    @Test
    void testEmptyBase58() {
        byte[] empty = {};
        byte[] result = new Base58().encode(empty);
        assertEquals(0, result.length, "empty Base58 encode");
        assertNull(new Base58().encode(null), "empty Base58 encode");
        empty = new byte[0];
        result = new Base58().decode(empty);
        assertEquals(0, result.length, "empty Base58 decode");
        assertNull(new Base58().decode((byte[]) null), "empty Base58 decode");
    }

    @Test
    void testEncodeDecode() {
        for (int i = 1; i < 5; i++) {
            final byte[] data = new byte[random.nextInt(BOUND) + 1];
            Arrays.fill(data, (byte) i);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEqualsAt(data, dec, i);
        }
    }

    @Test
    void testEncodeDecodeRandom() {
        for (int i = 1; i < 5; i++) {
            final byte[] data = new byte[random.nextInt(BOUND) + 1];
            random.nextBytes(data);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEqualsAt(data, dec, i);
        }
    }

    @Test
    void testEncodeDecodeSmall() {
        for (int i = 0; i < 12; i++) {
            final byte[] data = new byte[i];
            Arrays.fill(data, (byte) i);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEqualsAt(data, dec, i);
        }
    }

    @Test
    void testEncodeDecodeSmallRandom() {
        for (int i = 0; i < 12; i++) {
            final byte[] data = new byte[i];
            random.nextBytes(data);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEqualsAt(data, dec, i);
        }
    }

    @Test
    void testHexEncoding() {
        final String hexString = "48656c6c6f20576f726c6421";
        final byte[] encoded = new Base58().encode(StringUtils.getBytesUtf8(hexString));
        final byte[] decoded = new Base58().decode(StringUtils.newStringUtf8(encoded));
        assertEquals("5m7UdtXCfQxGvX2K9dLrkNs7AFMS98qn8", StringUtils.newStringUtf8(encoded), "Hex encoding failed");
        assertEquals(hexString, StringUtils.newStringUtf8(decoded), "Hex decoding failed");
    }

    /**
     * Tests encode and decode against every test vector in the IETF Base58 encoding draft
     * (draft-msporny-base58-03, Appendix A). The hex column is the raw binary input; the second
     * column is the expected Base58 output.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/draft-msporny-base58-03#appendix-A">
     *      draft-msporny-base58-03 Appendix A</a>
     */
    @ParameterizedTest(name = "[{index}] hex={0}")
    @CsvSource({
        // single byte 'a' (0x61)
        "61,                                                                                     2g",
        // "bbb"
        "626262,                                                                                 a3gV",
        // "ccc"
        "636363,                                                                                 aPEr",
        // "simply a long string"
        "73696d706c792061206c6f6e6720737472696e67,                                               2cFupjhnEsSn59qHXstmK2ffpLv2",
        // leading zero byte + random payload (produces leading '1')
        // 25-byte Bitcoin address payload (version + RIPEMD160 hash + checksum) from Bitcoin wiki
        "00010966776006953d5567439e5e39f86a0d273beed61967f6,                                     16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM",
        "516b6fcd0f,                                                                             ABnLTmg",
        "bf4f89001e670274dd,                                                                     3SEo3LWLoPntC",
        "572e4794,                                                                               3EFU7m",
        "ecac89cad93923c02321,                                                                   EJDM8drfXA6uyA",
        "10c8511e,                                                                               Rt5zm",
        // ten zero bytes -> ten '1' characters
        "00000000000000000000,                                                                   1111111111",
        // 43-byte payload whose Base58 encoding is exactly the full alphabet in order
        "000111d38e5fc9071ffcd20b4a763cc9ae4f252bb4e48fd66a835e252ada93ff480d6dd43dc62a641155a5, 123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    })
    void testIetfDraftBase58Vectors(final String hex, final String expectedBase58) {
        final byte[] binary = fromHex(hex.trim());
        final String base58 = expectedBase58.trim();
        final byte[] base58Bytes = base58.getBytes(StandardCharsets.US_ASCII);
        assertArrayEquals(base58Bytes, new Base58().encode(binary), "encode failed for hex=" + hex.trim());
        assertArrayEquals(binary, new Base58().decode(base58Bytes), "decode failed for base58=" + base58);
    }

    @Test
    void testInvalidCharacters() {
        // Test decoding with invalid characters (those not in Base58 alphabet)
        final byte[] invalidChars = "0OIl".getBytes(CHARSET_UTF8); // All excluded from Base58
        assertThrows(IllegalArgumentException.class, () -> new Base58().decode(invalidChars));
    }

    @Test
    void testIsInAlphabet() {
        final Base58 base58 = new Base58();
        // Valid characters
        for (char c = '1'; c <= '9'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        for (char c = 'A'; c <= 'H'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        for (char c = 'J'; c <= 'N'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        for (char c = 'P'; c <= 'Z'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        for (char c = 'a'; c <= 'k'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        for (char c = 'm'; c <= 'z'; c++) {
            assertTrue(base58.isInAlphabet((byte) c), "char " + c);
        }
        // Invalid characters - excluded from Base58
        assertFalse(base58.isInAlphabet((byte) '0'), "char 0");
        assertFalse(base58.isInAlphabet((byte) 'O'), "char O");
        assertFalse(base58.isInAlphabet((byte) 'I'), "char I");
        assertFalse(base58.isInAlphabet((byte) 'l'), "char l");
        // Out of bounds
        assertFalse(base58.isInAlphabet((byte) -1));
        assertFalse(base58.isInAlphabet((byte) 0));
        assertFalse(base58.isInAlphabet((byte) 128));
        assertFalse(base58.isInAlphabet((byte) 255));
    }

    /**
     * Verifies that the number of leading {@code '1'} characters in the encoded output exactly
     * equals the number of leading zero bytes in the input, for a range of counts.
     */
    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 5, 10 })
    void testLeadingZeroByteCountPreserved(final int zeros) {
        // Append a non-zero tail so the total value is non-trivial.
        final byte[] data = new byte[zeros + 3];
        data[zeros]     = 0x01;
        data[zeros + 1] = 0x02;
        data[zeros + 2] = 0x03;
        final byte[] encoded = new Base58().encode(data);
        int leadingOnes = 0;
        for (final byte b : encoded) {
            if (b != '1') {
                break;
            }
            leadingOnes++;
        }
        assertEquals(zeros, leadingOnes, "leading '1' count must equal leading zero-byte count");
        assertArrayEquals(data, new Base58().decode(encoded), "round-trip must preserve leading zeros");
    }

    @Test
    void testLeadingZeros() {
        // Test that leading zero bytes are encoded as '1' characters
        final byte[] input = { 0, 0, 1, 2, 3 };
        final byte[] encoded = new Base58().encode(input);
        final String encodedStr = new String(encoded);
        // Should start with "11" (two leading ones for two leading zeros)
        assertTrue(encodedStr.startsWith("11"), "Leading zeros should encode as '1' characters");
        // Decode should restore the leading zeros
        final byte[] decoded = new Base58().decode(encoded);
        assertArrayEquals(input, decoded, "Decoded should match original including leading zeros");
    }

    @Test
    void testObjectDecodeWithInvalidParameter() {
        assertThrows(DecoderException.class, () -> new Base58().decode(Integer.valueOf(5)));
    }

    @Test
    void testObjectDecodeWithValidParameter() throws Exception {
        final String original = "Hello World!";
        final Object o = new Base58().encode(original.getBytes(CHARSET_UTF8));
        final Base58 base58 = new Base58();
        final Object oDecoded = base58.decode(o);
        final byte[] baDecoded = (byte[]) oDecoded;
        final String dest = new String(baDecoded);
        assertEquals(original, dest, "dest string does not equal original");
    }

    @Test
    void testObjectEncodeWithInvalidParameter() {
        assertThrows(EncoderException.class, () -> new Base58().encode("Yadayadayada"));
    }

    @Test
    void testObjectEncodeWithValidParameter() throws Exception {
        final String original = "Hello World!";
        final Object origObj = original.getBytes(CHARSET_UTF8);
        final Object oEncoded = new Base58().encode(origObj);
        final byte[] bArray = new Base58().decode((byte[]) oEncoded);
        final String dest = new String(bArray);
        assertEquals(original, dest, "dest string does not equal original");
    }

    @Test
    void testRoundTrip() {
        final String[] testStrings = { "", "a", "ab", "abc", "abcd", "abcde", "abcdef", "Hello World", "The quick brown fox jumps over the lazy dog",
                "1234567890", "!@#$%^&*()" };
        for (final String test : testStrings) {
            final byte[] input = test.getBytes(CHARSET_UTF8);
            final byte[] encoded = new Base58().encode(input);
            final byte[] decoded = new Base58().decode(encoded);
            assertArrayEquals(input, decoded, "Round trip failed for: " + test);
        }
    }

    /**
     * Round-trips arrays of all-{@code 0xFF} bytes (maximum unsigned byte value) to confirm
     * that bytes with the high bit set are handled correctly.
     */
    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 8, 16 })
    void testRoundTripAllMaxBytes(final int len) {
        final byte[] data = new byte[len];
        Arrays.fill(data, (byte) 0xFF);
        assertArrayEquals(data, new Base58().decode(new Base58().encode(data)),
            "round-trip failed for " + len + " x 0xFF bytes");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testRoundtripByte0(final int len) throws IOException {
        // Sanity check, each step from scratch:
        final byte[] zeros = new byte[len];
        final byte[] encoded0s = ArrayFill.fill(zeros.clone(), (byte) '1');
        assertArrayEquals(encoded0s, Base58.builder().get().encode(zeros));
        final byte[] decoded = Base58.builder().get().decode(encoded0s);
        assertArrayEquals(zeros, decoded, () -> String.format("zeros=%s, decoded=%s", Arrays.toString(zeros), Arrays.toString(decoded)));
    }

    @Test
    void testSingleBytes() {
        // Test encoding of single bytes
        for (int i = 1; i <= 255; i++) {
            final byte[] data = { (byte) i };
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEquals(data, dec, "Failed for byte value: " + i);
        }
    }

    /**
     * Encodes a single zero byte; it must produce the single character {@code '1'}, and the
     * round-trip must restore {@code [0x00]}.  This byte value is excluded from {@link #testSingleBytes()}
     * which starts at {@code 1}.
     */
    @Test
    void testSingleByteZero() {
        final byte[] data = { 0 };
        final byte[] encoded = new Base58().encode(data);
        assertArrayEquals(new byte[] { '1' }, encoded, "single zero byte must encode as '1'");
        assertArrayEquals(data, new Base58().decode(encoded), "round-trip of single zero byte");
    }

    @Test
    void testTestVectors() {
        final String content = "Hello World!";
        final String content1 = "The quick brown fox jumps over the lazy dog.";
        final long content2 = 0x0000287fb4cdL; // Use long to preserve the full 48-bit value
        final byte[] encodedBytes = new Base58().encode(StringUtils.getBytesUtf8(content));
        final byte[] encodedBytes1 = new Base58().encode(StringUtils.getBytesUtf8(content1));
        final byte[] content2Bytes = ByteBuffer.allocate(8).putLong(content2).array();
        final byte[] content2Trimmed = new byte[6];
        System.arraycopy(content2Bytes, 2, content2Trimmed, 0, 6);
        final byte[] encodedBytes2 = new Base58().encode(content2Trimmed);
        final String encodedContent = StringUtils.newStringUtf8(encodedBytes);
        final String encodedContent1 = StringUtils.newStringUtf8(encodedBytes1);
        final String encodedContent2 = StringUtils.newStringUtf8(encodedBytes2);
        assertEquals("2NEpo7TZRRrLZSi2U", encodedContent, "encoding hello world");
        assertEquals("USm3fpXnKG5EUBx2ndxBDMPVciP5hGey2Jh4NDv6gmeo1LkMeiKrLJUUBk6Z", encodedContent1);
        assertEquals("11233QC4", encodedContent2, "encoding 0x0000287fb4cd");
        final byte[] decodedBytes = new Base58().decode(encodedBytes);
        final byte[] decodedBytes1 = new Base58().decode(encodedBytes1);
        final byte[] decodedBytes2 = new Base58().decode(encodedBytes2);
        final String decodedContent = StringUtils.newStringUtf8(decodedBytes);
        final String decodedContent1 = StringUtils.newStringUtf8(decodedBytes1);
        assertEquals(content, decodedContent, "decoding hello world");
        assertEquals(content1, decodedContent1);
        assertArrayEquals(content2Trimmed, decodedBytes2, "decoding 0x0000287fb4cd");
    }
}
