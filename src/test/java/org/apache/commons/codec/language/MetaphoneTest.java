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
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.junit.jupiter.api.Test;

/**
 */
public class MetaphoneTest extends AbstractStringEncoderTest<Metaphone> {

    public void assertIsMetaphoneEqual(final String source, final String[] matches) {
        // match source to all matches
        for (final String matche : matches) {
            assertTrue(getStringEncoder().isMetaphoneEqual(source, matche), "Source: " + source + ", should have same Metaphone as: " + matche);
        }
        // match to each other
        for (final String matche : matches) {
            for (final String matche2 : matches) {
                assertTrue(getStringEncoder().isMetaphoneEqual(matche, matche2));
            }
        }
    }

    public void assertMetaphoneEqual(final String[][] pairs) {
        validateFixture(pairs);
        for (final String[] pair : pairs) {
            final String name0 = pair[0];
            final String name1 = pair[1];
            final String failMsg = "Expected match between " + name0 + " and " + name1;
            assertTrue(getStringEncoder().isMetaphoneEqual(name0, name1), failMsg);
            assertTrue(getStringEncoder().isMetaphoneEqual(name1, name0), failMsg);
        }
    }

    @Override
    protected Metaphone createStringEncoder() {
        return new Metaphone();
    }

    @Test
    public void testDiscardOfSCEOrSCIOrSCY() {
        assertEquals("SNS", getStringEncoder().metaphone("SCIENCE"));
        assertEquals("SN", getStringEncoder().metaphone("SCENE"));
        assertEquals("S", getStringEncoder().metaphone("SCY"));
    }

    @Test
    public void testDiscardOfSilentGN() {
        // NOTE: This does not test for silent GN, but for starting with GN
        assertEquals("N", getStringEncoder().metaphone("GNU"));

        // NOTE: Trying to test for GNED, but expected code does not appear to execute
        assertEquals("SNT", getStringEncoder().metaphone("SIGNED"));
    }

    @Test
    public void testDiscardOfSilentHAfterG() {
        assertEquals("KNT", getStringEncoder().metaphone("GHENT"));
        assertEquals("B", getStringEncoder().metaphone("BAUGH"));
    }

    @Test
    public void testExceedLength() {
        // should be AKSKS, but is truncated by Max Code Length
        assertEquals("AKSK", getStringEncoder().metaphone("AXEAXE"));
    }

    @Test
    public void testIsMetaphoneEqual1() {
        assertMetaphoneEqual(new String[][] { { "Case", "case" }, { "CASE", "Case" }, { "caSe", "cAsE" }, { "quick", "cookie" } });
    }

