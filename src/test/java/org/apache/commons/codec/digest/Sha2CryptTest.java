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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class Sha2CryptTest {

    private static void assertSaltRejected(final int bits, final String salt) {
        final byte[] key = "secret".getBytes(StandardCharsets.UTF_8);
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            if (bits == 256) {
                Sha2Crypt.sha256Crypt(key, salt);
            } else {
                Sha2Crypt.sha512Crypt(key, salt);
            }
        });
    }

    static Stream<Arguments> invalidSaltCharacters() {
        return saltsForBothVariants(new String[][] {
            // @formatter:off
            { "Unicode after valid prefix", "rounds=1000$abcäöüäöü" },
            { "space after valid prefix", "abc def" },
            { "tab after valid prefix", "abc\tdef" },
            { "punctuation after valid prefix", "abc!" },
            { "invalid character after 16 valid characters", "abcdefghijklmnop!" }
            // @formatter:on
        });
    }

    static Stream<Arguments> malformedRounds() {
        return saltsForBothVariants(new String[][] {
            // @formatter:off
            { "incorrect rounds keyword", "notrounds=1000$asdfasdf" },
            { "empty rounds", "rounds=$abc" },
            { "non-numeric rounds", "rounds=abc$abc" },
            { "negative rounds", "rounds=-1$abc" }
            // @formatter:on
        });
    }

    private static Stream<Arguments> saltsForBothVariants(final String[][] cases) {
        return Stream.of(256, 512).flatMap(bits -> Stream.of(cases)
                .map(testCase -> Arguments.of(bits, Named.of(testCase[0], (bits == 256 ? "$5$" : "$6$") + testCase[1]))));
    }

    static Stream<Arguments> trailingLineTerminators() {
        return saltsForBothVariants(new String[][] {
            // @formatter:off
            { "LF", "rounds=1000$abc\n" },
            { "CR", "rounds=1000$abc\r" },
            { "CRLF", "rounds=1000$abc\r\n" },
            { "NEL (U+0085)", "rounds=1000$abc\u0085" },
            { "LINE SEPARATOR (U+2028)", "rounds=1000$abc\u2028" },
            { "PARAGRAPH SEPARATOR (U+2029)", "rounds=1000$abc\u2029" }
            // @formatter:on
        });
    }

    @SuppressWarnings("deprecation")
    @Test
    void testCtor() {
        assertNotNull(new Sha2Crypt());
    }

    @ParameterizedTest(name = "SHA-{0}: {1}")
    @MethodSource("invalidSaltCharacters")
    void testInvalidSaltCharactersRejected(final int bits, final String salt) {
        assertSaltRejected(bits, salt);
    }

    @ParameterizedTest(name = "SHA-{0}: invalid salt prefix")
    @ValueSource(ints = { 256, 512 })
    void testInvalidSaltPrefixRejected(final int bits) {
        assertSaltRejected(bits, "xx");
    }

    @ParameterizedTest
    @ValueSource(ints = { 100_000, 1_000_000 })
    void testLargeRounds(final int rounds) {
        final String salt = "$6$rounds=" + rounds + "$abcdefghijklmnop";
        Crypt.crypt("anything".getBytes(StandardCharsets.UTF_8), salt);
    }

    @ParameterizedTest(name = "SHA-{0}: {1}")
    @MethodSource("malformedRounds")
    void testMalformedRoundsRejected(final int bits, final String salt) {
        assertSaltRejected(bits, salt);
    }

    @Test
    void testRoundsAboveCeilingRejected() {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=1000001$abcdefghijklmnop"));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=999999999$abcdefghijklmnop"));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=99999999999$abcdefghijklmnop"));
    }

    @Test
    void testRoundsCeilingOverride() {
        final String previous = System.getProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY);
        System.setProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY, "2000000");
        try {
            assertNotNull(Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=2000000$abcdefghijklmnop"));
        } finally {
            if (previous == null) {
                System.clearProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY);
            } else {
                System.setProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY, previous);
            }
        }
    }

    @Test
    void testRoundsLeadingZeroes() {
        final String expected = Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=1000$abcdefghijklmnop");
        final String actual = Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=0000001000$abcdefghijklmnop");
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "SHA-{0}: {1}")
    @MethodSource("trailingLineTerminators")
    void testTrailingLineTerminatorsRejected(final int bits, final String salt) {
        assertSaltRejected(bits, salt);
    }
}
