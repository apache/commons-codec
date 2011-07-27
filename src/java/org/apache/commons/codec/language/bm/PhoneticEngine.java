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
    private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap<NameType, Set<String>>(NameType.class);

    static {
        NAME_PREFIXES.put(NameType.ASHKENAZI,
                Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("bar", "ben", "da", "de", "van", "von"))));
        NAME_PREFIXES.put(NameType.SEPHARDIC, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("al", "el", "da", "dal", "de",
                "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
        NAME_PREFIXES.put(NameType.GENERIC, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("da", "dal", "de", "del", "dela",
                "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))));
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

    /**
     * Encodes a string to its phonetic representation.
     * 
     * @param input
     *            the String to encode
     * @return the encoding of the input
     */
    public String encode(String input) {
        Set<String> languageSet = this.lang.guessLanguages(input);
        return phoneticUtf8(input, languageSet);
    }

    /**
     * Encodes an input string into an output phonetic representation, given a set of possible origin languages.
     * 
     * @param input
     *            String to phoneticise; a String with dashes or spaces separating each word
     * @param languageSet
     * @return a phonetic representation of the input; a String containing '-'-separated phonetic representations of the input
     */
    public String phoneticUtf8(String input, final Set<String> languageSet) {
        List<Rule> rules = Rule.instance(this.nameType, RuleType.RULES, languageSet);
        List<Rule> finalRules1 = Rule.instance(this.nameType, this.ruleType, "common");
        List<Rule> finalRules2 = Rule.instance(this.nameType, this.ruleType, languageSet);

        // tidy the input
        // lower case is a locale-dependent operation
        input = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();

        if (this.nameType == NameType.GENERIC) {
            for (String l : NAME_PREFIXES.get(this.nameType)) {
                // handle generic prefixes
                if (input.startsWith(l + " ")) {
                    // check for any prefix in the words list
                    String remainder = input.substring(l.length() + 1); // input without the prefix
                    String combined = l + remainder; // input with prefix without space
                    return encode(remainder) + "-" + encode(combined);
                }
                // fixme: this case is invariant on l
                else if (input.length() >= 2 && input.substring(0, 2).equals("d'")) // check for d'
                {
                    String remainder = input.substring(2);
                    String combined = "d" + remainder;
                    return encode(remainder) + "-" + encode(combined);
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

        String phonetic = "";

        // loop over each char in the input - we will handle the increment manually
        for (int i = 0; i < input.length();) {
            RulesApplication rulesApplication = new RulesApplication(rules, languageSet, input, phonetic, i).invoke();
            i = rulesApplication.getI();
            phonetic = rulesApplication.getPhonetic();
        }

        phonetic = applyFinalRules(phonetic, finalRules1, languageSet, false);
        phonetic = applyFinalRules(phonetic, finalRules2, languageSet, true);

        return phonetic;
    }

    private String removeDuplicateAlternates(final String phonetic) {
        List<String> altArray = splitOnPipe(phonetic);

        String result = "|";
        for (String alt : altArray) {
            if (!result.contains("|" + alt + "|")) {
                result += (alt + "|");
            }
        }

        result = result.substring(1, result.length() - 1);
        return result;
    }

    /**
     * Applied to a single alternative at a time -- not to a parenthisized list it removes all embedded bracketed attributes, logically-ands
     * them together, and places them at the end.
     * 
     * However if strip is true, this can indeed remove embedded bracketed attributes from a parenthesized list
     * 
     * @param input
     * @param strip
     * @return
     */
    private String normalizeLanguageAttributes(final String input, final boolean strip) {
        String text = input;
        Set<String> langs = new HashSet<String>();

        int bracketStart;
        while ((bracketStart = text.indexOf('[')) != -1) {
            int bracketEnd = text.indexOf(']', bracketStart);
            if (bracketEnd == -1) {
                throw new IllegalArgumentException("no closing square bracket in: " + text);
            }

            String body = text.substring(bracketStart + 1, bracketEnd);
            langs.addAll(Arrays.asList(body.split("[+]")));
            text = text.substring(0, bracketStart) + text.substring(bracketEnd + 1);
        }

        if (langs.isEmpty() || strip) {
            return text;
        } else if (langs.contains(Languages.ANY)) {
            return "[" + Languages.ANY + "]";
        } else {
            return text + "[" + join(langs, "+") + "]";
        }
    }

    private String applyFinalRules(String phonetic, List<Rule> finalRules, Set<String> languageArg, boolean strip) {
        if (finalRules == null) {
            throw new NullPointerException("finalRules can not be null");
        }
        if (finalRules.isEmpty()) {
            return phonetic;
        }

        phonetic = expand(phonetic);
        // must protect | in [] as split takes a regex, not a string literal
        List<String> phoneticArray = splitOnPipe(phonetic);

        for (int k = 0; k < phoneticArray.size(); k++) {
            // log("k: " + k);

            String aPhonetic = phoneticArray.get(k);
            String phonetic2 = "";

            String phoneticx = normalizeLanguageAttributes(aPhonetic, true);
            for (int i = 0; i < aPhonetic.length();) {
                // we will handle the increment manually
                if (aPhonetic.substring(i, i + 1).equals("[")) {
                    int attribStart = i;
                    i++;
                    while (true) {
                        i++;
                        String nextChar = aPhonetic.substring(i, i + 1);
                        if (nextChar.equals("]")) {
                            phonetic2 += aPhonetic.substring(attribStart, i);
                            break;
                        }
                    }

                    continue;
                }

                RulesApplication rulesApplication = new RulesApplication(finalRules, languageArg, phoneticx, phonetic2, i).invoke();
                boolean found = rulesApplication.isFound();
                phonetic2 = rulesApplication.getPhonetic();

                if (!found) {
                    phonetic2 += aPhonetic.substring(i, i + 1);
                }

                i = rulesApplication.getI();
            }

            phoneticArray.set(k, expand(phonetic2));
        }

        phonetic = join(phoneticArray, "|");
        if (strip) {
            phonetic = normalizeLanguageAttributes(phonetic, true);
        }
        if (!phonetic.contains("|")) {
            phonetic = "(" + removeDuplicateAlternates(phonetic) + ")";
        }

        return phonetic;
    }

    private String expand(String phonetic) {
        int altStart = phonetic.indexOf('(');
        if (altStart == -1) {
            return normalizeLanguageAttributes(phonetic, false);
        }

        String prefix = phonetic.substring(0, altStart);
        altStart++;
        int altEnd = phonetic.indexOf(')');
        String altString = phonetic.substring(altStart, altEnd);
        altEnd++;
        String suffix = phonetic.substring(altEnd);
        List<String> altArray = splitOnPipe(altString);

        String result = "";
        for (String alt : altArray) {
            String alternate = expand(prefix + alt + suffix);
            if (alternate.length() != 0 && !alternate.equals("[any]")) {
                if (result.length() > 0) {
                    result += "|";
                }
                result += alternate;
            }
        }

        return result;
    }

    /**
     * Tests for compatible language rules to do so, apply the rule, expand the results, and detect alternatives with incompatible
     * attributes then drop each alternative that has incompatible attributes and keep those that are compatible if there are no compatible
     * alternatives left, return false otherwise return the compatible alternatives
     * 
     * @param phonetic
     * @param target
     * @param languageArg
     * @return a String or null.
     */
    private String applyRuleIfCompatible(String phonetic, String target, Set<String> languageArg) {
        String candidate = phonetic + target;
        if (!candidate.contains("[")) {
            return candidate;
        }

        candidate = expand(candidate);
        List<String> candidateArray = splitOnPipe(candidate);

        candidate = "";
        boolean found = false;

        for (String thisCandidate : candidateArray) {
            if (!languageArg.contains(Languages.ANY)) {
                thisCandidate = normalizeLanguageAttributes(thisCandidate + "[" + languageArg + "]", false);
            }

            if (!thisCandidate.equals("[0]")) {
                found = true;
                if (candidate.length() != 0) {
                    candidate += "|";
                }
                candidate += thisCandidate;
            }
        }

        if (!found) {
            return null; // eugh!
        }
        if (candidate.contains("|")) {
            candidate = "(" + candidate + ")";
        }

        return candidate;
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

    private static List<String> splitOnPipe(String str) {
        List<String> res = new ArrayList<String>();

        while (true) {
            int i = str.indexOf('|');
            if (i < 0) {
                res.add(str);
                break;
            }

            res.add(str.substring(0, i));
            str = str.substring(i + 1);
        }

        return res;
    }

    private class RulesApplication {
        private final List<Rule> finalRules;
        private final Set<String> languageArg;
        private final String input;

        private String phonetic;
        private int i;
        private boolean found;

        public RulesApplication(List<Rule> finalRules, Set<String> languageArg, String input, String phonetic, int i) {
            if (finalRules == null) {
                throw new NullPointerException("The finalRules argument must not be null");
            }
            this.finalRules = finalRules;
            this.languageArg = languageArg;
            this.phonetic = phonetic;
            this.input = input;
            this.i = i;
        }

        public String getPhonetic() {
            return this.phonetic;
        }

        public int getI() {
            return this.i;
        }

        public boolean isFound() {
            return this.found;
        }

        public RulesApplication invoke() {
            this.found = false;
            int patternLength = 0;
            RULES: for (Rule rule : this.finalRules) {
                String pattern = rule.getPattern();
                patternLength = pattern.length();
                // log("trying pattern: " + pattern);

                if (!rule.patternAndContextMatches(this.input, this.i) || !rule.languageMatches(this.languageArg)) {
                    // log("no match");
                    continue RULES;
                }

                String candidate = applyRuleIfCompatible(this.phonetic, rule.getPhoneme(), this.languageArg);

                if (candidate == null || candidate.length() == 0) {
                    // log("no candidate");
                    continue RULES;
                }
                this.phonetic = candidate;
                this.found = true;
                break RULES;
            }

            if (!this.found) {
                patternLength = 1;
            }

            this.i += patternLength;
            return this;
        }
    }
}
