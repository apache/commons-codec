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

package org.apache.commons.codec.net;

import java.nio.ByteBuffer;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Implements the Percent-Encoding scheme, as described in HTTP 1.1 specification. For extensibility, an array of
 * special US-ASCII characters can be specified in order to perform proper URI encoding for the different parts
 * of the URI.
 * <p>
 * This class is immutable. It is also thread-safe besides using BitSet which is not thread-safe, but its public
 * interface only call the access
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.1">Percent-Encoding</a>
 * @since 1.12
 */
public class PercentCodec implements BinaryEncoder, BinaryDecoder {

    /**
     * The escape character used by the Percent-Encoding in order to introduce an encoded character.
     */

    private static final byte ESCAPE_CHAR = '%';

    /**
     * The bit set used to store the character that should be always encoded
     */
    private final BitSet alwaysEncodeChars = new BitSet();

    /**
     * The flag defining if the space character should be encoded as '+'
     */
    private final boolean plusForSpace;

    /**
     * The minimum and maximum code of the bytes that is inserted in the bit set, used to prevent look-ups
     */
    private int alwaysEncodeCharsMin = Integer.MAX_VALUE, alwaysEncodeCharsMax = Integer.MIN_VALUE;

    /**
     * Constructs a Percent coded that will encode all the non US-ASCII characters using the Percent-Encoding
     * while it will not encode all the US-ASCII characters, except for character '%' that is used as escape
     * character for Percent-Encoding.
     */
    public PercentCodec() {
        this.plusForSpace = false;
        insertAlwaysEncodeChar(ESCAPE_CHAR);
    }

    /**
     * Constructs a Percent codec by specifying the characters that belong to US-ASCII that should
     * always be encoded. The rest US-ASCII characters will not be encoded, except for character '%' that
     * is used as escape character for Percent-Encoding.
     *
     * @param alwaysEncodeChars the unsafe characters that should always be encoded
     * @param plusForSpace      the flag defining if the space character should be encoded as '+'
     */
    public PercentCodec(final byte[] alwaysEncodeChars, final boolean plusForSpace) {
        this.plusForSpace = plusForSpace;
        insertAlwaysEncodeChars(alwaysEncodeChars);
    }

    /**
     * Adds the byte array into a BitSet for faster lookup
     *
     * @param alwaysEncodeCharsArray
     */
    private void insertAlwaysEncodeChars(final byte[] alwaysEncodeCharsArray) {
        if (alwaysEncodeCharsArray != null) {
            for (final byte b : alwaysEncodeCharsArray) {
                insertAlwaysEncodeChar(b);
            }
        }
        insertAlwaysEncodeChar(ESCAPE_CHAR);
    }

    /**
     * Inserts a single character into a BitSet and maintains the min and max of the characters of the
     * {@code BitSet alwaysEncodeChars} in order to avoid look-ups when a byte is out of this range.
     *
     * @param b the byte that is candidate for min and max limit
     */
    private void insertAlwaysEncodeChar(final byte b) {
        this.alwaysEncodeChars.set(b);
        if (b < alwaysEncodeCharsMin) {
            alwaysEncodeCharsMin = b;
        }
        if (b > alwaysEncodeCharsMax) {
            alwaysEncodeCharsMax = b;
        }
    }

    /**
     * Percent-Encoding based on RFC 3986. The non US-ASCII characters are encoded, as well as the
     * US-ASCII characters that are configured to be always encoded.
     */
    @Override
    public byte[] encode(final byte[] bytes) throws EncoderException {
        if (bytes == null) {
            return null;
        }

        final int expectedEncodingBytes = expectedEncodingBytes(bytes);
        final boolean willEncode = expectedEncodingBytes != bytes.length;
        if (willEncode || (plusForSpace && containsSpace(bytes))) {
            return doEncode(bytes, expectedEncodingBytes, willEncode);
        }
        return bytes;
    }

    private byte[] doEncode(final byte[] bytes, final int expectedLength, final boolean willEncode) {
        final ByteBuffer buffer = ByteBuffer.allocate(expectedLength);
        for (final byte b : bytes) {
            if (willEncode && canEncode(b)) {
                byte bb = b;
                if (bb < 0) {
                    bb = (byte) (256 + bb);
                }
                final char hex1 = Utils.hexDigit(bb >> 4);
                final char hex2 = Utils.hexDigit(bb);
                buffer.put(ESCAPE_CHAR);
                buffer.put((byte) hex1);
                buffer.put((byte) hex2);
            } else if (plusForSpace && b == ' ') {
                buffer.put((byte) '+');
            } else {
                buffer.put(b);
            }
        }
        return buffer.array();
    }

    private int expectedEncodingBytes(final byte[] bytes) {
        int byteCount = 0;
        for (final byte b : bytes) {
            byteCount += canEncode(b) ? 3: 1;
        }
        return byteCount;
    }

    private boolean containsSpace(final byte[] bytes) {
        for (final byte b : bytes) {
            if (b == ' ') {
                return true;
            }
        }
        return false;
    }

    private boolean canEncode(final byte c) {
        return !isAsciiChar(c) || (inAlwaysEncodeCharsRange(c) && alwaysEncodeChars.get(c));
    }

    private boolean inAlwaysEncodeCharsRange(final byte c) {
        return c >= alwaysEncodeCharsMin && c <= alwaysEncodeCharsMax;
    }

    private boolean isAsciiChar(final byte c) {
        return c >= 0;
    }

    /**
     * Decode bytes encoded with Percent-Encoding based on RFC 3986. The reverse process is performed in order to
     * decode the encoded characters to Unicode.
     */
    @Override
    public byte[] decode(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }

        final ByteBuffer buffer = ByteBuffer.allocate(expectedDecodingBytes(bytes));
        for (int i = 0; i < bytes.length; i++) {
            final byte b = bytes[i];
            if (b == ESCAPE_CHAR) {
                try {
                    final int u = Utils.digit16(bytes[++i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.put((byte) ((u << 4) + l));
                } catch (final ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid percent decoding: ", e);
                }
            } else if (plusForSpace && b == '+') {
                buffer.put((byte) ' ');
            } else {
                buffer.put(b);
            }
        }
        return buffer.array();
    }

    private int expectedDecodingBytes(final byte[] bytes) {
        int byteCount = 0;
        for (int i = 0; i < bytes.length; ) {
            final byte b = bytes[i];
            i += b == ESCAPE_CHAR ? 3: 1;
            byteCount++;
        }
        return byteCount;
    }

    /**
     * Encodes an object into using the Percent-Encoding. Only byte[] objects are accepted.
     *
     * @param obj the object to encode
     * @return the encoding result byte[] as Object
     * @throws EncoderException if the object is not a byte array
     */
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return encode((byte[]) obj);
        }
        throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent encoded");
    }

    /**
     * Decodes a byte[] Object, whose bytes are encoded with Percent-Encoding.
     *
     * @param obj the object to decode
     * @return the decoding result byte[] as Object
     * @throws DecoderException if the object is not a byte array
     */
    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return decode((byte[]) obj);
        }
        throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent decoded");
    }
}
