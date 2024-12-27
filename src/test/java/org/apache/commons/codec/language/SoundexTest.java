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

// (FYI: Formatted and sorted with Eclipse)

package org.apache.commons.codec.language;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Soundex}.
 *
 * <p>Keep this file in UTF-8 encoding for proper Javadoc processing.</p>
 */
public class SoundexTest extends AbstractStringEncoderTest<Soundex> {

    @Override
    protected Soundex createStringEncoder() {
        return new Soundex();
    }

    @Test
    public void testB650() throws EncoderException {
        checkEncodingVariations("B650", new String[]{
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
        assertEquals("H452", getStringEncoder().encode("HOL>MES"));

    }

    @Test
    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, getStringEncoder().difference(null, null));
        assertEquals(0, getStringEncoder().difference("", ""));
        assertEquals(0, getStringEncoder().difference(" ", " "));
        // Normal cases
        assertEquals(4, getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(2, getStringEncoder().difference("Ann", "Andrew"));
        assertEquals(1, getStringEncoder().difference("Margaret", "Andrew"));
        assertEquals(0, getStringEncoder().difference("Janet", "Margaret"));
        // Examples from https://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(4, getStringEncoder().difference("Green", "Greene"));
        assertEquals(0, getStringEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from https://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(4, getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(4, getStringEncoder().difference("Smithers", "Smythers"));
        assertEquals(2, getStringEncoder().difference("Anothers", "Brothers"));
    }

    @Test
    public void testEncodeBasic() {
        assertEquals("T235", getStringEncoder().encode("testing"));
        assertEquals("T000", getStringEncoder().encode("The"));
        assertEquals("Q200", getStringEncoder().encode("quick"));
        assertEquals("B650", getStringEncoder().encode("brown"));
        assertEquals("F200", getStringEncoder().encode("fox"));
        assertEquals("J513", getStringEncoder().encode("jumped"));
        assertEquals("O160", getStringEncoder().encode("over"));
        assertEquals("T000", getStringEncoder().encode("the"));
        assertEquals("L200", getStringEncoder().encode("lazy"));
        assertEquals("D200", getStringEncoder().encode("dogs"));
    }

    /**
     * Examples from http://www.bradandkathy.com/genealogy/overviewofsoundex.html
     */
    @Test
    public void testEncodeBatch2() {
        assertEquals("A462", getStringEncoder().encode("Allricht"));
        assertEquals("E166", getStringEncoder().encode("Eberhard"));
        assertEquals("E521", getStringEncoder().encode("Engebrethson"));
        assertEquals("H512", getStringEncoder().encode("Heimbach"));
        assertEquals("H524", getStringEncoder().encode("Hanselmann"));
        assertEquals("H431", getStringEncoder().encode("Hildebrand"));
        assertEquals("K152", getStringEncoder().encode("Kavanagh"));
        assertEquals("L530", getStringEncoder().encode("Lind"));
        assertEquals("L222", getStringEncoder().encode("Lukaschowsky"));
        assertEquals("M235", getStringEncoder().encode("McDonnell"));
        assertEquals("M200", getStringEncoder().encode("McGee"));
        assertEquals("O155", getStringEncoder().encode("Opnian"));
        assertEquals("O155", getStringEncoder().encode("Oppenheimer"));
        assertEquals("R355", getStringEncoder().encode("Riedemanas"));
        assertEquals("Z300", getStringEncoder().encode("Zita"));
        assertEquals("Z325", getStringEncoder().encode("Zitzmeinn"));
    }

    /**
     * Examples from http://www.archives.gov/research_room/genealogy/census/soundex.html
     */
    @Test
    public void testEncodeBatch3() {
        assertEquals("W252", getStringEncoder().encode("Washington"));
        assertEquals("L000", getStringEncoder().encode("Lee"));
        assertEquals("G362", getStringEncoder().encode("Gutierrez"));
        assertEquals("P236", getStringEncoder().encode("Pfister"));
        assertEquals("J250", getStringEncoder().encode("Jackson"));
        assertEquals("T522", getStringEncoder().encode("Tymczak"));
        // For VanDeusen: D-250 (D, 2 for the S, 5 for the N, 0 added) is also
        // possible.
        assertEquals("V532", getStringEncoder().encode("VanDeusen"));
    }

    /**
     * Examples from: http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testEncodeBatch4() {
        assertEquals("H452", getStringEncoder().encode("HOLMES"));
        assertEquals("A355", getStringEncoder().encode("ADOMOMI"));
        assertEquals("V536", getStringEncoder().encode("VONDERLEHR"));
        assertEquals("B400", getStringEncoder().encode("BALL"));
        assertEquals("S000", getStringEncoder().encode("SHAW"));
        assertEquals("J250", getStringEncoder().encode("JACKSON"));
        assertEquals("S545", getStringEncoder().encode("SCANLON"));
        assertEquals("S532", getStringEncoder().encode("SAINTJOHN"));

    }

    @Test
    public void testEncodeIgnoreApostrophes() throws EncoderException {
        checkEncodingVariations("O165", new String[]{
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
        checkEncodingVariations("K525", new String[]{
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
        assertEquals("W252", getStringEncoder().encode(" \t\n\r Washington \t\n\r "));
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

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     */
    @Test
    public void testHWRuleEx1() {
        // From
        // http://www.archives.gov/research_room/genealogy/census/soundex.html:
        // Ashcraft is coded A-261 (A, 2 for the S, C ignored, 6 for the R, 1
        // for the F). It is not coded A-226.
        assertEquals("A261", getStringEncoder().encode("Ashcraft"));
        assertEquals("A261", getStringEncoder().encode("Ashcroft"));
        assertEquals("Y330", getStringEncoder().encode("yehudit"));
        assertEquals("Y330", getStringEncoder().encode("yhwdyt"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testHWRuleEx2() {
        assertEquals("B312", getStringEncoder().encode("BOOTHDAVIS"));
        assertEquals("B312", getStringEncoder().encode("BOOTH-DAVIS"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testHWRuleEx3() throws EncoderException {
        assertEquals("S460", getStringEncoder().encode("Sgler"));
        assertEquals("S460", getStringEncoder().encode("Swhgler"));
        // Also S460:
        checkEncodingVariations("S460", new String[]{
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
     * https://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
     */
    @Test
    public void testMsSqlServer1() {
        assertEquals("S530", getStringEncoder().encode("Smith"));
        assertEquals("S530", getStringEncoder().encode("Smythe"));
    }

    /**
     * Examples for MS SQLServer from
     * https://support.microsoft.com/default.aspx?scid=https://support.microsoft.com:80/support
     * /kb/articles/Q100/3/65.asp&NoWebContent=1
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testMsSqlServer2() throws EncoderException {
        checkEncodingVariations("E625", new String[]{"Erickson", "Erickson", "Erikson", "Ericson", "Ericksen", "Ericsen"});
    }

    /**
     * Examples for MS SQLServer from https://databases.about.com/library/weekly/aa042901a.htm
     */
    @Test
    public void testMsSqlServer3() {
        assertEquals("A500", getStringEncoder().encode("Ann"));
        assertEquals("A536", getStringEncoder().encode("Andrew"));
        assertEquals("J530", getStringEncoder().encode("Janet"));
        assertEquals("M626", getStringEncoder().encode("Margaret"));
        assertEquals("S315", getStringEncoder().encode("Steven"));
        assertEquals("M240", getStringEncoder().encode("Michael"));
        assertEquals("R163", getStringEncoder().encode("Robert"));
        assertEquals("L600", getStringEncoder().encode("Laura"));
        assertEquals("A500", getStringEncoder().encode("Anne"));
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
        assertEquals("E000", getStringEncoder().encode("e"));
        if (Character.isLetter('\u00e9')) { // e-acute
            //         uppercase E-acute
            assertThrows(IllegalArgumentException.class, () -> getStringEncoder().encode("\u00e9"));
        } else {
            assertEquals("", getStringEncoder().encode("\u00e9"));
        }
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * https://issues.apache.org/jira/browse/CODEC-30
     */
    @Test
    public void testUsMappingOWithDiaeresis() {
        assertEquals("O000", getStringEncoder().encode("o"));
        if (Character.isLetter('\u00f6')) { // o-umlaut
            //         uppercase O-umlaut
            assertThrows(IllegalArgumentException.class, () -> getStringEncoder().encode("\u00f6"));
        } else {
            assertEquals("", getStringEncoder().encode("\u00f6"));
        }
    }

    /**
     * Tests example from https://en.wikipedia.org/wiki/Soundex#American_Soundex as of 2015-03-22.
     */
    @Test
    public void testWikipediaAmericanSoundex() {
        assertEquals("R163", getStringEncoder().encode("Robert"));
        assertEquals("R163", getStringEncoder().encode("Rupert"));
        assertEquals("A261", getStringEncoder().encode("Ashcraft"));
        assertEquals("A261", getStringEncoder().encode("Ashcroft"));
        assertEquals("T522", getStringEncoder().encode("Tymczak"));
        assertEquals("P236", getStringEncoder().encode("Pfister"));
    }
}
