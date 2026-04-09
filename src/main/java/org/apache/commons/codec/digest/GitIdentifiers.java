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
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    private static GitDirectoryEntry.Type getGitDirectoryEntryType(final Path path) {
        // Symbolic links first
        if (Files.isSymbolicLink(path)) {
            return GitDirectoryEntry.Type.SYMBOLIC_LINK;
        }
        if (Files.isDirectory(path)) {
            return GitDirectoryEntry.Type.DIRECTORY;
        }
        if (Files.isExecutable(path)) {
            return GitDirectoryEntry.Type.EXECUTABLE;
        }
        return GitDirectoryEntry.Type.REGULAR;
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
        DigestUtils.updateDigest(messageDigest, gitBlobPrefix(data.length));
        return DigestUtils.digest(messageDigest, data);
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
     * @since 1.22.0
     */
    public static byte[] blobId(final MessageDigest messageDigest, final Path data) throws IOException {
        messageDigest.reset();
        if (Files.isSymbolicLink(data)) {
            final byte[] linkTarget = Files.readSymbolicLink(data).toString().getBytes(StandardCharsets.UTF_8);
            DigestUtils.updateDigest(messageDigest, gitBlobPrefix(linkTarget.length));
            return DigestUtils.digest(messageDigest, linkTarget);
        }
        DigestUtils.updateDigest(messageDigest, gitBlobPrefix(Files.size(data)));
        return DigestUtils.updateDigest(messageDigest, data).digest();
    }

    private static byte[] gitBlobPrefix(final long dataSize) {
        return gitPrefix("blob ", dataSize);
    }

    private static byte[] gitPrefix(final String prefix, final long dataSize) {
        return (prefix + dataSize + "\0").getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns a generalized Git tree identifier for a collection of directory entries.
     *
     * <p>The identifier is computed in the way described by the
     * <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#53-directories">SWHID directory identifier</a>, but it can use any hash
     * algorithm.</p>
     *
     * <p>When the hash algorithm is SHA-1, the identifier is identical to Git tree identifier and SWHID directory identifier.</p>
     *
     * @param messageDigest The MessageDigest to use (for example SHA-1).
     * @param entries       The directory entries.
     * @return A generalized Git tree identifier.
     */
    static byte[] treeId(final MessageDigest messageDigest, final Collection<GitDirectoryEntry> entries) {
        final TreeSet<GitDirectoryEntry> treeSet = new TreeSet<>(entries);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (final GitDirectoryEntry entry : treeSet) {
            final byte[] treeEntryBytes = entry.toTreeEntryBytes();
            baos.write(treeEntryBytes, 0, treeEntryBytes.length);
        }
        messageDigest.reset();
        DigestUtils.updateDigest(messageDigest, gitTreePrefix(baos.size()));
        return DigestUtils.updateDigest(messageDigest, baos.toByteArray()).digest();
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
     * @since 1.22.0
     */
    public static byte[] treeId(final MessageDigest messageDigest, final Path data) throws IOException {
        final List<GitDirectoryEntry> entries = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(data)) {
            for (final Path path : files) {
                final GitDirectoryEntry.Type type = getGitDirectoryEntryType(path);
                final byte[] rawObjectId;
                if (type == GitDirectoryEntry.Type.DIRECTORY) {
                    rawObjectId = treeId(messageDigest, path);
                } else {
                    rawObjectId = blobId(messageDigest, path);
                }
                entries.add(new GitDirectoryEntry(path, type, rawObjectId));
            }
        }
        return treeId(messageDigest, entries);
    }

    private static byte[] gitTreePrefix(final long dataSize) {
        return gitPrefix("tree ", dataSize);
    }

    private GitIdentifiers() {
        // utility class
    }
}
