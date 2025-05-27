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

import java.util.Objects;

import org.apache.commons.codec.CodecPolicy;

/**
 * Provides Base16 encoding and decoding as defined by <a href="https://tools.ietf.org/html/rfc4648#section-8">RFC 4648 - 8. Base 16 Encoding</a>.
 *
 * <p>
 * This class is thread-safe.
 * </p>
 * <p>
 * This implementation strictly follows RFC 4648, and as such unlike the {@link Base32} and {@link Base64} implementations, it does not ignore invalid alphabet
 * characters or whitespace, neither does it offer chunking or padding characters.
 * </p>
 * <p>
 * The only additional feature above those specified in RFC 4648 is support for working with a lower-case alphabet in addition to the default upper-case
 * alphabet.
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/rfc4648#section-8">RFC 4648 - 8. Base 16 Encoding</a>
 * @since 1.15
 */
public class Base16 extends BaseNCodec {

    /**
     * BASE16 characters are 4 bits in length. They are formed by taking an 8-bit group, which is converted into two BASE16 characters.
     */
    private static final int BITS_PER_ENCODED_BYTE = 4;
    private static final int BYTES_PER_ENCODED_BLOCK = 2;
    private static final int BYTES_PER_UNENCODED_BLOCK = 1;

    /**
     * This array is a lookup table that translates Unicode characters drawn from the "Base16 Alphabet" (as specified in Table 5 of RFC 4648) into their 4-bit
     * positive integer equivalents. Characters that are not in the Base16 alphabet but fall within the bounds of the array are translated to -1.
     */
    // @formatter:off
    private static final byte[] UPPER_CASE_DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
             0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, 10, 11, 12, 13, 14, 15                                      // 40-46 A-F
    };
    // @formatter:on

    /**
     * This array is a lookup table that translates 4-bit positive integer index values into their "Base16 Alphabet" equivalents as specified in Table 5 of RFC
     * 4648.
     */
    private static final byte[] UPPER_CASE_ENCODE_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * This array is a lookup table that translates Unicode characters drawn from the a lower-case "Base16 Alphabet" into their 4-bit positive integer
     * equivalents. Characters that are not in the Base16 alphabet but fall within the bounds of the array are translated to -1.
     */
    // @formatter:off
    private static final byte[] LOWER_CASE_DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
             0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 40-4f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 50-5f
            -1, 10, 11, 12, 13, 14, 15                                      // 60-66 a-f
    };
    // @formatter:on

    /**
     * This array is a lookup table that translates 4-bit positive integer index values into their "Base16 Alphabet" lower-case equivalents.
     */
    private static final byte[] LOWER_CASE_ENCODE_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /** Mask used to extract 4 bits, used when decoding character. */
    private static final int MASK_4_BITS = 0x0f;

    /**
     * Decode table to use.
     */
    private final byte[] decodeTable;

    /**
     * Encode table to use.
     */
    private final byte[] encodeTable;

    /**
     * Constructs a Base16 codec used for decoding and encoding.
     */
    public Base16() {
        this(false);
    }

    /**
     * Constructs a Base16 codec used for decoding and encoding.
     *
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet.
     */
    public Base16(final boolean lowerCase) {
        this(lowerCase, DECODING_POLICY_DEFAULT);
    }

    /**
     * Constructs a Base16 codec used for decoding and encoding.
     *
     * @param lowerCase      if {@code true} then use a lower-case Base16 alphabet.
     * @param encodeTable    the encode table.
     * @param decodingPolicy Decoding policy.
     */
    private Base16(final boolean lowerCase, final byte[] encodeTable, final CodecPolicy decodingPolicy) {
        super(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, 0, 0, PAD_DEFAULT, decodingPolicy);
        Objects.requireNonNull(encodeTable, "encodeTable");
        this.encodeTable = encodeTable;
        this.decodeTable = encodeTable == LOWER_CASE_ENCODE_TABLE ? LOWER_CASE_DECODE_TABLE : UPPER_CASE_DECODE_TABLE;
    }

    /**
     * Constructs a Base16 codec used for decoding and encoding.
     *
     * @param lowerCase      if {@code true} then use a lower-case Base16 alphabet.
     * @param decodingPolicy Decoding policy.
     */
    public Base16(final boolean lowerCase, final CodecPolicy decodingPolicy) {
        this(lowerCase, lowerCase ? LOWER_CASE_ENCODE_TABLE : UPPER_CASE_ENCODE_TABLE, decodingPolicy);
    }

    @Override
    void decode(final byte[] data, int offset, final int length, final Context context) {
        if (context.eof || length < 0) {
            context.eof = true;
            if (context.ibitWorkArea != 0) {
                validateTrailingCharacter();
            }
            return;
        }
        final int dataLen = Math.min(data.length - offset, length);
        final int availableChars = (context.ibitWorkArea != 0 ? 1 : 0) + dataLen;
        // small optimization to short-cut the rest of this method when it is fed byte-by-byte
        if (availableChars == 1 && availableChars == dataLen) {
            // store 1/2 byte for next invocation of decode, we offset by +1 as empty-value is 0
            context.ibitWorkArea = decodeOctet(data[offset]) + 1;
            return;
        }
        // we must have an even number of chars to decode
        final int charsToProcess = availableChars % BYTES_PER_ENCODED_BLOCK == 0 ? availableChars : availableChars - 1;
        final int end = offset + dataLen;
        final byte[] buffer = ensureBufferSize(charsToProcess / BYTES_PER_ENCODED_BLOCK, context);
        int result;
        if (dataLen < availableChars) {
            // we have 1/2 byte from previous invocation to decode
            result = context.ibitWorkArea - 1 << BITS_PER_ENCODED_BYTE;
            result |= decodeOctet(data[offset++]);
            buffer[context.pos++] = (byte) result;
            // reset to empty-value for next invocation!
            context.ibitWorkArea = 0;
        }
        final int loopEnd = end - 1;
        while (offset < loopEnd) {
            result = decodeOctet(data[offset++]) << BITS_PER_ENCODED_BYTE;
            result |= decodeOctet(data[offset++]);
            buffer[context.pos++] = (byte) result;
        }
        // we have one char of a hex-pair left over
        if (offset < end) {
            // store 1/2 byte for next invocation of decode, we offset by +1 as empty-value is 0
            context.ibitWorkArea = decodeOctet(data[offset]) + 1;
        }
    }

    private int decodeOctet(final byte octet) {
        int decoded = -1;
        if ((octet & 0xff) < decodeTable.length) {
            decoded = decodeTable[octet];
        }
        if (decoded == -1) {
            throw new IllegalArgumentException("Invalid octet in encoded value: " + (int) octet);
        }
        return decoded;
    }

    @Override
    void encode(final byte[] data, final int offset, final int length, final Context context) {
        if (context.eof) {
            return;
        }
        if (length < 0) {
            context.eof = true;
            return;
        }
        final int size = length * BYTES_PER_ENCODED_BLOCK;
        if (size < 0) {
            throw new IllegalArgumentException("Input length exceeds maximum size for encoded data: " + length);
        }
        final byte[] buffer = ensureBufferSize(size, context);
        final int end = offset + length;
        for (int i = offset; i < end; i++) {
            final int value = data[i];
            final int high = value >> BITS_PER_ENCODED_BYTE & MASK_4_BITS;
            final int low = value & MASK_4_BITS;
            buffer[context.pos++] = encodeTable[high];
            buffer[context.pos++] = encodeTable[low];
        }
    }

    /**
     * Returns whether or not the {@code octet} is in the Base16 alphabet.
     *
     * @param octet The value to test.
     * @return {@code true} if the value is defined in the Base16 alphabet {@code false} otherwise.
     */
    @Override
    public boolean isInAlphabet(final byte octet) {
        return (octet & 0xff) < decodeTable.length && decodeTable[octet] != -1;
    }

    /**
     * Validates whether decoding allows an entire final trailing character that cannot be used for a complete byte.
     *
     * @throws IllegalArgumentException if strict decoding is enabled
     */
    private void validateTrailingCharacter() {
        if (isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character is a valid base 16 alphabet character but not a possible encoding. " +
                    "Decoding requires at least two characters to create one byte.");
        }
    }
}
