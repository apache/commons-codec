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
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Implements the 'www-form-urlencoded' encoding scheme, also misleadingly known as URL encoding.
 * <p>
 * This codec is meant to be a replacement for standard Java classes {@link java.net.URLEncoder} and
 * {@link java.net.URLDecoder} on older Java platforms, as these classes in Java versions below
 * 1.4 rely on the platform's default charset encoding.
 * </p>
 * <p>
 * This class is thread-safe as of 1.11
 * </p>
 *
 * @see <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">Chapter 17.13.4 Form content types</a>
 *           of the <a href="http://www.w3.org/TR/html4/">HTML 4.01 Specification</a>
 *
 * @since 1.2
 */
public class URLCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {

    /**
     * The default charset used for string decoding and encoding.
     *
     * @deprecated TODO: This field will be changed to a private final Charset in 2.0. (CODEC-126)
     */
    @Deprecated
    protected volatile String charset; // added volatile: see CODEC-232

    /**
     * Release 1.5 made this field final.
     */
    protected static final byte ESCAPE_CHAR = '%';

    /**
     * BitSet of www-form-url safe characters.
     * This is a copy of the internal BitSet which is now used for the conversion.
     * Changes to this field are ignored.
     * @deprecated 1.11 Will be removed in 2.0 (CODEC-230)
     */
    @Deprecated
    protected static final BitSet WWW_FORM_URL;

    private static final BitSet WWW_FORM_URL_SAFE = new BitSet(256);

    // Static initializer for www_form_url
    static {
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            WWW_FORM_URL_SAFE.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            WWW_FORM_URL_SAFE.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            WWW_FORM_URL_SAFE.set(i);
        }
        // special chars
        WWW_FORM_URL_SAFE.set('-');
        WWW_FORM_URL_SAFE.set('_');
        WWW_FORM_URL_SAFE.set('.');
        WWW_FORM_URL_SAFE.set('*');
        // blank to be replaced with +
        WWW_FORM_URL_SAFE.set(' ');

