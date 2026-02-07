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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides Base58 encoding and decoding as commonly used in cryptocurrency and blockchain applications.
 * <p>
 * Base58 is a binary-to-text encoding scheme that uses a 58-character alphabet to encode data. It avoids
 * characters that can be confused (0/O, I/l, +/) and is commonly used in Bitcoin and other blockchain systems.
 * </p>
 * <p>
 * This implementation accumulates data internally until EOF is signaled, at which point the entire input is
 * converted using BigInteger arithmetic. This is necessary because Base58 encoding/decoding requires access
 * to the complete data to properly handle leading zeros.
 * </p>
 * <p>
 * This class is thread-safe for read operations but the Context object used during encoding/decoding should
 * not be shared between threads.
 * </p>
 * <p>
 * The Base58 alphabet is: 123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz
 * (excludes: 0, I, O, l)
 * </p>
 *
 * @see Base58InputStream
 * @see Base58OutputStream
 * @since 1.22.0
 */
public class Base58 extends BaseNCodec {

    /**
     * Base58 alphabet: 123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz
     * (excludes: 0, I, O, l)
     */
    private static final byte[] ENCODE_TABLE = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    /**
     * This array is a lookup table that translates Unicode characters drawn from the "Base58 Alphabet"
     * into their numeric equivalents (0-57). Characters that are not in the Base58 alphabet are marked
     * with -1.
     */
    private static final byte[] DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, -1, -1, -1, -1, -1, -1, // 30-3f '1'-'9' -> 0-8
            -1, 9, 10, 11, 12, 13, 14, 15, 16, -1, 17, 18, 19, 20, 21, -1, // 40-4f 'A'-'N', 'P'-'Z' (skip 'I' and 'O')
            22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,                     // 50-5a 'P'-'Z'
            -1, -1, -1, -1, -1, // 5b-5f
            -1, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, -1, 44, 45, 46, // 60-6f 'a'-'k', 'm'-'o' (skip 'l')
            47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,                     // 70-7a 'p'-'z'
    };
    private final transient Map<Context, byte[]> accumulated = new WeakHashMap<>();

    /**
     * Constructs a Base58 codec used for encoding and decoding.
     */
    public Base58() {
        this(new Builder());
    }

    /**
     * Constructs a Base58 codec used for encoding and decoding with custom configuration.
     *
     * @param builder the builder with custom configuration
     */
    public Base58(final Builder builder) {
        super(builder);
    }

    /**
     * Decodes the given Base58 encoded data.
     * <p>
     * This implementation accumulates data internally. When length &lt; 0 (EOF), the accumulated
     * data is converted from Base58 to binary.
     * </p>
     *
     * @param array   the byte array containing Base58 encoded data
     * @param offset  the offset in the array to start from
     * @param length  the number of bytes to decode, or negative to signal EOF
     * @param context the context for this decoding operation
     */
    @Override
    void decode(byte[] array, int offset, int length, Context context) {
        if (context.eof) {
            return;
        }

        if (length < 0) {
            context.eof = true;
            final byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
            if (accumulate.length > 0) {
                convertFromBase58(accumulate, context);
            }
            accumulated.remove(context);
            return;
        }

        final byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
        final byte[] newAccumulated = new byte[accumulate.length + length];
        if (accumulate.length > 0) {
            System.arraycopy(accumulate, 0, newAccumulated, 0, accumulate.length);
        }
        System.arraycopy(array, offset, newAccumulated, accumulate.length, length);
        accumulated.put(context, newAccumulated);
    }

    /**
     * Encodes the given binary data as Base58.
     * <p>
     * This implementation accumulates data internally. When length &lt; 0 (EOF), the accumulated
     * data is converted to Base58.
     * </p>
     *
     * @param array   the byte array containing binary data to encode
     * @param offset  the offset in the array to start from
     * @param length  the number of bytes to encode, or negative to signal EOF
     * @param context the context for this encoding operation
     */
    @Override
    void encode(byte[] array, int offset, int length, Context context) {
        if (context.eof) {
            return;
        }

        if (length < 0) {
            context.eof = true;
            final byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
            convertToBase58(accumulate, context);
            accumulated.remove(context);
            return;
        }

        final byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
        final byte[] newAccumulated = new byte[accumulate.length + length];
        if (accumulate.length > 0) {
            System.arraycopy(accumulate, 0, newAccumulated, 0, accumulate.length);
        }
        System.arraycopy(array, offset, newAccumulated, accumulate.length, length);
        accumulated.put(context, newAccumulated);
    }

    /**
     * Converts accumulated binary data to Base58 encoding.
     * <p>
     * Uses BigInteger arithmetic to convert the binary data to Base58. Leading zeros in the
     * binary data are represented as '1' characters in the Base58 encoding.
     * </p>
     *
     * @param accumulate the binary data to encode
     * @param context    the context for this encoding operation
     * @return the buffer containing the encoded data
     */
    private byte[] convertToBase58(byte[] accumulate, Context context) {
        final StringBuilder base58 = getStringBuilder(accumulate);
        final String encoded = base58.reverse().toString();

        final byte[] encodedBytes = encoded.getBytes(StandardCharsets.UTF_8);
        final byte[] buffer = ensureBufferSize(encodedBytes.length, context);
        System.arraycopy(encodedBytes, 0, buffer, context.pos, encodedBytes.length);
        context.pos += encodedBytes.length;
        return buffer;
    }

    /**
     * Builds the Base58 string representation of the given binary data.
     * <p>
     * Converts binary data to a BigInteger and divides by 58 repeatedly to get the Base58 digits.
     * Handles leading zeros by counting them and appending '1' for each leading zero byte.
     * </p>
     *
     * @param accumulate the binary data to convert
     * @return a StringBuilder with the Base58 representation (not yet reversed)
     */
    private StringBuilder getStringBuilder(byte[] accumulate) {
        BigInteger value = new BigInteger(1, accumulate);
        int leadingZeros = 0;

        for (byte b : accumulate) {
            if (b == 0) {
                leadingZeros++;
            } else {
                break;
            }
        }

        final StringBuilder base58 = new StringBuilder();
        while (value.signum() > 0) {
            final BigInteger[] divRem = value.divideAndRemainder(BigInteger.valueOf(58));
            base58.append((char) ENCODE_TABLE[divRem[1].intValue()]);
            value = divRem[0];
        }

        for (int i = 0; i < leadingZeros; i++) {
            base58.append('1');
        }
        return base58;
    }

    /**
     * Converts Base58 encoded data to binary.
     * <p>
     * Uses BigInteger arithmetic to convert the Base58 string to binary data. Leading '1' characters
     * in the Base58 encoding represent leading zero bytes in the binary data.
     * </p>
     *
     * @param base58Data the Base58 encoded data
     * @param context    the context for this decoding operation
     * @throws IllegalArgumentException if the Base58 data contains invalid characters
     */
    private void convertFromBase58(byte[] base58Data, Context context) {
        BigInteger value = BigInteger.ZERO;
        int leadingOnes = 0;

        for (byte b : base58Data) {
            if (b == '1') {
                leadingOnes++;
            } else {
                break;
            }
        }

        final BigInteger base = BigInteger.valueOf(58);
        BigInteger power = BigInteger.ONE;

        for (int i = base58Data.length - 1; i >= leadingOnes; i--) {
            final byte b = base58Data[i];
            final int digit = b < DECODE_TABLE.length ? DECODE_TABLE[b] : -1;

            if (digit < 0) {
                throw new IllegalArgumentException("Invalid character in Base58 string: " + (char) b);
            }

            value = value.add(BigInteger.valueOf(digit).multiply(power));
            power = power.multiply(base);
        }

        byte[] decoded = value.toByteArray();

        if (decoded.length > 1 && decoded[0] == 0) {
            final byte[] tmp = new byte[decoded.length - 1];
            System.arraycopy(decoded, 1, tmp, 0, tmp.length);
            decoded = tmp;
        }

        final byte[] result = new byte[leadingOnes + decoded.length];
        System.arraycopy(decoded, 0, result, leadingOnes, decoded.length);

        final byte[] buffer = ensureBufferSize(result.length, context);
        System.arraycopy(result, 0, buffer, context.pos, result.length);
        context.pos += result.length;
    }

    /**
     * Returns whether or not the {@code octet} is in the Base58 alphabet.
     *
     * @param value The value to test.
     * @return {@code true} if the value is defined in the Base58 alphabet {@code false} otherwise.
     */
    @Override
    protected boolean isInAlphabet(byte value) {
        return value >= 0 && value < DECODE_TABLE.length && DECODE_TABLE[value] != -1;
    }

    /**
     * Builds {@link Base58} instances with custom configuration.
     */
    public static class Builder extends AbstractBuilder<Base58, Base58.Builder> {

        /**
         * Constructs a new Base58 builder.
         */
        public Builder() {
            super(ENCODE_TABLE);
            setDecodeTable(DECODE_TABLE);
        }

        /**
         * Builds a new Base58 instance with the configured settings.

         * @return a new Base58 codec
         */
        @Override
        public Base58 get() {
            return new Base58(this);
        }

        /**
         * Creates a new Base58 codec instance.
         *
         * @return a new Base58 codec
         */
        @Override
        public Base58.Builder setEncodeTable(final byte... encodeTable) {
            super.setDecodeTableRaw(DECODE_TABLE);
            return super.setEncodeTable(encodeTable);
        }
    }

}
