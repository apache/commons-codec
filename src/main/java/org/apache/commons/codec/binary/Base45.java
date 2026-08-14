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

import java.util.Arrays;

import org.apache.commons.codec.CodecPolicy;

/**
 * Provides Base45 encoding and decoding as defined by <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285</a>.
 * <p>
 * Base45 is designed for efficient encoding of binary data in environments where a subset of ASCII characters is available, specifically 45 characters chosen
 * from the QR code alphanumeric mode character set. Base45 is used in European Union Digital COVID Certificates (EUDCC) and similar applications.
 * </p>
 * <p>
 * The Base45 alphabet consists of 45 characters:
 * </p>
 *
 * <pre>
 * Value Encoding  Value Encoding  Value Encoding  Value Encoding
 *    0 0           12 C           24 O           36 Space
 *    1 1           13 D           25 P           37 $
 *    2 2           14 E           26 Q           38 %
 *    3 3           15 F           27 R           39 *
 *    4 4           16 G           28 S           40 +
 *    5 5           17 H           29 T           41 -
 *    6 6           18 I           30 U           42 .
 *    7 7           19 J           31 V           43 /
 *    8 8           20 K           32 W           44 :
 *    9 9           21 L           33 X
 *   10 A           22 M           34 Y
 *   11 B           23 N           35 Z
 * </pre>
 *
 * <h2>Encoding</h2>
 * <p>
 * Input bytes are grouped in pairs (2 bytes). Each pair is encoded as 3 Base45 characters. A single remaining byte is encoded as 2 Base45 characters. There is
 * no padding.
 * </p>
 * <ul>
 * <li>For each 2-byte pair {@code (b0, b1)}: {@code n = b0 * 256 + b1}; output 3 characters {@code alphabet[n % 45]}, {@code alphabet[(n / 45) % 45]},
 * {@code alphabet[n / 2025]}</li>
 * <li>For a final single byte {@code b0}: {@code n = b0}; output 2 characters {@code alphabet[n % 45]}, {@code alphabet[n / 45]}</li>
 * </ul>
 * <h2>Decoding</h2>
 * <p>
 * Input characters are grouped in triples (3 characters). Each triple decodes to 2 bytes. A pair of trailing characters decodes to 1 byte. An input whose
 * length modulo 3 equals 1 is invalid.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 * <p>
 * To create an instance, use the default constructor or the builder:
 * </p>
 *
 * <pre>
 * Base45 codec = new Base45();
 *
 * // Or, use the builder to customize the encode table:
 * Base45 custom = Base45.builder().setEncodeTable(...).get();
 * </pre>
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285 – The Base45 Data Encoding</a>
 * @since 1.23.0
 */
public class Base45 extends BaseNCodec {

    /**
     * Builds {@link Base45} instances.
     * <p>
     * To configure a new instance, use a {@link Builder}. For example:
     * </p>
     *
     * <pre>
     *
     * Base45 base45 = Base45.builder().get();
     * </pre>
     *
     * @since 1.23.0
     */
    public static class Builder extends AbstractBuilder<Base45, Builder> {

        /**
         * Constructs a new instance using the Base45 alphabet as defined by RFC 9285.
         */
        public Builder() {
            super(ENCODE_TABLE);
            setDecodingPolicy(CodecPolicy.STRICT);
            setDecodeTableRaw(DECODE_TABLE);
            setEncodeTableRaw(ENCODE_TABLE);
            setEncodedBlockSize(BYTES_PER_ENCODED_BLOCK);
            setUnencodedBlockSize(BYTES_PER_UNENCODED_BLOCK);
        }

        @Override
        public Base45 get() {
            return new Base45(this);
        }

        /**
         * Sets the decoding policy. {@link CodecPolicy#STRICT} is the only supported policy.
         *
         * @param decodingPolicy The decoding policy; {@code null} resets to the default ({@link CodecPolicy#STRICT}).
         * @return {@code this} instance.
         * @throws IllegalArgumentException if the given policy is {@link CodecPolicy#LENIENT}.
         */
        @Override
        public Builder setDecodingPolicy(final CodecPolicy decodingPolicy) {
            if (decodingPolicy == CodecPolicy.LENIENT) {
                throw new IllegalArgumentException("CodecPolicy.STRICT is the only supported policy.");
            }
            return super.setDecodingPolicy(decodingPolicy != null ? decodingPolicy : CodecPolicy.STRICT);
        }

