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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link StringUtils}
 */
class StringUtilsTest {

    private static final byte[] BYTES_FIXTURE = { 'a', 'b', 'c' };

    // This is valid input for UTF-16BE
    private static final byte[] BYTES_FIXTURE_16BE = { 0, 'a', 0, 'b', 0, 'c' };

    // This is valid for UTF-16LE
    private static final byte[] BYTES_FIXTURE_16LE = { 'a', 0, 'b', 0, 'c', 0 };

    private static final String STRING_FIXTURE = "ABC";

    static Stream<Arguments> getBytesCases() {
        return Stream.of(
                Arguments.of(StandardCharsets.ISO_8859_1,
                        Named.<Function<String, byte[]>>of("getBytesIso8859_1", StringUtils::getBytesIso8859_1)),
                Arguments.of(StandardCharsets.US_ASCII,
                        Named.<Function<String, byte[]>>of("getBytesUsAscii", StringUtils::getBytesUsAscii)),
                Arguments.of(StandardCharsets.UTF_16,
                        Named.<Function<String, byte[]>>of("getBytesUtf16", StringUtils::getBytesUtf16)),
                Arguments.of(StandardCharsets.UTF_16BE,
                        Named.<Function<String, byte[]>>of("getBytesUtf16Be", StringUtils::getBytesUtf16Be)),
                Arguments.of(StandardCharsets.UTF_16LE,
                        Named.<Function<String, byte[]>>of("getBytesUtf16Le", StringUtils::getBytesUtf16Le)),
                Arguments.of(StandardCharsets.UTF_8,
                        Named.<Function<String, byte[]>>of("getBytesUtf8", StringUtils::getBytesUtf8)));
    }

