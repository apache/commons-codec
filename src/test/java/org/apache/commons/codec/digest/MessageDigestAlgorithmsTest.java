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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests {@link MessageDigestAlgorithms}.
 *
 * @since 1.11
 */
public class MessageDigestAlgorithmsTest {

    @BeforeAll
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
            fail("One or more entries are missing from the MessageDigestAlgorithms.values() array");
        }
        if (psf != MessageDigestAlgorithms.values().length) {
            fail("One or more unexpected entries found in the MessageDigestAlgorithms.values() array");
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

    public static String[] data() {
        return MessageDigestAlgorithms.values();
    }

    private DigestUtilsTest digestUtilsTest;

    private byte[] digestTestData(final String messageDigestAlgorithm) {
        return DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData());
    }

    private byte[] getTestData() {
        return digestUtilsTest.getTestData();
    }

    private File getTestFile() {
        return digestUtilsTest.getTestPath().toFile();
    }

    private Path getTestPath() {
        return digestUtilsTest.getTestPath();
    }

    private RandomAccessFile getTestRandomAccessFile() {
        return digestUtilsTest.getTestRandomAccessFile();
    }

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
    public void testAlgorithm(final String messageDigestAlgorithm) throws NoSuchAlgorithmException {
        final String algorithm = messageDigestAlgorithm;
        assertNotNull(algorithm);
        assertFalse(algorithm.isEmpty());
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        MessageDigest.getInstance(algorithm);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestByteArray(final String messageDigestAlgorithm) {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestData()));
        assertArrayEquals(digestTestData(messageDigestAlgorithm), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),getTestData()));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestByteBuffer(final String messageDigestAlgorithm) {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), ByteBuffer.wrap(getTestData())));
        assertArrayEquals(digestTestData(messageDigestAlgorithm), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),ByteBuffer.wrap(getTestData())));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestFile(final String messageDigestAlgorithm) throws IOException {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestFile()));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestFile()));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestInputStream(final String messageDigestAlgorithm) throws IOException {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
                DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), new ByteArrayInputStream(getTestData())));
        assertArrayEquals(digestTestData(messageDigestAlgorithm), DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm),new ByteArrayInputStream(getTestData())));
    }

    @ParameterizedTest
    @MethodSource("data")
    private void testDigestPath(final String messageDigestAlgorithm, final OpenOption... options) throws IOException {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestPath(), options));
        assertArrayEquals(digestTestData(messageDigestAlgorithm),
            DigestUtils.digest(DigestUtils.getDigest(messageDigestAlgorithm), getTestPath(), options));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestPathOpenOptionsEmpty(final String messageDigestAlgorithm) throws IOException {
        testDigestPath(messageDigestAlgorithm);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testDigestPathStandardOpenOptionRead(final String messageDigestAlgorithm) throws IOException {
        testDigestPath(messageDigestAlgorithm, StandardOpenOption.READ);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testGetMessageDigest(final String messageDigestAlgorithm) {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));
        final MessageDigest messageDigest = DigestUtils.getDigest(messageDigestAlgorithm);
        assertEquals(messageDigestAlgorithm, messageDigest.getAlgorithm());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNonBlockingDigestRandomAccessFile(final String messageDigestAlgorithm) throws IOException {
        assumeTrue(DigestUtils.isAvailable(messageDigestAlgorithm));

        final byte[] expected = digestTestData(messageDigestAlgorithm);

        assertArrayEquals(expected,
                DigestUtils.digest(
                        DigestUtils.getDigest(messageDigestAlgorithm), getTestRandomAccessFile()
                )
        );
        getTestRandomAccessFile().seek(0);
        assertArrayEquals(expected,
                DigestUtils.digest(
                        DigestUtils.getDigest(messageDigestAlgorithm), getTestRandomAccessFile()
                )
        );
    }

}
