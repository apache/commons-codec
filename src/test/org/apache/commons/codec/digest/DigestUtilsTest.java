/* 
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 */
package org.apache.commons.codec.digest;

import junit.framework.TestCase;

/**
 * Tests Digest methods.
 *
 * @author Dave Dribin
 * @author David Graham
 */
public class DigestUtilsTest extends TestCase {

	public void testMd5Hex() {
		// Examples from RFC 1321
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtils.md5Hex(""));

		assertEquals("0cc175b9c0f1b6a831c399e269772661", DigestUtils.md5Hex("a"));

		assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex("abc"));

		assertEquals(
			"f96b697d7cb7938d525a2f31aaf161d0",
			DigestUtils.md5Hex("message digest"));

		assertEquals(
			"c3fcd3d76192e4007dfb496cca67e13b",
			DigestUtils.md5Hex("abcdefghijklmnopqrstuvwxyz"));

		assertEquals(
			"d174ab98d277d9f5a5611c2c9f419d9f",
			DigestUtils.md5Hex(
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "abcdefghijklmnopqrstuvwxyz"
					+ "0123456789"));

		assertEquals(
			"57edf4a22be3c955ac49da2e2107b67a",
			DigestUtils.md5Hex(
				"1234567890123456789012345678901234567890"
					+ "1234567890123456789012345678901234567890"));
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
		assertEquals(
			"a9993e364706816aba3e25717850c26c9cd0d89d",
			DigestUtils.shaHex("abc"));

		assertEquals(
			"84983e441c3bd26ebaae4aa1f95129e5e54670f1",
			DigestUtils.shaHex(
				"abcdbcdecdefdefgefghfghighij"
					+ "hijkijkljklmklmnlmnomnopnopq"));
	}

}
