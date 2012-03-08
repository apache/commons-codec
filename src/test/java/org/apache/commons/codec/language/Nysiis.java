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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * THIS CLASS LIVES IN THE TEST DIRECTORY UNTIL IT IS FULLY BAKED. 
 * 
 * Encodes a string into a NYSIIS value. NYSIIS is an encoding used to relate similar names, but can also be used as a
 * general purpose scheme to find word with similar phonemes.
 * 
 * <p>
 * NYSIIS features an accuracy increase of 2.7% over the traditional Soundex algorithm.
 * </p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/NYSIIS">http://en.wikipedia.org/wiki/NYSIIS</a>
 * @see <a href="http://www.dropby.com/NYSIIS.html">http://www.dropby.com/NYSIIS.html</a>
 * @see Soundex
 * @version $Id: Nysiis.java 669755 2008-06-20 01:21:52Z sebb $
 */
public class Nysiis implements StringEncoder {

    private static final char[] CHARS_A = new char[] { 'A' };
    private static final char[] CHARS_AF = new char[] { 'A', 'F' };
    private static final char[] CHARS_C = new char[] { 'C' };
    private static final char[] CHARS_FF = new char[] { 'F', 'F' };
    private static final char[] CHARS_G = new char[] { 'G' };
    private static final char[] CHARS_N = new char[] { 'N' };
    private static final char[] CHARS_NN = new char[] { 'N', 'N' };
    private static final char[] CHARS_S = new char[] { 'S' };
    private static final char[] CHARS_SSS = new char[] { 'S', 'S', 'S' };
    private static final char SPACE = ' ';
    private static final int TRUE_LENGTH = 6;

    /**
     * Tests if the given character is a vowel.
     * 
     * @param c
     *            the character to test
     * @return <code>true</code> if the character is a vowel, <code>false</code> otherwise
     */
    private static boolean isVowel(final char c) {
        return c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U';
    }

    /**
     * Transcodes the remaining parts of the String. The method operates on a sliding window, looking at 4 characters at
     * a time: [i-1, i, i+1, i+2].
     * 
     * @param prev
     *            the previous character
     * @param curr
     *            the current character
     * @param next
     *            the next character
     * @param aNext
     *            the after next character
     * @return a transcoded array of characters, starting from the current position
     */
    private static char[] transcodeRemaining(final char prev, final char curr, final char next, final char aNext) {
        // 1. EV -> AF
        if (curr == 'E' && next == 'V') {
            return CHARS_AF;
        }

        // A, E, I, O, U -> A
        if (isVowel(curr)) {
            return CHARS_A;
        }

        // 2. Q -> G, Z -> S, M -> N
        if (curr == 'Q') {
            return CHARS_G;
        } else if (curr == 'Z') {
            return CHARS_S;
        } else if (curr == 'M') {
            return CHARS_N;
        }

        // 3. KN -> NN else K -> C
        if (curr == 'K') {
            if (next == 'N') {
                return CHARS_NN;
            } else {
                return CHARS_C;
            }
        }

        // 4. SCH -> SSS
        if (curr == 'S' && next == 'C' && aNext == 'H') {
            return CHARS_SSS;
        }

        // PH -> FF
        if (curr == 'P' && next == 'H') {
            return CHARS_FF;
        }

        // 5. H -> If previous or next is a non vowel, previous.
        if (curr == 'H' && (!isVowel(prev) || !isVowel(next))) {
            return new char[] { prev };
        }

        // 6. W -> If previous is vowel, previous.
        if (curr == 'W' && isVowel(prev)) {
            return new char[] { prev };
        }

        return new char[] { curr };
    }

    private final boolean trueLength;

    public Nysiis() {
        this(true);
    }

    public Nysiis(boolean trueLength) {
        this.trueLength = trueLength;
    }

    /**
     * Encodes an Object using the NYSIIS algorithm. This method is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an {@link EncoderException} if the supplied object is not of type
     * {@link String}.
     * 
     * @param pObject
     *            Object to encode
     * @return An object (or a {@link String}) containing the NYSIIS code which corresponds to the given String.
     * @throws EncoderException
     *             if the parameter supplied is not of a {@link String}
     * @throws IllegalArgumentException
     *             if a character is not mapped
     */
    public Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException("Parameter supplied to Nysiis encode is not of type java.lang.String");
        }
        return this.nysiis((String) pObject);
    }

    /**
     * Encodes a String using the NYSIIS algorithm.
     * 
     * @param pString
     *            A String object to encode
     * @return A Nysiis code corresponding to the String supplied
     * @throws IllegalArgumentException
     *             if a character is not mapped
     */
    public String encode(String pString) {
        return this.nysiis(pString);
    }

    public boolean isTrueLength() {
        return trueLength;
    }

    /**
     * Retrieves the NYSIIS code for a given String object.
     * 
     * @param str
     *            String to encode using the NYSIIS algorithm
     * @return A NYSIIS code for the String supplied
     */
    public String nysiis(String str) {
        if (str == null) {
            return null;
        }

        // Use the same clean rules as Soundex
        str = SoundexUtils.clean(str);

        if (str.length() == 0) {
            return str;
        }

        // Translate first characters of name:
        // MAC -> MCC, KN -> NN, K -> C, PH | PF -> FF, SCH -> SSS
        str = str.replaceFirst("^MAC", "MCC");
        str = str.replaceFirst("^KN", "NN");
        str = str.replaceFirst("^K", "C");
        str = str.replaceFirst("^(PH|PF)", "FF");
        str = str.replaceFirst("^SCH", "SSS");

        // Translate last characters of name:
        // EE -> Y, IE -> Y, DT | RT | RD | NT | ND -> D
        str = str.replaceFirst("(EE|IE)$", "Y");
        str = str.replaceFirst("(DT|RT|RD|NT|ND)$", "D");

        // First character of key = first character of name.
        StringBuffer key = new StringBuffer(str.length());
        key.append(str.charAt(0));

        // Transcode remaining characters, incrementing by one character each time
        final char[] chars = str.toCharArray();
        final int len = chars.length;

        for (int i = 1; i < len; i++) {
            final char next = i < len - 1 ? chars[i + 1] : SPACE;
            final char aNext = i < len - 2 ? chars[i + 2] : SPACE;
            final char[] transcoded = transcodeRemaining(chars[i - 1], chars[i], next, aNext);
            System.arraycopy(transcoded, 0, chars, i, transcoded.length);

            // only append the current char to the key if it is different from the last one
            if (chars[i] != chars[i - 1]) {
                key.append(chars[i]);
            }
        }

        if (key.length() > 1) {
            char lastChar = key.charAt(key.length() - 1);

            // If last character is S, remove it.
            if (lastChar == 'S') {
                key.deleteCharAt(key.length() - 1);
                lastChar = key.charAt(key.length() - 1);
            }

            if (key.length() > 2) {
                final char last2Char = key.charAt(key.length() - 2);
                // If last characters are AY, replace with Y.
                if (last2Char == 'A' && lastChar == 'Y') {
                    key.deleteCharAt(key.length() - 2);
                }
            }

            // If last character is A, remove it.
            if (lastChar == 'A') {
                key.deleteCharAt(key.length() - 1);
            }
        }

        final String string = key.toString();
        return this.isTrueLength() ? string.substring(0, Math.min(TRUE_LENGTH, string.length())) : string;
    }

}
