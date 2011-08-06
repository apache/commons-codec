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
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

/**
 * Tests Rule.
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class RuleTest {
    private Rule.Phoneme[][] makePhonemes() {
        String[][] words = {
                { "rinD", "rinDlt", "rina", "rinalt", "rino", "rinolt", "rinu", "rinult" },
                { "dortlaj", "dortlej", "ortlaj", "ortlej", "ortlej-dortlaj" } };
        Rule.Phoneme[][] phonemes = new Rule.Phoneme[words.length][];

        for (int i = 0; i < words.length; i++) {
            String[] words_i = words[i];
            Rule.Phoneme[] phonemes_i = phonemes[i] = new Rule.Phoneme[words_i.length];
            for (int j = 0; j < words_i.length; j++) {
                phonemes_i[j] = new Rule.Phoneme(words_i[j], Languages.NO_LANGUAGES);
            }
        }

        return phonemes;
    }

    @Test
    public void phonemeComparedToSelfIsZero() {
        for (Rule.Phoneme[] phs : makePhonemes()) {
            for (Rule.Phoneme ph : phs) {
                assertEquals("Phoneme compared to itself should be zero: " + ph.getPhonemeText(), 0, ph.compareTo(ph));
            }
        }
    }

    @Test
    public void phonemeComparedToLaterIsNegative() {
        for (Rule.Phoneme[] phs : makePhonemes()) {
            for (int i = 0; i < phs.length; i++) {
                for (int j = i + 1; j < phs.length; j++) {
                    int c = phs[i].compareTo(phs[j]);

                    assertThat("Comparing " + phs[i].getPhonemeText() + " to " + phs[j].getPhonemeText() + " should be negative", c,
                            new NegativeIntegerBaseMatcher());
                }
            }
        }
    }

    private static class NegativeIntegerBaseMatcher extends BaseMatcher<Integer> {
        public boolean matches(Object item) {
            return ((Integer) item) < 0;
        }

        public void describeTo(Description description) {
            description.appendText("value should be negative");
        }
    }
}
