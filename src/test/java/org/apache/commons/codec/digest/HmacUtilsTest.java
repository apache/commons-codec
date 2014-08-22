/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.digest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests HmacUtils methods.
 *
 * @version $Id$
 */
public class HmacUtilsTest {
    private static final String STANDARD_KEY_STRING = "key";
    private static final byte[] STANDARD_KEY_BYTES = STANDARD_KEY_STRING.getBytes();
    private static final byte[] STANDARD_MD5_RESULT_BYTES = new byte[] { -128, 7, 7, 19, 70, 62, 119, 73, -71, 12, 45,
            -62, 73, 17, -30, 117 };
    private static final String STANDARD_MD5_RESULT_STRING = "80070713463e7749b90c2dc24911e275";
    private static final String STANDARD_PHRASE_STRING = "The quick brown fox jumps over the lazy dog";
    private static final byte[] STANDARD_PHRASE_BYTES = STANDARD_PHRASE_STRING.getBytes();
    private static final byte[] STANDARD_SHA1_RESULT_BYTES = new byte[] { -34, 124, -101, -123, -72, -73, -118, -90,
            -68, -118, 122, 54, -9, 10, -112, 112, 28, -99, -76, -39 };
    private static final String STANDARD_SHA1_RESULT_STRING = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9";
    private static final byte[] STANDARD_SHA256_RESULT_BYTES = new byte[] { -9, -68, -125, -12, 48, 83, -124, 36, -79,
            50, -104, -26, -86, 111, -79, 67, -17, 77, 89, -95, 73, 70, 23, 89, -105, 71, -99, -68, 45, 26, 60, -40 };
    private static final String STANDARD_SHA256_RESULT_STRING = "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8";
    private static final byte[] STANDARD_SHA384_RESULT_BYTES = new byte[] { -41, -12, 114, 126, 44, 11, 57, -82, 15,
            30, 64, -52, -106, -10, 2, 66, -43, -73, -128, 24, 65, -50, -90, -4, 89, 44, 93, 62, 26, -27, 7, 0, 88, 42,
            -106, -49, 53, -31, -27, 84, -103, 95, -28, -32, 51, -127, -62, 55 };
    private static final String STANDARD_SHA384_RESULT_STRING = "D7F4727E2C0B39AE0F1E40CC96F60242D5B7801841CEA6FC592C5D3E1AE50700582A96CF35E1E554995FE4E03381C237"
            .toLowerCase();
    private static final byte[] STANDARD_SHA512_RESULT_BYTES = new byte[] { -76, 42, -16, -112, 87, -70, -63, -30, -44,
            23, 8, -28, -118, -112, 46, 9, -75, -1, 127, 18, -85, 66, -118, 79, -24, 102, 83, -57, 61, -46, 72, -5,
            -126, -7, 72, -91, 73, -9, -73, -111, -91, -76, 25, 21, -18, 77, 30, -61, -109, 83, 87, -28, -30, 49, 114,
            80, -48, 55, 42, -6, 46, -66, -21, 58 };
    private static final String STANDARD_SHA512_RESULT_STRING = "B42AF09057BAC1E2D41708E48A902E09B5FF7F12AB428A4FE86653C73DD248FB82F948A549F7B791A5B41915EE4D1EC3935357E4E2317250D0372AFA2EBEEB3A"
            .toLowerCase();

    @Test
    public void testConstructor() {
        assertNotNull(new HmacUtils());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        HmacUtils.getHmacMd5(new byte[] {});
    }

