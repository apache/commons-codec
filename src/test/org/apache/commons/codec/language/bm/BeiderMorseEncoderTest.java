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

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests BeiderMorseEncoder.
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class BeiderMorseEncoderTest extends StringEncoderAbstractTest {
    private void assertNotEmpty(BeiderMorseEncoder bmpm, final String value) throws EncoderException {
        Assert.assertFalse(value, bmpm.encode(value).equals(""));
    }

    private BeiderMorseEncoder createGenericApproxEncoder() {
        BeiderMorseEncoder encoder = new BeiderMorseEncoder();
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
     * @throws EncoderException
     */
    @Test
    public void testAllChars() throws EncoderException {
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            bmpm.encode("" + c);
        }
    }

    @Test
    public void testAsciiEncodeNotEmpty1Letter() throws EncoderException {
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        for (char c = 'a'; c <= 'z'; c++) {
            final String value = "" + c;
            final String valueU = value.toUpperCase();
            assertNotEmpty(bmpm, value);
            assertNotEmpty(bmpm, valueU);
        }
    }

    @Test
    public void testAsciiEncodeNotEmpty2Letters() throws EncoderException {
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
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
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        String[] names = { "ácz", "átz", "Ignácz", "Ignátz", "Ignác" };
        for (String name : names) {
            assertNotEmpty(bmpm, name);
        }
    }

    /**
     * Tests https://issues.apache.org/jira/browse/CODEC-125?focusedCommentId=13071566&page=com.atlassian.jira.plugin.system.issuetabpanels:
     * comment-tabpanel#comment-13071566
     * 
     * @throws EncoderException
     */
    @Test
    public void testEncodeGna() throws EncoderException {
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        bmpm.encode("gna");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLangIllegalArgumentException() {
        Rule.getInstance(NameType.GENERIC, RuleType.APPROX, "noSuchLanguage");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidLangIllegalStateException() {
        Lang.loadFromResource("thisIsAMadeUpResourceName", Languages.getInstance(NameType.GENERIC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLanguageIllegalArgumentException() {
        Languages.getInstance("thereIsNoSuchLanguage");
    }

    @Test(timeout = 10000L)
    public void testLongestEnglishSurname() throws EncoderException {
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        bmpm.encode("MacGhilleseatheanaich");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeIndexForRuleMatchIndexOutOfBoundsException() {
        Rule r = new Rule("a", "", "", new Rule.Phoneme("", Languages.ANY_LANGUAGE));
        r.patternAndContextMatches("bob", -1);
    }

    @Test
    public void testSetConcat() {
        BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setConcat(false);
        assertEquals("Should be able to set concat to false", false, bmpm.isConcat());
    }

    @Test
    public void testSetNameTypeAsh() {
        BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setNameType(NameType.ASHKENAZI);
        assertEquals("Name type should have been set to ash", NameType.ASHKENAZI, bmpm.getNameType());
    }

    @Test
    public void testSetRuleTypeExact() {
        BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setRuleType(RuleType.EXACT);
        assertEquals("Rule type should have been set to exact", RuleType.EXACT, bmpm.getRuleType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRuleTypeToRulesIllegalArgumentException() {
        BeiderMorseEncoder bmpm = new BeiderMorseEncoder();
        bmpm.setRuleType(RuleType.RULES);
    }

    /**
     * Runs between 1.1 and 13 seconds at length 40 for me (Gary Gregory, 2011/08/06)
     *  
     * @throws EncoderException
     */
    @Test(/* timeout = 20000L */)
    public void testSpeedCheck() throws EncoderException {
        char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'o', 'u' };
        BeiderMorseEncoder bmpm = createGenericApproxEncoder();
        StringBuffer stringBuffer = new StringBuffer();
        Random rand = new Random();
        stringBuffer.append(chars[rand.nextInt(chars.length)]);
        long start;
        for (int i = 0; i < 40; i++) {
            start = System.currentTimeMillis();
            // System.out.println(i + " String to encode:" + stringBuffer.toString());
            bmpm.encode(stringBuffer.toString());
            stringBuffer.append(chars[rand.nextInt(chars.length)]);
            // System.out.println(i + " Elapsed time in ms:" + (System.currentTimeMillis() - start));
        }
    }
}
