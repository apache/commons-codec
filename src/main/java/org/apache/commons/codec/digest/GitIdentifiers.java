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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Operations for computing Git object identifiers and their generalizations described by the
 * <a href="https://www.swhid.org/swhid-specification/">SWHID specification</a>.
 *
 * <p>When the hash algorithm is SHA-1, the identifiers produced by this class are identical to those used by Git.
 * Other hash algorithms produce generalized identifiers as described by the SWHID specification.</p>
 *
 * <p>This class is immutable and thread-safe. However, the {@link MessageDigest} instances passed to it generally won't be.</p>
 *
 * @see <a href="https://git-scm.com/book/en/v2/Git-Internals-Git-Objects">Git Internals – Git Objects</a>
 * @see <a href="https://www.swhid.org/swhid-specification/">SWHID Specification</a>
 * @since 1.22.0
 */
public class GitIdentifiers {

    /**
     * The type of a Git tree entry, which maps to a Unix file-mode string.
     *
     * <p>Git encodes the file type and permission bits as an ASCII octal string that precedes the entry name in the binary tree format. The values defined here
     * cover the four entry types that Git itself produces.</p>
     */
    public enum FileMode {

        /**
         * A sub-directory (Git sub-tree).
         */
        DIRECTORY("40000"),

        /**
         * An executable file.
         */
        EXECUTABLE("100755"),

        /**
         * A regular (non-executable) file.
         */
        REGULAR("100644"),

        /**
         * A symbolic link.
         */
        SYMBOLIC_LINK("120000");

        /**
         * The octal mode as used by Git.
         */
        private final String mode;

        /**
         * Serialized {@code mode}: since this is mutable, it must remain private.
         */
        private final byte[] modeBytes;

        FileMode(final String mode) {
            this.mode = mode;
            this.modeBytes = mode.getBytes(StandardCharsets.US_ASCII);
        }

        /**
         * Gets the octal mode as used by Git.
         *
         * @return The octal mode
         */
        public String getMode() {
            return mode;
        }
    }

    /**
     * Represents a single entry in a Git tree object.
     *
     * <p>A Git tree object encodes a directory snapshot. Each entry holds:</p>
     * <ul>
     *   <li>a {@link FileMode} that determines the Unix file mode (e.g. {@code 100644} for a regular file),</li>
     *   <li>the entry name (file or directory name, without a path separator),</li>
     *   <li>the raw object id of the referenced blob or sub-tree.</li>
     * </ul>
     *
     * <p>Entries are ordered by {@link #compareTo} using Git's tree-sort rule: directory names are compared as if they ended with {@code '/'}, so that {@code foo/}
     * sorts after {@code foobar}.</p>
     *
     * @see <a href="https://git-scm.com/book/en/v2/Git-Internals-Git-Objects">Git Internals – Git Objects</a>
     * @see <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#53-directories">SWHID Directory Identifier</a>
     */
    static class DirectoryEntry implements Comparable<DirectoryEntry> {

        /**
         * The entry name (file or directory name, no path separator).
         */
        private final String name;
        /**
         * The raw object id of the referenced blob or sub-tree.
         */
        private final byte[] rawObjectId;
        /**
         * The key used for ordering entries within a tree object.
         *
         * <p>>Git appends {@code '/'} to directory names before comparing.</p>
         */
        private final String sortKey;
        /**
         * The Git object type, which determines the Unix file-mode prefix.
         */
        private final FileMode type;

        /**
         * Creates an entry.
         *
         * @param name The name of the entry
         * @param type The type of the entry
         * @param rawObjectId The id of the entry
         */
        DirectoryEntry(final String name, final FileMode type, final byte[] rawObjectId) {
            if (Objects.requireNonNull(name).indexOf('/') >= 0) {
                throw new IllegalArgumentException("Entry name must not contain '/': " + name);
            }
            this.name = name;
            this.type = Objects.requireNonNull(type);
            this.sortKey = type == FileMode.DIRECTORY ? name + "/" : name;
            this.rawObjectId = Objects.requireNonNull(rawObjectId);
        }

