/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.GitIdentifiers.DirectoryEntry;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link GitIdentifiers}.
 */
class GitIdentifiersTest {

    /**
     * Binary body of the test tree object used in {@link #testTreeIdCollection}.
     *
     * <p>Each entry has the format {@code <mode> SP <name> NUL <20-byte-object-id>}.</p>
     */
    private static final String TREE_BODY_HEX =
            // 100644 hello.txt\0 + objectId
            "3130303634342068656c6c6f2e74787400" + "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0" +
            // 120000 link.txt\0 + objectId
            "313230303030206c696e6b2e74787400" + "1234567890abcdef1234567890abcdef12345678" +
            // 100755 run.sh\0 + objectId
            "3130303735352072756e2e736800" + "f0e1d2c3b4a5f6e7d8c9b0a1f2e3d4c5b6a7f8e9" +
            // 40000 src\0 + objectId
            "34303030302073726300" + "deadbeefdeadbeefdeadbeefdeadbeefdeadbeef";

    static Stream<Arguments> blobIdProvider() {
        return Stream.of(Arguments.of("DigestUtilsTest/hello.txt", "5f4a83288e67f1be2d6fcdad84165a86c6a970d7"),
                Arguments.of("DigestUtilsTest/greetings.txt", "6cf4f797455661e61d1ee6913fc29344f5897243"),
                Arguments.of("DigestUtilsTest/subdir/nested.txt", "07a392ddb4dbff06a373a7617939f30b2dcfe719"));
    }

    private static Path resourcePath(final String resourceName) throws Exception {
        return Paths.get(GitIdentifiersTest.class.getClassLoader().getResource(resourceName).toURI());
    }

    @ParameterizedTest
    @MethodSource("blobIdProvider")
    void testBlobIdByteArray(final String resourceName, final String expectedSha1Hex) throws Exception {
        final byte[] data = Files.readAllBytes(resourcePath(resourceName));
        assertArrayEquals(Hex.decodeHex(expectedSha1Hex), GitIdentifiers.blobId(DigestUtils.getSha1Digest(), data));
    }

    @ParameterizedTest
    @MethodSource("blobIdProvider")
    void testBlobIdPath(final String resourceName, final String expectedSha1Hex) throws Exception {
        assertArrayEquals(Hex.decodeHex(expectedSha1Hex), GitIdentifiers.blobId(DigestUtils.getSha1Digest(), resourcePath(resourceName)));
    }


    private static final byte[] ZERO_ID = new byte[20];

