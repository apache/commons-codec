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
import junit.framework.TestCase;

/**
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: $
 */
public class StringBytesUtilsTest extends TestCase {

    private static byte[] BYTES_FIXTURE;

    private static final String STRING_FIXTURE = "ABC";
    {
        try {
            BYTES_FIXTURE = "abc".getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public void testGetBytesIso8859_1() throws UnsupportedEncodingException {
        String charsetName = "ISO-8859-1";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesIso8859_1(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetBytesUsAscii() throws UnsupportedEncodingException {
        String charsetName = "US-ASCII";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesUsAscii(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetBytesUtf16() throws UnsupportedEncodingException {
        String charsetName = "UTF-16";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesUtf16(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetBytesUtf16Be() throws UnsupportedEncodingException {
        String charsetName = "UTF-16BE";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesUtf16Be(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetBytesUtf16Le() throws UnsupportedEncodingException {
        String charsetName = "UTF-16LE";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesUtf16Le(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetBytesUtf8() throws UnsupportedEncodingException {
        String charsetName = "UTF-8";
        testGetSupportedBytes(charsetName);
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getBytesUtf8(STRING_FIXTURE);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    private void testGetSupportedBytes(String charsetName) throws UnsupportedEncodingException {
        byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        byte[] actual = StringBytesUtils.getSupportedBytes(STRING_FIXTURE, charsetName);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    public void testGetSupportedBytesBadEnc() {
        try {
            StringBytesUtils.getSupportedBytes(STRING_FIXTURE, "UNKNOWN");
            Assert.fail("Expected " + IllegalStateException.class.getName());
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    private void testNewString(String charsetName) throws UnsupportedEncodingException {
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newString(BYTES_FIXTURE, charsetName);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringBadEnc() {
        try {
            StringBytesUtils.newString(BYTES_FIXTURE, "UNKNOWN");
            Assert.fail("Expected " + IllegalStateException.class.getName());
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    public void testNewStringIso8859_1() throws UnsupportedEncodingException {
        String charsetName = "ISO-8859-1";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringIso8859_1(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringUsAscii() throws UnsupportedEncodingException {
        String charsetName = "US-ASCII";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringUsAscii(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringUtf16() throws UnsupportedEncodingException {
        String charsetName = "UTF-16";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringUtf16(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringUtf16Be() throws UnsupportedEncodingException {
        String charsetName = "UTF-16BE";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringUtf16Be(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringUtf16Le() throws UnsupportedEncodingException {
        String charsetName = "UTF-16LE";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringUtf16Le(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

    public void testNewStringUtf8() throws UnsupportedEncodingException {
        String charsetName = "UTF-8";
        testNewString(charsetName);
        String expected = new String(BYTES_FIXTURE, charsetName);
        String actual = StringBytesUtils.newStringUtf8(BYTES_FIXTURE);
        Assert.assertEquals(expected, actual);
    }

}
