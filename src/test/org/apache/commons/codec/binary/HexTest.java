/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their name without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * @author <a href="mailto:siege@preoccupied.net">Christopher O'Brien</a>
 * @author Tim O'Brien
 * @author Gary Gregory
 * @version $Id: HexTest.java,v 1.6 2003/11/03 19:04:34 ggregory Exp $
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

    public void testHelloWorld() {
        byte[] b = "Hello World".getBytes();
        char[] c = Hex.encodeHex(b);
        assertEquals("48656c6c6f20576f726c64", new String(c));
    }
}
