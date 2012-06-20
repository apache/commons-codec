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

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.Charsets;

/**
 * The libc crypt() "$1$" and Apache "$apr1$" MD5-based hash algorithm.
 * <p>
 * Based on the public domain ("beer-ware") C implementation from Poul-Henning Kamp which was found at:
 * </p>
 * <p>
 * http://www.freebsd.org/cgi/cvsweb.cgi/src/lib/libcrypt/crypt-md5.c?rev=1.1;content-type=text%2Fplain</br>
 * Source: $FreeBSD: src/lib/libcrypt/crypt-md5.c,v 1.1 1999/01/21 13:50:09 brandon Exp $
 * </p>
 * <p>
 * Conversion to Kotlin and from there to Java in 2012.
 * </p>
 * <p>
 * The C style comments are from the original C code, the ones with "//" from the port.
 * </p>
 *
 * <p>This class is immutable and thread-safe.</p>
 *
 * @version $Id$
 * @since 1.7
 */
public class Md5Crypt {

    /**
     * The Identifier of the Apache variant.
     */
    static final String APR1_PREFIX = "$apr1$";

    /**
     * The number of bytes of the final hash.
     */
    private static final int BLOCKSIZE = 16;

    /**
     * The MessageDigest MD5_ALGORITHM.
     */
    private static final String MD5_ALGORITHM = "MD5";

    /**
     * The Identifier of this crypt() variant.
     */
    static final String MD5_PREFIX = "$1$";

    /**
     * The number of rounds of the big loop.
     */
    private static final int ROUNDS = 1000;

    /** See {@link #apr1Crypt(String, String)} for details. */
    public static String apr1Crypt(byte[] keyBytes) throws Exception {
        return apr1Crypt(keyBytes, APR1_PREFIX + B64.getRandomSalt(8));
    }

    /** See {@link #apr1Crypt(String, String)} for details. */
    public static String apr1Crypt(byte[] keyBytes, String salt) throws Exception {
        // to make the md5Crypt regex happy
        if (salt != null && !salt.startsWith(APR1_PREFIX)) {
            salt = APR1_PREFIX + salt;
        }
        return Md5Crypt.md5Crypt(keyBytes, salt, APR1_PREFIX);
    }

    /** See {@link #apr1Crypt(String, String)} for details. */
    public static String apr1Crypt(String keyBytes) throws Exception {
        return apr1Crypt(keyBytes.getBytes(Charsets.UTF_8));
    }

    /**
     * Generates an Apache htpasswd compatible "$apr1$" MD5 based hash value. *
     *
     * The algorithm is identical to the crypt(3) "$1$" one but produces different outputs due to the different salt
     * prefix.
     *
     * @param keyBytes
     *            The plaintext string that should be hashed.
     * @param salt
     *            Salt string including the prefix and optionally garbage at the end. Will be generated randomly if
     *            null.
     */
    public static String apr1Crypt(String keyBytes, String salt) throws Exception {
        return apr1Crypt(keyBytes.getBytes(Charsets.UTF_8), salt);
    }

    /**
     * Generates a libc6 crypt() compatible "$1$" hash value.
     *
     * See {@link Crypt#crypt(String, String)} for details.
     */
    public static String md5Crypt(final byte[] keyBytes) throws Exception {
        return md5Crypt(keyBytes, MD5_PREFIX + B64.getRandomSalt(8));
    }

    /**
     * Generates a libc crypt() compatible "$1$" MD5 based hash value.
     *
     * See {@link Crypt#crypt(String, String)} for details.
     *
     * @param keyBytes
     *            The plaintext string that should be hashed.
     * @param salt
     *            Salt string including the prefix and optionally garbage at the end. Will be generated randomly if
     *            null.
     */
    public static String md5Crypt(byte[] keyBytes, String salt) throws Exception {
        return md5Crypt(keyBytes, salt, MD5_PREFIX);
    }

    /**
     * Generates a libc6 crypt() "$1$" or Apache htpasswd "$apr1$" hash value.
     *
     * See {@link Crypt#crypt(String, String)} or {@link #apr1Crypt(String, String)} for details.
     */
    public static String md5Crypt(final byte[] keyBytes, final String salt, final String prefix) throws Exception {
        int keyLen = keyBytes.length;

        // Extract the real salt from the given string which can be a complete hash string.
        String saltString;
        if (salt == null) {
            saltString = B64.getRandomSalt(8);
        } else {
            Pattern p = Pattern.compile("^" + prefix.replace("$", "\\$") + "([\\.\\/a-zA-Z0-9]{1,8}).*");
            Matcher m = p.matcher(salt);
            if (m == null || !m.find()) {
                throw new IllegalArgumentException("Invalid salt value: " + salt);
            }
            saltString = m.group(1);
        }
        byte[] saltBytes = saltString.getBytes(Charsets.UTF_8);

        MessageDigest ctx = MessageDigest.getInstance(MD5_ALGORITHM);

        /*
         * The password first, since that is what is most unknown
         */
        ctx.update(keyBytes);

        /*
         * Then our magic string
         */
        ctx.update(prefix.getBytes(Charsets.UTF_8));

        /*
         * Then the raw salt
         */
        ctx.update(saltBytes);

        /*
         * Then just as many characters of the MD5(pw,salt,pw)
         */
        MessageDigest ctx1 = MessageDigest.getInstance(MD5_ALGORITHM);
        ctx1.update(keyBytes);
        ctx1.update(saltBytes);
        ctx1.update(keyBytes);
        byte[] finalb = ctx1.digest();
        int ii = keyLen;
        while (ii > 0) {
            ctx.update(finalb, 0, ii > 16 ? 16 : ii);
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
        int j = 0;
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
        StringBuilder passwd = new StringBuilder(prefix + saltString + "$");
        finalb = ctx.digest();

        /*
         * and now, just to make sure things don't run too fast On a 60 Mhz Pentium this takes 34 msec, so you would
         * need 30 seconds to build a 1000 entry dictionary...
         */
        for (int i = 0; i < ROUNDS; i++) {
            ctx1 = MessageDigest.getInstance(MD5_ALGORITHM);
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
