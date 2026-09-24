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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * RFC 1522 compliant codec test cases
 */
class RFC1522CodecTest {

    static class RFC1522TestCodec extends RFC1522Codec {

        RFC1522TestCodec() {
            super(StandardCharsets.UTF_8);
        }

        @Override
        protected byte[] doDecoding(final byte[] bytes) {
            return bytes;
        }

        @Override
        protected byte[] doEncoding(final byte[] bytes) {
            return bytes;
        }

        @Override
        protected String getEncoding() {
            return "T";
        }
    }

    static void assertExpectedDecoderException(final String text) {
        assertThrows(DecoderException.class, () -> new RFC1522TestCodec().decodeText(text));
    }

    @ParameterizedTest
    // @formatter:off
    @ValueSource(strings = {
        "whatever",
        "=?",
        "?=",
        "==",
        "=?=",
        "=??=",
        "=?stuff?=",
        "=?UTF-8??=",
        "=?UTF-8?stuff?=",
        "=?UTF-8?T?stuff",
        "=??T?stuff?=",
        "=?UTF-8??stuff?=",
        "=?UTF-8?W?stuff?=",
        "=?UTF-8?T?stuff?more?=",
        "=?UTF-8?T?stuff??=",
        "=?UTF-8?T???="
    })
    // @formatter:on
    void testDecodeInvalid(final String text) throws Exception {
        assertExpectedDecoderException(text);
    }

    @Test
    void testNullInput() throws Exception {
        final RFC1522TestCodec testCodec = new RFC1522TestCodec();
        assertNull(testCodec.decodeText(null));
        assertNull(testCodec.encodeText(null, CharEncoding.UTF_8));
    }
}
