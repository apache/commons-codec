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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

/**
 * Tests the {@link ColognePhonetic} class.
 *
 * <p>
 * Keep this file in UTF-8 encoding for proper Javadoc processing.
 * </p>
 */
class ColognePhoneticTest extends AbstractStringEncoderTest<ColognePhonetic> {

    private static final Set<String> TESTSET = new HashSet<>();

    /** Character sequences to be tested by the code. */
    // @formatter:off
    private static final String[] MATCHES = {
            ".*[AEIOUJY].*",         // A, E, I, J, O, U, Y
            ".*H.*",                 // H
            ".*B.*",                 // B
            ".*P[^H].*",             // P not before H
            ".*[DT][^CSZ].*",        // D,T not before C,S,Z
            ".*[FVW].*",             // F,V,W
            ".*PH.*",                // P before H
            ".*[GKQ].*",             // G,K,Q
            "C[AHKLOQRUX].*",        // Initial C before A, H, K, L, O, Q, R, U, X
            ".*[^SZ]C[AHKLOQRUX].*", // C before A, H, K, L, O, Q, R, U, X but not after S, Z
            ".*[^CKQ]X.*",           // X not after C,K,Q
            ".*L.*",                 // L
            ".*[MN].*",              // M,N
            ".*R.*",                 // R
            ".*[SZ].*",              // S,Z
            ".*[SZ]C.*",             // C after S,Z
            "C[^AHKLOQRUX].*",       // Initial C except before A, H, K, L, O, Q, R, U, X
            ".+C[^AHKLOQRUX].*",     // C except before A, H, K, L, O, Q, R, U, X
            ".*[DT][CSZ].*",         // D,T before C,S,Z
            ".*[CKQ]X.*",            // X after C,K,Q
            // @formatter:on
    };

    @AfterAll
    // Check that all possible input sequence conditions are represented
    public static void finishTests() {
        int errors = 0;
        for (final String m : MATCHES) {
            if (!hasTestCase(m)) {
                System.out.println(m + " has no test case");
                errors++;
            }
        }
        assertEquals(0, errors, "Not expecting any missing test cases");
    }

    private static boolean hasTestCase(final String re) {
        for (final String s : TESTSET) {
            if (s.matches(re)) {
                return true;
            }
        }
        return false;
    }

    // Allow command-line testing
    public static void main(final String[] args) {
        final ColognePhonetic coder = new ColognePhonetic();
        for (final String arg : args) {
            final String code = coder.encode(arg);
            System.out.println("'" + arg + "' = '" + code + "'");
        }
    }

    static Stream<Arguments> testBasicEncoding() {
        // @formatter:off
        return Stream.of(
            Arguments.arguments("01", "Aabjoe"),
            Arguments.arguments("0856", "Aaclan"),
            Arguments.arguments("04567", "Aychlmajr") // CODEC-122
        );
        // @formatter:on
    }

    static Stream<Arguments> testEdgeCases() {
        // @formatter:off
        return Stream.of(
            Arguments.arguments("a", "0"),
            Arguments.arguments("e", "0"),
            Arguments.arguments("i", "0"),
            Arguments.arguments("o", "0"),
            Arguments.arguments("u", "0"),
            Arguments.arguments("\u00E4", "0"), // a-umlaut
            Arguments.arguments("\u00F6", "0"), // o-umlaut
            Arguments.arguments("\u00FC", "0"), // u-umlaut
            Arguments.arguments("\u00DF", "8"), // small sharp s
            Arguments.arguments("aa", "0"),
            Arguments.arguments("ha", "0"),
            Arguments.arguments("h", ""),
            Arguments.arguments("aha", "0"),
            Arguments.arguments("b", "1"),
            Arguments.arguments("p", "1"),
            Arguments.arguments("ph", "3"),
            Arguments.arguments("f", "3"),
            Arguments.arguments("v", "3"),
            Arguments.arguments("w", "3"),
            Arguments.arguments("g", "4"),
            Arguments.arguments("k", "4"),
            Arguments.arguments("q", "4"),
            Arguments.arguments("x", "48"),
            Arguments.arguments("ax", "048"),
            Arguments.arguments("cx", "48"),
            Arguments.arguments("l", "5"),
            Arguments.arguments("cl", "45"),
            Arguments.arguments("acl", "085"),
            Arguments.arguments("mn", "6"),
            Arguments.arguments("{mn}", "6"), // test chars above Z
            Arguments.arguments("r", "7")
        );
        // @formatter:on
    }

