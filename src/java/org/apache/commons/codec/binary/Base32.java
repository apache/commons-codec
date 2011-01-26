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

/**
 * Provides Base32 encoding and decoding as defined by RFC 4648.
 * 
 *                               <b>Initial implementation. API may change. Incomplete.</b>
 *  
 * <p>
 * The class can be parameterized in the following manner with various constructors:
 * <ul>
 * <li>Line length: Default 76. Line length that aren't multiples of 8 will still essentially end up being multiples of
 * 8 in the encoded data.
 * 
 * <li>Line separator: Default is CRLF ("\r\n")</li>
 * </ul>
 * </p>
 * <p>
 * Since this class operates directly on byte streams, and not character streams, it is hard-coded to only encode/decode
 * character encodings which are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, UTF-8, etc).
 * </p>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>
 * 
 * @since 1.5
 * @version $Revision$
 */
public class Base32 extends BaseNCodec {

    /**
     * BASE32 characters are 5 bits in length. 
     * They are formed by taking a block of five octets to form a 40-bit string, 
     * which is converted into eight BASE32 characters.
     */
    private static final int BITS_PER_ENCODED_CHAR = 5;
    private static final int BYTES_PER_UNENCODED_BLOCK = 5;
    private static final int BYTES_PER_ENCODED_BLOCK = 8;

    /**
     * Chunk separator per RFC 2045 section 2.1.
     *
     * <p>
     * N.B. The next major release may break compatibility and make this field private.
     * </p>
     * 
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 2.1</a>
     */
    private static final byte[] CHUNK_SEPARATOR = {'\r', '\n'};

