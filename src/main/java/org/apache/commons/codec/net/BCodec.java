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
 * Identical to the Base64 encoding defined by <a href="https://www.ietf.org/rfc/rfc1521.txt">RFC 1521</a>
 * and allows a character set to be specified.
 * <p>
 * <a href="https://www.ietf.org/rfc/rfc1522.txt">RFC 1522</a> describes techniques to allow the encoding of non-ASCII
 * text in various portions of a RFC 822 [2] message header, in a manner which is unlikely to confuse existing message
 * handling software.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * <p>
 * Decoding is lenient by default: the Base64 payload can contain ignored characters, noncanonical padding or trailing bits, and data after padding.
 * Different encoded words can therefore decode to the same text. To require a canonical Base64 payload, select {@link CodecPolicy#STRICT}:
 * </p>
 *
 * <pre>
 * BCodec codec = new BCodec(StandardCharsets.UTF_8, CodecPolicy.STRICT);
 * </pre>
 *
 * <p>
 * Strict decoding requires the standard Base64 alphabet, padding for partial blocks, and no whitespace within the payload. Invalid payloads cause a
 * {@link DecoderException}. This validates the Base64 payload only; it does not establish a unique representation of the complete encoded word or message
 * header, including its charset label. Applications comparing header values for security decisions must use a consistent representation, and signature
 * verification must follow the signing protocol.
 * </p>
 *
 * @see <a href="https://www.ietf.org/rfc/rfc1522.txt">MIME (Multipurpose Internet Mail Extensions) Part Two: Message
 *          Header Extensions for Non-ASCII Text</a>
 *
 * @since 1.3
 */
public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder {

    /**
     * The default decoding policy is lenient.
     */
    private static final CodecPolicy DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;

    /**
     * Decoding policy for the Base64 payload. The default is lenient; strict decoding requires a canonical payload.
     */
    private final CodecPolicy decodingPolicy;

    /**
     * Constructs a new instance.
     */
    public BCodec() {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Constructs a new instance for the selection of a default Charset.
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
     * Constructs a new instance for the selection of a default Charset.
     *
     * <p>
     * Use {@link CodecPolicy#STRICT} to require canonical standard Base64 payloads. The other constructors use {@link CodecPolicy#LENIENT}.
     * This policy applies to the Base64 payload, not the complete encoded word; see the class documentation.
     * </p>
     *
     * @param charset
     *            the default string Charset to use.
     * @param decodingPolicy The decoding policy.
     * @see Charset
     * @since 1.15
     */
    public BCodec(final Charset charset, final CodecPolicy decodingPolicy) {
        super(charset);
        this.decodingPolicy = decodingPolicy;
    }

    /**
     * Constructs a new instance for the selection of a default Charset.
     *
     * @param charsetName
     *            the default Charset to use.
     * @throws java.nio.charset.UnsupportedCharsetException
     *             If the named Charset is unavailable.
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
     * <p>
     * Uses the decoding policy selected at construction. The default is lenient and does not require a canonical Base64 payload. Use
     * {@link #BCodec(Charset, CodecPolicy)} with {@link CodecPolicy#STRICT} for canonical payload validation.
     * </p>
     *
     * @param value
     *            Base64 object to convert into its original form.
     * @return original object.
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
     * <p>
     * Uses the decoding policy selected at construction. The default is lenient and does not require a canonical Base64 payload. Use
     * {@link #BCodec(Charset, CodecPolicy)} with {@link CodecPolicy#STRICT} for canonical payload validation.
     * </p>
     *
     * @param value
     *            Base64 string to convert into its original form.
     * @return original string.
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

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException Thrown when a problem is detected processing data.
     */
    @Override
    protected byte[] doDecoding(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        // @formatter:off
        try {
            return Base64.builder()
                    .setLineLength(0)
                    .setLineSeparator(BaseNCodec.getChunkSeparator())
                    .setUrlSafe(false)
                    .setDecodingPolicy(decodingPolicy)
                    .get()
                    .decode(bytes);
        } catch (final IllegalArgumentException e) {
            throw new DecoderException(e.getMessage(), e);
        }
        // @formatter:on
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
     *            object to convert to Base64 form.
     * @return Base64 object.
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
     *            string to convert to Base64 form.
     * @return Base64 string.
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
     *            string to convert to Base64 form.
     * @param sourceCharset
     *            the Charset for {@code value}.
     * @return Base64 string.
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
     *            string to convert to Base64 form.
     * @param sourceCharset
     *            the Charset for {@code value}.
     * @return Base64 string.
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
     * Tests whether decoding requires a canonical Base64 payload.
     *
     * <p>
     * Strict decoding raises {@link DecoderException} for a noncanonical Base64 payload, including invalid alphabet characters, padding, or trailing bits.
     * The default is lenient. This policy does not establish a canonical representation of the complete encoded word.
     * </p>
     *
     * @return true if using strict decoding.
     * @since 1.15
     */
    public boolean isStrictDecoding() {
        return decodingPolicy == CodecPolicy.STRICT;
    }
}
