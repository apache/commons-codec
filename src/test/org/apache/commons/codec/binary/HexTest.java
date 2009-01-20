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

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Tests {@link org.apache.commons.codec.binary.Hex}.
 * 
 * @author Apache Software Foundation
 * @version $Id$
 */

public class HexTest extends TestCase {

    public HexTest(String name) {
        super(name);
    }
    
    public void testDecodeArrayOddCharacters() {
        try {
            new Hex().decode(new byte[] { 65 });
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeBadCharacterPos0() {
        try {
            new Hex().decode("q0");
            fail("An exception wasn't thrown when trying to decode an illegal character");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeBadCharacterPos1() {
        try {
            new Hex().decode("0q");
            fail("An exception wasn't thrown when trying to decode an illegal character");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeClassCastException() {
        try {
            new Hex().decode(new int[] { 65 });
            fail("An exception wasn't thrown when trying to decode.");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeHexOddCharacters() {
        try {
            Hex.decodeHex(new char[] { 'A' });
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeStringOddCharacters() {
        try {
            new Hex().decode("6");
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        }
        catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDencodeEmpty() throws DecoderException {
        assertTrue(Arrays.equals(new byte[0], Hex.decodeHex(new char[0])));
        assertTrue(Arrays.equals(new byte[0], new Hex().decode(new byte[0])));
        assertTrue(Arrays.equals(new byte[0], (byte[])new Hex().decode("")));
    }
    
    public void testEncodeClassCastException() {
        try {
            new Hex().encode(new int[] { 65 });
            fail("An exception wasn't thrown when trying to encode.");
        }
        catch (EncoderException e) {
            // Expected exception
        }
    }

    public void testEncodeDecodeRandom() throws DecoderException, EncoderException {
        Random random = new Random();

        Hex hex = new Hex();
        for (int i = 5; i > 0; i--) {
            byte[] data = new byte[random.nextInt(10000) + 1];
            random.nextBytes(data);

            // static API
            char[] encodedChars = Hex.encodeHex(data);
            byte[] decodedBytes = Hex.decodeHex(encodedChars);
            assertTrue(Arrays.equals(data, decodedBytes));
            
            // instance API with array parameter
            byte[] encodedStringBytes = hex.encode(data);
            decodedBytes = hex.decode(encodedStringBytes);
            assertTrue(Arrays.equals(data, decodedBytes));

            // instance API with char[] (Object) parameter
            String dataString = new String(encodedChars);
            char[] encodedStringChars = (char[])hex.encode(dataString);
            decodedBytes = (byte[])hex.decode(encodedStringChars);
            assertTrue(Arrays.equals(dataString.getBytes(), decodedBytes));

            // instance API with String (Object) parameter
            dataString = new String(encodedChars);
            encodedStringChars = (char[])hex.encode(dataString);
            decodedBytes = (byte[])hex.decode(new String(encodedStringChars));
            assertTrue(Arrays.equals(dataString.getBytes(), decodedBytes));
        }
    }

    public void testEncodeEmpty() throws EncoderException {
        assertTrue(Arrays.equals(new char[0], Hex.encodeHex(new byte[0])));
        assertTrue(Arrays.equals(new byte[0], new Hex().encode(new byte[0])));
        assertTrue(Arrays.equals(new char[0], (char[])new Hex().encode("")));
    }

    public void testEncodeZeroes() {
        char[] c = Hex.encodeHex(new byte[36]);
        assertEquals(
            "000000000000000000000000000000000000"
                + "000000000000000000000000000000000000",
            new String(c));
    }

    public void testHelloWorldLowerCaseHex() {
        byte[] b = "Hello World".getBytes();
        final String expected = "48656c6c6f20576f726c64";
        char[] actual;
        actual = Hex.encodeHex(b);
        assertTrue(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, true);
        assertTrue(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, false);
        assertFalse(expected.equals(new String(actual)));
    }

    public void testHelloWorldUpperCaseHex() {
        byte[] b = "Hello World".getBytes();
        final String expected = "48656C6C6F20576F726C64";
        char[] actual;
        actual = Hex.encodeHex(b);
        assertFalse(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, true);
        assertFalse(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, false);
        assertTrue(expected.equals(new String(actual)));
    }
}
