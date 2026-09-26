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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link Nysiis}
 */
class NysiisTest extends AbstractStringEncoderTest<Nysiis> {

    static Stream<Arguments> dropByCases() {
        // Explanation of differences between this implementation and the one at dropby.com is
        // prepended to the test string. The referenced rules refer to the outlined steps the
        // class description for Nysiis.
        // @formatter:off
        return Stream.of(
                // 1. Transcode first characters of name
                Arguments.of("MACINTOSH", "MCANT"),
                // violates 4j: the second N should not be added, as the first
                //              key char is already a N
                Arguments.of("KNUTH", "NAT"),           // Original: NNAT; modified: NATH
                // O and E are transcoded to A because of rule 4a
                // H also to A because of rule 4h
                // the N gets mysteriously lost, maybe because of a wrongly implemented rule 4h
                // that skips the next char in such a case?
                // the remaining A is removed because of rule 7
                Arguments.of("KOEHN", "CAN"),           // Original: C
                // violates 4j: see also KNUTH
                Arguments.of("PHILLIPSON", "FALAPSAN"), // Original: FFALAP[SAN]
                // violates 4j: see also KNUTH
                Arguments.of("PFEISTER", "FASTAR"),     // Original: FFASTA[R]
                // violates 4j: see also KNUTH
                Arguments.of("SCHOENHOEFT", "SANAFT"),  // Original: SSANAF[T]
                // 2. Transcode last characters of name:
                Arguments.of("MCKEE", "MCY"),
                Arguments.of("MACKIE", "MCY"),
                Arguments.of("HEITSCHMIDT", "HATSNAD"),
                Arguments.of("BART", "BAD"),
                Arguments.of("HURD", "HAD"),
                Arguments.of("HUNT", "HAD"),
                Arguments.of("WESTERLUND", "WASTARLAD"),
                // 4. Transcode remaining characters by following these rules,
                //    incrementing by one character each time:
                Arguments.of("CASSTEVENS", "CASTAFAN"),
                Arguments.of("VASQUEZ", "VASG"),
                Arguments.of("FRAZIER", "FRASAR"),
                Arguments.of("BOWMAN", "BANAN"),
                Arguments.of("MCKNIGHT", "MCNAGT"),
                Arguments.of("RICKERT", "RACAD"),
                // violates 5: the last S is not removed
                // when comparing to DEUTS, which is phonetically similar
                // the result it also DAT, which is correct for DEUTSCH too imo
                Arguments.of("DEUTSCH", "DAT"),         // Original: DATS
                Arguments.of("WESTPHAL", "WASTFAL"),
                // violates 4h: the H should be transcoded to S and thus ignored as
                // the first key character is also S
                Arguments.of("SHRIVER", "SRAVAR"),      // Original: SHRAVA[R]
                // same as KOEHN, the L gets mysteriously lost
                Arguments.of("KUHL", "CAL"),            // Original: C
                Arguments.of("RAWSON", "RASAN"),
                // If last character is S, remove it
                Arguments.of("JILES", "JAL"),
                // violates 6: if the last two characters are AY, remove A
                Arguments.of("CARRAWAY", "CARY"),       // Original: CARAY
                Arguments.of("YAMADA", "YANAD"));
        // @formatter:on
    }

