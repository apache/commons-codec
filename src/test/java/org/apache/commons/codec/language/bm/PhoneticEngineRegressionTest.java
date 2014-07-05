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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

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
        args = new TreeMap<String, String>();
        args.put("nameType", "GENERIC");
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, true, "Angelo"), "anZelo|andZelo|angelo|anhelo|anjelo|anxelo");
        assertEquals(encode(args, true, "D'Angelo"), "(anZelo|andZelo|angelo|anhelo|anjelo|anxelo)-(danZelo|dandZelo|dangelo|danhelo|danjelo|danxelo)");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "andZelo|angelo|anxelo");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<String, String>();
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, false, "Angelo"), "anZelo|andZelo|angelo|anhelo|anjelo|anxelo");
        assertEquals(encode(args, false, "D'Angelo"), "(anZelo|andZelo|angelo|anhelo|anjelo|anxelo)-(danZelo|dandZelo|dangelo|danhelo|danjelo|danxelo)");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "andZelo|angelo|anxelo");
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<String, String>();
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        assertEquals(encode(args, true, "D'Angelo"), "(YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo)-(dYngYlo|dYngilo|dagilo|dangYlo|dangilo|daniilo|danilo|danxilo|danzilo|dogilo|dongYlo|dongilo|doniilo|donilo|donxilo|donzilo)");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "angilo|anxilo|anzilo|ongilo|onxilo|onzilo");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<String, String>();
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo");
        assertEquals(encode(args, false, "D'Angelo"), "(YngYlo|Yngilo|agilo|angYlo|angilo|aniilo|anilo|anxilo|anzilo|ogilo|ongYlo|ongilo|oniilo|onilo|onxilo|onzilo)-(dYngYlo|dYngilo|dagilo|dangYlo|dangilo|daniilo|danilo|danxilo|danzilo|dogilo|dongYlo|dongilo|doniilo|donilo|donxilo|donzilo)");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "angilo|anxilo|anzilo|ongilo|onxilo|onzilo");
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testSolrASHKENAZI() {
        Map<String, String> args;

        // concat is true, ruleType is EXACT
        args = new TreeMap<String, String>();
        args.put("nameType", "ASHKENAZI");
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, true, "Angelo"), "andZelo|angelo|anhelo|anxelo");
        assertEquals(encode(args, true, "D'Angelo"), "dandZelo|dangelo|danhelo|danxelo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "angelo|anxelo");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<String, String>();
        args.put("nameType", "ASHKENAZI");
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, false, "Angelo"), "andZelo|angelo|anhelo|anxelo");
        assertEquals(encode(args, false, "D'Angelo"), "dandZelo|dangelo|danhelo|danxelo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "angelo|anxelo");
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<String, String>();
        args.put("nameType", "ASHKENAZI");
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, true, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        assertEquals(encode(args, true, "D'Angelo"), "dYngYlo|dYngilo|dangYlo|dangilo|danilo|danxilo|danzilo|dongYlo|dongilo|donilo|donxilo|donzilo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "angilo|anxilo|ongilo|onxilo");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<String, String>();
        args.put("nameType", "ASHKENAZI");
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, false, "Angelo"), "YngYlo|Yngilo|angYlo|angilo|anilo|anxilo|anzilo|ongYlo|ongilo|onilo|onxilo|onzilo");
        assertEquals(encode(args, false, "D'Angelo"), "dYngYlo|dYngilo|dangYlo|dangilo|danilo|danxilo|danzilo|dongYlo|dongilo|donilo|donxilo|donzilo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "angilo|anxilo|ongilo|onxilo");
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testSolrSEPHARDIC() {
        Map<String, String> args;

        // concat is true, ruleType is EXACT
        args = new TreeMap<String, String>();
        args.put("nameType", "SEPHARDIC");
        assertEquals(encode(args, true, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, true, "Angelo"), "anZelo|andZelo|anxelo");
        assertEquals(encode(args, true, "D'Angelo"), "anZelo|andZelo|anxelo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "andZelo|anxelo");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is EXACT
        args = new TreeMap<String, String>();
        args.put("nameType", "SEPHARDIC");
        assertEquals(encode(args, false, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        args.put("ruleType", "EXACT");
        assertEquals(encode(args, false, "Angelo"), "anZelo|andZelo|anxelo");
        assertEquals(encode(args, false, "D'Angelo"), "danZelo|dandZelo|danxelo");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "andZelo|anxelo");
        assertEquals(encode(args, false, "1234"), "");

        // concat is true, ruleType is APPROX
        args = new TreeMap<String, String>();
        args.put("nameType", "SEPHARDIC");
        assertEquals(encode(args, true, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, true, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        assertEquals(encode(args, true, "D'Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, true, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        assertEquals(encode(args, true, "1234"), "");

        // concat is false, ruleType is APPROX
        args = new TreeMap<String, String>();
        args.put("nameType", "SEPHARDIC");
        assertEquals(encode(args, false, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        args.put("ruleType", "APPROX");
        assertEquals(encode(args, false, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        assertEquals(encode(args, false, "D'Angelo"), "danhila|danhilu|danzila|danzilu|nhila|nhilu|nzila|nzilu");
        args.put("languageSet", "italian,greek,spanish");
        assertEquals(encode(args, false, "Angelo"), "anhila|anhilu|anzila|anzilu|nhila|nhilu|nzila|nzilu");
        assertEquals(encode(args, false, "1234"), "");
    }

    @Test
    public void testCompatibilityWithOriginalVersion() {
        // see CODEC-187
        // comparison: http://stevemorse.org/census/soundex.html

        Map<String, String> args = new TreeMap<String, String>();
        args.put("nameType", "GENERIC");
        args.put("ruleType", "APPROX");

        assertEquals(encode(args, true, "abram"), "Ybram|Ybrom|abram|abran|abrom|abron|avram|avrom|obram|obran|obrom|obron|ovram|ovrom");
        assertEquals(encode(args, true, "Bendzin"), "bndzn|bntsn|bnzn|vndzn|vntsn");

        args.put("nameType", "ASHKENAZI");
        args.put("ruleType", "APPROX");

        assertEquals(encode(args, true, "abram"), "Ybram|Ybrom|abram|abrom|avram|avrom|imbram|imbrom|obram|obrom|ombram|ombrom|ovram|ovrom");
        assertEquals(encode(args, true, "Halpern"), "YlpYrn|Ylpirn|alpYrn|alpirn|olpYrn|olpirn|xalpirn|xolpirn");

    }

    /**
     * This code is similar in style to code found in Solr:
     * solr/core/src/java/org/apache/solr/analysis/BeiderMorseFilterFactory.java
     *
     * Making a JUnit test out of it to protect Solr from possible future
     * regressions in Commons-Codec.
     */
    private static String encode(final Map<String, String> args, final boolean concat, final String input) {
        Languages.LanguageSet languageSet;
        PhoneticEngine engine;

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
            languageSet = Languages.LanguageSet.from(new HashSet<String>(Arrays.asList(languageSetArg.split(","))));
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
        } else {
            return engine.encode(input, languageSet);
        }
    }
}
