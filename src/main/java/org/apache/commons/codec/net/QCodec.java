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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

/**
 * Similar to the Quoted-Printable content-transfer-encoding defined in
 * <a href="http://www.ietf.org/rfc/rfc1521.txt">RFC 1521</a> and designed to allow text containing mostly ASCII
 * characters to be decipherable on an ASCII terminal without decoding.
 * <p>
 * <a href="http://www.ietf.org/rfc/rfc1522.txt">RFC 1522</a> describes techniques to allow the encoding of non-ASCII
 * text in various portions of a RFC 822 [2] message header, in a manner which is unlikely to confuse existing message
 * handling software.
 * </p>
 * <p>
 * This class is conditionally thread-safe.
 * The instance field for encoding blanks is mutable {@link #setEncodeBlanks(boolean)}
 * but is not volatile, and accesses are not synchronised.
 * If an instance of the class is shared between threads, the caller needs to ensure that suitable synchronisation
 * is used to ensure safe publication of the value between threads, and must not invoke
 * {@link #setEncodeBlanks(boolean)} after initial setup.
 * </p>
 *
 * @see <a href="http://www.ietf.org/rfc/rfc1522.txt">MIME (Multipurpose Internet Mail Extensions) Part Two: Message
 *          Header Extensions for Non-ASCII Text</a>
 *
 * @since 1.3
 */
public class QCodec extends RFC1522Codec implements StringEncoder, StringDecoder {
    /**
     * The default Charset used for string decoding and encoding.
     */
    private final Charset charset;

    /**
     * BitSet of printable characters as defined in RFC 1522.
     */
    private static final BitSet PRINTABLE_CHARS = new BitSet(256);
    // Static initializer for printable chars collection
    static {
        // alpha characters
        PRINTABLE_CHARS.set(' ');
        PRINTABLE_CHARS.set('!');
        PRINTABLE_CHARS.set('"');
        PRINTABLE_CHARS.set('#');
        PRINTABLE_CHARS.set('$');
        PRINTABLE_CHARS.set('%');
        PRINTABLE_CHARS.set('&');
        PRINTABLE_CHARS.set('\'');
        PRINTABLE_CHARS.set('(');
        PRINTABLE_CHARS.set(')');
        PRINTABLE_CHARS.set('*');
        PRINTABLE_CHARS.set('+');
        PRINTABLE_CHARS.set(',');
        PRINTABLE_CHARS.set('-');
        PRINTABLE_CHARS.set('.');
        PRINTABLE_CHARS.set('/');
        for (int i = '0'; i <= '9'; i++) {
            PRINTABLE_CHARS.set(i);
        }
        PRINTABLE_CHARS.set(':');
        PRINTABLE_CHARS.set(';');
        PRINTABLE_CHARS.set('<');
        PRINTABLE_CHARS.set('>');
        PRINTABLE_CHARS.set('@');
        for (int i = 'A'; i <= 'Z'; i++) {
            PRINTABLE_CHARS.set(i);
        }
        PRINTABLE_CHARS.set('[');
        PRINTABLE_CHARS.set('\\');
        PRINTABLE_CHARS.set(']');
        PRINTABLE_CHARS.set('^');
        PRINTABLE_CHARS.set('`');
        for (int i = 'a'; i <= 'z'; i++) {
            PRINTABLE_CHARS.set(i);
        }
        PRINTABLE_CHARS.set('{');
        PRINTABLE_CHARS.set('|');
        PRINTABLE_CHARS.set('}');
        PRINTABLE_CHARS.set('~');
    }

    private static final byte SPACE = 32;

    private static final byte UNDERSCORE = 95;

    private boolean encodeBlanks = false;

