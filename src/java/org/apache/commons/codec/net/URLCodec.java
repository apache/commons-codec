/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//codec/src/java/org/apache/commons/codec/net/URLCodec.java,v 1.3 2003/07/31 20:09:21 tobrien Exp $
 * $Revision: 1.3 $
 * $Date: 2003/07/31 20:09:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

/**
 * URLCodec implements 'www-form-urlencoded' encoding scheme, 
 * also misleadingly known as URL encoding. 
 * For more detailed information please refer to 
 * <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">
 * Chapter 17.13.4 'Form content types'</a> of the 
 * <a href="http://www.w3.org/TR/html4/">HTML 4.01 Specification<a>
 * 
 * <p> 
 * This codec is meant to be a replacement for standard Java classes
 * {@link java.net.URLEncoder} and {@link java.net.URLDecoder} 
 * on older Java platforms, as these classes in Java versions below 
 * 1.4 rely on the platform's default charset encoding.
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @version $Id: URLCodec.java,v 1.3 2003/07/31 20:09:21 tobrien Exp $
 */

public class URLCodec 
        implements BinaryEncoder, BinaryDecoder, 
                   StringEncoder, StringDecoder 
{
    
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
     * Default constructor
     */
    public URLCodec() {
        super();
    }

    /**
     * Converts an array of bytes into an array of URL safe 7-bit 
     * characters. Unsafe characters are escaped.
     *
     * @param urlsafe bitset of characters deemed URL safe
     * @param pArray array of bytes to convert to URL safe characters
     * @return array of bytes containing URL safe characters
     * @throws EncoderException Thrown if URL encoding is unsuccessful
     */
    public static final byte[] urlencode(BitSet urlsafe, byte[] pArray) 
        throws EncoderException
    {
        if (pArray == null) {
            return null;
        }
        if (urlsafe == null) {
            urlsafe = WWW_FORM_URL;
        }
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        for (int i = 0; i < pArray.length; i++) {
            int b = pArray[i];
            if (b >= 0 && urlsafe.get(b)) {
                if (b == ' ') {
                    b = '+';
                }
                buffer.write(b);
            } else {
                buffer.write('%');
                char hex1 = Character.toUpperCase(
                  Character.forDigit((b >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(
                  Character.forDigit(b & 0xF, 16));
                buffer.write(hex1);
                buffer.write(hex2);
            }
        }
        return buffer.toByteArray(); 
    }


    /**
     * Converts an array of URL safe 7-bit characters into an array of 
     * original bytes. Escaped characters are converted back to their 
     * original representation.
     *
     * @param pArray array of URL safe characters
     * @return array of original bytes 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     */
    public static final byte[] urldecode(byte[] pArray) 
         throws DecoderException
    {
        if (pArray == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        for (int i = 0; i < pArray.length; i++) {
            int b = pArray[i];
            if (b == '+') {
                buffer.write(' ');
            } else if (b == '%') {
                try {
                    int u = Character.digit((char)pArray[++i], 16);
                    int l = Character.digit((char)pArray[++i], 16);
                    if (u == -1 || l == -1) {
                        throw new DecoderException("Invalid URL encoding");
                    }
                    buffer.write((char)((u << 4) + l));
                } catch(ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid URL encoding");
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray(); 
    }


    /**
     * Converts an array of bytes into an array of URL safe 7-bit 
     * characters. Unsafe characters are escaped.
     *
     * @param pArray array of bytes to convert to URL safe characters
     * @return array of bytes containing URL safe characters
     * @throws EncoderException Thrown if URL encoding is unsuccessful
     */
    public byte[] encode(byte[] pArray) throws EncoderException {
        return urlencode(WWW_FORM_URL, pArray);
    }


    /**
     * Converts an array of URL safe 7-bit characters into an array of 
     * original bytes. Escaped characters are converted back to their 
     * original representation.
     *
     * @param pArray array of URL safe characters
     * @return array of original bytes 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     */
    public byte[] decode(byte[] pArray) throws DecoderException {
        return urldecode(pArray);
    }


    /**
     * Converts a string into its URL safe form using the specified
     * character set. Unsafe characters are escaped.
     *
     * @param pString string to convert to a URL safe form
     * @return URL safe string
     * @throws EncoderException Thrown if URL encoding is unsuccessful
     * @throws UnsupportedEncodingException Thrown if charset is not
     *                                      supported 
     */
    public String encode(String pString, String charset) 
        throws EncoderException, UnsupportedEncodingException  
    {
        if (pString == null) {
            return null;
        }
        return new String(encode(pString.getBytes(charset)), "US-ASCII");
    }


    /**
     * Converts a string into its URL safe form. Unsafe characters are 
     * escaped.
     *
     * @param pString string to convert to a URL safe form
     * @return URL safe string
     * @throws EncoderException Thrown if URL encoding is unsuccessful
     */
    public String encode(String pString) throws EncoderException {
        if (pString == null) {
            return null;
        }
        try {
            return new String(encode(pString.getBytes()), "US-ASCII");
        } catch(UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage());
        }
    }


    /**
     * Converts a URL safe string into its original form using the 
     * specified character set. Escaped characters are converted back 
     * to their original representation.
     *
     * @param pString URL safe string to convert into its original form
     * @return original string 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     * @throws UnsupportedEncodingException Thrown if charset is not
     *                                      supported 
     */
    public String decode(String pString, String charset) 
        throws DecoderException, UnsupportedEncodingException 
    {
        if (pString == null) {
            return null;
        }
        return new String(decode(pString.getBytes("US-ASCII")), charset);
    }


    /**
     * Converts a URL safe string into its original form. Escaped 
     * characters are converted back to their original representation.
     *
     * @param pString URL safe string to convert into its original form
     * @return original string 
     * @throws DecoderException Thrown if URL decoding is unsuccessful
     */
    public String decode(String pString) throws DecoderException {
        if (pString == null) {
            return null;
        }
        try {
            return new String(decode(pString.getBytes("US-ASCII")));
        } catch(UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage());
        }
    }


    /**
     * Converts an object into its URL safe form. Unsafe characters are 
     * escaped.
     *
     * @param pObject string to convert to a URL safe form
     * @return URL safe object
     * @throws EncoderException Thrown if URL encoding is not 
     *                          applicable to objects of this type or
     *                          if encoding is unsuccessful
     */
    public Object encode(Object pObject) throws EncoderException {
        if (pObject == null) {
            return null;
        } else if (pObject instanceof byte[]) {
            return encode((byte[])pObject);
        } else if (pObject instanceof String) {
            return encode((String)pObject);
        } else {
            throw new EncoderException("Objects of type " +
                pObject.getClass().getName() + " cannot be URL encoded"); 
              
        }
    }

    /**
     * Converts a URL safe object into its original form. Escaped 
     * characters are converted back to their original representation.
     *
     * @param pObject URL safe object to convert into its original form
     * @return original object 
     * @throws DecoderException Thrown if URL decoding is not 
     *                          applicable to objects of this type
     *                          if decoding is unsuccessful
     */
    public Object decode(Object pObject) throws DecoderException {
        if (pObject == null) {
            return null;
        } else if (pObject instanceof byte[]) {
            return decode((byte[])pObject);
        } else if (pObject instanceof String) {
            return decode((String)pObject);
        } else {
            throw new DecoderException("Objects of type " +
                pObject.getClass().getName() + " cannot be URL decoded"); 
              
        }
    }
}
