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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
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


    private static final byte[] ZERO_ID = new byte[20];

    static Stream<Arguments> blobIdProvider() {
        return Stream.of(Arguments.of("DigestUtilsTest/hello.txt", "5f4a83288e67f1be2d6fcdad84165a86c6a970d7"),
                Arguments.of("DigestUtilsTest/greetings.txt", "6cf4f797455661e61d1ee6913fc29344f5897243"),
                Arguments.of("DigestUtilsTest/subdir/nested.txt", "07a392ddb4dbff06a373a7617939f30b2dcfe719"));
    }

    private static Path resourcePath(final String resourceName) throws Exception {
        return Paths.get(GitIdentifiersTest.class.getClassLoader().getResource(resourceName).toURI());
    }

    static Stream<Arguments> testTreeIdBuilder() {
        return Stream.of(
                Arguments.of(MessageDigestAlgorithms.SHA_1,
                        "ce013625030ba8dba906f756967f9e9ca394464a",  // blob id of "hello\n"
                        "8bbe8a53790056316b23b7c270f10ab6bf6bb1b4",  // blob id of "subdir"
                        "1a2485251c33a70432394c93fb89330ef214bfc9",  // blob id of "#!/bin/sh\n"
                        "4b825dc642cb6eb9a060e54bf8d69288fbee4904"), // tree id of empty directory
                Arguments.of(MessageDigestAlgorithms.SHA_256,
                        "2cf8d83d9ee29543b34a87727421fdecb7e3f3a183d337639025de576db9ebb4",
                        "33910dae80b0db75dbad7fa521dbbf1885a07edfab1228871c41a2e94ccd7edb",
                        "1249034e3cf9007362d695b09b1fbdb4c578903bf10b665749b94743f8177ce1",
                        "6ef19b41225c5369f1c104d45d8d85efa9b057b53b14b4b9b939dd74decc5321"));
    }

    @ParameterizedTest
    @MethodSource("blobIdProvider")
    void testBlobIdByteArray(final String resourceName, final String expectedSha1Hex) throws Exception {
        final byte[] data = Files.readAllBytes(resourcePath(resourceName));
        assertArrayEquals(Hex.decodeHex(expectedSha1Hex), GitIdentifiers.blobId(DigestUtils.getSha1Digest(), data));
    }

    @ParameterizedTest
    @MethodSource("blobIdProvider")
    void testBlobIdInputStreamWithSize(final String resourceName, final String expectedSha1Hex) throws Exception {
        final byte[] data = Files.readAllBytes(resourcePath(resourceName));
        assertArrayEquals(Hex.decodeHex(expectedSha1Hex),
                GitIdentifiers.blobId(DigestUtils.getSha1Digest(), data.length, new ByteArrayInputStream(data)));
    }

    @ParameterizedTest
    @MethodSource("blobIdProvider")
    void testBlobIdPath(final String resourceName, final String expectedSha1Hex) throws Exception {
        assertArrayEquals(Hex.decodeHex(expectedSha1Hex), GitIdentifiers.blobId(DigestUtils.getSha1Digest(), resourcePath(resourceName)));
    }

    @Test
    void testBlobIdSymlink(@TempDir final Path tempDir) throws Exception {
        final Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.write(subDir.resolve("file.txt"), "hello".getBytes(StandardCharsets.UTF_8));
        try {
            final Path linkToDir = Files.createSymbolicLink(tempDir.resolve("link-to-dir"), Paths.get("subdir"));
            final Path linkToFile = Files.createSymbolicLink(tempDir.resolve("link-to-file"), Paths.get("subdir/file.txt"));
            final MessageDigest sha1 = DigestUtils.getSha1Digest();
            assertArrayEquals(Hex.decodeHex("8bbe8a53790056316b23b7c270f10ab6bf6bb1b4"), GitIdentifiers.blobId(sha1, linkToDir));
            assertArrayEquals(Hex.decodeHex("dfe6ef8392ae13a11ff85419b4fd906d997b6cb7"), GitIdentifiers.blobId(sha1, linkToFile));
        } catch (final UnsupportedOperationException e) {
            Assumptions.abort("Symbolic links not supported on this filesystem");
        }
    }

    @Test
    void testDirectoryEntryConstructor() {
        assertThrows(NullPointerException.class, () -> new DirectoryEntry(null, GitIdentifiers.FileMode.REGULAR, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new DirectoryEntry("hello.txt", null, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new DirectoryEntry("hello.txt", GitIdentifiers.FileMode.REGULAR, null));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryEntry("/", GitIdentifiers.FileMode.REGULAR, ZERO_ID));
    }

    /**
     * Equality and hash code are based solely on the entry name.
     */
    @Test
    void testDirectoryEntryEqualityBasedOnNameOnly() {
        final byte[] otherId = new byte[20];
        Arrays.fill(otherId, (byte) 0xff);
        final DirectoryEntry regular = new DirectoryEntry("foo", GitIdentifiers.FileMode.REGULAR, ZERO_ID);
        final DirectoryEntry executable = new DirectoryEntry("foo", GitIdentifiers.FileMode.EXECUTABLE, otherId);
        // Same name, different type and object id -> equal
        assertEquals(regular, executable);
        assertEquals(regular.hashCode(), executable.hashCode());
        // Different name -> not equal
        assertNotEquals(regular, new DirectoryEntry("bar", GitIdentifiers.FileMode.REGULAR, ZERO_ID));
        // Same reference -> equal
        assertEquals(regular, regular);
        // Not equal to null or unrelated type
        assertFalse(regular.equals(null));
        assertFalse(regular.equals("foo"));
    }

    /**
     * Entries should be sorted by Git sort rule.
     *
     * <p>Git compares the names of the entries, but adds a {@code /} at the end of directory entries.</p>
     */
    @Test
    void testDirectoryEntrySortOrder() {
        final DirectoryEntry alpha = new DirectoryEntry("alpha.txt", GitIdentifiers.FileMode.REGULAR, ZERO_ID);
        final DirectoryEntry fooTxt = new DirectoryEntry("foo.txt", GitIdentifiers.FileMode.REGULAR, ZERO_ID);
        final DirectoryEntry fooDir = new DirectoryEntry("foo", GitIdentifiers.FileMode.DIRECTORY, ZERO_ID);
        final DirectoryEntry foobar = new DirectoryEntry("foobar", GitIdentifiers.FileMode.REGULAR, ZERO_ID);
        final DirectoryEntry zeta = new DirectoryEntry("zeta.txt", GitIdentifiers.FileMode.REGULAR, ZERO_ID);
        final List<DirectoryEntry> entries = new ArrayList<>(Arrays.asList(zeta, foobar, fooDir, alpha, fooTxt));
        entries.sort(DirectoryEntry::compareTo);
        assertEquals(Arrays.asList(alpha, fooTxt, fooDir, foobar, zeta), entries);
    }

    @ParameterizedTest
    @MethodSource
    void testTreeIdBuilder(final String algorithm, final String helloHex, final String linkHex, final String runHex, final String srcHex) throws Exception {
        final byte[] helloContent = "hello\n".getBytes(StandardCharsets.UTF_8);
        final byte[] runContent = "#!/bin/sh\n".getBytes(StandardCharsets.UTF_8);
        final String linkTarget = "subdir";
        final MessageDigest md = DigestUtils.getDigest(algorithm);

        // Verify individual blob IDs against pre-computed constants.
        assertArrayEquals(Hex.decodeHex(helloHex), GitIdentifiers.blobId(md, helloContent));
        assertArrayEquals(Hex.decodeHex(linkHex), GitIdentifiers.blobId(md, linkTarget.getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(Hex.decodeHex(runHex), GitIdentifiers.blobId(md, runContent));

        // Entries are supplied out of order to verify that the builder sorts them correctly.
        final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(md);
        builder.addDirectory("src");
        builder.addFile(GitIdentifiers.FileMode.EXECUTABLE, "run.sh", runContent);
        builder.addFile(GitIdentifiers.FileMode.REGULAR, "hello.txt", helloContent);
        builder.addSymbolicLink("link.txt", linkTarget);

        // Expected tree body: entries in Git sort order (hello.txt, link.txt, run.sh, src/).
        // Each entry: hex-encoded "<mode> <name>\0" followed by the object id.
        final byte[] treeBody = Hex.decodeHex("3130303634342068656c6c6f2e74787400" + helloHex +   // 100644 hello.txt\0
                "313230303030206c696e6b2e74787400" + linkHex +   // 120000 link.txt\0
                "3130303735352072756e2e736800" + runHex +   // 100755 run.sh\0
                "34303030302073726300" + srcHex);   // 40000 src\0
        md.reset();
        DigestUtils.updateDigest(md, ("tree " + treeBody.length + "\0").getBytes(StandardCharsets.UTF_8));
        assertArrayEquals(DigestUtils.updateDigest(md, treeBody).digest(), builder.build());
    }

    @Test
    void testTreeIdBuilderAddFileInputStream() throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "Hello, World!\n".getBytes(StandardCharsets.UTF_8);

        final GitIdentifiers.TreeIdBuilder byteArrayBuilder = GitIdentifiers.treeIdBuilder(md);
        byteArrayBuilder.addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);
        final byte[] expected = byteArrayBuilder.build();

        final GitIdentifiers.TreeIdBuilder sizedStreamBuilder = GitIdentifiers.treeIdBuilder(md);
        sizedStreamBuilder.addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content.length, new ByteArrayInputStream(content));
        assertArrayEquals(expected, sizedStreamBuilder.build());
    }

    @Test
    void testTreeIdBuilderEmptyPathSegments() throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "hello\n".getBytes(StandardCharsets.UTF_8);

        // Canonical form
        final GitIdentifiers.TreeIdBuilder canonical = GitIdentifiers.treeIdBuilder(md);
        canonical.addFile(GitIdentifiers.FileMode.REGULAR, "subdir/file.txt", content);
        final byte[] expected = canonical.build();

        // Leading slash
        final GitIdentifiers.TreeIdBuilder withLeading = GitIdentifiers.treeIdBuilder(md);
        withLeading.addFile(GitIdentifiers.FileMode.REGULAR, "/subdir/file.txt", content);
        assertArrayEquals(expected, withLeading.build());

        // Consecutive slashes
        final GitIdentifiers.TreeIdBuilder withDouble = GitIdentifiers.treeIdBuilder(md);
        withDouble.addFile(GitIdentifiers.FileMode.REGULAR, "subdir//file.txt", content);
        assertArrayEquals(expected, withDouble.build());

        // addDirectory with leading/trailing slashes
        final GitIdentifiers.TreeIdBuilder viaDirectory = GitIdentifiers.treeIdBuilder(md);
        viaDirectory.addDirectory("/subdir/").addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);
        assertArrayEquals(expected, viaDirectory.build());
    }

    @ParameterizedTest
    @ValueSource(strings = {".", ".."})
    void testTreeIdBuilderInvalidPathSegments(final String segment) {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] data = new byte[0];
        // Sole path component
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addFile(GitIdentifiers.FileMode.REGULAR, segment, data));
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addDirectory(segment));
        // Embedded in a longer path
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addFile(GitIdentifiers.FileMode.REGULAR, "subdir/" + segment + "/file.txt", data));
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addDirectory("subdir/" + segment));
    }

    @Test
    void testTreeIdBuilderNestedFileEquivalentToDirectoryAndFile() throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "hello\n".getBytes(StandardCharsets.UTF_8);

        final GitIdentifiers.TreeIdBuilder direct = GitIdentifiers.treeIdBuilder(md);
        direct.addFile(GitIdentifiers.FileMode.REGULAR, "nested/file.txt", content);

        final GitIdentifiers.TreeIdBuilder indirect = GitIdentifiers.treeIdBuilder(md);
        indirect.addDirectory("nested").addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);

        assertArrayEquals(direct.build(), indirect.build());
    }

    @Test
    void testTreeIdPath() throws Exception {
        assertArrayEquals(Hex.decodeHex("e4b21f6d78ceba6eb7c211ac15e3337ec4614e8a"),
                GitIdentifiers.treeId(DigestUtils.getSha1Digest(), resourcePath("DigestUtilsTest")));
    }

}
