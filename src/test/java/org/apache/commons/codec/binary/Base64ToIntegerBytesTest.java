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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class Base64ToIntegerBytesTest {

    private static final byte[] ZERO_BYTE_ARRAY = new byte[] { 0 };

    @Test
    void testBigIntegerWithAllZeros() {
        final BigInteger allZeros = new BigInteger("0");
        final byte[] result = Base64.toIntegerBytes(allZeros);
        assertArrayEquals(ZERO_BYTE_ARRAY, result);
    }

    @Test
    void testBigIntegerWithBoundaryValues() {
        // Test boundary values that might cause overflow or underflow issues
        final BigInteger[] boundaries = { BigInteger.valueOf(Integer.MAX_VALUE), BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE),
                BigInteger.valueOf(Long.MIN_VALUE) };
        for (final BigInteger boundary : boundaries) {
            assertNotNull(Base64.toIntegerBytes(boundary), "Should handle boundary value: " + boundary);
        }
    }

    @Test
    void testBigIntegerWithByteBoundaryValues() {
        // Test specific byte boundary values
        final BigInteger[] boundaryValues = { BigInteger.valueOf(Byte.MAX_VALUE), // 127
                BigInteger.valueOf(Byte.MIN_VALUE), // -128
                BigInteger.valueOf(255), // 255 (unsigned byte max)
                BigInteger.valueOf(-256) // -256
        };
        for (final BigInteger boundary : boundaryValues) {
            assertNotNull(Base64.toIntegerBytes(boundary), "Should handle boundary value: " + boundary);
        }
    }

    @Test
    void testBigIntegerWithDecimalString() {
        final BigInteger decimal = new BigInteger("100");
        final byte[] result = Base64.toIntegerBytes(decimal);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithHexValues() {
        // Test with hex string representation
        final BigInteger hexValue = new BigInteger("FF", 16); // 255
        final byte[] result = Base64.toIntegerBytes(hexValue);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(-1, result[0]); // -1 in two's complement
    }

    @Test
    void testBigIntegerWithLargeValues() {
        // Test very large numbers that exceed typical integer ranges
        final BigInteger huge = new BigInteger("9223372036854775807"); // Long.MAX_VALUE
        final byte[] result = Base64.toIntegerBytes(huge);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithLeadingZeros() {
        // Create a BigInteger that would have leading zeros in byte representation
        final BigInteger big = new BigInteger("00000000000000000000000000000001", 16);
        final byte[] result = Base64.toIntegerBytes(big);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(1, result[0]);
    }

    @Test
    void testBigIntegerWithMaxValue() {
        final BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
        final byte[] result = Base64.toIntegerBytes(max);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithMinValue() {
        final BigInteger min = BigInteger.valueOf(Long.MIN_VALUE);
        final byte[] result = Base64.toIntegerBytes(min);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithMixedSigns() {
        // Test that the method correctly handles negative numbers
        final BigInteger[] negativeValues = { BigInteger.valueOf(-1), BigInteger.valueOf(-256), BigInteger.valueOf(Integer.MIN_VALUE),
                new BigInteger("-999999999999999999") };
        for (final BigInteger negative : negativeValues) {
            assertNotNull(Base64.toIntegerBytes(negative), "Should handle negative value: " + negative);
        }
    }

    @Test
    void testBigIntegerWithMultipleBytes() {
        // Test a number that requires multiple bytes to represent
        final BigInteger multiByte = new BigInteger("65536"); // 2^16
        final byte[] result = Base64.toIntegerBytes(multiByte);
        assertNotNull(result);
        assertTrue(result.length >= 2);
        // Should correctly represent the multi-byte value
    }

    @Test
    void testBigIntegerWithNegativeHexValues() {
        // Test with negative hex values
        final BigInteger hexNeg = new BigInteger("80", 16); // -128 in two's complement
        final byte[] result = Base64.toIntegerBytes(hexNeg);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(-128, result[0]);
    }

    @Test
    void testBigIntegerWithNegativeLargeValues() {
        final BigInteger hugeNeg = new BigInteger("-9223372036854775808"); // Long.MIN_VALUE
        final byte[] result = Base64.toIntegerBytes(hugeNeg);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithNegativeVeryLargeNumber() {
        final String largeString = "-1234567890123456789012345678901234567890";
        final BigInteger large = new BigInteger(largeString);
        final byte[] result = Base64.toIntegerBytes(large);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithOverflowProtection() {
        // Test that the method handles overflow conditions gracefully
        final BigInteger overflowTest = new BigInteger("12345678901234567890123456789012345678901234567890");
        final byte[] result = Base64.toIntegerBytes(overflowTest);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithSingleByteRange() {
        // Test boundary values for single byte range
        final BigInteger[] testValues = { BigInteger.valueOf(-128), // -128 (minimum byte value)
                BigInteger.valueOf(-127), // -127
                BigInteger.valueOf(-1), // -1
                BigInteger.valueOf(0), // 0
                BigInteger.valueOf(1), // 1
                BigInteger.valueOf(126), // 126
                BigInteger.valueOf(127) // 127 (maximum byte value)
        };
        for (final BigInteger value : testValues) {
            final byte[] result = Base64.toIntegerBytes(value);
            assertNotNull(result, "Should not return null for value: " + value);
        }
    }

    @Test
    void testBigIntegerWithVeryLargeNumber() {
        // Test with numbers that have many digits
        final String largeString = "1234567890123456789012345678901234567890";
        final BigInteger large = new BigInteger(largeString);
        final byte[] result = Base64.toIntegerBytes(large);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithVeryLargePositiveValue() {
        final BigInteger veryLarge = new BigInteger("123456789012345678901234567890");
        final byte[] result = Base64.toIntegerBytes(veryLarge);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithVerySmallNegativeValue() {
        final BigInteger verySmall = new BigInteger("-256");
        final byte[] result = Base64.toIntegerBytes(verySmall);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBigIntegerWithZeroInDifferentFormats() {
        // Different ways to represent zero
        final BigInteger zero1 = BigInteger.ZERO;
        final BigInteger zero2 = new BigInteger("0");
        final BigInteger zero3 = new BigInteger("0000000000000000");
        final byte[] result1 = Base64.toIntegerBytes(zero1);
        final byte[] result2 = Base64.toIntegerBytes(zero2);
        final byte[] result3 = Base64.toIntegerBytes(zero3);
        assertArrayEquals(ZERO_BYTE_ARRAY, result1);
        assertArrayEquals(ZERO_BYTE_ARRAY, result2);
        assertArrayEquals(ZERO_BYTE_ARRAY, result3);
    }


    @Test
    void testLargeNegativeBigInteger() {
        final BigInteger large = new BigInteger("-2147483648"); // Integer.MIN_VALUE
        final byte[] result = Base64.toIntegerBytes(large);
        assertNotNull(result);
        // Should handle large negative numbers correctly
        assertTrue(result.length > 0);
    }

    @Test
    void testLargePositiveBigInteger() {
        final BigInteger large = new BigInteger("2147483647"); // Integer.MAX_VALUE
        final byte[] result = Base64.toIntegerBytes(large);
        assertNotNull(result);
        // Should handle large positive numbers correctly
        assertTrue(result.length > 0);
    }

    @Test
    void testNullBigInteger() {
        assertThrows(NullPointerException.class, () -> {
            Base64.toIntegerBytes(null);
        });
    }

    @Test
    void testOneBigInteger() {
        final BigInteger one = BigInteger.ONE;
        final byte[] result = Base64.toIntegerBytes(one);
        assertNotNull(result);
        // Should represent the integer value 1
        assertEquals(1, result.length);
        assertEquals(1, result[0]);
    }

    @Test
    void testSmallNegativeBigInteger() {
        final BigInteger small = new BigInteger("-128");
        final byte[] result = Base64.toIntegerBytes(small);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(-128, result[0]);
    }

    @Test
    void testSmallPositiveBigInteger() {
        final BigInteger small = new BigInteger("127");
        final byte[] result = Base64.toIntegerBytes(small);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(127, result[0]);
    }

}