    static Stream<Arguments> testExamples() {
        // @formatter:off
        return Stream.of(
            Arguments.arguments("m\u00DCller", "657"), // mÜller - why upper case U-umlaut?
            Arguments.arguments("m\u00FCller", "657"), // müller - add equivalent lower-case
            Arguments.arguments("schmidt", "862"),
            Arguments.arguments("schneider", "8627"),
            Arguments.arguments("fischer", "387"),
            Arguments.arguments("weber", "317"),
            Arguments.arguments("wagner", "3467"),
            Arguments.arguments("becker", "147"),
            Arguments.arguments("hoffmann", "0366"),
            Arguments.arguments("sch\u00C4fer", "837"), // schÄfer - why upper case A-umlaut ?
            Arguments.arguments("sch\u00e4fer", "837"), // schäfer - add equivalent lower-case
            Arguments.arguments("Breschnew", "17863"),
            Arguments.arguments("Wikipedia", "3412"),
            Arguments.arguments("peter", "127"),
            Arguments.arguments("pharma", "376"),
            Arguments.arguments("m\u00f6nchengladbach", "664645214"), // mönchengladbach
            Arguments.arguments("deutsch", "28"),
            Arguments.arguments("deutz", "28"),
            Arguments.arguments("hamburg", "06174"),
            Arguments.arguments("hannover", "0637"),
            Arguments.arguments("christstollen", "478256"),
            Arguments.arguments("Xanthippe", "48621"),
            Arguments.arguments("Zacharias", "8478"),
            Arguments.arguments("Holzbau", "0581"),
            Arguments.arguments("matsch", "68"),
            Arguments.arguments("matz", "68"),
            Arguments.arguments("Arbeitsamt", "071862"),
            Arguments.arguments("Eberhard", "01772"),
            Arguments.arguments("Eberhardt", "01772"),
            Arguments.arguments("Celsius", "8588"),
            Arguments.arguments("Ace", "08"),
            Arguments.arguments("shch", "84"), // CODEC-254
            Arguments.arguments("xch", "484"), // CODEC-255
            Arguments.arguments("heithabu", "021")
        );
        // @formatter:on
    }

    static Stream<Arguments> testIsEncodeEquals() {
        // @formatter:off
        return Stream.of(
            Arguments.arguments("Muller", "M\u00fcller"), // Müller
            Arguments.arguments("Meyer", "Mayr"),
            Arguments.arguments("house", "house"),
            Arguments.arguments("House", "house"),
            Arguments.arguments("Haus", "house"),
            Arguments.arguments("ganz", "Gans"),
            Arguments.arguments("ganz", "G\u00e4nse"), // Gänse
            Arguments.arguments("Miyagi", "Miyako")
        );
        // @formatter:on
    }

    @Override
    // Capture test strings for later checking
    public void checkEncoding(final String expected, final String source) throws EncoderException {
        // Note that the German letter Eszett is converted to SS by toUpperCase, so we don't need to replace it
        TESTSET.add(source.toUpperCase(Locale.GERMAN).replace('Ä', 'A').replace('Ö', 'O').replace('Ü', 'U'));
        super.checkEncoding(expected, source);
    }

    @Override
    protected ColognePhonetic createStringEncoder() {
        return new ColognePhonetic();
    }

    @ParameterizedTest
    @MethodSource
    void testBasicEncoding(final String expected, final String source) throws EncoderException {
        checkEncoding(expected, source);
    }

    @Test
    // Ensure that override still allows tests to work
    void testCanFail() {
        assertThrows(AssertionFailedError.class, () -> checkEncoding("/", "Fehler"));
    }

    @ParameterizedTest
    @MethodSource
    void testEdgeCases(final String source, final String expected) throws EncoderException {
        checkEncoding(expected, source);
    }

    @ParameterizedTest
    @MethodSource
    void testExamples(final String source, final String expected) throws EncoderException {
        checkEncoding(expected, source);
    }

    @Test
    void testHyphen() throws EncoderException {
        final String[][] data = { { "bergisch-gladbach", "174845214" }, { "M\u00fcller-L\u00fcdenscheidt", "65752682" } }; // Müller-Lüdenscheidt
        checkEncodings(data);
    }

    @ParameterizedTest
    @MethodSource
    void testIsEncodeEquals(final String source, final String expected) {
        final boolean encodeEqual = getStringEncoder().isEncodeEqual(expected, source);
        assertTrue(encodeEqual, () -> expected + " != " + source);
    }

    @Test
    void testSpecialCharsBetweenSameLetters() throws EncoderException {
        checkEncodingVariations("28282", "Test test", "Testtest", "Test-test", "TesT#Test", "TesT?test");
    }

    @Test
    void testVariationsMella() throws EncoderException {
        checkEncodingVariations("65", "mella", "milah", "moulla", "mellah", "muehle", "mule");
    }

    @Test
    void testVariationsMeyer() throws EncoderException {
        checkEncodingVariations("67", "Meier", "Maier", "Mair", "Meyer", "Meyr", "Mejer", "Major");
    }
}
