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

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Converts between byte arrays and strings of "0"s and "1"s.
 *
 * <p>This class is immutable and thread-safe.</p>
 *
/**
 * Utility class for performing bitwise operations on boolean arrays and converting byte arrays to boolean arrays.
 */
public class BitVectorUtils {

    /**
     * Performs bitwise AND operation on two boolean arrays.
     *
     * @param a First boolean array.
     * @param b Second boolean array.
     * @return Result of bitwise AND operation.
     * @throws IllegalArgumentException If arrays are not of equal length.
     */
    public static boolean[] and(boolean[] a, boolean[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of equal length");
        }
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] && b[i];
        }
        return result;
    }

    /**
     * Performs bitwise OR operation on two boolean arrays.
     *
     * @param a First boolean array.
     * @param b Second boolean array.
     * @return Result of bitwise OR operation.
     * @throws IllegalArgumentException If arrays are not of equal length.
     */
    public static boolean[] or(boolean[] a, boolean[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of equal length");
        }
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] || b[i];
        }
        return result;
    }

    /**
     * Performs bitwise XOR operation on two boolean arrays.
     *
     * @param a First boolean array.
     * @param b Second boolean array.
     * @return Result of bitwise XOR operation.
     * @throws IllegalArgumentException If arrays are not of equal length.
     */
    public static boolean[] xor(boolean[] a, boolean[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of equal length");
        }
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }
        return result;
    }

    /**
     * Performs bitwise NAND operation on two boolean arrays.
     *
     * @param a First boolean array.
     * @param b Second boolean array.
     * @return Result of bitwise NAND operation.
     * @throws IllegalArgumentException If arrays are not of equal length.
     */
    public static boolean[] nand(boolean[] a, boolean[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of equal length");
        }
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = !(a[i] && b[i]);
        }
        return result;
    }

    /**
     * Converts a byte array into a boolean array.
     *
     * @param bytes Byte array to convert.
     * @return Boolean array where each bit corresponds to a bit in the byte array.
     */
    public static boolean[] bytesToBooleans(byte[] bytes) {
        boolean[] booleans = new boolean[bytes.length * 8]; // 8 bits per byte
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8; j++) {
                booleans[i * 8 + j] = (b & (1 << (7 - j))) != 0;
            }
        }
        return booleans;
    }

    /**
     * Main method to test the functionality.
     */
    public static void main(String[] args) {
        // Example usage
        boolean[] array1 = {true, false, true, false};
        boolean[] array2 = {false, true, false, true};

        // Perform bitwise AND
        boolean[] resultAnd = and(array1, array2);
        System.out.println("AND result: " + Arrays.toString(resultAnd));

        // Perform bitwise OR
        boolean[] resultOr = or(array1, array2);
        System.out.println("OR result: " + Arrays.toString(resultOr));

        // Perform bitwise XOR
        boolean[] resultXor = xor(array1, array2);
        System.out.println("XOR result: " + Arrays.toString(resultXor));

        // Perform bitwise NAND
        boolean[] resultNand = nand(array1, array2);
        System.out.println("NAND result: " + Arrays.toString(resultNand));

        // Convert byte array to boolean array
        byte[] byteArray = {0x0F, (byte) 0xF0}; // Example byte array
        boolean[] boolArray = bytesToBooleans(byteArray);
        System.out.println("Byte to Boolean: " + Arrays.toString(boolArray));
    }
}

 *
 * @since 1.3
 */
public class BinaryCodec implements BinaryDecoder, BinaryEncoder {
    /*
     * tried to avoid using ArrayUtils to minimize dependencies while using these empty arrays - dep is just not worth
     * it.
     */
    /** Empty char array. */
    private static final char[] EMPTY_CHAR_ARRAY = {};

    /** Empty byte array. */
    private static final byte[] EMPTY_BYTE_ARRAY = {};

    /** Mask for bit 0 of a byte. */
    private static final int BIT_0 = 1;

    /** Mask for bit 1 of a byte. */
    private static final int BIT_1 = 0x02;

    /** Mask for bit 2 of a byte. */
    private static final int BIT_2 = 0x04;

    /** Mask for bit 3 of a byte. */
    private static final int BIT_3 = 0x08;

    /** Mask for bit 4 of a byte. */
    private static final int BIT_4 = 0x10;

    /** Mask for bit 5 of a byte. */
    private static final int BIT_5 = 0x20;

    /** Mask for bit 6 of a byte. */
    private static final int BIT_6 = 0x40;

    /** Mask for bit 7 of a byte. */
    private static final int BIT_7 = 0x80;

    private static final int[] BITS = {BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5, BIT_6, BIT_7};

