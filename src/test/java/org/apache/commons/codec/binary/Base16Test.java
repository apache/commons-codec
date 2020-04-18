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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assume;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for Base16 class.
 *
 * @since 1.15
 */
public class Base16Test {

    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private final Random random = new Random();

    /**
     * @return Returns the random.
     */
    public Random getRandom() {
        return this.random;
    }

    /**
     * Test the isStringBase16 method.
     */
    @Test
    public void testIsStringBase16() {
        final String nullString = null;
        final String emptyString = "";
        final String validString = "cafebabec0a1f2e3b4a5b6e7c8";
        final String invalidString = validString + (char) 0; // append null
                                                                // character

        try {
            Base16.isBase16(nullString);
            fail("Base16.isStringBase16() should not be null-safe.");
        } catch (final NullPointerException npe) {
            assertNotNull("Base16.isStringBase16() should not be null-safe.", npe);
        }

        assertTrue("Base16.isStringBase16(empty-string) is true", Base16.isBase16(emptyString));
        assertTrue("Base16.isStringBase16(valid-string) is true", Base16.isBase16(validString));
        assertFalse("Base16.isStringBase16(invalid-string) is false", Base16.isBase16(invalidString));
    }

    /**
     * Test the Base16 implementation
     */
    @Test
    public void testBase16() {
        final String content = "Hello World";
        String encodedContent;
        byte[] encodedBytes = Base16.encodeBase16(StringUtils.getBytesUtf8(content));
        encodedContent = StringUtils.newStringUtf8(encodedBytes);
        assertEquals("encoding hello world", "48656c6c6f20576f726c64", encodedContent);

        Base16 b16 = new Base16();
        encodedBytes = b16.encode(StringUtils.getBytesUtf8(content));
        encodedContent = StringUtils.newStringUtf8(encodedBytes);
        assertEquals("encoding hello world", "48656c6c6f20576f726c64", encodedContent);
    }

    @Test
    public void testBase16AtBufferStart() {
        testBase16InBuffer(0, 100);
    }

    @Test
    public void testBase16AtBufferEnd() {
        testBase16InBuffer(100, 0);
    }

    @Test
    public void testBase16AtBufferMiddle() {
        testBase16InBuffer(100, 100);
    }

    private void testBase16InBuffer(final int startPasSize, final int endPadSize) {
        final String content = "Hello World";
        String encodedContent;
        final byte[] bytesUtf8 = StringUtils.getBytesUtf8(content);
        byte[] buffer = ArrayUtils.addAll(bytesUtf8, new byte[endPadSize]);
        buffer = ArrayUtils.addAll(new byte[startPasSize], buffer);
        final byte[] encodedBytes = new Base16().encode(buffer, startPasSize, bytesUtf8.length);
        encodedContent = StringUtils.newStringUtf8(encodedBytes);
        assertEquals("encoding hello world", "48656c6c6f20576f726c64", encodedContent);
    }

    /**
     * isBase16 throws RuntimeException on some
     * non-Base16 bytes
     */
    @Test(expected=RuntimeException.class)
    public void testCodec68() {
        final byte[] x = new byte[] { 'n', 'H', '=', '=', (byte) 0x9c };
        Base16.decodeBase16(x);
    }

    @Test
    public void testConstructors() {
        new Base16();
        new Base16();
        new Base16(CHARSET_UTF8);
        new Base16(false, CHARSET_UTF8);
    }

    @Test
    public void testConstructor_Charset() {
        final Base16 Base16 = new Base16(CHARSET_UTF8);
        final byte[] encoded = Base16.encode(Base16TestData.DECODED);
        final String expectedResult = Base16TestData.ENCODED_UTF8_LOWERCASE;
        final String result = StringUtils.newStringUtf8(encoded);
        assertEquals("new Base16(UTF_8)", expectedResult, result);
    }

    @Test
    public void testConstructor_Boolean_Charset() {
        final Base16 Base16 = new Base16(false, CHARSET_UTF8);
        final byte[] encoded = Base16.encode(Base16TestData.DECODED);
        final String expectedResult = Base16TestData.ENCODED_UTF8_UPPERCASE;
        final String result = StringUtils.newStringUtf8(encoded);
        assertEquals("new Base16(false, UTF_8)", result, expectedResult);
    }

