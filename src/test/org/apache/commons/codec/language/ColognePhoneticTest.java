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

import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

public class ColognePhoneticTest extends StringEncoderAbstractTest {

    public ColognePhoneticTest(String name) {
        super(name);
    }

    protected StringEncoder createEncoder() {
        return new ColognePhonetic();
    }

    public void testExamples() {
        ColognePhonetic koellePhon = new ColognePhonetic();
        String[][] data = { { "Müller-Lüdenscheidt", "65752682" },
                { "Breschnew", "17863" }, { "Wikipedia", "3412" } };

        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i][1], koellePhon.colognePhonetic(data[i][0]));
        }
    }

    public void testBorderCases() {
        ColognePhonetic koellePhon = new ColognePhonetic();

        String[][] data = { { "a", "0" }, { "e", "0" }, { "i", "0" },
                { "o", "0" }, { "u", "0" }, { "\u00E4", "0" }, { "\u00F6", "0" },
                { "\u00FC", "0" }, { "aa", "0" }, { "ha", "0" }, { "h", "" },
                { "aha", "0" }, { "b", "1" }, { "p", "1" }, { "ph", "3" },
                { "f", "3" }, { "v", "3" }, { "w", "3" }, { "g", "4" },
                { "k", "4" }, { "q", "4" }, { "x", "48" }, { "ax", "048" },
                { "cx", "48" }, { "l", "5" }, { "cl", "45" }, { "acl", "085" },
                { "mn", "6" }, { "r", "7" } };

        for (int i = 0; i < data.length; i++) {
            assertEquals("Failed to correctly convert element of index: " + i,
                         data[i][1], koellePhon.colognePhonetic(data[i][0]));
        }
    }

    public void testIsCologneEquals() {
        ColognePhonetic koellePhon = new ColognePhonetic();
        assertFalse("Cologne-phonetic encodings should not be equal",
                koellePhon.isCologneEqual("Meyer", "Müller"));
        assertTrue("Cologne-phonetic encodings should be equal",
                koellePhon.isCologneEqual("Meyer", "Mayr"));
    }
}