        /**
         * Sets the encode table and derives the matching decode table, so the codec can always decode its own output.
         *
         * @param encodeTable The encode table with exactly 45 unique entries, null resets to the default.
         * @return {@code this} instance.
         * @throws IllegalArgumentException if the encode table does not contain exactly 45 unique entries.
         */
        @Override
        public Builder setEncodeTable(final byte... encodeTable) {
            super.setDecodeTableRaw(toDecodeTable(encodeTable));
            return super.setEncodeTable(encodeTable);
        }

        /**
         * Always throws UnsupportedOperationException: Unsupported by Base45 RFC 9285.
         *
         * @throws UnsupportedOperationException Always thrown: Unsupported by Base45 RFC 9285.
         */
        @Override
        public Builder setLineLength(final int lineLength) {
            throw new UnsupportedOperationException("Unsupported by Base45 RFC 9285");
        }

        /**
         * Always throws UnsupportedOperationException: Unsupported by Base45 RFC 9285.
         *
         * @throws UnsupportedOperationException Always thrown: Unsupported by Base45 RFC 9285.
         */
        @Override
        public Builder setLineSeparator(final byte... lineSeparator) {
            throw new UnsupportedOperationException("Unsupported by Base45 RFC 9285");
        }

        /**
         * Always throws UnsupportedOperationException: Unsupported by Base45 RFC 9285.
         *
         * @throws UnsupportedOperationException Always thrown: Unsupported by Base45 RFC 9285.
         */
        @Override
        public Builder setPadding(final byte padding) {
            throw new UnsupportedOperationException("Unsupported by Base45 RFC 9285");
        }
    }

    /**
     * The number of characters in the Base45 alphabet.
     */
    private static final int BASE = 45;

    /**
     * The square of the Base45 alphabet size (45 * 45 = 2025), used during decoding.
     */
    private static final int BASE_SQUARED = BASE * BASE; // 2025

    /**
     * Number of Base45 characters in an encoded block (encoding 2 unencoded bytes).
     */
    static final int BYTES_PER_ENCODED_BLOCK = 3;

    private static final int TAIL_ENCODED_BLOCK = BYTES_PER_ENCODED_BLOCK - 1;

    /**
     * Number of unencoded bytes per full encoding block.
     */
    static final int BYTES_PER_UNENCODED_BLOCK = 2;

