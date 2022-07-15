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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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
     * @param keyBytes plaintext string to hash.
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
     * @param keyBytes plaintext string to hash.
     * @param random the instance of {@link Random} to use for generating the salt. Consider using {@link SecureRandom}
     *            or {@link ThreadLocalRandom}.
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
     *            plaintext string to hash.
     * @param salt
     *            An APR1 salt. The salt may be null, in which case a salt is generated for you using
     *            {@link ThreadLocalRandom}; for more secure salts consider using {@link SecureRandom} to generate your
     *            own salts.
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
     * A salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     * {@link SecureRandom} to generate your own salts and calling {@link #apr1Crypt(byte[], String)}.
     * </p>
     *
     * @param keyBytes
     *            plaintext string to hash.
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
     *
     * @param keyBytes
     *            plaintext string to hash.
     * @param salt
     *            salt string including the prefix and optionally garbage at the end. The salt may be null, in which
     *            case a salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     *            {@link SecureRandom} to generate your own salts.
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
     *</p>
     * <p>
     * A salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     * {@link SecureRandom} to generate your own salts and calling {@link #md5Crypt(byte[], String)}.
     * </p>
     * @param keyBytes
     *            plaintext string to hash.
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
     *</p>
     * <p>
     * A salt is generated for you using the instance of {@link Random} you supply.
     * </p>
     * @param keyBytes
     *            plaintext string to hash.
     * @param random
     *            the instance of {@link Random} to use for generating the salt. Consider using {@link SecureRandom}
     *            or {@link ThreadLocalRandom}.
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
     *            plaintext string to hash.
     * @param salt
     *            salt string including the prefix and optionally garbage at the end. The salt may be null, in which
     *            case a salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     *            {@link SecureRandom} to generate your own salts.
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
     *            plaintext string to hash.
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null, in which case a salt
     *            is generated for you using {@link ThreadLocalRandom}; for more secure salts consider
     *            using {@link SecureRandom} to generate your own salts.
     * @param prefix
     *            salt prefix
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
     *            plaintext string to hash.
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null, in which case a salt
     *            is generated for you using {@link ThreadLocalRandom}; for more secure salts consider
     *            using {@link SecureRandom} to generate your own salts.
     * @param prefix
     *            salt prefix
     * @param random
     *            the instance of {@link Random} to use for generating the salt. Consider using {@link SecureRandom}
     *            or {@link ThreadLocalRandom}.
     * @return the hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     * @since 1.12
     */
    public static String md5Crypt(final byte[] keyBytes, final String salt, final String prefix, final Random random) {
        final int keyLen = keyBytes.length;

        // Extract the real salt from the given string which can be a complete hash string.
        final String saltString;
        if (salt == null) {
            saltString = B64.getRandomSalt(8, random);
        } else {
            final Pattern p = Pattern.compile("^" + prefix.replace("$", "\\$") + "([\\.\\/a-zA-Z0-9]{1,8}).*");
            final Matcher m = p.matcher(salt);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid salt value: " + salt);
            }
            saltString = m.group(1);
        }
        final byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);

        final MessageDigest ctx = DigestUtils.getMd5Digest();

        /*
         * The password first, since that is what is most unknown
         */
        ctx.update(keyBytes);

        /*
         * Then our magic string
         */
        ctx.update(prefix.getBytes(StandardCharsets.UTF_8));

        /*
         * Then the raw salt
         */
        ctx.update(saltBytes);

        /*
         * Then just as many characters of the MD5(pw,salt,pw)
         */
        MessageDigest ctx1 = DigestUtils.getMd5Digest();
        ctx1.update(keyBytes);
        ctx1.update(saltBytes);
        ctx1.update(keyBytes);
        byte[] finalb = ctx1.digest();
        int ii = keyLen;
        while (ii > 0) {
            ctx.update(finalb, 0, Math.min(ii, 16));
            ii -= 16;
        }

        /*
         * Don't leave anything around in vm they could use.
         */
        Arrays.fill(finalb, (byte) 0);

        /*
         * Then something really weird...
         */
        ii = keyLen;
        final int j = 0;
        while (ii > 0) {
            if ((ii & 1) == 1) {
                ctx.update(finalb[j]);
            } else {
                ctx.update(keyBytes[j]);
            }
            ii >>= 1;
        }

        /*
         * Now make the output string
         */
        final StringBuilder passwd = new StringBuilder(prefix + saltString + "$");
        finalb = ctx.digest();

        /*
         * and now, just to make sure things don't run too fast On a 60 Mhz Pentium this takes 34 milliseconds, so you
         * would need 30 seconds to build a 1000 entry dictionary...
         */
        for (int i = 0; i < ROUNDS; i++) {
            ctx1 = DigestUtils.getMd5Digest();
            if ((i & 1) != 0) {
                ctx1.update(keyBytes);
            } else {
                ctx1.update(finalb, 0, BLOCKSIZE);
            }

            if (i % 3 != 0) {
                ctx1.update(saltBytes);
            }

            if (i % 7 != 0) {
                ctx1.update(keyBytes);
            }

            if ((i & 1) != 0) {
                ctx1.update(finalb, 0, BLOCKSIZE);
            } else {
                ctx1.update(keyBytes);
            }
            finalb = ctx1.digest();
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
         * Don't leave anything around in vm they could use.
         */
        // Is there a better way to do this with the JVM?
        ctx.reset();
        ctx1.reset();
        Arrays.fill(keyBytes, (byte) 0);
        Arrays.fill(saltBytes, (byte) 0);
        Arrays.fill(finalb, (byte) 0);

        return passwd.toString();
    }
}
