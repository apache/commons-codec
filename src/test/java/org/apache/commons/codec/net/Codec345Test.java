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

package org.apache.commons.codec.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the reproduction in <a href="https://issues.apache.org/jira/browse/CODEC-345">CODEC-345</a>.
 */
class Codec345Test {

    @ParameterizedTest
    @ValueSource(strings = { "/pages/1/Test+Page", "/display/TST/Caf%C3%A9" })
    void testEncodeUrlWithCallerSuppliedSafeCharacters(final String input) {
        // RFC 2396 abs_path, as used by HtmlUnit's UrlUtils and HttpClient 3.x's URI.
        final BitSet allowed = new BitSet(256);
        for (int c = 'a'; c <= 'z'; c++) {
            allowed.set(c);
        }
        for (int c = 'A'; c <= 'Z'; c++) {
            allowed.set(c);
        }
        for (int c = '0'; c <= '9'; c++) {
            allowed.set(c);
        }
        for (final char c : "-_.!~*'()".toCharArray()) {
            allowed.set(c); // mark
        }
        allowed.set('%'); // escaped
        for (final char c : ":@&=+$,;/".toCharArray()) {
            allowed.set(c); // pchar
        }
        assertEquals(input, new String(URLCodec.encodeUrl(allowed, input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.US_ASCII));
    }
}
