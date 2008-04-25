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

// (FYI: Formatted and sorted with Eclipse)
package org.apache.commons.codec.language;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * Tests {@link Soundex}
 *
 * @author Apache Software Foundation
 * @version $Id$
 */
public class SoundexTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return new TestSuite(SoundexTest.class);
    }

    private Soundex encoder = null;

    public SoundexTest(String name) {
        super(name);
    }

    void encodeAll(String[] strings, String expectedEncoding) {
        for (int i = 0; i < strings.length; i++) {
            assertEquals(expectedEncoding, this.getEncoder().encode(strings[i]));
        }
    }

    /**
	 * @return Returns the _encoder.
	 */
    public Soundex getEncoder() {
        return this.encoder;
    }

    protected StringEncoder makeEncoder() {
        return new Soundex();
    }

    /**
	 * @param encoder
	 *                  The encoder to set.
	 */
    public void setEncoder(Soundex encoder) {
        this.encoder = encoder;
    }

    public void setUp() throws Exception {
        super.setUp();
        this.setEncoder(new Soundex());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        this.setEncoder(null);
    }

    public void testB650() {
        this.encodeAll(
            new String[] {
                "BARHAM",
                "BARONE",
                "BARRON",
                "BERNA",
                "BIRNEY",
                "BIRNIE",
                "BOOROM",
                "BOREN",
                "BORN",
                "BOURN",
                "BOURNE",
                "BOWRON",
                "BRAIN",
                "BRAME",
                "BRANN",
                "BRAUN",
                "BREEN",
                "BRIEN",
                "BRIM",
                "BRIMM",
                "BRINN",
                "BRION",
                "BROOM",
                "BROOME",
                "BROWN",
                "BROWNE",
                "BRUEN",
                "BRUHN",
                "BRUIN",
                "BRUMM",
                "BRUN",
                "BRUNO",
                "BRYAN",
                "BURIAN",
                "BURN",
                "BURNEY",
                "BYRAM",
                "BYRNE",
                "BYRON",
                "BYRUM" },
            "B650");
    }

    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, this.getEncoder().difference(null, null));
        assertEquals(0, this.getEncoder().difference("", ""));
        assertEquals(0, this.getEncoder().difference(" ", " "));
        // Normal cases
        assertEquals(4, this.getEncoder().difference("Smith", "Smythe"));
        assertEquals(2, this.getEncoder().difference("Ann", "Andrew"));
        assertEquals(1, this.getEncoder().difference("Margaret", "Andrew"));
        assertEquals(0, this.getEncoder().difference("Janet", "Margaret"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(4, this.getEncoder().difference("Green", "Greene"));
        assertEquals(0, this.getEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(4, this.getEncoder().difference("Smith", "Smythe"));
        assertEquals(4, this.getEncoder().difference("Smithers", "Smythers"));
        assertEquals(2, this.getEncoder().difference("Anothers", "Brothers"));
    }

    public void testEncodeBasic() {
        assertEquals("T235", this.getEncoder().encode("testing"));
        assertEquals("T000", this.getEncoder().encode("The"));
        assertEquals("Q200", this.getEncoder().encode("quick"));
        assertEquals("B650", this.getEncoder().encode("brown"));
        assertEquals("F200", this.getEncoder().encode("fox"));
        assertEquals("J513", this.getEncoder().encode("jumped"));
        assertEquals("O160", this.getEncoder().encode("over"));
        assertEquals("T000", this.getEncoder().encode("the"));
        assertEquals("L200", this.getEncoder().encode("lazy"));
        assertEquals("D200", this.getEncoder().encode("dogs"));
    }

    /**
	 * Examples from
	 * http://www.bradandkathy.com/genealogy/overviewofsoundex.html
	 */
    public void testEncodeBatch2() {
        assertEquals("A462", this.getEncoder().encode("Allricht"));
        assertEquals("E166", this.getEncoder().encode("Eberhard"));
        assertEquals("E521", this.getEncoder().encode("Engebrethson"));
        assertEquals("H512", this.getEncoder().encode("Heimbach"));
        assertEquals("H524", this.getEncoder().encode("Hanselmann"));
        assertEquals("H431", this.getEncoder().encode("Hildebrand"));
        assertEquals("K152", this.getEncoder().encode("Kavanagh"));
        assertEquals("L530", this.getEncoder().encode("Lind"));
        assertEquals("L222", this.getEncoder().encode("Lukaschowsky"));
        assertEquals("M235", this.getEncoder().encode("McDonnell"));
        assertEquals("M200", this.getEncoder().encode("McGee"));
        assertEquals("O155", this.getEncoder().encode("Opnian"));
        assertEquals("O155", this.getEncoder().encode("Oppenheimer"));
        assertEquals("R355", this.getEncoder().encode("Riedemanas"));
        assertEquals("Z300", this.getEncoder().encode("Zita"));
        assertEquals("Z325", this.getEncoder().encode("Zitzmeinn"));
    }

    /**
	 * Examples from
	 * http://www.archives.gov/research_room/genealogy/census/soundex.html
	 */
    public void testEncodeBatch3() {
        assertEquals("W252", this.getEncoder().encode("Washington"));
        assertEquals("L000", this.getEncoder().encode("Lee"));
        assertEquals("G362", this.getEncoder().encode("Gutierrez"));
        assertEquals("P236", this.getEncoder().encode("Pfister"));
        assertEquals("J250", this.getEncoder().encode("Jackson"));
        assertEquals("T522", this.getEncoder().encode("Tymczak"));
        // For VanDeusen: D-250 (D, 2 for the S, 5 for the N, 0 added) is also
        // possible.
        assertEquals("V532", this.getEncoder().encode("VanDeusen"));
    }

    /**
	 * Examples from: http://www.myatt.demon.co.uk/sxalg.htm
	 */
    public void testEncodeBatch4() {
        assertEquals("H452", this.getEncoder().encode("HOLMES"));
        assertEquals("A355", this.getEncoder().encode("ADOMOMI"));
        assertEquals("V536", this.getEncoder().encode("VONDERLEHR"));
        assertEquals("B400", this.getEncoder().encode("BALL"));
        assertEquals("S000", this.getEncoder().encode("SHAW"));
        assertEquals("J250", this.getEncoder().encode("JACKSON"));
        assertEquals("S545", this.getEncoder().encode("SCANLON"));
        assertEquals("S532", this.getEncoder().encode("SAINTJOHN"));

    }

	public void testBadCharacters() {
		assertEquals("H452", this.getEncoder().encode("HOL>MES"));

	}

    public void testEncodeIgnoreApostrophes() {
        this.encodeAll(new String[] { "OBrien", "'OBrien", "O'Brien", "OB'rien", "OBr'ien", "OBri'en", "OBrie'n", "OBrien'" }, "O165");
    }

    /**
	 * Test data from http://www.myatt.demon.co.uk/sxalg.htm
	 */
    public void testEncodeIgnoreHyphens() {
        this.encodeAll(
            new String[] {
                "KINGSMITH",
                "-KINGSMITH",
                "K-INGSMITH",
                "KI-NGSMITH",
                "KIN-GSMITH",
                "KING-SMITH",
                "KINGS-MITH",
                "KINGSM-ITH",
                "KINGSMI-TH",
                "KINGSMIT-H",
                "KINGSMITH-" },
            "K525");
    }

    public void testEncodeIgnoreTrimmable() {
        assertEquals("W252", this.getEncoder().encode(" \t\n\r Washington \t\n\r "));
    }

    /**
	 * Consonants from the same code group separated by W or H are treated as
	 * one.
	 */
    public void testHWRuleEx1() {
        // From
        // http://www.archives.gov/research_room/genealogy/census/soundex.html:
        // Ashcraft is coded A-261 (A, 2 for the S, C ignored, 6 for the R, 1
        // for the F). It is not coded A-226.
        assertEquals("A261", this.getEncoder().encode("Ashcraft"));
    }

    /**
	 * Consonants from the same code group separated by W or H are treated as
	 * one.
	 *
	 * Test data from http://www.myatt.demon.co.uk/sxalg.htm
	 */
    public void testHWRuleEx2() {
        assertEquals("B312", this.getEncoder().encode("BOOTHDAVIS"));
        assertEquals("B312", this.getEncoder().encode("BOOTH-DAVIS"));
    }

    /**
	 * Consonants from the same code group separated by W or H are treated as
	 * one.
	 */
    public void testHWRuleEx3() {
        assertEquals("S460", this.getEncoder().encode("Sgler"));
        assertEquals("S460", this.getEncoder().encode("Swhgler"));
        // Also S460:
        this.encodeAll(
            new String[] {
                "SAILOR",
                "SALYER",
                "SAYLOR",
                "SCHALLER",
                "SCHELLER",
                "SCHILLER",
                "SCHOOLER",
                "SCHULER",
                "SCHUYLER",
                "SEILER",
                "SEYLER",
                "SHOLAR",
                "SHULER",
                "SILAR",
                "SILER",
                "SILLER" },
            "S460");
    }

    public void testMaxLength() throws Exception {
        Soundex soundex = new Soundex();
        soundex.setMaxLength(soundex.getMaxLength());
        assertEquals("S460", this.getEncoder().encode("Sgler"));
    }

    public void testMaxLengthLessThan3Fix() throws Exception {
        Soundex soundex = new Soundex();
        soundex.setMaxLength(2);
        assertEquals("S460", soundex.encode("SCHELLER"));
    }

    /**
     * Examples for MS SQLServer from
     * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
     */
    public void testMsSqlServer1() {
        assertEquals("S530", this.getEncoder().encode("Smith"));
        assertEquals("S530", this.getEncoder().encode("Smythe"));
    }

    /**
     * Examples for MS SQLServer from
     * http://support.microsoft.com/default.aspx?scid=http://support.microsoft.com:80/support/kb/articles/Q100/3/65.asp&NoWebContent=1
     */
    public void testMsSqlServer2() {
        this.encodeAll(new String[]{"Erickson", "Erickson", "Erikson", "Ericson", "Ericksen", "Ericsen"}, "E625");
    }
    /**
     * Examples for MS SQLServer from
     * http://databases.about.com/library/weekly/aa042901a.htm
     */
    public void testMsSqlServer3() {
        assertEquals("A500", this.getEncoder().encode("Ann"));
        assertEquals("A536", this.getEncoder().encode("Andrew"));
        assertEquals("J530", this.getEncoder().encode("Janet"));
        assertEquals("M626", this.getEncoder().encode("Margaret"));
        assertEquals("S315", this.getEncoder().encode("Steven"));
        assertEquals("M240", this.getEncoder().encode("Michael"));
        assertEquals("R163", this.getEncoder().encode("Robert"));
        assertEquals("L600", this.getEncoder().encode("Laura"));
        assertEquals("A500", this.getEncoder().encode("Anne"));
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * http://issues.apache.org/bugzilla/show_bug.cgi?id=29080
     */
    public void testUsMappingOWithDiaeresis() {
        assertEquals("O000", this.getEncoder().encode("o"));
        if ( Character.isLetter('ö') ) {
            try {
                assertEquals("Ö000", this.getEncoder().encode("ö"));
                fail("Expected IllegalArgumentException not thrown");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } else {
            assertEquals("", this.getEncoder().encode("ö"));
        }
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * http://issues.apache.org/bugzilla/show_bug.cgi?id=29080
     */
    public void testUsMappingEWithAcute() {
        assertEquals("E000", this.getEncoder().encode("e"));
        if ( Character.isLetter('é') ) {
            try {
                assertEquals("É000", this.getEncoder().encode("é"));
                fail("Expected IllegalArgumentException not thrown");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } else {
            assertEquals("", this.getEncoder().encode("é"));
        }
    }
    
    /**
     * https://issues.apache.org/jira/browse/CODEC-54
     * https://issues.apache.org/jira/browse/CODEC-56
     */
    public void testUsEnglishStatic() {
        assertEquals(Soundex.US_ENGLISH.soundex("Williams"), "W452");
    }

    /**
     * https://issues.apache.org/jira/browse/CODEC-54
     * https://issues.apache.org/jira/browse/CODEC-56
     */
    public void testNewInstance() {
        assertEquals(new Soundex().soundex("Williams"), "W452");
    }
}
