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

import java.util.stream.IntStream;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link DaitchMokotoffSoundex}.
 * <p>
 * Keep this file in UTF-8 encoding for proper Javadoc processing.
 * </p>
 */
class DaitchMokotoffSoundexTest extends AbstractStringEncoderTest<DaitchMokotoffSoundex> {

    static IntStream getNonLetters() {
        return IntStream.rangeClosed(Character.MIN_VALUE, Character.MAX_VALUE).filter(c -> !Character.isLetter(c));
    }

    @Override
    protected DaitchMokotoffSoundex createStringEncoder() {
        return new DaitchMokotoffSoundex();
    }

    private String encode(final String source) {
        return getStringEncoder().encode(source);
    }

    private String soundex(final String source) {
        return getStringEncoder().soundex(source);
    }

    @Test
    void testAccentedCharacterFolding() {
        assertEquals("294795", soundex("Straßburg"));
        assertEquals("294795", soundex("Strasburg"));
        assertEquals("095600", soundex("Éregon"));
        assertEquals("095600", soundex("Eregon"));
    }

    @Test
    void testAdjacentCodes() {
        // AKSSOL
        // A-KS-S-O-L
        // 0-54-4---8 -> wrong
        // 0-54-----8 -> correct
        assertEquals("054800", soundex("AKSSOL"));
        // GERSCHFELD
        // G-E-RS-CH-F-E-L-D
        // 5--4/94-5/4-7-8-3 -> wrong
        // 5--4/94-5/--7-8-3 -> correct
        assertEquals("547830|545783|594783|594578", soundex("GERSCHFELD"));
    }

    @Test
    void testEncodeBasic() {
        // same as above, but without branching
        assertEquals("097400", encode("AUERBACH"));
        assertEquals("097400", encode("OHRBACH"));
        assertEquals("874400", encode("LIPSHITZ"));
        assertEquals("874400", encode("LIPPSZYC"));
        assertEquals("876450", encode("LEWINSKY"));
        assertEquals("876450", encode("LEVINSKI"));
        assertEquals("486740", encode("SZLAMAWICZ"));
        assertEquals("486740", encode("SHLAMOVITZ"));
    }

    @Test
    void testEncodeIgnoreApostrophes() throws EncoderException {
        checkEncodingVariations("079600", "OBrien", "'OBrien", "O'Brien", "OB'rien", "OBr'ien", "OBri'en", "OBrie'n", "OBrien'");
    }

    /**
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     *
     * @throws EncoderException for some failure scenarios
     */
    @Test
    void testEncodeIgnoreHyphens() throws EncoderException {
        checkEncodingVariations("565463", "KINGSMITH", "-KINGSMITH", "K-INGSMITH", "KI-NGSMITH", "KIN-GSMITH", "KING-SMITH", "KINGS-MITH", "KINGSM-ITH",
                "KINGSMI-TH", "KINGSMIT-H", "KINGSMITH-");
    }

    @ParameterizedTest
    @MethodSource("getNonLetters")
    void testEncodeIgnoreNonLetters(final int nonLetterInt) throws EncoderException {
        final char nonLetterChar = (char) nonLetterInt;
        checkEncodingVariations("746536", "Washington" + nonLetterChar, nonLetterChar + "Washington", nonLetterChar + "Washington" + nonLetterChar,
                "Washi" + nonLetterChar + "ngton");
    }

    @Test
    void testEncodeIgnoreTrimmable() {
        assertEquals("746536", encode(" \t\n\r Washington \t\n\r "));
        assertEquals("746536", encode("Washington"));
    }

    /**
     * Examples from http://www.jewishgen.org/infofiles/soundex.html
     */
    @Test
    void testSoundexBasic() {
        assertEquals("583600", soundex("GOLDEN"));
        assertEquals("087930", soundex("Alpert"));
        assertEquals("791900", soundex("Breuer"));
        assertEquals("579000", soundex("Haber"));
        assertEquals("665600", soundex("Mannheim"));
        assertEquals("664000", soundex("Mintz"));
        assertEquals("370000", soundex("Topf"));
        assertEquals("586660", soundex("Kleinmann"));
        assertEquals("769600", soundex("Ben Aron"));
        assertEquals("097400|097500", soundex("AUERBACH"));
        assertEquals("097400|097500", soundex("OHRBACH"));
        assertEquals("874400", soundex("LIPSHITZ"));
        assertEquals("874400|874500", soundex("LIPPSZYC"));
        assertEquals("876450", soundex("LEWINSKY"));
        assertEquals("876450", soundex("LEVINSKI"));
        assertEquals("486740", soundex("SZLAMAWICZ"));
        assertEquals("486740", soundex("SHLAMOVITZ"));
    }

    /**
     * Examples from http://www.avotaynu.com/soundex.htm
     */
    @Test
    void testSoundexBasic2() {
        assertEquals("467000|567000", soundex("Ceniow"));
        assertEquals("467000", soundex("Tsenyuv"));
        assertEquals("587400|587500", soundex("Holubica"));
        assertEquals("587400", soundex("Golubitsa"));
        assertEquals("746480|794648", soundex("Przemysl"));
        assertEquals("746480", soundex("Pshemeshil"));
        assertEquals("944744|944745|944754|944755|945744|945745|945754|945755", soundex("Rosochowaciec"));
        assertEquals("945744", soundex("Rosokhovatsets"));
    }

    /**
     * Examples from https://en.wikipedia.org/wiki/Daitch%E2%80%93Mokotoff_Soundex
     */
    @Test
    void testSoundexBasic3() {
        assertEquals("734000|739400", soundex("Peters"));
        assertEquals("734600|739460", soundex("Peterson"));
        assertEquals("645740", soundex("Moskowitz"));
        assertEquals("645740", soundex("Moskovitz"));
        assertEquals("154600|145460|454600|445460", soundex("Jackson"));
        final String jacksonJackson = "154654|154645|154644|145465|145464|454654|454645|454644|445465|445464";
        assertEquals(jacksonJackson, soundex("Jackson-Jackson"));
        assertEquals(jacksonJackson, soundex("Jackson--Jackson"));
        assertEquals(jacksonJackson, soundex("Jackson—Jackson"));
        assertEquals(jacksonJackson, soundex("Jackson_Jackson"));
        assertEquals(jacksonJackson, soundex("Jackson$Jackson"));
        assertEquals(jacksonJackson, soundex("JacksonJackson"));
    }

    @Test
    void testSpecialRomanianCharacters() {
        assertEquals("364000|464000", soundex("ţamas")); // t-cedilla
        assertEquals("364000|464000", soundex("țamas")); // t-comma
    }
}
