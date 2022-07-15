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

import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Series of tests for the Match Rating Approach algorithm.
 *
 * General naming nomenclature for the test is of the form:
 * GeneralMetadataOnTheTestArea_ActualTestValues_ExpectedResult
 *
 * An unusual value is indicated by the term "corner case"
 */
public class MatchRatingApproachEncoderTest extends StringEncoderAbstractTest<MatchRatingApproachEncoder> {

    // ********** BEGIN REGION - TEST SUPPORT METHODS

    @Test
    public final void testAccentRemoval_AllLower_SuccessfullyRemoved() {
        assertEquals("aeiou", this.getStringEncoder().removeAccents("áéíóú"));
    }

    @Test
    public final void testAccentRemoval_WithSpaces_SuccessfullyRemovedAndSpacesInvariant() {
        assertEquals("ae io  u", this.getStringEncoder().removeAccents("áé íó  ú"));
    }

    @Test
    public final void testAccentRemoval_UpperAndLower_SuccessfullyRemovedAndCaseInvariant() {
        assertEquals("AeiOuu", this.getStringEncoder().removeAccents("ÁeíÓuu"));
    }

    @Test
    public final void testAccentRemoval_MixedWithUnusualChars_SuccessfullyRemovedAndUnusualCharactersInvariant() {
        assertEquals("A-e'i.,o&u", this.getStringEncoder().removeAccents("Á-e'í.,ó&ú"));
    }

    @Test
    public final void testAccentRemoval_GerSpanFrenMix_SuccessfullyRemoved() {
        assertEquals("aeoußAEOUnNa", this.getStringEncoder().removeAccents("äëöüßÄËÖÜñÑà"));
    }

    @Test
    public final void testAccentRemoval_ComprehensiveAccentMix_AllSuccessfullyRemoved() {
        assertEquals("E,E,E,E,U,U,I,I,A,A,O,e,e,e,e,u,u,i,i,a,a,o,c",
                this.getStringEncoder().removeAccents("È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô,ç"));
    }

    @Test
    public final void testAccentRemovalNormalString_NoChange() {
        assertEquals("Colorless green ideas sleep furiously", this.getStringEncoder().removeAccents("Colorless green ideas sleep furiously"));
    }

    @Test
    public final void testAccentRemoval_NINO_NoChange() {
        assertEquals("", this.getStringEncoder().removeAccents(""));
    }

    @Test
    public final void testAccentRemoval_NullValue_ReturnNullSuccessfully() {
        assertNull(this.getStringEncoder().removeAccents(null));
    }

    @Test
    public final void testRemoveSingleDoubleConsonants_BUBLE_RemovedSuccessfully() {
        assertEquals("BUBLE", this.getStringEncoder().removeDoubleConsonants("BUBBLE"));
    }

    @Test
    public final void testRemoveDoubleConsonants_MISSISSIPPI_RemovedSuccessfully() {
        assertEquals("MISISIPI", this.getStringEncoder().removeDoubleConsonants("MISSISSIPPI"));
    }

    @Test
    public final void testRemoveDoubleDoubleVowel_BEETLE_NotRemoved() {
        assertEquals("BEETLE", this.getStringEncoder().removeDoubleConsonants("BEETLE"));
    }

    @Test
    public final void testIsVowel_CapitalA_ReturnsTrue() {
        assertTrue(this.getStringEncoder().isVowel("A"));
    }