    /**
     * Default constructor.
     */
    public QCodec() {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Constructor which allows for the selection of a default Charset.
     *
     * @param charset
     *            the default string Charset to use.
     *
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @since 1.7
     */
    public QCodec(final Charset charset) {
        super();
        this.charset = charset;
    }

    /**
     * Constructor which allows for the selection of a default Charset.
     *
     * @param charsetName
     *            the Charset to use.
     * @throws java.nio.charset.UnsupportedCharsetException
     *             If the named Charset is unavailable
     * @since 1.7 throws UnsupportedCharsetException if the named Charset is unavailable
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     */
    public QCodec(final String charsetName) {
        this(Charset.forName(charsetName));
    }

    @Override
    protected String getEncoding() {
        return "Q";
    }

    @Override
    protected byte[] doEncoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final byte[] data = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
        if (this.encodeBlanks) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == SPACE) {
                    data[i] = UNDERSCORE;
                }
            }
        }
        return data;
    }

    @Override
    protected byte[] doDecoding(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        boolean hasUnderscores = false;
        for (final byte b : bytes) {
            if (b == UNDERSCORE) {
                hasUnderscores = true;
                break;
            }
        }
        if (hasUnderscores) {
            final byte[] tmp = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                final byte b = bytes[i];
                if (b != UNDERSCORE) {
                    tmp[i] = b;
                } else {
                    tmp[i] = SPACE;
                }
            }
            return QuotedPrintableCodec.decodeQuotedPrintable(tmp);
        }
        return QuotedPrintableCodec.decodeQuotedPrintable(bytes);
    }

    /**
     * Encodes a string into its quoted-printable form using the specified Charset. Unsafe characters are escaped.
     *
     * @param sourceStr
     *            string to convert to quoted-printable form
     * @param sourceCharset
     *            the Charset for sourceStr
     * @return quoted-printable string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     * @since 1.7
     */
    public String encode(final String sourceStr, final Charset sourceCharset) throws EncoderException {
        if (sourceStr == null) {
            return null;
        }
        return encodeText(sourceStr, sourceCharset);
    }

    /**
     * Encodes a string into its quoted-printable form using the specified Charset. Unsafe characters are escaped.
     *
     * @param sourceStr
     *            string to convert to quoted-printable form
     * @param sourceCharset
     *            the Charset for sourceStr
     * @return quoted-printable string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    public String encode(final String sourceStr, final String sourceCharset) throws EncoderException {
        if (sourceStr == null) {
            return null;
        }
        try {
            return encodeText(sourceStr, sourceCharset);
        } catch (final UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }

    /**
     * Encodes a string into its quoted-printable form using the default Charset. Unsafe characters are escaped.
     *
     * @param sourceStr
     *            string to convert to quoted-printable form
     * @return quoted-printable string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    @Override
    public String encode(final String sourceStr) throws EncoderException {
        if (sourceStr == null) {
            return null;
        }
        return encode(sourceStr, getCharset());
    }

    /**
     * Decodes a quoted-printable string into its original form. Escaped characters are converted back to their original
     * representation.
     *
     * @param str
     *            quoted-printable string to convert into its original form
     * @return original string
     * @throws DecoderException
     *             A decoder exception is thrown if a failure condition is encountered during the decode process.
     */
    @Override
    public String decode(final String str) throws DecoderException {
        if (str == null) {
            return null;
        }
        try {
            return decodeText(str);
        } catch (final UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }

    /**
     * Encodes an object into its quoted-printable form using the default Charset. Unsafe characters are escaped.
     *
     * @param obj
     *            object to convert to quoted-printable form
     * @return quoted-printable object
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return encode((String) obj);
        } else {
            throw new EncoderException("Objects of type " +
                  obj.getClass().getName() +
                  " cannot be encoded using Q codec");
        }
    }

    /**
     * Decodes a quoted-printable object into its original form. Escaped characters are converted back to their original
     * representation.
     *
     * @param obj
     *            quoted-printable object to convert into its original form
     * @return original object
     * @throws DecoderException
     *             Thrown if the argument is not a {@code String}. Thrown if a failure condition is encountered
     *             during the decode process.
     */
    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return decode((String) obj);
        } else {
            throw new DecoderException("Objects of type " +
                  obj.getClass().getName() +
                  " cannot be decoded using Q codec");
        }
    }

    /**
     * Gets the default Charset name used for string decoding and encoding.
     *
     * @return the default Charset name
     * @since 1.7
     */
    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Gets the default Charset name used for string decoding and encoding.
     *
     * @return the default Charset name
     */
    public String getDefaultCharset() {
        return this.charset.name();
    }

    /**
     * Tests if optional transformation of SPACE characters is to be used
     *
     * @return {@code true} if SPACE characters are to be transformed, {@code false} otherwise
     */
    public boolean isEncodeBlanks() {
        return this.encodeBlanks;
    }

    /**
     * Defines whether optional transformation of SPACE characters is to be used
     *
     * @param b
     *            {@code true} if SPACE characters are to be transformed, {@code false} otherwise
     */
    public void setEncodeBlanks(final boolean b) {
        this.encodeBlanks = b;
    }
}
