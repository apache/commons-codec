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
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Percent codec test cases.
 */
public class PercentCodecTest {

    @Test
    public void testBasicEncodeDecode() throws Exception {
        final PercentCodec percentCodec = new PercentCodec();
        final String input = "abcdABCD";
        final byte[] encoded = percentCodec.encode(input.getBytes(StandardCharsets.UTF_8));
        final String encodedS = new String(encoded, StandardCharsets.UTF_8);
        final byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, StandardCharsets.UTF_8);
        assertEquals(input, encodedS, "Basic PercentCodec encoding test");
        assertEquals(input, decodedS, "Basic PercentCodec decoding test");
    }

    @Test
    @Disabled // TODO Should be removed?
    public void testBasicSpace() throws Exception {
        final PercentCodec percentCodec = new PercentCodec();
        final String input = " ";
        final byte[] encoded = percentCodec.encode(input.getBytes(StandardCharsets.UTF_8));
        assertArrayEquals("%20".getBytes(StandardCharsets.UTF_8), encoded);
    }

    @Test
    public void testConfigurablePercentEncoder() throws Exception {
        final String input = "abc123_-.*\u03B1\u03B2";
        final PercentCodec percentCodec = new PercentCodec("abcdef".getBytes(StandardCharsets.UTF_8), false);
        final byte[] encoded = percentCodec.encode(input.getBytes(StandardCharsets.UTF_8));
        final String encodedS = new String(encoded, StandardCharsets.UTF_8);
        assertEquals("%61%62%63123_-.*%CE%B1%CE%B2", encodedS, "Configurable PercentCodec encoding test");
        final byte[] decoded = percentCodec.decode(encoded);
        assertEquals(new String(decoded, StandardCharsets.UTF_8), input, "Configurable PercentCodec decoding test");
    }

    @Test
    public void testDecodeInvalidEncodedResultDecoding() throws Exception {
        final String inputS = "\u03B1\u03B2";
        final PercentCodec percentCodec = new PercentCodec();
        final byte[] encoded = percentCodec.encode(inputS.getBytes(StandardCharsets.UTF_8));
        try {
            percentCodec.decode(Arrays.copyOf(encoded, encoded.length-1)); //exclude one byte
        } catch (final Exception e) {
            assertTrue(DecoderException.class.isInstance(e) &&
                ArrayIndexOutOfBoundsException.class.isInstance(e.getCause()));
        }
    }

    @Test
    public void testDecodeNullObject() throws Exception {
        final PercentCodec percentCodec = new PercentCodec();
        assertNull(percentCodec.decode((Object) null));
    }

    @Test
    public void testDecodeUnsupportedObject() {
        final PercentCodec percentCodec = new PercentCodec();
        assertThrows(DecoderException.class, () -> percentCodec.decode("test"));
    }

    @Test
    public void testEncodeNullObject() throws Exception {
        final PercentCodec percentCodec = new PercentCodec();
        assertNull(percentCodec.encode((Object) null));
    }

    @Test
    public void testEncodeUnsupportedObject() {
        final PercentCodec percentCodec = new PercentCodec();
        assertThrows(EncoderException.class, () -> percentCodec.encode("test"));
    }

    @Test
    public void testPercentEncoderDecoderWithNullOrEmptyInput() throws Exception {
        final PercentCodec percentCodec = new PercentCodec(null, true);
        assertNull(percentCodec.encode(null), "Null input value encoding test");
        assertNull(percentCodec.decode(null), "Null input value decoding test");
        final byte[] emptyInput = "".getBytes(StandardCharsets.UTF_8);
        assertEquals(percentCodec.encode(emptyInput), emptyInput, "Empty input value encoding test");
        assertArrayEquals(percentCodec.decode(emptyInput), emptyInput, "Empty input value decoding test");
    }

    @Test
    public void testPercentEncoderDecoderWithPlusForSpace() throws Exception {
        final String input = "a b c d";
        final PercentCodec percentCodec = new PercentCodec(null, true);
        final byte[] encoded = percentCodec.encode(input.getBytes(StandardCharsets.UTF_8));
        final String encodedS = new String(encoded, StandardCharsets.UTF_8);
        assertEquals("a+b+c+d", encodedS, "PercentCodec plus for space encoding test");
        final byte[] decode = percentCodec.decode(encoded);
        assertEquals(new String(decode, StandardCharsets.UTF_8), input, "PercentCodec plus for space decoding test");
    }

    @Test
    public void testSafeCharEncodeDecodeObject() throws Exception {
        final PercentCodec percentCodec = new PercentCodec(null, true);
        final String input = "abc123_-.*";
        final Object encoded = percentCodec.encode((Object) input.getBytes(StandardCharsets.UTF_8));
        final String encodedS = new String((byte[]) encoded, StandardCharsets.UTF_8);
        final Object decoded = percentCodec.decode(encoded);
        final String decodedS = new String((byte[]) decoded, StandardCharsets.UTF_8);
        assertEquals(input, encodedS, "Basic PercentCodec safe char encoding test");
        assertEquals(input, decodedS, "Basic PercentCodec safe char decoding test");
    }

    @Test
    public void testUnsafeCharEncodeDecode() throws Exception {
        final PercentCodec percentCodec = new PercentCodec();
        final String input = "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6% ";
        final byte[] encoded = percentCodec.encode(input.getBytes(StandardCharsets.UTF_8));
        final String encodedS = new String(encoded, StandardCharsets.UTF_8);
        final byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, StandardCharsets.UTF_8);
        assertEquals("%CE%B1%CE%B2%CE%B3%CE%B4%CE%B5%CE%B6%25 ", encodedS, "Basic PercentCodec unsafe char encoding test");
        assertEquals(input, decodedS, "Basic PercentCodec unsafe char decoding test");
    }

}
