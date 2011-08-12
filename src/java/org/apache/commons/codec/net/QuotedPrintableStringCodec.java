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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

public class QuotedPrintableStringCodec extends QuotedPrintableCodec implements StringEncoder, StringDecoder {

    public QuotedPrintableStringCodec() {
        super();
    }

    public QuotedPrintableStringCodec(String charset) {
        super(charset);
    }

    /**
     * Decodes a quoted-printable string into its original form using the specified string charset. Escaped characters are converted back to
     * their original representation.
     * 
     * @param pString
     *            quoted-printable string to convert into its original form
     * @param charsetName
     *            the original string charset
     * @return original string
     * @throws DecoderException
     *             Thrown if quoted-printable decoding is unsuccessful
     * @throws UnsupportedEncodingException
     *             Thrown if charset is not supported
     */
    public String decode(String pString, String charsetName) throws DecoderException, UnsupportedEncodingException {
        if (pString == null) {
            return null;
        }
        return new String(decode(StringUtils.getBytesUsAscii(pString)), charsetName);
    }

    /**
     * Encodes a string into its quoted-printable form using the specified charset. Unsafe characters are escaped.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in RFC 1521 and is
     * suitable for encoding binary data and unformatted text.
     * </p>
     * 
     * @param pString
     *            string to convert to quoted-printable form
     * @param charsetName
     *            the charset for pString
     * @return quoted-printable string
     * 
     * @throws UnsupportedEncodingException
     *             Thrown if the charset is not supported
     */
    public String encode(String pString, String charsetName) throws UnsupportedEncodingException {
        if (pString == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(encode(pString.getBytes(charsetName)));
    }

    /**
     * Encodes a string into its quoted-printable form using the default string charset. Unsafe characters are escaped.
     * 
     * <p>
     * This function implements a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in RFC 1521 and is
     * suitable for encoding binary data.
     * </p>
     * 
     * @param pString
     *            string to convert to quoted-printable form
     * @return quoted-printable string
     * 
     * @throws EncoderException
     *             Thrown if quoted-printable encoding is unsuccessful
     * 
     * @see #getDefaultCharset()
     */
    public String encode(String pString) throws EncoderException {
        if (pString == null) {
            return null;
        }
        try {
            return encode(pString, getDefaultCharset());
        } catch (UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }

    /**
     * Decodes a quoted-printable string into its original form using the default string charset. Escaped characters are converted back to
     * their original representation.
     * 
     * @param pString
     *            quoted-printable string to convert into its original form
     * @return original string
     * @throws DecoderException
     *             Thrown if quoted-printable decoding is unsuccessful. Thrown if charset is not supported.
     * @see #getDefaultCharset()
     */
    public String decode(String pString) throws DecoderException {
        if (pString == null) {
            return null;
        }
        try {
            return decode(pString, getDefaultCharset());
        } catch (UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
}