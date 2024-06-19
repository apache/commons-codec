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
package org.apache.commons.codec.digest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The libc crypt() "$1$" and Apache "$apr1$" MD5-based hash algorithm.
 * <p>
 * Based on the public domain ("beer-ware") C implementation from Poul-Henning Kamp which was found at: <a
 * href="http://www.freebsd.org/cgi/cvsweb.cgi/src/lib/libcrypt/crypt-md5.c?rev=1.1;content-type=text%2Fplain">
 * crypt-md5.c @ freebsd.org</a>
 * </p>
 * <p>
 * Source:
 * </p>
 * <pre>
 * $FreeBSD: src/lib/libcrypt/crypt-md5.c,v 1.1 1999/01/21 13:50:09 brandon Exp $
 * </pre>
 * <p>
 * Conversion to Kotlin and from there to Java in 2012.
 * </p>
 * <p>
 * The C style comments are from the original C code, the ones with "//" from the port.
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @since 1.7
 */
public class Md5Crypt {

    /** The Identifier of the Apache variant. */
    static final String APR1_PREFIX = "$apr1$";

    /** The number of bytes of the final hash. */
    private static final int BLOCKSIZE = 16;

    /** The Identifier of this crypt() variant. */
    static final String MD5_PREFIX = "$1$";

    /** The number of rounds of the big loop. */
    private static final int ROUNDS = 1000;

    /**
     * See {@link #apr1Crypt(byte[], String)} for details.
     * <p>
     * A salt is generated for you using {@link SecureRandom}; your own {@link Random} in
     * {@link #apr1Crypt(byte[], Random)}.
     * </p>
     *
     * @param keyBytes plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @return the hash value
     * @throws IllegalArgumentException when a {@link java.security.NoSuchAlgorithmException} is caught. *
     * @see #apr1Crypt(byte[], String)
     */
    public static String apr1Crypt(final byte[] keyBytes) {
        return apr1Crypt(keyBytes, APR1_PREFIX + B64.getRandomSalt(8));
    }

    /**
     * See {@link #apr1Crypt(byte[], String)} for details.
     * <p>
     * A salt is generated for you using the user provided {@link Random}.
     * </p>
     *
     * @param keyBytes plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param random the instance of {@link Random} to use for generating the salt.
     *              Consider using {@link SecureRandom} for more secure salts.
     * @return the hash value
     * @throws IllegalArgumentException when a {@link java.security.NoSuchAlgorithmException} is caught. *
     * @see #apr1Crypt(byte[], String)
     * @since 1.12
     */
    public static String apr1Crypt(final byte[] keyBytes, final Random random) {
        return apr1Crypt(keyBytes, APR1_PREFIX + B64.getRandomSalt(8, random));
    }

