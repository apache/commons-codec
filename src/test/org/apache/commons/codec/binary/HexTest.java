/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//codec/src/test/org/apache/commons/codec/binary/HexTest.java,v 1.2 2003/07/30 22:34:18 tobrien Exp $
 * $Revision: 1.2 $
 * $Date: 2003/07/30 22:34:18 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

/**
 * 
 * @author <a href="mailto:siege@preoccupied.net">Christopher O'Brien</a>
 * @author Tim O'Brien
 */

public class HexTest extends TestCase {

    public HexTest(String name) {
        super(name);
    }

    public void testEncodeEmpty() throws Exception {
        char[] c = Hex.encodeHex(new byte[0]);
        assertTrue(Arrays.equals(new char[0], c));
    }

    public void testEncodeZeroes() throws Exception {
        char[] c = Hex.encodeHex(new byte[36]);
        assertEquals(
            "000000000000000000000000000000000000"
                + "000000000000000000000000000000000000",
            new String(c));
    }

    public void testHelloWorld() throws Exception {
        byte[] b = "Hello World".getBytes();
        char[] c = Hex.encodeHex(b);
        assertEquals("48656c6c6f20576f726c64", new String(c));
    }

    public void testEncodeDecodeRandom() throws Exception {
        Random random = new Random();

        for (int i = 5; i > 0; i--) {
            byte[] data = new byte[random.nextInt(10000) + 1];
            random.nextBytes(data);

            char[] enc = Hex.encodeHex(data);
            byte[] data2 = Hex.decodeHex(enc);

            assertTrue(Arrays.equals(data, data2));
        }
    }

    public void testOddCharacters() throws Exception {

        boolean exceptionThrown = false;

        try {
            char[] singleChar = new char[1];
            singleChar[0] = 'a';

            Hex.decodeHex( singleChar );
        }
        catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue( "An exception wasn't thrown when trying to " +
                    "decode an odd number of characters", exceptionThrown );


    }

}
