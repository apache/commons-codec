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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sanity checks for {@link CharEncoding}.
 *
 */
public class CharEncodingTest {

    /**
     * We could make the constructor private in the future, it's a matter a style.
     */
    @Test
    public void testConstructor() {
        new CharEncoding();
    }

    @Test
    public void testIso8859_1() {
        assertEquals("ISO-8859-1", CharEncoding.ISO_8859_1);
    }

    @Test
    public void testUsAscii() {
        assertEquals("US-ASCII", CharEncoding.US_ASCII);
    }

    @Test
    public void testUtf16() {
        assertEquals("UTF-16", CharEncoding.UTF_16);
    }

    @Test
    public void testUtf16Be() {
        assertEquals("UTF-16BE", CharEncoding.UTF_16BE);
    }

    @Test
    public void testUtf16Le() {
        assertEquals("UTF-16LE", CharEncoding.UTF_16LE);
    }

    @Test
    public void testUtf8() {
        assertEquals("UTF-8", CharEncoding.UTF_8);
    }

}
