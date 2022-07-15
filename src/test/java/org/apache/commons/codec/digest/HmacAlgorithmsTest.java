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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.crypto.Mac;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link HmacAlgorithms}.
 *
 * @since 1.11
 */
public class HmacAlgorithmsTest {

    static final String STANDARD_KEY_STRING = "key";

    static final byte[] STANDARD_KEY_BYTES = STANDARD_KEY_STRING.getBytes();

    static final byte[] STANDARD_MD5_RESULT_BYTES = { -128, 7, 7, 19, 70, 62, 119, 73, -71, 12, 45, -62, 73,
            17, -30, 117 };

    static final String STANDARD_MD5_RESULT_STRING = "80070713463e7749b90c2dc24911e275";

    static final String STANDARD_PHRASE_STRING = "The quick brown fox jumps over the lazy dog";

    static final byte[] STANDARD_PHRASE_BYTES = STANDARD_PHRASE_STRING.getBytes();

    static final byte[] STANDARD_SHA1_RESULT_BYTES = { -34, 124, -101, -123, -72, -73, -118, -90, -68, -118,
            122, 54, -9, 10, -112, 112, 28, -99, -76, -39 };

    static final String STANDARD_SHA1_RESULT_STRING = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9";

    static final byte[] STANDARD_SHA224_RESULT_BYTES = { -120, -1, -117, 84, 103, 93, 57, -72, -9, 35, 34,
            -26, 95, -7, 69, -59, 45, -106, 55, -103, -120, -83, -94, 86, 57, 116, 126, 105 };

    static final String STANDARD_SHA224_RESULT_STRING = "88ff8b54675d39b8f72322e65ff945c52d96379988ada25639747e69";

    static final byte[] STANDARD_SHA256_RESULT_BYTES = { -9, -68, -125, -12, 48, 83, -124, 36, -79, 50, -104,
            -26, -86, 111, -79, 67, -17, 77, 89, -95, 73, 70, 23, 89, -105, 71, -99, -68, 45, 26, 60, -40 };

    static final String STANDARD_SHA256_RESULT_STRING = "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8";

    static final byte[] STANDARD_SHA384_RESULT_BYTES = { -41, -12, 114, 126, 44, 11, 57, -82, 15, 30, 64,
            -52, -106, -10, 2, 66, -43, -73, -128, 24, 65, -50, -90, -4, 89, 44, 93, 62, 26, -27, 7, 0, 88, 42, -106,
            -49, 53, -31, -27, 84, -103, 95, -28, -32, 51, -127, -62, 55 };

    static final String STANDARD_SHA384_RESULT_STRING = "D7F4727E2C0B39AE0F1E40CC96F60242D5B7801841CEA6FC592C5D3E1AE50700582A96CF35E1E554995FE4E03381C237"
            .toLowerCase();

    static final byte[] STANDARD_SHA512_RESULT_BYTES = { -76, 42, -16, -112, 87, -70, -63, -30, -44, 23, 8,
            -28, -118, -112, 46, 9, -75, -1, 127, 18, -85, 66, -118, 79, -24, 102, 83, -57, 61, -46, 72, -5, -126, -7,
            72, -91, 73, -9, -73, -111, -91, -76, 25, 21, -18, 77, 30, -61, -109, 83, 87, -28, -30, 49, 114, 80, -48,
            55, 42, -6, 46, -66, -21, 58 };

    static final String STANDARD_SHA512_RESULT_STRING = "B42AF09057BAC1E2D41708E48A902E09B5FF7F12AB428A4FE86653C73DD248FB82F948A549F7B791A5B41915EE4D1EC3935357E4E2317250D0372AFA2EBEEB3A"
            .toLowerCase();

    private static final byte[] EMPTY_BYTE_ARRAY = {};

