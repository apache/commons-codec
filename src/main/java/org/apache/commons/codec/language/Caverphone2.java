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

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Encodes a string into a Caverphone 2.0 value.
 *
 * This is an algorithm created by the Caversham Project at the University of Otago. It implements the Caverphone 2.0
 * algorithm:
 *
 * @see <a href="https://en.wikipedia.org/wiki/Caverphone">Wikipedia - Caverphone</a>
 * @see <a href="https://caversham.otago.ac.nz/files/working/ctp150804.pdf">Caverphone 2.0 specification</a>
 * @since 1.5
 *
 * <p>This class is immutable and thread-safe.</p>
 */
public class Caverphone2 extends AbstractCaverphone {

    private static final String TEN_1 = "1111111111";

    // Patterns are compiled once: String.replaceAll compiles its regex on every call, which on a hot
    // encode path (one encode applies more than twenty of them) is a large, repeated allocation.
    private static final Pattern NON_LOWER = Pattern.compile("[^a-z]");
    private static final Pattern FINAL_E = Pattern.compile("e$");
    private static final Pattern START_COUGH = Pattern.compile("^cough");
    private static final Pattern START_ROUGH = Pattern.compile("^rough");
    private static final Pattern START_TOUGH = Pattern.compile("^tough");
    private static final Pattern START_ENOUGH = Pattern.compile("^enough");
    private static final Pattern START_TROUGH = Pattern.compile("^trough");
    private static final Pattern START_GN = Pattern.compile("^gn");
    private static final Pattern FINAL_MB = Pattern.compile("mb$");
    private static final Pattern START_VOWEL = Pattern.compile("^[aeiou]");
    private static final Pattern VOWEL = Pattern.compile("[aeiou]");
    private static final Pattern START_Y3 = Pattern.compile("^y3");
    private static final Pattern START_Y = Pattern.compile("^y");
    private static final Pattern RUN_S = Pattern.compile("s+");
    private static final Pattern RUN_T = Pattern.compile("t+");
    private static final Pattern RUN_P = Pattern.compile("p+");
    private static final Pattern RUN_K = Pattern.compile("k+");
    private static final Pattern RUN_F = Pattern.compile("f+");
    private static final Pattern RUN_M = Pattern.compile("m+");
    private static final Pattern RUN_N = Pattern.compile("n+");
    private static final Pattern FINAL_W = Pattern.compile("w$");
    private static final Pattern START_H = Pattern.compile("^h");
    private static final Pattern FINAL_R = Pattern.compile("r$");
    private static final Pattern FINAL_L = Pattern.compile("l$");
    private static final Pattern FINAL_3 = Pattern.compile("3$");

    /**
     * Constructs a new instance.
     */
    public Caverphone2() {
        // empty
    }

    /**
     * Encodes the given String into a Caverphone 2.0 value.
     *
     * @param source
     *            String the source string.
     * @return A Caverphone code for the given String.
     */
    @Override
    public String encode(final String source) {
        String txt = source;
        if (SoundexUtils.isEmpty(txt)) {
            return TEN_1;
        }

        // 1. Convert to lowercase
        txt = txt.toLowerCase(Locale.ENGLISH);

        // 2. Remove anything not A-Z
        txt = NON_LOWER.matcher(txt).replaceAll("");

        // 2.5. Remove final e
        txt = FINAL_E.matcher(txt).replaceAll(""); // 2.0 only

        // 3. Handle various start options
        txt = START_COUGH.matcher(txt).replaceAll("cou2f");
        txt = START_ROUGH.matcher(txt).replaceAll("rou2f");
        txt = START_TOUGH.matcher(txt).replaceAll("tou2f");
        txt = START_ENOUGH.matcher(txt).replaceAll("enou2f"); // 2.0 only
        txt = START_TROUGH.matcher(txt).replaceAll("trou2f"); // 2.0 only
                                                   // note the spec says ^enough here again, c+p error I assume
        txt = START_GN.matcher(txt).replaceAll("2n");

        // End
        txt = FINAL_MB.matcher(txt).replaceAll("m2");

        // 4. Handle replacements
        txt = txt.replace("cq", "2q");
        txt = txt.replace("ci", "si");
        txt = txt.replace("ce", "se");
        txt = txt.replace("cy", "sy");
        txt = txt.replace("tch", "2ch");
        txt = txt.replace("c", "k");
        txt = txt.replace("q", "k");
        txt = txt.replace("x", "k");
        txt = txt.replace("v", "f");
        txt = txt.replace("dg", "2g");
        txt = txt.replace("tio", "sio");
        txt = txt.replace("tia", "sia");
        txt = txt.replace("d", "t");
        txt = txt.replace("ph", "fh");
        txt = txt.replace("b", "p");
        txt = txt.replace("sh", "s2");
        txt = txt.replace("z", "s");
        txt = START_VOWEL.matcher(txt).replaceAll("A");
        txt = VOWEL.matcher(txt).replaceAll("3");
        txt = txt.replace("j", "y"); // 2.0 only
        txt = START_Y3.matcher(txt).replaceAll("Y3"); // 2.0 only
        txt = START_Y.matcher(txt).replaceAll("A"); // 2.0 only
        txt = txt.replace("y", "3"); // 2.0 only
        txt = txt.replace("3gh3", "3kh3");
        txt = txt.replace("gh", "22");
        txt = txt.replace("g", "k");
        txt = RUN_S.matcher(txt).replaceAll("S");
        txt = RUN_T.matcher(txt).replaceAll("T");
        txt = RUN_P.matcher(txt).replaceAll("P");
        txt = RUN_K.matcher(txt).replaceAll("K");
        txt = RUN_F.matcher(txt).replaceAll("F");
        txt = RUN_M.matcher(txt).replaceAll("M");
        txt = RUN_N.matcher(txt).replaceAll("N");
        txt = txt.replace("w3", "W3");
        txt = txt.replace("wh3", "Wh3");
        txt = FINAL_W.matcher(txt).replaceAll("3"); // 2.0 only
        txt = txt.replace("w", "2");
        txt = START_H.matcher(txt).replaceAll("A");
        txt = txt.replace("h", "2");
        txt = txt.replace("r3", "R3");
        txt = FINAL_R.matcher(txt).replaceAll("3"); // 2.0 only
        txt = txt.replace("r", "2");
        txt = txt.replace("l3", "L3");
        txt = FINAL_L.matcher(txt).replaceAll("3"); // 2.0 only
        txt = txt.replace("l", "2");

        // 5. Handle removals
        txt = txt.replace("2", "");
        txt = FINAL_3.matcher(txt).replaceAll("A"); // 2.0 only
        txt = txt.replace("3", "");

        // 6. put ten 1s on the end
        txt += TEN_1;

        // 7. take the first ten characters as the code
        return txt.substring(0, TEN_1.length());
    }

}
