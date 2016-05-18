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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests {@link MessageDigestAlgorithms}.
 *
 * @since 1.11
 */
@RunWith(Parameterized.class)
public class MessageDigestAlgorithmsTest {

    @Parameters(name = "{0}")
    public static Object[] data() {
        return MessageDigestAlgorithms.values();
    }

    private DigestUtilsTest digestUtilsTest;

    private final String messageDigestAlgorithm;

    public MessageDigestAlgorithmsTest(String messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

    private byte[] digestTestData() throws IOException {
        return DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData());
    }

    private byte[] getTestData() {
        return digestUtilsTest.getTestData();
    }

    private File getTestFile() {
        return digestUtilsTest.getTestFile();
    }

    @Before
    public void setUp() throws Exception {
        digestUtilsTest = new DigestUtilsTest();
        digestUtilsTest.setUp();
    }

    @After
    public void tearDown() throws Exception {
        digestUtilsTest.tearDown();
        digestUtilsTest = null;
    }

    @Test
    public void testAlgorithm() throws IOException, NoSuchAlgorithmException {
        final String algorithm = messageDigestAlgorithm;
        Assert.assertNotNull(algorithm);
        Assert.assertFalse(algorithm.isEmpty());
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        MessageDigest.getInstance(algorithm);
    }

    @Test
    public void testDigestByteArray() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestData()));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData()));
    }

    @Test
    public void testDigestByteBuffer() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), ByteBuffer.wrap(getTestData())));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),ByteBuffer.wrap(getTestData())));
    }

    @Test
    public void testDigestFile() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestFile()));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestFile()));
    }

    @Test
    public void testDigestInputStream() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), new ByteArrayInputStream(getTestData())));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),new ByteArrayInputStream(getTestData())));
    }

    @Test
    public void testGetMessageDigest() throws IOException, NoSuchAlgorithmException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        final MessageDigest messageDigest = DigestUtils.getDigest(messageDigestAlgorithm);
        Assert.assertEquals(messageDigestAlgorithm, messageDigest.getAlgorithm());
    }

}