    /**
     * See {@link #apr1Crypt(String, String)} for details.
     * <p>
     * A salt is generated for you using {@link SecureRandom}
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param salt
     *            An APR1 salt. The salt may be null, in which case a salt is generated for you using
     *            {@link SecureRandom}
     * @return the hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String apr1Crypt(final byte[] keyBytes, String salt) {
        // to make the md5Crypt regex happy
        if (salt != null && !salt.startsWith(APR1_PREFIX)) {
            salt = APR1_PREFIX + salt;
        }
        return Md5Crypt.md5Crypt(keyBytes, salt, APR1_PREFIX);
    }

    /**
     * See {@link #apr1Crypt(String, String)} for details.
     * <p>
     * A salt is generated for you using {@link SecureRandom}.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @return the hash value
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     * @see #apr1Crypt(byte[], String)
     */
    public static String apr1Crypt(final String keyBytes) {
        return apr1Crypt(keyBytes.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an Apache htpasswd compatible "$apr1$" MD5 based hash value.
     * <p>
     * The algorithm is identical to the crypt(3) "$1$" one but produces different outputs due to the different salt
     * prefix.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param salt
     *            salt string including the prefix and optionally garbage at the end. The salt may be null, in which
     *            case a salt is generated for you using {@link SecureRandom}.
     * @return the hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String apr1Crypt(final String keyBytes, final String salt) {
        return apr1Crypt(keyBytes.getBytes(StandardCharsets.UTF_8), salt);
    }

    /**
     * Generates a libc6 crypt() compatible "$1$" hash value.
     * <p>
     * See {@link #md5Crypt(byte[], String)} for details.
     * </p>
     * <p>
     * A salt is generated for you using {@link SecureRandom}.
     * </p>
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @return the hash value
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     * @see #md5Crypt(byte[], String)
     */
    public static String md5Crypt(final byte[] keyBytes) {
        return md5Crypt(keyBytes, MD5_PREFIX + B64.getRandomSalt(8));
    }

    /**
     * Generates a libc6 crypt() compatible "$1$" hash value.
     * <p>
     * See {@link #md5Crypt(byte[], String)} for details.
     * </p>
     * <p>
     * A salt is generated for you using the instance of {@link Random} you supply.
     * </p>
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param random
     *            the instance of {@link Random} to use for generating the salt.
     *            Consider using {@link SecureRandom} for more secure salts.
     * @return the hash value
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     * @see #md5Crypt(byte[], String)
     * @since 1.12
     */
    public static String md5Crypt(final byte[] keyBytes, final Random random) {
        return md5Crypt(keyBytes, MD5_PREFIX + B64.getRandomSalt(8, random));
    }

    /**
     * Generates a libc crypt() compatible "$1$" MD5 based hash value.
     * <p>
     * See {@link Crypt#crypt(String, String)} for details. We use {@link SecureRandom} for seed generation by
     * default.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param salt
     *            salt string including the prefix and optionally garbage at the end. The salt may be null, in which
     *            case a salt is generated for you using {@link SecureRandom}.
     * @return the hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String md5Crypt(final byte[] keyBytes, final String salt) {
        return md5Crypt(keyBytes, salt, MD5_PREFIX);
    }

    /**
     * Generates a libc6 crypt() "$1$" or Apache htpasswd "$apr1$" hash value.
     * <p>
     * See {@link Crypt#crypt(String, String)} or {@link #apr1Crypt(String, String)} for details. We use
     * {@link SecureRandom by default}.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null, in which case a salt
     *            is generated for you using {@link SecureRandom}.
     * @param prefix
     *            The salt prefix {@value #APR1_PREFIX}, {@value #MD5_PREFIX}.
     * @return the hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String md5Crypt(final byte[] keyBytes, final String salt, final String prefix) {
        return md5Crypt(keyBytes, salt, prefix, new SecureRandom());
    }

    /**
     * Generates a libc6 crypt() "$1$" or Apache htpasswd "$apr1$" hash value.
     * <p>
     * See {@link Crypt#crypt(String, String)} or {@link #apr1Crypt(String, String)} for details.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash. Each array element is set to {@code 0} before returning.
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null, in which case a salt
     *            is generated for you using {@link SecureRandom}.
     * @param prefix
     *            The salt prefix {@value #APR1_PREFIX}, {@value #MD5_PREFIX}.
     * @param random
     *            the instance of {@link Random} to use for generating the salt.
     *            Consider using {@link SecureRandom} for more secure salts.
     * @return the hash value
     * @throws IllegalArgumentException
     /**
 * Generates an MD5-based crypt hash.
 *
 * @param keyBytes The key bytes to hash.
 * @param salt The salt string for hashing.
 * @param prefix The prefix for the hash.
 * @param random The random generator for salt if not provided.
 * @return The MD5-based crypt hash string.
 * @throws IllegalArgumentException If the salt or prefix does not match the allowed pattern.
 * @since 1.12
 */
public static String md5Crypt(final byte[] keyBytes, final String salt, final String prefix, final Random random) {
    final int keyLen = keyBytes.length;
    String saltString;

    // Determine the salt string
    if (salt == null) {
        saltString = B64.getRandomSalt(8, random);
    } else {
        Objects.requireNonNull(prefix, "prefix");

        // Validate the prefix
        if (prefix.length() < 3 || prefix.charAt(0) != '$' || prefix.charAt(prefix.length() - 1) != '$') {
            throw new IllegalArgumentException("Invalid prefix value: " + prefix);
        }

        // Extract salt from the given string
        final Pattern p = Pattern.compile("^" + prefix.replace("$", "\\$") + "([\\.\\/a-zA-Z0-9]{1,8}).*");
        final Matcher m = p.matcher(salt);
        if (!m.find()) {
            throw new IllegalArgumentException("Invalid salt value: " + salt);
        }
        saltString = m.group(1);
    }

    final byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);

    // MD5 context initialization
    final MessageDigest ctx = DigestUtils.getMd5Digest();
    final MessageDigest ctx1 = DigestUtils.getMd5Digest();

    // Update context with key and salt
    ctx1.update(keyBytes);
    ctx1.update(saltBytes);
    ctx1.update(keyBytes);
    byte[] finalb = ctx1.digest();

    // Additional processing with context
    int ii = keyLen;
    while (ii > 0) {
        ctx.update(finalb, 0, Math.min(ii, 16));
        ii -= 16;
    }

    // Clear sensitive data
    Arrays.fill(finalb, (byte) 0);

    // Another round of processing with context
    ii = keyLen;
    for (int j = 0; ii > 0; ii >>= 1, j++) {
        if ((ii & 1) == 1) {
            ctx.update(finalb[j]);
        } else {
            ctx.update(keyBytes[j]);
        }
    }

    // Build the output string
    final StringBuilder passwd = new StringBuilder(prefix + saltString + "$");
    finalb = ctx.digest();

    // Further processing iterations
    for (int i = 0; i < ROUNDS; i++) {
        ctx1.update((i & 1) != 0 ? keyBytes : finalb, 0, (i % 3 != 0 ? BLOCKSIZE : 0) + (i % 7 != 0 ? saltBytes.length : 0));
        finalb = ctx1.digest();
    }

    return passwd.toString();
}

        // The following was nearly identical to the Sha2Crypt code.
        // Again, the buflen is not really needed.
        // int buflen = MD5_PREFIX.length() - 1 + salt_string.length() + 1 + BLOCKSIZE + 1;
        B64.b64from24bit(finalb[0], finalb[6], finalb[12], 4, passwd);
        B64.b64from24bit(finalb[1], finalb[7], finalb[13], 4, passwd);
        B64.b64from24bit(finalb[2], finalb[8], finalb[14], 4, passwd);
        B64.b64from24bit(finalb[3], finalb[9], finalb[15], 4, passwd);
        B64.b64from24bit(finalb[4], finalb[10], finalb[5], 4, passwd);
        B64.b64from24bit((byte) 0, (byte) 0, finalb[11], 2, passwd);

        /*
         * Don't leave anything around in JVM they could use.
         */
        // Is there a better way to do this with the JVM?
        ctx.reset();
        ctx1.reset();
        Arrays.fill(keyBytes, (byte) 0);
        Arrays.fill(saltBytes, (byte) 0);
        Arrays.fill(finalb, (byte) 0);

        return passwd.toString();
    }

    /**
     * TODO Make private in 2.0.
     *
     * @deprecated TODO Make private in 2.0.
     */
    @Deprecated
    public Md5Crypt() {
        // empty
    }
}
