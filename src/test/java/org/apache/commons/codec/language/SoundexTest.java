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
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals("H452", this.getStringEncoder().encode("HOL>MES"));

    }

    @Test
    public void testDifference() throws EncoderException {
        // Edge cases
        Assert.assertEquals(0, this.getStringEncoder().difference(null, null));
        Assert.assertEquals(0, this.getStringEncoder().difference("", ""));
        Assert.assertEquals(0, this.getStringEncoder().difference(" ", " "));
        // Normal cases
        Assert.assertEquals(4, this.getStringEncoder().difference("Smith", "Smythe"));
        Assert.assertEquals(2, this.getStringEncoder().difference("Ann", "Andrew"));
        Assert.assertEquals(1, this.getStringEncoder().difference("Margaret", "Andrew"));
        Assert.assertEquals(0, this.getStringEncoder().difference("Janet", "Margaret"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        Assert.assertEquals(4, this.getStringEncoder().difference("Green", "Greene"));
        Assert.assertEquals(0, this.getStringEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        Assert.assertEquals(4, this.getStringEncoder().difference("Smith", "Smythe"));
        Assert.assertEquals(4, this.getStringEncoder().difference("Smithers", "Smythers"));
        Assert.assertEquals(2, this.getStringEncoder().difference("Anothers", "Brothers"));
    }

    @Test
    public void testEncodeBasic() {
        Assert.assertEquals("T235", this.getStringEncoder().encode("testing"));
        Assert.assertEquals("T000", this.getStringEncoder().encode("The"));
        Assert.assertEquals("Q200", this.getStringEncoder().encode("quick"));
        Assert.assertEquals("B650", this.getStringEncoder().encode("brown"));
        Assert.assertEquals("F200", this.getStringEncoder().encode("fox"));
        Assert.assertEquals("J513", this.getStringEncoder().encode("jumped"));
        Assert.assertEquals("O160", this.getStringEncoder().encode("over"));
        Assert.assertEquals("T000", this.getStringEncoder().encode("the"));
        Assert.assertEquals("L200", this.getStringEncoder().encode("lazy"));
        Assert.assertEquals("D200", this.getStringEncoder().encode("dogs"));
    }

    /**
     * Examples from http://www.bradandkathy.com/genealogy/overviewofsoundex.html
     */
    @Test
    public void testEncodeBatch2() {
        Assert.assertEquals("A462", this.getStringEncoder().encode("Allricht"));
        Assert.assertEquals("E166", this.getStringEncoder().encode("Eberhard"));
        Assert.assertEquals("E521", this.getStringEncoder().encode("Engebrethson"));
        Assert.assertEquals("H512", this.getStringEncoder().encode("Heimbach"));
        Assert.assertEquals("H524", this.getStringEncoder().encode("Hanselmann"));
        Assert.assertEquals("H431", this.getStringEncoder().encode("Hildebrand"));
        Assert.assertEquals("K152", this.getStringEncoder().encode("Kavanagh"));
        Assert.assertEquals("L530", this.getStringEncoder().encode("Lind"));
        Assert.assertEquals("L222", this.getStringEncoder().encode("Lukaschowsky"));
        Assert.assertEquals("M235", this.getStringEncoder().encode("McDonnell"));
        Assert.assertEquals("M200", this.getStringEncoder().encode("McGee"));
        Assert.assertEquals("O155", this.getStringEncoder().encode("Opnian"));
        Assert.assertEquals("O155", this.getStringEncoder().encode("Oppenheimer"));
        Assert.assertEquals("R355", this.getStringEncoder().encode("Riedemanas"));
        Assert.assertEquals("Z300", this.getStringEncoder().encode("Zita"));
        Assert.assertEquals("Z325", this.getStringEncoder().encode("Zitzmeinn"));
    }

    /**
     * Examples from http://www.archives.gov/research_room/genealogy/census/soundex.html
     */
    @Test
    public void testEncodeBatch3() {
        Assert.assertEquals("W252", this.getStringEncoder().encode("Washington"));
        Assert.assertEquals("L000", this.getStringEncoder().encode("Lee"));
        Assert.assertEquals("G362", this.getStringEncoder().encode("Gutierrez"));
        Assert.assertEquals("P236", this.getStringEncoder().encode("Pfister"));
        Assert.assertEquals("J250", this.getStringEncoder().encode("Jackson"));
        Assert.assertEquals("T522", this.getStringEncoder().encode("Tymczak"));
        // For VanDeusen: D-250 (D, 2 for the S, 5 for the N, 0 added) is also
        // possible.
        Assert.assertEquals("V532", this.getStringEncoder().encode("VanDeusen"));
    }

    /**
     * Examples from: http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testEncodeBatch4() {
        Assert.assertEquals("H452", this.getStringEncoder().encode("HOLMES"));
        Assert.assertEquals("A355", this.getStringEncoder().encode("ADOMOMI"));
        Assert.assertEquals("V536", this.getStringEncoder().encode("VONDERLEHR"));
        Assert.assertEquals("B400", this.getStringEncoder().encode("BALL"));
        Assert.assertEquals("S000", this.getStringEncoder().encode("SHAW"));
        Assert.assertEquals("J250", this.getStringEncoder().encode("JACKSON"));
        Assert.assertEquals("S545", this.getStringEncoder().encode("SCANLON"));
        Assert.assertEquals("S532", this.getStringEncoder().encode("SAINTJOHN"));

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
        Assert.assertEquals("W252", this.getStringEncoder().encode(" \t\n\r Washington \t\n\r "));
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
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcraft"));
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcroft"));
        Assert.assertEquals("Y330", this.getStringEncoder().encode("yehudit"));
        Assert.assertEquals("Y330", this.getStringEncoder().encode("yhwdyt"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     */
    @Test
    public void testHWRuleEx2() {
        Assert.assertEquals("B312", this.getStringEncoder().encode("BOOTHDAVIS"));
        Assert.assertEquals("B312", this.getStringEncoder().encode("BOOTH-DAVIS"));
    }

    /**
     * Consonants from the same code group separated by W or H are treated as one.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testHWRuleEx3() throws EncoderException {
        Assert.assertEquals("S460", this.getStringEncoder().encode("Sgler"));
        Assert.assertEquals("S460", this.getStringEncoder().encode("Swhgler"));
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
        Assert.assertEquals("S530", this.getStringEncoder().encode("Smith"));
        Assert.assertEquals("S530", this.getStringEncoder().encode("Smythe"));
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
        Assert.assertEquals("A500", this.getStringEncoder().encode("Ann"));
        Assert.assertEquals("A536", this.getStringEncoder().encode("Andrew"));
        Assert.assertEquals("J530", this.getStringEncoder().encode("Janet"));
        Assert.assertEquals("M626", this.getStringEncoder().encode("Margaret"));
        Assert.assertEquals("S315", this.getStringEncoder().encode("Steven"));
        Assert.assertEquals("M240", this.getStringEncoder().encode("Michael"));
        Assert.assertEquals("R163", this.getStringEncoder().encode("Robert"));
        Assert.assertEquals("L600", this.getStringEncoder().encode("Laura"));
        Assert.assertEquals("A500", this.getStringEncoder().encode("Anne"));
    }

    /**
     * https://issues.apache.org/jira/browse/CODEC-54 https://issues.apache.org/jira/browse/CODEC-56
     */
    @Test
    public void testNewInstance() {
        Assert.assertEquals("W452", new Soundex().soundex("Williams"));
    }

    @Test
    public void testNewInstance2() {
        Assert.assertEquals("W452", new Soundex(Soundex.US_ENGLISH_MAPPING_STRING.toCharArray()).soundex("Williams"));
    }

    @Test
    public void testNewInstance3() {
        Assert.assertEquals("W452", new Soundex(Soundex.US_ENGLISH_MAPPING_STRING).soundex("Williams"));
    }

    @Test
    public void testSoundexUtilsConstructable() {
        new SoundexUtils();
    }

    @Test
    public void testSoundexUtilsNullBehaviour() {
        Assert.assertEquals(null, SoundexUtils.clean(null));
        Assert.assertEquals("", SoundexUtils.clean(""));
        Assert.assertEquals(0, SoundexUtils.differenceEncoded(null, ""));
        Assert.assertEquals(0, SoundexUtils.differenceEncoded("", null));
    }

    /**
     * https://issues.apache.org/jira/browse/CODEC-54 https://issues.apache.org/jira/browse/CODEC-56
     */
    @Test
    public void testUsEnglishStatic() {
        Assert.assertEquals("W452", Soundex.US_ENGLISH.soundex("Williams"));
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * https://issues.apache.org/jira/browse/CODEC-30
     */
    @Test
    public void testUsMappingEWithAcute() {
        Assert.assertEquals("E000", this.getStringEncoder().encode("e"));
        if (Character.isLetter('\u00e9')) { // e-acute
            try {
                //         uppercase E-acute
                Assert.assertEquals("\u00c9000", this.getStringEncoder().encode("\u00e9"));
                Assert.fail("Expected IllegalArgumentException not thrown");
            } catch (final IllegalArgumentException e) {
                // expected
            }
        } else {
            Assert.assertEquals("", this.getStringEncoder().encode("\u00e9"));
        }
    }

    /**
     * Fancy characters are not mapped by the default US mapping.
     *
     * https://issues.apache.org/jira/browse/CODEC-30
     */
    @Test
    public void testUsMappingOWithDiaeresis() {
        Assert.assertEquals("O000", this.getStringEncoder().encode("o"));
        if (Character.isLetter('\u00f6')) { // o-umlaut
            try {
                //         uppercase O-umlaut
                Assert.assertEquals("\u00d6000", this.getStringEncoder().encode("\u00f6"));
                Assert.fail("Expected IllegalArgumentException not thrown");
            } catch (final IllegalArgumentException e) {
                // expected
            }
        } else {
            Assert.assertEquals("", this.getStringEncoder().encode("\u00f6"));
        }
    }

    /**
     * Tests example from http://en.wikipedia.org/wiki/Soundex#American_Soundex as of 2015-03-22.
     */
    @Test
    public void testWikipediaAmericanSoundex() {
        Assert.assertEquals("R163", this.getStringEncoder().encode("Robert"));
        Assert.assertEquals("R163", this.getStringEncoder().encode("Rupert"));
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcraft"));
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcroft"));
        Assert.assertEquals("T522", this.getStringEncoder().encode("Tymczak"));
        Assert.assertEquals("P236", this.getStringEncoder().encode("Pfister"));
    }

    @Test
// examples and algorithm rules from:  http://www.genealogy.com/articles/research/00000060.html
    public void testGenealogy() { // treat vowels and HW as silent
        final Soundex s = Soundex.US_ENGLISH_GENEALOGY;
        Assert.assertEquals("H251", s.encode("Heggenburger"));
        Assert.assertEquals("B425", s.encode("Blackman"));
        Assert.assertEquals("S530", s.encode("Schmidt"));
        Assert.assertEquals("L150", s.encode("Lippmann"));
        // Additional local example
        Assert.assertEquals("D200", s.encode("Dodds")); // 'o' is not a separator here - it is silent
        Assert.assertEquals("D200", s.encode("Dhdds")); // 'h' is silent
        Assert.assertEquals("D200", s.encode("Dwdds")); // 'w' is silent
    }

    @Test
// examples and algorithm rules from:  http://west-penwith.org.uk/misc/soundex.htm
    public void testSimplifiedSoundex() { // treat vowels and HW as separators
        final Soundex s = Soundex.US_ENGLISH_SIMPLIFIED;
        Assert.assertEquals("W452", s.encode("WILLIAMS"));
        Assert.assertEquals("B625", s.encode("BARAGWANATH"));
        Assert.assertEquals("D540", s.encode("DONNELL"));
        Assert.assertEquals("L300", s.encode("LLOYD"));
        Assert.assertEquals("W422", s.encode("WOOLCOCK"));
        // Additional local examples
        Assert.assertEquals("D320", s.encode("Dodds"));
        Assert.assertEquals("D320", s.encode("Dwdds")); // w is a separator
        Assert.assertEquals("D320", s.encode("Dhdds")); // h is a separator
    }
}