    /**
     * Test encode and decode of empty byte array.
     */
    @Test
    public void testEmptyBase16() {
        byte[] empty = new byte[0];
        byte[] result = Base16.encodeBase16(empty);
        assertEquals("empty Base16 encode", 0, result.length);
        assertEquals("empty Base16 encode", null, Base16.encodeBase16(null));

        empty = new byte[0];
        result = Base16.decodeBase16(empty);
        assertEquals("empty Base16 decode", 0, result.length);
        assertEquals("empty Base16 encode", null, Base16.decodeBase16((byte[]) null));
    }

    // encode/decode a large random array
    @Test
    public void testEncodeDecodeRandom() {
        for (int i = 1; i < 5; i++) {
            final int len = this.getRandom().nextInt(10000) + 1;
            final byte[] data = new byte[len];
            this.getRandom().nextBytes(data);
            final byte[] enc = Base16.encodeBase16(data);
            assertTrue(Base16.isBase16(enc));
            final byte[] data2 = Base16.decodeBase16(enc);
            assertArrayEquals(data, data2);
        }
    }

    // encode/decode random arrays from size 0 to size 11
    @Test
    public void testEncodeDecodeSmall() {
        for (int i = 0; i < 12; i++) {
            final byte[] data = new byte[i];
            this.getRandom().nextBytes(data);
            final byte[] enc = Base16.encodeBase16(data);
            assertTrue("\"" + new String(enc) + "\" is Base16 data.", Base16.isBase16(enc));
            final byte[] data2 = Base16.decodeBase16(enc);
            assertArrayEquals(toString(data) + " equals " + toString(data2), data, data2);
        }
    }

    @Test
    public void testEncodeOverMaxSize() {
        testEncodeOverMaxSize(-1);
        testEncodeOverMaxSize(0);
        testEncodeOverMaxSize(1);
        testEncodeOverMaxSize(2);
    }

