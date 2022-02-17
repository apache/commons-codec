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

package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Nysiis}
 *
 * @since 1.7
 */
public class NysiisTest extends StringEncoderAbstractTest<Nysiis> {

    private final Nysiis fullNysiis = new Nysiis(false);

    /**
     * Takes an array of String pairs where each pair's first element is the input and the second element the expected
     * encoding.
     *
     * @param testValues
     *            an array of String pairs where each pair's first element is the input and the second element the
     *            expected encoding.
     * @throws EncoderException for some failure scenarios     */
    private void assertEncodings(final String[]... testValues) throws EncoderException {
        for (final String[] arr : testValues) {
            assertEquals(arr[1], this.fullNysiis.encode(arr[0]), "Problem with " + arr[0]);
        }
    }

    @Override
    protected Nysiis createStringEncoder() {
        return new Nysiis();
    }

    private void encodeAll(final String[] strings, final String expectedEncoding) {
        for (final String string : strings) {
            assertEquals(expectedEncoding, getStringEncoder().encode(string), "Problem with " + string);
        }
    }

    @Test
    public void testBran() {
        encodeAll(new String[] { "Brian", "Brown", "Brun" }, "BRAN");
    }

    @Test
    public void testCap() {
        this.encodeAll(new String[] { "Capp", "Cope", "Copp", "Kipp" }, "CAP");
    }

    @Test
    public void testDad() {
        // Data Quality and Record Linkage Techniques P.121 claims this is DAN,
        // but it should be DAD, verified also with dropby.com
        this.encodeAll(new String[] { "Dent" }, "DAD");
    }

    @Test
    public void testDan() {
        this.encodeAll(new String[] { "Dane", "Dean", "Dionne" }, "DAN");
    }

