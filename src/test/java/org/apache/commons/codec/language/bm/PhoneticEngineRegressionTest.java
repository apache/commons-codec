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

package org.apache.commons.codec.language.bm;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests PhoneticEngine and Languages.LanguageSet in ways very similar to code found in solr-3.6.0.
 *
 * @since 1.7
 */
public class PhoneticEngineRegressionTest {

    @Test
    public void testSolrGENERIC() {
        Map<String, String> args;

        // concat is true, ruleType is EXACT
        args = new TreeMap<>();
        args.put("nameType", "GENERIC");
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("anZelo|andZelo|angelo|anhelo|anjelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals("(anZelo|andZelo|angelo|anhelo|anjelo|anxelo)-(danZelo|dandZelo|dangelo|danhelo|danjelo|danxelo)",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("andZelo|angelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<>();
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("anZelo|andZelo|angelo|anhelo|anjelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals("(anZelo|andZelo|angelo|anhelo|anjelo|anxelo)-(danZelo|dandZelo|dangelo|danhelo|danjelo|danxelo)",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("andZelo|angelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<>();
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        assertEquals("(YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo)-(dYngYlo|dYngilo|dagilo|dangYlo|dangilo|daniilo|danilo|danxilo|danzilo|dogilo|dongYlo|dongilo|doniilo|donilo|donxilo|donzilo)",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angilo|anxilo|anzilo|ongilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<>();
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        assertEquals("(YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo)-(dYngYlo|dYngilo|dagilo|dangYlo|dangilo|daniilo|danilo|danxilo|danzilo|dogilo|dongYlo|dongilo|doniilo|donilo|donxilo|donzilo)",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angilo|anxilo|anzilo|ongilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testSolrASHKENAZI() {
        Map<String, String> args;

        // concat is true, ruleType is EXACT
        args = new TreeMap<>();
        args.put("nameType", "ASHKENAZI");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("andZelo|angelo|anhelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals("dandZelo|dangelo|danhelo|danxelo",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<>();
        args.put("nameType", "ASHKENAZI");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("andZelo|angelo|anhelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals("dandZelo|dangelo|danhelo|danxelo",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<>();
        args.put("nameType", "ASHKENAZI");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, true, "Angelo"));
        assertEquals("dYngYlo|dYngilo|dangYlo|dangilo|danilo|danxilo|danzilo|dongYlo|dongilo|donilo|donxilo|donzilo",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angilo|anxilo|ongilo|onxilo",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<>();
        args.put("nameType", "ASHKENAZI");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo",
            encode(args, false, "Angelo"));
        assertEquals("dYngYlo|dYngilo|dangYlo|dangilo|danilo|danxilo|danzilo|dongYlo|dongilo|donilo|donxilo|donzilo",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("angilo|anxilo|ongilo|onxilo",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testSolrSEPHARDIC() {
        Map<String, String> args;

        // concat is true, ruleType is EXACT
        args = new TreeMap<>();
        args.put("nameType", "SEPHARDIC");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, true, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("anZelo|andZelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals("anZelo|andZelo|anxelo",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("andZelo|anxelo",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<>();
        args.put("nameType", "SEPHARDIC");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, false, "Angelo"));
        args.put("ruleType", "EXACT");
        assertEquals("anZelo|andZelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals("danZelo|dandZelo|danxelo",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("andZelo|anxelo",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<>();
        args.put("nameType", "SEPHARDIC");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, true, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, true, "Angelo"));
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, true, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, true, "Angelo"));
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<>();
        args.put("nameType", "SEPHARDIC");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, false, "Angelo"));
        args.put("ruleType", "APPROX");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, false, "Angelo"));
        assertEquals("danhila|danhilu|danzila|danzilu|nhila|nhilu|nzila|nzilu",
            encode(args, false, "D'Angelo"));
        args.put("languageSet", "italian,greek,spanish");
        assertEquals("anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu",
            encode(args, false, "Angelo"));
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testCompatibilityWithOriginalVersion() {
        // see CODEC-187
        // comparison: http://stevemorse.org/census/soundex.html

        final Map<String, String> args = new TreeMap<>();
        args.put("nameType", "GENERIC");
        args.put("ruleType", "APPROX");

        assertEquals("Ybram|Ybrom|abram|abran|abrom|abron|avram|avrom|obram|obran|obrom|obron|ovram|ovrom",
            encode(args, true, "abram"));
        assertEquals("bndzn|bntsn|bnzn|vndzn|vntsn",
            encode(args, true, "Bendzin"));

        args.put("nameType", "ASHKENAZI");
        args.put("ruleType", "APPROX");

        assertEquals("Ybram|Ybrom|abram|abrom|avram|avrom|imbram|imbrom|obram|obrom|ombram|ombrom|ovram|ovrom",
            encode(args, true, "abram"));
        assertEquals("YlpYrn|Ylpirn|alpYrn|alpirn|olpYrn|olpirn|xalpirn|xolpirn",
            encode(args, true, "Halpern"));

    }

    /**
     * This code is similar in style to code found in Solr:
     * solr/core/src/java/org/apache/solr/analysis/BeiderMorseFilterFactory.java
     *
     * Making a JUnit test out of it to protect Solr from possible future
     * regressions in Commons-Codec.
     */
    private static String encode(final Map<String, String> args, final boolean concat, final String input) {
        final Languages.LanguageSet languageSet;
        final PhoneticEngine engine;

        // PhoneticEngine = NameType + RuleType + concat
        // we use common-codec's defaults: GENERIC + APPROX + true
        final String nameTypeArg = args.get("nameType");
        final NameType nameType = (nameTypeArg == null) ? NameType.GENERIC : NameType.valueOf(nameTypeArg);

        final String ruleTypeArg = args.get("ruleType");
        final RuleType ruleType = (ruleTypeArg == null) ? RuleType.APPROX : RuleType.valueOf(ruleTypeArg);

        engine = new PhoneticEngine(nameType, ruleType, concat);

        // LanguageSet: defaults to automagic, otherwise a comma-separated list.
        final String languageSetArg = args.get("languageSet");
        if (languageSetArg == null || languageSetArg.equals("auto")) {
            languageSet = null;
        } else {
            languageSet = Languages.LanguageSet.from(new HashSet<>(Arrays.asList(languageSetArg.split(","))));
        }

        /*
            org/apache/lucene/analysis/phonetic/BeiderMorseFilter.java (lines 96-98) does this:

            encoded = (languages == null)
                ? engine.encode(termAtt.toString())
                : engine.encode(termAtt.toString(), languages);

            Hence our approach, below:
        */
        if (languageSet == null) {
            return engine.encode(input);
        }
        return engine.encode(input, languageSet);
    }
}
