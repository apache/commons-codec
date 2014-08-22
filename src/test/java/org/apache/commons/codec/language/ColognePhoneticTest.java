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
import org.junit.Test;

/**
 * Tests the <code>ColognePhonetic</code> class.
 *
 * <p>Keep this file in UTF-8 encoding for proper Javadoc processing.</p>
 *
 */
public class ColognePhoneticTest extends StringEncoderAbstractTest<ColognePhonetic> {

    @Override
    protected ColognePhonetic createStringEncoder() {
        return new ColognePhonetic();
    }

    @Test
    public void testAabjoe() throws EncoderException {
        this.checkEncoding("01", "Aabjoe");
    }

    @Test
    public void testAaclan() throws EncoderException {
        this.checkEncoding("0856", "Aaclan");
    }

    /**
     * Tests [CODEC-122]
     *
     * @throws EncoderException
     */
    @Test
    public void testAychlmajrForCodec122() throws EncoderException {
        this.checkEncoding("04567", "Aychlmajr");
    }

    @Test
    public void testEdgeCases() throws EncoderException {
        final String[][] data = {
            {"a", "0"},
            {"e", "0"},
            {"i", "0"},
            {"o", "0"},
            {"u", "0"},
            {"\u00E4", "0"}, // a-umlaut
            {"\u00F6", "0"}, // o-umlaut
            {"\u00FC", "0"}, // u-umlaut
            {"aa", "0"},
            {"ha", "0"},
            {"h", ""},
            {"aha", "0"},
            {"b", "1"},
            {"p", "1"},
            {"ph", "3"},
            {"f", "3"},
            {"v", "3"},
            {"w", "3"},
            {"g", "4"},
            {"k", "4"},
            {"q", "4"},
            {"x", "48"},
            {"ax", "048"},
            {"cx", "48"},
            {"l", "5"},
            {"cl", "45"},
            {"acl", "085"},
            {"mn", "6"},
            {"r", "7"}};
        this.checkEncodings(data);
    }

    @Test
    public void testExamples() throws EncoderException {
        final String[][] data = {
            {"m\u00DCller", "657"}, // mÜller - why upper case U-umlaut?
            {"schmidt", "862"},
            {"schneider", "8627"},
            {"fischer", "387"},
            {"weber", "317"},
            {"wagner", "3467"},
            {"becker", "147"},
            {"hoffmann", "0366"},
            {"sch\u00C4fer", "837"}, // schÄfer - why upper case A-umlaut ?
            {"Breschnew", "17863"},
            {"Wikipedia", "3412"},
            {"peter", "127"},
            {"pharma", "376"},
            {"m\u00f6nchengladbach", "664645214"}, // mönchengladbach
            {"deutsch", "28"},
            {"deutz", "28"},
            {"hamburg", "06174"},
            {"hannover", "0637"},
            {"christstollen", "478256"},
            {"Xanthippe", "48621"},
            {"Zacharias", "8478"},
            {"Holzbau", "0581"},
            {"matsch", "68"},
            {"matz", "68"},
            {"Arbeitsamt", "071862"},
            {"Eberhard", "01772"},
            {"Eberhardt", "01772"},
            {"heithabu", "021"}};
        this.checkEncodings(data);
    }

    @Test
    public void testHyphen() throws EncoderException {
        final String[][] data = {{"bergisch-gladbach", "174845214"},
                {"M\u00fcller-L\u00fcdenscheidt", "65752682"}}; // Müller-Lüdenscheidt
        this.checkEncodings(data);
    }

    @Test
    public void testIsEncodeEquals() {
        final String[][] data = {
            {"Meyer", "M\u00fcller"}, // Müller
            {"Meyer", "Mayr"},
            {"house", "house"},
            {"House", "house"},
            {"Haus", "house"},
            {"ganz", "Gans"},
            {"ganz", "G\u00e4nse"}, // Gänse
            {"Miyagi", "Miyako"}};
        for (final String[] element : data) {
            this.getStringEncoder().isEncodeEqual(element[1], element[0]);
        }
    }

    @Test
    public void testVariationsMella() throws EncoderException {
        final String data[] = {"mella", "milah", "moulla", "mellah", "muehle", "mule"};
        this.checkEncodingVariations("65", data);
    }

    @Test
    public void testVariationsMeyer() throws EncoderException {
        final String data[] = {"Meier", "Maier", "Mair", "Meyer", "Meyr", "Mejer", "Major"};
        this.checkEncodingVariations("67", data);
    }
}
