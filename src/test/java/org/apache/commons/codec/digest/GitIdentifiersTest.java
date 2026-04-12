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
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.codec.DecoderException;
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

    // Virtual tree:
    //
    // link      -> src           (symlink)
    // link.txt  -> src/hello.txt (symlink)
    // src/
    //   hello.txt                (regular file)
    //   run.sh                   (executable file)

    /** Content of {@code src/hello.txt}. */
    private static final byte[] HELLO_CONTENT = "hello\n".getBytes(StandardCharsets.UTF_8);
    /** SHA-1 blob id of {@link #HELLO_CONTENT}: {@code printf 'hello\n' | git hash-object --stdin} */
    private static final byte[] HELLO_BLOB_ID_SHA1 = hex("ce013625030ba8dba906f756967f9e9ca394464a");
    /** SHA-256 blob id of {@link #HELLO_CONTENT}. */
    private static final byte[] HELLO_BLOB_ID_SHA256 = hex("2cf8d83d9ee29543b34a87727421fdecb7e3f3a183d337639025de576db9ebb4");

    /** Content of {@code src/run.sh}. */
    private static final byte[] RUN_CONTENT = "#!/bin/sh\n".getBytes(StandardCharsets.UTF_8);
    /** SHA-1 blob id of {@link #RUN_CONTENT}: {@code printf '#!/bin/sh\n' | git hash-object --stdin} */
    private static final byte[] RUN_BLOB_ID_SHA1 = hex("1a2485251c33a70432394c93fb89330ef214bfc9");
    /** SHA-256 blob id of {@link #RUN_CONTENT}. */
    private static final byte[] RUN_BLOB_ID_SHA256 = hex("1249034e3cf9007362d695b09b1fbdb4c578903bf10b665749b94743f8177ce1");

    /** Target of symlink {@code link}. */
    private static final String LINK_CONTENT = "src";
    /** SHA-1 blob id of the symlink target {@link #LINK_CONTENT}: {@code printf 'src' | git hash-object --stdin} */
    private static final byte[] LINK_BLOB_ID_SHA1 = hex("e8310385c56dc4bbe379f43400f3181f6a59f260");
    /** SHA-256 blob id of the symlink target {@link #LINK_CONTENT}. */
    private static final byte[] LINK_BLOB_ID_SHA256 = hex("e1bdca538422554ea204da85e0cec156b12b6808473083610ff95ea390843ab6");

    /** Target of symlink {@code link.txt}. */
    private static final String LINK_TXT_CONTENT = "src/hello.txt";
    /** SHA-1 blob id of the symlink target {@link #LINK_TXT_CONTENT}: {@code printf 'src/hello.txt' | git hash-object --stdin} */
    private static final byte[] LINK_TXT_BLOB_ID_SHA1 = hex("132a953033e00dcff94f5cccb261f52cd1d71173");
    /** SHA-256 blob id of the symlink target {@link #LINK_TXT_CONTENT}. */
    private static final byte[] LINK_TXT_BLOB_ID_SHA256 = hex("2499925193a48a84a546a2f7cd3ce7789d4e073ef1e7276fe682bfbb2b636cef");

    // Tree ids can be recomputed in a git repository with:
    //   git init /tmp/t && cd /tmp/t
    // followed by writing the blob objects and calling git mktree.

    /**
     * SHA-1 tree id of {@code src/} (hello.txt + run.sh):
     * <pre>
     * printf '100644 blob ce013625030ba8dba906f756967f9e9ca394464a\thello.txt\n
     *         100755 blob 1a2485251c33a70432394c93fb89330ef214bfc9\trun.sh\n' | git mktree
     * </pre>
     */
    private static final byte[] SRC_TREE_ID_SHA1 = hex("5575b4a0141a2287ec2836a620e5d6aa8fb203ba");
    /**
     * SHA-256 tree id of {@code src/}:
     * <pre>
     * printf '100644 blob 2cf8d83d9ee29543b34a87727421fdecb7e3f3a183d337639025de576db9ebb4\thello.txt\n
     *         100755 blob 1249034e3cf9007362d695b09b1fbdb4c578903bf10b665749b94743f8177ce1\trun.sh\n' | git mktree
     * </pre>
     */
    private static final byte[] SRC_TREE_ID_SHA256 = hex("5b4e74befcb98e3050c511d02353d00565b2172be0a2bc5de833f011ad27f694");

    /**
     * SHA-1 tree id of the main directory (link + link.txt + src/):
     * <pre>
     * printf '120000 blob e8310385c56dc4bbe379f43400f3181f6a59f260\tlink\n
     *         120000 blob 132a953033e00dcff94f5cccb261f52cd1d71173\tlink.txt\n
     *         040000 tree 5575b4a0141a2287ec2836a620e5d6aa8fb203ba\tsrc\n' | git mktree
     * </pre>
     */
    private static final byte[] MAIN_TREE_ID_SHA1 = hex("3217900fd0a6624cd6aa169c2a9f289f7f34432b");
    /**
     * SHA-256 tree id of the main directory:
     * <pre>
     * printf '120000 blob e1bdca538422554ea204da85e0cec156b12b6808473083610ff95ea390843ab6\tlink\n
     *         120000 blob 2499925193a48a84a546a2f7cd3ce7789d4e073ef1e7276fe682bfbb2b636cef\tlink.txt\n
     *         040000 tree 5b4e74befcb98e3050c511d02353d00565b2172be0a2bc5de833f011ad27f694\tsrc\n' | git mktree
     * </pre>
     */
    private static final byte[] MAIN_TREE_ID_SHA256 = hex("58e9a59940e4d2ae7e374b63fedf3b7bba8cfdc60308f64abd066db137300bcd");

    static Stream<Arguments> blobIdProvider() {
        return Stream.of(Arguments.of("DigestUtilsTest/hello.txt", "5f4a83288e67f1be2d6fcdad84165a86c6a970d7"),
                Arguments.of("DigestUtilsTest/greetings.txt", "6cf4f797455661e61d1ee6913fc29344f5897243"),
                Arguments.of("DigestUtilsTest/subdir/nested.txt", "07a392ddb4dbff06a373a7617939f30b2dcfe719"));
    }

    /** Decodes a compile-time hex literal; throws {@link AssertionError} on malformed input. */
    private static byte[] hex(final String hex) {
        try {
            return Hex.decodeHex(hex);
        } catch (final DecoderException e) {
            throw new AssertionError(e);
        }
    }

    private static Path resourcePath(final String resourceName) throws Exception {
        return Paths.get(GitIdentifiersTest.class.getClassLoader().getResource(resourceName).toURI());
    }

    static Stream<Arguments> virtualTreeProvider() {
        return Stream.of(
                Arguments.of(MessageDigestAlgorithms.SHA_1, HELLO_BLOB_ID_SHA1, LINK_BLOB_ID_SHA1, LINK_TXT_BLOB_ID_SHA1, RUN_BLOB_ID_SHA1,
                        SRC_TREE_ID_SHA1, MAIN_TREE_ID_SHA1),
                Arguments.of(MessageDigestAlgorithms.SHA_256, HELLO_BLOB_ID_SHA256, LINK_BLOB_ID_SHA256, LINK_TXT_BLOB_ID_SHA256, RUN_BLOB_ID_SHA256,
                        SRC_TREE_ID_SHA256, MAIN_TREE_ID_SHA256));
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
    @MethodSource("virtualTreeProvider")
    void testTreeIdBuilder(final String algorithm, final byte[] helloId, final byte[] linkId, final byte[] linkTxtId, final byte[] runId,
            final byte[] srcTreeId, final byte[] mainTreeId) throws Exception {
        final MessageDigest md = DigestUtils.getDigest(algorithm);

        // Verify individual blob IDs against pre-computed constants.
        assertArrayEquals(helloId, GitIdentifiers.blobId(md, HELLO_CONTENT));
        assertArrayEquals(linkId, GitIdentifiers.blobId(md, LINK_CONTENT.getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(linkTxtId, GitIdentifiers.blobId(md, LINK_TXT_CONTENT.getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(runId, GitIdentifiers.blobId(md, RUN_CONTENT));

        // Entries are supplied out of order to verify that the builder sorts them correctly.
        final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(md);
        builder.addSymbolicLink("link.txt", LINK_TXT_CONTENT);
        builder.addFile(GitIdentifiers.FileMode.REGULAR, "src/hello.txt", HELLO_CONTENT);
        builder.addSymbolicLink("link", LINK_CONTENT);
        builder.addFile(GitIdentifiers.FileMode.EXECUTABLE, "src/run.sh", RUN_CONTENT);

        // Check trees
        assertArrayEquals(mainTreeId, builder.get());
        assertArrayEquals(srcTreeId, builder.addDirectory("src").get());
    }

    @Test
    void testTreeIdBuilderAddFileInputStream() throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "Hello, World!\n".getBytes(StandardCharsets.UTF_8);

        final GitIdentifiers.TreeIdBuilder byteArrayBuilder = GitIdentifiers.treeIdBuilder(md);
        byteArrayBuilder.addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);
        final byte[] expected = byteArrayBuilder.get();

        final GitIdentifiers.TreeIdBuilder sizedStreamBuilder = GitIdentifiers.treeIdBuilder(md);
        sizedStreamBuilder.addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content.length, new ByteArrayInputStream(content));
        assertArrayEquals(expected, sizedStreamBuilder.get());
    }

    @Test
    void testTreeIdBuilderInvalidPathSegments() {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] data = {};
        // Sole path component
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addFile(GitIdentifiers.FileMode.REGULAR, "..", data));
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addDirectory(".."));
        // Embedded in a longer path
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addFile(GitIdentifiers.FileMode.REGULAR, "subdir/../file.txt", data));
        assertThrows(IllegalArgumentException.class,
                () -> GitIdentifiers.treeIdBuilder(md).addDirectory("subdir/.."));
    }

    @Test
    void testTreeIdBuilderNestedFileEquivalentToDirectoryAndFile() throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "hello\n".getBytes(StandardCharsets.UTF_8);

        final GitIdentifiers.TreeIdBuilder direct = GitIdentifiers.treeIdBuilder(md);
        direct.addFile(GitIdentifiers.FileMode.REGULAR, "nested/file.txt", content);

        final GitIdentifiers.TreeIdBuilder indirect = GitIdentifiers.treeIdBuilder(md);
        indirect.addDirectory("nested").addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);

        assertArrayEquals(direct.get(), indirect.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "."})
    void testTreeIdBuilderNoopPathSegments(final String segment) throws Exception {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final byte[] content = "hello\n".getBytes(StandardCharsets.UTF_8);

        // Canonical form
        final GitIdentifiers.TreeIdBuilder canonical = GitIdentifiers.treeIdBuilder(md);
        canonical.addFile(GitIdentifiers.FileMode.REGULAR, "subdir/file.txt", content);
        final byte[] expected = canonical.get();

        // Leading segment
        final GitIdentifiers.TreeIdBuilder withLeading = GitIdentifiers.treeIdBuilder(md);
        withLeading.addFile(GitIdentifiers.FileMode.REGULAR, segment + "/subdir/file.txt", content);
        assertArrayEquals(expected, withLeading.get());

        // Intermediate segment
        final GitIdentifiers.TreeIdBuilder withIntermediate = GitIdentifiers.treeIdBuilder(md);
        withIntermediate.addFile(GitIdentifiers.FileMode.REGULAR, "subdir/" + segment + "/file.txt", content);
        assertArrayEquals(expected, withIntermediate.get());

        // addDirectory with leading/trailing segments
        final GitIdentifiers.TreeIdBuilder viaDirectory = GitIdentifiers.treeIdBuilder(md);
        viaDirectory.addDirectory(segment + "/subdir/" + segment).addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", content);
        assertArrayEquals(expected, viaDirectory.get());
    }

    @Test
    void testTreeIdPath() throws Exception {
        assertArrayEquals(Hex.decodeHex("e4b21f6d78ceba6eb7c211ac15e3337ec4614e8a"),
                GitIdentifiers.treeId(DigestUtils.getSha1Digest(), resourcePath("DigestUtilsTest")));
    }

    @ParameterizedTest
    @MethodSource("virtualTreeProvider")
    void testTreeIdPathUnix(final String algorithm, final byte[] helloId, final byte[] linkId, final byte[] linkTxtId,
            final byte[] runId, final byte[] srcTreeId, final byte[] mainTreeId, final @TempDir Path tempDir) throws Exception {
        final MessageDigest md = DigestUtils.getDigest(algorithm);

        // Files
        final Path link = tempDir.resolve("link");
        final Path linkTxt = tempDir.resolve("link.txt");
        final Path src = tempDir.resolve("src");
        final Path hello = src.resolve("hello.txt");
        final Path run = src.resolve("run.sh");

        // Create the same structure as the virtual tree.
        try {
            Files.createSymbolicLink(link, Paths.get(LINK_CONTENT));
            Files.createSymbolicLink(linkTxt, Paths.get(LINK_TXT_CONTENT));
        } catch (final UnsupportedOperationException e) {
            Assumptions.abort("Symbolic links not supported on this filesystem");
        }
        Files.createDirectory(src);
        Files.write(hello, HELLO_CONTENT);
        Files.write(run, RUN_CONTENT);
        Files.setPosixFilePermissions(run, PosixFilePermissions.fromString("rwxr-xr-x"));

        // Verify individual blob IDs against pre-computed constants.
        assertArrayEquals(helloId, GitIdentifiers.blobId(md, hello));
        assertArrayEquals(linkId, GitIdentifiers.blobId(md, link));
        assertArrayEquals(linkTxtId, GitIdentifiers.blobId(md, linkTxt));
        assertArrayEquals(runId, GitIdentifiers.blobId(md, run));

        // Check trees
        assertArrayEquals(mainTreeId, GitIdentifiers.treeId(md, tempDir));
        assertArrayEquals(srcTreeId, GitIdentifiers.treeId(md, src));
    }
}