    @Test
    void testDirectoryEntryConstructor() {
        assertThrows(NullPointerException.class, () -> new DirectoryEntry(null, DirectoryEntry.Type.REGULAR, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new DirectoryEntry(Paths.get("hello.txt"), null, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new DirectoryEntry(Paths.get("hello.txt"), DirectoryEntry.Type.REGULAR, null));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryEntry(Paths.get("/"), DirectoryEntry.Type.REGULAR, ZERO_ID));
    }

    /**
     * Equality and hash code are based solely on the entry name.
     */
    @Test
    void testDirectoryEntryEqualityBasedOnNameOnly() {
        final byte[] otherId = new byte[20];
        Arrays.fill(otherId, (byte) 0xff);
        final DirectoryEntry regular = new DirectoryEntry(Paths.get("foo"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry executable = new DirectoryEntry(Paths.get("foo"), DirectoryEntry.Type.EXECUTABLE, otherId);
        // Same name, different type and object id -> equal
        assertEquals(regular, executable);
        assertEquals(regular.hashCode(), executable.hashCode());
        // Different name -> not equal
        assertNotEquals(regular, new DirectoryEntry(Paths.get("bar"), DirectoryEntry.Type.REGULAR, ZERO_ID));
        // Same reference -> equal
        assertEquals(regular, regular);
        // Not equal to null or unrelated type
        assertNotEquals(null, regular);
        assertNotEquals("foo", regular);
    }

    /**
     * The Path constructor must extract the filename component.
     */
    @Test
    void testDirectoryEntryPathConstructorUsesFilename() {
        final DirectoryEntry fromLabel = new DirectoryEntry(Paths.get("hello.txt"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry fromRelative = new DirectoryEntry(Paths.get("subdir/hello.txt"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry fromAbsolute = new DirectoryEntry(Paths.get("hello.txt").toAbsolutePath(), DirectoryEntry.Type.REGULAR, ZERO_ID);
        assertEquals(fromLabel, fromRelative);
        assertEquals(fromLabel, fromAbsolute);
        assertArrayEquals(fromLabel.toTreeEntryBytes(), fromRelative.toTreeEntryBytes());
        assertArrayEquals(fromLabel.toTreeEntryBytes(), fromAbsolute.toTreeEntryBytes());
    }

    /**
     * Entries should be sorted by Git sort rule.
     *
     * <p>Git compares the names of the entries, but adds a {@code /} at the end of directory entries.</p>
     */
    @Test
    void testDirectoryEntrySortOrder() {
        final DirectoryEntry alpha = new DirectoryEntry(Paths.get("alpha.txt"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry fooTxt = new DirectoryEntry(Paths.get("foo.txt"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry fooDir = new DirectoryEntry(Paths.get("foo"), DirectoryEntry.Type.DIRECTORY, ZERO_ID);
        final DirectoryEntry foobar = new DirectoryEntry(Paths.get("foobar"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final DirectoryEntry zeta = new DirectoryEntry(Paths.get("zeta.txt"), DirectoryEntry.Type.REGULAR, ZERO_ID);
        final List<DirectoryEntry> entries = new ArrayList<>(Arrays.asList(zeta, foobar, fooDir, alpha, fooTxt));
        entries.sort(DirectoryEntry::compareTo);
        assertEquals(Arrays.asList(alpha, fooTxt, fooDir, foobar, zeta), entries);
    }

    @Test
    void testBlobIdSymlink(@TempDir final Path tempDir) throws Exception {
        final Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.write(subDir.resolve("file.txt"), "hello".getBytes(StandardCharsets.UTF_8));
        final Path linkToDir;
        final Path linkToFile;
        try {
            linkToDir = Files.createSymbolicLink(tempDir.resolve("link-to-dir"), Paths.get("subdir"));
            linkToFile = Files.createSymbolicLink(tempDir.resolve("link-to-file"), Paths.get("subdir/file.txt"));
        } catch (final UnsupportedOperationException e) {
            Assumptions.assumeTrue(false, "Symbolic links not supported on this filesystem");
            return;
        }
        final MessageDigest sha1 = DigestUtils.getSha1Digest();
        assertArrayEquals(Hex.decodeHex("8bbe8a53790056316b23b7c270f10ab6bf6bb1b4"), GitIdentifiers.blobId(sha1, linkToDir));
        assertArrayEquals(Hex.decodeHex("dfe6ef8392ae13a11ff85419b4fd906d997b6cb7"), GitIdentifiers.blobId(sha1, linkToFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256})
    void testTreeIdCollection(final String algorithm) throws Exception {
        final byte[] helloId = Hex.decodeHex("a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0");
        final byte[] runId = Hex.decodeHex("f0e1d2c3b4a5f6e7d8c9b0a1f2e3d4c5b6a7f8e9");
        final byte[] linkId = Hex.decodeHex("1234567890abcdef1234567890abcdef12345678");
        final byte[] srcId = Hex.decodeHex("deadbeefdeadbeefdeadbeefdeadbeefdeadbeef");

        // Entries are supplied out of order to verify that the method sorts them correctly.
        final List<DirectoryEntry> entries = new ArrayList<>();
        entries.add(new DirectoryEntry(Paths.get("src"), DirectoryEntry.Type.DIRECTORY, srcId));
        entries.add(new DirectoryEntry(Paths.get("run.sh"), DirectoryEntry.Type.EXECUTABLE, runId));
        entries.add(new DirectoryEntry(Paths.get("hello.txt"), DirectoryEntry.Type.REGULAR, helloId));
        entries.add(new DirectoryEntry(Paths.get("link.txt"), DirectoryEntry.Type.SYMBOLIC_LINK, linkId));

        // Compute expected value
        final byte[] treeBody = Hex.decodeHex(TREE_BODY_HEX);
        final MessageDigest md = DigestUtils.getDigest(algorithm);
        DigestUtils.updateDigest(md, ("tree " + treeBody.length + "\0").getBytes(StandardCharsets.UTF_8));
        final byte[] expected = DigestUtils.updateDigest(md, treeBody).digest();

        assertArrayEquals(expected, GitIdentifiers.treeId(md, entries));
    }

    @Test
    void testTreeIdPath() throws Exception {
        assertArrayEquals(Hex.decodeHex("e4b21f6d78ceba6eb7c211ac15e3337ec4614e8a"),
                GitIdentifiers.treeId(DigestUtils.getSha1Digest(), resourcePath("DigestUtilsTest")));
    }

}
