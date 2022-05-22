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

import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Quoted-printable codec test cases
 *
 */
public class BCodecTest {
    private static final String[] BASE64_IMPOSSIBLE_CASES = {
            // Require the RFC 1522 "encoded-word" header
            "=?ASCII?B?ZE==?=",
            "=?ASCII?B?ZmC=?=",
            "=?ASCII?B?Zm9vYE==?=",
            "=?ASCII?B?Zm9vYmC=?=",
            "=?ASCII?B?AB==?="
    };

    static final int SWISS_GERMAN_STUFF_UNICODE[] =
        { 0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4 };

    static final int RUSSIAN_STUFF_UNICODE[] =
        { 0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 0x432, 0x435, 0x442 };

    private String constructString(final int[] unicodeChars) {
        final StringBuilder buffer = new StringBuilder();
        if (unicodeChars != null) {
            for (final int unicodeChar : unicodeChars) {
                buffer.append((char) unicodeChar);
            }
        }
        return buffer.toString();
    }

    @Test
    public void testNullInput() throws Exception {
        final BCodec bcodec = new BCodec();
        assertNull(bcodec.doDecoding(null));
        assertNull(bcodec.doEncoding(null));
    }

    @Test
    public void testUTF8RoundTrip() throws Exception {

        final String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
        final String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);

        final BCodec bcodec = new BCodec(CharEncoding.UTF_8);

        assertEquals("=?UTF-8?B?0JLRgdC10Lxf0L/RgNC40LLQtdGC?=", bcodec.encode(ru_msg));
        assertEquals("=?UTF-8?B?R3LDvGV6aV96w6Rtw6Q=?=", bcodec.encode(ch_msg));

        assertEquals(ru_msg, bcodec.decode(bcodec.encode(ru_msg)));
        assertEquals(ch_msg, bcodec.decode(bcodec.encode(ch_msg)));
    }

    @Test
    public void testBasicEncodeDecode() throws Exception {
        final BCodec bcodec = new BCodec();
        final String plain = "Hello there";
        final String encoded = bcodec.encode(plain);
        assertEquals("=?UTF-8?B?SGVsbG8gdGhlcmU=?=", encoded, "Basic B encoding test");
        assertEquals(plain, bcodec.decode(encoded), "Basic B decoding test");
    }

    @Test
    public void testEncodeDecodeNull() throws Exception {
        final BCodec bcodec = new BCodec();
        assertNull(bcodec.encode((String) null), "Null string B encoding test");
        assertNull(bcodec.decode((String) null), "Null string B decoding test");
    }

    @Test
    public void testEncodeStringWithNull() throws Exception {
        final BCodec bcodec = new BCodec();
        final String test = null;
        final String result = bcodec.encode(test, "charset");
        assertNull(result, "Result should be null");
    }

    @Test
    public void testDecodeStringWithNull() throws Exception {
        final BCodec bcodec = new BCodec();
        final String test = null;
        final String result = bcodec.decode(test);
        assertNull(result, "Result should be null");
    }

    @Test
    public void testEncodeObjects() throws Exception {
        final BCodec bcodec = new BCodec();
        final String plain = "what not";
        final String encoded = (String) bcodec.encode((Object) plain);

        assertEquals("=?UTF-8?B?d2hhdCBub3Q=?=", encoded, "Basic B encoding test");
        final Object result = bcodec.encode((Object) null);

        assertNull(result, "Encoding a null Object should return null");

        assertThrows(EncoderException.class, () -> bcodec.encode(Double.valueOf(3.0d)),
            "Trying to url encode a Double object should cause an exception.");
    }

    @Test
    public void testInvalidEncoding() {
        assertThrows(UnsupportedCharsetException.class, () -> new BCodec("NONSENSE"));
    }

    @Test
    public void testDecodeObjects() throws Exception {
        final BCodec bcodec = new BCodec();
        final String decoded = "=?UTF-8?B?d2hhdCBub3Q=?=";
        final String plain = (String) bcodec.decode((Object) decoded);
        assertEquals("what not", plain, "Basic B decoding test");
        final Object result = bcodec.decode((Object) null);
        assertNull(result, "Decoding a null Object should return null");

        assertThrows(DecoderException.class, () -> bcodec.decode(Double.valueOf(3.0d)));
    }

    @Test
    public void testBase64ImpossibleSamplesDefault() throws DecoderException {
        final BCodec codec = new BCodec();
        // Default encoding is lenient
        assertFalse(codec.isStrictDecoding());
        for (final String s : BASE64_IMPOSSIBLE_CASES) {
            codec.decode(s);
        }
    }

    @Test
    public void testBase64ImpossibleSamplesLenient() throws DecoderException {
        final BCodec codec = new BCodec(StandardCharsets.UTF_8, CodecPolicy.LENIENT);
        // Default encoding is lenient
        assertFalse(codec.isStrictDecoding());
        for (final String s : BASE64_IMPOSSIBLE_CASES) {
            codec.decode(s);
        }
    }

    @Test
    public void testBase64ImpossibleSamplesStrict() {
        final BCodec codec = new BCodec(StandardCharsets.UTF_8, CodecPolicy.STRICT);
        assertTrue(codec.isStrictDecoding());
        for (final String s : BASE64_IMPOSSIBLE_CASES) {
            assertThrows(DecoderException.class, () -> codec.decode(s));
        }
    }

}
