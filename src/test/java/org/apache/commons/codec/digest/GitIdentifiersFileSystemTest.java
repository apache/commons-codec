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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests file modes independently of the host file system's permissions and execution access.
 */
class GitIdentifiersFileSystemTest {

    /**
     * A single-file tree exposing only the operations needed to compute its identifier. Proxies implement the small subset of the Path and attribute
     * interfaces used by the tests; unsupported operations fail explicitly. No files or permissions on the host are changed.
     */
    private static final class TestFileSystem extends FileSystem {

        private final TestFileSystemProvider provider;
        private boolean open = true;

        TestFileSystem(final String permissions, final boolean executable) {
            provider = new TestFileSystemProvider(this, permissions, executable);
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
            return true;
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

        TestFileSystemProvider(final TestFileSystem fileSystem, final String permissions, final boolean executable) {
            this.fileSystem = fileSystem;
            this.permissions = permissions;
            this.executable = executable;
            final Class<?> attributeType = permissions == null ? BasicFileAttributes.class : PosixFileAttributes.class;
            attributes = (BasicFileAttributes) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { attributeType },
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                        case "isDirectory":
                        case "isSymbolicLink":
                        case "isOther":
                            return false;
                        case "isRegularFile":
                            return true;
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
        public void delete(final Path path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
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
            return new ByteArrayInputStream(CONTENT);
        }

        @Override
        public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) {
            return type.cast(attributes);
        }

        @Override
        public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) {
            throw new UnsupportedOperationException();
        }
    }

    private static final byte[] CONTENT = { 'x' };

    private static void assertTreeId(final FileSystem fileSystem, final GitIdentifiers.FileMode mode) throws IOException {
        final MessageDigest md = DigestUtils.getSha1Digest();
        final GitIdentifiers.TreeIdBuilder builder = GitIdentifiers.treeIdBuilder(md);
        builder.addFile(mode, "file.txt", CONTENT);
        assertArrayEquals(builder.get(), GitIdentifiers.treeId(md, fileSystem.getPath("root")));
    }

    /**
     * Execution access without POSIX attributes must not turn an ordinary file into an executable Git entry.
     */
    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testTreeIdWithoutPosixPermissions(final boolean executable) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(null, executable)) {
            final Path file = fileSystem.getPath("file.txt");
            assertNull(Files.getFileAttributeView(file, PosixFileAttributeView.class));
            assertEquals(executable, Files.isExecutable(file));
            assertTreeId(fileSystem, GitIdentifiers.FileMode.REGULAR);
        }
    }

    /**
     * Only the owner-execute bit determines the Git mode, even when group permissions or an ACL give the current process different execution access.
     */
    @ParameterizedTest
    @CsvSource({
        "rw-------, false, REGULAR",
        "rw-------, true,  REGULAR",
        "rw-r-xr--, true,  REGULAR",
        "rw-r--r-x, true,  REGULAR",
        "rwx------, false, EXECUTABLE",
        "rwx------, true,  EXECUTABLE"
    })
    void testTreeIdWithPosixPermissions(final String permissions, final boolean executable, final GitIdentifiers.FileMode mode) throws IOException {
        try (TestFileSystem fileSystem = new TestFileSystem(permissions, executable)) {
            final Path file = fileSystem.getPath("file.txt");
            assertEquals(PosixFilePermissions.fromString(permissions), Files.getPosixFilePermissions(file));
            assertEquals(executable, Files.isExecutable(file));
            assertTreeId(fileSystem, mode);
        }
    }
}
