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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TestCase for BinaryCodec class.
 *
 */
public class BinaryCodecTest {

    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    /** Mask with bit zero-based index 0 raised. */
    private static final int BIT_0 = 0x01;

    /** Mask with bit zero-based index 1 raised. */
    private static final int BIT_1 = 0x02;

    /** Mask with bit zero-based index 2 raised. */
    private static final int BIT_2 = 0x04;

    /** Mask with bit zero-based index 3 raised. */
    private static final int BIT_3 = 0x08;

    /** Mask with bit zero-based index 4 raised. */
    private static final int BIT_4 = 0x10;

    /** Mask with bit zero-based index 5 raised. */
    private static final int BIT_5 = 0x20;

    /** Mask with bit zero-based index 6 raised. */
    private static final int BIT_6 = 0x40;

    /** Mask with bit zero-based index 7 raised. */
    private static final int BIT_7 = 0x80;

    /** An instance of the binary codec. */
    BinaryCodec instance = null;

    @Before
    public void setUp() throws Exception {
        this.instance = new BinaryCodec();
    }

    @After
    public void tearDown() throws Exception {
        this.instance = null;
    }

    // ------------------------------------------------------------------------
    //
    // Test decode(Object)
    //
    // ------------------------------------------------------------------------
    /**
     * Tests for Object decode(Object)
     */
    @Test
    public void testDecodeObjectException() {
        try {
            this.instance.decode(new Object());
        } catch (final DecoderException e) {
            // all is well.
            return;
        }
        fail("Expected DecoderException");
    }