    @Test
    public void testGetHMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES,
                HmacUtils.getHmacMd5(STANDARD_KEY_BYTES).doFinal(STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.getHmacSha1(STANDARD_KEY_BYTES).doFinal(STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.getHmacSha256(STANDARD_KEY_BYTES).doFinal(STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.getHmacSha384(STANDARD_KEY_BYTES).doFinal(STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.getHmacSha512(STANDARD_KEY_BYTES).doFinal(STANDARD_PHRASE_BYTES));
    }

    @Test
    public void testHmacMd5Hex() throws IOException {
        assertEquals("80070713463e7749b90c2dc24911e275",
                HmacUtils.hmacMd5Hex(STANDARD_KEY_STRING, "The quick brown fox jumps over the lazy dog"));
        assertEquals("750c783e6ab0b503eaa86e310a5db738", HmacUtils.hmacMd5Hex("Jefe", "what do ya want for nothing?"));
        assertEquals(
                "750c783e6ab0b503eaa86e310a5db738",
                HmacUtils.hmacMd5Hex("Jefe".getBytes(),
                        new ByteArrayInputStream("what do ya want for nothing?".getBytes())));
    }

    @Test
    public void testHmacSha1Hex() throws IOException {
        assertEquals(STANDARD_SHA1_RESULT_STRING, HmacUtils.hmacSha1Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", HmacUtils.hmacSha1Hex(STANDARD_KEY_STRING, ""));
        assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
                HmacUtils.hmacSha1Hex("Jefe", "what do ya want for nothing?"));
        assertEquals(
                "effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
                HmacUtils.hmacSha1Hex("Jefe".getBytes(),
                        new ByteArrayInputStream("what do ya want for nothing?".getBytes())));
    }

    @Test
    public void testHmacSha1UpdateWithByteArray() throws IOException {
        final Mac mac = HmacUtils.getHmacSha1(STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, STANDARD_PHRASE_BYTES);
        assertEquals(STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, "".getBytes());
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @Test
    public void testHmacSha1UpdateWithInpustream() throws IOException {
        final Mac mac = HmacUtils.getHmacSha1(STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, new ByteArrayInputStream(STANDARD_PHRASE_BYTES));
        assertEquals(STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, new ByteArrayInputStream("".getBytes()));
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @Test
    public void testHmacSha1UpdateWithString() throws IOException {
        final Mac mac = HmacUtils.getHmacSha1(STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, STANDARD_PHRASE_STRING);
        assertEquals(STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, "");
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @Test
    public void testInitializedMac() throws IOException {
        final Mac md5Mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, STANDARD_KEY_BYTES);
        final Mac md5Mac2 = HmacUtils.getInitializedMac("HmacMD5", STANDARD_KEY_BYTES);
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES, HmacUtils.updateHmac(md5Mac, STANDARD_PHRASE_STRING)
                .doFinal());
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES, HmacUtils.updateHmac(md5Mac2, STANDARD_PHRASE_STRING)
                .doFinal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializedMacNullAlgo() throws IOException {
        HmacUtils.getInitializedMac((String) null, STANDARD_KEY_BYTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializedMacNullKey() throws IOException {
        HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInternalNoSuchAlgorithmException() {
        HmacUtils.getInitializedMac("Bogus Bogus", StringUtils.getBytesUtf8("akey"));
    }

    @Test
    public void testMd5HMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        Assert.assertEquals(STANDARD_MD5_RESULT_STRING, HmacUtils.hmacMd5Hex(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertEquals(STANDARD_MD5_RESULT_STRING,
                HmacUtils.hmacMd5Hex(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(STANDARD_MD5_RESULT_STRING,
                HmacUtils.hmacMd5Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMd5HMacFail() throws IOException {
        HmacUtils.hmacMd5((byte[]) null, STANDARD_PHRASE_BYTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        HmacUtils.getHmacMd5(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecretKeySpecAllowsEmtyKeys() {
        new SecretKeySpec(new byte[] {}, "HmacMD5");
    }

    @Test
    public void testSha1HMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        Assert.assertEquals(STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertEquals(STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha1HMacFail() throws IOException {
        HmacUtils.hmacSha1((byte[]) null, STANDARD_PHRASE_BYTES);
    }

    @Test
    public void testSha256HMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        Assert.assertEquals(STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertEquals(STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha256HMacFail() throws IOException {
        HmacUtils.hmacSha256((byte[]) null, STANDARD_PHRASE_BYTES);
    }

    @Test
    public void testSha384HMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        Assert.assertEquals(STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertEquals(STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha384HMacFail() throws IOException {
        HmacUtils.hmacSha384((byte[]) null, STANDARD_PHRASE_BYTES);
    }

    @Test
    public void testSha512HMac() throws IOException {
        Assert.assertArrayEquals(STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
        Assert.assertEquals(STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(STANDARD_KEY_BYTES, STANDARD_PHRASE_BYTES));
        Assert.assertEquals(STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(STANDARD_KEY_BYTES, new ByteArrayInputStream(STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(STANDARD_KEY_STRING, STANDARD_PHRASE_STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha512HMacFail() throws IOException {
        HmacUtils.hmacSha512((byte[]) null, STANDARD_PHRASE_BYTES);
    }
}