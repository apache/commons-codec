/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * @version $Revision: 1.9 $ $Date: 2004/02/23 07:32:50 $
 * @author Rodney Waldhoff
 * @author Gary Gregory
 */
public class MetaphoneTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return (new TestSuite(MetaphoneTest.class));
    }

    private Metaphone metaphone = null;

    public MetaphoneTest(String name) {
        super(name);
    }

    public void assertIsMetaphoneEqual(String source, String[] matches) {
        // match source to all matches
        for (int i = 0; i < matches.length; i++) {
            assertTrue(this.getMetaphone().isMetaphoneEqual(source, matches[i]));
        }
        // match to each other
        for (int i = 0; i < matches.length; i++) {
            for (int j = 0; j < matches.length; j++) {
                assertTrue(this.getMetaphone().isMetaphoneEqual(matches[i], matches[j]));
            }
        }
    }

    public void assertMetaphoneEqual(String[][] pairs) {
        this.validateFixture(pairs);
        for (int i = 0; i < pairs.length; i++) {
            String name0 = pairs[i][0];
            String name1 = pairs[i][1];
            String failMsg = "Expected match between " + name0 + " and " + name1;
            assertTrue(failMsg, this.getMetaphone().isMetaphoneEqual(name0, name1));
            assertTrue(failMsg, this.getMetaphone().isMetaphoneEqual(name1, name0));
        }
    }
    /**
	 * @return Returns the metaphone.
	 */
    private Metaphone getMetaphone() {
        return this.metaphone;
    }

    protected StringEncoder makeEncoder() {
        return new Metaphone();
    }

    /**
	 * @param metaphone
	 *                  The metaphone to set.
	 */
    private void setMetaphone(Metaphone metaphone) {
        this.metaphone = metaphone;
    }

    public void setUp() throws Exception {
        super.setUp();
        this.setMetaphone(new Metaphone());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        this.setMetaphone(null);
    }

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
    public void testIsMetaphoneEqual2() {
        this.assertMetaphoneEqual(new String[][] { { "Lawrence", "Lorenza" }, {
                "Gary", "Cahra" }, });
    }

    /**
	 * Initial AE case.
	 * 
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
    public void testIsMetaphoneEqualAero() {
        this.assertIsMetaphoneEqual("Aero", new String[] { "Eure" });
    }

    /**
	 * Initial WH case.
	 * 
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
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
    public void testIsMetaphoneEqualAlbert() {
        this.assertIsMetaphoneEqual("Albert", new String[] { "Ailbert", "Alberik", "Albert", "Alberto", "Albrecht" });
    }

    /**
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
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
    public void testIsMetaphoneEqualParis() {
        this.assertIsMetaphoneEqual("Paris", new String[] { "Pearcy", "Perris", "Piercy", "Pierz", "Pryse" });
    }

    /**
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
    public void testIsMetaphoneEqualPeter() {
        this.assertIsMetaphoneEqual(
            "Peter",
            new String[] { "Peadar", "Peder", "Pedro", "Peter", "Petr", "Peyter", "Pieter", "Pietro", "Piotr" });
    }

    /**
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
    public void testIsMetaphoneEqualRay() {
        this.assertIsMetaphoneEqual("Ray", new String[] { "Ray", "Rey", "Roi", "Roy", "Ruy" });
    }

    /**
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
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
    public void testIsMetaphoneEqualWright() {
        this.assertIsMetaphoneEqual("Wright", new String[] { "Rota", "Rudd", "Ryde" });
    }

    /**
	 * Match data computed from http://www.lanw.com/java/phonetic/default.htm
	 */
    public void testIsMetaphoneEqualXalan() {
        this.assertIsMetaphoneEqual(
            "Xalan",
            new String[] { "Celene", "Celina", "Celine", "Selena", "Selene", "Selina", "Seline", "Suellen", "Xylina" });
    }

    public void testMetaphone() {
        assertEquals("TSTN", this.getMetaphone().metaphone("testing"));
        assertEquals("0", this.getMetaphone().metaphone("The"));
        assertEquals("KK", this.getMetaphone().metaphone("quick"));
        assertEquals("BRN", this.getMetaphone().metaphone("brown"));
        assertEquals("FKS", this.getMetaphone().metaphone("fox"));
        assertEquals("JMPT", this.getMetaphone().metaphone("jumped"));
        assertEquals("OFR", this.getMetaphone().metaphone("over"));
        assertEquals("0", this.getMetaphone().metaphone("the"));
        assertEquals("LS", this.getMetaphone().metaphone("lazy"));
        assertEquals("TKS", this.getMetaphone().metaphone("dogs"));
    }

    public void validateFixture(String[][] pairs) {
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
