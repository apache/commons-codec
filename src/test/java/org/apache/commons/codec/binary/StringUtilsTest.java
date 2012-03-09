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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests {@link StringUtils}
 * 
 * @version $Id$
 */
public class StringUtilsTest {

    private static final byte[] BYTES_FIXTURE = {'a','b','c'};

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
        String charsetName = "ISO-8859-1";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesIso8859_1(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    private void testGetBytesUnchecked(String charsetName) throws UnsupportedEncodingException {
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUnchecked(STRING_FIXTURE, charsetName);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUsAscii() throws UnsupportedEncodingException {
        String charsetName = "US-ASCII";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUsAscii(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUtf16() throws UnsupportedEncodingException {
        String charsetName = "UTF-16";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUtf16(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUtf16Be() throws UnsupportedEncodingException {
        String charsetName = "UTF-16BE";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUtf16Be(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUtf16Le() throws UnsupportedEncodingException {
        String charsetName = "UTF-16LE";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUtf16Le(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUtf8() throws UnsupportedEncodingException {
        String charsetName = "UTF-8";
        testGetBytesUnchecked(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringUtils.getBytesUtf8(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void testGetBytesUncheckedBadName() {
        try {
            StringUtils.getBytesUnchecked(STRING_FIXTURE, "UNKNOWN");
            Assert.fail("Expected " + IllegalStateException.class.getName());
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    private void testNewString(String charsetName) throws UnsupportedEncodingException {
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newString(BYTES_FIXTURE, charsetName);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringBadEnc() {
        try {
            StringUtils.newString(BYTES_FIXTURE, "UNKNOWN");
            Assert.fail("Expected " + IllegalStateException.class.getName());
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    @Test
    public void testNewStringIso8859_1() throws UnsupportedEncodingException {
        String charsetName = "ISO-8859-1";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringIso8859_1(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUsAscii() throws UnsupportedEncodingException {
        String charsetName = "US-ASCII";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringUsAscii(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16() throws UnsupportedEncodingException {
        String charsetName = "UTF-16";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringUtf16(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16Be() throws UnsupportedEncodingException {
        String charsetName = "UTF-16BE";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringUtf16Be(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf16Le() throws UnsupportedEncodingException {
        String charsetName = "UTF-16LE";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringUtf16Le(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNewStringUtf8() throws UnsupportedEncodingException {
        String charsetName = "UTF-8";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringUtils.newStringUtf8(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }
}
