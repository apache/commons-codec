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

import java.util.Locale;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a Cologne Phonetic value.
 * <p>
 * Implements the <a href="http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik">K&ouml;lner Phonetik</a> (Cologne
 * Phonetic) algorithm issued by Hans Joachim Postel in 1969.
 * </p>
 * <p>
 * The <i>K&ouml;lner Phonetik</i> is a phonetic algorithm which is optimized for the German language. It is related to
 * the well-known soundex algorithm.
 * </p>
 *
 * <h2>Algorithm</h2>
 *
 * <ul>
 *
 * <li>
 * <h3>Step 1:</h3>
 * After preprocessing (conversion to upper case, transcription of <a
 * href="http://en.wikipedia.org/wiki/Germanic_umlaut">germanic umlauts</a>, removal of non alphabetical characters) the
 * letters of the supplied text are replaced by their phonetic code according to the following table.
 * <table border="1">
 * <caption style="caption-side: bottom"><small><i>(Source: <a
 * href="http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik#Buchstabencodes">Wikipedia (de): K&ouml;lner Phonetik --
 * Buchstabencodes</a>)</i></small></caption> <tbody>
 * <tr>
 * <th>Letter</th>
 * <th>Context</th>
 * <th>Code</th>
 * </tr>
 * <tr>
 * <td>A, E, I, J, O, U, Y</td>
 * <td></td>
 * <td>0</td>
 * </tr>
 * <tr>
 *
 * <td>H</td>
 * <td></td>
 * <td>-</td>
 * </tr>
 * <tr>
 * <td>B</td>
 * <td></td>
 * <td rowspan="2">1</td>
 * </tr>
 * <tr>
 * <td>P</td>
 * <td>not before H</td>
 *
 * </tr>
 * <tr>
 * <td>D, T</td>
 * <td>not before C, S, Z</td>
 * <td>2</td>
 * </tr>
 * <tr>
 * <td>F, V, W</td>
 * <td></td>
 * <td rowspan="2">3</td>
 * </tr>
 * <tr>
 *
 * <td>P</td>
 * <td>before H</td>
 * </tr>
 * <tr>
 * <td>G, K, Q</td>
 * <td></td>
 * <td rowspan="3">4</td>
 * </tr>
 * <tr>
 * <td rowspan="2">C</td>
 * <td>at onset before A, H, K, L, O, Q, R, U, X</td>
 *
 * </tr>
 * <tr>
 * <td>before A, H, K, O, Q, U, X except after S, Z</td>
 * </tr>
 * <tr>
 * <td>X</td>
 * <td>not after C, K, Q</td>
 * <td>48</td>
 * </tr>
 * <tr>
 * <td>L</td>
 * <td></td>
 *
 * <td>5</td>
 * </tr>
 * <tr>
 * <td>M, N</td>
 * <td></td>
 * <td>6</td>
 * </tr>
 * <tr>
 * <td>R</td>
 * <td></td>
 * <td>7</td>
 * </tr>
 *
 * <tr>
 * <td>S, Z</td>
 * <td></td>
 * <td rowspan="6">8</td>
 * </tr>
 * <tr>
 * <td rowspan="3">C</td>
 * <td>after S, Z</td>
 * </tr>
 * <tr>
 * <td>at onset except before A, H, K, L, O, Q, R, U, X</td>
 * </tr>
 *
 * <tr>
 * <td>not before A, H, K, O, Q, U, X</td>
 * </tr>
 * <tr>
 * <td>D, T</td>
 * <td>before C, S, Z</td>
 * </tr>
 * <tr>
 * <td>X</td>
 * <td>after C, K, Q</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h4>Example:</h4>
 *
 * {@code "M}&uuml;{@code ller-L}&uuml;<code>denscheidt"
 * =&gt; "MULLERLUDENSCHEIDT" =&gt; "6005507500206880022"</code>
 *
 * </li>
 *
 * <li>
 * <h3>Step 2:</h3>
 * Collapse of all multiple consecutive code digits.
 * <h4>Example:</h4>
 * {@code "6005507500206880022" =&gt; "6050750206802"}</li>
 *
 * <li>
 * <h3>Step 3:</h3>
 * Removal of all codes "0" except at the beginning. This means that two or more identical consecutive digits can occur
 * if they occur after removing the "0" digits.
 *
 * <h4>Example:</h4>
 * {@code "6050750206802" =&gt; "65752682"}</li>
 *
 * </ul>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see <a href="http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik">Wikipedia (de): K&ouml;lner Phonetik (in German)</a>
 * @since 1.5
 */
public class ColognePhonetic implements StringEncoder {

    // Predefined char arrays for better performance and less GC load
    private static final char[] AEIJOUY = new char[] { 'A', 'E', 'I', 'J', 'O', 'U', 'Y' };
    private static final char[] CSZ = new char[] { 'C', 'S', 'Z' };
    private static final char[] FPVW = new char[] { 'F', 'P', 'V', 'W' };
    private static final char[] GKQ = new char[] { 'G', 'K', 'Q' };
    private static final char[] CKQ = new char[] { 'C', 'K', 'Q' };
    private static final char[] AHKLOQRUX = new char[] { 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X' };
    private static final char[] SZ = new char[] { 'S', 'Z' };
    private static final char[] AHKOQUX = new char[] { 'A', 'H', 'K', 'O', 'Q', 'U', 'X' };
    private static final char[] DTX = new char[] { 'D', 'T', 'X' };

    private static final char CHAR_IGNORE = '-';    // is this character to be ignored?

    /**
     * This class is not thread-safe; the field {@link #length} is mutable.
     * However, it is not shared between threads, as it is constructed on demand
     * by the method {@link ColognePhonetic#colognePhonetic(String)}
     */
    private abstract class CologneBuffer {

        protected final char[] data;

        protected int length = 0;

        public CologneBuffer(final char[] data) {
            this.data = data;
            this.length = data.length;
        }

        public CologneBuffer(final int buffSize) {
            this.data = new char[buffSize];
            this.length = 0;
        }

        protected abstract char[] copyData(int start, final int length);

        public int length() {
            return length;
        }

        @Override
        public String toString() {
            return new String(copyData(0, length));
        }
    }

    private class CologneOutputBuffer extends CologneBuffer {

        private char lastCode;

        public CologneOutputBuffer(final int buffSize) {
            super(buffSize);
            lastCode = '/'; // impossible value
        }

        /**
         * Stores the next code in the output buffer, keeping track of the previous code.
         * '0' is only stored if it is the first entry.
         * Ignored chars are never stored.
         * If the code is the same as the last code (whether stored or not) it is not stored.
         *
         * @param code the code to store.
         */
        public void put(final char code) {
            if (code != CHAR_IGNORE && lastCode != code && (code != '0' || length == 0)) {
                data[length] = code;
                length++;
            }
            lastCode = code;
        }

        @Override
        protected char[] copyData(final int start, final int length) {
            final char[] newData = new char[length];
            System.arraycopy(data, start, newData, 0, length);
            return newData;
        }
    }

    private class CologneInputBuffer extends CologneBuffer {

        public CologneInputBuffer(final char[] data) {
            super(data);
        }

        @Override
        protected char[] copyData(final int start, final int length) {
            final char[] newData = new char[length];
            System.arraycopy(data, data.length - this.length + start, newData, 0, length);
            return newData;
        }

        public char getNextChar() {
            return data[getNextPos()];
        }

        protected int getNextPos() {
            return data.length - length;
        }

        public char removeNext() {
            final char ch = getNextChar();
            length--;
            return ch;
        }
    }

    /*
     * Returns whether the array contains the key, or not.
     */
    private static boolean arrayContains(final char[] arr, final char key) {
        for (final char element : arr) {
            if (element == key) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Implements the <i>K&ouml;lner Phonetik</i> algorithm.
     * </p>
     * <p>
     * In contrast to the initial description of the algorithm, this implementation does the encoding in one pass.
     * </p>
     *
     * @param text The source text to encode
     * @return the corresponding encoding according to the <i>K&ouml;lner Phonetik</i> algorithm
     */
    public String colognePhonetic(final String text) {
        if (text == null) {
            return null;
        }

        final CologneInputBuffer input = new CologneInputBuffer(preprocess(text));
        final CologneOutputBuffer output = new CologneOutputBuffer(input.length() * 2);

        char nextChar;

        char lastChar = CHAR_IGNORE;
        char chr;

        while (input.length() > 0) {
            chr = input.removeNext();

            if (input.length() > 0) {
                nextChar = input.getNextChar();
            } else {
                nextChar = CHAR_IGNORE;
            }

            if (chr < 'A' || chr > 'Z') {
                    continue; // ignore unwanted characters
            }

            if (arrayContains(AEIJOUY, chr)) {
                output.put('0');
            } else if (chr == 'B' || (chr == 'P' && nextChar != 'H')) {
                output.put('1');
            } else if ((chr == 'D' || chr == 'T') && !arrayContains(CSZ, nextChar)) {
                output.put('2');
            } else if (arrayContains(FPVW, chr)) {
                output.put('3');
            } else if (arrayContains(GKQ, chr)) {
                output.put('4');
            } else if (chr == 'X' && !arrayContains(CKQ, lastChar)) {
                output.put('4');
                output.put('8');
            } else if (chr == 'S' || chr == 'Z') {
                output.put('8');
            } else if (chr == 'C') {
                if (output.length() == 0) {
                    if (arrayContains(AHKLOQRUX, nextChar)) {
                        output.put('4');
                    } else {
                        output.put('8');
                    }
                } else {
                    if (arrayContains(SZ, lastChar) || !arrayContains(AHKOQUX, nextChar)) {
                        output.put('8');
                    } else {
                        output.put('4');
                    }
                }
            } else if (arrayContains(DTX, chr)) {
                output.put('8');
            } else if (chr == 'R') {
                output.put('7');
            } else if (chr == 'L') {
                output.put('5');
            } else if (chr == 'M' || chr == 'N') {
                output.put('6');
            } else if (chr == 'H') {
                output.put(CHAR_IGNORE); // needed by put
            } else {
                // ignored; should not happen
            }

            lastChar = chr;
        }
        return output.toString();
    }

    @Override
    public Object encode(final Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("This method's parameter was expected to be of the type " +
                String.class.getName() +
                ". But actually it was of the type " +
                object.getClass().getName() +
                ".");
        }
        return encode((String) object);
    }

    @Override
    public String encode(final String text) {
        return colognePhonetic(text);
    }

    /**
     * Compares the first encoded string to the second encoded string.
     *
     * @param text1 source text to encode before testing for equality.
     * @param text2 source text to encode before testing for equality.
     * @return {@code true} if the encoding the first string equals the encoding of the second string, {@code false}
     *         otherwise
     */
    public boolean isEncodeEqual(final String text1, final String text2) {
        return colognePhonetic(text1).equals(colognePhonetic(text2));
    }

    /**
     * Converts the string to upper case and replaces Germanic umlaut characters
     * The following characters are mapped:
     * <ul>
     * <li>capital A, umlaut mark</li>
     * <li>capital U, umlaut mark</li>
     * <li>capital O, umlaut mark</li>
     * <li>small sharp s, German</li>
     * </ul>
     */
    private char[] preprocess(final String text) {
        // This converts German small sharp s (Eszett) to SS
        final char[] chrs = text.toUpperCase(Locale.GERMAN).toCharArray();

        for (int index = 0; index < chrs.length; index++) {
            switch (chrs[index]) {
                case '\u00C4': // capital A, umlaut mark
                    chrs[index] = 'A';
                    break;
                case '\u00DC': // capital U, umlaut mark
                    chrs[index] = 'U';
                    break;
                case '\u00D6': // capital O, umlaut mark
                    chrs[index] = 'O';
                    break;
                default:
                    break;
            }
        }
        return chrs;
    }
}
