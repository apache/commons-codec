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

import java.io.ByteArrayOutputStream;
import java.util.BitSet;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;

/**
 * <p>
 * Codec for the Quoted-Printable section of <a href="http://www.ietf.org/rfc/rfc1521.txt">RFC 1521</a>.
 * </p>
 * <p>
 * The Quoted-Printable encoding is intended to represent data that largely consists of octets that correspond to
 * printable characters in the ASCII character set. It encodes the data in such a way that the resulting octets are
 * unlikely to be modified by mail transport. If the data being encoded are mostly ASCII text, the encoded form of the
 * data remains largely recognizable by humans. A body which is entirely ASCII may also be encoded in Quoted-Printable
 * to ensure the integrity of the data should the message pass through a character- translating, and/or line-wrapping
 * gateway.
 * </p>
 * 
 * <p>
 * Note:
 * </p>
 * <p>
 * Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet because the complete quoted-printable spec
 * does not lend itself well into the byte[] oriented codec framework. Complete the codec once the streamable codec
 * framework is ready. The motivation behind providing the codec in a partial form is that it can already come in handy
 * for those applications that do not require quoted-printable line formatting (rules #3, #4, #5), for instance Q codec.
 * </p>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1521.txt"> RFC 1521 MIME (Multipurpose Internet Mail Extensions) Part One:
 *          Mechanisms for Specifying and Describing the Format of Internet Message Bodies </a>
 * 
 * @author Apache Software Foundation
 * @since 1.3
 * @version $Id$
 */
public abstract class QuotedPrintableCodec {
    /**
     * The default charset used for string decoding and encoding.
     */
    private final String charset;

    /**
     * BitSet of printable characters as defined in RFC 1521.
     */
    private static final BitSet PRINTABLE_CHARS = new BitSet(256);

    private static final byte ESCAPE_CHAR = '=';

    private static final byte TAB = 9;

    private static final byte SPACE = 32;
    // Static initializer for printable chars collection
    static {
        // alpha characters
        for (int i = 33; i <= 60; i++) {
            PRINTABLE_CHARS.set(i);
        }
        for (int i = 62; i <= 126; i++) {
            PRINTABLE_CHARS.set(i);
        }
        PRINTABLE_CHARS.set(TAB);
        PRINTABLE_CHARS.set(SPACE);
    }

    /**
     * Default constructor.
     */
    public QuotedPrintableCodec() {
        this(CharEncoding.UTF_8);
    }

    /**
     * Constructor which allows for the selection of a default charset
     * 
     * @param charset
     *                  the default string charset to use.
     */
    public QuotedPrintableCodec(String charset) {
        super();
        this.charset = charset;
    }

    /**
     * Encodes byte into its quoted-printable representation.
     * 
     * @param b
     *                  byte to encode
     * @param buffer
     *                  the buffer to write to
     */
    private static final void encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
        buffer.write(ESCAPE_CHAR);
        char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
        char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
    }

    /**
     * Encodes an array of bytes into an array of quoted-printable 7-bit characters. Unsafe characters are escaped.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521 and is suitable for encoding binary data and unformatted text.
     * </p>
     * 
     * @param printable
     *                  bitset of characters deemed quoted-printable
     * @param bytes
     *                  array of bytes to be encoded
     * @return array of bytes containing quoted-printable data
     */
    public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (printable == null) {
            printable = PRINTABLE_CHARS;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (byte c : bytes) {
            int b = c;
            if (b < 0) {
                b = 256 + b;
            }
            if (printable.get(b)) {
                buffer.write(b);
            } else {
                encodeQuotedPrintable(b, buffer);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Decodes an array quoted-printable characters into an array of original bytes. Escaped characters are converted
     * back to their original representation.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521.
     * </p>
     * 
     * @param bytes
     *                  array of quoted-printable characters
     * @return array of original bytes
     * @throws DecoderException
     *                  Thrown if quoted-printable decoding is unsuccessful
     */
    public static final byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b == ESCAPE_CHAR) {
                try {
                    int u = Utils.digit16(bytes[++i]);
                    int l = Utils.digit16(bytes[++i]);
                    buffer.write((char) ((u << 4) + l));
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid quoted-printable encoding", e);
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Encodes an array of bytes into an array of quoted-printable 7-bit characters. Unsafe characters are escaped.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521 and is suitable for encoding binary data and unformatted text.
     * </p>
     * 
     * @param bytes
     *                  array of bytes to be encoded
     * @return array of bytes containing quoted-printable data
     */
    public byte[] encode(byte[] bytes) {
        return encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
    }

    /**
     * Decodes an array of quoted-printable characters into an array of original bytes. Escaped characters are converted
     * back to their original representation.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521.
     * </p>
     * 
     * @param bytes
     *                  array of quoted-printable characters
     * @return array of original bytes
     * @throws DecoderException
     *                  Thrown if quoted-printable decoding is unsuccessful
     */
    public byte[] decode(byte[] bytes) throws DecoderException {
        return decodeQuotedPrintable(bytes);
    }

    /**
     * Returns the default charset used for string decoding and encoding.
     * 
     * @return the default string charset.
     */
    public String getDefaultCharset() {
        return this.charset;
    }
}
