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

package org.apache.commons.codec.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Base58}.
 */
public class Base58Test {

    @Test
    void testBase58() {
        final String content = "Hello World";
        final byte[] encodedBytes = new Base58().encode(StringUtils.getBytesUtf8(content));
        final String encodedContent = StringUtils.newStringUtf8(encodedBytes);
        assertEquals("JxF12TrwUP45BMd", encodedContent, "encoding hello world");

        final byte[] decodedBytes = new Base58().decode(encodedBytes);
        final String decodedContent = StringUtils.newStringUtf8(decodedBytes);
        assertEquals(content, decodedContent, "decoding hello world");
    }
}
