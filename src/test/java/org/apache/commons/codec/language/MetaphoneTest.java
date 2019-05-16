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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Test;

/**
 */
public class MetaphoneTest extends StringEncoderAbstractTest<Metaphone> {

    public void assertIsMetaphoneEqual(final String source, final String[] matches) {
        // match source to all matches
        for (final String matche : matches) {
            assertTrue("Source: " + source + ", should have same Metaphone as: " + matche,
                       this.getStringEncoder().isMetaphoneEqual(source, matche));
        }
        // match to each other
        for (final String matche : matches) {
            for (final String matche2 : matches) {
                assertTrue(this.getStringEncoder().isMetaphoneEqual(matche, matche2));
            }
        }
    }

    public void assertMetaphoneEqual(final String[][] pairs) {
        this.validateFixture(pairs);
        for (final String[] pair : pairs) {
            final String name0 = pair[0];
            final String name1 = pair[1];
            final String failMsg = "Expected match between " + name0 + " and " + name1;
            assertTrue(failMsg, this.getStringEncoder().isMetaphoneEqual(name0, name1));
            assertTrue(failMsg, this.getStringEncoder().isMetaphoneEqual(name1, name0));
        }
    }

    @Override
    protected Metaphone createStringEncoder() {
        return new Metaphone();
    }

    @Test
    public void testIsMetaphoneEqual1() {
        this.assertMetaphoneEqual(new String[][] { { "Case", "case" }, {
                "CASE", "Case" }, {
                "caSe", "cAsE" }, {
                "quick", "cookie" }
        });
    }

