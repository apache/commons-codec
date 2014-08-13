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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

/**
 * Tests HmacUtils methods.
 * 
 * @version $Id$
 */
public class HmacUtilsTest {

	@Test
	public void testConstructable() {
		assertNotNull(new HmacUtils());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInternalNoSuchAlgorithmException() {
		HmacUtils.getInitializedMac("Bogus Bogus",
				StringUtils.getBytesUtf8("akey"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullKey() {
		HmacUtils.getHmacMd5(null);
	}

	@Test
	public void testHmacMd5Hex() throws IOException {

		assertEquals("74e6f7298a9c2d168935f58c001bad88",
				HmacUtils.hmacMd5Hex("", null));

		assertEquals("74e6f7298a9c2d168935f58c001bad88",
				HmacUtils.hmacMd5Hex("", ""));

		assertEquals("80070713463e7749b90c2dc24911e275", HmacUtils.hmacMd5Hex(
				"key", "The quick brown fox jumps over the lazy dog"));

		assertEquals("750c783e6ab0b503eaa86e310a5db738",
				HmacUtils.hmacMd5Hex("Jefe", "what do ya want for nothing?"));

		assertEquals("750c783e6ab0b503eaa86e310a5db738", HmacUtils.hmacMd5Hex(
				"Jefe".getBytes(), new ByteArrayInputStream(
						"what do ya want for nothing?".getBytes())));

	}

	@Test
	public void testHmacSha1Hex() throws IOException {

		assertEquals("fbdb1d1b18aa6c08324b7d64b71fb76370690e1d",
				HmacUtils.hmacSha1Hex("", null));

		assertEquals("fbdb1d1b18aa6c08324b7d64b71fb76370690e1d",
				HmacUtils.hmacSha1Hex("", ""));

		assertEquals("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9",
				HmacUtils.hmacSha1Hex("key",
						"The quick brown fox jumps over the lazy dog"));

		assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f",
				HmacUtils.hmacSha1Hex("key", ""));

		assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
				HmacUtils.hmacSha1Hex("Jefe", "what do ya want for nothing?"));

		assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79",
				HmacUtils.hmacSha1Hex(
						"Jefe".getBytes(),
						new ByteArrayInputStream("what do ya want for nothing?"
								.getBytes())));

	}

	@Test
	public void testHmacSha256Hex() throws IOException {

		assertEquals(
				"b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad",
				HmacUtils.hmacSha256Hex("", null));

		assertEquals(
				"b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad",
				HmacUtils.hmacSha256Hex("", ""));

		assertEquals(
				"f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",
				HmacUtils.hmacSha256Hex("key",
						"The quick brown fox jumps over the lazy dog"));
	}

	@Test
	public void testHmacSha384Hex() throws IOException {

		assertEquals(
				"6C1F2EE938FAD2E24BD91298474382CA218C75DB3D83E114B3D4367776D14D3551289E75E8209CD4B792302840234ADC"
						.toLowerCase(), HmacUtils.hmacSha384Hex("", null));

		assertEquals(
				"6C1F2EE938FAD2E24BD91298474382CA218C75DB3D83E114B3D4367776D14D3551289E75E8209CD4B792302840234ADC"
						.toLowerCase(), HmacUtils.hmacSha384Hex("", ""));

		assertEquals(
				"D7F4727E2C0B39AE0F1E40CC96F60242D5B7801841CEA6FC592C5D3E1AE50700582A96CF35E1E554995FE4E03381C237"
						.toLowerCase(), HmacUtils.hmacSha384Hex("key",
						"The quick brown fox jumps over the lazy dog"));
	}

	@Test
	public void testHmacSha512Hex() throws IOException {

		assertEquals(
				"B936CEE86C9F87AA5D3C6F2E84CB5A4239A5FE50480A6EC66B70AB5B1F4AC6730C6C515421B327EC1D69402E53DFB49AD7381EB067B338FD7B0CB22247225D47"
						.toLowerCase(), HmacUtils.hmacSha512Hex("", null));

		assertEquals(
				"B936CEE86C9F87AA5D3C6F2E84CB5A4239A5FE50480A6EC66B70AB5B1F4AC6730C6C515421B327EC1D69402E53DFB49AD7381EB067B338FD7B0CB22247225D47"
						.toLowerCase(), HmacUtils.hmacSha512Hex("", ""));

		assertEquals(
				"B42AF09057BAC1E2D41708E48A902E09B5FF7F12AB428A4FE86653C73DD248FB82F948A549F7B791A5B41915EE4D1EC3935357E4E2317250D0372AFA2EBEEB3A"
						.toLowerCase(), HmacUtils.hmacSha512Hex("key",
						"The quick brown fox jumps over the lazy dog"));
	}

	@Test
	public void testHmacSha1UpdateWithByteArray() throws IOException {

		Mac mac = HmacUtils.getHmacSha1("key".getBytes());
		HmacUtils.updateHmac(mac,
				"The quick brown fox jumps over the lazy dog".getBytes());
		assertEquals("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9",
				Hex.encodeHexString(mac.doFinal()));
		HmacUtils.updateHmac(mac, "".getBytes());
		assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f",
				Hex.encodeHexString(mac.doFinal()));
	}

	@Test
	public void testHmacSha1UpdateWithInpustream() throws IOException {

		Mac mac = HmacUtils.getHmacSha1("key".getBytes());
		HmacUtils.updateHmac(mac, new ByteArrayInputStream(
				"The quick brown fox jumps over the lazy dog".getBytes()));
		assertEquals("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9",
				Hex.encodeHexString(mac.doFinal()));
		HmacUtils.updateHmac(mac, new ByteArrayInputStream("".getBytes()));
		assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f",
				Hex.encodeHexString(mac.doFinal()));
	}

	@Test
	public void testHmacSha1UpdateWithString() throws IOException {

		Mac mac = HmacUtils.getHmacSha1("key".getBytes());
		HmacUtils
				.updateHmac(mac, "The quick brown fox jumps over the lazy dog");
		assertEquals("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9",
				Hex.encodeHexString(mac.doFinal()));
		HmacUtils.updateHmac(mac, "");
		assertEquals("f42bb0eeb018ebbd4597ae7213711ec60760843f",
				Hex.encodeHexString(mac.doFinal()));
	}

}