    /**
     * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Alphabet"
     * equivalents as specified in Table 3 of RFC 2045.
     */
    private static final byte[] BASE32_ENCODE_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7',
    };

    /**
     * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Hex Alphabet"
     * equivalents as specified in Table 3 of RFC 2045.
     */
    private static final byte[] BASE32HEX_ENCODE_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
    };

    /**
     * Byte used to pad output.
     */
    private static final byte PAD = '=';

    /**
     * This array is a lookup table that translates Unicode characters drawn from the "Base32 Alphabet" (as specified in
     * Table 3 of RFC 2045) into their 5-bit positive integer equivalents. Characters that are not in the Base32
     * alphabet but fall within the bounds of the array are translated to -1.
     * 
     */
    private static final byte[] BASE32_DECODE_TABLE = {
         //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, // 20-2f
            -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, // 30-3f 2-7
            -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, // 40-4f A-N
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 50-5a O-Z
    };

    /**
     * This array is a lookup table that translates Unicode characters drawn from the "Base32 |Hex Alphabet" (as specified in
     * Table 3 of RFC 2045) into their 5-bit positive integer equivalents. Characters that are not in the Base32 Hex
     * alphabet but fall within the bounds of the array are translated to -1.
     * 
     */
    private static final byte[] BASE32HEX_DECODE_TABLE = {
         //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, // 20-2f
             0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1, // 30-3f 2-7
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 40-4f A-N
            25, 26, 27, 28, 29, 30, 31, 32,                                 // 50-57 O-V
    };

    /** Mask used to extract 5 bits, used when encoding Base32 bytes */
    private static final int MASK_5BITS = 0x1f;

    /** Mask used to extract 8 bits, used in decoding Base32 bytes */
    private static final int MASK_8BITS = 0xff;

    // The static final fields above are used for the original static byte[] methods on Base32.
    // The private member fields below are used with the new streaming approach, which requires
    // some state be preserved between calls of encode() and decode().

    /**
     * Encode table to use.
     */
    private final byte[] encodeTable;

    private final byte[] decodeTable;
    /**
     * Line length for encoding. Not used when decoding. A value of zero or less implies no chunking of the Base32
     * encoded data.
     */
    private final int lineLength;

    /**
     * Line separator for encoding. Not used when decoding. Only used if lineLength > 0.
     */
    private final byte[] lineSeparator;

    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     * <code>decodeSize = 3 + lineSeparator.length;</code>
     * TODO 3?
     */
    private final int decodeSize;

    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     * <code>encodeSize = 4 + lineSeparator.length;</code>
     * TODO 4? is that BYTES_PER_ENCODED_BLOCK? - probably yes
     */
    private final int encodeSize;

    /**
     * Creates a Base32 codec used for decoding and encoding.
     * <p>
     * When encoding the line length is 0 (no chunking).
     * </p>
     * 
     */
    public Base32() {
        this(false);
    }

    /**
     * Creates a Base32 codec used for decoding and encoding.
     * <p>
     * When encoding the line length is 0 (no chunking).
     * </p>
     * @param useHex if <code>true</code> then use Base32 Hex alphabet
     */
    public Base32(boolean useHex) {
        this(0, null, useHex);
    }

    /**
     * Creates a Base32 codec used for decoding and encoding.
     * <p>
     * When encoding the line length is given in the constructor, the line separator is CRLF.
     * </p>
     * 
     * @param lineLength
     *            Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     *            If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     */
    public Base32(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    /**
     * Creates a Base32 codec used for decoding and encoding.
     * <p>
     * When encoding the line length and line separator are given in the constructor.
     * </p>
     * <p>
     * Line lengths that aren't multiples of 8 will still essentially end up being multiples of 8 in the encoded data.
     * </p>
     * 
     * @param lineLength
     *            Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     *            If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator
     *            Each line of encoded data will end with this sequence of bytes.
     * @throws IllegalArgumentException
     *             The provided lineSeparator included some Base32 characters. That's not going to work!
     */
    public Base32(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }
    
    /**
     * Creates a Base32 / Base32 Hex codec used for decoding and encoding.
     * <p>
     * When encoding the line length and line separator are given in the constructor.
     * </p>
     * <p>
     * Line lengths that aren't multiples of 8 will still essentially end up being multiples of 8 in the encoded data.
     * </p>
     * 
     * @param lineLength
     *            Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     *            If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator
     *            Each line of encoded data will end with this sequence of bytes.
     * @param useHex if <code>true</code>, then use Base32 Hex alphabet, otherwise use Base32 alphabet
     * @throws IllegalArgumentException
     *             The provided lineSeparator included some Base32 characters. That's not going to work!
     */
    public Base32(int lineLength, byte[] lineSeparator, boolean useHex) {
        if (lineSeparator == null) {
            lineLength = 0;  // disable chunk-separating
            lineSeparator = CHUNK_SEPARATOR;  // this just gets ignored
        }
        this.lineLength = lineLength > 0 ? (lineLength / BYTES_PER_ENCODED_BLOCK) * BYTES_PER_ENCODED_BLOCK : 0;
        this.lineSeparator = new byte[lineSeparator.length];
        System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
        if (lineLength > 0) {
            this.encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.length;
        } else {
            this.encodeSize = BYTES_PER_ENCODED_BLOCK;
        }
        this.decodeSize = this.encodeSize - 1;
        if (containsBase32Byte(lineSeparator)) {
            String sep = StringUtils.newStringUtf8(lineSeparator);
            throw new IllegalArgumentException("lineSeperator must not contain Base32 characters: [" + sep + "]");
        }
        if (useHex){
            this.encodeTable = BASE32HEX_ENCODE_TABLE;
            this.decodeTable = BASE32HEX_DECODE_TABLE;            
        } else {
            this.encodeTable = BASE32_ENCODE_TABLE;
            this.decodeTable = BASE32_DECODE_TABLE;            
        }
    }

    /**
     * Returns whether or not the <code>octet</code> is in the Base32 alphabet.
     * 
     * @param octet
     *            The value to test
     * @return <code>true</code> if the value is defined in the the Base32 alphabet (or pad), <code>false</code> otherwise.
     */
    public boolean isBase32(byte octet) {
        return octet == PAD || (octet >= 0 && octet < BASE32_DECODE_TABLE.length && BASE32_DECODE_TABLE[octet] != -1);
    }

    /**
     * Returns whether or not the <code>octet</code> is in the Base32 Hex alphabet.
     * 
     * @param octet
     *            The value to test
     * @return <code>true</code> if the value is defined in the the Base32 Hex alphabet (or pad), <code>false</code> otherwise.
     */
    public boolean isBase32Hex(byte octet) {
        return octet == PAD || (octet >= 0 && octet < BASE32HEX_DECODE_TABLE.length && BASE32HEX_DECODE_TABLE[octet] != -1);
    }

    /**
     * Tests a given String to see if it contains only valid characters within the Base32 alphabet. Currently the
     * method treats whitespace as valid.
     * 
     * @param base32
     *            String to test
     * @return <code>true</code> if all characters in the String are valid characters in the Base32 alphabet or if
     *         the String is empty; <code>false</code>, otherwise
     */
    public boolean isBase32(String base32) {
        return isBase32(StringUtils.getBytesUtf8(base32));
    }

    /**
     * Tests a given byte array to see if it contains only valid characters within the Base32 alphabet. Currently the
     * method treats whitespace as valid.
     * 
     * @param arrayOctet
     *            byte array to test
     * @return <code>true</code> if all bytes are valid characters in the Base32 alphabet or if the byte array is empty;
     *         <code>false</code>, otherwise
     */    
    public boolean isBase32(byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (!isBase32(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tests a given byte array to see if it contains any characters within the Base32 alphabet.
     * Does not allow white-space.
     * 
     * @param arrayOctet
     *            byte array to test
     * @return <code>true</code> if any byte is a valid character in the Base32 alphabet; <code>false</code> otherwise
     */
    private boolean containsBase32Byte(byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (isBase32(arrayOctet[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Encodes binary data using the Base32 algorithm but does not chunk the output.
     * 
     * @param binaryData
     *            binary data to encode
     * @return byte[] containing Base32 characters in their UTF-8 representation.
     */
    public static byte[] encodeBase32(byte[] binaryData) {
        return encodeBase32(binaryData, false);
    }

    /**
     * Encodes binary data using the Base32 algorithm but does not chunk the output.
     *
     * @param binaryData
     *            binary data to encode
     * @return String containing Base32 characters.
     */    
    public static String encodeBase32String(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase32(binaryData, false));
    }
    
    /**
     * Encodes binary data using the Base32 algorithm but does not chunk the output.
     *
     * @param binaryData
     *            binary data to encode
     * @return String containing Base32Hex characters.
     */    
    public static String encodeBase32HexString(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase32Hex(binaryData, false));
    }
    
    /**
     * Encodes binary data using the Base32 algorithm and chunks the encoded output into 76 character blocks
     * 
     * @param binaryData
     *            binary data to encode
     * @return Base32 characters chunked in 76 character blocks
     */
    public static byte[] encodeBase32Chunked(byte[] binaryData) {
        return encodeBase32(binaryData, true);
    }

    /**
     * Encodes binary data using the Base32 algorithm, optionally chunking the output into 76 character blocks.
     * 
     * @param binaryData
     *            Array containing binary data to encode.
     * @param isChunked
     *            if <code>true</code> this encoder will chunk the Base32 output into 76 character blocks
     * @return Base32-encoded data.
     * @throws IllegalArgumentException
     *             Thrown when the input array needs an output array bigger than {@link Integer#MAX_VALUE}
     */
    public static byte[] encodeBase32(byte[] binaryData, boolean isChunked) {
        return encodeBase32(binaryData, isChunked, Integer.MAX_VALUE);
    }

    /**
     * Encodes binary data using the Base32 Hex algorithm, optionally chunking the output into 76 character blocks.
     * 
     * @param binaryData
     *            Array containing binary data to encode.
     * @param isChunked
     *            if <code>true</code> this encoder will chunk the Base32 output into 76 character blocks
     * @return Base32Hex-encoded data.
     * @throws IllegalArgumentException
     *             Thrown when the input array needs an output array bigger than {@link Integer#MAX_VALUE}
     */
    public static byte[] encodeBase32Hex(byte[] binaryData, boolean isChunked) {
        return encodeBase32Hex(binaryData, isChunked, Integer.MAX_VALUE);
    }

    /**
     * Encodes binary data using the Base32 algorithm, optionally chunking the output into 76 character blocks.
     * 
     * @param binaryData
     *            Array containing binary data to encode.
     * @param isChunked
     *            if <code>true</code> this encoder will chunk the Base32 output into 76 character blocks
     * @param maxResultSize
     *            The maximum result size to accept.
     * @return Base32-encoded data.
     * @throws IllegalArgumentException
     *             Thrown when the input array needs an output array bigger than maxResultSize
     */
    public static byte[] encodeBase32(byte[] binaryData, boolean isChunked, int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        long len = getEncodeLength(binaryData, MIME_CHUNK_SIZE, CHUNK_SEPARATOR);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                len +
                ") than the specified maxium size of " +
                maxResultSize);
        }
                
        Base32 b64 = isChunked ? new Base32(MIME_CHUNK_SIZE, CHUNK_SEPARATOR) : new Base32();
        return b64.encode(binaryData);
    }

    /**
     * Encodes binary data using the Base32Hex algorithm, optionally chunking the output into 76 character blocks.
     * 
     * @param binaryData
     *            Array containing binary data to encode.
     * @param isChunked
     *            if <code>true</code> this encoder will chunk the Base32 output into 76 character blocks
     * @param maxResultSize
     *            The maximum result size to accept.
     * @return Base32Hex-encoded data.
     * @throws IllegalArgumentException
     *             Thrown when the input array needs an output array bigger than maxResultSize
     */
    public static byte[] encodeBase32Hex(byte[] binaryData, boolean isChunked, int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        long len = getEncodeLength(binaryData, MIME_CHUNK_SIZE, CHUNK_SEPARATOR);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                len +
                ") than the specified maxium size of " +
                maxResultSize);
        }
                
        Base32 b64 = isChunked ? new Base32(MIME_CHUNK_SIZE, CHUNK_SEPARATOR, true) : new Base32(true);
        return b64.encode(binaryData);
    }

    /**
     * Decodes a Base32 String into octets
     *
     * @param base32String
     *            String containing Base32 data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase32(String base32String) {
        return new Base32().decode(base32String);
    }

    /**
     * Decodes Base32 data into octets
     * 
     * @param base32Data
     *            Byte array containing Base32 data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase32(byte[] base32Data) {
        return new Base32().decode(base32Data);
    }

    /**
     * Decodes a Base32 Hex String into octets
     *
     * @param base32HexString
     *            String containing Base32Hex data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase32Hex(String base32HexString) {
        return new Base32(true).decode(base32HexString);
    }

    /**
     * Decodes Base32 Hex data into octets
     * 
     * @param base32HexData
     *            Byte array containing Base32Hex data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase32Hex(byte[] base32HexData) {
        return new Base32(true).decode(base32HexData);
    }

    /**
     * <p>
     * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
     * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, so flush last
     * remaining bytes (if not multiple of 5).
     * </p>
     * 
     * @param in
     *            byte[] array of binary data to Base32 encode.
     * @param inPos
     *            Position to start reading data from.
     * @param inAvail
     *            Amount of bytes available from input for encoding.
     */
    void encode(byte[] in, int inPos, int inAvail) { // package protected for access from I/O streams
        if (eof) {
            return;
        }
        // inAvail < 0 is how we're informed of EOF in the underlying data we're
        // encoding.
        if (inAvail < 0) {
            eof = true;
            if (buffer == null || buffer.length - pos < encodeSize) {
                resizeBuffer();
            }
            switch (modulus) { // % 5
                case 1 : // Only 1 octet; take top 5 bits then remainder
                    buffer[pos++] = encodeTable[(int)(x >> 3) & MASK_5BITS]; // 8-1*5 = 3
                    buffer[pos++] = encodeTable[(int)(x << 2) & MASK_5BITS]; // 5-3=2
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    break;
    
                case 2 : // 2 octets = 16 bits to use
                    buffer[pos++] = encodeTable[(int)(x >> 11) & MASK_5BITS]; // 16-1*5 = 11
                    buffer[pos++] = encodeTable[(int)(x >>  6) & MASK_5BITS]; // 16-2*5 = 6
                    buffer[pos++] = encodeTable[(int)(x >>  1) & MASK_5BITS]; // 16-3*5 = 1
                    buffer[pos++] = encodeTable[(int)(x <<  4) & MASK_5BITS]; // 5-1 = 4
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    break;
                case 3 : // 3 octets = 24 bits to use
                    buffer[pos++] = encodeTable[(int)(x >> 19) & MASK_5BITS]; // 24-1*5 = 19
                    buffer[pos++] = encodeTable[(int)(x >> 14) & MASK_5BITS]; // 24-2*5 = 14
                    buffer[pos++] = encodeTable[(int)(x >>  9) & MASK_5BITS]; // 24-3*5 = 9
                    buffer[pos++] = encodeTable[(int)(x >>  4) & MASK_5BITS]; // 24-4*5 = 4
                    buffer[pos++] = encodeTable[(int)(x <<  1) & MASK_5BITS]; // 5-4 = 1
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    buffer[pos++] = PAD;
                    break;
                case 4 : // 4 octets = 32 bits to use
                    buffer[pos++] = encodeTable[(int)(x >> 27) & MASK_5BITS]; // 32-1*5 = 27
                    buffer[pos++] = encodeTable[(int)(x >> 22) & MASK_5BITS]; // 32-2*5 = 22
                    buffer[pos++] = encodeTable[(int)(x >> 17) & MASK_5BITS]; // 32-3*5 = 17
                    buffer[pos++] = encodeTable[(int)(x >> 12) & MASK_5BITS]; // 32-4*5 = 12
                    buffer[pos++] = encodeTable[(int)(x >>  7) & MASK_5BITS]; // 32-5*5 =  7
                    buffer[pos++] = encodeTable[(int)(x >>  2) & MASK_5BITS]; // 32-6*5 =  2
                    buffer[pos++] = encodeTable[(int)(x <<  3) & MASK_5BITS]; // 5-2 = 3
                    buffer[pos++] = PAD;
                    break;
            }
            // Don't want to append the CRLF two times in a row, so make sure previous
            // character is not from CRLF!
            byte b = lineSeparator[lineSeparator.length - 1];
            if (lineLength > 0 && pos > 0 && buffer[pos-1] != b) {
                System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator.length);
                pos += lineSeparator.length;
            }
        } else {
            for (int i = 0; i < inAvail; i++) {
                if (buffer == null || buffer.length - pos < encodeSize) {
                    resizeBuffer();
                }
                modulus = (++modulus) % BITS_PER_ENCODED_CHAR;
                int b = in[inPos++];
                if (b < 0) {
                    b += 256;
                }
                x = (x << 8) + b; // ??
                if (0 == modulus) { // we have enough bytes to create our output 
                    buffer[pos++] = encodeTable[(int)(x >> 35) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 30) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 25) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 20) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 15) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 10) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)(x >> 5) & MASK_5BITS];
                    buffer[pos++] = encodeTable[(int)x & MASK_5BITS];
                    currentLinePos += BYTES_PER_ENCODED_BLOCK;
                    if (lineLength > 0 && lineLength <= currentLinePos) {
                        System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator.length);
                        pos += lineSeparator.length;
                        currentLinePos = 0;
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
     * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
     * call is not necessary when decoding, but it doesn't hurt, either.
     * </p>
     * <p>
     * Ignores all non-Base32 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     * </p>
     * 
     * @param in
     *            byte[] array of ascii data to Base32 decode.
     * @param inPos
     *            Position to start reading data from.
     * @param inAvail
     *            Amount of bytes available from input for encoding.
     *
     * Output is written to {@link #buffer} as 8-bit octets, using {@link pos} as the buffer position
     */
    void decode(byte[] in, int inPos, int inAvail) { // package protected for access from I/O streams
        if (eof) {
            return;
        }
        if (inAvail < 0) {
            eof = true;
        }
        for (int i = 0; i < inAvail; i++) {
            if (buffer == null || buffer.length - pos < decodeSize) {
                resizeBuffer();
            }
            byte b = in[inPos++];
            if (b == PAD) {
                // We're done.
                eof = true;
                break;
            } else {
                if (b >= 0 && b < this.decodeTable.length) {
                    int result = this.decodeTable[b];
                    if (result >= 0) {
                        modulus = (++modulus) % BYTES_PER_ENCODED_BLOCK;
                        x = (x << BITS_PER_ENCODED_CHAR) + result; // collect decoded bytes
                        if (modulus == 0) { // we can output the 5 bytes
                            buffer[pos++] = (byte) ((x >> 32) & MASK_8BITS);
                            buffer[pos++] = (byte) ((x >> 24) & MASK_8BITS);
                            buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                            buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                            buffer[pos++] = (byte) (x & MASK_8BITS);
                        }
                    }
                }
            }
        }
    
        // Two forms of EOF as far as Base32 decoder is concerned: actual
        // EOF (-1) and first time '=' character is encountered in stream.
        // This approach makes the '=' padding characters completely optional.
        if (eof && modulus != 0) {
            if (buffer == null || buffer.length - pos < decodeSize) {
                resizeBuffer();
            }
    
            //  we ignore partial bytes, i.e. only multiples of 8 count
            switch (modulus) {
                case 2 : // 10 bits, drop 2 and output one byte
                    buffer[pos++] = (byte) ((x >> 2) & MASK_8BITS);
                    break;
                case 3 : // 15 bits, drop 7 and output 1 byte
                    buffer[pos++] = (byte) ((x >> 7) & MASK_8BITS);
                    break;
                case 4 : // 20 bits = 2*8 + 4
                    x = x >> 4; // drop 4 bits
                    buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x) & MASK_8BITS);
                    break;
                case 5 : // 25bits = 3*8 + 1
                    x = x >> 1;
                    buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x) & MASK_8BITS);
                    break;
                case 6 : // 30bits = 3*8 + 6
                    x = x >> 6;
                    buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x) & MASK_8BITS);
                    break;
                case 7 : // 35 = 4*8 +3
                    x = x >> 3;
                    buffer[pos++] = (byte) ((x >> 24) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x >> 16) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x >> 8) & MASK_8BITS);
                    buffer[pos++] = (byte) ((x) & MASK_8BITS);
                    break;
            }
        }
    }

  /**
  * Pre-calculates the amount of space needed to Base32-encode the supplied array.
  *
  * @param pArray byte[] array which will later be encoded
  * @param chunkSize line-length of the output (<= 0 means no chunking) between each
  *        chunkSeparator (e.g. CRLF).
  * @param chunkSeparator the sequence of bytes used to separate chunks of output (e.g. CRLF).
  *
  * @return amount of space needed to encoded the supplied array.  Returns
  *         a long since a max-len array will require Integer.MAX_VALUE + 33%.
  */
 private static long getEncodeLength(byte[] pArray, int chunkSize,
         byte[] chunkSeparator) {
             // Base32 always encodes to multiples of 8 (BYTES_PER_ENCODED_CHUNK).
             chunkSize = (chunkSize / BYTES_PER_ENCODED_BLOCK) * BYTES_PER_ENCODED_BLOCK;
         
             long len = (pArray.length * BYTES_PER_ENCODED_BLOCK) / BYTES_PER_UNENCODED_BLOCK;
             long mod = len % BYTES_PER_ENCODED_BLOCK;
             if (mod != 0) {
                 len += BYTES_PER_ENCODED_BLOCK - mod;
             }
             if (chunkSize > 0) {
                 boolean lenChunksPerfectly = len % chunkSize == 0;
                 len += (len / chunkSize) * chunkSeparator.length;
                 if (!lenChunksPerfectly) {
                     len += chunkSeparator.length;
                 }
             }
             return len;
         }

}