        // Create a copy in case anyone (ab)uses it
        WWW_FORM_URL = (BitSet) WWW_FORM_URL_SAFE.clone();
    }


    /**
     * Default constructor.
     */
    public URLCodec() {
        this(CharEncoding.UTF_8);
    }

    /**
     * Constructor which allows for the selection of a default charset.
     *
     * @param charset the default string charset to use.
     */
    public URLCodec(final String charset) {
        this.charset = charset;
    }

    /**
     * Encodes an array of bytes into an array of URL safe 7-bit characters. Unsafe characters are escaped.
     *
     * @param urlsafe
     *            bitset of characters deemed URL safe
     * @param bytes
     *            array of bytes to convert to URL safe characters
     * @return array of bytes containing URL safe characters
     */
    public static final byte[] encodeUrl(BitSet urlsafe, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (urlsafe == null) {
            urlsafe = WWW_FORM_URL_SAFE;
        }

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (final byte c : bytes) {
            int b = c;
            if (b < 0) {
                b = 256 + b;
            }
            if (urlsafe.get(b)) {
                if (b == ' ') {
                    b = '+';
                }
                buffer.write(b);
            } else {
                buffer.write(ESCAPE_CHAR);
                final char hex1 = Utils.hexDigit(b >> 4);
                final char hex2 = Utils.hexDigit(b);
                buffer.write(hex1);
                buffer.write(hex2);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Decodes an array of URL safe 7-bit characters into an array of original bytes. Escaped characters are converted
     * back to their original representation.
     *
     * @param bytes
     *            array of URL safe characters
     * @return array of original bytes
     * @throws DecoderException
     *             Thrown if URL decoding is unsuccessful
     */
    public static final byte[] decodeUrl(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            final int b = bytes[i];
            if (b == '+') {
                buffer.write(' ');
            } else if (b == ESCAPE_CHAR) {
                try {
                    final int u = Utils.digit16(bytes[++i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.write((char) ((u << 4) + l));
                } catch (final ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid URL encoding: ", e);
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Encodes an array of bytes into an array of URL safe 7-bit characters. Unsafe characters are escaped.
     *
     * @param bytes
     *            array of bytes to convert to URL safe characters
     * @return array of bytes containing URL safe characters
     */
    @Override
    public byte[] encode(final byte[] bytes) {
        return encodeUrl(WWW_FORM_URL_SAFE, bytes);
    }


    /**
     * Decodes an array of URL safe 7-bit characters into an array of original bytes. Escaped characters are converted
     * back to their original representation.
     *
     * @param bytes
     *            array of URL safe characters
     * @return array of original bytes
     * @throws DecoderException
     *             Thrown if URL decoding is unsuccessful
     */
    @Override
    public byte[] decode(final byte[] bytes) throws DecoderException {
        return decodeUrl(bytes);
    }

    /**
     * Encodes a string into its URL safe form using the specified string charset. Unsafe characters are escaped.
     *
     * @param str
     *            string to convert to a URL safe form
     * @param charsetName
     *            the charset for str
     * @return URL safe string
     * @throws UnsupportedEncodingException
     *             Thrown if charset is not supported
     */
    public String encode(final String str, final String charsetName) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(encode(str.getBytes(charsetName)));
    }

    /**
     * Encodes a string into its URL safe form using the default string charset. Unsafe characters are escaped.
     *
     * @param str
     *            string to convert to a URL safe form
     * @return URL safe string
     * @throws EncoderException
     *             Thrown if URL encoding is unsuccessful
     *
     * @see #getDefaultCharset()
     */
    @Override
    public String encode(final String str) throws EncoderException {
        if (str == null) {
            return null;
        }
        try {
            return encode(str, getDefaultCharset());
        } catch (final UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }


    /**
     * Decodes a URL safe string into its original form using the specified encoding. Escaped characters are converted
     * back to their original representation.
     *
     * @param str
     *            URL safe string to convert into its original form
     * @param charsetName
     *            the original string charset
     * @return original string
     * @throws DecoderException
     *             Thrown if URL decoding is unsuccessful
     * @throws UnsupportedEncodingException
     *             Thrown if charset is not supported
     */
    public String decode(final String str, final String charsetName)
            throws DecoderException, UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return new String(decode(StringUtils.getBytesUsAscii(str)), charsetName);
    }

    /**
     * Decodes a URL safe string into its original form using the default string charset. Escaped characters are
     * converted back to their original representation.
     *
     * @param str
     *            URL safe string to convert into its original form
     * @return original string
     * @throws DecoderException
     *             Thrown if URL decoding is unsuccessful
     * @see #getDefaultCharset()
     */
    @Override
    public String decode(final String str) throws DecoderException {
        if (str == null) {
            return null;
        }
        try {
            return decode(str, getDefaultCharset());
        } catch (final UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }

    /**
     * Encodes an object into its URL safe form. Unsafe characters are escaped.
     *
     * @param obj
     *            string to convert to a URL safe form
     * @return URL safe object
     * @throws EncoderException
     *             Thrown if URL encoding is not applicable to objects of this type or if encoding is unsuccessful
     */
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return encode((byte[])obj);
        }
        if (obj instanceof String) {
            return encode((String)obj);
        }
        throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be URL encoded");
    }

    /**
     * Decodes a URL safe object into its original form. Escaped characters are converted back to their original
     * representation.
     *
     * @param obj
     *            URL safe object to convert into its original form
     * @return original object
     * @throws DecoderException
     *             Thrown if the argument is not a {@code String} or {@code byte[]}. Thrown if a failure
     *             condition is encountered during the decode process.
     */
    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return decode((byte[]) obj);
        }
        if (obj instanceof String) {
            return decode((String) obj);
        }
        throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be URL decoded");
    }

    /**
     * The default charset used for string decoding and encoding.
     *
     * @return the default string charset.
     */
    public String getDefaultCharset() {
        return this.charset;
    }

    /**
     * The {@code String} encoding used for decoding and encoding.
     *
     * @return Returns the encoding.
     *
     * @deprecated Use {@link #getDefaultCharset()}, will be removed in 2.0.
     */
    @Deprecated
    public String getEncoding() {
        return this.charset;
    }

}
