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

import static org.apache.commons.codec.binary.StringUtils.getBytesUtf8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests DigestUtils methods.
 *
 */
public class DigestUtilsTest {

    private static final String EMPTY_STRING = "";

    private final byte[] testData = new byte[1024 * 1024];

    private File testFile;

    private File testRandomAccessFile;

    private RandomAccessFile testRandomAccessFileWrapper;

    private void assumeJava8() {
        Assume.assumeTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8));
    }

    private void assumeJava9() {
        Assume.assumeTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
    }

    byte[] getTestData() {
        return testData;
    }

    File getTestFile() {
        return testFile;
    }

    Path getTestPath() {
        return testFile.toPath();
    }

    RandomAccessFile getTestRandomAccessFile() {
        return testRandomAccessFileWrapper;
    }

    @Before
    public void setUp() throws Exception {
        new Random().nextBytes(testData);
        testFile = File.createTempFile(DigestUtilsTest.class.getName(), ".dat");
        try (final FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write(testData);
        }

        testRandomAccessFile = File.createTempFile(DigestUtilsTest.class.getName(), ".dat");
        try (final FileOutputStream fos = new FileOutputStream(testRandomAccessFile)) {
            fos.write(testData);
        }
        testRandomAccessFileWrapper = new RandomAccessFile(testRandomAccessFile, "rw");
    }

    @After
    public void tearDown() {
        if (!testFile.delete()) {
            testFile.deleteOnExit();
        }

        if (!testRandomAccessFile.delete()) {
            testRandomAccessFile.deleteOnExit();
        }
    }

    @Test
    public void testDigestAs() throws IOException {
        final String expected = "d41d8cd98f00b204e9800998ecf8427e";
        final String pathname = "src/test/resources/org/apache/commons/codec/empty.bin";
        final String algo = MessageDigestAlgorithms.MD5;
        assertEquals(expected, new DigestUtils(algo).digestAsHex(new File(pathname)));
        try (final FileInputStream inputStream = new FileInputStream(pathname)) {
            assertEquals(expected, new DigestUtils(algo).digestAsHex(inputStream));
        }
        final byte[] allBytes = Files.readAllBytes(Paths.get(pathname));
        assertEquals(expected, new DigestUtils(algo).digestAsHex(allBytes));
        assertEquals(expected, new DigestUtils(algo).digestAsHex(ByteBuffer.wrap(allBytes)));
    }

    @Test
    public void testGetMessageDigest() {
        final DigestUtils digestUtils = new DigestUtils(MessageDigestAlgorithms.MD5);
        assertNotNull(digestUtils.getMessageDigest());
        assertEquals(MessageDigestAlgorithms.MD5, digestUtils.getMessageDigest().getAlgorithm());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInternalNoSuchAlgorithmException() {
        DigestUtils.getDigest("Bogus Bogus");
    }

    @Test
    public void testIsAvailable() {
        assertTrue(DigestUtils.isAvailable(MessageDigestAlgorithms.MD5));
        assertFalse(DigestUtils.isAvailable("FOO"));
        assertFalse(DigestUtils.isAvailable(null));
    }

    @Test
    public void testMd2Hex() throws IOException {
        // Examples from RFC 1319
        assertEquals("8350e5a3e24c153df2275c9f80692773", DigestUtils.md2Hex(EMPTY_STRING));

        assertEquals("32ec01ec4a6dac72c0ab96fb34c0b5d1", DigestUtils.md2Hex("a"));

        assertEquals("da853b0d3f88d99b30283a69e6ded6bb", DigestUtils.md2Hex("abc"));

        assertEquals("ab4f496bfb2a530b219ff33031fe06b0", DigestUtils.md2Hex("message digest"));

        assertEquals("4e8ddff3650292ab5a4108c3aa47940b", DigestUtils.md2Hex("abcdefghijklmnopqrstuvwxyz"));

        assertEquals(
            "da33def2a42df13975352846c30338cd",
            DigestUtils.md2Hex("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"));

        assertEquals(
            "d5976f79d83d3a0dc9806c3c66f3efd8",
            DigestUtils.md2Hex("1234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890"));

        assertEquals(DigestUtils.md2Hex(testData),
                DigestUtils.md2Hex(new ByteArrayInputStream(testData)));
}

    /**
     * An MD2 hash converted to hex should always be 32 characters.
     */
    @Test
    public void testMd2HexLength() {
        String hashMe = "this is some string that is longer than 32 characters";
        String hash = DigestUtils.md2Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());

        hashMe = "length < 32";
        hash = DigestUtils.md2Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
    }

    /**
     * An MD2 hash should always be a 16 element byte[].
     */
    @Test
    public void testMd2Length() {
        String hashMe = "this is some string that is longer than 16 characters";
        byte[] hash = DigestUtils.md2(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);

        hashMe = "length < 16";
        hash = DigestUtils.md2(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
    }

    @Test
    public void testMd5Hex() throws IOException {
        // Examples from RFC 1321
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtils.md5Hex(EMPTY_STRING));

        assertEquals("0cc175b9c0f1b6a831c399e269772661", DigestUtils.md5Hex("a"));

        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex("abc"));

        assertEquals("f96b697d7cb7938d525a2f31aaf161d0", DigestUtils.md5Hex("message digest"));

        assertEquals("c3fcd3d76192e4007dfb496cca67e13b", DigestUtils.md5Hex("abcdefghijklmnopqrstuvwxyz"));

        assertEquals(
            "d174ab98d277d9f5a5611c2c9f419d9f",
            DigestUtils.md5Hex("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"));

        assertEquals(
            "57edf4a22be3c955ac49da2e2107b67a",
            DigestUtils.md5Hex("1234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890"));

        assertEquals(DigestUtils.md5Hex(testData),
                DigestUtils.md5Hex(new ByteArrayInputStream(testData)));
    }

    /**
     * An MD5 hash converted to hex should always be 32 characters.
     */
    @Test
    public void testMd5HexLengthForBytes() {
        String hashMe = "this is some string that is longer than 32 characters";
        String hash = DigestUtils.md5Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());

        hashMe = "length < 32";
        hash = DigestUtils.md5Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
    }

    /**
     * An MD5 hash should always be a 16 element byte[].
     */
    @Test
    public void testMd5LengthForBytes() {
        String hashMe = "this is some string that is longer than 16 characters";
        byte[] hash = DigestUtils.md5(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);

        hashMe = "length < 16";
        hash = DigestUtils.md5(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
    }

    @Test
    public void testSha1Hex() throws IOException {
        // Examples from FIPS 180-1
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.sha1Hex("abc"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.sha1Hex(getBytesUtf8("abc")));

        assertEquals(
            "84983e441c3bd26ebaae4aa1f95129e5e54670f1",
            DigestUtils.sha1Hex("abcdbcdecdefdefgefghfghighij" + "hijkijkljklmklmnlmnomnopnopq"));
        assertEquals(DigestUtils.sha1Hex(testData),
                DigestUtils.sha1Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha1UpdateWithByteArray(){
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";

        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());

        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, d1.getBytes());
        DigestUtils.updateDigest(messageDigest, d2.getBytes());
        final String actualResult = Hex.encodeHexString(messageDigest.digest());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSha1UpdateWithByteBuffer(){
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";

        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());

        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, ByteBuffer.wrap(d1.getBytes()));
        DigestUtils.updateDigest(messageDigest, ByteBuffer.wrap(d2.getBytes()));
        final String actualResult = Hex.encodeHexString(messageDigest.digest());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSha1UpdateWithString(){
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";

        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(StringUtils.getBytesUtf8(d1));
        messageDigest.update(StringUtils.getBytesUtf8(d2));
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());

        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, d1);
        DigestUtils.updateDigest(messageDigest, d2);
        final String actualResult = Hex.encodeHexString(messageDigest.digest());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSha224_FileAsHex() throws IOException {
        assumeJava8();
        final String expected = "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f";
        final String pathname = "src/test/resources/org/apache/commons/codec/empty.bin";
        final String algo = MessageDigestAlgorithms.SHA_224;
        final DigestUtils digestUtils = new DigestUtils(algo);
        assertEquals(expected, digestUtils.digestAsHex(new File(pathname)));
        try (final FileInputStream inputStream = new FileInputStream(pathname)) {
            assertEquals(expected, digestUtils.digestAsHex(inputStream));
        }
        final byte[] allBytes = Files.readAllBytes(Paths.get(pathname));
        assertEquals(expected, digestUtils.digestAsHex(allBytes));
        assertEquals(expected, digestUtils.digestAsHex(ByteBuffer.wrap(allBytes)));
    }

    @Test
    public void testSha224_PathAsHex() throws IOException {
        assumeJava8();
        assertEquals("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
                new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex(Paths.get("src/test/resources/org/apache/commons/codec/empty.bin")));
    }

    @Test
    public void testSha224_StringAsHex() {
        assumeJava8();
        assertEquals("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
                new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex(EMPTY_STRING));
        assertEquals("730e109bd7a8a32b1cb9d9a09aa2325d2430587ddbc0c38bad911525",
                new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex("The quick brown fox jumps over the lazy dog"));

        // Examples from FIPS 180-4?
    }

    @Test
    public void testSha256() throws IOException {
    // Examples from FIPS 180-2
    assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
             DigestUtils.sha256Hex("abc"));
    assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
             DigestUtils.sha256Hex(getBytesUtf8("abc")));
    assertEquals("248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
             DigestUtils.sha256Hex("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"));

    assertEquals(DigestUtils.sha256Hex(testData),
            DigestUtils.sha256Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha256HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha256Hex(testData),
                DigestUtils.sha256Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha3_224() {
        assumeJava9();
        // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
        //
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-224_Msg0.pdf
        assertEquals(
                "6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7",
                DigestUtils.sha3_224Hex(EMPTY_STRING));
    }

    @Test
    public void testSha3_224HexInputStream() throws IOException {
        assumeJava9();
        assertEquals(DigestUtils.sha3_224Hex(testData),
                DigestUtils.sha3_224Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha3_256() {
        assumeJava9();
        // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
        //
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-256_Msg0.pdf
        assertEquals(
                "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a",
                DigestUtils.sha3_256Hex(EMPTY_STRING));
    }

    @Test
    public void testSha3_256HexInputStream() throws IOException {
        assumeJava9();
        assertEquals(DigestUtils.sha3_256Hex(testData),
                DigestUtils.sha3_256Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha3_384() {
        assumeJava9();
        // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
        //
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-384_Msg0.pdf
        assertEquals(
                "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
                DigestUtils.sha3_384Hex(EMPTY_STRING));
    }

    @Test
    public void testSha3_384HexInputStream() throws IOException {
        assumeJava9();
        assertEquals(DigestUtils.sha3_384Hex(testData),
                DigestUtils.sha3_384Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha3_512() {
        assumeJava9();
        // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
        //
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-512_Msg0.pdf
        assertEquals(
                "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
                DigestUtils.sha3_512Hex(EMPTY_STRING));
    }

    @Test
    public void testSha3_512HexInputStream() throws IOException {
        assumeJava9();
        assertEquals(DigestUtils.sha3_512Hex(testData),
                DigestUtils.sha3_512Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha384() throws IOException {
    // Examples from FIPS 180-2
    assertEquals("cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed" +
             "8086072ba1e7cc2358baeca134c825a7",
             DigestUtils.sha384Hex("abc"));
    assertEquals("cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed" +
             "8086072ba1e7cc2358baeca134c825a7",
             DigestUtils.sha384Hex(getBytesUtf8("abc")));
    assertEquals("09330c33f71147e83d192fc782cd1b4753111b173b3b05d22fa08086e3b0f712" +
            "fcc7c71a557e2db966c3e9fa91746039",
             DigestUtils.sha384Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmn" +
                       "hijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    assertEquals(DigestUtils.sha384Hex(testData),
            DigestUtils.sha384Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha384HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha384Hex(testData),
                DigestUtils.sha384Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    public void testSha512() {
    // Examples from FIPS 180-2
    assertEquals("ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
            "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
             DigestUtils.sha512Hex("abc"));
    assertEquals("ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
             "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
             DigestUtils.sha512Hex(getBytesUtf8("abc")));
    assertEquals("8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018" +
             "501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909",
             DigestUtils.sha512Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmn" +
                       "hijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    public void testSha512_224() throws Exception {
        assumeJava9();
        // Examples from
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA512_224.pdf
        final String stringInput = "abc";
        final byte[] bytesInput = getBytesUtf8(stringInput);
        final String resultString = "4634270F707B6A54DAAE7530460842E20E37ED265CEEE9A43E8924AA".toLowerCase(Locale.ROOT);
        final byte[] resultBytes = Hex.decodeHex(resultString);
        //
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(bytesInput));
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(new ByteArrayInputStream(bytesInput)));
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(stringInput));
        //
        assertEquals(resultString, DigestUtils.sha512_224Hex(bytesInput));
        assertEquals(resultString, DigestUtils.sha512_224Hex(new ByteArrayInputStream(bytesInput)));
        assertEquals(resultString, DigestUtils.sha512_224Hex(stringInput));
        // Example 2
        assertEquals("23FEC5BB94D60B23308192640B0C453335D664734FE40E7268674AF9".toLowerCase(Locale.ROOT),
            DigestUtils.sha512_224Hex(
                "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    public void testSha512_256() throws Exception {
        assumeJava9();
        // Examples from
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA512_256.pdf
        final String stringInput = "abc";
        final byte[] bytesInput = getBytesUtf8(stringInput);
        final String resultString = "53048E2681941EF99B2E29B76B4C7DABE4C2D0C634FC6D46E0E2F13107E7AF23"
            .toLowerCase(Locale.ROOT);
        final byte[] resultBytes = Hex.decodeHex(resultString);
        //
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(bytesInput));
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(new ByteArrayInputStream(bytesInput)));
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(stringInput));
        //
        assertEquals(resultString, DigestUtils.sha512_256Hex(bytesInput));
        assertEquals(resultString, DigestUtils.sha512_256Hex(new ByteArrayInputStream(bytesInput)));
        assertEquals(resultString, DigestUtils.sha512_256Hex(stringInput));
        // Example 2
        assertEquals("3928E184FB8690F840DA3988121D31BE65CB9D3EF83EE6146FEAC861E19B563A".toLowerCase(Locale.ROOT),
            DigestUtils.sha512_256Hex(
                "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    public void testSha512HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha512Hex(testData),
                DigestUtils.sha512Hex(new ByteArrayInputStream(testData)));
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    public void testShaHex() throws IOException {
        // Examples from FIPS 180-1
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex("abc"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex(getBytesUtf8("abc")));

        assertEquals(
            "84983e441c3bd26ebaae4aa1f95129e5e54670f1",
            DigestUtils.shaHex("abcdbcdecdefdefgefghfghighij" + "hijkijkljklmklmnlmnomnopnopq"));
        assertEquals(DigestUtils.shaHex(testData),
                DigestUtils.shaHex(new ByteArrayInputStream(testData)));
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    public void testShaUpdateWithByteArray(){
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";

        MessageDigest messageDigest = DigestUtils.getShaDigest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());

        messageDigest = DigestUtils.getShaDigest();
        DigestUtils.updateDigest(messageDigest, d1.getBytes());
        DigestUtils.updateDigest(messageDigest, d2.getBytes());
        final String actualResult = Hex.encodeHexString(messageDigest.digest());

        assertEquals(expectedResult, actualResult);
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    public void testShaUpdateWithString(){
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";

        MessageDigest messageDigest = DigestUtils.getShaDigest();
        messageDigest.update(StringUtils.getBytesUtf8(d1));
        messageDigest.update(StringUtils.getBytesUtf8(d2));
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());

        messageDigest = DigestUtils.getShaDigest();
        DigestUtils.updateDigest(messageDigest, d1);
        DigestUtils.updateDigest(messageDigest, d2);
        final String actualResult = Hex.encodeHexString(messageDigest.digest());

        assertEquals(expectedResult, actualResult);
    }

}