    /**
     * Decodes a byte array where each byte represents an ASCII '0' or '1'.
     *
     * @param ascii
     *                  each byte represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     */
    public static byte[] fromAscii(final byte[] ascii) {
        if (isEmpty(ascii)) {
            return EMPTY_BYTE_ARRAY;
        }
        final int asciiLength = ascii.length;
        // get length/8 times bytes with 3 bit shifts to the right of the length
        final byte[] raw = new byte[asciiLength >> 3];
        /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
        for (int ii = 0, jj = asciiLength - 1; ii < raw.length; ii++, jj -= 8) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if (ascii[jj - bits] == '1') {
                    raw[ii] |= BITS[bits];
                }
            }
        }
        return raw;
    }

    /**
     * Decodes a char array where each char represents an ASCII '0' or '1'.
     *
     * @param ascii
     *                  each char represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a char in the char array argument
     */
    public static byte[] fromAscii(final char[] ascii) {
        if (ascii == null || ascii.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        final int asciiLength = ascii.length;
        // get length/8 times bytes with 3 bit shifts to the right of the length
        final byte[] raw = new byte[asciiLength >> 3];
        /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
        for (int ii = 0, jj = asciiLength - 1; ii < raw.length; ii++, jj -= 8) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if (ascii[jj - bits] == '1') {
                    raw[ii] |= BITS[bits];
                }
            }
        }
        return raw;
    }

    /**
     * Returns {@code true} if the given array is {@code null} or empty (size 0.)
     *
     * @param array
     *            the source array
     * @return {@code true} if the given array is {@code null} or empty (size 0.)
     */
    static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 character bytes - each byte is a truncated
     * char.
     *
     * @param raw
     *                  the raw binary data to convert
     * @return an array of 0 and 1 character bytes for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static byte[] toAsciiBytes(final byte[] raw) {
        if (isEmpty(raw)) {
            return EMPTY_BYTE_ARRAY;
        }
        final int rawLength = raw.length;
        // get 8 times the bytes with 3 bit shifts to the left of the length
        final byte[] l_ascii = new byte[rawLength << 3];
        /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
        for (int ii = 0, jj = l_ascii.length - 1; ii < rawLength; ii++, jj -= 8) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if ((raw[ii] & BITS[bits]) == 0) {
                    l_ascii[jj - bits] = '0';
                } else {
                    l_ascii[jj - bits] = '1';
                }
            }
        }
        return l_ascii;
    }

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 characters.
     *
     * @param raw
     *                  the raw binary data to convert
     * @return an array of 0 and 1 characters for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static char[] toAsciiChars(final byte[] raw) {
        if (isEmpty(raw)) {
            return EMPTY_CHAR_ARRAY;
        }
        final int rawLength = raw.length;
        // get 8 times the bytes with 3 bit shifts to the left of the length
        final char[] l_ascii = new char[rawLength << 3];
        /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
        for (int ii = 0, jj = l_ascii.length - 1; ii < rawLength; ii++, jj -= 8) {
            for (int bits = 0; bits < BITS.length; ++bits) {
                if ((raw[ii] & BITS[bits]) == 0) {
                    l_ascii[jj - bits] = '0';
                } else {
                    l_ascii[jj - bits] = '1';
                }
            }
        }
        return l_ascii;
    }

    /**
     * Converts an array of raw binary data into a String of ASCII 0 and 1 characters.
     *
     * @param raw
     *                  the raw binary data to convert
     * @return a String of 0 and 1 characters representing the binary data
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static String toAsciiString(final byte[] raw) {
        return new String(toAsciiChars(raw));
    }

    /**
     * Decodes a byte array where each byte represents an ASCII '0' or '1'.
     *
     * @param ascii
     *                  each byte represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     * @see org.apache.commons.codec.Decoder#decode(Object)
     */
    @Override
    public byte[] decode(final byte[] ascii) {
        return fromAscii(ascii);
    }

    /**
     * Decodes a byte array where each byte represents an ASCII '0' or '1'.
     *
     * @param ascii
     *                  each byte represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     * @throws DecoderException
     *                  if argument is not a byte[], char[] or String
     * @see org.apache.commons.codec.Decoder#decode(Object)
     */
    @Override
    public Object decode(final Object ascii) throws DecoderException {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        if (ascii instanceof byte[]) {
            return fromAscii((byte[]) ascii);
        }
        if (ascii instanceof char[]) {
            return fromAscii((char[]) ascii);
        }
        if (ascii instanceof String) {
            return fromAscii(((String) ascii).toCharArray());
        }
        throw new DecoderException("argument not a byte array");
    }

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 characters.
     *
     * @param raw
     *                  the raw binary data to convert
     * @return 0 and 1 ASCII character bytes one for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    @Override
    public byte[] encode(final byte[] raw) {
        return toAsciiBytes(raw);
    }

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 chars.
     *
     * @param raw
     *                  the raw binary data to convert
     * @return 0 and 1 ASCII character chars one for each bit of the argument
     * @throws EncoderException
     *                  if the argument is not a byte[]
     * @see org.apache.commons.codec.Encoder#encode(Object)
     */
    @Override
    public Object encode(final Object raw) throws EncoderException {
        if (!(raw instanceof byte[])) {
            throw new EncoderException("argument not a byte array");
        }
        return toAsciiChars((byte[]) raw);
    }

    /**
     * Decodes a String where each char of the String represents an ASCII '0' or '1'.
     *
     * @param ascii
     *                  String of '0' and '1' characters
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     * @see org.apache.commons.codec.Decoder#decode(Object)
     */
    public byte[] toByteArray(final String ascii) {
        if (ascii == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return fromAscii(ascii.toCharArray());
    }
}