    /**
     * Tests for Object decode(Object)
     */
    @Test
    public void testDecodeObject() throws Exception {
        byte[] bits;
        // With a single raw binary
        bits = new byte[1];
        assertDecodeObject(bits, "00000000");
        bits = new byte[1];
        bits[0] = BIT_0;
        assertDecodeObject(bits, "00000001");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        assertDecodeObject(bits, "00000011");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        assertDecodeObject(bits, "00000111");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        assertDecodeObject(bits, "00001111");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        assertDecodeObject(bits, "00011111");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        assertDecodeObject(bits, "00111111");
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        assertDecodeObject(bits, "01111111");
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "11111111");
        // With a two raw binaries
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0000000011111111");
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0000000111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0000001111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0000011111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0000111111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0001111111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0011111111111111");
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "0111111111111111");
        bits = new byte[2];
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        assertDecodeObject(bits, "1111111111111111");
        assertDecodeObject(new byte[0], null);
    }

    // ------------------------------------------------------------------------
    //
    // Test decode(byte[])
    //
    // ------------------------------------------------------------------------
    /**
     * Utility used to assert the encoded and decoded values.
     *
     * @param bits
     *            the pre-encoded data
     * @param encodeMe
     *            data to encode and compare
     */
    void assertDecodeObject(final byte[] bits, final String encodeMe) throws DecoderException {
        byte[] decoded;
        decoded = (byte[]) instance.decode(encodeMe);
        assertEquals(new String(bits), new String(decoded));
        if (encodeMe == null) {
            decoded = instance.decode((byte[]) null);
        } else {
            decoded = (byte[]) instance.decode((Object) encodeMe.getBytes(CHARSET_UTF8));
        }
        assertEquals(new String(bits), new String(decoded));
        if (encodeMe == null) {
            decoded = (byte[]) instance.decode((char[]) null);
        } else {
            decoded = (byte[]) instance.decode(encodeMe.toCharArray());
        }
        assertEquals(new String(bits), new String(decoded));
    }

    /*
     * Tests for byte[] decode(byte[])
     */
    @Test
    public void testDecodeByteArray() {
        // With a single raw binary
        byte[] bits = new byte[1];
        byte[] decoded = instance.decode("00000000".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0;
        decoded = instance.decode("00000001".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        decoded = instance.decode("00000011".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        decoded = instance.decode("00000111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        decoded = instance.decode("00001111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        decoded = instance.decode("00011111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        decoded = instance.decode("00111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        decoded = instance.decode("01111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("11111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        // With a two raw binaries
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0000000011111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0000000111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0000001111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0000011111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0000111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0001111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0011111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("0111111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.decode("1111111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
    }

    // ------------------------------------------------------------------------
    //
    // Test toByteArray(String)
    //
    // ------------------------------------------------------------------------
    /*
     * Tests for byte[] toByteArray(String)
     */
    @Test
    public void testToByteArrayFromString() {
        // With a single raw binary
        byte[] bits = new byte[1];
        byte[] decoded = instance.toByteArray("00000000");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0;
        decoded = instance.toByteArray("00000001");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        decoded = instance.toByteArray("00000011");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        decoded = instance.toByteArray("00000111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        decoded = instance.toByteArray("00001111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        decoded = instance.toByteArray("00011111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        decoded = instance.toByteArray("00111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        decoded = instance.toByteArray("01111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("11111111");
        assertEquals(new String(bits), new String(decoded));
        // With a two raw binaries
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0000000011111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0000000111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0000001111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0000011111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0000111111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0001111111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0011111111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("0111111111111111");
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = instance.toByteArray("1111111111111111");
        assertEquals(new String(bits), new String(decoded));
        assertEquals(0, instance.toByteArray((String) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test fromAscii(char[])
    //
    // ------------------------------------------------------------------------
    /*
     * Tests for byte[] fromAscii(char[])
     */
    @Test
    public void testFromAsciiCharArray() {
        assertEquals(0, BinaryCodec.fromAscii((char[]) null).length);
        assertEquals(0, BinaryCodec.fromAscii(new char[0]).length);
        // With a single raw binary
        byte[] bits = new byte[1];
        byte[] decoded = BinaryCodec.fromAscii("00000000".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0;
        decoded = BinaryCodec.fromAscii("00000001".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        decoded = BinaryCodec.fromAscii("00000011".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        decoded = BinaryCodec.fromAscii("00000111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        decoded = BinaryCodec.fromAscii("00001111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        decoded = BinaryCodec.fromAscii("00011111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        decoded = BinaryCodec.fromAscii("00111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        decoded = BinaryCodec.fromAscii("01111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("11111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        // With a two raw binaries
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000000011111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000000111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000001111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000011111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000111111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0001111111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0011111111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0111111111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("1111111111111111".toCharArray());
        assertEquals(new String(bits), new String(decoded));
        assertEquals(0, BinaryCodec.fromAscii((char[]) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test fromAscii(byte[])
    //
    // ------------------------------------------------------------------------
    /*
     * Tests for byte[] fromAscii(byte[])
     */
    @Test
    public void testFromAsciiByteArray() {
        assertEquals(0, BinaryCodec.fromAscii((byte[]) null).length);
        assertEquals(0, BinaryCodec.fromAscii(new byte[0]).length);
        // With a single raw binary
        byte[] bits = new byte[1];
        byte[] decoded = BinaryCodec.fromAscii("00000000".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0;
        decoded = BinaryCodec.fromAscii("00000001".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        decoded = BinaryCodec.fromAscii("00000011".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        decoded = BinaryCodec.fromAscii("00000111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        decoded = BinaryCodec.fromAscii("00001111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        decoded = BinaryCodec.fromAscii("00011111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        decoded = BinaryCodec.fromAscii("00111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        decoded = BinaryCodec.fromAscii("01111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("11111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        // With a two raw binaries
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000000011111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000000111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000001111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000011111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0000111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0001111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0011111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("0111111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        bits = new byte[2];
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        decoded = BinaryCodec.fromAscii("1111111111111111".getBytes(CHARSET_UTF8));
        assertEquals(new String(bits), new String(decoded));
        assertEquals(0, BinaryCodec.fromAscii((byte[]) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test encode(byte[])
    //
    // ------------------------------------------------------------------------
    /*
     * Tests for byte[] encode(byte[])
     */
    @Test
    public void testEncodeByteArray() {
        // With a single raw binary
        byte[] bits = new byte[1];
        String l_encoded = new String(instance.encode(bits));
        assertEquals("00000000", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00000001", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00000011", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00000111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00001111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00011111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(instance.encode(bits));
        assertEquals("00111111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(instance.encode(bits));
        assertEquals("01111111", l_encoded);
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("11111111", l_encoded);
        // With a two raw binaries
        bits = new byte[2];
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000000000", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000000001", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000000011", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000000111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000001111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000011111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000000111111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000001111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000011111111", l_encoded);
        // work on the other byte now
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000000111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000001111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000011111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0000111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0001111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0011111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("0111111111111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(instance.encode(bits));
        assertEquals("1111111111111111", l_encoded);
        assertEquals(0, instance.encode((byte[]) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test toAsciiBytes
    //
    // ------------------------------------------------------------------------
    @Test
    public void testToAsciiBytes() {
        // With a single raw binary
        byte[] bits = new byte[1];
        String l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00000000", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00000001", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00000011", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00000111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00001111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00011111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("00111111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("01111111", l_encoded);
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("11111111", l_encoded);
        // With a two raw binaries
        bits = new byte[2];
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000000000", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000000001", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000000011", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000000111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000001111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000011111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000000111111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000001111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000011111111", l_encoded);
        // work on the other byte now
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000000111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000001111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000011111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0000111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0001111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0011111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("0111111111111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiBytes(bits));
        assertEquals("1111111111111111", l_encoded);
        assertEquals(0, BinaryCodec.toAsciiBytes((byte[]) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test toAsciiChars
    //
    // ------------------------------------------------------------------------
    @Test
    public void testToAsciiChars() {
        // With a single raw binary
        byte[] bits = new byte[1];
        String l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00000000", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00000001", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00000011", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00000111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00001111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00011111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("00111111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("01111111", l_encoded);
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("11111111", l_encoded);
        // With a two raw binaries
        bits = new byte[2];
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000000000", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000000001", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000000011", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000000111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000001111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000011111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000000111111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000001111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000011111111", l_encoded);
        // work on the other byte now
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000000111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000001111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000011111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0000111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0001111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0011111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("0111111111111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String(BinaryCodec.toAsciiChars(bits));
        assertEquals("1111111111111111", l_encoded);
        assertEquals(0, BinaryCodec.toAsciiChars((byte[]) null).length);
    }

    // ------------------------------------------------------------------------
    //
    // Test toAsciiString
    //
    // ------------------------------------------------------------------------
    /**
     * Tests the toAsciiString(byte[]) method
     */
    @Test
    public void testToAsciiString() {
        // With a single raw binary
        byte[] bits = new byte[1];
        String l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00000000", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00000001", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00000011", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00000111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00001111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00011111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("00111111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("01111111", l_encoded);
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("11111111", l_encoded);
        // With a two raw binaries
        bits = new byte[2];
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000000000", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000000001", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000000011", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000000111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000001111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000011111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000000111111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000001111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000011111111", l_encoded);
        // work on the other byte now
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000000111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000001111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000011111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0000111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0001111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0011111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("0111111111111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = BinaryCodec.toAsciiString(bits);
        assertEquals("1111111111111111", l_encoded);
    }

    // ------------------------------------------------------------------------
    //
    // Test encode(Object)
    //
    // ------------------------------------------------------------------------
    /*
     * Tests for Object encode(Object)
     */
    @Test
    public void testEncodeObjectNull() throws Exception {
        final Object obj = new byte[0];
        assertEquals(0, ((char[]) instance.encode(obj)).length);
    }

    /*
     * Tests for Object encode(Object)
     */
    @Test
    public void testEncodeObjectException() {
        try {
            instance.encode("");
        } catch (final EncoderException e) {
            // all is well.
            return;
        }
        fail("Expected EncoderException");
    }

    /*
     * Tests for Object encode(Object)
     */
    @Test
    public void testEncodeObject() throws Exception {
        // With a single raw binary
        byte[] bits = new byte[1];
        String l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00000000", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00000001", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00000011", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00000111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00001111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00011111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("00111111", l_encoded);
        bits = new byte[1];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("01111111", l_encoded);
        bits = new byte[1];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("11111111", l_encoded);
        // With a two raw binaries
        bits = new byte[2];
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000000000", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000000001", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000000011", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000000111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000001111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000011111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000000111111", l_encoded);
        bits = new byte[2];
        bits[0] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000001111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000011111111", l_encoded);
        // work on the other byte now
        bits = new byte[2];
        bits[1] = BIT_0;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000000111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000001111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000011111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0000111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0001111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0011111111111111", l_encoded);
        bits = new byte[2];
        bits[1] = BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6;
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("0111111111111111", l_encoded);
        bits = new byte[2];
        bits[0] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        bits[1] = (byte) (BIT_0 | BIT_1 | BIT_2 | BIT_3 | BIT_4 | BIT_5 | BIT_6 | BIT_7);
        l_encoded = new String((char[]) instance.encode((Object) bits));
        assertEquals("1111111111111111", l_encoded);
    }
}