    /**
     * Tests data gathered from around the internet.
     *
     * @see <a href="http://www.dropby.com/NYSIISTextStrings.html">http://www.dropby.com/NYSIISTextStrings.html</a>
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testDropBy() throws EncoderException {
        // Explanation of differences between this implementation and the one at dropby.com is
        // prepended to the test string. The referenced rules refer to the outlined steps the
        // class description for Nysiis.

        this.assertEncodings(
                // 1. Transcode first characters of name
                new String[] { "MACINTOSH", "MCANT" },
                // violates 4j: the second N should not be added, as the first
                //              key char is already a N
                new String[] { "KNUTH", "NAT" },           // Original: NNAT; modified: NATH
                // O and E are transcoded to A because of rule 4a
                // H also to A because of rule 4h
                // the N gets mysteriously lost, maybe because of a wrongly implemented rule 4h
                // that skips the next char in such a case?
                // the remaining A is removed because of rule 7
                new String[] { "KOEHN", "CAN" },           // Original: C
                // violates 4j: see also KNUTH
                new String[] { "PHILLIPSON", "FALAPSAN" }, // Original: FFALAP[SAN]
                // violates 4j: see also KNUTH
                new String[] { "PFEISTER", "FASTAR" },     // Original: FFASTA[R]
                // violates 4j: see also KNUTH
                new String[] { "SCHOENHOEFT", "SANAFT" },  // Original: SSANAF[T]
                // 2. Transcode last characters of name:
                new String[] { "MCKEE", "MCY" },
                new String[] { "MACKIE", "MCY" },
                new String[] { "HEITSCHMIDT", "HATSNAD" },
                new String[] { "BART", "BAD" },
                new String[] { "HURD", "HAD" },
                new String[] { "HUNT", "HAD" },
                new String[] { "WESTERLUND", "WASTARLAD" },
                // 4. Transcode remaining characters by following these rules,
                //    incrementing by one character each time:
                new String[] { "CASSTEVENS", "CASTAFAN" },
                new String[] { "VASQUEZ", "VASG" },
                new String[] { "FRAZIER", "FRASAR" },
                new String[] { "BOWMAN", "BANAN" },
                new String[] { "MCKNIGHT", "MCNAGT" },
                new String[] { "RICKERT", "RACAD" },
                // violates 5: the last S is not removed
                // when comparing to DEUTS, which is phonetically similar
                // the result it also DAT, which is correct for DEUTSCH too imo
                new String[] { "DEUTSCH", "DAT" },         // Original: DATS
                new String[] { "WESTPHAL", "WASTFAL" },
                // violates 4h: the H should be transcoded to S and thus ignored as
                // the first key character is also S
                new String[] { "SHRIVER", "SRAVAR" },      // Original: SHRAVA[R]
                // same as KOEHN, the L gets mysteriously lost
                new String[] { "KUHL", "CAL" },            // Original: C
                new String[] { "RAWSON", "RASAN" },
                // If last character is S, remove it
                new String[] { "JILES", "JAL" },
                // violates 6: if the last two characters are AY, remove A
                new String[] { "CARRAWAY", "CARY" },       // Original: CARAY
                new String[] { "YAMADA", "YANAD" });
    }

    @Test
    public void testFal() {
        this.encodeAll(new String[] { "Phil" }, "FAL");
    }

    /**
     * Tests data gathered from around the internets.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testOthers() throws EncoderException {
        this.assertEncodings(
                new String[] { "O'Daniel", "ODANAL" },
                new String[] { "O'Donnel", "ODANAL" },
                new String[] { "Cory", "CARY" },
                new String[] { "Corey", "CARY" },
                new String[] { "Kory", "CARY" },
                //
                new String[] { "FUZZY", "FASY" });
    }

    /**
     * Tests rule 1: Translate first characters of name: MAC → MCC, KN → N, K → C, PH, PF → FF, SCH → SSS
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule1() throws EncoderException {
        this.assertEncodings(
                new String[] { "MACX", "MCX" },
                new String[] { "KNX", "NX" },
                new String[] { "KX", "CX" },
                new String[] { "PHX", "FX" },
                new String[] { "PFX", "FX" },
                new String[] { "SCHX", "SX" });
    }

    /**
     * Tests rule 2: Translate last characters of name: EE → Y, IE → Y, DT, RT, RD, NT, ND → D
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule2() throws EncoderException {
        this.assertEncodings(
                new String[] { "XEE", "XY" },
                new String[] { "XIE", "XY" },
                new String[] { "XDT", "XD" },
                new String[] { "XRT", "XD" },
                new String[] { "XRD", "XD" },
                new String[] { "XNT", "XD" },
                new String[] { "XND", "XD" });
    }

    /**
     * Tests rule 4.1: EV → AF else A, E, I, O, U → A
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule4Dot1() throws EncoderException {
        this.assertEncodings(
                new String[] { "XEV", "XAF" },
                new String[] { "XAX", "XAX" },
                new String[] { "XEX", "XAX" },
                new String[] { "XIX", "XAX" },
                new String[] { "XOX", "XAX" },
                new String[] { "XUX", "XAX" });
    }

    /**
     * Tests rule 4.2: Q → G, Z → S, M → N
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule4Dot2() throws EncoderException {
        this.assertEncodings(
                new String[] { "XQ", "XG" },
                new String[] { "XZ", "X" },
                new String[] { "XM", "XN" });
    }

    /**
     * Tests rule 5: If last character is S, remove it.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule5() throws EncoderException {
        this.assertEncodings(
                new String[] { "XS", "X" },
                new String[] { "XSS", "X" });
    }

    /**
     * Tests rule 6: If last characters are AY, replace with Y.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule6() throws EncoderException {
        this.assertEncodings(
                new String[] { "XAY", "XY" },
                new String[] { "XAYS", "XY" }); // Rules 5, 6
    }

    /**
     * Tests rule 7: If last character is A, remove it.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testRule7() throws EncoderException {
        this.assertEncodings(
                new String[] { "XA", "X" },
                new String[] { "XAS", "X" }); // Rules 5, 7
    }
    @Test
    public void testSnad() {
        // Data Quality and Record Linkage Techniques P.121 claims this is SNAT,
        // but it should be SNAD
        this.encodeAll(new String[] { "Schmidt" }, "SNAD");
    }

    @Test
    public void testSnat() {
        this.encodeAll(new String[] { "Smith", "Schmit" }, "SNAT");
    }

    @Test
    public void testSpecialBranches() {
        this.encodeAll(new String[] { "Kobwick" }, "CABWAC");
        this.encodeAll(new String[] { "Kocher" }, "CACAR");
        this.encodeAll(new String[] { "Fesca" }, "FASC");
        this.encodeAll(new String[] { "Shom" }, "SAN");
        this.encodeAll(new String[] { "Ohlo" }, "OL");
        this.encodeAll(new String[] { "Uhu" }, "UH");
        this.encodeAll(new String[] { "Um" }, "UN");
    }

    @Test
    public void testTranan() {
        this.encodeAll(new String[] { "Trueman", "Truman" }, "TRANAN");
    }

    @Test
    public void testTrueVariant() {
        final Nysiis encoder = new Nysiis(true);

        final String encoded = encoder.encode("WESTERLUND");
        assertTrue(encoded.length() <= 6);
        assertEquals("WASTAR", encoded);
    }

}
