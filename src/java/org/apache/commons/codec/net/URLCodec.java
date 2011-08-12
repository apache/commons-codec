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
 * <p>Implements the 'www-form-urlencoded' encoding scheme, 
 * also misleadingly known as URL encoding.</p>
 *  
 * <p>For more detailed information please refer to 
 * <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">
 * Chapter 17.13.4 'Form content types'</a> of the 
 * <a href="http://www.w3.org/TR/html4/">HTML 4.01 Specification<a></p>
 * 
 * <p> 
 * This codec is meant to be a replacement for standard Java classes
 * {@link java.net.URLEncoder} and {@link java.net.URLDecoder} 
 * on older Java platforms, as these classes in Java versions below 
 * 1.4 rely on the platform's default charset encoding.
 * </p>
 * 
 * @author Apache Software Foundation
 * @since 1.2
 * @version $Id$
 */
public abstract class URLCodec {
    
    /**
     * Radix used in encoding and decoding.
     */
    static final int RADIX = 16;
    
    /**
     * The default charset used for string decoding and encoding.
     */
    protected final String charset;
    
    /**
     * Release 1.5 made this field final.
     */
    protected static final byte ESCAPE_CHAR = '%';
    /**
     * BitSet of www-form-url safe characters.
     */
    protected static final BitSet WWW_FORM_URL = new BitSet(256);
    
    // Static initializer for www_form_url
    static {
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            WWW_FORM_URL.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            WWW_FORM_URL.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            WWW_FORM_URL.set(i);
        }
        // special chars
        WWW_FORM_URL.set('-');
        WWW_FORM_URL.set('_');
        WWW_FORM_URL.set('.');
        WWW_FORM_URL.set('*');
        // blank to be replaced with +
        WWW_FORM_URL.set(' ');
    }


    /**
     * Default constructor.
     */
    public URLCodec() {
        this(CharEncoding.UTF_8);
    }

    /**
     * Constructor which allows for the selection of a default charset
     * 
     * @param charset the default string charset to use.
     */
    public URLCodec(String charset) {
        super();
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
    public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (urlsafe == null) {
            urlsafe = WWW_FORM_URL;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (byte c : bytes) {
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
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, RADIX));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
                buffer.write(hex1);
                buffer.write(hex2);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Decodes an array of URL safe 7-bit characters into an array of 
     * original bytes. Escaped characters are converted back to their 
     * original representation.
     *
     * @param bytes array of URL safe characters
     * @return array of original bytes 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     */
    public static final byte[] decodeUrl(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b == '+') {
                buffer.write(' ');
            } else if (b == ESCAPE_CHAR) {
                try {
                    int u = Utils.digit16(bytes[++i]);
                    int l = Utils.digit16(bytes[++i]);
                    buffer.write((char) ((u << 4) + l));
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid URL encoding: ", e);
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Encodes an array of bytes into an array of URL safe 7-bit 
     * characters. Unsafe characters are escaped.
     *
     * @param bytes array of bytes to convert to URL safe characters
     * @return array of bytes containing URL safe characters
     */
    public byte[] encode(byte[] bytes) {
        return encodeUrl(WWW_FORM_URL, bytes);
    }


    /**
     * Decodes an array of URL safe 7-bit characters into an array of 
     * original bytes. Escaped characters are converted back to their 
     * original representation.
     *
     * @param bytes array of URL safe characters
     * @return array of original bytes 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     */
    public byte[] decode(byte[] bytes) throws DecoderException {
        return decodeUrl(bytes);
    }

    /**
     * The default charset used for string decoding and encoding.
     *
     * @return the default string charset.
     */
    public String getDefaultCharset() {
        return this.charset;
    }

}
