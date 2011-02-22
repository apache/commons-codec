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

import junit.framework.Assert;

import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

public class ColognePhoneticTest extends StringEncoderAbstractTest {

    private ColognePhonetic colognePhonetic = new ColognePhonetic();

    public ColognePhoneticTest(String name) {
        super(name);
    }

    public void checkEncoding(String expected, String source) {
        Assert.assertEquals("Source: " + source, expected, this.colognePhonetic.encode(source));
    }

    private void checkEncodings(String[][] data) {
        for (int i = 0; i < data.length; i++) {
            this.checkEncoding(data[i][1], data[i][0]);
        }
    }

    private void checkEncodingVariations(String expected, String data[]) {
        for (int i = 0; i < data.length; i++) {
            this.checkEncoding(expected, data[i]);
        }
    }

    protected StringEncoder createEncoder() {
        return new ColognePhonetic();
    }

    public void testAabjoe() {
        this.checkEncoding("01", "Aabjoe");
    }

    public void testAaclan() {
        this.checkEncoding("0856", "Aaclan");
    }

    public void testEdgeCases() {
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

    public void testExamples() {
        String[][] data = {{"Müller-Lüdenscheidt", "65752682"}, {"Breschnew", "17863"}, {"Wikipedia", "3412"}};
        this.checkEncodings(data);
    }

    public void testHyphen() {
        this.checkEncoding("174845214", "bergisch-gladbach");
        // From the Javadoc example:
        this.checkEncoding("65752682", "M�ller-L�denscheidt");
    }

    public void testIsCologneEquals() {
        Assert.assertFalse("Cologne-phonetic encodings should not be equal", this.colognePhonetic.isCologneEqual("Meyer", "Müller"));
        Assert.assertTrue("Cologne-phonetic encodings should be equal", this.colognePhonetic.isCologneEqual("Meyer", "Mayr"));
    }

    public void testIsCologneEqualsPhpData() {
        String[][] data = {
            {"house", "house"},
            {"House", "house"},
            {"Haus", "house"},
            {"ganz", "Gans"},
            {"ganz", "Gänse"},
            {"Miyagi", "Miyako"}};
        for (int i = 0; i < data.length; i++) {
            this.colognePhonetic.isCologneEqual(data[i][1], data[i][0]);
        }
    }

    /**
     * Test data from http://repo.magdev.de/src/Text_ColognePhonetic-0.2.2/test/Text_ColognePhoneticTest.php
     */
    public void testPhpData() {
        String[][] data = {{"peter", "127"}, {"pharma", "376"}, {"bergisch-gladbach", "174845214"}, {"mönchengladbach", "664645214"},
            // {"deutsch", "288"}, // Probably a bug
            {"deutz", "28"},
            // {"hamburg", "6174"},
            // {"hannover", "637"},
            // {"christstollen", "4788256"},
            {"Xanthippe", "48621"},
            {"Zacharias", "8478"},
            // {"Holzbau", "581"},
            // {"matsch", "688"},
            {"matz", "68"},
            {"Arbeitsamt", "071862"},
            {"Eberhard", "01772"},
            {"Eberhardt", "01772"},
            // {"heithabu", "21"},
            {"Müller-Lüdenscheidt", "65752682"},};
        this.checkEncodings(data);
    }

    public void testVariations() {
        String data[] = {"Meier", "Maier", "Mair", "Meyer", "Meyr", "Mejer", "Major"};
        this.checkEncodingVariations("67", data);
    }
}
