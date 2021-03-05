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
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * GNU libc crypt(3) compatible hash method.
 * <p>
 * See {@link #crypt(String, String)} for further details.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @since 1.7
 */
public class Crypt {

    /**
     * Encrypts a password in a crypt(3) compatible way.
     * <p>
     * A random salt and the default algorithm (currently SHA-512) are used. See {@link #crypt(String, String)} for
     * details.
     * </p>
     * <p>
     * A salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     * {@link SecureRandom} to generate your own salts and calling {@link #crypt(byte[], String)}.
     * </p>
     *
     * @param keyBytes
     *            plaintext password
     * @return hash value
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String crypt(final byte[] keyBytes) {
        return crypt(keyBytes, null);
    }

    /**
     * Encrypts a password in a crypt(3) compatible way.
     * <p>
     * If no salt is provided, a random salt and the default algorithm (currently SHA-512) will be used. See
     * {@link #crypt(String, String)} for details.
     *
     * @param keyBytes
     *            plaintext password
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null,
     *            in which case a salt is generated for you using {@link ThreadLocalRandom};
     *            for more secure salts consider using {@link SecureRandom} to
     *            generate your own salts.
     * @return hash value
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String crypt(final byte[] keyBytes, final String salt) {
        if (salt == null) {
            return Sha2Crypt.sha512Crypt(keyBytes);
        }
        if (salt.startsWith(Sha2Crypt.SHA512_PREFIX)) {
            return Sha2Crypt.sha512Crypt(keyBytes, salt);
        }
        if (salt.startsWith(Sha2Crypt.SHA256_PREFIX)) {
            return Sha2Crypt.sha256Crypt(keyBytes, salt);
        }
        if (salt.startsWith(Md5Crypt.MD5_PREFIX)) {
            return Md5Crypt.md5Crypt(keyBytes, salt);
        }
        return UnixCrypt.crypt(keyBytes, salt);
    }

    /**
     * Calculates the digest using the strongest crypt(3) algorithm.
     * <p>
     * A random salt and the default algorithm (currently SHA-512) are used.
     * </p>
     * <p>
     * A salt is generated for you using {@link ThreadLocalRandom}; for more secure salts consider using
     * {@link SecureRandom} to generate your own salts and calling {@link #crypt(String, String)}.
     * </p>
     *
     * @see #crypt(String, String)
     * @param key
     *            plaintext password
     * @return hash value
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
     */
    public static String crypt(final String key) {
        return crypt(key, null);
    }

    /**
     * Encrypts a password in a crypt(3) compatible way.
     * <p>
     * The exact algorithm depends on the format of the salt string:
     * <ul>
     * <li>SHA-512 salts start with {@code $6$} and are up to 16 chars long.
     * <li>SHA-256 salts start with {@code $5$} and are up to 16 chars long
     * <li>MD5 salts start with {@code $1$} and are up to 8 chars long
     * <li>DES, the traditional UnixCrypt algorithm is used with only 2 chars
     * <li>Only the first 8 chars of the passwords are used in the DES algorithm!
     * </ul>
     * The magic strings {@code "$apr1$"} and {@code "$2a$"} are not recognized by this method as its output should be
     * identical with that of the libc implementation.
     * <p>
     * The rest of the salt string is drawn from the set {@code [a-zA-Z0-9./]} and is cut at the maximum length of if a
     * {@code "$"} sign is encountered. It is therefore valid to enter a complete hash value as salt to e.g. verify a
     * password with:
     *
     * <pre>
     * storedPwd.equals(crypt(enteredPwd, storedPwd))
     * </pre>
     * <p>
     * The resulting string starts with the marker string ({@code $n$}), where n is the same as the input salt.
     * The salt is then appended, followed by a {@code "$"} sign.
     * This is followed by the actual hash value.
     * For DES the string only contains the salt and actual hash.
     * The total length is dependent on the algorithm used:
     * <ul>
     * <li>SHA-512: 106 chars
     * <li>SHA-256: 63 chars
     * <li>MD5: 34 chars
     * <li>DES: 13 chars
     * </ul>
     * <p>
     * Example:
     *
     * <pre>
     *      crypt("secret", "$1$xxxx") =&gt; "$1$xxxx$aMkevjfEIpa35Bh3G4bAc."
     *      crypt("secret", "xx") =&gt; "xxWAum7tHdIUw"
     * </pre>
     * <p>
     * This method comes in a variation that accepts a byte[] array to support input strings that are not encoded in
     * UTF-8 but e.g. in ISO-8859-1 where equal characters result in different byte values.
     *
     * @see "The man page of the libc crypt (3) function."
     * @param key
     *            plaintext password as entered by the used
     * @param salt
     *            real salt value without prefix or "rounds=". The salt may be null, in which case a
     *            salt is generated for you using {@link ThreadLocalRandom}; for more secure salts
     *            consider using {@link SecureRandom} to generate your own salts.
     * @return hash value, i.e. encrypted password including the salt string
     * @throws IllegalArgumentException
     *             if the salt does not match the allowed pattern
     * @throws IllegalArgumentException
     *             when a {@link java.security.NoSuchAlgorithmException} is caught. *
     */
    public static String crypt(final String key, final String salt) {
        return crypt(key.getBytes(StandardCharsets.UTF_8), salt);
    }
}
