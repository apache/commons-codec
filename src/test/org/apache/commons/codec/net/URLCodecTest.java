/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import junit.framework.TestCase;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

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

    public void testEncodeNull() throws Exception {
        URLCodec urlcodec = new URLCodec();
        byte[] plain = null;
        byte[] encoded = urlcodec.encode(plain);
        assertEquals("Encoding a null string should return null", 
            null, encoded);
    }
    
    public void testEncodeUrlWithNullBitSet() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "Hello there!";
        String encoded = new String( URLCodec.encodeUrl(null, plain.getBytes()));
        assertEquals("Basic URL encoding test", 
            "Hello+there%21", encoded);
        assertEquals("Basic URL decoding test", 
            plain, urlcodec.decode(encoded));
        
    }

    public void testDecodeWithNullArray() throws Exception {
        byte[] plain = null;
        byte[] result = URLCodec.decodeUrl( plain );
        assertEquals("Result should be null", null, result);
    }

    public void testEncodeStringWithNull() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String test = null;
        String result = urlcodec.encode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }

    public void testDecodeStringWithNull() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String test = null;
        String result = urlcodec.decode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }
    
    public void testEncodeObjects() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "Hello there!";
        String encoded = (String) urlcodec.encode((Object) plain);
        assertEquals("Basic URL encoding test", 
            "Hello+there%21", encoded);

        byte[] plainBA = plain.getBytes();
        byte[] encodedBA = (byte[]) urlcodec.encode((Object) plainBA);
        encoded = new String(encodedBA);
        assertEquals("Basic URL encoding test", 
            "Hello+there%21", encoded);
            
        Object result = urlcodec.encode((Object) null);
        assertEquals( "Encoding a null Object should return null", null, result);
        
        try {
            Object dObj = new Double(3.0);
            urlcodec.encode( dObj );
            fail( "Trying to url encode a Double object should cause an exception.");
        } catch( EncoderException ee ) {
            // Exception expected, test segment passes.
        }
    }
    
    public void testInvalidEncoding() {
        URLCodec urlcodec = new URLCodec("NONSENSE");
           String plain = "Hello there!";
            try {
               urlcodec.encode(plain);
                fail( "We set the encoding to a bogus NONSENSE vlaue, this shouldn't have worked.");
            } catch( EncoderException ee ) {
                // Exception expected, test segment passes.
            }
            try {
               urlcodec.decode(plain);
                fail( "We set the encoding to a bogus NONSENSE vlaue, this shouldn't have worked.");
            } catch( DecoderException ee ) {
                // Exception expected, test segment passes.
            }
    }

    public void testDecodeObjects() throws Exception {
        URLCodec urlcodec = new URLCodec();
        String plain = "Hello+there%21";
        String decoded = (String) urlcodec.decode((Object) plain);
        assertEquals("Basic URL decoding test", 
            "Hello there!", decoded);

        byte[] plainBA = plain.getBytes();
        byte[] decodedBA = (byte[]) urlcodec.decode((Object) plainBA);
        decoded = new String(decodedBA);
        assertEquals("Basic URL decoding test", 
            "Hello there!", decoded);
            
        Object result = urlcodec.decode((Object) null);
        assertEquals( "Decoding a null Object should return null", null, result);
        
        try {
            Object dObj = new Double(3.0);
            urlcodec.decode( dObj );
            fail( "Trying to url encode a Double object should cause an exception.");
        } catch( DecoderException ee ) {
            // Exception expected, test segment passes.
        }
    }

    public void testDefaultEncoding() throws Exception {
        String plain = "Hello there!";
        URLCodec urlcodec = new URLCodec("UnicodeBig");
        urlcodec.encode(plain); // To work around a weird quirk in Java 1.2.2
        String encoded1 = urlcodec.encode(plain, "UnicodeBig");
        String encoded2 = urlcodec.encode(plain);
        assertEquals(encoded1, encoded2);
    }

}
