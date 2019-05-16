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
 */
public class HmacUtilsTest {

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        HmacUtils.getHmacMd5(new byte[] {});
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testGetHMac() {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES,
                HmacUtils.getHmacMd5(HmacAlgorithmsTest.STANDARD_KEY_BYTES).doFinal(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.getHmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES).doFinal(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.getHmacSha256(HmacAlgorithmsTest.STANDARD_KEY_BYTES).doFinal(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.getHmacSha384(HmacAlgorithmsTest.STANDARD_KEY_BYTES).doFinal(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.getHmacSha512(HmacAlgorithmsTest.STANDARD_KEY_BYTES).doFinal(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testHmacMd5Hex() throws IOException {
        assertEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_STRING,
                HmacUtils.hmacMd5Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, "The quick brown fox jumps over the lazy dog"));
        assertEquals("750c783e6ab0b503eaa86e310a5db738", HmacUtils.hmacMd5Hex("Jefe", "what do ya want for nothing?"));
        assertEquals(
                "750c783e6ab0b503eaa86e310a5db738",
                HmacUtils.hmacMd5Hex("Jefe".getBytes(),
                        new ByteArrayInputStream("what do ya want for nothing?".getBytes())));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testHmacSha1Hex() throws IOException {
        assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING, HmacUtils.hmacSha1Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", HmacUtils.hmacSha1Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, ""));
        assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
                HmacUtils.hmacSha1Hex("Jefe", "what do ya want for nothing?"));
        assertEquals(
                "effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
                HmacUtils.hmacSha1Hex("Jefe".getBytes(),
                        new ByteArrayInputStream("what do ya want for nothing?".getBytes())));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testHmacSha1UpdateWithByteArray() {
        final Mac mac = HmacUtils.getHmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
        assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, "".getBytes());
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testHmacSha1UpdateWithInpustream() throws IOException {
        final Mac mac = HmacUtils.getHmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, new ByteArrayInputStream("".getBytes()));
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testHmacSha1UpdateWithString() {
        final Mac mac = HmacUtils.getHmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES);
        HmacUtils.updateHmac(mac, HmacAlgorithmsTest.STANDARD_PHRASE_STRING);
        assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING, Hex.encodeHexString(mac.doFinal()));
        HmacUtils.updateHmac(mac, "");
        assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f", Hex.encodeHexString(mac.doFinal()));
    }

    @Test
    public void testInitializedMac() {
        final Mac md5Mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, HmacAlgorithmsTest.STANDARD_KEY_BYTES);
        final Mac md5Mac2 = HmacUtils.getInitializedMac("HmacMD5", HmacAlgorithmsTest.STANDARD_KEY_BYTES);
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES, HmacUtils.updateHmac(md5Mac, HmacAlgorithmsTest.STANDARD_PHRASE_STRING)
                .doFinal());
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES, HmacUtils.updateHmac(md5Mac2, HmacAlgorithmsTest.STANDARD_PHRASE_STRING)
                .doFinal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializedMacNullAlgo() {
        HmacUtils.getInitializedMac((String) null, HmacAlgorithmsTest.STANDARD_KEY_BYTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializedMacNullKey() {
        HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInternalNoSuchAlgorithmException() {
        HmacUtils.getInitializedMac("Bogus Bogus", StringUtils.getBytesUtf8("akey"));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testMd5HMac() throws IOException {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_BYTES,
                HmacUtils.hmacMd5(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_STRING, HmacUtils.hmacMd5Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_STRING,
                HmacUtils.hmacMd5Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_MD5_RESULT_STRING,
                HmacUtils.hmacMd5Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testMd5HMacFail() {
        HmacUtils.hmacMd5((byte[]) null, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        HmacUtils.getHmacMd5(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecretKeySpecAllowsEmtyKeys() {
        new SecretKeySpec(new byte[] {}, "HmacMD5");
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testSha1HMac() throws IOException {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_BYTES,
                HmacUtils.hmacSha1(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA1_RESULT_STRING,
                HmacUtils.hmacSha1Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testSha1HMacFail() {
        HmacUtils.hmacSha1((byte[]) null, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testSha256HMac() throws IOException {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_BYTES,
                HmacUtils.hmacSha256(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA256_RESULT_STRING,
                HmacUtils.hmacSha256Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testSha256HMacFail() {
        HmacUtils.hmacSha256((byte[]) null, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testSha384HMac() throws IOException {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_BYTES,
                HmacUtils.hmacSha384(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA384_RESULT_STRING,
                HmacUtils.hmacSha384Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testSha384HMacFail() {
        HmacUtils.hmacSha384((byte[]) null, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test
    public void testSha512HMac() throws IOException {
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertArrayEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_BYTES,
                HmacUtils.hmacSha512(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(HmacAlgorithmsTest.STANDARD_KEY_BYTES, new ByteArrayInputStream(HmacAlgorithmsTest.STANDARD_PHRASE_BYTES)));
        Assert.assertEquals(HmacAlgorithmsTest.STANDARD_SHA512_RESULT_STRING,
                HmacUtils.hmacSha512Hex(HmacAlgorithmsTest.STANDARD_KEY_STRING, HmacAlgorithmsTest.STANDARD_PHRASE_STRING));
    }

    @SuppressWarnings("deprecation") // most of the static methods are deprecated
    @Test(expected = IllegalArgumentException.class)
    public void testSha512HMacFail() {
        HmacUtils.hmacSha512((byte[]) null, HmacAlgorithmsTest.STANDARD_PHRASE_BYTES);
    }
}