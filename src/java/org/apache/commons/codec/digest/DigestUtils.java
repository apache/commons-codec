/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name without prior 
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

package org.apache.commons.codec.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * Operations to simplifiy common {@link java.security.MessageDigest} tasks.  This
 * class is thread safe.
 *
 * @author Dave Dribin
 * @author David Graham
 * @author Gary Gregory
 */
public class DigestUtils {

    /**
     * Returns a MessageDigest for the given <code>algorithm</code>.
     *
     * @param algorithm The MessageDigest algorithm name.
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught,
     */
    static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns an MD5 MessageDigest.
     *
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught,
     */
    private static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

	/**
	 * Returns an SHA digest.
	 *
	 * @return An SHA digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught,
	 */
	private static MessageDigest getShaDigest() {
        return getDigest("SHA");
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element 
	 * <code>byte[]</code>.
	 *
	 * @param data Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(byte[] data) {
		return getMd5Digest().digest(data);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element 
	 * <code>byte[]</code>.
	 *
	 * @param data Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(String data) {
		return md5(data.getBytes());
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character 
	 * hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(byte[] data) {
		return new String(Hex.encodeHex(md5(data)));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character 
	 * hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(String data) {
		return new String(Hex.encodeHex(md5(data)));
	}

	/**
	 * Calculates the SHA digest and returns the value as a 
	 * <code>byte[]</code>.
	 *
	 * @param data Data to digest
	 * @return SHA digest
	 */
	public static byte[] sha(byte[] data) {
		return getShaDigest().digest(data);
	}

	/**
	 * Calculates the SHA digest and returns the value as a 
	 * <code>byte[]</code>.
	 *
	 * @param data Data to digest
	 * @return SHA digest
	 */
	public static byte[] sha(String data) {
		return sha(data.getBytes());
	}

	/**
     * Calculates the SHA digest and returns the value as a hex string.
     *
     * @param data Data to digest
     * @return SHA digest as a hex string
     */
	public static String shaHex(byte[] data) {
		return new String(Hex.encodeHex(sha(data)));
	}

	/**
	 * Calculates the SHA digest and returns the value as a hex string.
	 *
	 * @param data Data to digest
	 * @return SHA digest as a hex string
	 */
	public static String shaHex(String data) {
		return new String(Hex.encodeHex(sha(data)));
	}

}