    /**
     * Matches computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqual2() {
        this.assertMetaphoneEqual(new String[][] { { "Lawrence", "Lorenza" }, {
                "Gary", "Cahra" }, });
    }

    /**
     * Initial AE case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualAero() {
        this.assertIsMetaphoneEqual("Aero", new String[] { "Eure" });
    }

    /**
     * Initial WH case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualWhite() {
        this.assertIsMetaphoneEqual(
            "White",
            new String[] { "Wade", "Wait", "Waite", "Wat", "Whit", "Wiatt", "Wit", "Wittie", "Witty", "Wood", "Woodie", "Woody" });
    }

    /**
     * Initial A, not followed by an E case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualAlbert() {
        this.assertIsMetaphoneEqual("Albert", new String[] { "Ailbert", "Alberik", "Albert", "Alberto", "Albrecht" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualGary() {
        this.assertIsMetaphoneEqual(
            "Gary",
            new String[] {
                "Cahra",
                "Cara",
                "Carey",
                "Cari",
                "Caria",
                "Carie",
                "Caro",
                "Carree",
                "Carri",
                "Carrie",
                "Carry",
                "Cary",
                "Cora",
                "Corey",
                "Cori",
                "Corie",
                "Correy",
                "Corri",
                "Corrie",
                "Corry",
                "Cory",
                "Gray",
                "Kara",
                "Kare",
                "Karee",
                "Kari",
                "Karia",
                "Karie",
                "Karrah",
                "Karrie",
                "Karry",
                "Kary",
                "Keri",
                "Kerri",
                "Kerrie",
                "Kerry",
                "Kira",
                "Kiri",
                "Kora",
                "Kore",
                "Kori",
                "Korie",
                "Korrie",
                "Korry" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualJohn() {
        this.assertIsMetaphoneEqual(
            "John",
            new String[] {
                "Gena",
                "Gene",
                "Genia",
                "Genna",
                "Genni",
                "Gennie",
                "Genny",
                "Giana",
                "Gianna",
                "Gina",
                "Ginni",
                "Ginnie",
                "Ginny",
                "Jaine",
                "Jan",
                "Jana",
                "Jane",
                "Janey",
                "Jania",
                "Janie",
                "Janna",
                "Jany",
                "Jayne",
                "Jean",
                "Jeana",
                "Jeane",
                "Jeanie",
                "Jeanna",
                "Jeanne",
                "Jeannie",
                "Jen",
                "Jena",
                "Jeni",
                "Jenn",
                "Jenna",
                "Jennee",
                "Jenni",
                "Jennie",
                "Jenny",
                "Jinny",
                "Jo Ann",
                "Jo-Ann",
                "Jo-Anne",
                "Joan",
                "Joana",
                "Joane",
                "Joanie",
                "Joann",
                "Joanna",
                "Joanne",
                "Joeann",
                "Johna",
                "Johnna",
                "Joni",
                "Jonie",
                "Juana",
                "June",
                "Junia",
                "Junie" });
    }

    /**
     * Initial KN case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualKnight() {
        this.assertIsMetaphoneEqual(
            "Knight",
            new String[] {
                "Hynda",
                "Nada",
                "Nadia",
                "Nady",
                "Nat",
                "Nata",
                "Natty",
                "Neda",
                "Nedda",
                "Nedi",
                "Netta",
                "Netti",
                "Nettie",
                "Netty",
                "Nita",
                "Nydia" });
    }
    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualMary() {
        this.assertIsMetaphoneEqual(
            "Mary",
            new String[] {
                "Mair",
                "Maire",
                "Mara",
                "Mareah",
                "Mari",
                "Maria",
                "Marie",
                "Mary",
                "Maura",
                "Maure",
                "Meara",
                "Merrie",
                "Merry",
                "Mira",
                "Moira",
                "Mora",
                "Moria",
                "Moyra",
                "Muire",
                "Myra",
                "Myrah" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualParis() {
        this.assertIsMetaphoneEqual("Paris", new String[] { "Pearcy", "Perris", "Piercy", "Pierz", "Pryse" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualPeter() {
        this.assertIsMetaphoneEqual(
            "Peter",
            new String[] { "Peadar", "Peder", "Pedro", "Peter", "Petr", "Peyter", "Pieter", "Pietro", "Piotr" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualRay() {
        this.assertIsMetaphoneEqual("Ray", new String[] { "Ray", "Rey", "Roi", "Roy", "Ruy" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualSusan() {
        this.assertIsMetaphoneEqual(
            "Susan",
            new String[] {
                "Siusan",
                "Sosanna",
                "Susan",
                "Susana",
                "Susann",
                "Susanna",
                "Susannah",
                "Susanne",
                "Suzann",
                "Suzanna",
                "Suzanne",
                "Zuzana" });
    }

    /**
     * Initial WR case.
     *
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualWright() {
        this.assertIsMetaphoneEqual("Wright", new String[] { "Rota", "Rudd", "Ryde" });
    }

    /**
     * Match data computed from http://www.lanw.com/java/phonetic/default.htm
     */
    @Test
    public void testIsMetaphoneEqualXalan() {
        this.assertIsMetaphoneEqual(
            "Xalan",
            new String[] { "Celene", "Celina", "Celine", "Selena", "Selene", "Selina", "Seline", "Suellen", "Xylina" });
    }

    @Test
    public void testMetaphone() {
        assertEquals("HL", this.getStringEncoder().metaphone("howl"));
        assertEquals("TSTN", this.getStringEncoder().metaphone("testing"));
        assertEquals("0", this.getStringEncoder().metaphone("The"));
        assertEquals("KK", this.getStringEncoder().metaphone("quick"));
        assertEquals("BRN", this.getStringEncoder().metaphone("brown"));
        assertEquals("FKS", this.getStringEncoder().metaphone("fox"));
        assertEquals("JMPT", this.getStringEncoder().metaphone("jumped"));
        assertEquals("OFR", this.getStringEncoder().metaphone("over"));
        assertEquals("0", this.getStringEncoder().metaphone("the"));
        assertEquals("LS", this.getStringEncoder().metaphone("lazy"));
        assertEquals("TKS", this.getStringEncoder().metaphone("dogs"));
    }

