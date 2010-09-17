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
 * <h1>ColognePhonetic</h1>
 * 
 * <p>
 * <b>ColognePhonetic</b> provides an implementation of the <a
 * href="http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik">“Kölner
 * Phonetic”</a> (cologne phonetic) algorithm issued by Hans Joachim Postel in
 * 1969.
 * </p>
 * 
 * <p>
 * The <i>Kölner Phonetik</i> is a phonetic algorithm which is optimized for the
 * German language. It is related to the well-known soundex algorithm.
 * </p>
 * 
 * <h2>Algorithm</h2>
 * 
 * <ul>
 * 
 * <li>
 * <h3>First step:</h3>
 * After a preprocessing (convertion to upper case, transcription of <a
 * href="http://en.wikipedia.org/wiki/Germanic_umlaut">germanic umlauts</a>,
 * removal of non alphabetical characters) the letters of the supplied text are
 * replaced by their phonetic code according to the folowing table.
 * <table border="1">
 * <tbody>
 * <tr>
 * <th>Letter</th>
 * <th>Context</th>
 * <th align="center">Code</th>
 * </tr>
 * <tr>
 * <td>A, E, I, J, O, U, Y</td>
 * <td></td>
 * <td align="center">0</td>
 * </tr>
 * <tr>
 * 
 * <td>H</td>
 * <td></td>
 * <td align="center">-</td>
 * </tr>
 * <tr>
 * <td>B</td>
 * <td></td>
 * <td rowspan="2" align="center">1</td>
 * </tr>
 * <tr>
 * <td>P</td>
 * <td>not before H</td>
 * 
 * </tr>
 * <tr>
 * <td>D, T</td>
 * <td>not before C, S, Z</td>
 * <td align="center">2</td>
 * </tr>
 * <tr>
 * <td>F, V, W</td>
 * <td></td>
 * <td rowspan="2" align="center">3</td>
 * </tr>
 * <tr>
 * 
 * <td>P</td>
 * <td>before H</td>
 * </tr>
 * <tr>
 * <td>G, K, Q</td>
 * <td></td>
 * <td rowspan="3" align="center">4</td>
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
 * <td align="center">48</td>
 * </tr>
 * <tr>
 * <td>L</td>
 * <td></td>
 * 
 * <td align="center">5</td>
 * </tr>
 * <tr>
 * <td>M, N</td>
 * <td></td>
 * <td align="center">6</td>
 * </tr>
 * <tr>
 * <td>R</td>
 * <td></td>
 * <td align="center">7</td>
 * </tr>
 * 
 * <tr>
 * <td>S, Z</td>
 * <td></td>
 * <td rowspan="6" align="center">8</td>
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
 * <p>
 * <small><i>(Source: <a href=
 * "http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik#Buchstabencodes"
 * >Wikipedia (de): Kölner Phonetik – Buchstabencodes</a>)</i></small>
 * </p>
 * 
 * <h4>Example:</h4>
 * 
 * {@code "Müller-Lüdenscheidt" => "MULLERLUDENSCHEIDT" => "6005507500206880022"}
 * 
 * </li>
 * 
 * <li>
 * <h3>Second step:</h3>
 * Removal of all doubly codes.
 * <h4>Example:</h4>
 * {@code "6005507500206880022" => "6050750206802"}</li>
 * 
 * <li>
 * <h3>Third step:</h3>
 * Removal of all codes “0” except at the beginning
 * 
 * <h4>Example:</h4>
 * {@code "6050750206802" => "65752682"}</li>
 * 
 * </ul>
 * 
 * @see<ul> <li><a
 *          href="http://de.wikipedia.org/wiki/K%C3%B6lner_Phonetik"><span
 *          style="font-variant:small-caps">Wikipedia</span> (de): <i>Kölner
 *          Phonetik</i></a>—for German description of the algorithm and more
 *          sources.</li> <li>{@linkplain #colognePhonetic(String)}—for the
 *          description of the actual implementation</li> <li>
 *          {@linkplain #isCologneEqual(String, String)}</li> <li>
 *          {@linkplain StringEncoder}—for the interface implemented by this
 *          class</li> </ul>
 * 
 * @author Falk Meyer, IT2media
 * @version beta 2010/9/13
 */

public class ColognePhonetic implements StringEncoder {

    private class CologneLeftBuffer implements CharSequence {

        private final char[] data;
        private int length = 0;

        public CologneLeftBuffer(int buffSize) {
            data = new char[buffSize];
        }

        public CologneLeftBuffer(char[] data) {
            this.data = data;
            this.length = data.length;
        }

        public int length() {
            return length;
        }

        public char charAt(int index) {
            if (index < length) {
                return data[index];
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public CharSequence subSequence(int start, int end) {
            final int length = end - start;

            char[] retData = copyData(start, length);

            return new CologneLeftBuffer(retData);
        }

        private char[] copyData(int start, final int length) {
            char[] retData = new char[length];

            System.arraycopy(data, start, retData, 0, length);
            return retData;
        }

        public char getLast() {
            return data[length - 1];
        }

        public void putRight(char chr) {
            data[length] = chr;
            length++;
        }

        public char dropLast() {
            length--;
            return data[length];
        }

        public String toString() {
            return new String(copyData(0, length));
        }
    }

    private class CologneRightBuffer implements CharSequence {

        private int length = 0;
        private final char[] data;

        public CologneRightBuffer(int buffSize) {
            data = new char[buffSize];
        }

        public CologneRightBuffer(char[] data) {
            this.data = data;
            this.length = data.length;
        }

        public int length() {
            return length;
        }

        public char charAt(int index) {
            if (index < length) {
                return data[data.length - length + index];
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public CharSequence subSequence(int start, int end) {
            final int length = end - start;
            char[] newData = copyData(start, length);

            return new CologneRightBuffer(newData);
        }

        private char[] copyData(int start, final int length) {
            char[] newData = new char[length];

            System.arraycopy(data, data.length - this.length + start, newData,
                    0, length);
            return newData;
        }

        public void putLeft(char chr) {
            length++;
            data[data.length - length] = chr;
        }

        public char getNext() {
            return data[data.length - length];
        }

        public char dropNext() {
            char ret = data[data.length - length];
            length--;

            return ret;
        }

        public String toString() {
            return new String(copyData(0, length));
        }

    }

    private static final char[][] PRE_REPLACEMENTS = new char[][] {
            new char[] { '\u00C4', 'A' },     // Ä
            new char[] { '\u00DC', 'U' },     // Ü
            new char[] { '\u00D6', 'O' },     // Ö
            new char[] { '\u00DF', 'S' }      // ß
    };

    public Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException(
                    "This method’s parameter was expected to be of the type "
                            + String.class.getName()
                            + ". But actually it was of the type "
                            + pObject.getClass().getName() + ".");
        }

        return encode((String) pObject);
    }

    public String encode(String text) {
        return colognePhonetic(text);
    }

    /**
     * <p>
     * <b>colognePhonetic()</b> is the actual implementations of the <i>Kölner
     * Phonetik</i> algorithm.
     * </p>
     * <p>
     * In contrast to the initial description of the algorithm, this
     * implementation does the encoding in one pass.
     * </p>
     * 
     * @param text
     * @return the corresponding encoding according to the <i>Kölner
     *         Phonetik</i> algorithm
     */

    public String colognePhonetic(String text) {
        if (text == null) {
            return null;
        }

        text = preProcess(text);

        CologneLeftBuffer left = new CologneLeftBuffer(text.length() * 2);
        CologneRightBuffer right = new CologneRightBuffer(text.toCharArray());

        char nextChar;

        char lastChar = '-';
        char lastCode = '/';
        char code;
        char chr;

        int rightLength = right.length();

        while (rightLength > 0) {
            chr = right.dropNext();

            if ((rightLength = right.length()) > 0) {
                nextChar = right.getNext();
            } else {
                nextChar = '-';
            }

            if (arrayContains(new char[] { 'E', 'I', 'A', 'U', 'O', 'Y' }, chr)) {
                code = '0';
            } else if (chr == 'H' || chr < 'A' || chr > 'Z') {
                if (lastCode == '/') {
                    continue;
                }
                code = '-';
            } else if (chr == 'B' || (chr == 'P' && nextChar != 'H')) {
                code = '1';
            } else if ((chr == 'D' || chr == 'T')
                    && !arrayContains(new char[] { 'S', 'C', 'Z' }, nextChar)) {
                code = '2';
            } else if (arrayContains(new char[] { 'W', 'F', 'P', 'V' }, chr)) {
                code = '3';
            } else if (arrayContains(new char[] { 'G', 'K', 'Q' }, chr)) {
                code = '4';
            } else if (chr == 'X'
                    && !arrayContains(new char[] { 'C', 'K', 'Q' }, lastChar)) {
                code = '4';
                right.putLeft('S');
                rightLength++;
            } else if (chr == 'S' || chr == 'Z') {
                code = '8';
            } else if (chr == 'C') {
                if (lastCode == '/') {
                    if (arrayContains(new char[] { 'A', 'H', 'L', 'O', 'R',
                            'U', 'K', 'X', 'Q' }, nextChar)) {
                        code = '4';
                    } else {
                        code = '8';
                    }
                } else {
                    if (arrayContains(new char[] { 'S', 'Z' }, lastChar)
                            || !arrayContains(new char[] { 'A', 'H', 'O', 'U',
                                    'K', 'Q', 'X' }, nextChar)) {
                        code = '8';
                    } else {
                        code = '4';
                    }
                }
            } else if (arrayContains(new char[] { 'T', 'D', 'X' }, chr)) {
                code = '8';
            } else if (chr == 'R') {
                code = '7';
            } else if (chr == 'L') {
                code = '5';
            } else if (chr == 'M' || chr == 'N') {
                code = '6';
            } else {
                code = chr;
            }

            if (code != '-'
                    && (lastCode != code && (code != '0' || lastCode == '/')
                            || code < '0' || code > '8')) {
                left.putRight(code);
            }

            lastChar = chr;
            lastCode = code;
        }

        return left.toString();
    }

    /*
     * Returns whether the array contains the key, or not.
     */
    private static boolean arrayContains(char[] arr, char key) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == key) {
                return true;
            }
        }

        return false;
    }

    /*
     * Converts the string to upper case and replaces germanic umlauts, and the
     * “ß”.
     */
    private String preProcess(String text) {
        text = text.toUpperCase(Locale.GERMAN);

        char[] chrs = text.toCharArray();

        for (int index = 0; index < chrs.length; index++) {
            if (chrs[index] > 'Z') {
                for (int replacement = 0; replacement < PRE_REPLACEMENTS.length; replacement++) {
                    if (chrs[index] == PRE_REPLACEMENTS[replacement][0]) {
                        chrs[index] = PRE_REPLACEMENTS[replacement][1];
                        break;
                    }
                }
            }
        }

        text = new String(chrs);

        return text;
    }

    public boolean isCologneEqual(String text1, String text2) {
        return colognePhonetic(text1).equals(colognePhonetic(text2));
    }
}
