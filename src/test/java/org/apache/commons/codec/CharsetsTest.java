/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sanity checks for {@link Charsets}.
 *
 */
public class CharsetsTest {

    @Test
    public void testToCharset() {
        assertEquals(Charset.defaultCharset(), Charsets.toCharset((String) null));
        assertEquals(Charset.defaultCharset(), Charsets.toCharset((Charset) null));
        assertEquals(Charset.defaultCharset(), Charsets.toCharset(Charset.defaultCharset()));
        assertEquals(StandardCharsets.UTF_8, Charsets.toCharset(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIso8859_1() {
        assertEquals("ISO-8859-1", Charsets.ISO_8859_1.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUsAscii() {
        assertEquals("US-ASCII", Charsets.US_ASCII.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16() {
        assertEquals("UTF-16", Charsets.UTF_16.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16Be() {
        assertEquals("UTF-16BE", Charsets.UTF_16BE.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf16Le() {
        assertEquals("UTF-16LE", Charsets.UTF_16LE.name());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUtf8() {
        assertEquals("UTF-8", Charsets.UTF_8.name());
    }

}
