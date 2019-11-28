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
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link DaitchMokotoffSoundex}.
 * <p>
 * Keep this file in UTF-8 encoding for proper Javadoc processing.
 * </p>
 *
 * @since 1.10
 */
public class DaitchMokotoffSoundexTest extends StringEncoderAbstractTest<DaitchMokotoffSoundex> {

    @Override
    protected DaitchMokotoffSoundex createStringEncoder() {
        return new DaitchMokotoffSoundex();
    }

    private String soundex(final String source) {
        return getStringEncoder().soundex(source);
    }

    private String encode(final String source) {
        return getStringEncoder().encode(source);
    }

    @Test
    public void testAccentedCharacterFolding() {
        Assert.assertEquals("294795", soundex("Straßburg"));
        Assert.assertEquals("294795", soundex("Strasburg"));

        Assert.assertEquals("095600", soundex("Éregon"));
        Assert.assertEquals("095600", soundex("Eregon"));
    }

    @Test
    public void testAdjacentCodes() {
        // AKSSOL
        // A-KS-S-O-L
        // 0-54-4---8 -> wrong
        // 0-54-----8 -> correct
        Assert.assertEquals("054800", soundex("AKSSOL"));

        // GERSCHFELD
        // G-E-RS-CH-F-E-L-D
        // 5--4/94-5/4-7-8-3 -> wrong
        // 5--4/94-5/--7-8-3 -> correct
        Assert.assertEquals("547830|545783|594783|594578", soundex("GERSCHFELD"));
    }

    public void testEncodeBasic() {
        // same as above, but without branching
        Assert.assertEquals("097400", encode("AUERBACH"));
        Assert.assertEquals("097400", encode("OHRBACH"));
        Assert.assertEquals("874400", encode("LIPSHITZ"));
        Assert.assertEquals("874400", encode("LIPPSZYC"));
        Assert.assertEquals("876450", encode("LEWINSKY"));
        Assert.assertEquals("876450", encode("LEVINSKI"));
        Assert.assertEquals("486740", encode("SZLAMAWICZ"));
        Assert.assertEquals("486740", encode("SHLAMOVITZ"));
    }

    @Test
    public void testEncodeIgnoreApostrophes() throws EncoderException {
        this.checkEncodingVariations("079600", new String[] { "OBrien", "'OBrien", "O'Brien", "OB'rien", "OBr'ien",
                "OBri'en", "OBrie'n", "OBrien'" });
    }

    /**
     * Test data from http://www.myatt.demon.co.uk/sxalg.htm
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testEncodeIgnoreHyphens() throws EncoderException {
        this.checkEncodingVariations("565463", new String[] { "KINGSMITH", "-KINGSMITH", "K-INGSMITH", "KI-NGSMITH",
                "KIN-GSMITH", "KING-SMITH", "KINGS-MITH", "KINGSM-ITH", "KINGSMI-TH", "KINGSMIT-H", "KINGSMITH-" });
    }

    @Test
    public void testEncodeIgnoreTrimmable() {
        Assert.assertEquals("746536", encode(" \t\n\r Washington \t\n\r "));
        Assert.assertEquals("746536", encode("Washington"));
    }

    /**
     * Examples from http://www.jewishgen.org/infofiles/soundex.html
     */
    @Test
    public void testSoundexBasic() {
        Assert.assertEquals("583600", soundex("GOLDEN"));
        Assert.assertEquals("087930", soundex("Alpert"));
        Assert.assertEquals("791900", soundex("Breuer"));
        Assert.assertEquals("579000", soundex("Haber"));
        Assert.assertEquals("665600", soundex("Mannheim"));
        Assert.assertEquals("664000", soundex("Mintz"));
        Assert.assertEquals("370000", soundex("Topf"));
        Assert.assertEquals("586660", soundex("Kleinmann"));
        Assert.assertEquals("769600", soundex("Ben Aron"));

        Assert.assertEquals("097400|097500", soundex("AUERBACH"));
        Assert.assertEquals("097400|097500", soundex("OHRBACH"));
        Assert.assertEquals("874400", soundex("LIPSHITZ"));
        Assert.assertEquals("874400|874500", soundex("LIPPSZYC"));
        Assert.assertEquals("876450", soundex("LEWINSKY"));
        Assert.assertEquals("876450", soundex("LEVINSKI"));
        Assert.assertEquals("486740", soundex("SZLAMAWICZ"));
        Assert.assertEquals("486740", soundex("SHLAMOVITZ"));
    }

    /**
     * Examples from http://www.avotaynu.com/soundex.htm
     */
    @Test
    public void testSoundexBasic2() {
        Assert.assertEquals("467000|567000", soundex("Ceniow"));
        Assert.assertEquals("467000", soundex("Tsenyuv"));
        Assert.assertEquals("587400|587500", soundex("Holubica"));
        Assert.assertEquals("587400", soundex("Golubitsa"));
        Assert.assertEquals("746480|794648", soundex("Przemysl"));
        Assert.assertEquals("746480", soundex("Pshemeshil"));
        Assert.assertEquals("944744|944745|944754|944755|945744|945745|945754|945755", soundex("Rosochowaciec"));
        Assert.assertEquals("945744", soundex("Rosokhovatsets"));
    }

    /**
     * Examples from http://en.wikipedia.org/wiki/Daitch%E2%80%93Mokotoff_Soundex
     */
    @Test
    public void testSoundexBasic3() {
        Assert.assertEquals("734000|739400", soundex("Peters"));
        Assert.assertEquals("734600|739460", soundex("Peterson"));
        Assert.assertEquals("645740", soundex("Moskowitz"));
        Assert.assertEquals("645740", soundex("Moskovitz"));
        Assert.assertEquals("154600|145460|454600|445460", soundex("Jackson"));
        Assert.assertEquals("154654|154645|154644|145465|145464|454654|454645|454644|445465|445464",
                soundex("Jackson-Jackson"));
    }

    @Test
    public void testSpecialRomanianCharacters() {
        Assert.assertEquals("364000|464000", soundex("ţamas")); // t-cedilla
        Assert.assertEquals("364000|464000", soundex("țamas")); // t-comma
    }

}
