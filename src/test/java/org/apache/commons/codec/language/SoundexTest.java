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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Soundex}.
 *
 * <p>Keep this file in UTF-8 encoding for proper Javadoc processing.</p>
 *
 */
public class SoundexTest extends StringEncoderAbstractTest<Soundex> {

    @Override
    protected Soundex createStringEncoder() {
        return new Soundex();
    }

    @Test
    public void testB650() throws EncoderException {
        this.checkEncodingVariations("B650", new String[]{
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
            "BYRUM"});
    }

    @Test
    public void testBadCharacters() {
        assertEquals("H452", this.getStringEncoder().encode("HOL>MES"));

    }

    @Test
    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, this.getStringEncoder().difference(null, null));
        assertEquals(0, this.getStringEncoder().difference("", ""));
        assertEquals(0, this.getStringEncoder().difference(" ", " "));
        // Normal cases
        assertEquals(4, this.getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(2, this.getStringEncoder().difference("Ann", "Andrew"));
        assertEquals(1, this.getStringEncoder().difference("Margaret", "Andrew"));
        assertEquals(0, this.getStringEncoder().difference("Janet", "Margaret"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(4, this.getStringEncoder().difference("Green", "Greene"));
        assertEquals(0, this.getStringEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(4, this.getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(4, this.getStringEncoder().difference("Smithers", "Smythers"));
        assertEquals(2, this.getStringEncoder().difference("Anothers", "Brothers"));
    }

    @Test
    public void testEncodeBasic() {
        assertEquals("T235", this.getStringEncoder().encode("testing"));
        assertEquals("T000", this.getStringEncoder().encode("The"));
        assertEquals("Q200", this.getStringEncoder().encode("quick"));
        assertEquals("B650", this.getStringEncoder().encode("brown"));
        assertEquals("F200", this.getStringEncoder().encode("fox"));
        assertEquals("J513", this.getStringEncoder().encode("jumped"));
        assertEquals("O160", this.getStringEncoder().encode("over"));
        assertEquals("T000", this.getStringEncoder().encode("the"));
        assertEquals("L200", this.getStringEncoder().encode("lazy"));
        assertEquals("D200", this.getStringEncoder().encode("dogs"));
    }

    /**
     * Examples from http://www.bradandkathy.com/genealogy/overviewofsoundex.html
     */
    @Test
    public void testEncodeBatch2() {
        assertEquals("A462", this.getStringEncoder().encode("Allricht"));
        assertEquals("E166", this.getStringEncoder().encode("Eberhard"));
        assertEquals("E521", this.getStringEncoder().encode("Engebrethson"));
        assertEquals("H512", this.getStringEncoder().encode("Heimbach"));
        assertEquals("H524", this.getStringEncoder().encode("Hanselmann"));
        assertEquals("H431", this.getStringEncoder().encode("Hildebrand"));
        assertEquals("K152", this.getStringEncoder().encode("Kavanagh"));
        assertEquals("L530", this.getStringEncoder().encode("Lind"));
        assertEquals("L222", this.getStringEncoder().encode("Lukaschowsky"));
        assertEquals("M235", this.getStringEncoder().encode("McDonnell"));
        assertEquals("M200", this.getStringEncoder().encode("McGee"));
        assertEquals("O155", this.getStringEncoder().encode("Opnian"));
        assertEquals("O155", this.getStringEncoder().encode("Oppenheimer"));
        assertEquals("R355", this.getStringEncoder().encode("Riedemanas"));
        assertEquals("Z300", this.getStringEncoder().encode("Zita"));
        assertEquals("Z325", this.getStringEncoder().encode("Zitzmeinn"));
    }

    /**
     * Examples from http://www.archives.gov/research_room/genealogy/census/soundex.html
     */
    @Test
    public void testEncodeBatch3() {
        assertEquals("W252", this.getStringEncoder().encode("Washington"));
        assertEquals("L000", this.getStringEncoder().encode("Lee"));
        assertEquals("G362", this.getStringEncoder().encode("Gutierrez"));
        assertEquals("P236", this.getStringEncoder().encode("Pfister"));
        assertEquals("J250", this.getStringEncoder().encode("Jackson"));
        assertEquals("T522", this.getStringEncoder().encode("Tymczak"));
        // For VanDeusen: D-250 (D, 2 for the S, 5 for the N, 0 added) is also
        // possible.
        assertEquals("V532", this.getStringEncoder().encode("VanDeusen"));
    }

    /**
     * Examples from: http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testEncodeBatch4() {
        assertEquals("H452", this.getStringEncoder().encode("HOLMES"));
        assertEquals("A355", this.getStringEncoder().encode("ADOMOMI"));
        assertEquals("V536", this.getStringEncoder().encode("VONDERLEHR"));
        assertEquals("B400", this.getStringEncoder().encode("BALL"));
        assertEquals("S000", this.getStringEncoder().encode("SHAW"));
        assertEquals("J250", this.getStringEncoder().encode("JACKSON"));
        assertEquals("S545", this.getStringEncoder().encode("SCANLON"));
        assertEquals("S532", this.getStringEncoder().encode("SAINTJOHN"));

    }

    @Test
    public void testEncodeIgnoreApostrophes() throws EncoderException {
        this.checkEncodingVariations("O165", new String[]{
            "OBrien",
            "'OBrien",
            "O'Brien",
            "OB'rien",
            "OBr'ien",
            "OBri'en",
            "OBrie'n",
            "OBrien'"});
    }

    /**
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testEncodeIgnoreHyphens() throws EncoderException {
        this.checkEncodingVariations("K525", new String[]{
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
            "KINGSMITH-"});
    }

    @Test
    public void testEncodeIgnoreTrimmable() {
        assertEquals("W252", this.getStringEncoder().encode(" \t\n\r Washington \t\n\r "));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     */
    @Test
    public void testHWRuleEx1() {
        // From
        // http://www.archives.gov/research_room/genealogy/census/soundex.html:
        // Ashcraft is coded A-261 (A, 2 for the S, C ignored, 6 for the R, 1
        // for the F). It is not coded A-226.
        assertEquals("A261", this.getStringEncoder().encode("Ashcraft"));
        assertEquals("A261", this.getStringEncoder().encode("Ashcroft"));
        assertEquals("Y330", this.getStringEncoder().encode("yehudit"));
        assertEquals("Y330", this.getStringEncoder().encode("yhwdyt"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testHWRuleEx2() {
        assertEquals("B312", this.getStringEncoder().encode("BOOTHDAVIS"));
        assertEquals("B312", this.getStringEncoder().encode("BOOTH-DAVIS"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testHWRuleEx3() throws EncoderException {
        assertEquals("S460", this.getStringEncoder().encode("Sgler"));
        assertEquals("S460", this.getStringEncoder().encode("Swhgler"));
        // Also S460:
        this.checkEncodingVariations("S460", new String[]{
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
            "SILLER"});
    }

    /**
     * Examples for MS SQLServer from
     * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
     */
    @Test
    public void testMsSqlServer1() {
        assertEquals("S530", this.getStringEncoder().encode("Smith"));
        assertEquals("S530", this.getStringEncoder().encode("Smythe"));
    }

    /**
     * Examples for MS SQLServer from
     * http://support.microsoft.com/default.aspx?scid=http://support.microsoft.com:80/support
     * /kb/articles/Q100/3/65.asp&NoWebContent=1
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testMsSqlServer2() throws EncoderException {
        this.checkEncodingVariations("E625", new String[]{"Erickson", "Erickson", "Erikson", "Ericson", "Ericksen", "Ericsen"});
    }

    /**
     * Examples for MS SQLServer from http://databases.about.com/library/weekly/aa042901a.htm
     */
    @Test
    public void testMsSqlServer3() {
        assertEquals("A500", this.getStringEncoder().encode("Ann"));
        assertEquals("A536", this.getStringEncoder().encode("Andrew"));
        assertEquals("J530", this.getStringEncoder().encode("Janet"));
        assertEquals("M626", this.getStringEncoder().encode("Margaret"));
        assertEquals("S315", this.getStringEncoder().encode("Steven"));
        assertEquals("M240", this.getStringEncoder().encode("Michael"));
        assertEquals("R163", this.getStringEncoder().encode("Robert"));
        assertEquals("L600", this.getStringEncoder().encode("Laura"));
        assertEquals("A500", this.getStringEncoder().encode("Anne"));
    }

    /**
     * https://issues.apache.org/jira/browse/CODEC-54 https://issues.apache.org/jira/browse/CODEC-56
     */
    @Test
    public void testNewInstance() {
        assertEquals("W452", new Soundex().soundex("Williams"));
    }

    @Test
    public void testNewInstance2() {
        assertEquals("W452", new Soundex(Soundex.US_ENGLISH_MAPPING_STRING.toCharArray()).soundex("Williams"));
    }

    @Test
    public void testNewInstance3() {
        assertEquals("W452", new Soundex(Soundex.US_ENGLISH_MAPPING_STRING).soundex("Williams"));
    }

    @Test
    public void testSoundexUtilsConstructable() {
        new SoundexUtils();
    }

    @Test
    public void testSoundexUtilsNullBehaviour() {
        assertNull(SoundexUtils.clean(null));
        assertEquals("", SoundexUtils.clean(""));
        assertEquals(0, SoundexUtils.differenceEncoded(null, ""));
        assertEquals(0, SoundexUtils.differenceEncoded("", null));
    }

    /**
     * https://issues.apache.org/jira/browse/CODEC-54 https://issues.apache.org/jira/browse/CODEC-56
     */
    @Test
    public void testUsEnglishStatic() {
        assertEquals("W452", Soundex.US_ENGLISH.soundex("Williams"));
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * https://issues.apache.org/jira/browse/CODEC-30
     */
    @Test
    public void testUsMappingEWithAcute() {
        assertEquals("E000", this.getStringEncoder().encode("e"));
        if (Character.isLetter('\u00e9')) { // e-acute
            //         uppercase E-acute
            assertThrows(IllegalArgumentException.class, () -> getStringEncoder().encode("\u00e9"));
        } else {
            assertEquals("", this.getStringEncoder().encode("\u00e9"));
        }
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * https://issues.apache.org/jira/browse/CODEC-30
     */
    @Test
    public void testUsMappingOWithDiaeresis() {
        assertEquals("O000", this.getStringEncoder().encode("o"));
        if (Character.isLetter('\u00f6')) { // o-umlaut
            //         uppercase O-umlaut
            assertThrows(IllegalArgumentException.class, () -> getStringEncoder().encode("\u00f6"));
        } else {
            assertEquals("", this.getStringEncoder().encode("\u00f6"));
        }
    }

    /**
     * Tests example from http://en.wikipedia.org/wiki/Soundex#American_Soundex as of 2015-03-22.
     */
    @Test
    public void testWikipediaAmericanSoundex() {
        assertEquals("R163", this.getStringEncoder().encode("Robert"));
        assertEquals("R163", this.getStringEncoder().encode("Rupert"));
        assertEquals("A261", this.getStringEncoder().encode("Ashcraft"));
        assertEquals("A261", this.getStringEncoder().encode("Ashcroft"));
        assertEquals("T522", this.getStringEncoder().encode("Tymczak"));
        assertEquals("P236", this.getStringEncoder().encode("Pfister"));
    }

    @Test
// examples and algorithm rules from:  http://www.genealogy.com/articles/research/00000060.html
    public void testGenealogy() { // treat vowels and HW as silent
        final Soundex s = Soundex.US_ENGLISH_GENEALOGY;
        assertEquals("H251", s.encode("Heggenburger"));
        assertEquals("B425", s.encode("Blackman"));
        assertEquals("S530", s.encode("Schmidt"));
        assertEquals("L150", s.encode("Lippmann"));
        // Additional local example
        assertEquals("D200", s.encode("Dodds")); // 'o' is not a separator here - it is silent
        assertEquals("D200", s.encode("Dhdds")); // 'h' is silent
        assertEquals("D200", s.encode("Dwdds")); // 'w' is silent
    }

    @Test
// examples and algorithm rules from:  http://west-penwith.org.uk/misc/soundex.htm
    public void testSimplifiedSoundex() { // treat vowels and HW as separators
        final Soundex s = Soundex.US_ENGLISH_SIMPLIFIED;
        assertEquals("W452", s.encode("WILLIAMS"));
        assertEquals("B625", s.encode("BARAGWANATH"));
        assertEquals("D540", s.encode("DONNELL"));
        assertEquals("L300", s.encode("LLOYD"));
        assertEquals("W422", s.encode("WOOLCOCK"));
        // Additional local examples
        assertEquals("D320", s.encode("Dodds"));
        assertEquals("D320", s.encode("Dwdds")); // w is a separator
        assertEquals("D320", s.encode("Dhdds")); // h is a separator
    }
}