    @Test
    public void testWordEndingInMB() {
        assertEquals( "KM", this.getStringEncoder().metaphone("COMB") );
        assertEquals( "TM", this.getStringEncoder().metaphone("TOMB") );
        assertEquals( "WM", this.getStringEncoder().metaphone("WOMB") );
    }

    @Test
    public void testDiscardOfSCEOrSCIOrSCY() {
        assertEquals( "SNS", this.getStringEncoder().metaphone("SCIENCE") );
        assertEquals( "SN", this.getStringEncoder().metaphone("SCENE") );
        assertEquals( "S", this.getStringEncoder().metaphone("SCY") );
    }

    /**
     * Tests (CODEC-57) Metaphone.metaphone(String) returns an empty string when passed the word "why"
     */
    @Test
    public void testWhy() {
        // PHP returns "H". The original metaphone returns an empty string.
        assertEquals("", this.getStringEncoder().metaphone("WHY"));
    }

    @Test
    public void testWordsWithCIA() {
        assertEquals( "XP", this.getStringEncoder().metaphone("CIAPO") );
    }

    @Test
    public void testTranslateOfSCHAndCH() {
        assertEquals( "SKTL", this.getStringEncoder().metaphone("SCHEDULE") );
        assertEquals( "SKMT", this.getStringEncoder().metaphone("SCHEMATIC") );

        assertEquals( "KRKT", this.getStringEncoder().metaphone("CHARACTER") );
        assertEquals( "TX", this.getStringEncoder().metaphone("TEACH") );
    }

    @Test
    public void testTranslateToJOfDGEOrDGIOrDGY() {
        assertEquals( "TJ", this.getStringEncoder().metaphone("DODGY") );
        assertEquals( "TJ", this.getStringEncoder().metaphone("DODGE") );
        assertEquals( "AJMT", this.getStringEncoder().metaphone("ADGIEMTI") );
    }

    @Test
    public void testDiscardOfSilentHAfterG() {
        assertEquals( "KNT", this.getStringEncoder().metaphone("GHENT") );
        assertEquals( "B", this.getStringEncoder().metaphone("BAUGH") );
    }

    @Test
    public void testDiscardOfSilentGN() {
        // NOTE: This does not test for silent GN, but for starting with GN
        assertEquals( "N", this.getStringEncoder().metaphone("GNU") );

        // NOTE: Trying to test for GNED, but expected code does not appear to execute
        assertEquals( "SNT", this.getStringEncoder().metaphone("SIGNED") );
    }

    @Test
    public void testPHTOF() {
        assertEquals( "FX", this.getStringEncoder().metaphone("PHISH") );
    }

    @Test
    public void testSHAndSIOAndSIAToX() {
        assertEquals( "XT", this.getStringEncoder().metaphone("SHOT") );
        assertEquals( "OTXN", this.getStringEncoder().metaphone("ODSIAN") );
        assertEquals( "PLXN", this.getStringEncoder().metaphone("PULSION") );
    }

    @Test
    public void testTIOAndTIAToX() {
        assertEquals( "OX", this.getStringEncoder().metaphone("OTIA") );
        assertEquals( "PRXN", this.getStringEncoder().metaphone("PORTION") );
    }

    @Test
    public void testTCH() {
        assertEquals( "RX", this.getStringEncoder().metaphone("RETCH") );
        assertEquals( "WX", this.getStringEncoder().metaphone("WATCH") );
    }

    @Test
    public void testExceedLength() {
        // should be AKSKS, but istruncated by Max Code Length
        assertEquals( "AKSK", this.getStringEncoder().metaphone("AXEAXE") );
    }

    @Test
    public void testSetMaxLengthWithTruncation() {
        // should be AKSKS, but istruncated by Max Code Length
        this.getStringEncoder().setMaxCodeLen( 6 );
        assertEquals( "AKSKSK", this.getStringEncoder().metaphone("AXEAXEAXE") );
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
