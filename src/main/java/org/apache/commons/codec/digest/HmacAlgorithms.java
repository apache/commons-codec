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

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Standard {@link HmacUtils} algorithm names from the <cite>Java Cryptography Architecture Standard Algorithm Name
 * Documentation</cite>.
 *
 * <p>
 * <strong>Note: Not all JCE implementations support all the algorithms in this enum.</strong>
 * </p>
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html#SunJCEProvider"> Java
 *      6 Cryptography Architecture Sun Providers Documentation</a>
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#SunJCEProvider"> Java
 *      7 Cryptography Architecture Sun Providers Documentation</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#SunJCEProvider"> Java
 *      8 Cryptography Architecture Sun Providers Documentation</a>
 * @since 1.10
 * @version $Id$
 */
public enum HmacAlgorithms {

    /**
     * The HmacMD5 Message Authentication Code (MAC) algorithm specified in RFC 2104 and RFC 1321.
     * <p>
     * Every implementation of the Java platform is required to support this standard MAC algorithm.
     * </p>
     */
    HMAC_MD5("HmacMD5"),

    /**
     * The HmacSHA1 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2.
     * <p>
     * Every implementation of the Java platform is required to support this standard MAC algorithm.
     * </p>
     */
    HMAC_SHA_1("HmacSHA1"),

    /**
     * The HmacSHA224 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2.
     * <p>
     * Every implementation of the Java 8+ platform is required to support this standard MAC algorithm.
     * </p>
     * @since 1.11
     */
    HMAC_SHA_224("HmacSHA224"),

    /**
     * The HmacSHA256 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2.
     * <p>
     * Every implementation of the Java platform is required to support this standard MAC algorithm.
     * </p>
     */
    HMAC_SHA_256("HmacSHA256"),

    /**
     * The HmacSHA384 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2.
     * <p>
     * This MAC algorithm is <em>optional</em>; not all implementations support it.
     * </p>
     */
    HMAC_SHA_384("HmacSHA384"),

    /**
     * The HmacSHA512 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2.
     * <p>
     * This MAC algorithm is <em>optional</em>; not all implementations support it.
     * </p>
     */
    HMAC_SHA_512("HmacSHA512");

    private final String name;

    private HmacAlgorithms(final String algorithm) {
        this.name = algorithm;
    }

    /**
     * Returns an initialized <code>Mac</code> for the this algorithm.
     * <p>
     * Every implementation of the Java platform is required to support this standard Mac algorithm.
     * </p>
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public Mac getHmac(final byte[] key) {
        return HmacUtils.getInitializedMac(name, key);
    }

    /**
     * Gets the algorithm name.
     *
     * @return the algorithm name.
     * @since 1.11
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) for the given key and value.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest (maybe empty or null)
     * @return HMAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public byte[] hmac(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmac(key).doFinal(valueToDigest);
        } catch (final IllegalStateException e) {
            // cannot happen
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) for the given key and value.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest. The InputStream must not be null and will not be closed.
     * @return HMAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public byte[] hmac(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacUtils.updateHmac(getHmac(key), valueToDigest).doFinal();
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) for the given key and value.
     * The Strings are converted to bytes using the UTF-8 charset.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest (maybe empty or null)
     * @return HMAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public byte[] hmac(final String key, final String valueToDigest) {
        return hmac(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest (maybe empty or null)
     * @return HMAC for the given key and value as a hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public String hmacHex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmac(key, valueToDigest));
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest. The InputStream must not be null and will not be closed.
     * @return HMAC for the given key and value as a hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public String hmacHex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmac(key, valueToDigest));
    }

    /**
     * Returns a keyed-Hash Message Authentication Code (HMAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            The key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) to digest (maybe empty or null)
     * @return HMAC for the given key and value as a hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @since 1.11
     */
    public String hmacHex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmac(key, valueToDigest));
    }

    /**
     * Returns whether this algorithm is available
     *
     * @return whether this algorithm is available
     * @since 1.11
     */
    public boolean isAvailable() {
        try {
            Mac.getInstance(name);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    /**
     * The algorithm name
     *
     * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 6 Cryptography Architecture Sun Providers Documentation</a>
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 7 Cryptography Architecture Sun Providers Documentation</a>
     * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 8 Cryptography Architecture Sun Providers Documentation</a>
     * @return The algorithm name ("HmacSHA512" for example)
     */
    @Override
    public String toString() {
        return name;
    }

}
