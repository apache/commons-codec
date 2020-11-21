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
 * @see <a href=
 *      "http://docs.oracle.com/javase/9/security/oracleproviders.htm#JSSEC-GUID-A47B1249-593C-4C38-A0D0-68FA7681E0A7">
 *      Java 9 Cryptography Architecture Sun Providers Documentation</a>
 * @since 1.10
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

    HmacAlgorithms(final String algorithm) {
        this.name = algorithm;
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
     * The algorithm name
     *
     * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 6 Cryptography Architecture Sun Providers Documentation</a>
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 7 Cryptography Architecture Sun Providers Documentation</a>
     * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#SunJCEProvider">
     *      Java 8 Cryptography Architecture Sun Providers Documentation</a>
     * @see <a href=
     *      "http://docs.oracle.com/javase/9/security/oracleproviders.htm#JSSEC-GUID-A47B1249-593C-4C38-A0D0-68FA7681E0A7">
     *      Java 9 Cryptography Architecture Sun Providers Documentation</a>
     * @return The algorithm name ("HmacSHA512" for example)
     */
    @Override
    public String toString() {
        return name;
    }

}
