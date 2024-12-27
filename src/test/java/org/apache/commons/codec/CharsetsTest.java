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

package org.apache.commons.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.Charsets;
import org.junit.jupiter.api.Test;

/**
 * Sanity checks for {@link Charsets}.
 */
public class CharsetsTest {

    private static final TreeSet<String> AVAILABLE_CHARSET_NAMES = new TreeSet<>(Charset.availableCharsets().keySet());

    public static SortedSet<String> getAvailableCharsetNames() {
        return AVAILABLE_CHARSET_NAMES;
    }

    public static Collection<Charset> getRequiredCharsets() {
        return Charsets.requiredCharsets().values();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIso8859_1() {
        assertEquals("ISO-8859-1", Charsets.ISO_8859_1.name());
    }

    @Test
    public void testToCharset() {
        assertEquals(Charset.defaultCharset(), Charsets.toCharset((String) null));
        assertEquals(Charset.defaultCharset(), Charsets.toCharset((Charset) null));
        assertEquals(Charset.defaultCharset(), Charsets.toCharset(Charset.defaultCharset()));
        assertEquals(StandardCharsets.UTF_8, Charsets.toCharset(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUsAscii() {
        assertEquals(StandardCharsets.US_ASCII.name(), Charsets.US_ASCII.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16() {
        assertEquals(StandardCharsets.UTF_16.name(), Charsets.UTF_16.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16Be() {
        assertEquals(StandardCharsets.UTF_16BE.name(), Charsets.UTF_16BE.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16Le() {
        assertEquals(StandardCharsets.UTF_16LE.name(), Charsets.UTF_16LE.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf8() {
        assertEquals(StandardCharsets.UTF_8.name(), Charsets.UTF_8.name());
    }

}