    @Test
    public final void testIsVowel_SmallD_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isVowel("d"));
    }

    @Test
    public final void testRemoveVowel_ALESSANDRA_Returns_ALSSNDR() {
        assertEquals("ALSSNDR", this.getStringEncoder().removeVowels("ALESSANDRA"));
    }

    @Test
    public final void testRemoveVowel__AIDAN_Returns_ADN() {
        assertEquals("ADN", this.getStringEncoder().removeVowels("AIDAN"));
    }

    @Test
    public final void testRemoveVowel__DECLAN_Returns_DCLN() {
        assertEquals("DCLN", this.getStringEncoder().removeVowels("DECLAN"));
    }

    @Test
    public final void testGetFirstLast3__ALEXANDER_Returns_Aleder() {
        assertEquals("Aleder", this.getStringEncoder().getFirst3Last3("Alexzander"));
    }

    @Test
    public final void testGetFirstLast3_PETE_Returns_PETE() {
        assertEquals("PETE", this.getStringEncoder().getFirst3Last3("PETE"));
    }

    @Test
    public final void testLeftToRightThenRightToLeft_ALEXANDER_ALEXANDRA_Returns4() {
        assertEquals(4, this.getStringEncoder().leftToRightThenRightToLeftProcessing("ALEXANDER", "ALEXANDRA"));
    }

    @Test
    public final void testLeftToRightThenRightToLeft_EINSTEIN_MICHAELA_Returns0() {
        assertEquals(0, this.getStringEncoder().leftToRightThenRightToLeftProcessing("EINSTEIN", "MICHAELA"));
    }

    @Test
    public final void testGetMinRating_7_Return4_Successfully() {
        assertEquals(4, this.getStringEncoder().getMinRating(7));
    }

    @Test
    public final void testGetMinRating_1_Returns5_Successfully() {
        assertEquals(5, this.getStringEncoder().getMinRating(1));
    }

    @Test
    public final void testGetMinRating_2_Returns5_Successfully() {
        assertEquals(5, this.getStringEncoder().getMinRating(2));
    }

    @Test
    public final void testgetMinRating_5_Returns4_Successfully(){
        assertEquals(4, this.getStringEncoder().getMinRating(5));
    }

    @Test
    public final void testgetMinRating_5_Returns4_Successfully2(){
        assertEquals(4, this.getStringEncoder().getMinRating(5));
    }

    @Test
    public final void testgetMinRating_6_Returns4_Successfully(){
        assertEquals(4, this.getStringEncoder().getMinRating(6));
    }

    @Test
    public final void testgetMinRating_7_Returns4_Successfully(){
        assertEquals(4, this.getStringEncoder().getMinRating(7));
    }

    @Test
    public final void testgetMinRating_8_Returns3_Successfully(){
        assertEquals(3, this.getStringEncoder().getMinRating(8));
    }

    @Test
    public final void testgetMinRating_10_Returns3_Successfully(){
        assertEquals(3, this.getStringEncoder().getMinRating(10));
    }

    @Test
    public final void testgetMinRating_11_Returns_3_Successfully(){
        assertEquals(3, this.getStringEncoder().getMinRating(11));
    }

    @Test
    public final void testGetMinRating_13_Returns_1_Successfully() {
        assertEquals(1, this.getStringEncoder().getMinRating(13));
    }

    @Test
    public final void testCleanName_SuccessfullyClean() {
        assertEquals("THISISATEST", this.getStringEncoder().cleanName("This-ís   a t.,es &t"));
    }

    @Test
    public final void testIsVowel_SingleVowel_ReturnsTrue() {
        assertTrue(this.getStringEncoder().isVowel(("I")));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_SecondNameNothing_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("test", ""));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_FirstNameNothing_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("", "test"));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_SecondNameJustSpace_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("test", " "));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_FirstNameJustSpace_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals(" ", "test"));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_SecondNameNull_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("test", null));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_FirstNameNull_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals(null, "test"));
    }

    @Test
    public final void testIsEncodeEquals_CornerCase_FirstNameJust1Letter_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("t", "test"));
    }

    @Test
    public final void testIsEncodeEqualsSecondNameJust1Letter_ReturnsFalse() {
        assertFalse(this.getStringEncoder().isEncodeEquals("test", "t"));
    }

    // ***** END REGION - TEST SUPPORT METHODS

    // ***** BEGIN REGION - TEST GET MRA ENCODING

    @Test
    public final void testGetEncoding_HARPER_HRPR() {
        assertEquals("HRPR", this.getStringEncoder().encode("HARPER"));
    }

    @Test
    public final void testGetEncoding_SMITH_to_SMTH() {
        assertEquals("SMTH", this.getStringEncoder().encode("Smith"));
    }

    @Test
    public final void testGetEncoding_SMYTH_to_SMYTH() {
        assertEquals("SMYTH", this.getStringEncoder().encode("Smyth"));
    }

    @Test
    public final void testGetEncoding_Space_to_Nothing() {
        assertEquals("", this.getStringEncoder().encode(" "));
    }

    @Test
    public final void testGetEncoding_NoSpace_to_Nothing() {
        assertEquals("", this.getStringEncoder().encode(""));
    }

    @Test
    public final void testGetEncoding_Null_to_Nothing() {
        assertEquals("", this.getStringEncoder().encode(null));
    }

    @Test
    public final void testGetEncoding_One_Letter_to_Nothing() {
        assertEquals("", this.getStringEncoder().encode("E"));
    }

    @Test
    public final void testCompareNameNullSpace_ReturnsFalseSuccessfully() {
        assertFalse(getStringEncoder().isEncodeEquals(null, " "));
    }

    @Test
    public final void testCompareNameSameNames_ReturnsFalseSuccessfully() {
        assertTrue(getStringEncoder().isEncodeEquals("John", "John"));
    }

    // ***** END REGION - TEST GET MRA ENCODING

    // ***** BEGIN REGION - TEST GET MRA COMPARISONS

    @Test
    public final void testCompare_SMITH_SMYTH_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("smith", "smyth"));
    }

    @Test
    public final void testCompare_BURNS_BOURNE_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Burns", "Bourne"));
    }

    @Test
    public final void testCompare_ShortNames_AL_ED_WorksButNoMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Al", "Ed"));
    }

    @Test
    public final void testCompare_CATHERINE_KATHRYN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Catherine", "Kathryn"));
    }

    @Test
    public final void testCompare_BRIAN_BRYAN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Brian", "Bryan"));
    }

    @Test
    public final void testCompare_SEAN_SHAUN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Séan", "Shaun"));
    }

    @Test
    public final void testCompare_COLM_COLIN_WithAccentsAndSymbolsAndSpaces_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Cólm.   ", "C-olín"));
    }

    @Test
    public final void testCompare_STEPHEN_STEVEN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Stephen", "Steven"));
    }

    @Test
    public final void testCompare_STEVEN_STEFAN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Steven", "Stefan"));
    }

    @Test
    public final void testCompare_STEPHEN_STEFAN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Stephen", "Stefan"));
    }

    @Test
    public final void testCompare_SAM_SAMUEL_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Sam", "Samuel"));
    }

    @Test
    public final void testCompare_MICKY_MICHAEL_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Micky", "Michael"));
    }

    @Test
    public final void testCompare_OONA_OONAGH_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Oona", "Oonagh"));
    }

    @Test
    public final void testCompare_SOPHIE_SOFIA_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Sophie", "Sofia"));
    }

    @Test
    public final void testCompare_FRANCISZEK_FRANCES_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Franciszek", "Frances"));
    }

    @Test
    public final void testCompare_TOMASZ_TOM_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Tomasz", "tom"));
    }

    @Test
    public final void testCompare_SmallInput_CARK_Kl_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Kl", "Karl"));
    }

    @Test
    public final void testCompareNameToSingleLetter_KARL_C_DoesNotMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Karl", "C"));
    }

    @Test
    public final void testCompare_ZACH_ZAKARIA_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Zach", "Zacharia"));
    }

    @Test
    public final void testCompare_KARL_ALESSANDRO_DoesNotMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Karl", "Alessandro"));
    }

    @Test
    public final void testCompare_Forenames_UNA_OONAGH_ShouldSuccessfullyMatchButDoesNot() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Úna", "Oonagh")); // Disappointing
    }

    // ***** Begin Region - Test Get Encoding - Surnames

    @Test
    public final void testCompare_Surname_OSULLIVAN_OSUILLEABHAIN_SuccessfulMatch() {
        assertTrue(this.getStringEncoder().isEncodeEquals("O'Sullivan", "Ó ' Súilleabháin"));
    }

    @Test
    public final void testCompare_LongSurnames_MORIARTY_OMUIRCHEARTAIGH_DoesNotSuccessfulMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Moriarty", "OMuircheartaigh"));
    }

    @Test
    public final void testCompare_LongSurnames_OMUIRCHEARTAIGH_OMIREADHAIGH_SuccessfulMatch() {
        assertTrue(this.getStringEncoder().isEncodeEquals("o'muireadhaigh", "Ó 'Muircheartaigh "));
    }

    @Test
    public final void testCompare_Surname_COOPERFLYNN_SUPERLYN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Cooper-Flynn", "Super-Lyn"));
    }

    @Test
    public final void testCompare_Surname_HAILEY_HALLEY_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Hailey", "Halley"));
    }

    // **** BEGIN YIDDISH/SLAVIC SECTION ****

    @Test
    public final void testCompare_Surname_AUERBACH_UHRBACH_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Auerbach", "Uhrbach"));
    }

    @Test
    public final void testCompare_Surname_MOSKOWITZ_MOSKOVITZ_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Moskowitz", "Moskovitz"));
    }

    @Test
    public final void testCompare_Surname_LIPSHITZ_LIPPSZYC_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("LIPSHITZ", "LIPPSZYC"));
    }

    @Test
    public final void testCompare_Surname_LEWINSKY_LEVINSKI_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("LEWINSKY", "LEVINSKI"));
    }

    @Test
    public final void testCompare_Surname_SZLAMAWICZ_SHLAMOVITZ_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("SZLAMAWICZ", "SHLAMOVITZ"));
    }

    @Test
    public final void testCompare_Surname_ROSOCHOWACIEC_ROSOKHOVATSETS_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("R o s o ch o w a c ie c", " R o s o k ho v a ts e ts"));
    }

    @Test
    public final void testCompare_Surname_PRZEMYSL_PSHEMESHIL_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals(" P rz e m y s l", " P sh e m e sh i l"));
    }

    // **** END YIDDISH/SLAVIC SECTION ****

    @Test
    public final void testCompare_PETERSON_PETERS_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Peterson", "Peters"));
    }

    @Test
    public final void testCompare_MCGOWAN_MCGEOGHEGAN_SuccessfullyMatched() {
        assertTrue(this.getStringEncoder().isEncodeEquals("McGowan", "Mc Geoghegan"));
    }

    @Test
    public final void testCompare_SurnamesCornerCase_MURPHY_Space_NoMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Murphy", " "));
    }

    @Test
    public final void testCompare_SurnamesCornerCase_MURPHY_NoSpace_NoMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Murphy", ""));
    }

    @Test
    public final void testCompare_SurnameCornerCase_Nulls_NoMatch() {
        assertFalse(this.getStringEncoder().isEncodeEquals(null, null));
    }

    @Test
    public final void testCompare_Surnames_MURPHY_LYNCH_NoMatchExpected() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Murphy", "Lynch"));
    }

    @Test
    public final void testCompare_Forenames_SEAN_JOHN_MatchExpected() {
        assertTrue(this.getStringEncoder().isEncodeEquals("Sean", "John"));
    }

    @Test
    public final void testCompare_Forenames_SEAN_PETE_NoMatchExpected() {
        assertFalse(this.getStringEncoder().isEncodeEquals("Sean", "Pete"));
    }

    @Override
    protected MatchRatingApproachEncoder createStringEncoder() {
        return new MatchRatingApproachEncoder();
    }

    // ***** END REGION - TEST GET MRA COMPARISONS

}
