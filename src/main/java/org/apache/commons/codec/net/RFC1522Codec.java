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

package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Implements methods common to all codecs defined in RFC 1522.
 * <p>
 * <a href="http://www.ietf.org/rfc/rfc1522.txt">RFC 1522</a> describes techniques to allow the
 * encoding of non-ASCII text in various portions of a RFC 822 [2] message header, in a manner which
 * is unlikely to confuse existing message handling software.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @see <a href="http://www.ietf.org/rfc/rfc1522.txt">MIME (Multipurpose Internet Mail Extensions) Part Two:
 *          Message Header Extensions for Non-ASCII Text</a>
 * @since 1.3
 */
abstract class RFC1522Codec {

    /** Separator. */
    protected static final char SEP = '?';

    /** Prefix. */
    protected static final String POSTFIX = "?=";

    /** Postfix. */
    protected static final String PREFIX = "=?";

    /**
     * The default Charset used for string decoding and encoding.
     */
    protected final Charset charset;

    RFC1522Codec(final Charset charset) {
        this.charset = Objects.requireNonNull(charset, "charset");
    }

    /**
     * Applies an RFC 1522 compliant decoding scheme to the given string of text.
     * <p>
     * This method processes the "encoded-word" header common to all the RFC 1522 codecs and then invokes
     * {@link #doDecoding(byte[])}  method of a concrete class to perform the specific decoding.
     * </p>
     *
     * @param text
     *            a string to decode
     * @return A new decoded String or {@code null} if the input is {@code null}.
     * @throws DecoderException
     *             thrown if there is an error condition during the decoding process.
     * @throws UnsupportedEncodingException
     *             thrown if charset specified in the "encoded-word" header is not supported
     */
    protected String decodeText(final String text) throws DecoderException, UnsupportedEncodingException {
        if (text == null) {
            return null;
        }
        if (!text.startsWith(PREFIX) || !text.endsWith(POSTFIX)) {
            throw new DecoderException("RFC 1522 violation: malformed encoded content");
        }
        final int terminator = text.length() - 2;
        int from = 2;
        int to = text.indexOf(SEP, from);
        if (to == terminator) {
            throw new DecoderException("RFC 1522 violation: charset token not found");
        }
        final String charset = text.substring(from, to);
        if (charset.isEmpty()) {
            throw new DecoderException("RFC 1522 violation: charset not specified");
        }
        from = to + 1;
        to = text.indexOf(SEP, from);
        if (to == terminator) {
            throw new DecoderException("RFC 1522 violation: encoding token not found");
        }
        final String encoding = text.substring(from, to);
        if (!getEncoding().equalsIgnoreCase(encoding)) {
            throw new DecoderException("This codec cannot decode " + encoding + " encoded content");
        }
        from = to + 1;
        to = text.indexOf(SEP, from);
        byte[] data = StringUtils.getBytesUsAscii(text.substring(from, to));
        data = doDecoding(data);
        return new String(data, charset);
    }

    /**
     * Decodes an array of bytes using the defined encoding scheme.
     *
     * @param bytes
     *            Data to be decoded
     * @return a byte array that contains decoded data
     * @throws DecoderException
     *             A decoder exception is thrown if a Decoder encounters a failure condition during the decode process.
     */
    protected abstract byte[] doDecoding(byte[] bytes) throws DecoderException;

    /**
     * Encodes an array of bytes using the defined encoding scheme.
     *
     * @param bytes
     *            Data to be encoded
     * @return A byte array containing the encoded data
     * @throws EncoderException
     *             thrown if the Encoder encounters a failure condition during the encoding process.
     */
    protected abstract byte[] doEncoding(byte[] bytes) throws EncoderException;

    /**
     * Applies an RFC 1522 compliant encoding scheme to the given string of text with the given charset.
     * <p>
     * This method constructs the "encoded-word" header common to all the RFC 1522 codecs and then invokes
     * {@link #doEncoding(byte[])}  method of a concrete class to perform the specific encoding.
     * </p>
     *
     * @param text
     *            a string to encode
     * @param charset
     *            a charset to be used
     * @return RFC 1522 compliant "encoded-word"
     * @throws EncoderException
     *             thrown if there is an error condition during the Encoding process.
     * @see Charset
     */
    protected String encodeText(final String text, final Charset charset) throws EncoderException {
        if (text == null) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(PREFIX);
        buffer.append(charset);
        buffer.append(SEP);
        buffer.append(getEncoding());
        buffer.append(SEP);
        buffer.append(StringUtils.newStringUsAscii(doEncoding(text.getBytes(charset))));
        buffer.append(POSTFIX);
        return buffer.toString();
    }

    /**
     * Applies an RFC 1522 compliant encoding scheme to the given string of text with the given charset.
     * <p>
     * This method constructs the "encoded-word" header common to all the RFC 1522 codecs and then invokes
     * {@link #doEncoding(byte[])}  method of a concrete class to perform the specific encoding.
     * </p>
     *
     * @param text
     *            a string to encode
     * @param charsetName
     *            the charset to use
     * @return RFC 1522 compliant "encoded-word"
     * @throws EncoderException
     *             thrown if there is an error condition during the Encoding process.
     * @throws UnsupportedCharsetException
     *             if charset is not available
     * @see Charset
     */
    protected String encodeText(final String text, final String charsetName) throws EncoderException {
        if (text == null) {
            // Don't attempt charsetName conversion.
            return null;
        }
        return encodeText(text, Charset.forName(charsetName));
    }

    /**
     * Gets the default Charset name used for string decoding and encoding.
     *
     * @return the default Charset name
     * @since 1.7
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gets the default Charset name used for string decoding and encoding.
     *
     * @return the default Charset name
     */
    public String getDefaultCharset() {
        return charset.name();
    }

    /**
     * Returns the codec name (referred to as encoding in the RFC 1522).
     *
     * @return name of the codec.
     */
    protected abstract String getEncoding();
}
