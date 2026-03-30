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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a single entry in a Git tree object.
 *
 * <p>A Git tree object encodes a directory snapshot. Each entry holds:</p>
 * <ul>
 *   <li>a {@link Type} that determines the Unix file mode (e.g. {@code 100644} for a regular file),</li>
 *   <li>the entry name (file or directory name, without a path separator),</li>
 *   <li>the raw object id of the referenced blob or sub-tree.</li>
 * </ul>
 *
 * <p>Entries are ordered by {@link #compareTo} using Git's tree-sort rule: directory names are compared as if they ended with {@code '/'}, so that {@code foo/}
 * sorts after {@code foobar}.</p>
 *
 * <p>Call {@link #toTreeEntryBytes()} to obtain the binary encoding that Git feeds to its hash function when computing the tree object identifier.</p>
 *
 * @see <a href="https://git-scm.com/book/en/v2/Git-Internals-Git-Objects">Git Internals – Git Objects</a>
 * @see <a href="https://www.swhid.org/swhid-specification/v1.2/5.Core_identifiers/#53-directories">SWHID Directory Identifier</a>
 */
class GitDirectoryEntry implements Comparable<GitDirectoryEntry> {

    /**
     * The type of a Git tree entry, which maps to a Unix file-mode string.
     *
     * <p>Git encodes the file type and permission bits as an ASCII octal string that precedes the entry name in the binary tree format. The values defined here
     * cover the four entry types that Git itself produces.</p>
     *
     * <p>This enum is package-private. If it were made public, {@link #mode} would need to be wrapped in an immutable copy to prevent external mutation.</p>
     */
    enum Type {

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
         * The ASCII-encoded octal mode string as it appears in the binary tree entry.
         */
        private final byte[] mode;

        Type(final String mode) {
            this.mode = mode.getBytes(StandardCharsets.US_ASCII);
        }
    }

    private static String getFileName(final Path path) {
        final Path fileName = path.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException(path.toString());
        }
        return fileName.toString();
    }

    /**
     * The entry name (file or directory name, no path separator).
     */
    private final String name;

    /**
     * The key used for ordering entries within a tree object.
     *
     * <p>>Git appends {@code '/'} to directory names before comparing.</p>
     */
    private final String sortKey;

    /**
     * The Git object type, which determines the Unix file-mode prefix.
     */
    private final Type type;

    /**
     * The raw object id of the referenced blob or sub-tree.
     */
    private final byte[] rawObjectId;

    /**
     * Creates an entry.
     *
     * @param path The path of the entry; must not be an empty path.
     * @param type The type of the entry.
     * @param rawObjectId The id of the entry.
     * @throws IllegalArgumentException If the path is empty.
     * @throws NullPointerException If any argument is {@code null}.
     */
    GitDirectoryEntry(final Path path, final Type type, final byte[] rawObjectId) {
        this(getFileName(path), type, rawObjectId);
    }

    /**
     * Creates an entry.
     *
     * @param name The name of the entry
     * @param type The type of the entry
     * @param rawObjectId The id of the entry
     */
    private GitDirectoryEntry(final String name, final Type type, final byte[] rawObjectId) {
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.sortKey = type == Type.DIRECTORY ? name + "/" : name;
        this.rawObjectId = Objects.requireNonNull(rawObjectId);
    }

    @Override
    public int compareTo(final GitDirectoryEntry o) {
        return sortKey.compareTo(o.sortKey);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GitDirectoryEntry)) {
            return false;
        }
        final GitDirectoryEntry other = (GitDirectoryEntry) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns the binary encoding of this entry as it appears inside a Git tree object.
     *
     * <p>The format follows the Git tree entry layout:</p>
     * <pre>
     *   &lt;mode&gt; SP &lt;name&gt; NUL &lt;20-byte-object-id&gt;
     * </pre>
     *
     * @return the binary tree-entry encoding; never {@code null}.
     */
    byte[] toTreeEntryBytes() {
        final byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        final byte[] result = new byte[type.mode.length + nameBytes.length + rawObjectId.length + 2];
        System.arraycopy(type.mode, 0, result, 0, type.mode.length);
        result[type.mode.length] = ' ';
        System.arraycopy(nameBytes, 0, result, type.mode.length + 1, nameBytes.length);
        result[type.mode.length + nameBytes.length + 1] = '\0';
        System.arraycopy(rawObjectId, 0, result, type.mode.length + nameBytes.length + 2, rawObjectId.length);
        return result;
    }
}
