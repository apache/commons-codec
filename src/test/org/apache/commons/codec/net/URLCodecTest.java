/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//codec/src/test/org/apache/commons/codec/net/URLCodecTest.java,v 1.2 2003/07/31 20:09:21 tobrien Exp $
 * $Revision: 1.2 $
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

import org.apache.commons.codec.DecoderException;

import junit.framework.TestCase;

/**
 * URL codec test cases
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */

public class URLCodecTest extends TestCase {
    
    static final int SWISS_GERMAN_STUFF_UNICODE [] = {
        0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
    };
    
    static final int RUSSIAN_STUFF_UNICODE [] = {
        0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 
        0x432, 0x435, 0x442 
    }; 

    public URLCodecTest(String name) {
        super(name);
    }

    private String constructString(int [] unicodeChars) {
        StringBuffer buffer = new StringBuffer();
        if (unicodeChars != null) {
            for (int i = 0; i < unicodeChars.length; i++) {
                buffer.append((char)unicodeChars[i]); 
            }
        }
        return buffer.toString();
    }
    
    public void testUTF8RoundTrip() throws Exception {

        String ru_msg = constructString(RUSSIAN_STUFF_UNICODE); 
        String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE); 
        
        URLCodec codec = new URLCodec();
        
        assertEquals(
            "%D0%92%D1%81%D0%B5%D0%BC_%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82", 
            codec.encode(ru_msg, "UTF-8")
        );
        assertEquals("Gr%C3%BCezi_z%C3%A4m%C3%A4", codec.encode(ch_msg, "UTF-8"));
        
        assertEquals(ru_msg, codec.decode(codec.encode(ru_msg, "UTF-8"), "UTF-8"));
        assertEquals(ch_msg, codec.decode(codec.encode(ch_msg, "UTF-8"), "UTF-8"));
    }

    public void testBasicEncodeDecode() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "Hello there!";
        String encoded = urlcodec.encode(plain);
        assertEquals("Basic URL encoding test", 
            "Hello+there%21", encoded);
        assertEquals("Basic URL decoding test", 
            plain, urlcodec.decode(encoded));
    }


    public void testSafeCharEncodeDecode() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "abc123_-.*";
        String encoded = urlcodec.encode(plain);
        assertEquals("Safe chars URL encoding test", 
            plain, encoded);
        assertEquals("Safe chars URL decoding test", 
            plain, urlcodec.decode(encoded));
    }


    public void testUnsafeEncodeDecode() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "~!@#$%^&()+{}\"\\;:`,/[]";
        String encoded = urlcodec.encode(plain);
        assertEquals("Unsafe chars URL encoding test", 
            "%7E%21%40%23%24%25%5E%26%28%29%2B%7B%7D%22%5C%3B%3A%60%2C%2F%5B%5D", encoded);
        assertEquals("Unsafe chars URL decoding test", 
            plain, urlcodec.decode(encoded));
    }


    public void testEncodeDecodeNull() throws Exception {
        URLCodec urlcodec = new URLCodec();
        assertNull("Null string URL encoding test", 
            urlcodec.encode((String)null));
        assertNull("Null string URL decoding test", 
            urlcodec.decode((String)null));
    }


    public void testDecodeInvalid() throws Exception {
        URLCodec urlcodec = new URLCodec();
        try {
            urlcodec.decode("%");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            urlcodec.decode("%A");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            urlcodec.decode("%WW");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
    }
}
