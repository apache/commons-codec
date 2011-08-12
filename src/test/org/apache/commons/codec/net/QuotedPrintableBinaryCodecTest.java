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

import org.junit.Test;

/**
 * Quoted-printable codec test cases
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @version $Id: QuotedPrintableStringCodecTest.java 1156841 2011-08-11 21:33:42Z ggregory $
 */
public class QuotedPrintableBinaryCodecTest {
    
    static final int SWISS_GERMAN_STUFF_UNICODE [] = {
        0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
    };
    
    static final int RUSSIAN_STUFF_UNICODE [] = {
        0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 
        0x432, 0x435, 0x442 
    }; 

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
        QuotedPrintableBinaryCodec qpcodec = new QuotedPrintableBinaryCodec();
        assertNull("Null string quoted-printable encoding test", qpcodec.encode(null));
        assertNull("Null string quoted-printable decoding test", qpcodec.decode(null));
    }

    @Test
    public void testEncodeNull() throws Exception {
        QuotedPrintableBinaryCodec qpcodec = new QuotedPrintableBinaryCodec();
        byte[] plain = null;
        byte[] encoded = qpcodec.encode(plain);
        assertEquals("Encoding a null string should return null", 
            null, encoded);
    }
    
    @Test
    public void testDecodeWithNullArray() throws Exception {
        byte[] plain = null;
        byte[] result = QuotedPrintableBinaryCodec.decodeQuotedPrintable( plain );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testEncodeObjects() throws Exception {
        QuotedPrintableBinaryCodec qpcodec = new QuotedPrintableBinaryCodec();
        String plain = "1+1 = 2";

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] encodedBA = qpcodec.encode(plainBA);
        String encoded = new String(encodedBA);
        assertEquals("Basic quoted-printable encoding test", "1+1 =3D 2", encoded);

        Object result = qpcodec.encode(null);
        assertEquals("Encoding a null Object should return null", null, result);
    }

    @Test
    public void testDecodeObjects() throws Exception {
        QuotedPrintableBinaryCodec qpcodec = new QuotedPrintableBinaryCodec();
        String plain = "1+1 =3D 2";

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] decodedBA = qpcodec.decode(plainBA);
        String decoded = new String(decodedBA);
        assertEquals("Basic quoted-printable decoding test", "1+1 = 2", decoded);

        Object result = qpcodec.decode(null);
        assertEquals("Decoding a null Object should return null", null, result);
    }

}