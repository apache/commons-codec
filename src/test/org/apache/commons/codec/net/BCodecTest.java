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

import junit.framework.TestCase;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.CharEncoding;

/**
 * Quoted-printable codec test cases
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @version $Id$
 */
public class BCodecTest extends TestCase {

    static final int SWISS_GERMAN_STUFF_UNICODE[] =
        { 0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4 };

    static final int RUSSIAN_STUFF_UNICODE[] =
        { 0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 0x432, 0x435, 0x442 };

    public BCodecTest(String name) {
        super(name);
    }

    private String constructString(int[] unicodeChars) {
        StringBuffer buffer = new StringBuffer();
        if (unicodeChars != null) {
            for (int i = 0; i < unicodeChars.length; i++) {
                buffer.append((char) unicodeChars[i]);
            }
        }
        return buffer.toString();
    }

    public void testNullInput() throws Exception {
        BCodec bcodec = new BCodec();
        assertNull(bcodec.doDecoding(null));
        assertNull(bcodec.doEncoding(null));
    }

    public void testUTF8RoundTrip() throws Exception {

        String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
        String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);

        BCodec bcodec = new BCodec(CharEncoding.UTF_8);

        assertEquals("=?UTF-8?B?0JLRgdC10Lxf0L/RgNC40LLQtdGC?=", bcodec.encode(ru_msg));
        assertEquals("=?UTF-8?B?R3LDvGV6aV96w6Rtw6Q=?=", bcodec.encode(ch_msg));

        assertEquals(ru_msg, bcodec.decode(bcodec.encode(ru_msg)));
        assertEquals(ch_msg, bcodec.decode(bcodec.encode(ch_msg)));
    }

    public void testBasicEncodeDecode() throws Exception {
        BCodec bcodec = new BCodec();
        String plain = "Hello there";
        String encoded = bcodec.encode(plain);
        assertEquals("Basic B encoding test", "=?UTF-8?B?SGVsbG8gdGhlcmU=?=", encoded);
        assertEquals("Basic B decoding test", plain, bcodec.decode(encoded));
    }

    public void testEncodeDecodeNull() throws Exception {
        BCodec bcodec = new BCodec();
        assertNull("Null string B encoding test", bcodec.encode((String) null));
        assertNull("Null string B decoding test", bcodec.decode((String) null));
    }

    public void testEncodeStringWithNull() throws Exception {
        BCodec bcodec = new BCodec();
        String test = null;
        String result = bcodec.encode(test, "charset");
        assertEquals("Result should be null", null, result);
    }

    public void testDecodeStringWithNull() throws Exception {
        BCodec bcodec = new BCodec();
        String test = null;
        String result = bcodec.decode(test);
        assertEquals("Result should be null", null, result);
    }

    public void testEncodeObjects() throws Exception {
        BCodec bcodec = new BCodec();
        String plain = "what not";
        String encoded = (String) bcodec.encode((Object) plain);

        assertEquals("Basic B encoding test", "=?UTF-8?B?d2hhdCBub3Q=?=", encoded);

        Object result = bcodec.encode((Object) null);
        assertEquals("Encoding a null Object should return null", null, result);

        try {
            Object dObj = new Double(3.0);
            bcodec.encode(dObj);
            fail("Trying to url encode a Double object should cause an exception.");
        } catch (EncoderException ee) {
            // Exception expected, test segment passes.
        }
    }

    public void testInvalidEncoding() {
        BCodec bcodec = new BCodec("NONSENSE");
        try {
            bcodec.encode("Hello there!");
            fail("We set the encoding to a bogus NONSENSE value, this shouldn't have worked.");
        } catch (EncoderException ee) {
            // Exception expected, test segment passes.
        }
        try {
            bcodec.decode("=?NONSENSE?B?Hello there!?=");
            fail("We set the encoding to a bogus NONSENSE value, this shouldn't have worked.");
        } catch (DecoderException ee) {
            // Exception expected, test segment passes.
        }
    }

    public void testDecodeObjects() throws Exception {
        BCodec bcodec = new BCodec();
        String decoded = "=?UTF-8?B?d2hhdCBub3Q=?=";
        String plain = (String) bcodec.decode((Object) decoded);
        assertEquals("Basic B decoding test", "what not", plain);

        Object result = bcodec.decode((Object) null);
        assertEquals("Decoding a null Object should return null", null, result);

        try {
            Object dObj = new Double(3.0);
            bcodec.decode(dObj);
            fail("Trying to url encode a Double object should cause an exception.");
        } catch (DecoderException ee) {
            // Exception expected, test segment passes.
        }
    }
}
