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
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void checkValues() throws Exception {
        final Field [] fields = MessageDigestAlgorithms.class.getDeclaredFields();
        boolean ok = true;
        int psf = 0;
        for(final Field f : fields) {
            // Ignore cobertura instrumentation fields
            if (f.getName().contains("cobertura")) {
                continue;
            }

            // Only interested in public fields
            final int modifiers = f.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                psf++;
                if (!contains((String) f.get(null))) {
                    System.out.println("Not found in MessageDigestAlgorithms.values(): "+f.getName());
                    ok = false;
                }
            }
        }
        if (!ok) {
            Assert.fail("One or more entries are missing from the MessageDigestAlgorithms.values() array");
        }
        if (psf != MessageDigestAlgorithms.values().length) {
            Assert.fail("One or more unexpected entries found in the MessageDigestAlgorithms.values() array");
        }
    }

    private static boolean contains(final String key) {
        for(final String s : MessageDigestAlgorithms.values()) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Parameters(name = "{0}")
    public static Object[] data() {
        return MessageDigestAlgorithms.values();
    }

    private DigestUtilsTest digestUtilsTest;

    private final String messageDigestAlgorithm;

    public MessageDigestAlgorithmsTest(final String messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

    private byte[] digestTestData() {
        return DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData());
    }

    private byte[] getTestData() {
        return digestUtilsTest.getTestData();
    }

    private File getTestFile() {
        return digestUtilsTest.getTestFile();
    }

    private Path getTestPath() {
        return digestUtilsTest.getTestPath();
    }

    private RandomAccessFile getTestRandomAccessFile() {
        return digestUtilsTest.getTestRandomAccessFile();
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
    public void testAlgorithm() throws NoSuchAlgorithmException {
        final String algorithm = messageDigestAlgorithm;
        Assert.assertNotNull(algorithm);
        Assert.assertFalse(algorithm.isEmpty());
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        MessageDigest.getInstance(algorithm);
    }

    @Test
    public void testDigestByteArray() {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestData()));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData()));
    }

    @Test
    public void testDigestByteBuffer() {
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
        Assert.assertArrayEquals(digestTestData(),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestFile()));
    }

    @Test
    public void testDigestInputStream() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), new ByteArrayInputStream(getTestData())));
        Assert.assertArrayEquals(digestTestData(), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),new ByteArrayInputStream(getTestData())));
    }

    private void testDigestPath(OpenOption... options) throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        Assert.assertArrayEquals(digestTestData(),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestPath(), options));
        Assert.assertArrayEquals(digestTestData(),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestPath(), options));
    }

    @Test
    public void testDigestPathOpenOptionsEmpty() throws IOException {
        testDigestPath();
    }

    @Test
    public void testDigestPathStandardOpenOptionRead() throws IOException {
        testDigestPath(StandardOpenOption.READ);
    }

    @Test
    public void testGetMessageDigest() {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        final MessageDigest messageDigest = DigestUtils.getDigest(messageDigestAlgorithm);
        Assert.assertEquals(messageDigestAlgorithm, messageDigest.getAlgorithm());
    }

    @Test
    public void testNonBlockingDigestRandomAccessFile() throws IOException {
        Assume.assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));

        final byte[] expected = digestTestData();

        Assert.assertArrayEquals(expected,
                DigestUtils.digest(
                        DigestUtils.getDigest(messageDigestAlgorithm), getTestRandomAccessFile()
                )
        );
        getTestRandomAccessFile().seek(0);
        Assert.assertArrayEquals(expected,
                DigestUtils.digest(
                        DigestUtils.getDigest(messageDigestAlgorithm), getTestRandomAccessFile()
                )
        );
    }

}