    static Stream<Arguments> othersCases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("O'Daniel", "ODANAL"),
                Arguments.of("O'Donnel", "ODANAL"),
                Arguments.of("Cory", "CARY"),
                Arguments.of("Corey", "CARY"),
                Arguments.of("Kory", "CARY"),
                //
                Arguments.of("FUZZY", "FASY"));
        // @formatter:on
    }

    static Stream<Arguments> rule1Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("MACX", "MCX"),
                Arguments.of("KNX", "NX"),
                Arguments.of("KX", "CX"),
                Arguments.of("PHX", "FX"),
                Arguments.of("PFX", "FX"),
                Arguments.of("SCHX", "SX"));
        // @formatter:on
    }

    static Stream<Arguments> rule2Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XEE", "XY"),
                Arguments.of("XIE", "XY"),
                Arguments.of("XDT", "XD"),
                Arguments.of("XRT", "XD"),
                Arguments.of("XRD", "XD"),
                Arguments.of("XNT", "XD"),
                Arguments.of("XND", "XD"));
        // @formatter:on
    }

    static Stream<Arguments> rule4Dot1Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XEV", "XAF"),
                Arguments.of("XAX", "XAX"),
                Arguments.of("XEX", "XAX"),
                Arguments.of("XIX", "XAX"),
                Arguments.of("XOX", "XAX"),
                Arguments.of("XUX", "XAX"));
        // @formatter:on
    }

    static Stream<Arguments> rule4Dot2Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XQ", "XG"),
                Arguments.of("XZ", "X"),
                Arguments.of("XM", "XN"));
        // @formatter:on
    }

    static Stream<Arguments> rule5Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XS", "X"),
                Arguments.of("XSS", "X"));
        // @formatter:on
    }

    static Stream<Arguments> rule6Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XAY", "XY"),
                Arguments.of("XAYS", "XY")); // Rules 5, 6
        // @formatter:on
    }

    static Stream<Arguments> rule7Cases() {
        // @formatter:off
        return Stream.of(
                Arguments.of("XA", "X"),
                Arguments.of("XAS", "X")); // Rules 5, 7
        // @formatter:on
    }

    private final Nysiis fullNysiis = new Nysiis(false);

    @Override
    protected Nysiis createStringEncoder() {
        return new Nysiis();
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "Brian", "Brown", "Brun" })
    void testBran(final String input) {
        assertEquals("BRAN", getStringEncoder().encode(input));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "Capp", "Cope", "Copp", "Kipp" })
    void testCap(final String input) {
        assertEquals("CAP", getStringEncoder().encode(input));
    }

    @Test
    void testDad() {
        // Data Quality and Record Linkage Techniques P.121 claims this is DAN,
        // but it should be DAD, verified also with dropby.com
        assertEquals("DAD", getStringEncoder().encode("Dent"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "Dane", "Dean", "Dionne" })
    void testDan(final String input) {
        assertEquals("DAN", getStringEncoder().encode(input));
    }

    /**
     * Tests data gathered from around the internet.
     *
     * @see <a href="https://www.dropby.com/NYSIISTextStrings.html">http://www.dropby.com/NYSIISTextStrings.html</a>
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("dropByCases")
    void testDropBy(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    @Test
    void testFal() {
        assertEquals("FAL", getStringEncoder().encode("Phil"));
    }

    /**
     * Tests data gathered from around the internets.
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("othersCases")
    void testOthers(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 1: Translate first characters of name: MAC → MCC, KN → N, K → C, PH, PF → FF, SCH → SSS
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule1Cases")
    void testRule1(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 2: Translate last characters of name: EE → Y, IE → Y, DT, RT, RD, NT, ND → D
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule2Cases")
    void testRule2(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 4.1: EV → AF else A, E, I, O, U → A
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule4Dot1Cases")
    void testRule4Dot1(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 4.2: Q → G, Z → S, M → N
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule4Dot2Cases")
    void testRule4Dot2(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 5: If last character is S, remove it.
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule5Cases")
    void testRule5(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 6: If last characters are AY, replace with Y.
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule6Cases")
    void testRule6(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    /**
     * Tests rule 7: If last character is A, remove it.
     */
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("rule7Cases")
    void testRule7(final String input, final String expected) {
        assertEquals(expected, fullNysiis.encode(input));
    }

    @Test
    void testSnad() {
        // Data Quality and Record Linkage Techniques P.121 claims this is SNAT,
        // but it should be SNAD
        assertEquals("SNAD", getStringEncoder().encode("Schmidt"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "Smith", "Schmit" })
    void testSnat(final String input) {
        assertEquals("SNAT", getStringEncoder().encode(input));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @CsvSource({
        "Kobwick, CABWAC",
        "Kocher, CACAR",
        "Fesca, FASC",
        "Shom, SAN",
        "Ohlo, OL",
        "Uhu, UH",
        "Um, UN"
    })
    void testSpecialBranches(final String input, final String expected) {
        assertEquals(expected, getStringEncoder().encode(input));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "Trueman", "Truman" })
    void testTranan(final String input) {
        assertEquals("TRANAN", getStringEncoder().encode(input));
    }

    @Test
    void testTrueVariant() {
        final Nysiis encoder = new Nysiis(true);

        final String encoded = encoder.encode("WESTERLUND");
        assertTrue(encoded.length() <= 6);
        assertEquals("WASTAR", encoded);
    }

}
