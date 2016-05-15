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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Standard {@link MessageDigest} algorithm names from the <cite>Java Cryptography Architecture Standard Algorithm Name
 * Documentation</cite>.
 * <p>
 * This enum is immutable and thread-safe.
 * </p>
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html">Java Cryptography
 *      Architecture Standard Algorithm Name Documentation</a>
 * @since 1.11
 * @version $Id: MessageDigestAlgorithm.java 1637936 2014-11-10 16:47:29Z ggregory $
 */
public enum MessageDigestAlgorithm {

    /**
     * The MD2 message digest algorithm defined in RFC 1319.
     */
    MD2("MD2"),

    /**
     * The MD5 message digest algorithm defined in RFC 1321.
     */
    MD5("MD5"),

    /**
     * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_1("SHA-1"),

    /**
     * The SHA-224 hash algorithm defined in the FIPS PUB 180-4.
     * <p>
     * Java 8 only.
     * </p>
     */
    SHA_224("SHA-224"),

    /**
     * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_256("SHA-256"),

    /**
     * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_384("SHA-384"),

    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_512("SHA-512"),

    /**
     * The SHA3-224 hash algorithm defined in the NIST FIPS 202.
     * <p>
     * Java 9 only.
     * </p>
     */
    SHA3_224("SHA3-224"),

    /**
     * The SHA3-256 hash algorithm defined in the NIST FIPS 202.
     * <p>
     * Java 9 only.
     * </p>
     */
    SHA3_256("SHA3-256"),

    /**
     * The SHA3-384 hash algorithm defined in the NIST FIPS 202.
     * <p>
     * Java 9 only.
     * </p>
     */
    SHA3_384("SHA3-384"),

    /**
     * The SHA3-512 hash algorithm defined in the NIST FIPS 202.
     * <p>
     * Java 9 only.
     * </p>
     */
    SHA3_512("SHA3-512");

    private final String algorithm;

    private MessageDigestAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Read through a byte[] and returns the digest for the data
     *
     * @param data
     *            Data to digest
     * @return the digest
     * @throws IOException
     *             On error reading from the stream
     */
    public byte[] digest(byte[] data) throws IOException {
        return getMessageDigest().digest(data);
    }

    /**
     * Read through a ByteBuffer and returns the digest for the data
     *
     * @param data
     *            Data to digest
     * @return the digest
     * @throws IOException
     *             On error reading from the stream
     */
    public byte[] digest(ByteBuffer data) throws IOException {
        return DigestUtils.digest(getMessageDigest(), data);
    }

    /**
     * Read through a File and returns the digest for the data
     *
     * @param data
     *            Data to digest
     * @return the digest
     * @throws IOException
     *             On error reading from the stream
     */
    public byte[] digest(File data) throws IOException {
        return DigestUtils.digest(getMessageDigest(), data);
    }

    /**
     * Read through an InputStream and returns the digest for the data
     *
     * @param data
     *            Data to digest
     * @return the digest
     * @throws IOException
     *             On error reading from the stream
     */
    public byte[] digest(InputStream data) throws IOException {
        return DigestUtils.digest(getMessageDigest(), data);
    }

    /**
     * Gets the algorithm name.
     *
     * @return the algorithm name.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns a <code>MessageDigest</code> for this <code>algorithm</code>.
     *
     * @return A digest instance.
     * @see MessageDigest#getInstance(String)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught.
     */
    public MessageDigest getMessageDigest() {
        return DigestUtils.getDigest(algorithm);
    }

    /**
     * Whether a MessageDigest for this algorithm can be created.
     *
     * @return Whether a MessageDigest for this algorithm can be created.
     */
    public boolean isAvailable() {
        return DigestUtils.getDigest(algorithm, null) != null;
    }

}