    // TODO HMAC_SHA_224
    public static Stream<Arguments> data() {
        List<Arguments> list = Arrays.asList(
        // @formatter:off
                Arguments.of( HmacAlgorithms.HMAC_MD5, STANDARD_MD5_RESULT_BYTES, STANDARD_MD5_RESULT_STRING ),
                Arguments.of( HmacAlgorithms.HMAC_SHA_1, STANDARD_SHA1_RESULT_BYTES, STANDARD_SHA1_RESULT_STRING ),
                Arguments.of( HmacAlgorithms.HMAC_SHA_256, STANDARD_SHA256_RESULT_BYTES, STANDARD_SHA256_RESULT_STRING ),
                Arguments.of( HmacAlgorithms.HMAC_SHA_384, STANDARD_SHA384_RESULT_BYTES, STANDARD_SHA384_RESULT_STRING ),
                Arguments.of( HmacAlgorithms.HMAC_SHA_512, STANDARD_SHA512_RESULT_BYTES, STANDARD_SHA512_RESULT_STRING ));
        // @formatter:on
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            list = new ArrayList<>(list);
            list.add(Arguments.of(HmacAlgorithms.HMAC_SHA_224, STANDARD_SHA224_RESULT_BYTES, STANDARD_SHA224_RESULT_STRING));
        }
        return list.stream();
    }

    private DigestUtilsTest digestUtilsTest;

    @BeforeEach
    public void setUp() throws Exception {
        digestUtilsTest = new DigestUtilsTest();
        digestUtilsTest.setUp();
    }

    @AfterEach
    public void tearDown() throws Exception {
        digestUtilsTest.tearDown();
        digestUtilsTest = null;
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testAlgorithm(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) throws NoSuchAlgorithmException {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        final String algorithm = hmacAlgorithm.getName();
        assertNotNull(algorithm);
        assertFalse(algorithm.isEmpty());
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        Mac.getInstance(algorithm);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testGetHmacEmptyKey(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> HmacUtils.getInitializedMac(hmacAlgorithm, EMPTY_BYTE_ARRAY));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testGetHmacNullKey(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> HmacUtils.getInitializedMac(hmacAlgorithm, null));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacFailByteArray(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (byte[]) null).hmac(STANDARD_PHRASE_BYTES));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacFailInputStream(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (byte[]) null).hmac(new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacFailString(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (String) null).hmac(STANDARD_PHRASE_STRING));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacHexFailByteArray(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (byte[]) null).hmac(STANDARD_PHRASE_BYTES));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacHexFailInputStream(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (byte[]) null).hmac(new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHmacHexFailString(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertThrows(IllegalArgumentException.class, () -> new HmacUtils(hmacAlgorithm, (String) null).hmac(STANDARD_PHRASE_STRING));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInitializedMac(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        final Mac mac = HmacUtils.getInitializedMac(hmacAlgorithm, STANDARD_KEY_BYTES);
        final Mac mac2 = HmacUtils.getInitializedMac(hmacAlgorithm.getName(), STANDARD_KEY_BYTES);
        assertArrayEquals(standardResultBytes, HmacUtils.updateHmac(mac, STANDARD_PHRASE_STRING).doFinal());
        assertArrayEquals(standardResultBytes, HmacUtils.updateHmac(mac2, STANDARD_PHRASE_STRING).doFinal());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacByteArray(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertArrayEquals(standardResultBytes, new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmac(STANDARD_PHRASE_BYTES));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacHexByteArray(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertEquals(standardResultString, new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmacHex(STANDARD_PHRASE_BYTES));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacHexInputStream(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) throws IOException {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertEquals(standardResultString,
                new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmacHex(new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacHexString(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertEquals(standardResultString, new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmacHex(STANDARD_PHRASE_STRING));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacInputStream(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) throws IOException {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertArrayEquals(standardResultBytes,
                new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmac(new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testMacString(final HmacAlgorithms hmacAlgorithm, final byte[] standardResultBytes, final String standardResultString) {
        assumeTrue(HmacUtils.isAvailable(hmacAlgorithm));
        assertArrayEquals(standardResultBytes, new HmacUtils(hmacAlgorithm, STANDARD_KEY_BYTES).hmac(STANDARD_PHRASE_STRING));
    }

}
