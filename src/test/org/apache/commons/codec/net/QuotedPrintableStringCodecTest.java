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
import static org.junit.Assert.fail;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Quoted-printable codec test cases
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @version $Id$
 */
public class QuotedPrintableStringCodecTest {
    
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
    public void testUTF8RoundTrip() throws Exception {

        String ru_msg = constructString(RUSSIAN_STUFF_UNICODE); 
        String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE); 
        
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        
        assertEquals(
            "=D0=92=D1=81=D0=B5=D0=BC_=D0=BF=D1=80=D0=B8=D0=B2=D0=B5=D1=82", 
        qpcodec.encode(ru_msg, CharEncoding.UTF_8)
        );
        assertEquals("Gr=C3=BCezi_z=C3=A4m=C3=A4", qpcodec.encode(ch_msg, CharEncoding.UTF_8));
        
        assertEquals(ru_msg, qpcodec.decode(qpcodec.encode(ru_msg, CharEncoding.UTF_8), CharEncoding.UTF_8));
        assertEquals(ch_msg, qpcodec.decode(qpcodec.encode(ch_msg, CharEncoding.UTF_8), CharEncoding.UTF_8));
    }

    @Test
    public void testBasicEncodeDecode() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "= Hello there =\r\n";
        String encoded = qpcodec.encode(plain);
        assertEquals("Basic quoted-printable encoding test", 
            "=3D Hello there =3D=0D=0A", encoded);
        assertEquals("Basic quoted-printable decoding test", 
            plain, qpcodec.decode(encoded));
    }

    @Test
    public void testSafeCharEncodeDecode() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "abc123_-.*~!@#$%^&()+{}\"\\;:`,/[]";
        String encoded = qpcodec.encode(plain);
        assertEquals("Safe chars quoted-printable encoding test", 
            plain, encoded);
        assertEquals("Safe chars quoted-printable decoding test", 
            plain, qpcodec.decode(encoded));
    }


    @Test
    public void testUnsafeEncodeDecode() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "=\r\n";
        String encoded = qpcodec.encode(plain);
        assertEquals("Unsafe chars quoted-printable encoding test", 
            "=3D=0D=0A", encoded);
        assertEquals("Unsafe chars quoted-printable decoding test", 
            plain, qpcodec.decode(encoded));
    }

    @Test
    public void testEncodeDecodeNull() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        assertNull("Null string quoted-printable encoding test", 
            qpcodec.encode((String)null));
        assertNull("Null string quoted-printable decoding test", 
            qpcodec.decode((String)null));
    }


    @Test
    public void testDecodeInvalid() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        try {
            qpcodec.decode("=");
            fail("DecoderException should have been thrown");
        } catch (DecoderException e) {
            // Expected. Move on
        }
        try {
            qpcodec.decode("=A");
            fail("DecoderException should have been thrown");
        } catch (DecoderException e) {
            // Expected. Move on
        }
        try {
            qpcodec.decode("=WW");
            fail("DecoderException should have been thrown");
        } catch (DecoderException e) {
            // Expected. Move on
        }
    }

    @Test
    public void testEncodeNull() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        byte[] plain = null;
        byte[] encoded = qpcodec.encode(plain);
        assertEquals("Encoding a null string should return null", 
            null, encoded);
    }
    
    @Test
    public void testEncodeUrlWithNullBitSet() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "1+1 = 2";
        String encoded = new String(QuotedPrintableStringCodec.
            encodeQuotedPrintable(null, plain.getBytes("UTF-8")));
        assertEquals("Basic quoted-printable encoding test", 
            "1+1 =3D 2", encoded);
        assertEquals("Basic quoted-printable decoding test", 
            plain, qpcodec.decode(encoded));
        
    }

    @Test
    public void testDecodeWithNullArray() throws Exception {
        byte[] plain = null;
        byte[] result = QuotedPrintableStringCodec.decodeQuotedPrintable( plain );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testEncodeStringWithNull() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String test = null;
        String result = qpcodec.encode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testDecodeStringWithNull() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String test = null;
        String result = qpcodec.decode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }
    
    @Test
    public void testEncodeStrings() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "1+1 = 2";
        String encoded = qpcodec.encode(plain);
        assertEquals("Basic quoted-printable encoding test", 
            "1+1 =3D 2", encoded);

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] encodedBA = qpcodec.encode(plainBA);
        encoded = new String(encodedBA);
        assertEquals("Basic quoted-printable encoding test", 
            "1+1 =3D 2", encoded);
            
        Object result = qpcodec.encode((String)null);
        assertEquals( "Encoding a null Object should return null", null, result);        
    }
    
    @Test
    public void testInvalidEncoding() {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec("NONSENSE");
           String plain = "Hello there!";
            try {
               qpcodec.encode(plain);
                fail( "We set the encoding to a bogus NONSENSE vlaue, this shouldn't have worked.");
            } catch (EncoderException ee) {
                // Exception expected, test segment passes.
            }
            try {
               qpcodec.decode(plain);
                fail( "We set the encoding to a bogus NONSENSE vlaue, this shouldn't have worked.");
            } catch (DecoderException ee) {
                // Exception expected, test segment passes.
            }
    }

    @Test
    public void testDecodeStrings() throws Exception {
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec();
        String plain = "1+1 =3D 2";
        String decoded = qpcodec.decode(plain);
        assertEquals("Basic quoted-printable decoding test", 
            "1+1 = 2", decoded);

        byte[] plainBA = plain.getBytes("UTF-8");
        byte[] decodedBA = qpcodec.decode(plainBA);
        decoded = new String(decodedBA);
        assertEquals("Basic quoted-printable decoding test", 
            "1+1 = 2", decoded);
            
        String result = qpcodec.decode((String)null);
        assertEquals( "Decoding a null Object should return null", null, result);        
    }

    @Test
    public void testDefaultEncoding() throws Exception {
        String plain = "Hello there!";
        QuotedPrintableStringCodec qpcodec = new QuotedPrintableStringCodec("UnicodeBig");
        qpcodec.encode(plain); // To work around a weird quirk in Java 1.2.2
        String encoded1 = qpcodec.encode(plain, "UnicodeBig");
        String encoded2 = qpcodec.encode(plain);
        assertEquals(encoded1, encoded2);
    }

    @Test
    @Ignore
    /**
     * The QuotedPrintableStringCodec documentation states that this is not supported.
     *  
     * @throws Exception
     * @see <a href="https://issues.apache.org/jira/browse/CODEC-121">CODEC-121</a>
     */
    public void testSoftLineBreakDecode() throws Exception {
        String qpdata = "If you believe that truth=3Dbeauty, then surely=20=\r\nmathematics is the most beautiful branch of philosophy.";
        String expected = "If you believe that truth=beauty, then surely mathematics is the most beautiful branch of philosophy.";
        assertEquals(expected, new QuotedPrintableStringCodec().decode(qpdata));
    }

    @Test
    @Ignore
    /**
     * The QuotedPrintableStringCodec documentation states that this is not supported.
     *  
     * @throws Exception
     * @see <a href="https://issues.apache.org/jira/browse/CODEC-121">CODEC-121</a>
     */
    public void testSoftLineBreakEncode() throws Exception {
        String qpdata = "If you believe that truth=3Dbeauty, then surely=20=\r\nmathematics is the most beautiful branch of philosophy.";
        String expected = "If you believe that truth=beauty, then surely mathematics is the most beautiful branch of philosophy.";
        assertEquals(qpdata, new QuotedPrintableStringCodec().encode(expected));
    }
}
