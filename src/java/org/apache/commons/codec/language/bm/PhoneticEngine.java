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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * Converts words into potential phonetic representations.
 * </p>
 * <p>
 * This is a two-stage process. Firstly, the word is converted into a phonetic representation that takes into account the likely source
 * language. Next, this phonetic representation is converted into a pan-european 'average' representation, allowing comparison between
 * different versions of essentially the same word from different languages.
 * </p>
 * <p>
 * This class is intentionally immutable. If you wish to alter the settings for a PhoneticEngine, you must make a new one with the updated
 * settings. This makes the class thread-safe.
 * </p>
 * <p>
 * Ported from phoneticengine.php
 * </p>
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class PhoneticEngine {

    static final class PhonemeBuilder {

        public static PhonemeBuilder empty(Languages.LanguageSet languages) {
            return new PhonemeBuilder(Collections.singleton(new Rule.Phoneme("", languages)));
        }

        private final Set<Rule.Phoneme> phonemes;

        private PhonemeBuilder(Set<Rule.Phoneme> phonemes) {
            this.phonemes = phonemes;
        }

        public PhonemeBuilder append(CharSequence str) {
            Set<Rule.Phoneme> newPhonemes = new HashSet<Rule.Phoneme>();

            for (Rule.Phoneme ph : this.phonemes) {
                newPhonemes.add(ph.append(str));
            }

            return new PhonemeBuilder(newPhonemes);
        }

        public PhonemeBuilder apply(Rule.PhonemeExpr phonemeExpr) {
            Set<Rule.Phoneme> newPhonemes = new HashSet<Rule.Phoneme>();

            for (Rule.Phoneme left : this.phonemes) {
                for (Rule.Phoneme right : phonemeExpr.getPhonemes()) {
                    Rule.Phoneme join = left.join(right);
                    if (!join.getLanguages().isEmpty()) {
                        newPhonemes.add(join);
                    }
                }
            }

            return new PhonemeBuilder(newPhonemes);
        }

        public Set<Rule.Phoneme> getPhonemes() {
            return this.phonemes;
        }

        public String makeString() {

            StringBuilder sb = new StringBuilder();
            // System.err.println(this.phonemes.getClass());

            for (Rule.Phoneme ph : this.phonemes) {
                if (sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(ph.getPhonemeText());
            }

            return sb.toString();
        }
    }

    private static final class RulesApplication {
        private final List<Rule> finalRules;
        private final CharSequence input;

        private PhonemeBuilder phonemeBuilder;
        private int i;
        private boolean found;

        public RulesApplication(List<Rule> finalRules, CharSequence input, PhonemeBuilder phonemeBuilder, int i) {
            if (finalRules == null) {
                throw new NullPointerException("The finalRules argument must not be null");
            }
            this.finalRules = finalRules;
            this.phonemeBuilder = phonemeBuilder;
            this.input = input;
            this.i = i;
        }

        public int getI() {
            return this.i;
        }

        public PhonemeBuilder getPhonemeBuilder() {
            return this.phonemeBuilder;
        }

        public RulesApplication invoke() {
            this.found = false;
            int patternLength = 0;
            RULES: for (Rule rule : this.finalRules) {
                String pattern = rule.getPattern();
                patternLength = pattern.length();
                // log("trying pattern: " + pattern);

                if (!rule.patternAndContextMatches(this.input, this.i)) {
                    // log("no match");
                    continue RULES;
                }

                this.phonemeBuilder = this.phonemeBuilder.apply(rule.getPhoneme());
                this.found = true;
                break RULES;
            }

            if (!this.found) {
                patternLength = 1;
            }

            this.i += patternLength;
            return this;
        }

        public boolean isFound() {
            return this.found;
        }
    }

    private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap<NameType, Set<String>>(NameType.class);

    static {
        NAME_PREFIXES.put(NameType.ASHKENAZI,
                Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("bar", "ben", "da", "de", "van", "von"))));
        NAME_PREFIXES.put(NameType.SEPHARDIC, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("al", "el", "da", "dal", "de",
                "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
        NAME_PREFIXES.put(NameType.GENERIC, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("da", "dal", "de", "del", "dela",
                "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
    }

    private static CharSequence cacheSubSequence(final CharSequence cached) {
        // return cached;
        final CharSequence[][] cache = new CharSequence[cached.length()][cached.length()];
        return new CharSequence() {
            public char charAt(int index) {
                return cached.charAt(index);
            }

            public int length() {
                return cached.length();
            }

            public CharSequence subSequence(int start, int end) {
                if (start == end)
                    return "";

                CharSequence res = cache[start][end - 1];
                if (res == null) {
                    res = cached.subSequence(start, end);
                    cache[start][end - 1] = res;
                }
                return res;
            }
        };
    }

    private static String join(Iterable<String> strings, String sep) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> si = strings.iterator();
        if (si.hasNext()) {
            sb.append(si.next());
        }
        while (si.hasNext()) {
            sb.append(sep).append(si.next());
        }

        return sb.toString();
    }

    private final Lang lang;

    private final NameType nameType;

    private final RuleType ruleType;

    private final boolean concat;

    /**
     * Generates a new, fully-configured phonetic engine.
     * 
     * @param nameType
     *            the type of names it will use
     * @param ruleType
     *            the type of rules it will apply
     * @param concat
     *            if it will concatenate multiple encodings
     */
    public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat) {
        if (ruleType == RuleType.RULES) {
            throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
        }
        this.nameType = nameType;
        this.ruleType = ruleType;
        this.concat = concat;
        this.lang = Lang.instance(nameType);
    }

    private PhonemeBuilder applyFinalRules(PhonemeBuilder phonemeBuilder, List<Rule> finalRules) {
        if (finalRules == null) {
            throw new NullPointerException("finalRules can not be null");
        }
        if (finalRules.isEmpty()) {
            return phonemeBuilder;
        }

        Set<Rule.Phoneme> phonemes = new TreeSet<Rule.Phoneme>();

        for (Rule.Phoneme phoneme : phonemeBuilder.getPhonemes()) {
            PhonemeBuilder subBuilder = PhonemeBuilder.empty(phoneme.getLanguages());
            CharSequence phonemeText = cacheSubSequence(phoneme.getPhonemeText());
            // System.err.println("Expanding: " + phonemeText);

            for (int i = 0; i < phonemeText.length();) {
                RulesApplication rulesApplication = new RulesApplication(finalRules, phonemeText, subBuilder, i).invoke();
                boolean found = rulesApplication.isFound();
                subBuilder = rulesApplication.getPhonemeBuilder();

                if (!found) {
                    // System.err.println("Not found. Appending as-is");
                    subBuilder = subBuilder.append(phonemeText.subSequence(i, i + 1));
                }

                i = rulesApplication.getI();

                // System.err.println(phonemeText + " " + i + ": " + subBuilder.makeString());
            }

            // System.err.println("Expanded to: " + subBuilder.makeString());
            // System.err.println("phenomes in collection of type: " + subBuilder.getPhonemes().getClass());
            phonemes.addAll(subBuilder.getPhonemes());
        }

        return new PhonemeBuilder(phonemes);
    }

    /**
     * Encodes a string to its phonetic representation.
     * 
     * @param input
     *            the String to encode
     * @return the encoding of the input
     */
    public String encode(String input) {
        Languages.LanguageSet languageSet = this.lang.guessLanguages(input);
        return encode(input, languageSet);
    }

    /**
     * Encodes an input string into an output phonetic representation, given a set of possible origin languages.
     * 
     * @param input
     *            String to phoneticise; a String with dashes or spaces separating each word
     * @param languageSet
     * @return a phonetic representation of the input; a String containing '-'-separated phonetic representations of the input
     */
    public String encode(String input, final Languages.LanguageSet languageSet) {
        final List<Rule> rules = Rule.getInstance(this.nameType, RuleType.RULES, languageSet);
        final List<Rule> finalRules1 = Rule.getInstance(this.nameType, this.ruleType, "common");
        final List<Rule> finalRules2 = Rule.getInstance(this.nameType, this.ruleType, languageSet);
        // System.err.println("Languages: " + languageSet);
        // System.err.println("Rules: " + rules);

        // tidy the input
        // lower case is a locale-dependent operation
        input = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();

        if (this.nameType == NameType.GENERIC) {
            if (input.length() >= 2 && input.substring(0, 2).equals("d'")) { // check for d'
                String remainder = input.substring(2);
                String combined = "d" + remainder;
                return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
            }
            for (String l : NAME_PREFIXES.get(this.nameType)) {
                // handle generic prefixes
                if (input.startsWith(l + " ")) {
                    // check for any prefix in the words list
                    String remainder = input.substring(l.length() + 1); // input without the prefix
                    String combined = l + remainder; // input with prefix without space
                    return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
                }
            }
        }

        final List<String> words = Arrays.asList(input.split("\\s+"));
        final List<String> words2 = new ArrayList<String>();

        switch (this.nameType) {
        case SEPHARDIC:
            for (String aWord : words) {
                String[] parts = aWord.split("'");
                String lastPart = parts[parts.length - 1];
                words2.add(lastPart);
            }
            words2.removeAll(NAME_PREFIXES.get(this.nameType));
            break;
        case ASHKENAZI:
            words2.addAll(words);
            words2.removeAll(NAME_PREFIXES.get(this.nameType));
            break;
        case GENERIC:
            words2.addAll(words);
            break;
        default:
            throw new IllegalStateException("Unreachable case: " + this.nameType);
        }

        if (this.concat) {
            // concat mode enabled
            input = join(words2, " ");
        } else if (words2.size() == 1) {
            // not a multi-word name
            input = words.iterator().next();
        } else {
            // encode each word in a multi-word name separately (normally used for approx matches)
            StringBuilder result = new StringBuilder();
            for (String word : words2) {
                result.append("-").append(encode(word));
            }
            // return the result without the leading "-"
            return result.substring(1);
        }

        PhonemeBuilder phonemeBuilder = PhonemeBuilder.empty(languageSet);

        // loop over each char in the input - we will handle the increment manually
        CharSequence inputCache = cacheSubSequence(input);
        for (int i = 0; i < inputCache.length();) {
            RulesApplication rulesApplication = new RulesApplication(rules, inputCache, phonemeBuilder, i).invoke();
            i = rulesApplication.getI();
            phonemeBuilder = rulesApplication.getPhonemeBuilder();
            // System.err.println(input + " " + i + ": " + phonemeBuilder.makeString());
        }

        // System.err.println("Applying general rules");
        phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules1);
        // System.err.println("Now got: " + phonemeBuilder.makeString());
        // System.err.println("Applying language-specific rules");
        phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules2);
        // System.err.println("Now got: " + phonemeBuilder.makeString());
        // System.err.println("Done");

        return phonemeBuilder.makeString();
    }

    /**
     * Gets the Lang language guessing rules being used.
     * 
     * @return the Lang in use
     */
    public Lang getLang() {
        return this.lang;
    }

    /**
     * Gets the NameType being used.
     * 
     * @return the NameType in use
     */
    public NameType getNameType() {
        return this.nameType;
    }

    /**
     * Gets the RuleType being used.
     * 
     * @return the RuleType in use
     */
    public RuleType getRuleType() {
        return this.ruleType;
    }

    /**
     * Gets if multiple phonetic encodings are concatenated or if just the first one is kept.
     * 
     * @return true if multiple phonetic encodings are returned, false if just the first is.
     */
    public boolean isConcat() {
        return this.concat;
    }
}
