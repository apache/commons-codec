/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.codec.digest;

import junit.framework.TestCase;

/**
 * Tests Digest methods.
 * 
 * @author Apache Software Foundation
 */
public class DigestUtilsTest extends TestCase {

    public void testInternalNoSuchAlgorithmException() {
        try {
            DigestUtils.getDigest("Bogus Bogus");
            fail("A RuntimeException should have been thrown.");
        } catch (RuntimeException e) {
            // Expected exception.
        }
    }

    public void testMd5Hex() {
        // Examples from RFC 1321
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtils.md5Hex(""));

        assertEquals("0cc175b9c0f1b6a831c399e269772661", DigestUtils.md5Hex("a"));

        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex("abc"));

        assertEquals("f96b697d7cb7938d525a2f31aaf161d0", DigestUtils.md5Hex("message digest"));

        assertEquals("c3fcd3d76192e4007dfb496cca67e13b", DigestUtils.md5Hex("abcdefghijklmnopqrstuvwxyz"));

        assertEquals(
            "d174ab98d277d9f5a5611c2c9f419d9f",
            DigestUtils.md5Hex("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"));

        assertEquals(
            "57edf4a22be3c955ac49da2e2107b67a",
            DigestUtils.md5Hex("1234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890"));
    }

    /**
	 * An MD5 hash converted to hex should always be 32 characters.
	 */
    public void testMD5HexLength() {
        String hashMe = "this is some string that is longer than 32 characters";
        String hash = DigestUtils.md5Hex(hashMe.getBytes());
        assertEquals(32, hash.length());

        hashMe = "length < 32";
        hash = DigestUtils.md5Hex(hashMe.getBytes());
        assertEquals(32, hash.length());
    }

    /**
	 * An MD5 hash should always be a 16 element byte[].
	 */
    public void testMD5Length() {
        String hashMe = "this is some string that is longer than 16 characters";
        byte[] hash = DigestUtils.md5(hashMe.getBytes());
        assertEquals(16, hash.length);

        hashMe = "length < 16";
        hash = DigestUtils.md5(hashMe.getBytes());
        assertEquals(16, hash.length);
    }

    public void testShaHex() {
        // Examples from FIPS 180-1
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex("abc"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex("abc".getBytes()));

        assertEquals(
            "84983e441c3bd26ebaae4aa1f95129e5e54670f1",
            DigestUtils.shaHex("abcdbcdecdefdefgefghfghighij" + "hijkijkljklmklmnlmnomnopnopq"));
    }

}
