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
 * Tests {@link MessageDigestAlgorithm}.
 *
 * @since 1.11
 */
@RunWith(Parameterized.class)
public class MessageDigestAlgorithmTest {

    @Parameters(name = "{0}")
    public static Object[] data() {
        return MessageDigestAlgorithm.values();
    }

    private DigestUtilsTest digestUtilsTest;

    private final MessageDigestAlgorithm messageDigestAlgorithm;

    public MessageDigestAlgorithmTest(MessageDigestAlgorithm messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

    private byte[] digestTestData() throws IOException {
        return messageDigestAlgorithm.digest(getTestData());
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
        final String algorithm = messageDigestAlgorithm.getAlgorithm();
        Assert.assertNotNull(algorithm);
        Assert.assertFalse(algorithm.isEmpty());
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        MessageDigest.getInstance(algorithm);
    }

    @Test
    public void testDigestByteArray() throws IOException {
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(messageDigestAlgorithm.getMessageDigest(), getTestData()));
        Assert.assertArrayEquals(digestTestData(), messageDigestAlgorithm.digest(getTestData()));
    }

    @Test
    public void testDigestByteBuffer() throws IOException {
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(messageDigestAlgorithm.getMessageDigest(), ByteBuffer.wrap(getTestData())));
        Assert.assertArrayEquals(digestTestData(), messageDigestAlgorithm.digest(ByteBuffer.wrap(getTestData())));
    }

    @Test
    public void testDigestFile() throws IOException {
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(messageDigestAlgorithm.getMessageDigest(), getTestFile()));
        Assert.assertArrayEquals(digestTestData(), messageDigestAlgorithm.digest(getTestFile()));
    }

    @Test
    public void testDigestInputStream() throws IOException {
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(messageDigestAlgorithm.getMessageDigest(), new ByteArrayInputStream(getTestData())));
        Assert.assertArrayEquals(digestTestData(), messageDigestAlgorithm.digest(new ByteArrayInputStream(getTestData())));
    }

    @Test
    public void testGetMessageDigest() throws IOException, NoSuchAlgorithmException {
        Assume.assumeTrue(messageDigestAlgorithm.isAvailable());
        final MessageDigest messageDigest = messageDigestAlgorithm.getMessageDigest();
        Assert.assertEquals(messageDigestAlgorithm.getAlgorithm(), messageDigest.getAlgorithm());
    }

}