    /**
     * Matches computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqual2() {
        assertMetaphoneEqual(new String[][] { { "Lawrence", "Lorenza" }, { "Gary", "Cahra" }, });
    }

    /**
     * Initial AE case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualAero() {
        assertIsMetaphoneEqual("Aero", new String[] { "Eure" });
    }

    /**
     * Initial A, not followed by an E case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualAlbert() {
        assertIsMetaphoneEqual("Albert", new String[] { "Ailbert", "Alberik", "Albert", "Alberto", "Albrecht" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualGary() {
        assertIsMetaphoneEqual("Gary",
                new String[] { "Cahra", "Cara", "Carey", "Cari", "Caria", "Carie", "Caro", "Carree", "Carri", "Carrie", "Carry", "Cary", "Cora", "Corey",
                        "Cori", "Corie", "Correy", "Corri", "Corrie", "Corry", "Cory", "Gray", "Kara", "Kare", "Karee", "Kari", "Karia", "Karie", "Karrah",
                        "Karrie", "Karry", "Kary", "Keri", "Kerri", "Kerrie", "Kerry", "Kira", "Kiri", "Kora", "Kore", "Kori", "Korie", "Korrie", "Korry" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualJohn() {
        assertIsMetaphoneEqual("John",
                new String[] { "Gena", "Gene", "Genia", "Genna", "Genni", "Gennie", "Genny", "Giana", "Gianna", "Gina", "Ginni", "Ginnie", "Ginny", "Jaine",
                        "Jan", "Jana", "Jane", "Janey", "Jania", "Janie", "Janna", "Jany", "Jayne", "Jean", "Jeana", "Jeane", "Jeanie", "Jeanna", "Jeanne",
                        "Jeannie", "Jen", "Jena", "Jeni", "Jenn", "Jenna", "Jennee", "Jenni", "Jennie", "Jenny", "Jinny", "Jo Ann", "Jo-Ann", "Jo-Anne", "Joan",
                        "Joana", "Joane", "Joanie", "Joann", "Joanna", "Joanne", "Joeann", "Johna", "Johnna", "Joni", "Jonie", "Juana", "June", "Junia",
                        "Junie" });
    }

    /**
     * Initial KN case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualKnight() {
        assertIsMetaphoneEqual("Knight", new String[] { "Hynda", "Nada", "Nadia", "Nady", "Nat", "Nata", "Natty", "Neda", "Nedda", "Nedi", "Netta",
                "Netti", "Nettie", "Netty", "Nita", "Nydia" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualMary() {
        assertIsMetaphoneEqual("Mary", new String[] { "Mair", "Maire", "Mara", "Mareah", "Mari", "Maria", "Marie", "Mary", "Maura", "Maure", "Meara",
                "Merrie", "Merry", "Mira", "Moira", "Mora", "Moria", "Moyra", "Muire", "Myra", "Myrah" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualParis() {
        assertIsMetaphoneEqual("Paris", new String[] { "Pearcy", "Perris", "Piercy", "Pierz", "Pryse" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualPeter() {
        assertIsMetaphoneEqual("Peter", new String[] { "Peadar", "Peder", "Pedro", "Peter", "Petr", "Peyter", "Pieter", "Pietro", "Piotr" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualRay() {
        assertIsMetaphoneEqual("Ray", new String[] { "Ray", "Rey", "Roi", "Roy", "Ruy" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualSusan() {
        assertIsMetaphoneEqual("Susan",
                new String[] { "Siusan", "Sosanna", "Susan", "Susana", "Susann", "Susanna", "Susannah", "Susanne", "Suzann", "Suzanna", "Suzanne", "Zuzana" });
    }

    /**
     * Initial WH case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualWhite() {
        assertIsMetaphoneEqual("White",
                new String[] { "Wade", "Wait", "Waite", "Wat", "Whit", "Wiatt", "Wit", "Wittie", "Witty", "Wood", "Woodie", "Woody" });
    }

    /**
     * Initial WR case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualWright() {
        assertIsMetaphoneEqual("Wright", new String[] { "Rota", "Rudd", "Ryde" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualXalan() {
        assertIsMetaphoneEqual("Xalan", new String[] { "Celene", "Celina", "Celine", "Selena", "Selene", "Selina", "Seline", "Suellen", "Xylina" });
    }

    @Test
    public void testMetaphone() {
        assertEquals("HL", getStringEncoder().metaphone("howl"));
        assertEquals("TSTN", getStringEncoder().metaphone("testing"));
        assertEquals("0", getStringEncoder().metaphone("The"));
        assertEquals("KK", getStringEncoder().metaphone("quick"));
        assertEquals("BRN", getStringEncoder().metaphone("brown"));
        assertEquals("FKS", getStringEncoder().metaphone("fox"));
        assertEquals("JMPT", getStringEncoder().metaphone("jumped"));
        assertEquals("OFR", getStringEncoder().metaphone("over"));
        assertEquals("0", getStringEncoder().metaphone("the"));
        assertEquals("LS", getStringEncoder().metaphone("lazy"));
        assertEquals("TKS", getStringEncoder().metaphone("dogs"));
    }

    @Test
    public void testPHTOF() {
        assertEquals("FX", getStringEncoder().metaphone("PHISH"));
    }

    @Test
    public void testSetMaxLengthWithTruncation() {
        // should be AKSKS, but istruncated by Max Code Length
        getStringEncoder().setMaxCodeLen(6);
        assertEquals("AKSKSK", getStringEncoder().metaphone("AXEAXEAXE"));
    }

    @Test
    public void testSHAndSIOAndSIAToX() {
        assertEquals("XT", getStringEncoder().metaphone("SHOT"));
        assertEquals("OTXN", getStringEncoder().metaphone("ODSIAN"));
        assertEquals("PLXN", getStringEncoder().metaphone("PULSION"));
    }

    @Test
    public void testTCH() {
        assertEquals("RX", getStringEncoder().metaphone("RETCH"));
        assertEquals("WX", getStringEncoder().metaphone("WATCH"));
    }

    @Test
    public void testTIOAndTIAToX() {
        assertEquals("OX", getStringEncoder().metaphone("OTIA"));
        assertEquals("PRXN", getStringEncoder().metaphone("PORTION"));
    }

    @Test
    public void testTranslateOfSCHAndCH() {
        assertEquals("SKTL", getStringEncoder().metaphone("SCHEDULE"));
        assertEquals("SKMT", getStringEncoder().metaphone("SCHEMATIC"));

        assertEquals("KRKT", getStringEncoder().metaphone("CHARACTER"));
        assertEquals("TX", getStringEncoder().metaphone("TEACH"));
    }

    @Test
    public void testTranslateToJOfDGEOrDGIOrDGY() {
        assertEquals("TJ", getStringEncoder().metaphone("DODGY"));
        assertEquals("TJ", getStringEncoder().metaphone("DODGE"));
        assertEquals("AJMT", getStringEncoder().metaphone("ADGIEMTI"));
    }

    /**
     * Tests (CODEC-57) Metaphone.metaphone(String) returns an empty string when passed the word "why"
     */
    @Test
    public void testWhy() {
        // PHP returns "H". The original metaphone returns an empty string.
        assertEquals("", getStringEncoder().metaphone("WHY"));
    }

    @Test
    public void testWordEndingInMB() {
        assertEquals("KM", getStringEncoder().metaphone("COMB"));
        assertEquals("TM", getStringEncoder().metaphone("TOMB"));
        assertEquals("WM", getStringEncoder().metaphone("WOMB"));
    }

    @Test
    public void testWordsWithCIA() {
        assertEquals("XP", getStringEncoder().metaphone("CIAPO"));
    }

    public void validateFixture(final String[][] pairs) {
        if (pairs.length == 0) {
            fail("Test fixture is empty");
        }
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i].length != 2) {
                fail("Error in test fixture in the data array at index " + i);
            }
        }
    }

}
