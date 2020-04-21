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

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;

import java.nio.charset.Charset;

/**
 * Provides Base16 encoding and decoding.
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @since 1.15
 */
public class Base16 extends BaseNCodec {

    private static final int BYTES_PER_UNENCODED_BLOCK = 1;
    private static final int BYTES_PER_ENCODED_BLOCK = 2;

    private final boolean toLowerCase;
    private final Charset charset;

    /**
     * Creates a Base16 codec used for decoding and encoding.
     */
    protected Base16() {
        this(Hex.DEFAULT_CHARSET);
    }

    /**
     * Creates a Base16 codec used for decoding and encoding.
     *
     * @param charset the charset.
     */
    protected Base16(final Charset charset) {
        this(true, charset);
    }

    /**
     * Creates a Base16 codec used for decoding and encoding.
     *
     * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase.
     * @param charset the charset.
     */
    protected Base16(final boolean toLowerCase, final Charset charset) {
        super(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, 0, 0);
        this.toLowerCase = toLowerCase;
        this.charset = charset;
    }

    /**
     * Creates a Base16 codec used for decoding and encoding.
     *
     * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase.
     * @param charset the charset.
     * @param decodingPolicy Decoding policy.
     */
    protected Base16(final boolean toLowerCase, final Charset charset, final CodecPolicy decodingPolicy) {
        super(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, 0, 0,
                PAD_DEFAULT, decodingPolicy);
        this.toLowerCase = toLowerCase;
        this.charset = charset;
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

        final char[] chars = Hex.encodeHex(data, offset, length, toLowerCase);
        final byte[] encoded = new String(chars).getBytes(charset);

        final byte[] buffer = ensureBufferSize(encoded.length, context);
        System.arraycopy(encoded, 0, buffer, context.pos, encoded.length);

        context.pos += encoded.length;
    }

    @Override
    void decode(final byte[] data, final int offset, final int length, final Context context) {
        if (context.eof || length < 0) {
            context.eof = true;
            if (context.ibitWorkArea > 0) {
                validateTrailingCharacter();
            }
            return;
        }

        final int dataLen = Math.min(data.length - offset, length);
        final int availableChars = (context.ibitWorkArea > 0 ? 1 : 0) + dataLen;

        // small optimisation to short-cut the rest of this method when it is fed byte-by-byte
        if (availableChars == 1 && availableChars == dataLen) {
            context.ibitWorkArea = data[offset];   // store 1/2 byte for next invocation of decode
            return;
        }

        // NOTE: Each pair of bytes is really a pair of hex-chars, therefore each pair represents one byte

        // we must have an even number of chars to decode
        final char[] encodedChars = new char[availableChars % 2 == 0 ? availableChars : availableChars - 1];

        // copy all (or part of) data into encodedChars
        int i = 0;
        if (dataLen < availableChars) {
            // we have 1/2 byte from previous invocation to decode
            encodedChars[i++] = (char)context.ibitWorkArea;
            context.ibitWorkArea = -1; // reset for next iteration!
        }
        final int copyLen = encodedChars.length - i;
        for (int j = offset; j < copyLen + offset; j++) {
            encodedChars[i++] = (char) data[j];
        }

        // decode encodedChars into buffer
        final byte[] buffer = ensureBufferSize(encodedChars.length / 2, context);
        try {
            final int written = Hex.decodeHex(encodedChars, buffer, context.pos);
            context.pos += written;
        } catch (final DecoderException e) {
            throw new RuntimeException(e);  // this method ensures that this cannot happen at runtime!
        }

        // we have one char of a hex-pair left over
        if (copyLen < dataLen) {
            context.ibitWorkArea = data[offset + dataLen - 1];   // store 1/2 byte for next invocation of decode
        }
    }

    /**
     * Validates whether decoding allows an entire final trailing character that cannot be
     * used for a complete byte.
     *
     * @throws IllegalArgumentException if strict decoding is enabled
     */
    private void validateTrailingCharacter() {
        if (isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character is a valid base 16 alphabet" +
                    "character but not a possible encoding. " +
                    "Decoding requires at least two characters to create one byte.");
        }
    }

    @Override
    protected boolean isInAlphabet(final byte value) {
        if (value >= '0' && value <= '9') {
            return true;
        }

        if (toLowerCase) {
            return value >= 'a' && value <= 'f';
        } else {
            return value >= 'A' && value <= 'F';
        }
    }

    /**
     * Returns whether or not the {@code c} is in the base 16 alphabet.
     *
     * @param c The value to test
     * @return {@code true} if the value is defined in the the base 16 alphabet, {@code false} otherwise.
     */
    public static boolean isBase16(final char c) {
        return
                (c >= '0' && c <= '9')
                || (c >= 'A' && c <= 'F')
                || (c >= 'a' && c <= 'f');
    }

    /**
     * Tests a given String to see if it contains only valid characters within the Base16 alphabet.
     *
     * @param base16 String to test
     * @return {@code true} if all characters in the String are valid characters in the Base16 alphabet or if
     *      the String is empty; {@code false}, otherwise
     */
    public static boolean isBase16(final String base16) {
        return isBase16(base16.toCharArray());
    }

    /**
     * Tests a given char array to see if it contains only valid characters within the Base16 alphabet.
     *
     * @param arrayChars char array to test
     * @return {@code true} if all chars are valid characters in the Base16 alphabet or if the char array is empty;
     *         {@code false}, otherwise
     */
    public static boolean isBase16(final char[] arrayChars) {
        for (int i = 0; i < arrayChars.length; i++) {
            if (!isBase16(arrayChars[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests a given char array to see if it contains only valid characters within the Base16 alphabet.
     *
     * @param arrayChars byte array to test
     * @return {@code true} if all chars are valid characters in the Base16 alphabet or if the byte array is empty;
     *         {@code false}, otherwise
     */
    public static boolean isBase16(final byte[] arrayChars) {
        for (int i = 0; i < arrayChars.length; i++) {
            if (!isBase16((char) arrayChars[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encodes binary data using the base16 algorithm.
     *
     * @param binaryData Array containing binary data to encode.
     * @return Base16-encoded data.
     */
    public static byte[] encodeBase16(final byte[] binaryData) {
        return encodeBase16(binaryData, true, Hex.DEFAULT_CHARSET, Integer.MAX_VALUE);
    }

    /**
     * Encodes binary data using the base16 algorithm.
     *
     * @param binaryData Array containing binary data to encode.
     * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase.
     * @param charset the charset.
     * @param maxResultSize The maximum result size to accept.
     * @return Base16-encoded data.
     * @throws IllegalArgumentException Thrown when the input array needs an output array bigger than maxResultSize
     */
    public static byte[] encodeBase16(final byte[] binaryData,  final boolean toLowerCase, final Charset charset,
            final int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        // Create this so can use the super-class method
        // Also ensures that the same roundings are performed by the ctor and the code
        final Base16 b16 = new Base16(toLowerCase, charset);
        final long len = b16.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                    len +
                    ") than the specified maximum size of " +
                    maxResultSize);
        }

        return b16.encode(binaryData);
    }

    /**
     * Decodes a Base16 String into octets.
     *
     * @param base16String String containing Base16 data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase16(final String base16String) {
        return new Base16().decode(base16String);
    }

    /**
     * Decodes Base16 data into octets.
     *
     * @param base16Data Byte array containing Base16 data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase16(final byte[] base16Data) {
        return new Base16().decode(base16Data);
    }
}
