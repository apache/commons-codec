/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//codec/src/test/org/apache/commons/codec/net/URLCodecTest.java,v 1.1 2003/07/11 20:14:37 tobrien Exp $
 * $Revision: 1.1 $
 * $Date: 2003/07/11 20:14:37 $
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

    public URLCodecTest(String name) {
        super(name);
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
