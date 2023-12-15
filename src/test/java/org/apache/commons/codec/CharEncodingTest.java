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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Sanity checks for {@link CharEncoding}.
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
        assertEquals(StandardCharsets.ISO_8859_1.name(), CharEncoding.ISO_8859_1);
    }

    @Test
    public void testUsAscii() {
        assertEquals(StandardCharsets.US_ASCII.name(), CharEncoding.US_ASCII);
    }

    @Test
    public void testUtf16() {
        assertEquals(StandardCharsets.UTF_16.name(), CharEncoding.UTF_16);
    }

    @Test
    public void testUtf16Be() {
        assertEquals(StandardCharsets.UTF_16BE.name(), CharEncoding.UTF_16BE);
    }

    @Test
    public void testUtf16Le() {
        assertEquals(StandardCharsets.UTF_16LE.name(), CharEncoding.UTF_16LE);
    }

    @Test
    public void testUtf8() {
        assertEquals(StandardCharsets.UTF_8.name(), CharEncoding.UTF_8);
    }

}
