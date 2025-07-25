/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.Resources;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a Daitch-Mokotoff Soundex value.
 * <p>
 * The Daitch-Mokotoff Soundex algorithm is a refinement of the Russel and American Soundex algorithms, yielding greater
 * accuracy in matching especially Slavish and Yiddish surnames with similar pronunciation but differences in spelling.
 * </p>
 * <p>
 * The main differences compared to the other Soundex variants are:
 * </p>
 * <ul>
 * <li>coded names are 6 digits long
 * <li>the initial character of the name is coded
 * <li>rules to encoded multi-character n-grams
 * <li>multiple possible encodings for the same name (branching)
 * </ul>
 * <p>
 * This implementation supports branching, depending on the used method:
 * <ul>
 * <li>{@link #encode(String)} - branching disabled, only the first code will be returned
 * <li>{@link #soundex(String)} - branching enabled, all codes will be returned, separated by '|'
 * </ul>
 * <p>
 * Note: This implementation has additional branching rules compared to the original description of the algorithm. The
 * rules can be customized by overriding the default rules contained in the resource file
 * {@code org/apache/commons/codec/language/dmrules.txt}.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see Soundex
 * @see <a href="https://en.wikipedia.org/wiki/Daitch%E2%80%93Mokotoff_Soundex"> Wikipedia - Daitch-Mokotoff Soundex</a>
 * @see <a href="http://www.avotaynu.com/soundex.htm">Avotaynu - Soundexing and Genealogy</a>
 * @since 1.10
 */
public class DaitchMokotoffSoundex implements StringEncoder {

    /**
     * Inner class representing a branch during DM Soundex encoding.
     */
    private static final class Branch {
        private final StringBuilder builder;
        private String cachedString;
        private String lastReplacement;

        private Branch() {
            builder = new StringBuilder();
        }

        /**
         * Creates a new branch, identical to this branch.
         *
         * @return a new, identical branch
         */
        private Branch createBranch() {
            final Branch branch = new Branch();
            branch.builder.append(toString());
            branch.lastReplacement = this.lastReplacement;
            return branch;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Branch)) {
                return false;
            }
            return toString().equals(((Branch) other).toString());
        }

        /**
         * Finish this branch by appending '0's until the maximum code length has been reached.
         */
        private void finish() {
            while (builder.length() < MAX_LENGTH) {
                builder.append('0');
                cachedString = null;
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        /**
         * Process the next replacement to be added to this branch.
         *
         * @param replacement
         *            the next replacement to append.
         * @param forceAppend
         *            indicates if the default processing shall be overridden.
         */
        private void processNextReplacement(final String replacement, final boolean forceAppend) {
            final boolean append = lastReplacement == null || !lastReplacement.endsWith(replacement) || forceAppend;
            if (append && builder.length() < MAX_LENGTH) {
                builder.append(replacement);
                // remove all characters after the maximum length
                if (builder.length() > MAX_LENGTH) {
                    builder.delete(MAX_LENGTH, builder.length());
                }
                cachedString = null;
            }
            lastReplacement = replacement;
        }

        @Override
        public String toString() {
            if (cachedString == null) {
                cachedString = builder.toString();
            }
            return cachedString;
        }
    }

    /**
     * Inner class for storing rules.
     */
    private static final class Rule {
        private static final Pattern PIPE = Pattern.compile("\\|");
        private final String pattern;
        private final String[] replacementAtStart;
        private final String[] replacementBeforeVowel;
        private final String[] replacementDefault;

        private Rule(final String pattern, final String replacementAtStart, final String replacementBeforeVowel,
                final String replacementDefault) {
            this.pattern = pattern;
            this.replacementAtStart = PIPE.split(replacementAtStart);
            this.replacementBeforeVowel = PIPE.split(replacementBeforeVowel);
            this.replacementDefault = PIPE.split(replacementDefault);
        }

        private int getPatternLength() {
            return pattern.length();
        }

        private String[] getReplacements(final String context, final boolean atStart) {
            if (atStart) {
                return replacementAtStart;
            }

            final int nextIndex = getPatternLength();
            final boolean nextCharIsVowel = nextIndex < context.length() && isVowel(context.charAt(nextIndex));
            if (nextCharIsVowel) {
                return replacementBeforeVowel;
            }

            return replacementDefault;
        }

        private boolean isVowel(final char ch) {
            return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
        }

        private boolean matches(final String context) {
            return context.startsWith(pattern);
        }

        @Override
        public String toString() {
            return String.format("%s=(%s,%s,%s)", pattern, Arrays.asList(replacementAtStart),
                    Arrays.asList(replacementBeforeVowel), Arrays.asList(replacementDefault));
        }
    }

    /**
     * The NUL character.
     */
    private static final char NUL = '\0';

    private static final String COMMENT = "//";

    private static final String DOUBLE_QUOTE = "\"";

    private static final String MULTILINE_COMMENT_END = "*/";

    private static final String MULTILINE_COMMENT_START = "/*";

    /** The resource file containing the replacement and folding rules */
    private static final String RESOURCE_FILE = "/org/apache/commons/codec/language/dmrules.txt";

    /** The code length of a DM Soundex value. */
    private static final int MAX_LENGTH = 6;

    /** Transformation rules indexed by the first character of their pattern. */
    private static final Map<Character, List<Rule>> RULES = new HashMap<>();

    /** Folding rules. */
    private static final Map<Character, Character> FOLDINGS = new HashMap<>();

    private static final Pattern EQUAL = Pattern.compile("=");

    private static final Pattern SPACES = Pattern.compile("\\s+");

    static {
        try (Scanner scanner = new Scanner(Resources.getInputStream(RESOURCE_FILE), CharEncoding.UTF_8)) {
            parseRules(scanner, RESOURCE_FILE, RULES, FOLDINGS);
        }
        // sort RULES by pattern length in descending order
        RULES.forEach((k, v) -> v.sort((rule1, rule2) -> rule2.getPatternLength() - rule1.getPatternLength()));
    }

    private static void parseRules(final Scanner scanner, final String location, final Map<Character, List<Rule>> ruleMapping,
            final Map<Character, Character> asciiFoldings) {
        int currentLine = 0;
        boolean inMultilineComment = false;
        while (scanner.hasNextLine()) {
            currentLine++;
            final String rawLine = scanner.nextLine();
            String line = rawLine;
            if (inMultilineComment) {
                if (line.endsWith(MULTILINE_COMMENT_END)) {
                    inMultilineComment = false;
                }
                continue;
            }
            if (line.startsWith(MULTILINE_COMMENT_START)) {
                inMultilineComment = true;
            } else {
                // discard comments
                final int cmtI = line.indexOf(COMMENT);
                if (cmtI >= 0) {
                    line = line.substring(0, cmtI);
                }
                // trim leading-trailing whitespace
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // empty lines can be safely skipped
                }
                if (line.contains("=")) {
                    // folding
                    final String[] parts = EQUAL.split(line);
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Malformed folding statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    final String leftCharacter = parts[0];
                    final String rightCharacter = parts[1];
                    if (leftCharacter.length() != 1 || rightCharacter.length() != 1) {
                        throw new IllegalArgumentException(
                                "Malformed folding statement - patterns are not single characters: " + rawLine + " in " + location);
                    }
                    asciiFoldings.put(leftCharacter.charAt(0), rightCharacter.charAt(0));
                } else {
                    // rule
                    final String[] parts = SPACES.split(line);
                    if (parts.length != 4) {
                        throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    try {
                        final String pattern = stripQuotes(parts[0]);
                        final String replacement1 = stripQuotes(parts[1]);
                        final String replacement2 = stripQuotes(parts[2]);
                        final String replacement3 = stripQuotes(parts[3]);
                        final Rule r = new Rule(pattern, replacement1, replacement2, replacement3);
                        final char patternKey = r.pattern.charAt(0);
                        final List<Rule> rules = ruleMapping.computeIfAbsent(patternKey, k -> new ArrayList<>());
                        rules.add(r);
                    } catch (final IllegalArgumentException e) {
                        throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
                    }
                }
            }
        }
    }

    private static String stripQuotes(String str) {
        if (str.startsWith(DOUBLE_QUOTE)) {
            str = str.substring(1);
        }
        if (str.endsWith(DOUBLE_QUOTE)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /** Whether to use ASCII folding prior to encoding. */
    private final boolean folding;

    /**
     * Creates a new instance with ASCII-folding enabled.
     */
    public DaitchMokotoffSoundex() {
        this(true);
    }

    /**
     * Creates a new instance.
     * <p>
     * With ASCII-folding enabled, certain accented characters will be transformed to equivalent ASCII characters, for example
     * è -&gt; e.
     * </p>
     *
     * @param folding
     *            if ASCII-folding shall be performed before encoding
     */
    public DaitchMokotoffSoundex(final boolean folding) {
        this.folding = folding;
    }

    /**
     * Performs a cleanup of the input string before the actual Soundex transformation.
     * <p>
     * Removes all whitespace characters and performs ASCII folding if enabled.
     * </p>
     *
     * @param input
     *            the input string to clean up.
     * @return a cleaned up string.
     */
    private String cleanup(final String input) {
        final StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.isWhitespace(ch) || !Character.isLetter(ch)) {
                continue;
            }
            ch = Character.toLowerCase(ch);
            final Character character = FOLDINGS.get(ch);
            if (folding && character != null) {
                ch = character;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Encodes an Object using the Daitch-Mokotoff Soundex algorithm without branching.
     * <p>
     * This method is provided in order to satisfy the requirements of the Encoder interface, and will throw an
     * EncoderException if the supplied object is not of type {@link String}.
     * </p>
     *
     * @see #soundex(String)
     * @param obj
     *            Object to encode.
     * @return An object (of type {@link String}) containing the DM Soundex code, which corresponds to the String
     *         supplied.
     * @throws EncoderException
     *             if the parameter supplied is not of type {@link String}.
     * @throws IllegalArgumentException
     *             if a character is not mapped.
     */
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException(
                    "Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
        }
        return encode((String) obj);
    }

    /**
     * Encodes a String using the Daitch-Mokotoff Soundex algorithm without branching.
     *
     * @see #soundex(String)
     * @param source
     *            A String object to encode.
     * @return A DM Soundex code corresponding to the String supplied.
     * @throws IllegalArgumentException
     *             if a character is not mapped.
     */
    @Override
    public String encode(final String source) {
        if (source == null) {
            return null;
        }
        return soundex(source, false)[0];
    }

    /**
     * Encodes a String using the Daitch-Mokotoff Soundex algorithm with branching.
     * <p>
     * In case a string is encoded into multiple codes (see branching rules), the result will contain all codes,
     * separated by '|'.
     * </p>
     * <p>
     * Example: the name "AUERBACH" is encoded as both
     * </p>
     * <ul>
     * <li>097400</li>
     * <li>097500</li>
     * </ul>
     * <p>
     * Thus the result will be "097400|097500".
     * </p>
     *
     * @param source
     *            A String object to encode.
     * @return A string containing a set of DM Soundex codes corresponding to the String supplied.
     * @throws IllegalArgumentException
     *             if a character is not mapped.
     */
    public String soundex(final String source) {
        return String.join("|", soundex(source, true));
    }

    /**
     * Perform the actual DM Soundex algorithm on the input string.
     *
     * @param source
     *            A String object to encode.
     * @param branching
     *            If branching shall be performed.
     * @return A string array containing all DM Soundex codes corresponding to the String supplied depending on the
     *         selected branching mode.
     */
    private String[] soundex(final String source, final boolean branching) {
        if (source == null) {
            return null;
        }
        final String input = cleanup(source);
        final Set<Branch> currentBranches = new LinkedHashSet<>();
        currentBranches.add(new Branch());
        char lastChar = NUL;
        for (int index = 0; index < input.length(); index++) {
            final char ch = input.charAt(index);
            final String inputContext = input.substring(index);
            final List<Rule> rules = RULES.get(ch);
            if (rules == null) {
                continue;
            }
            // use an EMPTY_LIST to avoid false positive warnings wrt potential null pointer access
            final List<Branch> nextBranches = branching ? new ArrayList<>() : Collections.emptyList();
            for (final Rule rule : rules) {
                if (rule.matches(inputContext)) {
                    if (branching) {
                        nextBranches.clear();
                    }
                    final String[] replacements = rule.getReplacements(inputContext, lastChar == '\0');
                    final boolean branchingRequired = replacements.length > 1 && branching;
                    for (final Branch branch : currentBranches) {
                        for (final String nextReplacement : replacements) {
                            // if we have multiple replacements, always create a new branch
                            final Branch nextBranch = branchingRequired ? branch.createBranch() : branch;
                            // special rule: occurrences of mn or nm are treated differently
                            final boolean force = lastChar == 'm' && ch == 'n' || lastChar == 'n' && ch == 'm';
                            nextBranch.processNextReplacement(nextReplacement, force);
                            if (!branching) {
                                break;
                            }
                            nextBranches.add(nextBranch);
                        }
                    }
                    if (branching) {
                        currentBranches.clear();
                        currentBranches.addAll(nextBranches);
                    }
                    index += rule.getPatternLength() - 1;
                    break;
                }
            }
            lastChar = ch;
        }
        final String[] result = new String[currentBranches.size()];
        int index = 0;
        for (final Branch branch : currentBranches) {
            branch.finish();
            result[index++] = branch.toString();
        }
        return result;
    }
}
