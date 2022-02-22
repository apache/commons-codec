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

package org.apache.commons.codec.binary;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link StringUtils}
 *
 */
public class StringUtilsTest {

    private static final byte[] BYTES_FIXTURE = {'a','b','c'};

    // This is valid input for UTF-16BE
    private static final byte[] BYTES_FIXTURE_16BE = {0, 'a', 0, 'b', 0, 'c'};

    // This is valid for UTF-16LE
    private static final byte[] BYTES_FIXTURE_16LE = {'a', 0, 'b', 0, 'c', 0};

    private static final String STRING_FIXTURE = "ABC";

    /**
     * We could make the constructor private but there does not seem to be a point to jumping through extra code hoops
     * to restrict instantiation right now.
     */
    @Test
    public void testConstructor() {
        new StringUtils();
    }

    @Test
    public void testGetBytesIso8859_1() throws UnsupportedEncodingException {
        final String charsetName = "ISO-8859-1";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesIso8859_1(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    private void testGetBytesUnchecked(final String charsetName) throws UnsupportedEncodingException {
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUnchecked(STRING_FIXTURE, charsetName);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUsAscii() throws UnsupportedEncodingException {
        final String charsetName = "US-ASCII";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUsAscii(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUtf16() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUtf16(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUtf16Be() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16BE";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUtf16Be(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUtf16Le() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16LE";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUtf16Le(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUtf8() throws UnsupportedEncodingException {
        final String charsetName = "UTF-8";
        testGetBytesUnchecked(charsetName);
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        final byte[] actual = StringUtils.getBytesUtf8(STRING_FIXTURE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBytesUncheckedBadName() {
        assertThrows(IllegalStateException.class, () -> StringUtils.getBytesUnchecked(STRING_FIXTURE, "UNKNOWN"));
    }

    @Test
    public void testGetBytesUncheckedNullInput() {
        assertNull(StringUtils.getBytesUnchecked(null, "UNKNOWN"));
    }

    private void testNewString(final String charsetName) throws UnsupportedEncodingException {
        final String expected = new String(BYTES_FIXTURE, charsetName);
        final String actual = StringUtils.newString(BYTES_FIXTURE, charsetName);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringBadEnc() {
        assertThrows(IllegalStateException.class, () -> StringUtils.newString(BYTES_FIXTURE, "UNKNOWN"));
    }

    @Test
    public void testNewStringNullInput() {
        assertNull(StringUtils.newString(null, "UNKNOWN"));
    }

    @Test
    public void testNewStringNullInput_CODEC229() {
        assertNull(StringUtils.newStringUtf8(null));
        assertNull(StringUtils.newStringIso8859_1(null));
        assertNull(StringUtils.newStringUsAscii(null));
        assertNull(StringUtils.newStringUtf16(null));
        assertNull(StringUtils.newStringUtf16Be(null));
        assertNull(StringUtils.newStringUtf16Le(null));
    }

    @Test
    public void testNewStringIso8859_1() throws UnsupportedEncodingException {
        final String charsetName = "ISO-8859-1";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE, charsetName);
        final String actual = StringUtils.newStringIso8859_1(BYTES_FIXTURE);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUsAscii() throws UnsupportedEncodingException {
        final String charsetName = "US-ASCII";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE, charsetName);
        final String actual = StringUtils.newStringUsAscii(BYTES_FIXTURE);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE, charsetName);
        final String actual = StringUtils.newStringUtf16(BYTES_FIXTURE);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16Be() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16BE";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE_16BE, charsetName);
        final String actual = StringUtils.newStringUtf16Be(BYTES_FIXTURE_16BE);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16Le() throws UnsupportedEncodingException {
        final String charsetName = "UTF-16LE";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE_16LE, charsetName);
        final String actual = StringUtils.newStringUtf16Le(BYTES_FIXTURE_16LE);
        assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf8() throws UnsupportedEncodingException {
        final String charsetName = "UTF-8";
        testNewString(charsetName);
        final String expected = new String(BYTES_FIXTURE, charsetName);
        final String actual = StringUtils.newStringUtf8(BYTES_FIXTURE);
        assertEquals(expected, actual);
    }

    @Test
    public void testEqualsString() {
        assertTrue(StringUtils.equals(null, null));
        assertFalse(StringUtils.equals("abc", null));
        assertFalse(StringUtils.equals(null, "abc"));
        assertTrue(StringUtils.equals("abc", "abc"));
        assertFalse(StringUtils.equals("abc", "abcd"));
        assertFalse(StringUtils.equals("abcd", "abc"));
        assertFalse(StringUtils.equals("abc", "ABC"));
    }

    @Test
    public void testEqualsCS1() {
        assertFalse(StringUtils.equals(new StringBuilder("abc"), null));
        assertFalse(StringUtils.equals(null, new StringBuilder("abc")));
        assertTrue(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("abc")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("abcd")));
        assertFalse(StringUtils.equals(new StringBuilder("abcd"), new StringBuilder("abc")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("ABC")));
    }

    @Test
    public void testEqualsCS2() {
        assertTrue(StringUtils.equals("abc", new StringBuilder("abc")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), "abcd"));
        assertFalse(StringUtils.equals("abcd", new StringBuilder("abc")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), "ABC"));
    }

    @Test
    public void testByteBufferUtf8() {
        assertNull(StringUtils.getByteBufferUtf8(null), "Should be null safe");
        final String text = "asdhjfhsadiogasdjhagsdygfjasfgsdaksjdhfk";
        final ByteBuffer bb = StringUtils.getByteBufferUtf8(text);
        assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), bb.array());
    }
}
