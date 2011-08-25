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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

/**
 * URL codec test cases
 * 
 * @author Apache Software Foundation
 * @version $Id: URLCodecStringTest.java 1156841 2011-08-11 21:33:42Z ggregory $
 */
public class URLCodecBinaryTest {
    
    static final int SWISS_GERMAN_STUFF_UNICODE [] = {
        0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
    };
    
    static final int RUSSIAN_STUFF_UNICODE [] = {
        0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 
        0x432, 0x435, 0x442 
    }; 

    private void validateState(URLBinaryCodec urlCodec) {
        // no tests for now.
    }
    
    private String constructString(int [] unicodeChars) {
        StringBuffer buffer = new StringBuffer();
        if (unicodeChars != null) {
            for (int unicodeChar : unicodeChars) {
                buffer.append((char)unicodeChar); 
            }
        }
        return buffer.toString();
    }
    
    @Test
    public void testEncodeDecodeNull() throws Exception {
        URLBinaryCodec urlCodec = new URLBinaryCodec();
        assertNull("Null string URL encoding test", urlCodec.encode(null));
        assertNull("Null string URL decoding test", urlCodec.decode(null));
        this.validateState(urlCodec);
    }

    @Test
    public void testDecodeInvalidContent() throws UnsupportedEncodingException, DecoderException {
        String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE); 
        URLBinaryCodec urlCodec = new URLBinaryCodec();
        byte[] input = ch_msg.getBytes("ISO-8859-1");
        byte[] output = urlCodec.decode(input);
        assertEquals(input.length, output.length);
        for (int i = 0; i < input.length; i++) {
            assertEquals(input[i], output[i]);
        }
        this.validateState(urlCodec);
    }

    @Test
    public void testEncodeNull() throws Exception {
        URLBinaryCodec urlCodec = new URLBinaryCodec();
        byte[] plain = null;
        byte[] encoded = urlCodec.encode(plain);
        assertEquals("Encoding a null string should return null", 
            null, encoded);
        this.validateState(urlCodec);
    }
    
    @Test
    public void testDecodeWithNullArray() throws Exception {
        byte[] plain = null;
        byte[] result = URLCodec.decodeUrl( plain );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testEncodeObjects() throws Exception {
        URLBinaryCodec urlCodec = new URLBinaryCodec();
        String plain = "Hello there!";

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] encodedBA = urlCodec.encode(plainBA);
        String encoded = new String(encodedBA);
        assertEquals("Basic URL encoding test", 
            "Hello+there%21", encoded);
            
        byte[] result = urlCodec.encode(null);
        assertEquals( "Encoding a null Object should return null", null, result);
        
        this.validateState(urlCodec);
    }
    
    @Test
    public void testDecodeObjects() throws Exception {
        URLBinaryCodec urlCodec = new URLBinaryCodec();
        String plain = "Hello+there%21";

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] decodedBA = urlCodec.decode(plainBA);
        String decoded = new String(decodedBA);
        assertEquals("Basic URL decoding test", 
            "Hello there!", decoded);
            
        byte[] result = urlCodec.decode((byte[]) null);
        assertEquals( "Decoding a null Object should return null", null, result);
        
        this.validateState(urlCodec);
    }
}
