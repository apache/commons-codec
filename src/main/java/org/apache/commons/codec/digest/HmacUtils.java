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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.StringUtils;

/**
 * Simplifies common {@link javax.crypto.Mac} tasks. This class is immutable and thread-safe.
 *
 * <p>
 * <strong>Note: Not all JCE implementations supports all algorithms. If not supported, an IllegalArgumentException is
 * thrown.</strong>
 * </p>
 *
 * @since 1.10
 * @version $Id$
 */
public final class HmacUtils {

    private static final int STREAM_BUFFER_LENGTH = 1024;

    /**
     * Returns an initialized <code>Mac</code> for the HmacMD5 algorithm.
     * <p>
     * Every implementation of the Java platform is required to support this standard Mac algorithm.
     * </p>
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.HMAC_MD5.getHmac(byte[])}
     */
    @Deprecated
    public static Mac getHmacMd5(final byte[] key) {
        return HmacAlgorithms.HMAC_MD5.getHmac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the HmacSHA1 algorithm.
     * <p>
     * Every implementation of the Java platform is required to support this standard Mac algorithm.
     * </p>
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.HMAC_SHA1.getHmac(byte[])}
     */
    @Deprecated
    public static Mac getHmacSha1(final byte[] key) {
        return HmacAlgorithms.HMAC_SHA_1.getHmac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the HmacSHA256 algorithm.
     * <p>
     * Every implementation of the Java platform is required to support this standard Mac algorithm.
     * </p>
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.HMAC_SHA256.getHmac(byte[])}
     */
    @Deprecated
    public static Mac getHmacSha256(final byte[] key) {
        return HmacAlgorithms.HMAC_SHA_256.getHmac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the HmacSHA384 algorithm.
     * <p>
     * Every implementation of the Java platform is <em>not</em> required to support this Mac algorithm.
     * </p>
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.HMAC_SHA384.getHmac(byte[])}
     */
    @Deprecated
    public static Mac getHmacSha384(final byte[] key) {
        return HmacAlgorithms.HMAC_SHA_384.getHmac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the HmacSHA512 algorithm.
     * <p>
     * Every implementation of the Java platform is <em>not</em> required to support this Mac algorithm.
     * </p>
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.HMAC_SHA512.getHmac(byte[])}
     */
    @Deprecated
    public static Mac getHmacSha512(final byte[] key) {
        return HmacAlgorithms.HMAC_SHA_512.getHmac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the given <code>algorithm</code>.
     *
     * @param algorithm
     *            the name of the algorithm requested. See
     *            <a href= "http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA"
     *            >Appendix A in the Java Cryptography Architecture Reference Guide</a> for information about standard
     *            algorithm names.
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@link HmacAlgorithms#getInitializedMac(byte[])}.
     */
    @Deprecated
    public static Mac getInitializedMac(final HmacAlgorithms algorithm, final byte[] key) {
        return algorithm.getInitializedMac(key);
    }

    /**
     * Returns an initialized <code>Mac</code> for the given <code>algorithm</code>.
     *
     * @param algorithm
     *            the name of the algorithm requested. See
     *            <a href= "http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA"
     *            >Appendix A in the Java Cryptography Architecture Reference Guide</a> for information about standard
     *            algorithm names.
     * @param key
     *            They key for the keyed digest (must not be null)
     * @return A Mac instance initialized with the given key.
     * @see Mac#getInstance(String)
     * @see Mac#init(Key)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     */
    public static Mac getInitializedMac(final String algorithm, final byte[] key) {

        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }

        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // hmacMd5

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacMD5 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmac(byte[], byte[])}
     */
    @Deprecated
    public static byte[] hmacMd5(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_MD5.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacMD5 MAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmac(byte[], InputStream)}
     */
    @Deprecated
    public static byte[] hmacMd5(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_MD5.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacMD5 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmac(String, String)}
     */
    @Deprecated
    public static byte[] hmacMd5(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_MD5.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacMD5 MAC for the given key and value as a hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmacHex(byte[], byte[])}
     */
    @Deprecated
    public static String hmacMd5Hex(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_MD5.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacMD5 MAC for the given key and value as a hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmacHex(byte[], InputStream)}
     */
    @Deprecated
    public static String hmacMd5Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_MD5.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacMD5 Message Authentication Code (MAC) as a hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacMD5 MAC for the given key and value as a hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.MD5.getHmacHex(String, String)}
     */
    @Deprecated
    public static String hmacMd5Hex(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_MD5.hmacHex(key, valueToDigest);
    }

    // hmacSha1

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA1 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmac(byte[], byte[])}
     */
    @Deprecated
    public static byte[] hmacSha1(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_1.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA1 MAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmac(byte[], InputStream)}
     */
    @Deprecated
    public static byte[] hmacSha1(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_1.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA1 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmac(String, String)}
     */
    @Deprecated
    public static byte[] hmacSha1(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_1.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA1 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmacHex(byte[], byte[])}
     */
    @Deprecated
    public static String hmacSha1Hex(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_1.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA1 MAC for the given key and value as hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmacHex(byte[], InputStream)}
     */
    @Deprecated
    public static String hmacSha1Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_1.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA1 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA1 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_1.getHmacHex(String, String)}
     */
    @Deprecated
    public static String hmacSha1Hex(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_1.hmacHex(key, valueToDigest);
    }

    // hmacSha256

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA256 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmac(byte[], byte[])}
     */
    @Deprecated
    public static byte[] hmacSha256(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_256.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA256 MAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmac(byte[], InputStream)}
     */
    @Deprecated
    public static byte[] hmacSha256(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_256.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA256 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmac(String, String)}
     */
    @Deprecated
    public static byte[] hmacSha256(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_256.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA256 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmacHex(byte[], byte[])}
     */
    @Deprecated
    public static String hmacSha256Hex(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_256.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA256 MAC for the given key and value as hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmacHex(byte[], InputStream)}
     */
    @Deprecated
    public static String hmacSha256Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_256.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA256 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA256 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_256.getHmacHex(String, String)}
     */
    @Deprecated
    public static String hmacSha256Hex(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_256.hmacHex(key, valueToDigest);
    }

    // hmacSha384

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA384 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmac(byte[], byte[])}
     */
    @Deprecated
    public static byte[] hmacSha384(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_384.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA384 MAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmac(byte[], InputStream)}
     */
    @Deprecated
    public static byte[] hmacSha384(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_384.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA384 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmac(String, String)}
     */
    @Deprecated
    public static byte[] hmacSha384(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_384.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA384 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmacHex(byte[], byte[])}
     */
    @Deprecated
    public static String hmacSha384Hex(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_384.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA384 MAC for the given key and value as hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmacHex(byte[], InputStream)}
     */
    @Deprecated
    public static String hmacSha384Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_384.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA384 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA384 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_384.getHmacHex(String, String)}
     */
    @Deprecated
    public static String hmacSha384Hex(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_384.hmacHex(key, valueToDigest);
    }

    // hmacSha512

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA512 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmac(byte[], byte[])}
     */
    @Deprecated
    public static byte[] hmacSha512(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_512.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA512 MAC for the given key and value
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmac(byte[], InputStream)}
     */
    @Deprecated
    public static byte[] hmacSha512(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_512.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA512 MAC for the given key and value
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmac(String, String)}
     */
    @Deprecated
    public static byte[] hmacSha512(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_512.hmac(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA512 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmacHex(byte[], byte[])}
     */
    @Deprecated
    public static String hmacSha512Hex(final byte[] key, final byte[] valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_512.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return HmacSHA512 MAC for the given key and value as hex string (lowercase)
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmacHex(byte[], InputStream)}
     */
    @Deprecated
    public static String hmacSha512Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return HmacAlgorithms.HMAC_SHA_512.hmacHex(key, valueToDigest);
    }

    /**
     * Returns a HmacSHA512 Message Authentication Code (MAC) as hex string (lowercase) for the given key and value.
     *
     * @param key
     *            They key for the keyed digest (must not be null)
     * @param valueToDigest
     *            The value (data) which should to digest (maybe empty or null)
     * @return HmacSHA512 MAC for the given key and value as hex string (lowercase)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught or key is null or key is invalid.
     * @deprecated Use {@code HmacAlgorithms.SHA_512.getHmacHex(String, String)}
     */
    @Deprecated
    public static String hmacSha512Hex(final String key, final String valueToDigest) {
        return HmacAlgorithms.HMAC_SHA_512.hmacHex(key, valueToDigest);
    }

    // update

    /**
     * Updates the given {@link Mac}. This generates a digest for valueToDigest and the key the Mac was initialized
     *
     * @param mac
     *            the initialized {@link Mac} to update
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated {@link Mac}
     * @throws IllegalStateException
     *             if the Mac was not initialized
     * @since 1.x
     */
    public static Mac updateHmac(final Mac mac, final byte[] valueToDigest) {
        mac.reset();
        mac.update(valueToDigest);
        return mac;
    }

    /**
     * Updates the given {@link Mac}. This generates a digest for valueToDigest and the key the Mac was initialized
     *
     * @param mac
     *            the initialized {@link Mac} to update
     * @param valueToDigest
     *            the value to update the {@link Mac} with
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return the updated {@link Mac}
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalStateException
     *             If the Mac was not initialized
     * @since 1.x
     */
    public static Mac updateHmac(final Mac mac, final InputStream valueToDigest) throws IOException {
        mac.reset();
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = valueToDigest.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            mac.update(buffer, 0, read);
            read = valueToDigest.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return mac;
    }

    /**
     * Updates the given {@link Mac}. This generates a digest for valueToDigest and the key the Mac was initialized
     *
     * @param mac
     *            the initialized {@link Mac} to update
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated {@link Mac}
     * @throws IllegalStateException
     *             if the Mac was not initialized
     * @since 1.x
     */
    public static Mac updateHmac(final Mac mac, final String valueToDigest) {
        mac.reset();
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return mac;
    }
}
