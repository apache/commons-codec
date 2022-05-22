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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests BeiderMorseEncoder.
 *
 * @since 1.6
 */
public class BeiderMorseEncoderTest extends StringEncoderAbstractTest<StringEncoder> {
    private static final char[] TEST_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'o', 'u' };

    private void assertNotEmpty(final BeiderMorseEncoder bmpm, final String value) throws EncoderException {
        assertNotEquals("", bmpm.encode(value), value);
    }

    private BeiderMorseEncoder createGenericApproxEncoder() {
        final BeiderMorseEncoder encoder = new BeiderMorseEncoder();
        encoder.setNameType(NameType.GENERIC);
        encoder.setRuleType(RuleType.APPROX);
        return encoder;
    }

    @Override
    protected StringEncoder createStringEncoder() {
        return new BeiderMorseEncoder();
    }

    /**
     * Tests we do not blow up.
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testAllChars() throws EncoderException {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            bmpm.encode(Character.toString(c));
        }
    }

    @Test
    public void testAsciiEncodeNotEmpty1Letter() throws EncoderException {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        for (char c = 'a'; c <= 'z'; c++) {
            final String value = Character.toString(c);
            final String valueU = value.toUpperCase();
            assertNotEmpty(bmpm, value);
            assertNotEmpty(bmpm, valueU);
        }
    }

    @Test
    public void testAsciiEncodeNotEmpty2Letters() throws EncoderException {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        for (char c1 = 'a'; c1 <= 'z'; c1++) {
            for (char c2 = 'a'; c2 <= 'z'; c2++) {
                final String value = new String(new char[] { c1, c2 });
                final String valueU = value.toUpperCase();
                assertNotEmpty(bmpm, value);
                assertNotEmpty(bmpm, valueU);
            }
        }
    }

    @Test
    public void testEncodeAtzNotEmpty() throws EncoderException {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        //String[] names = { "ácz", "átz", "Ignácz", "Ignátz", "Ignác" };
        final String[] names = { "\u00e1cz", "\u00e1tz", "Ign\u00e1cz", "Ign\u00e1tz", "Ign\u00e1c" };
        for (final String name : names) {
            assertNotEmpty(bmpm, name);
        }
    }

    /**
     * Tests https://issues.apache.org/jira/browse/CODEC-125?focusedCommentId=13071566&page=com.atlassian.jira.plugin.system.issuetabpanels:
     * comment-tabpanel#comment-13071566
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testEncodeGna() throws EncoderException {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        bmpm.encode("gna");
    }

    @Test
    public void testInvalidLangIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Rule.getInstance(NameType.GENERIC, RuleType.APPROX, "noSuchLanguage"));
    }

    @Test
    public void testInvalidLangIllegalStateException() {
        assertThrows(IllegalArgumentException.class, () -> Lang.loadFromResource("thisIsAMadeUpResourceName", Languages.getInstance(NameType.GENERIC)));
    }

    @Test
    public void testInvalidLanguageIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Languages.getInstance("thereIsNoSuchLanguage"));
    }

    @Test
    public void testLongestEnglishSurname() {
        final BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        assertTimeout(Duration.ofMillis(10000L), () -> bmpm.encode("MacGhilleseatheanaich"));
    }

    @Test
    public void testNegativeIndexForRuleMatchIndexOutOfBoundsException() {
        final Rule r = new Rule("a", "", "", new Rule.Phoneme("", Languages.ANY_LANGUAGE));
        assertThrows(IndexOutOfBoundsException.class, () -> r.patternAndContextMatches("bob", -1));
    }

    @Test
    public void testOOM() throws EncoderException {
        final String phrase = "200697900'-->&#1913348150;</  bceaeef >aadaabcf\"aedfbff<!--\'-->?>cae" +
            "cfaaa><?&#<!--</script>&lang&fc;aadeaf?>>&bdquo<    cc =\"abff\"    /></   afe  >" +
            "<script><!-- f(';<    cf aefbeef = \"bfabadcf\" ebbfeedd = fccabeb >";

        final BeiderMorseEncoder encoder = new BeiderMorseEncoder();
        encoder.setNameType(NameType.GENERIC);
        encoder.setRuleType(RuleType.EXACT);
        encoder.setMaxPhonemes(10);

        final String phonemes = encoder.encode(phrase);
        assertFalse(phonemes.isEmpty());

        final String[] phonemeArr = phonemes.split("\\|");
        assertTrue(phonemeArr.length <= 10);
    }

    @Test
    public void testSetConcat() {
        final BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setConcat(false);
        assertFalse(bmpm.isConcat(), "Should be able to set concat to false");
    }

    @Test
    public void testSetNameTypeAsh() {
        final BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setNameType(NameType.ASHKENAZI);
        assertEquals(NameType.ASHKENAZI, bmpm.getNameType(), "Name type should have been set to ash");
    }

    @Test
    public void testSetRuleTypeExact() {
        final BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setRuleType(RuleType.EXACT);
        assertEquals(RuleType.EXACT, bmpm.getRuleType(), "Rule type should have been set to exact");
    }

    @Test
    public void testSetRuleTypeToRulesIllegalArgumentException() {
        final BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        assertThrows(IllegalArgumentException.class, () -> bmpm.setRuleType(RuleType.RULES));
    }

    /**
     * (Un)luckily, the worse performing test because of the data in the test characters.
     *
     * @throws EncoderException for some failure scenarios
     */
    @Test /* timeout = 20000L */
    public void testSpeedCheck() throws EncoderException {
        final BeiderMorseEncoder bmpm = this.createGenericApproxEncoder();
        final StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(TEST_CHARS[0]);
        for (int i = 0, j = 1; i < 40; i++, j++) {
            if (j == TEST_CHARS.length) {
                j = 0;
            }
            bmpm.encode(stringBuffer.toString());
            stringBuffer.append(TEST_CHARS[j]);
        }
    }

    @Test
    public void testSpeedCheck2() throws EncoderException {
        final BeiderMorseEncoder bmpm = this.createGenericApproxEncoder();
        final String phrase = "ItstheendoftheworldasweknowitandIfeelfine";

        for (int i = 1; i <= phrase.length(); i++) {
            bmpm.encode(phrase.subSequence(0, i));
        }
    }

    @Test
    public void testSpeedCheck3() throws EncoderException {
        final BeiderMorseEncoder bmpm = this.createGenericApproxEncoder();
        final String phrase = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

        for (int i = 1; i <= phrase.length(); i++) {
            bmpm.encode(phrase.subSequence(0, i));
        }
    }
}
