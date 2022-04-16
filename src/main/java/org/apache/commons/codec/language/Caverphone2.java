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

/**
 * Encodes a string into a Caverphone 2.0 value.
 *
 * This is an algorithm created by the Caversham Project at the University of Otago. It implements the Caverphone 2.0
 * algorithm:
 *
 * @see <a href="http://en.wikipedia.org/wiki/Caverphone">Wikipedia - Caverphone</a>
 * @see <a href="http://caversham.otago.ac.nz/files/working/ctp150804.pdf">Caverphone 2.0 specification</a>
 * @since 1.5
 *
 * <p>This class is immutable and thread-safe.</p>
 */
public class Caverphone2 extends AbstractCaverphone {

    private static final String TEN_1 = "1111111111";

    /**
     * Encodes the given String into a Caverphone 2.0 value.
     *
     * @param source
     *            String the source string
     * @return A caverphone code for the given String
     */
    @Override
    public String encode(final String source) {
        String txt = source;
        if (SoundexUtils.isEmpty(txt)) {
            return TEN_1;
        }

        // 1. Convert to lowercase
        txt = txt.toLowerCase(java.util.Locale.ENGLISH);

        // 2. Remove anything not A-Z
        txt = txt.replaceAll("[^a-z]", "");

        // 2.5. Remove final e
        txt = txt.replaceAll("e$", ""); // 2.0 only

        // 3. Handle various start options
        txt = txt.replaceAll("^cough", "cou2f");
        txt = txt.replaceAll("^rough", "rou2f");
        txt = txt.replaceAll("^tough", "tou2f");
        txt = txt.replaceAll("^enough", "enou2f"); // 2.0 only
        txt = txt.replaceAll("^trough", "trou2f"); // 2.0 only
                                                   // note the spec says ^enough here again, c+p error I assume
        txt = txt.replaceAll("^gn", "2n");

        // End
        txt = txt.replaceAll("mb$", "m2");

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
        txt = txt.replaceAll("^[aeiou]", "A");
        txt = txt.replaceAll("[aeiou]", "3");
        txt = txt.replace("j", "y"); // 2.0 only
        txt = txt.replaceAll("^y3", "Y3"); // 2.0 only
        txt = txt.replaceAll("^y", "A"); // 2.0 only
        txt = txt.replace("y", "3"); // 2.0 only
        txt = txt.replace("3gh3", "3kh3");
        txt = txt.replace("gh", "22");
        txt = txt.replace("g", "k");
        txt = txt.replaceAll("s+", "S");
        txt = txt.replaceAll("t+", "T");
        txt = txt.replaceAll("p+", "P");
        txt = txt.replaceAll("k+", "K");
        txt = txt.replaceAll("f+", "F");
        txt = txt.replaceAll("m+", "M");
        txt = txt.replaceAll("n+", "N");
        txt = txt.replace("w3", "W3");
        txt = txt.replace("wh3", "Wh3");
        txt = txt.replaceAll("w$", "3"); // 2.0 only
        txt = txt.replace("w", "2");
        txt = txt.replaceAll("^h", "A");
        txt = txt.replace("h", "2");
        txt = txt.replace("r3", "R3");
        txt = txt.replaceAll("r$", "3"); // 2.0 only
        txt = txt.replace("r", "2");
        txt = txt.replace("l3", "L3");
        txt = txt.replaceAll("l$", "3"); // 2.0 only
        txt = txt.replace("l", "2");

        // 5. Handle removals
        txt = txt.replace("2", "");
        txt = txt.replaceAll("3$", "A"); // 2.0 only
        txt = txt.replace("3", "");

        // 6. put ten 1s on the end
        txt = txt + TEN_1;

        // 7. take the first ten characters as the code
        return txt.substring(0, TEN_1.length());
    }

}
