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
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;

/**
 * Identical to the Base64 encoding defined by <a href="http://www.ietf.org/rfc/rfc1521.txt">RFC 1521</a>
 * and allows a character set to be specified.
 * <p>
 * <a href="http://www.ietf.org/rfc/rfc1522.txt">RFC 1522</a> describes techniques to allow the encoding of non-ASCII
 * text in various portions of a RFC 822 [2] message header, in a manner which is unlikely to confuse existing message
 * handling software.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @see <a href="http://www.ietf.org/rfc/rfc1522.txt">MIME (Multipurpose Internet Mail Extensions) Part Two: Message
 *          Header Extensions for Non-ASCII Text</a>
 *
 * @since 1.3
 */
public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder {

    /**
     * The default decoding policy.
     */
    private static final CodecPolicy DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;

    /**
     * If true then decoding should throw an exception for impossible combinations of bits at the
     * end of the byte input. The default is to decode as much of them as possible.
     */
    private final CodecPolicy decodingPolicy;

    /**
     * Default constructor.
     */
    public BCodec() {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Constructor which allows for the selection of a default Charset
     *
     * @param charset
     *            the default string Charset to use.
     *
     * @see Charset
     * @since 1.7
     */
    public BCodec(final Charset charset) {
        this(charset, DECODING_POLICY_DEFAULT);
    }

    /**
     * Constructor which allows for the selection of a default Charset.
     *
     * @param charset
     *            the default string Charset to use.
     * @param decodingPolicy The decoding policy.
     *
     * @see Charset
     * @since 1.15
     */
    public BCodec(final Charset charset, final CodecPolicy decodingPolicy) {
        super(charset);
        this.decodingPolicy = decodingPolicy;
    }

    /**
     * Constructor which allows for the selection of a default Charset
     *
     * @param charsetName
     *            the default Charset to use.
     * @throws java.nio.charset.UnsupportedCharsetException
     *             If the named Charset is unavailable
     * @since 1.7 throws UnsupportedCharsetException if the named Charset is unavailable
     * @see Charset
     */
    public BCodec(final String charsetName) {
        this(Charset.forName(charsetName));
    }

    /**
     * Decodes a Base64 object into its original form. Escaped characters are converted back to their original
     * representation.
     *
     * @param value
     *            Base64 object to convert into its original form
     * @return original object
     * @throws DecoderException
     *             Thrown if the argument is not a {@code String}. Thrown if a failure condition is encountered
     *             during the decode process.
     */
    @Override
    public Object decode(final Object value) throws DecoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return decode((String) value);
        }
        throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
    }

    /**
     * Decodes a Base64 string into its original form. Escaped characters are converted back to their original
     * representation.
     *
     * @param value
     *            Base64 string to convert into its original form
     * @return original string
     * @throws DecoderException
     *             A decoder exception is thrown if a failure condition is encountered during the decode process.
     */
    @Override
    public String decode(final String value) throws DecoderException {
        try {
            return decodeText(value);
        } catch (final UnsupportedEncodingException | IllegalArgumentException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }

    @Override
    protected byte[] doDecoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new Base64(0, BaseNCodec.getChunkSeparator(), false, decodingPolicy).decode(bytes);
    }

    @Override
    protected byte[] doEncoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.encodeBase64(bytes);
    }

    /**
     * Encodes an object into its Base64 form using the default Charset. Unsafe characters are escaped.
     *
     * @param value
     *            object to convert to Base64 form
     * @return Base64 object
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    @Override
    public Object encode(final Object value) throws EncoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return encode((String) value);
        }
        throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
    }

    /**
     * Encodes a string into its Base64 form using the default Charset. Unsafe characters are escaped.
     *
     * @param strSource
     *            string to convert to Base64 form
     * @return Base64 string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    @Override
    public String encode(final String strSource) throws EncoderException {
        return encode(strSource, getCharset());
    }

    /**
     * Encodes a string into its Base64 form using the specified Charset. Unsafe characters are escaped.
     *
     * @param strSource
     *            string to convert to Base64 form
     * @param sourceCharset
     *            the Charset for {@code value}
     * @return Base64 string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     * @since 1.7
     */
    public String encode(final String strSource, final Charset sourceCharset) throws EncoderException {
        return encodeText(strSource, sourceCharset);
    }

    /**
     * Encodes a string into its Base64 form using the specified Charset. Unsafe characters are escaped.
     *
     * @param strSource
     *            string to convert to Base64 form
     * @param sourceCharset
     *            the Charset for {@code value}
     * @return Base64 string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     */
    public String encode(final String strSource, final String sourceCharset) throws EncoderException {
        try {
            return encodeText(strSource, sourceCharset);
        } catch (final UnsupportedCharsetException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }

    @Override
    protected String getEncoding() {
        return "B";
    }

    /**
     * Returns true if decoding behavior is strict. Decoding will raise a
     * {@link DecoderException} if trailing bits are not part of a valid Base64 encoding.
     *
     * <p>The default is false for lenient encoding. Decoding will compose trailing bits
     * into 8-bit bytes and discard the remainder.
     *
     * @return true if using strict decoding
     * @since 1.15
     */
    public boolean isStrictDecoding() {
        return decodingPolicy == CodecPolicy.STRICT;
    }
}
