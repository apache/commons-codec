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

import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Quoted-printable codec test cases
 *
 */
public class QCodecTest {

    static final int SWISS_GERMAN_STUFF_UNICODE [] = {
        0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
    };

    static final int RUSSIAN_STUFF_UNICODE [] = {
        0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438,
        0x432, 0x435, 0x442
    };

    private String constructString(final int [] unicodeChars) {
        final StringBuilder buffer = new StringBuilder();
        if (unicodeChars != null) {
            for (final int unicodeChar : unicodeChars) {
                buffer.append((char)unicodeChar);
            }
        }
        return buffer.toString();
    }

    @Test
    public void testNullInput() throws Exception {
        final QCodec qcodec = new QCodec();
        assertNull(qcodec.doDecoding(null));
        assertNull(qcodec.doEncoding(null));
    }

    @Test
    public void testUTF8RoundTrip() throws Exception {

        final String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
        final String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);

        final QCodec qcodec = new QCodec(CharEncoding.UTF_8);

        assertEquals(
            "=?UTF-8?Q?=D0=92=D1=81=D0=B5=D0=BC=5F=D0=BF=D1=80=D0=B8=D0=B2=D0=B5=D1=82?=",
        qcodec.encode(ru_msg)
        );
        assertEquals("=?UTF-8?Q?Gr=C3=BCezi=5Fz=C3=A4m=C3=A4?=", qcodec.encode(ch_msg));

        assertEquals(ru_msg, qcodec.decode(qcodec.encode(ru_msg)));
        assertEquals(ch_msg, qcodec.decode(qcodec.encode(ch_msg)));
    }


    @Test
    public void testBasicEncodeDecode() throws Exception {
        final QCodec qcodec = new QCodec();
        final String plain = "= Hello there =\r\n";
        final String encoded = qcodec.encode(plain);
        assertEquals("=?UTF-8?Q?=3D Hello there =3D=0D=0A?=", encoded, "Basic Q encoding test");
        assertEquals(plain, qcodec.decode(encoded), "Basic Q decoding test");
    }

    @Test
    public void testUnsafeEncodeDecode() throws Exception {
        final QCodec qcodec = new QCodec();
        final String plain = "?_=\r\n";
        final String encoded = qcodec.encode(plain);
        assertEquals("=?UTF-8?Q?=3F=5F=3D=0D=0A?=", encoded, "Unsafe chars Q encoding test");
        assertEquals(plain, qcodec.decode(encoded), "Unsafe chars Q decoding test");
    }

    @Test
    public void testEncodeDecodeNull() throws Exception {
        final QCodec qcodec = new QCodec();
        assertNull(qcodec.encode((String)null), "Null string Q encoding test");
        assertNull(qcodec.decode((String)null), "Null string Q decoding test");
    }

    @Test
    public void testEncodeStringWithNull() throws Exception {
        final QCodec qcodec = new QCodec();
        final String test = null;
        final String result = qcodec.encode( test, "charset" );
        assertNull(result, "Result should be null");
    }

    @Test
    public void testDecodeStringWithNull() throws Exception {
        final QCodec qcodec = new QCodec();
        final String test = null;
        final String result = qcodec.decode( test );
        assertNull(result, "Result should be null");
    }


    @Test
    public void testEncodeObjects() throws Exception {
        final QCodec qcodec = new QCodec();
        final String plain = "1+1 = 2";
        final String encoded = (String) qcodec.encode((Object) plain);

        assertEquals("=?UTF-8?Q?1+1 =3D 2?=", encoded, "Basic Q encoding test");
        final Object result = qcodec.encode((Object) null);
        assertNull(result, "Encoding a null Object should return null");
        assertThrows(EncoderException.class, () -> qcodec.encode(Double.valueOf(3.0d)),
            "Trying to url encode a Double object should cause an exception.");
    }


    @Test
    public void testInvalidEncoding() {
        assertThrows(UnsupportedCharsetException.class, () -> new QCodec("NONSENSE"));
    }

    @Test
    public void testDecodeObjects() throws Exception {
        final QCodec qcodec = new QCodec();
        final String decoded = "=?UTF-8?Q?1+1 =3D 2?=";
        final String plain = (String) qcodec.decode((Object) decoded);
        assertEquals("1+1 = 2", plain, "Basic Q decoding test");

        final Object result = qcodec.decode((Object) null);
        assertNull(result, "Decoding a null Object should return null");
        assertThrows(DecoderException.class, () -> qcodec.decode(Double.valueOf(3.0d)),
            "Trying to url encode a Double object should cause an exception.");
    }


    @Test
    public void testEncodeDecodeBlanks() throws Exception {
        final String plain = "Mind those pesky blanks";
        final String encoded1 = "=?UTF-8?Q?Mind those pesky blanks?=";
        final String encoded2 = "=?UTF-8?Q?Mind_those_pesky_blanks?=";
        final QCodec qcodec = new QCodec();
        qcodec.setEncodeBlanks(false);
        String s = qcodec.encode(plain);
        assertEquals(encoded1, s, "Blanks encoding with the Q codec test");
        qcodec.setEncodeBlanks(true);
        s = qcodec.encode(plain);
        assertEquals(encoded2, s, "Blanks encoding with the Q codec test");
        s = qcodec.decode(encoded1);
        assertEquals(plain, s, "Blanks decoding with the Q codec test");
        s = qcodec.decode(encoded2);
        assertEquals(plain, s, "Blanks decoding with the Q codec test");
    }


    @Test
    public void testLetUsMakeCloverHappy() throws Exception {
        final QCodec qcodec = new QCodec();
        qcodec.setEncodeBlanks(true);
        assertTrue(qcodec.isEncodeBlanks());
        qcodec.setEncodeBlanks(false);
        assertFalse(qcodec.isEncodeBlanks());
    }

}