    static Stream<Arguments> mixedEqualityCases() {
        return Stream.of(
                Arguments.of(Named.of("String(abc)", "abc"), Named.of("StringBuilder(abc)", new StringBuilder("abc")), true),
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), Named.of("String(abcd)", "abcd"), false),
                Arguments.of(Named.of("String(abcd)", "abcd"), Named.of("StringBuilder(abc)", new StringBuilder("abc")), false),
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), Named.of("String(ABC)", "ABC"), false));
    }

    static Stream<Arguments> newStringCases() {
        return Stream.of(
                Arguments.of(StandardCharsets.ISO_8859_1,
                        Named.<Function<byte[], String>>of("newStringIso8859_1", StringUtils::newStringIso8859_1), BYTES_FIXTURE),
                Arguments.of(StandardCharsets.US_ASCII,
                        Named.<Function<byte[], String>>of("newStringUsAscii", StringUtils::newStringUsAscii), BYTES_FIXTURE),
                Arguments.of(StandardCharsets.UTF_16,
                        Named.<Function<byte[], String>>of("newStringUtf16", StringUtils::newStringUtf16), BYTES_FIXTURE),
                Arguments.of(StandardCharsets.UTF_16BE,
                        Named.<Function<byte[], String>>of("newStringUtf16Be", StringUtils::newStringUtf16Be), BYTES_FIXTURE_16BE),
                Arguments.of(StandardCharsets.UTF_16LE,
                        Named.<Function<byte[], String>>of("newStringUtf16Le", StringUtils::newStringUtf16Le), BYTES_FIXTURE_16LE),
                Arguments.of(StandardCharsets.UTF_8,
                        Named.<Function<byte[], String>>of("newStringUtf8", StringUtils::newStringUtf8), BYTES_FIXTURE));
    }

    static Stream<Named<Function<byte[], String>>> newStringFunctions() {
        return Stream.of(
                Named.<Function<byte[], String>>of("newStringIso8859_1", StringUtils::newStringIso8859_1),
                Named.<Function<byte[], String>>of("newStringUsAscii", StringUtils::newStringUsAscii),
                Named.<Function<byte[], String>>of("newStringUtf16", StringUtils::newStringUtf16),
                Named.<Function<byte[], String>>of("newStringUtf16Be", StringUtils::newStringUtf16Be),
                Named.<Function<byte[], String>>of("newStringUtf16Le", StringUtils::newStringUtf16Le),
                Named.<Function<byte[], String>>of("newStringUtf8", StringUtils::newStringUtf8));
    }

    static Stream<Arguments> stringBuilderEqualityCases() {
        return Stream.of(
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), null, false),
                Arguments.of(null, Named.of("StringBuilder(abc)", new StringBuilder("abc")), false),
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), Named.of("StringBuilder(abc)", new StringBuilder("abc")), true),
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), Named.of("StringBuilder(abcd)", new StringBuilder("abcd")), false),
                Arguments.of(Named.of("StringBuilder(abcd)", new StringBuilder("abcd")), Named.of("StringBuilder(abc)", new StringBuilder("abc")), false),
                Arguments.of(Named.of("StringBuilder(abc)", new StringBuilder("abc")), Named.of("StringBuilder(ABC)", new StringBuilder("ABC")), false));
    }

    static Stream<Arguments> stringEqualityCases() {
        return Stream.of(
                Arguments.of(null, null, true),
                Arguments.of(Named.of("String(abc)", "abc"), null, false),
                Arguments.of(null, Named.of("String(abc)", "abc"), false),
                Arguments.of(Named.of("String(abc)", "abc"), Named.of("String(abc)", "abc"), true),
                Arguments.of(Named.of("String(abc)", "abc"), Named.of("String(abcd)", "abcd"), false),
                Arguments.of(Named.of("String(abcd)", "abcd"), Named.of("String(abc)", "abc"), false),
                Arguments.of(Named.of("String(abc)", "abc"), Named.of("String(ABC)", "ABC"), false));
    }

    @Test
    void testByteBufferUtf8() {
        assertNull(StringUtils.getByteBufferUtf8(null), "Should be null safe");
        final String text = "asdhjfhsadiogasdjhagsdygfjasfgsdaksjdhfk";
        final ByteBuffer bb = StringUtils.getByteBufferUtf8(text);
        assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), bb.array());
    }

    /**
     * We could make the constructor private but there does not seem to be a point to jumping through extra code hoops
     * to restrict instantiation right now.
     */
    @Test
    void testConstructor() {
        new StringUtils();
    }

    @ParameterizedTest(name = "{0}, {1}: {2}")
    @MethodSource("mixedEqualityCases")
    void testEqualsMixedCharSequences(final CharSequence left, final CharSequence right, final boolean expected) {
        assertEquals(expected, StringUtils.equals(left, right));
    }

    @ParameterizedTest(name = "{0}, {1}: {2}")
    @MethodSource("stringBuilderEqualityCases")
    void testEqualsStringBuilders(final CharSequence left, final CharSequence right, final boolean expected) {
        assertEquals(expected, StringUtils.equals(left, right));
    }

    @ParameterizedTest(name = "{0}, {1}: {2}")
    @MethodSource("stringEqualityCases")
    void testEqualsStrings(final CharSequence left, final CharSequence right, final boolean expected) {
        assertEquals(expected, StringUtils.equals(left, right));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("getBytesCases")
    void testGetBytes(final Charset charset, final Function<String, byte[]> encoder) throws UnsupportedEncodingException {
        final String charsetName = charset.name();
        final byte[] expected = STRING_FIXTURE.getBytes(charsetName);
        assertArrayEquals(expected, StringUtils.getBytesUnchecked(STRING_FIXTURE, charsetName));
        assertArrayEquals(expected, encoder.apply(STRING_FIXTURE));
    }

    @Test
    void testGetBytesUncheckedBadName() {
        assertThrows(IllegalStateException.class, () -> StringUtils.getBytesUnchecked(STRING_FIXTURE, "UNKNOWN"));
    }

    @Test
    void testGetBytesUncheckedNullInput() {
        assertNull(StringUtils.getBytesUnchecked(null, "UNKNOWN"));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("newStringCases")
    void testNewString(final Charset charset, final Function<byte[], String> decoder, final byte[] input) throws UnsupportedEncodingException {
        final String charsetName = charset.name();
        assertEquals(new String(BYTES_FIXTURE, charsetName), StringUtils.newString(BYTES_FIXTURE, charsetName));
        assertEquals(new String(input, charsetName), decoder.apply(input));
    }

    @Test
    void testNewStringBadEnc() {
        assertThrows(IllegalStateException.class, () -> StringUtils.newString(BYTES_FIXTURE, "UNKNOWN"));
    }

    @Test
    void testNewStringNullInput() {
        assertNull(StringUtils.newString(null, "UNKNOWN"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("newStringFunctions")
    void testNewStringNullInput_CODEC229(final Function<byte[], String> decoder) {
        assertNull(decoder.apply(null));
    }
}
