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
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

public class ColognePhoneticTest extends StringEncoderAbstractTest {

    public ColognePhoneticTest(String name) {
        super(name);
    }

    protected StringEncoder createStringEncoder() {
        return new ColognePhonetic();
    }

    public void testAabjoe() throws EncoderException {
        this.checkEncoding("01", "Aabjoe");
    }

    public void testAaclan() throws EncoderException {
        this.checkEncoding("0856", "Aaclan");
    }

    public void testEdgeCases() throws EncoderException {
        String[][] data = {
            {"a", "0"},
            {"e", "0"},
            {"i", "0"},
            {"o", "0"},
            {"u", "0"},
            {"\u00E4", "0"},
            {"\u00F6", "0"},
            {"\u00FC", "0"},
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

    public void testExamples() throws EncoderException {
        String[][] data = {
            {"m\u00DCller", "657"},
            {"schmidt", "862"},
            {"schneider", "8627"},
            {"fischer", "387"},
            {"weber", "317"},
            {"wagner", "3467"},
            {"becker", "147"},
            {"hoffmann", "0366"},
            {"sch\u00C4fer", "837"},
            {"Breschnew", "17863"},
            {"Wikipedia", "3412"},
            {"peter", "127"},
            {"pharma", "376"},
            {"mönchengladbach", "664645214"},
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

    public void testHyphen() throws EncoderException {
        String[][] data = {{"bergisch-gladbach", "174845214"}, {"Müller-Lüdenscheidt", "65752682"},
            // From the Javadoc example:
            {"M�ller-L�denscheidt", "65752682"}};
        this.checkEncodings(data);
    }

    public void testIsEncodeEquals() {
        String[][] data = {
            {"Meyer", "Müller"},
            {"Meyer", "Mayr"},
            {"house", "house"},
            {"House", "house"},
            {"Haus", "house"},
            {"ganz", "Gans"},
            {"ganz", "Gänse"},
            {"Miyagi", "Miyako"}};
        for (int i = 0; i < data.length; i++) {
            ((ColognePhonetic) this.getStringEncoder()).isEncodeEqual(data[i][1], data[i][0]);
        }
    }

    public void testVariationsMella() throws EncoderException {
        String data[] = {"mella", "milah", "moulla", "mellah", "muehle", "mule"};
        this.checkEncodingVariations("65", data);
    }

    public void testVariationsMeyer() throws EncoderException {
        String data[] = {"Meier", "Maier", "Mair", "Meyer", "Meyr", "Mejer", "Major"};
        this.checkEncodingVariations("67", data);
    }
}
