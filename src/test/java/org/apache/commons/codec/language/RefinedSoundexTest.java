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

package org.apache.commons.codec.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests RefinedSoundex.
 */
class RefinedSoundexTest extends AbstractStringEncoderTest<RefinedSoundex> {

    static Stream<Arguments> differenceCases() {
        // @formatter:off
        return Stream.of(
                // Normal cases
                Arguments.of("Smith", "Smythe", 6),
                Arguments.of("Ann", "Andrew", 3),
                Arguments.of("Margaret", "Andrew", 1),
                Arguments.of("Janet", "Margaret", 1),
                // Examples from
                // https://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
                Arguments.of("Green", "Greene", 5),
                Arguments.of("Blotchet-Halls", "Greene", 1),
                // Examples from
                // https://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
                Arguments.of("Smith", "Smythe", 6),
                Arguments.of("Smithers", "Smythers", 8),
                Arguments.of("Anothers", "Brothers", 5));
        // @formatter:on
    }

    @Override
    protected RefinedSoundex createStringEncoder() {
        return new RefinedSoundex();
    }

    @ParameterizedTest(name = "{0}, {1}: difference {2}")
    @MethodSource("differenceCases")
    void testDifference(final String left, final String right, final int expected) throws EncoderException {
        assertEquals(expected, getStringEncoder().difference(left, right));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " " })
    void testDifferenceEmpty(final String input) throws EncoderException {
        assertEquals(0, getStringEncoder().difference(input, input));
    }

    @ParameterizedTest(name = "{0}: {1}")
    // @formatter:off
    @CsvSource({
        "testing, T6036084",
        "TESTING, T6036084",
        "The, T60",
        "quick, Q503",
        "brown, B1908",
        "fox, F205",
        "jumped, J408106",
        "over, O0209",
        "the, T60",
        "lazy, L7050",
        "dogs, D6043"
    })
    // @formatter:on
    void testEncode(final String input, final String expected) {
        assertEquals(expected, getStringEncoder().encode(input));
    }

    @Test
    void testEncodeCodec56() {
        assertEquals("D6043", RefinedSoundex.US_ENGLISH.encode("dogs"));
    }

    @Test
    void testGetMappingCodeNonLetter() {
        final char code = getStringEncoder().getMappingCode('#');
        assertEquals(0, code, "Code does not equals zero");
    }

    @Test
    void testInvalidSoundexCharacter() {
        final char[] invalid = new char[256];
        for (int i = 0; i < invalid.length; i++) {
            invalid[i] = (char) i;
        }

        assertEquals(new RefinedSoundex().encode(new String(invalid)), "A0136024043780159360205050136024043780159360205053");
    }

    @Test
    void testNewInstance() {
        assertEquals("D6043", new RefinedSoundex().soundex("dogs"));
    }

    @Test
    void testNewInstance2() {
        assertEquals("D6043", new RefinedSoundex(RefinedSoundex.US_ENGLISH_MAPPING_STRING.toCharArray()).soundex("dogs"));
    }

    @Test
    void testNewInstance3() {
        assertEquals("D6043", new RefinedSoundex(RefinedSoundex.US_ENGLISH_MAPPING_STRING).soundex("dogs"));
    }
}
