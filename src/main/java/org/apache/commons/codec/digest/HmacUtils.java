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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Simplifies common {@link javax.crypto.Mac} tasks. This class is immutable and thread-safe.
 * However the Mac may not be.
 * <p>
 * <strong>Note: Not all JCE implementations support all algorithms. If not supported, an IllegalArgumentException is
 * thrown.</strong>
 * <p>
 * Sample usage:
 * <pre>
 * byte[] key = {1,2,3,4}; // don't use this!
 * String valueToDigest = "The quick brown fox jumps over the lazy dog";
 * byte[] hmac = HmacUtils.use(HmacAlgorithms.HMAC_SHA_224).key(key).update(valueToDigest).doFinal();
 * // Mac re-use
 * HmacUtils hm1 = HmacUtils.use(HmacAlgorithms.HMAC_SHA_1).key(key);
 * String hexPom = hm1.update(new File("pom.xml")).doFinalHex();
 * String hexNot = hm1.update(new File("NOTICE.txt")).doFinalHex();
 * // Mac key update
 * String algo = "HmacNew";
 * HmacUtils hm2 = HmacUtils.use(algo).key(key);
 * byte[] key2 = {1,2,3,4,5}; // don't use this either!
 * String hexPom2 = hm2.update(new File("pom.xml")).doFinalHex();
 * String hexNot2 = hm2.key(key2).update(new File("NOTICE.txt")).doFinalHex();
 * </pre>
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
     * @deprecated Use {@link HmacAlgorithms#getHmac(byte[])}.
     */
    @Deprecated
    public static Mac getInitializedMac(final HmacAlgorithms algorithm, final byte[] key) {
        return algorithm.getHmac(key);
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
     * Resets and then updates the given {@link Mac} with the value.
     *
     * @param mac
     *            the initialized {@link Mac} to update
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated {@link Mac}
     * @throws IllegalStateException
     *             if the Mac was not initialized
     */
    public static Mac updateHmac(final Mac mac, final byte[] valueToDigest) {
        mac.reset();
        mac.update(valueToDigest);
        return mac;
    }

    /**
     * Resets and then updates the given {@link Mac} with the value.
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
     * Resets and then updates the given {@link Mac} with the value.
     *
     * @param mac
     *            the initialized {@link Mac} to update
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated {@link Mac}
     * @throws IllegalStateException
     *             if the Mac was not initialized
     */
    public static Mac updateHmac(final Mac mac, final String valueToDigest) {
        mac.reset();
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return mac;
    }

    private HmacUtils() {
        this(null);
    }

    // Fluent interface

    private final Mac mac;

    private HmacUtils(final Mac mac) {
        this.mac = mac;
    }


    /**
     * Creates an instance using the provided {@link Mac}
     * If necessary, the
     * key must be provided using the {@link #key(byte[])} method
     * before it can be used further.
     *
     * @param mac the {@link Mac} to be used.
     * @return the instance
     * @since 1.11
     */
    public static HmacUtils use(final Mac mac) {
        return new HmacUtils(mac);
    }

    /**
     * Creates an instance using the provided algorithm type.
     * The key must be provided using the {@link #key(byte[])} method
     * before it can be used further.
     *
     * @param algorithm to be used.
     * @return the instance
     * @since 1.11
     */
    public static HmacUtils use(final String algorithm) {
        try {
            return new HmacUtils(Mac.getInstance(algorithm));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates an instance using the provided algorithm type.
     * The key must be provided using the {@link #key(byte[])} method
     * before it can be used further.
     *
     * @param algorithm to be used.
     * @return the instance
     * @since 1.11
     */
    public static HmacUtils use(final HmacAlgorithms algorithm) {
        return use(algorithm.getName());
    }

    /**
     * Updates the stored {@link Mac} with the new key.
     * This resets the Mac ready for re-use.
     *
     * @param key the new key
     * @return this instance
     * @since 1.11
     */
    public HmacUtils key(byte[] key) {
        final SecretKeySpec keySpec = new SecretKeySpec(key, mac.getAlgorithm());
        try {
            mac.init(keySpec);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    /**
     * Updates the stored {@link Mac} with the value.
     *
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated instance
     * @throws IllegalStateException
     *             if the Mac was not initialized
     * @since 1.11
     */
    public HmacUtils update(final byte[] valueToDigest) {
        mac.update(valueToDigest);
        return this;
    }
    
    /**
     * Updates the stored {@link Mac} with the value.
     *
     * @param valueToDigest
     *            the value to update the {@link Mac} with (maybe null or empty)
     * @return the updated instance
     * @throws IllegalStateException
     *             if the Mac was not initialized
     * @since 1.11
     */
    public HmacUtils update(final ByteBuffer valueToDigest) {
        mac.update(valueToDigest);
        return this;
    }
    
    /**
     * Updates the stored {@link Mac} with the value.
     * String is converted to bytes using the UTF-8 charset.
     * @param valueToDigest
     *            the value to update the {@link Mac} with.
     * @return the updated instance
     * @throws IllegalStateException
     *             if the Mac was not initialized
     * @since 1.11
     */
    public HmacUtils update(final String valueToDigest) {
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return this;
    }
    
    /**
     * Updates the stored {@link Mac} with the value.
     *
     * @param valueToDigest
     *            the value to update the {@link Mac} with
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return the updated instance
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalStateException
     *             If the Mac was not initialized
     * @since 1.11
     */
    public HmacUtils update(final InputStream valueToDigest) throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read;

        while ((read = valueToDigest.read(buffer, 0, STREAM_BUFFER_LENGTH) ) > -1) {
            mac.update(buffer, 0, read);
        }
        return this;
    }

    /**
     * Updates the stored {@link Mac} with the value.
     *
     * @param valueToDigest
     *            the value to update the {@link Mac} with
     *            <p>
     *            The InputStream must not be null and will not be closed
     *            </p>
     * @return the updated instance
     * @throws IOException
     *             If an I/O error occurs.
     * @throws IllegalStateException
     *             If the Mac was not initialized
     * @since 1.11
     */
    public HmacUtils update(final File valueToDigest) throws IOException {
        final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest));
        try {
            return update(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Finishes the MAC operation and returns the result.
     * The Mac can be re-used to produce further results from the same key.
     * Or the key can be reset and the Mac reused.
     *
     * @return the result as a byte array
     * @since 1.11
     */
    public byte[] doFinal() {
        return mac.doFinal();
    }

    /**
     * Finishes the MAC operation and returns the result.
     * The Mac can be re-used to produce further results from the same key.
     * Or the key can be reset and the Mac reused.
     *
     * @return the result as a Hex String
     * @since 1.11
     */
    public String doFinalHex() {
        return Hex.encodeHexString(mac.doFinal());
    }

}