    /**
     * Lookup table translating ASCII character values (0–127) to their Base45 alphabet index (0–44), or -1 if the character is not in the Base45 alphabet.
     */
    // @formatter:off
    static final byte[] DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
               -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
               -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
               36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, // 20-2f ' ','$','%','*','+','-','.','/
                0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 44, -1, -1, -1, -1, -1, // 30-3f '0'-'9', ':'
               -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 40-4f 'A'-'O'
               25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, // 50-5f 'P'-'Z'
               -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 60-6f
               -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 70-7f
    };

    // @formatter:on
    /**
     * Lookup table translating 6-bit Base45 values (0–44) to their ASCII character equivalents.
     * <p>
     * As specified in <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285</a>: {@code 0-9, A-Z, Space, $, %, *, +, -, ., /, :}
     * </p>
     */
    // @formatter:off
    static final byte[] ENCODE_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',            //  0-9
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',  // 10-21
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',  // 22-33
            'Y', 'Z',                                                    // 34-35
            ' ', '$', '%', '*', '+', '-', '.', '/', ':',                 // 36-44
    };
    // @formatter:on

    /**
     * Creates a new {@link Builder} for configuring a {@link Base45} instance.
     *
     * @return A new {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs the decode table matching the given encode table.
     *
     * @param encodeTable The encode table.
     * @return A new decode table.
     * @throws IllegalArgumentException if the encode table does not contain exactly 45 unique entries.
     */
    private static byte[] calculateDecodeTable(final byte[] encodeTable) {
        if (encodeTable.length != BASE) {
            throw new IllegalArgumentException("encodeTable must have exactly " + BASE + " entries.");
        }
        final byte[] decodeTable = new byte[DECODE_TABLE.length];
        Arrays.fill(decodeTable, (byte) -1);
        for (int i = 0; i < encodeTable.length; i++) {
            final int encodedByte = encodeTable[i] & 0xff;
            if (encodedByte >= decodeTable.length || decodeTable[encodedByte] != -1) {
                throw new IllegalArgumentException("encodeTable entries must be unique values in the range 0-127.");
            }
            decodeTable[encodedByte] = (byte) i;
        }
        return decodeTable;
    }

    /**
     * Gets the decode table that matches the given encode table.
     *
     * @param encodeTable The encode table used to determine the decode lookup table.
     * @return The matching decode table.
     */
    private static byte[] toDecodeTable(final byte[] encodeTable) {
        final byte[] table = encodeTable != null ? encodeTable : ENCODE_TABLE;
        if (Arrays.equals(table, ENCODE_TABLE)) {
            return DECODE_TABLE;
        }
        return calculateDecodeTable(table);
    }

    /**
     * Constructs a Base45 codec using the default settings (strict decoding policy, the only supported policy).
     */
    public Base45() {
        this(builder());
    }

    /**
     * Constructs a Base45 codec from a builder.
     *
     * @param builder The builder to configure this instance.
     */
    private Base45(final Builder builder) {
        super(builder);
    }

    /**
     * Decodes all of the provided data, starting at {@code inPos}, for {@code inAvail} bytes.
     * <p>
     * This method must be called at least twice: once with the data to decode, and once with {@code inAvail} set to {@code -1} to notify the decoder that EOF
     * has been reached.
     * </p>
     * <p>
     * Input characters not in the Base45 alphabet are treated as follows:
     * </p>
     * <ul>
     * <li>Whitespace characters (e.g., CR, LF, TAB) that are not in the Base45 alphabet are silently skipped (note: space {@code ' '} IS in the Base45 alphabet
     * and is not skipped).</li>
     * <li>Any other non-alphabet character causes an {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * @param input   byte array of Base45-encoded character data to decode.
     * @param inPos   Position to start reading data from.
     * @param inAvail Number of bytes available from {@code input} for decoding, or {@code -1} to signal EOF.
     * @param context The context to be used.
     * @throws IllegalArgumentException if the input contains an invalid character, if the encoded length modulo 3 equals 1, or if an encoded triple decodes to
     *                                  a value exceeding 65535, or if a trailing 2-character sequence decodes to a value greater than 255.
     */
    @Override
    void decode(final byte[] input, int inPos, final int inAvail, final Context context) {
        // package-protected for access from I/O streams
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
            switch (context.modulus) {
            case 0:
                // Nothing to do; input length is a multiple of 3.
                break;
            case 1:
                // RFC 9285: "It is an error if the remaining string length is 1 character."
                throw new IllegalArgumentException("Invalid Base45 encoding: encoded input length modulo 3 must not equal 1.");
            case 2:
                // Two trailing characters decode to one byte.
                // Maximum decodable value from two Base45 characters: 44 + 44*45 = 2024.
                // Valid single-byte encodings have a decoded value in [0, 255].
                if (context.ibitWorkArea > 0xFF) {
                    throw new IllegalArgumentException("Invalid Base45 encoding: trailing 2-character sequence decodes to " + context.ibitWorkArea +
                            ", which exceeds the valid byte range (0-255).");
                }
                ensureBufferSize(1, context)[context.pos++] = (byte) context.ibitWorkArea;
                break;
            default:
                throw new IllegalStateException("Impossible modulus " + context.modulus);
            }
            return;
        }
        for (int i = 0; i < inAvail; i++) {
            final int b = input[inPos++] & 0xFF;
            // Characters not in the Base45 alphabet:
            // - Whitespace (excluding space ' ' which IS in the alphabet): skip silently.
            // - Any other non-alphabet character: throw.
            if (b >= decodeTable.length || decodeTable[b] < 0) {
                if (Character.isWhitespace(b)) {
                    // Skip whitespace characters that are not in the alphabet (e.g., CR, LF, TAB).
                    // Note: space (ASCII 32) is part of the Base45 alphabet and is handled above.
                    continue;
                }
                throw new IllegalArgumentException("Invalid Base45 character '" + (char) b + "' (value " + b + ").");
            }
            final int value = decodeTable[b];
            switch (context.modulus) {
            case 0:
                // First character of a 3-character group: initialize accumulator.
                context.ibitWorkArea = value;
                context.modulus = 1;
                break;
            case 1:
                // Second character of a 3-character group.
                context.ibitWorkArea += value * BASE;
                context.modulus = 2;
                break;
            case 2:
                // Third character of a 3-character group: compute value and output 2 bytes.
                context.ibitWorkArea += value * BASE_SQUARED;
                context.modulus = 0;
                if (context.ibitWorkArea > 0xFFFF) {
                    throw new IllegalArgumentException("Invalid Base45 encoding: 3-character sequence decodes to " + context.ibitWorkArea +
                            ", which exceeds the valid 16-bit range (0-65535).");
                }
                final byte[] buffer = ensureBufferSize(BYTES_PER_UNENCODED_BLOCK, context);
                buffer[context.pos++] = (byte) (context.ibitWorkArea >> 8);
                buffer[context.pos++] = (byte) (context.ibitWorkArea & 0xFF);
                context.ibitWorkArea = 0;
                break;
            default:
                throw new IllegalStateException("Impossible modulus " + context.modulus);
            }
        }
    }

    /**
     * Encodes all of the provided data, starting at {@code inPos}, for {@code inAvail} bytes.
     * <p>
     * This method must be called at least twice: once with the data to encode, and once with {@code inAvail} set to {@code -1} to notify the encoder that EOF
     * has been reached.
     * </p>
     * <p>
     * Each pair of input bytes is encoded to 3 Base45 characters. A final single byte is encoded as 2 Base45 characters. No padding is used.
     * </p>
     *
     * @param input   byte array of binary data to Base45-encode.
     * @param inPos   Position to start reading data from.
     * @param inAvail Number of bytes available from {@code input} for encoding, or {@code -1} to signal EOF.
     * @param context The context to be used.
     */
    @Override
    void encode(final byte[] input, int inPos, final int inAvail, final Context context) {
        // package-protected for access from I/O streams
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
            if (context.modulus == 1) {
                // One remaining byte: encode as 2 Base45 characters.
                final byte[] buffer = ensureBufferSize(TAIL_ENCODED_BLOCK, context);
                final int n = context.ibitWorkArea & 0xFF;
                buffer[context.pos++] = encodeTable[n % BASE];
                buffer[context.pos++] = encodeTable[n / BASE];
            }
            // If modulus == 0, all bytes have been encoded; nothing to flush.
            return;
        }
        for (int i = 0; i < inAvail; i++) {
            final int b = input[inPos++] & 0xFF;
            // Accumulate byte into work area and advance modulus.
            context.modulus = (context.modulus + 1) % BYTES_PER_UNENCODED_BLOCK;
            // Shift the accumulated value left by 8 bits and add the new byte.
            context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
            if (context.modulus == 0) {
                // We have a complete 2-byte group; encode as 3 Base45 characters.
                final byte[] buffer = ensureBufferSize(BYTES_PER_ENCODED_BLOCK, context);
                // The work area holds: b0 * 256 + b1 (a 16-bit value, 0–65535).
                int n = context.ibitWorkArea & 0xFFFF;
                buffer[context.pos++] = encodeTable[n % BASE];
                n /= BASE;
                buffer[context.pos++] = encodeTable[n % BASE];
                n /= BASE;
                buffer[context.pos++] = encodeTable[n];
                context.ibitWorkArea = 0;
            }
        }
    }

    /**
     * Gets the number of Base45-encoded characters needed to encode the given byte array, as specified by RFC 9285.
     * <p>
     * The formula is: {@code (n / 2) * 3 + (n % 2 != 0 ? 2 : 0)}, where {@code n} is the number of unencoded bytes.
     * </p>
     *
     * @param array The byte array to encode (used only for its length).
     * @return The number of Base45 characters that would be produced by encoding {@code array}.
     */
    @Override
    public long getEncodedLength(final byte[] array) {
        final long n = array.length;
        return n / 2 * 3 + (n % 2 != 0 ? 2 : 0);
    }

    /**
     * Tests whether or not the {@code value} is a valid Base45 alphabet character.
     *
     * @param value The byte value to test.
     * @return {@code true} if the byte corresponds to a character in the Base45 alphabet (RFC 9285); {@code false} otherwise.
     */
    @Override
    public boolean isInAlphabet(final byte value) {
        final int v = value & 0xFF;
        return v < decodeTable.length && decodeTable[v] >= 0;
    }

    /**
     * Tests a given byte array to see if it contains only valid characters within the alphabet. The method optionally treats whitespace as valid.
     * <p>
     * Unlike the {@link BaseNCodec} implementation, the pad character is <em>not</em> considered valid, because Base45 (RFC 9285) has no padding.
     * </p>
     *
     * @param arrayOctet         byte array to test.
     * @param allowWhitespacePad if {@code true}, then whitespace is also allowed.
     * @return {@code true} if all bytes are valid characters in the alphabet or if the byte array is empty; {@code false}, otherwise.
     */
    @Override
    public boolean isInAlphabet(final byte[] arrayOctet, final boolean allowWhitespacePad) {
        for (final byte octet : arrayOctet) {
            if (!isInAlphabet(octet) && (!allowWhitespacePad || !Character.isWhitespace(octet))) {
                return false;
            }
        }
        return true;
    }
}
