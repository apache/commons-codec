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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

/**
 * Tests filesystem capability assumptions, file modes, and symbolic links independently of the host filesystem.
 */
class GitIdentifiersFileSystemTest {

    /**
     * A single-entry tree with configurable attributes and symbolic-link operations. Proxies expose only the Path and attribute operations used by these
     * tests; unsupported operations fail explicitly. No files or permissions on the host are changed.
     */
    private static final class TestFileSystem extends FileSystem {

        private final TestFileSystemProvider provider;
        private boolean open = true;

        TestFileSystem(final String permissions, final boolean executable) {
            this(permissions, executable, null);
        }

        TestFileSystem(final String permissions, final boolean executable, final String linkTarget) {
            provider = new TestFileSystemProvider(this, permissions, executable, linkTarget);
        }

        @Override
        public void close() {
            open = false;
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return Collections.emptyList();
        }

        @Override
        public Path getPath(final String first, final String... more) {
            final Path name = Paths.get(first, more);
            return (Path) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Path.class }, (proxy, method, args) -> {
                switch (method.getName()) {
                case "getFileSystem":
                    return this;
                case "getFileName":
                    return name.getFileName();
                case "resolve":
                    return getPath(name.resolve(args[0].toString()).toString());
                case "toString":
                    return name.toString();
                default:
                    throw new UnsupportedOperationException(method.getName());
                }
            });
        }

        @Override
        public PathMatcher getPathMatcher(final String syntaxAndPattern) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return Collections.singleton(getPath("root"));
        }

        @Override
        public String getSeparator() {
            return "/";
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public WatchService newWatchService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileSystemProvider provider() {
            return provider;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return provider.permissions == null ? Collections.singleton("basic") : new HashSet<>(Arrays.asList("basic", "posix"));
        }
    }

    private static final class TestFileSystemProvider extends FileSystemProvider {

        private final TestFileSystem fileSystem;
        private final String permissions;
        private final boolean executable;
        private final BasicFileAttributes attributes;
        private final String linkTarget;
        private Exception linkCreationFailure;
        private Exception deletionFailure;
        private int linksCreated;
        private int linksDeleted;

        TestFileSystemProvider(final TestFileSystem fileSystem, final String permissions, final boolean executable, final String linkTarget) {
            this.fileSystem = fileSystem;
            this.permissions = permissions;
            this.executable = executable;
            this.linkTarget = linkTarget;
            final Class<?> attributeType = permissions == null ? BasicFileAttributes.class : PosixFileAttributes.class;
            attributes = (BasicFileAttributes) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { attributeType },
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                        case "isDirectory":
                        case "isOther":
                            return false;
                        case "isSymbolicLink":
                            return linkTarget != null;
                        case "isRegularFile":
                            return linkTarget == null;
                        case "permissions":
                            return PosixFilePermissions.fromString(permissions);
                        case "size":
                            return (long) CONTENT.length;
                        default:
                            throw new UnsupportedOperationException(method.getName());
                        }
                    });
        }

        @Override
        public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
            for (final AccessMode mode : modes) {
                if (mode == AccessMode.WRITE || mode == AccessMode.EXECUTE && !executable) {
                    throw new AccessDeniedException(path.toString());
                }
            }
        }

        @Override
        public void copy(final Path source, final Path target, final CopyOption... options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void createDirectory(final Path dir, final FileAttribute<?>... attrs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs) throws IOException {
            assertEquals("symlink-capability-probe", link.getFileName().toString());
            assertEquals("symlink-capability-target", target.toString());
            throwIfConfigured(linkCreationFailure);
            linksCreated++;
        }

        @Override
        public void delete(final Path path) throws IOException {
            assertEquals("symlink-capability-probe", path.getFileName().toString());
            throwIfConfigured(deletionFailure);
            linksDeleted++;
        }

        @Override
        public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
            if (linkTarget != null) {
                assertArrayEquals(new LinkOption[] { LinkOption.NOFOLLOW_LINKS }, options);
            }
            if (type != BasicFileAttributeView.class && (type != PosixFileAttributeView.class || permissions == null)) {
                return null;
            }
            return type.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> {
                if ("readAttributes".equals(method.getName())) {
                    return attributes;
                }
                throw new UnsupportedOperationException(method.getName());
            }));
        }

        @Override
        public FileStore getFileStore(final Path path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileSystem getFileSystem(final URI uri) {
            return fileSystem;
        }

        @Override
        public Path getPath(final URI uri) {
            return fileSystem.getPath(uri.getPath());
        }

        @Override
        public String getScheme() {
            return "git-identifiers-test";
        }

        @Override
        public boolean isHidden(final Path path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSameFile(final Path path, final Path path2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void move(final Path source, final Path target, final CopyOption... options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
            assertEquals("root", dir.toString(), "A symbolic link must not be traversed as a directory");
            final Path file = fileSystem.getPath("file.txt");
            final Set<Path> entries = filter.accept(file) ? Collections.singleton(file) : Collections.emptySet();
            return new DirectoryStream<Path>() {
                @Override
                public void close() {
                    // No resources to release.
                }

                @Override
                public Iterator<Path> iterator() {
                    return entries.iterator();
                }
            };
        }

        @Override
        public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream newInputStream(final Path path, final OpenOption... options) {
            assertNull(linkTarget, "Symbolic link contents must not be read through the target");
            return new ByteArrayInputStream(CONTENT);
        }

        @Override
        public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) {
            if (linkTarget != null) {
                assertArrayEquals(new LinkOption[] { LinkOption.NOFOLLOW_LINKS }, options);
            }
            return type.cast(attributes);
        }

        @Override
        public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path readSymbolicLink(final Path link) {
            assertTrue(linkTarget != null, "This fixture must represent a symbolic link");
            // Preserve the provider's exact target text independently of the host's path syntax.
            return (Path) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Path.class }, (proxy, method, args) -> {
                if ("toString".equals(method.getName())) {
                    return linkTarget;
                }
                throw new UnsupportedOperationException(method.getName());
            });
        }

        @Override
        public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) {
            throw new UnsupportedOperationException();
        }
    }

    private static final byte[] CONTENT = { 'x' };

    private static void assertTreeId(final FileSystem fileSystem, final String algorithm, final GitIdentifiers.FileMode mode) throws IOException {
        final MessageDigest md = DigestUtils.getDigest(algorithm);
        final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(md);
        builder.addFile(mode, "file.txt", CONTENT);
        assertArrayEquals(builder.get(), GitIdentifiers.treeId(md, fileSystem.getPath("root")));
    }

    static Stream<Arguments> basicFileModes() {
        return Stream.of(MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256)
                .flatMap(algorithm -> Stream.of(false, true).map(executable -> Arguments.of(algorithm, executable)));
    }

    static Stream<Arguments> posixFileModes() {
        return Stream.of(MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256).flatMap(algorithm -> IntStream.range(0, 512).boxed()
                .flatMap(mask -> Stream.of(false, true).map(executable -> {
                    final char[] permissions = "---------".toCharArray();
                    for (int i = 0; i < permissions.length; i++) {
                        if ((mask & (1 << (8 - i))) != 0) {
                            permissions[i] = "rwxrwxrwx".charAt(i);
                        }
                    }
                    final GitIdentifiers.FileMode mode = (mask & 0100) == 0 ? GitIdentifiers.FileMode.REGULAR : GitIdentifiers.FileMode.EXECUTABLE;
                    return Arguments.of(algorithm, new String(permissions), executable, mode);
                })));
    }

    static Stream<Arguments> symbolicLinks() {
        return Stream.of(MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256).flatMap(algorithm ->
                Stream.of(null, "rw-------", "rwxrwxrwx").flatMap(permissions -> Stream.of(false, true).flatMap(executable ->
                        Stream.of("file.txt", "subdir/file.txt", "subdir\\file.txt", "../missing.txt", "/target/file", "C:\\target\\file",
                                "directory", "dir/\u03bb.txt").map(target -> Arguments.of(algorithm, permissions, executable, target)))));
    }

    private static void throwIfConfigured(final Exception failure) throws IOException {
        if (failure instanceof IOException) {
            throw (IOException) failure;
        }
        if (failure != null) {
            throw (RuntimeException) failure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256 })
    void testNativeBlobTestSkipsWithoutSymlinkPrivileges(final String algorithm) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(null, false)) {
            fileSystem.provider.linkCreationFailure = new AccessDeniedException("probe");
            assertThrows(TestAbortedException.class,
                    () -> new GitIdentifiersTest().testBlobIdSymlink(algorithm, "subdir/file.txt", false, fileSystem.getPath("root")));
            assertEquals(0, fileSystem.provider.linksCreated);
        }
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.codec.digest.GitIdentifiersTest#virtualTreeProvider")
    void testNativeUnixTreeSkipsWithoutPosix(final String algorithm, final byte[] helloId, final byte[] linkId, final byte[] linkTxtId,
            final byte[] runId, final byte[] srcTreeId, final byte[] mainTreeId) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(null, false)) {
            assertThrows(TestAbortedException.class, () -> new GitIdentifiersTest().testTreeIdPathUnix(algorithm, helloId, linkId, linkTxtId,
                    runId, srcTreeId, mainTreeId, fileSystem.getPath("root")));
            assertEquals(0, fileSystem.provider.linksCreated, "The POSIX requirement must be checked before creating symbolic links");
        }
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.codec.digest.GitIdentifiersTest#virtualTreeProvider")
    void testNativeUnixTreeSkipsWithoutSymlinkPrivileges(final String algorithm, final byte[] helloId, final byte[] linkId, final byte[] linkTxtId,
            final byte[] runId, final byte[] srcTreeId, final byte[] mainTreeId) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem("rw-------", false)) {
            fileSystem.provider.linkCreationFailure = new FileSystemException("probe", "target", "A required privilege is not held");
            assertThrows(TestAbortedException.class, () -> new GitIdentifiersTest().testTreeIdPathUnix(algorithm, helloId, linkId, linkTxtId,
                    runId, srcTreeId, mainTreeId, fileSystem.getPath("root")));
            assertEquals(0, fileSystem.provider.linksCreated);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testPosixCapabilityAssumption(final boolean posix) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(posix ? "rw-------" : null, false)) {
            final Path directory = fileSystem.getPath("root");
            if (posix) {
                assertDoesNotThrow(() -> GitIdentifiersTest.assumePosixAttributes(directory));
            } else {
                assertThrows(TestAbortedException.class, () -> GitIdentifiersTest.assumePosixAttributes(directory));
            }
            assertEquals(0, fileSystem.provider.linksCreated);
        }
    }

    @ParameterizedTest(name = "{0}, permissions={1}, access={2}, target={3}")
    @MethodSource("symbolicLinks")
    void testSymbolicLinksWithoutHostSupport(final String algorithm, final String permissions, final boolean executable, final String target)
            throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(permissions, executable, target)) {
            final MessageDigest md = DigestUtils.getDigest(algorithm);
            final Path link = fileSystem.getPath("file.txt");
            final byte[] expectedBlob = GitIdentifiers.blobId(md, target.getBytes(StandardCharsets.UTF_8));
            assertArrayEquals(expectedBlob, GitIdentifiers.blobId(md, link));
            final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(md);
            builder.addSymbolicLink("file.txt", target);
            assertArrayEquals(builder.get(), GitIdentifiers.treeId(md, fileSystem.getPath("root")));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSymlinkProbeDoesNotHideCleanupFailures(final boolean posix) throws IOException {
        for (final Exception failure : new Exception[] { new IOException("cleanup failed"), new AccessDeniedException("probe"),
                new UnsupportedOperationException("cleanup unsupported"), new IllegalStateException("unexpected cleanup failure") }) {
            try (TestFileSystem fileSystem = new TestFileSystem(posix ? "rw-------" : null, false)) {
                fileSystem.provider.deletionFailure = failure;
                assertSame(failure, assertThrows(failure.getClass(), () -> GitIdentifiersTest.assumeSymbolicLinks(fileSystem.getPath("root"))));
                assertEquals(1, fileSystem.provider.linksCreated);
                assertEquals(0, fileSystem.provider.linksDeleted);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSymlinkProbeDoesNotHideInvalidFixtures(final boolean posix) throws IOException {
        for (final Exception failure : new Exception[] { new FileAlreadyExistsException("probe"), new NoSuchFileException("directory"),
                new NotDirectoryException("directory"), new IllegalArgumentException("invalid target"), new IllegalStateException("broken provider") }) {
            try (TestFileSystem fileSystem = new TestFileSystem(posix ? "rw-------" : null, false)) {
                fileSystem.provider.linkCreationFailure = failure;
                assertSame(failure, assertThrows(failure.getClass(), () -> GitIdentifiersTest.assumeSymbolicLinks(fileSystem.getPath("root"))));
                assertEquals(0, fileSystem.provider.linksCreated);
                assertEquals(0, fileSystem.provider.linksDeleted);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSymlinkProbeSkipsUnavailableCapabilities(final boolean posix) throws IOException {
        for (final Exception failure : new Exception[] { new UnsupportedOperationException("links unsupported"), new IOException("links unavailable"),
                new AccessDeniedException("probe"), new FileSystemException("probe", "target", "A required privilege is not held"),
                new SecurityException("link permission denied") }) {
            try (TestFileSystem fileSystem = new TestFileSystem(posix ? "rw-------" : null, false)) {
                fileSystem.provider.linkCreationFailure = failure;
                final TestAbortedException aborted = assertThrows(TestAbortedException.class,
                        () -> GitIdentifiersTest.assumeSymbolicLinks(fileSystem.getPath("root")));
                assertTrue(aborted.getMessage().contains(failure.toString()));
                assertEquals(0, fileSystem.provider.linksCreated);
                assertEquals(0, fileSystem.provider.linksDeleted);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSymlinkProbeSucceedsAndCleansUp(final boolean posix) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(posix ? "rw-------" : null, false)) {
            GitIdentifiersTest.assumeSymbolicLinks(fileSystem.getPath("root"));
            assertEquals(1, fileSystem.provider.linksCreated);
            assertEquals(1, fileSystem.provider.linksDeleted);
        }
    }

    /**
     * Execution access without POSIX attributes must not turn an ordinary file into an executable Git entry.
     */
    @ParameterizedTest(name = "{0}, access={1}")
    @MethodSource("basicFileModes")
    void testTreeIdWithoutPosixPermissions(final String algorithm, final boolean executable) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(null, executable)) {
            final Path file = fileSystem.getPath("file.txt");
            assertNull(Files.getFileAttributeView(file, PosixFileAttributeView.class));
            assertEquals(executable, Files.isExecutable(file));
            assertTreeId(fileSystem, algorithm, GitIdentifiers.FileMode.REGULAR);
        }
    }

    /**
     * Checks all 512 POSIX permission combinations independently of process execution access, using both Git hash algorithms.
     */
    @ParameterizedTest(name = "{0}, permissions={1}, access={2}, mode={3}")
    @MethodSource("posixFileModes")
    void testTreeIdWithPosixPermissions(final String algorithm, final String permissions, final boolean executable, final GitIdentifiers.FileMode mode)
            throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(permissions, executable)) {
            final Path file = fileSystem.getPath("file.txt");
            assertEquals(PosixFilePermissions.fromString(permissions), Files.getPosixFilePermissions(file));
            assertEquals(executable, Files.isExecutable(file));
            assertTreeId(fileSystem, algorithm, mode);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { MessageDigestAlgorithms.SHA_1, MessageDigestAlgorithms.SHA_256 })
    void testZipFileSystemKeepsRegularFileCoverage(final String algorithm, @TempDir final Path tempDir) throws IOException {
        Assumptions.assumeTrue(FileSystemProvider.installedProviders().stream().anyMatch(provider -> "jar".equals(provider.getScheme())),
                "ZIP filesystem provider is unavailable");
        final URI uri = URI.create("jar:" + tempDir.resolve("tree.zip").toUri());
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.singletonMap("create", "true"))) {
            final Path root = fileSystem.getPath("/");
            Files.write(root.resolve("file.txt"), CONTENT);
            assertThrows(TestAbortedException.class, () -> GitIdentifiersTest.assumePosixAttributes(root));
            assertThrows(TestAbortedException.class, () -> GitIdentifiersTest.assumeSymbolicLinks(root));
            final MessageDigest digest = DigestUtils.getDigest(algorithm);
            final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(digest);
            builder.addFile(GitIdentifiers.FileMode.REGULAR, "file.txt", CONTENT);
            assertArrayEquals(builder.get(), GitIdentifiers.treeId(digest, root));
        }
    }
}
