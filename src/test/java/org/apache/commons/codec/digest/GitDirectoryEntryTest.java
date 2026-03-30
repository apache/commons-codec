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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class GitDirectoryEntryTest {

    private static final byte[] ZERO_ID = new byte[20];

    @Test
    void testConstructor() {
        assertThrows(NullPointerException.class, () -> new GitDirectoryEntry(null, GitDirectoryEntry.Type.REGULAR, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new GitDirectoryEntry(Paths.get("hello.txt"), null, ZERO_ID));
        assertThrows(NullPointerException.class, () -> new GitDirectoryEntry(Paths.get("hello.txt"), GitDirectoryEntry.Type.REGULAR, null));
        assertThrows(IllegalArgumentException.class, () -> new GitDirectoryEntry(Paths.get("/"), GitDirectoryEntry.Type.REGULAR, ZERO_ID));
    }

    /**
     * Equality and hash code are based solely on the entry name.
     */
    @Test
    void testEqualityBasedOnNameOnly() {
        final byte[] otherId = new byte[20];
        Arrays.fill(otherId, (byte) 0xff);
        final GitDirectoryEntry regular = new GitDirectoryEntry(Paths.get("foo"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry executable = new GitDirectoryEntry(Paths.get("foo"), GitDirectoryEntry.Type.EXECUTABLE, otherId);
        // Same name, different type and object id -> equal
        assertEquals(regular, executable);
        assertEquals(regular.hashCode(), executable.hashCode());
        // Different name -> not equal
        assertNotEquals(regular, new GitDirectoryEntry(Paths.get("bar"), GitDirectoryEntry.Type.REGULAR, ZERO_ID));
        // Same reference -> equal
        assertEquals(regular, regular);
        // Not equal to null or unrelated type
        assertNotEquals(regular, null);
        assertNotEquals(regular, "foo");
    }

    /**
     * The Path constructor must extract the filename component.
     */
    @Test
    void testPathConstructorUsesFilename() {
        final GitDirectoryEntry fromLabel = new GitDirectoryEntry(Paths.get("hello.txt"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry fromRelative = new GitDirectoryEntry(Paths.get("subdir/hello.txt"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry fromAbsolute = new GitDirectoryEntry(Paths.get("hello.txt").toAbsolutePath(), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
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
    void testSortOrder() {
        final GitDirectoryEntry alpha = new GitDirectoryEntry(Paths.get("alpha.txt"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry fooTxt = new GitDirectoryEntry(Paths.get("foo.txt"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry fooDir = new GitDirectoryEntry(Paths.get("foo"), GitDirectoryEntry.Type.DIRECTORY, ZERO_ID);
        final GitDirectoryEntry foobar = new GitDirectoryEntry(Paths.get("foobar"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final GitDirectoryEntry zeta = new GitDirectoryEntry(Paths.get("zeta.txt"), GitDirectoryEntry.Type.REGULAR, ZERO_ID);
        final List<GitDirectoryEntry> entries = new ArrayList<>(Arrays.asList(zeta, foobar, fooDir, alpha, fooTxt));
        entries.sort(GitDirectoryEntry::compareTo);
        assertEquals(Arrays.asList(alpha, fooTxt, fooDir, foobar, zeta), entries);
    }
}
