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

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.Test;

/**
 * Quoted-printable codec test cases
 *
 */
public class QuotedPrintableCodecTest {

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
    public void testUTF8RoundTrip() throws Exception {

        final String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
        final String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);

        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();

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
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "= Hello there =\r\n";
        final String encoded = qpcodec.encode(plain);
        assertEquals("Basic quoted-printable encoding test",
            "=3D Hello there =3D=0D=0A", encoded);
        assertEquals("Basic quoted-printable decoding test",
            plain, qpcodec.decode(encoded));
    }

    @Test
    public void testSafeCharEncodeDecode() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "abc123_-.*~!@#$%^&()+{}\"\\;:`,/[]";
        final String encoded = qpcodec.encode(plain);
        assertEquals("Safe chars quoted-printable encoding test",
            plain, encoded);
        assertEquals("Safe chars quoted-printable decoding test",
            plain, qpcodec.decode(encoded));
    }


    @Test
    public void testUnsafeEncodeDecode() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "=\r\n";
        final String encoded = qpcodec.encode(plain);
        assertEquals("Unsafe chars quoted-printable encoding test",
            "=3D=0D=0A", encoded);
        assertEquals("Unsafe chars quoted-printable decoding test",
            plain, qpcodec.decode(encoded));
    }

    @Test
    public void testEncodeDecodeNull() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        assertNull("Null string quoted-printable encoding test",
            qpcodec.encode((String)null));
        assertNull("Null string quoted-printable decoding test",
            qpcodec.decode((String)null));
    }


    @Test
    public void testDecodeInvalid() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        try {
            qpcodec.decode("=");
            fail("DecoderException should have been thrown");
        } catch (final DecoderException e) {
            // Expected. Move on
        }
        try {
            qpcodec.decode("=A");
            fail("DecoderException should have been thrown");
        } catch (final DecoderException e) {
            // Expected. Move on
        }
        try {
            qpcodec.decode("=WW");
            fail("DecoderException should have been thrown");
        } catch (final DecoderException e) {
            // Expected. Move on
        }
    }

    @Test
    public void testEncodeNull() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final byte[] plain = null;
        final byte[] encoded = qpcodec.encode(plain);
        assertEquals("Encoding a null string should return null",
            null, encoded);
    }

    @Test
    public void testEncodeUrlWithNullBitSet() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "1+1 = 2";
        final String encoded = new String(QuotedPrintableCodec.
            encodeQuotedPrintable(null, plain.getBytes(StandardCharsets.UTF_8)));
        assertEquals("Basic quoted-printable encoding test",
            "1+1 =3D 2", encoded);
        assertEquals("Basic quoted-printable decoding test",
            plain, qpcodec.decode(encoded));

    }

    @Test
    public void testDecodeWithNullArray() throws Exception {
        final byte[] plain = null;
        final byte[] result = QuotedPrintableCodec.decodeQuotedPrintable( plain );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testEncodeStringWithNull() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String test = null;
        final String result = qpcodec.encode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testDecodeStringWithNull() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String test = null;
        final String result = qpcodec.decode( test, "charset" );
        assertEquals("Result should be null", null, result);
    }

    @Test
    public void testEncodeObjects() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "1+1 = 2";
        String encoded = (String) qpcodec.encode((Object) plain);
        assertEquals("Basic quoted-printable encoding test",
            "1+1 =3D 2", encoded);

        final byte[] plainBA = plain.getBytes(StandardCharsets.UTF_8);
        final byte[] encodedBA = (byte[]) qpcodec.encode((Object) plainBA);
        encoded = new String(encodedBA);
        assertEquals("Basic quoted-printable encoding test",
            "1+1 =3D 2", encoded);

        final Object result = qpcodec.encode((Object) null);
        assertEquals( "Encoding a null Object should return null", null, result);

        try {
            final Object dObj = Double.valueOf(3.0d);
            qpcodec.encode( dObj );
            fail( "Trying to url encode a Double object should cause an exception.");
        } catch (final EncoderException ee) {
            // Exception expected, test segment passes.
        }
    }

    @Test(expected=UnsupportedCharsetException.class)
    public void testInvalidEncoding() {
        new QuotedPrintableCodec("NONSENSE");
    }

    @Test
    public void testDecodeObjects() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        final String plain = "1+1 =3D 2";
        String decoded = (String) qpcodec.decode((Object) plain);
        assertEquals("Basic quoted-printable decoding test",
            "1+1 = 2", decoded);

        final byte[] plainBA = plain.getBytes(StandardCharsets.UTF_8);
        final byte[] decodedBA = (byte[]) qpcodec.decode((Object) plainBA);
        decoded = new String(decodedBA);
        assertEquals("Basic quoted-printable decoding test",
            "1+1 = 2", decoded);

        final Object result = qpcodec.decode((Object) null);
        assertEquals( "Decoding a null Object should return null", null, result);

        try {
            final Object dObj = Double.valueOf(3.0d);
            qpcodec.decode( dObj );
            fail( "Trying to url encode a Double object should cause an exception.");
        } catch (final DecoderException ee) {
            // Exception expected, test segment passes.
        }
    }

    @Test
    public void testDefaultEncoding() throws Exception {
        final String plain = "Hello there!";
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec("UnicodeBig");
        qpcodec.encode(plain); // To work around a weird quirk in Java 1.2.2
        final String encoded1 = qpcodec.encode(plain, "UnicodeBig");
        final String encoded2 = qpcodec.encode(plain);
        assertEquals(encoded1, encoded2);
    }

    @Test
    public void testSoftLineBreakDecode() throws Exception {
        final String qpdata = "If you believe that truth=3Dbeauty, then surely=20=\r\nmathematics is the most beautiful branch of philosophy.";
        final String expected = "If you believe that truth=beauty, then surely mathematics is the most beautiful branch of philosophy.";

        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec();
        assertEquals(expected, qpcodec.decode(qpdata));

        final String encoded = qpcodec.encode(expected);
        assertEquals(expected, qpcodec.decode(encoded));
    }

    @Test
    public void testSoftLineBreakEncode() throws Exception {
        final String qpdata = "If you believe that truth=3Dbeauty, then surely mathematics is the most b=\r\neautiful branch of philosophy.";
        final String expected = "If you believe that truth=beauty, then surely mathematics is the most beautiful branch of philosophy.";

        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec(true);
        assertEquals(qpdata, qpcodec.encode(expected));

        final String decoded = qpcodec.decode(qpdata);
        assertEquals(qpdata, qpcodec.encode(decoded));
    }

    @Test
    public void testSkipNotEncodedCRLF() throws Exception {
        final String qpdata = "CRLF in an\n encoded text should be=20=\r\n\rskipped in the\r decoding.";
        final String expected = "CRLF in an encoded text should be skipped in the decoding.";

        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec(true);
        assertEquals(expected, qpcodec.decode(qpdata));

        final String encoded = qpcodec.encode(expected);
        assertEquals(expected, qpcodec.decode(encoded));
    }

    @Test
    public void testTrailingSpecial() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec(true);

        String plain ="This is a example of a quoted-printable text file. This might contain sp=cial chars.";
        String expected = "This is a example of a quoted-printable text file. This might contain sp=3D=\r\ncial chars.";
        assertEquals(expected, qpcodec.encode(plain));

        plain ="This is a example of a quoted-printable text file. This might contain ta\tbs as well.";
        expected = "This is a example of a quoted-printable text file. This might contain ta=09=\r\nbs as well.";
        assertEquals(expected, qpcodec.encode(plain));
    }

    @Test
    public void testUltimateSoftBreak() throws Exception {
        final QuotedPrintableCodec qpcodec = new QuotedPrintableCodec(true);

        String plain ="This is a example of a quoted-printable text file. There is no end to it\t";
        String expected = "This is a example of a quoted-printable text file. There is no end to i=\r\nt=09";

        assertEquals(expected, qpcodec.encode(plain));

        plain ="This is a example of a quoted-printable text file. There is no end to it ";
        expected = "This is a example of a quoted-printable text file. There is no end to i=\r\nt=20";

        assertEquals(expected, qpcodec.encode(plain));

        // whitespace before soft break
        plain ="This is a example of a quoted-printable text file. There is no end to   ";
        expected = "This is a example of a quoted-printable text file. There is no end to=20=\r\n =20";

        assertEquals(expected, qpcodec.encode(plain));

        // non-printable character before soft break
        plain ="This is a example of a quoted-printable text file. There is no end to=  ";
        expected = "This is a example of a quoted-printable text file. There is no end to=3D=\r\n =20";

        assertEquals(expected, qpcodec.encode(plain));
    }

    @Test
    public void testFinalBytes() throws Exception {
        // whitespace, but does not need to be encoded
        final String plain ="This is a example of a quoted=printable text file. There is no tt";
        final String expected = "This is a example of a quoted=3Dprintable text file. There is no tt";

        assertEquals(expected, new QuotedPrintableCodec(true).encode(plain));
    }
}