    private void testEncodeOverMaxSize(final int maxSize) {
        try {
            Base16.encodeBase16(Base16TestData.DECODED, true, CHARSET_UTF8, maxSize);
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (final IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testIsArrayByteBase16() {
        assertFalse(Base16.isBase16(new char[] { (char)Byte.MIN_VALUE }));
        assertFalse(Base16.isBase16(new char[] { (char)-125 }));
        assertFalse(Base16.isBase16(new char[] { (char)-10 }));
        assertFalse(Base16.isBase16(new char[] { 0 }));
        assertFalse(Base16.isBase16(new char[] { 64, Byte.MAX_VALUE }));
        assertFalse(Base16.isBase16(new char[] { Byte.MAX_VALUE }));
        assertTrue(Base16.isBase16(new char[] { 'A' }));
        assertFalse(Base16.isBase16(new char[] { 'A', (char)Byte.MIN_VALUE }));
        assertTrue(Base16.isBase16(new char[] { 'A', 'F', 'a' }));
        assertFalse(Base16.isBase16(new char[] { '/', '=', '+' }));
        assertFalse(Base16.isBase16(new char[] { '$' }));
    }

    @Test
    public void testKnownDecodings() {
        assertEquals("The quick brown fox jumped over the lazy dogs.", new String(Base16.decodeBase16(
                "54686520717569636b2062726f776e20666f78206a756d706564206f76657220746865206c617a7920646f67732e".getBytes(CHARSET_UTF8))));
        assertEquals("It was the best of times, it was the worst of times.", new String(Base16.decodeBase16(
                "497420776173207468652062657374206f662074696d65732c206974207761732074686520776f727374206f662074696d65732e".getBytes(CHARSET_UTF8))));
        assertEquals("http://jakarta.apache.org/commmons", new String(
                Base16.decodeBase16("687474703a2f2f6a616b617274612e6170616368652e6f72672f636f6d6d6d6f6e73".getBytes(CHARSET_UTF8))));
        assertEquals("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz", new String(Base16.decodeBase16(
                "4161426243634464456546664767486849694a6a4b6b4c6c4d6d4e6e4f6f50705171527253735474557556765777587859795a7a".getBytes(CHARSET_UTF8))));
        assertEquals("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }",
                new String(Base16.decodeBase16("7b20302c20312c20322c20332c20342c20352c20362c20372c20382c2039207d".getBytes(CHARSET_UTF8))));
        assertEquals("xyzzy!", new String(Base16.decodeBase16("78797a7a7921".getBytes(CHARSET_UTF8))));
    }

    @Test
    public void testKnownEncodings() {
        assertEquals("54686520717569636b2062726f776e20666f78206a756d706564206f76657220746865206c617a7920646f67732e", new String(
                Base16.encodeBase16("The quick brown fox jumped over the lazy dogs.".getBytes(CHARSET_UTF8))));
        assertEquals("497420776173207468652062657374206f662074696d65732c206974207761732074686520776f727374206f662074696d65732e", new String(
                Base16.encodeBase16("It was the best of times, it was the worst of times.".getBytes(CHARSET_UTF8))));
        assertEquals("687474703a2f2f6a616b617274612e6170616368652e6f72672f636f6d6d6d6f6e73",
                new String(Base16.encodeBase16("http://jakarta.apache.org/commmons".getBytes(CHARSET_UTF8))));
        assertEquals("4161426243634464456546664767486849694a6a4b6b4c6c4d6d4e6e4f6f50705171527253735474557556765777587859795a7a", new String(
                Base16.encodeBase16("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".getBytes(CHARSET_UTF8))));
        assertEquals("7b20302c20312c20322c20332c20342c20352c20362c20372c20382c2039207d",
                new String(Base16.encodeBase16("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".getBytes(CHARSET_UTF8))));
        assertEquals("78797a7a7921", new String(Base16.encodeBase16("xyzzy!".getBytes(CHARSET_UTF8))));
    }

    @Test
    public void testNonBase16Test() {
        final byte[] bArray = { '%' };

        assertFalse("Invalid Base16 array was incorrectly validated as " + "an array of Base16 encoded data",
                Base16.isBase16(bArray));

        try {
            final Base16 b16 = new Base16();
            final byte[] result = b16.decode(bArray);

            assertEquals("The result should be empty as the test encoded content did "
                    + "not contain any valid base 16 characters", 0, result.length);
        } catch (final Exception e) {
            fail("Exception was thrown when trying to decode invalid Base16 encoded data");
        }
    }

    @Test
    public void testObjectDecodeWithInvalidParameter() {
        final Base16 b16 = new Base16();

        try {
            b16.decode(Integer.valueOf(5));
            fail("decode(Object) didn't throw an exception when passed an Integer object");
        } catch (final DecoderException e) {
            // ignored
        }

    }

    @Test
    public void testObjectDecodeWithValidParameter() throws Exception {
        final String original = "Hello World!";
        final Object o = Base16.encodeBase16(original.getBytes(CHARSET_UTF8));

        final Base16 b16 = new Base16();
        final Object oDecoded = b16.decode(o);
        final byte[] baDecoded = (byte[]) oDecoded;
        final String dest = new String(baDecoded);

        assertEquals("dest string does not equal original", original, dest);
    }

    @Test
    public void testObjectEncodeWithInvalidParameter() {
        final Base16 b16 = new Base16();
        try {
            b16.encode("Yadayadayada");
            fail("encode(Object) didn't throw an exception when passed a String object");
        } catch (final EncoderException e) {
            // Expected
        }
    }

    @Test
    public void testObjectEncodeWithValidParameter() throws Exception {
        final String original = "Hello World!";
        final Object origObj = original.getBytes(CHARSET_UTF8);

        final Base16 b16 = new Base16();
        final Object oEncoded = b16.encode(origObj);
        final byte[] bArray = Base16.decodeBase16((byte[]) oEncoded);
        final String dest = new String(bArray);

        assertEquals("dest string does not equal original", original, dest);
    }

    @Test
    public void testObjectEncode() {
        final Base16 b16 = new Base16();
        assertEquals("48656c6c6f20576f726c64", new String(b16.encode("Hello World".getBytes(CHARSET_UTF8))));
    }

    @Test
    public void testPairs() {
        assertEquals("0000", new String(Base16.encodeBase16(new byte[] { 0, 0 })));
        for (int i = -128; i <= 127; i++) {
            final byte test[] = { (byte) i, (byte) i };
            assertArrayEquals(test, Base16.decodeBase16(Base16.encodeBase16(test)));
        }
    }

    @Test
    public void testSingletons() {
        assertEquals("00", new String(Base16.encodeBase16(new byte[] { (byte) 0 })));
        assertEquals("01", new String(Base16.encodeBase16(new byte[] { (byte) 1 })));
        assertEquals("02", new String(Base16.encodeBase16(new byte[] { (byte) 2 })));
        assertEquals("03", new String(Base16.encodeBase16(new byte[] { (byte) 3 })));
        assertEquals("04", new String(Base16.encodeBase16(new byte[] { (byte) 4 })));
        assertEquals("05", new String(Base16.encodeBase16(new byte[] { (byte) 5 })));
        assertEquals("06", new String(Base16.encodeBase16(new byte[] { (byte) 6 })));
        assertEquals("07", new String(Base16.encodeBase16(new byte[] { (byte) 7 })));
        assertEquals("08", new String(Base16.encodeBase16(new byte[] { (byte) 8 })));
        assertEquals("09", new String(Base16.encodeBase16(new byte[] { (byte) 9 })));
        assertEquals("0a", new String(Base16.encodeBase16(new byte[] { (byte) 10 })));
        assertEquals("0b", new String(Base16.encodeBase16(new byte[] { (byte) 11 })));
        assertEquals("0c", new String(Base16.encodeBase16(new byte[] { (byte) 12 })));
        assertEquals("0d", new String(Base16.encodeBase16(new byte[] { (byte) 13 })));
        assertEquals("0e", new String(Base16.encodeBase16(new byte[] { (byte) 14 })));
        assertEquals("0f", new String(Base16.encodeBase16(new byte[] { (byte) 15 })));
        assertEquals("10", new String(Base16.encodeBase16(new byte[] { (byte) 16 })));
        assertEquals("11", new String(Base16.encodeBase16(new byte[] { (byte) 17 })));
        assertEquals("12", new String(Base16.encodeBase16(new byte[] { (byte) 18 })));
        assertEquals("13", new String(Base16.encodeBase16(new byte[] { (byte) 19 })));
        assertEquals("14", new String(Base16.encodeBase16(new byte[] { (byte) 20 })));
        assertEquals("15", new String(Base16.encodeBase16(new byte[] { (byte) 21 })));
        assertEquals("16", new String(Base16.encodeBase16(new byte[] { (byte) 22 })));
        assertEquals("17", new String(Base16.encodeBase16(new byte[] { (byte) 23 })));
        assertEquals("18", new String(Base16.encodeBase16(new byte[] { (byte) 24 })));
        assertEquals("19", new String(Base16.encodeBase16(new byte[] { (byte) 25 })));
        assertEquals("1a", new String(Base16.encodeBase16(new byte[] { (byte) 26 })));
        assertEquals("1b", new String(Base16.encodeBase16(new byte[] { (byte) 27 })));
        assertEquals("1c", new String(Base16.encodeBase16(new byte[] { (byte) 28 })));
        assertEquals("1d", new String(Base16.encodeBase16(new byte[] { (byte) 29 })));
        assertEquals("1e", new String(Base16.encodeBase16(new byte[] { (byte) 30 })));
        assertEquals("1f", new String(Base16.encodeBase16(new byte[] { (byte) 31 })));
        assertEquals("20", new String(Base16.encodeBase16(new byte[] { (byte) 32 })));
        assertEquals("21", new String(Base16.encodeBase16(new byte[] { (byte) 33 })));
        assertEquals("22", new String(Base16.encodeBase16(new byte[] { (byte) 34 })));
        assertEquals("23", new String(Base16.encodeBase16(new byte[] { (byte) 35 })));
        assertEquals("24", new String(Base16.encodeBase16(new byte[] { (byte) 36 })));
        assertEquals("25", new String(Base16.encodeBase16(new byte[] { (byte) 37 })));
        assertEquals("26", new String(Base16.encodeBase16(new byte[] { (byte) 38 })));
        assertEquals("27", new String(Base16.encodeBase16(new byte[] { (byte) 39 })));
        assertEquals("28", new String(Base16.encodeBase16(new byte[] { (byte) 40 })));
        assertEquals("29", new String(Base16.encodeBase16(new byte[] { (byte) 41 })));
        assertEquals("2a", new String(Base16.encodeBase16(new byte[] { (byte) 42 })));
        assertEquals("2b", new String(Base16.encodeBase16(new byte[] { (byte) 43 })));
        assertEquals("2c", new String(Base16.encodeBase16(new byte[] { (byte) 44 })));
        assertEquals("2d", new String(Base16.encodeBase16(new byte[] { (byte) 45 })));
        assertEquals("2e", new String(Base16.encodeBase16(new byte[] { (byte) 46 })));
        assertEquals("2f", new String(Base16.encodeBase16(new byte[] { (byte) 47 })));
        assertEquals("30", new String(Base16.encodeBase16(new byte[] { (byte) 48 })));
        assertEquals("31", new String(Base16.encodeBase16(new byte[] { (byte) 49 })));
        assertEquals("32", new String(Base16.encodeBase16(new byte[] { (byte) 50 })));
        assertEquals("33", new String(Base16.encodeBase16(new byte[] { (byte) 51 })));
        assertEquals("34", new String(Base16.encodeBase16(new byte[] { (byte) 52 })));
        assertEquals("35", new String(Base16.encodeBase16(new byte[] { (byte) 53 })));
        assertEquals("36", new String(Base16.encodeBase16(new byte[] { (byte) 54 })));
        assertEquals("37", new String(Base16.encodeBase16(new byte[] { (byte) 55 })));
        assertEquals("38", new String(Base16.encodeBase16(new byte[] { (byte) 56 })));
        assertEquals("39", new String(Base16.encodeBase16(new byte[] { (byte) 57 })));
        assertEquals("3a", new String(Base16.encodeBase16(new byte[] { (byte) 58 })));
        assertEquals("3b", new String(Base16.encodeBase16(new byte[] { (byte) 59 })));
        assertEquals("3c", new String(Base16.encodeBase16(new byte[] { (byte) 60 })));
        assertEquals("3d", new String(Base16.encodeBase16(new byte[] { (byte) 61 })));
        assertEquals("3e", new String(Base16.encodeBase16(new byte[] { (byte) 62 })));
        assertEquals("3f", new String(Base16.encodeBase16(new byte[] { (byte) 63 })));
        assertEquals("40", new String(Base16.encodeBase16(new byte[] { (byte) 64 })));
        assertEquals("41", new String(Base16.encodeBase16(new byte[] { (byte) 65 })));
        assertEquals("42", new String(Base16.encodeBase16(new byte[] { (byte) 66 })));
        assertEquals("43", new String(Base16.encodeBase16(new byte[] { (byte) 67 })));
        assertEquals("44", new String(Base16.encodeBase16(new byte[] { (byte) 68 })));
        assertEquals("45", new String(Base16.encodeBase16(new byte[] { (byte) 69 })));
        assertEquals("46", new String(Base16.encodeBase16(new byte[] { (byte) 70 })));
        assertEquals("47", new String(Base16.encodeBase16(new byte[] { (byte) 71 })));
        assertEquals("48", new String(Base16.encodeBase16(new byte[] { (byte) 72 })));
        assertEquals("49", new String(Base16.encodeBase16(new byte[] { (byte) 73 })));
        assertEquals("4a", new String(Base16.encodeBase16(new byte[] { (byte) 74 })));
        assertEquals("4b", new String(Base16.encodeBase16(new byte[] { (byte) 75 })));
        assertEquals("4c", new String(Base16.encodeBase16(new byte[] { (byte) 76 })));
        assertEquals("4d", new String(Base16.encodeBase16(new byte[] { (byte) 77 })));
        assertEquals("4e", new String(Base16.encodeBase16(new byte[] { (byte) 78 })));
        assertEquals("4f", new String(Base16.encodeBase16(new byte[] { (byte) 79 })));
        assertEquals("50", new String(Base16.encodeBase16(new byte[] { (byte) 80 })));
        assertEquals("51", new String(Base16.encodeBase16(new byte[] { (byte) 81 })));
        assertEquals("52", new String(Base16.encodeBase16(new byte[] { (byte) 82 })));
        assertEquals("53", new String(Base16.encodeBase16(new byte[] { (byte) 83 })));
        assertEquals("54", new String(Base16.encodeBase16(new byte[] { (byte) 84 })));
        assertEquals("55", new String(Base16.encodeBase16(new byte[] { (byte) 85 })));
        assertEquals("56", new String(Base16.encodeBase16(new byte[] { (byte) 86 })));
        assertEquals("57", new String(Base16.encodeBase16(new byte[] { (byte) 87 })));
        assertEquals("58", new String(Base16.encodeBase16(new byte[] { (byte) 88 })));
        assertEquals("59", new String(Base16.encodeBase16(new byte[] { (byte) 89 })));
        assertEquals("5a", new String(Base16.encodeBase16(new byte[] { (byte) 90 })));
        assertEquals("5b", new String(Base16.encodeBase16(new byte[] { (byte) 91 })));
        assertEquals("5c", new String(Base16.encodeBase16(new byte[] { (byte) 92 })));
        assertEquals("5d", new String(Base16.encodeBase16(new byte[] { (byte) 93 })));
        assertEquals("5e", new String(Base16.encodeBase16(new byte[] { (byte) 94 })));
        assertEquals("5f", new String(Base16.encodeBase16(new byte[] { (byte) 95 })));
        assertEquals("60", new String(Base16.encodeBase16(new byte[] { (byte) 96 })));
        assertEquals("61", new String(Base16.encodeBase16(new byte[] { (byte) 97 })));
        assertEquals("62", new String(Base16.encodeBase16(new byte[] { (byte) 98 })));
        assertEquals("63", new String(Base16.encodeBase16(new byte[] { (byte) 99 })));
        assertEquals("64", new String(Base16.encodeBase16(new byte[] { (byte) 100 })));
        assertEquals("65", new String(Base16.encodeBase16(new byte[] { (byte) 101 })));
        assertEquals("66", new String(Base16.encodeBase16(new byte[] { (byte) 102 })));
        assertEquals("67", new String(Base16.encodeBase16(new byte[] { (byte) 103 })));
        assertEquals("68", new String(Base16.encodeBase16(new byte[] { (byte) 104 })));
        for (int i = -128; i <= 127; i++) {
            final byte test[] = { (byte) i };
            assertTrue(Arrays.equals(test, Base16.decodeBase16(Base16.encodeBase16(test))));
        }
    }

    @Test
    public void testTriplets() {
        assertEquals("000000", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 0 })));
        assertEquals("000001", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 1 })));
        assertEquals("000002", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 2 })));
        assertEquals("000003", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 3 })));
        assertEquals("000004", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 4 })));
        assertEquals("000005", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 5 })));
        assertEquals("000006", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 6 })));
        assertEquals("000007", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 7 })));
        assertEquals("000008", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 8 })));
        assertEquals("000009", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 9 })));
        assertEquals("00000a", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 10 })));
        assertEquals("00000b", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 11 })));
        assertEquals("00000c", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 12 })));
        assertEquals("00000d", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 13 })));
        assertEquals("00000e", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 14 })));
        assertEquals("00000f", new String(Base16.encodeBase16(new byte[] { (byte) 0, (byte) 0, (byte) 15 })));
    }

    @Test
    public void testByteToStringVariations() throws DecoderException {
        final Base16 Base16 = new Base16();
        final byte[] b1 = StringUtils.getBytesUtf8("Hello World");
        final byte[] b2 = new byte[0];
        final byte[] b3 = null;

        assertEquals("byteToString Hello World", "48656c6c6f20576f726c64", Base16.encodeToString(b1));
        assertEquals("byteToString static Hello World", "48656c6c6f20576f726c64", StringUtils.newStringUtf8(Base16.encodeBase16(b1)));
        assertEquals("byteToString \"\"", "", Base16.encodeToString(b2));
        assertEquals("byteToString static \"\"", "", StringUtils.newStringUtf8(Base16.encodeBase16(b2)));
        assertEquals("byteToString null", null, Base16.encodeToString(b3));
        assertEquals("byteToString static null", null, StringUtils.newStringUtf8(Base16.encodeBase16(b3)));
    }

    @Test
    public void testStringToByteVariations() throws DecoderException {
        final Base16 Base16 = new Base16();
        final String s1 = "48656c6c6f20576f726c64";
        final String s2 = "";
        final String s3 = null;

        assertEquals("StringToByte Hello World", "Hello World", StringUtils.newStringUtf8(Base16.decode(s1)));
        assertEquals("StringToByte Hello World", "Hello World",
                StringUtils.newStringUtf8((byte[]) Base16.decode((Object) s1)));
        assertEquals("StringToByte static Hello World", "Hello World",
                StringUtils.newStringUtf8(Base16.decodeBase16(s1)));
        assertEquals("StringToByte \"\"", "", StringUtils.newStringUtf8(Base16.decode(s2)));
        assertEquals("StringToByte static \"\"", "", StringUtils.newStringUtf8(Base16.decodeBase16(s2)));
        assertEquals("StringToByte null", null, StringUtils.newStringUtf8(Base16.decode(s3)));
        assertEquals("StringToByte static null", null, StringUtils.newStringUtf8(Base16.decodeBase16(s3)));
    }

    private String toString(final byte[] data) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            buf.append(data[i]);
            if (i != data.length - 1) {
                buf.append(",");
            }
        }
        return buf.toString();
    }

    /**
     * Test for CODEC-265: Encode a 1GiB file.
     *
     * @see <a href="https://issues.apache.org/jira/projects/CODEC/issues/CODEC-265">CODEC-265</a>
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCodec265_over() {
        // 1GiB file to encode: 2^30 bytes
        final int size1GiB = 1 << 30;

        // Expecting a size of 2 output bytes per 1 input byte
        final int blocks = size1GiB;
        final int expectedLength = 2 * blocks;

        // This test is memory hungry. Check we can run it.
        final long presumableFreeMemory = BaseNCodecTest.getPresumableFreeMemory();

        // Estimate the maximum memory required:
        // 1GiB + 1GiB + ~2GiB + ~1.33GiB + 32 KiB  = ~5.33GiB
        //
        // 1GiB: Input buffer to encode
        // 1GiB: Existing working buffer (due to doubling of default buffer size of 8192)
        // ~2GiB: New working buffer to allocate (due to doubling)
        // ~1.33GiB: Expected output size (since the working buffer is copied at the end)
        // 32KiB: Some head room
        final long estimatedMemory = (long) size1GiB * 4 + expectedLength + 32 * 1024;
        Assume.assumeTrue("Not enough free memory for the test", presumableFreeMemory > estimatedMemory);

        final byte[] bytes = new byte[size1GiB];
        final byte[] encoded = Base16.encodeBase16(bytes);
        assertEquals(expectedLength, encoded.length);
    }

    @Test
    public void testIsInAlphabet() {
        // lower-case
        Base16 b16 = new Base16(true, CHARSET_UTF8);
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(b16.isInAlphabet((byte) c));
        }
        for (char c = 'a'; c <= 'f'; c++) {
            assertTrue(b16.isInAlphabet((byte) c));
        }
        for (char c = 'A'; c <= 'F'; c++) {
            assertFalse(b16.isInAlphabet((byte) c));
        }
        assertFalse(b16.isInAlphabet((byte) ('0' - 1)));
        assertFalse(b16.isInAlphabet((byte) ('9' + 1)));
        assertFalse(b16.isInAlphabet((byte) ('a' - 1)));
        assertFalse(b16.isInAlphabet((byte) ('z' + 1)));

        // upper-case
        b16 = new Base16(false, CHARSET_UTF8);
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(b16.isInAlphabet((byte) c));
        }
        for (char c = 'a'; c <= 'f'; c++) {
            assertFalse(b16.isInAlphabet((byte) c));
        }
        for (char c = 'A'; c <= 'F'; c++) {
            assertTrue(b16.isInAlphabet((byte) c));
        }
        assertFalse(b16.isInAlphabet((byte) ('0' - 1)));
        assertFalse(b16.isInAlphabet((byte) ('9' + 1)));
        assertFalse(b16.isInAlphabet((byte) ('A' - 1)));
        assertFalse(b16.isInAlphabet((byte) ('F' + 1)));
    }

    @Test
    public void testDecodeSingleBytes() {
        final String encoded = "556e74696c206e6578742074696d6521";

        final BaseNCodec.Context context = new BaseNCodec.Context();
        final Base16 b16 = new Base16();

        final byte[] encocdedBytes = StringUtils.getBytesUtf8(encoded);

        // decode byte-by-byte
        b16.decode(encocdedBytes, 0, 1, context);
        b16.decode(encocdedBytes, 1, 1, context);    // yields "U"
        b16.decode(encocdedBytes, 2, 1, context);
        b16.decode(encocdedBytes, 3, 1, context);    // yields "n"

        // decode split hex-pairs
        b16.decode(encocdedBytes, 4, 3, context);    // yields "t"
        b16.decode(encocdedBytes, 7, 3, context);    // yields "il"
        b16.decode(encocdedBytes, 10, 3, context);   // yields " "

        // decode remaining
        b16.decode(encocdedBytes, 13, 19, context);  // yields "next time!"

        final byte[] decodedBytes = new byte[context.pos];
        System.arraycopy(context.buffer, context.readPos, decodedBytes, 0, decodedBytes.length);
        final String decoded = StringUtils.newStringUtf8(decodedBytes);

        assertEquals("Until next time!", decoded);
    }
}