        @Override
        public int compareTo(final DirectoryEntry o) {
            return sortKey.compareTo(o.sortKey);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof DirectoryEntry)) {
                return false;
            }
            final DirectoryEntry other = (DirectoryEntry) obj;
            return name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

    }

    /**
     * Builds a Git tree identifier for a virtual directory structure, such as the contents of
     * an archive.
     */
    public static final class TreeIdBuilder {

        /**
         * A supplier of a blob identifier that may throw {@link IOException}.
         */
        @FunctionalInterface
        private interface BlobIdSupplier {
            byte[] get() throws IOException;
        }

        private static void checkPathComponent(String name) {
            if (".".equals(name) || "..".equals(name)) {
                throw new IllegalArgumentException("Path component not allowed: " + name);
            }
        }
        private final Map<String, TreeIdBuilder> dirEntries = new HashMap<>();
        private final Map<String, DirectoryEntry> fileEntries = new HashMap<>();
        private final MessageDigest messageDigest;

        private TreeIdBuilder(final MessageDigest messageDigest) {
            this.messageDigest = Objects.requireNonNull(messageDigest);
        }

        /**
         * Returns the {@link TreeIdBuilder} for the named subdirectory, creating it if absent.
         *
         * @param name The relative path of the subdirectory in normalized form (may contain {@code '/'}).
         * @return The {@link TreeIdBuilder} for the subdirectory.
         * @throws IllegalArgumentException If any path component is {@code "."} or {@code ".."}.
         */
        public TreeIdBuilder addDirectory(final String name) {
            TreeIdBuilder current = this;
            for (final String component : name.split("/", -1)) {
                if (component.isEmpty()) {
                    continue;
                }
                checkPathComponent(component);
                current = current.dirEntries.computeIfAbsent(component, k -> new TreeIdBuilder(messageDigest));
            }
            return current;
        }

        /**
         * Adds a file entry at the given path within this tree.
         *
         * <p>If {@code name} contains {@code '/'}, intermediate subdirectories are created automatically.</p>
         *
         * <p>The stream is eagerly drained.</p>
         *
         * <p>If the size of the stream is known in advance, consider using {@link TreeIdBuilder#addFile(FileMode, String, long, InputStream)} instead.</p>
         *
         * @param mode The file mode (e.g. {@link FileMode#REGULAR}).
         * @param name The relative path of the entry in normalized form(may contain {@code '/'}).
         * @param data The file content.
         * @throws IOException If the stream cannot be read.
         * @throws IllegalArgumentException If any path component is {@code "."} or {@code ".."}.
         */
        public void addFile(final FileMode mode, final String name, final InputStream data) throws IOException {
            addFile(mode, name, () -> blobId(messageDigest, readAllBytes(data)));
        }

        /**
         * Adds a file entry at the given path within this tree, streaming content without buffering.
         *
         * <p>If {@code name} contains {@code '/'}, intermediate subdirectories are created automatically.</p>
         *
         * <p>The stream is eagerly drained.</p>
         *
         * @param mode     The file mode (e.g. {@link FileMode#REGULAR}).
         * @param name The relative path of the entry in normalized form(may contain {@code '/'}).
         * @param dataSize The exact number of bytes in {@code data}.
         * @param data     The file content.
         * @throws IOException If the stream cannot be read.
         * @throws IllegalArgumentException If any path component is {@code "."} or {@code ".."}.
         */
        public void addFile(final FileMode mode, final String name, final long dataSize, final InputStream data) throws IOException {
            addFile(mode, name, () -> blobId(messageDigest, dataSize, data));
        }

        private void addFile(final FileMode mode, final String name, final BlobIdSupplier blobId) throws IOException {
            final int slash = name.indexOf('/');
            if (slash < 0) {
                checkPathComponent(name);
                fileEntries.put(name, new DirectoryEntry(name, mode, blobId.get()));
            } else {
                addDirectory(name.substring(0, slash)).addFile(mode, name.substring(slash + 1), blobId);
            }
        }

        /**
         * Adds a file entry at the given path within this tree.
         *
         * <p>If {@code name} contains {@code '/'}, intermediate subdirectories are created automatically.</p>
         *
         * @param mode The file mode (e.g. {@link FileMode#REGULAR}).
         * @param name The relative path of the entry in normalized form(may contain {@code '/'}).
         * @param data The file content.
         * @throws IOException If an I/O error occurs.
         * @throws IllegalArgumentException If any path component is {@code "."} or {@code ".."}.
         */
        public void addFile(final FileMode mode, final String name, final byte[] data) throws IOException {
            addFile(mode, name, () -> blobId(messageDigest, data));
        }

        /**
         * Computes the Git tree identifier for this directory and all its descendants.
         *
         * @return The raw tree identifier bytes.
         * @throws IOException If a digest operation fails.
         */
        public byte[] build() throws IOException {
            final Set<DirectoryEntry> entries = new TreeSet<>(fileEntries.values());
            for (final Map.Entry<String, TreeIdBuilder> e : dirEntries.entrySet()) {
                entries.add(new DirectoryEntry(e.getKey(), FileMode.DIRECTORY, e.getValue().build()));
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (final DirectoryEntry entry : entries) {
                baos.write(entry.type.modeBytes);
                baos.write(' ');
                baos.write(entry.name.getBytes(StandardCharsets.UTF_8));
                baos.write('\0');
                baos.write(entry.rawObjectId);
            }
            messageDigest.reset();
            DigestUtils.updateDigest(messageDigest, getGitTreePrefix(baos.size()));
            return DigestUtils.updateDigest(messageDigest, baos.toByteArray()).digest();
        }
    }

    /**
     * Reads through a byte array and returns a generalized Git blob identifier.
     *
     * <p>The identifier is computed in the way described by the
     * <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#52-contents">SWHID contents identifier</a>, but it can use any hash
     * algorithm.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git blob identifier and SWHID contents identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param data          Data to digest.
     * @return A generalized Git blob identifier.
     */
    public static byte[] blobId(final MessageDigest messageDigest, final byte[] data) {
        messageDigest.reset();
        DigestUtils.updateDigest(messageDigest, getGitBlobPrefix(data.length));
        return DigestUtils.digest(messageDigest, data);
    }

    /**
     * Reads through a stream and returns a generalized Git blob identifier.
     *
     * <p>The stream is drained and its contents are buffered to determine the size before hashing. To avoid
     * buffering, use {@link #blobId(MessageDigest, long, InputStream)} when the size is known in advance.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git blob identifier and SWHID contents identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param data          Stream to digest.
     * @return A generalized Git blob identifier.
     * @throws IOException On error reading the stream.
     */
    public static byte[] blobId(final MessageDigest messageDigest, final InputStream data) throws IOException {
        return blobId(messageDigest, readAllBytes(data));
    }

    /**
     * Reads through a stream of known size and returns a generalized Git blob identifier, without buffering.
     *
     * <p>When the size of the content is known in advance, this overload streams {@code data} directly through
     * the digest without buffering the full content in memory.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git blob identifier and SWHID contents identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param dataSize      The exact number of bytes in {@code data}.
     * @param data          Stream to digest.
     * @return A generalized Git blob identifier.
     * @throws IOException On error reading the stream.
     */
    public static byte[] blobId(final MessageDigest messageDigest, final long dataSize, final InputStream data) throws IOException {
        messageDigest.reset();
        DigestUtils.updateDigest(messageDigest, getGitBlobPrefix(dataSize));
        return DigestUtils.updateDigest(messageDigest, data).digest();
    }

    /**
     * Reads through a file and returns a generalized Git blob identifier.
     *
     * <p>The identifier is computed in the way described by the
     * <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#52-contents">SWHID contents identifier</a>, but it can use any hash
     * algorithm.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git blob identifier and SWHID contents identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param data          Path to the file to digest.
     * @return A generalized Git blob identifier.
     * @throws IOException On error accessing the file.
     */
    public static byte[] blobId(final MessageDigest messageDigest, final Path data) throws IOException {
        messageDigest.reset();
        if (Files.isSymbolicLink(data)) {
            final byte[] linkTarget = Files.readSymbolicLink(data).toString().getBytes(StandardCharsets.UTF_8);
            DigestUtils.updateDigest(messageDigest, getGitBlobPrefix(linkTarget.length));
            return DigestUtils.digest(messageDigest, linkTarget);
        }
        DigestUtils.updateDigest(messageDigest, getGitBlobPrefix(Files.size(data)));
        return DigestUtils.updateDigest(messageDigest, data).digest();
    }

    private static FileMode getGitDirectoryEntryType(final Path path) {
        // Symbolic links first
        if (Files.isSymbolicLink(path)) {
            return FileMode.SYMBOLIC_LINK;
        }
        if (Files.isDirectory(path)) {
            return FileMode.DIRECTORY;
        }
        if (Files.isExecutable(path)) {
            return FileMode.EXECUTABLE;
        }
        return FileMode.REGULAR;
    }

    private static byte[] getGitBlobPrefix(final long dataSize) {
        return getGitPrefix("blob", dataSize);
    }

    private static byte[] getGitPrefix(final String type, final long dataSize) {
        return (type + " " + dataSize + "\0").getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] getGitTreePrefix(final long dataSize) {
        return getGitPrefix("tree", dataSize);
    }

    private static void populateFromPath(final TreeIdBuilder builder, final Path directory) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory)) {
            for (final Path path : files) {
                final String name = Objects.toString(path.getFileName());
                final FileMode mode = getGitDirectoryEntryType(path);
                switch (mode) {
                    case DIRECTORY:
                        populateFromPath(builder.addDirectory(name), path);
                        break;
                    case SYMBOLIC_LINK:
                        final byte[] linkTarget = Files.readSymbolicLink(path).toString().getBytes(StandardCharsets.UTF_8);
                        builder.addFile(FileMode.SYMBOLIC_LINK, name, linkTarget);
                        break;
                    default:
                        try (InputStream is = Files.newInputStream(path)) {
                            builder.addFile(mode, name, Files.size(path), is);
                        }
                        break;
                }
            }
        }
    }

    private static byte[] readAllBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buf = new byte[DigestUtils.BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * Reads through a directory and returns a generalized Git tree identifier.
     *
     * <p>The identifier is computed in the way described by the
     * <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#53-directories">SWHID directory identifier</a>, but it can use any hash
     * algorithm.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git tree identifier and SWHID directory identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param data          Path to the directory to digest.
     * @return A generalized Git tree identifier.
     * @throws IOException On error accessing the directory or its contents.
     */
    public static byte[] treeId(final MessageDigest messageDigest, final Path data) throws IOException {
        final TreeIdBuilder builder = treeIdBuilder(messageDigest);
        populateFromPath(builder, data);
        return builder.build();
    }

    /**
     * Returns a new {@link TreeIdBuilder} for constructing a generalized Git tree identifier from a virtual directory
     * structure, such as the contents of an archive.
     *
     * <p>The identifier is computed in the way described by the
     * <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#53-directories">SWHID directory identifier</a>, but it can use any hash
     * algorithm.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git tree identifier and SWHID directory identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @return A new {@link TreeIdBuilder}.
     */
    public static TreeIdBuilder treeIdBuilder(final MessageDigest messageDigest) {
        return new TreeIdBuilder(messageDigest);
    }

    private GitIdentifiers() {
        // utility class
    }
}
