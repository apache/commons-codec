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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base58}.
 */
public class Base58Test {

    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

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
    void testEncodeDecodeSmall() {
        for (int i = 0; i < 12; i++) {
            final byte[] data = new byte[i];
            random.nextBytes(data);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEquals(data, dec);
        }
    }

    @Test
    void testEncodeDecodeRandom() {
        for (int i = 1; i < 5; i++) {
            final byte[] data = new byte[random.nextInt(10000) + 1];
            random.nextBytes(data);
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEquals(data, dec);
        }
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
    void testLeadingZeros() {
        // Test that leading zero bytes are encoded as '1' characters
        final byte[] input = new byte[] { 0, 0, 1, 2, 3 };
        final byte[] encoded = new Base58().encode(input);
        final String encodedStr = new String(encoded);
        
        // Should start with "11" (two leading ones for two leading zeros)
        assertTrue(encodedStr.startsWith("11"), "Leading zeros should encode as '1' characters");
        
        // Decode should restore the leading zeros
        final byte[] decoded = new Base58().decode(encoded);
        assertArrayEquals(input, decoded, "Decoded should match original including leading zeros");
    }

    @Test
    void testSingleBytes() {
        // Test encoding of single bytes
        for (int i = 1; i <= 255; i++) {
            final byte[] data = new byte[] { (byte) i };
            final byte[] enc = new Base58().encode(data);
            final byte[] dec = new Base58().decode(enc);
            assertArrayEquals(data, dec, "Failed for byte value: " + i);
        }
    }

    @Test
    void testInvalidCharacters() {
        // Test decoding with invalid characters (those not in Base58 alphabet)
        final byte[] invalidChars = "0OIl".getBytes(CHARSET_UTF8); // All excluded from Base58
        assertThrows(IllegalArgumentException.class, () -> new Base58().decode(invalidChars));
    }

    @Test
    void testRoundTrip() {
        final String[] testStrings = {
            "",
            "a",
            "ab",
            "abc",
            "abcd",
            "abcde",
            "abcdef",
            "Hello World",
            "The quick brown fox jumps over the lazy dog",
            "1234567890",
            "!@#$%^&*()"
        };

        for (final String test : testStrings) {
            final byte[] input = test.getBytes(CHARSET_UTF8);
            final byte[] encoded = new Base58().encode(input);
            final byte[] decoded = new Base58().decode(encoded);
            assertArrayEquals(input, decoded, "Round trip failed for: " + test);
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
}
