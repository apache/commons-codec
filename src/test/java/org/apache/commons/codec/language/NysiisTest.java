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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link Nysiis}
 * 
 * @version $Id$
 */
public class NysiisTest extends StringEncoderAbstractTest {

    private final Nysiis fullNysiis = new Nysiis(false);
    
    @Override
    protected StringEncoder createStringEncoder() {
        return new Nysiis();
    }

    private void encodeAll(String[] strings, String expectedEncoding) throws EncoderException {
        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals("Problem with " + strings[i], expectedEncoding, getStringEncoder().encode(strings[i]));
        }
    }

    @Test
    public void testBran() throws EncoderException {
        encodeAll(new String[] { "Brian", "Brown", "Brun" }, "BRAN");
    }

    @Test
    public void testCap() throws EncoderException {
        this.encodeAll(new String[] { "Capp", "Cope", "Copp", "Kipp" }, "CAP");
    }

    @Test
    public void testDad() throws EncoderException {
        // Data Quality and Record Linkage Techniques P.121 claims this is DAN,
        // but it should be DAD, verified also with dropby.com
        this.encodeAll(new String[] { "Dent" }, "DAD");
    }

    @Test
    public void testDan() throws EncoderException {
        this.encodeAll(new String[] { "Dane", "Dean", "Dionne" }, "DAN");
    }

    @Test
    public void testDropBy() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "MACINTOSH", "MCANT" },
                        new String[] { "KNUTH", "NAT"   },
                        new String[] { "KOEHN", "CAN" },
                        new String[] { "PHILLIPSON", "FALAPSAN" },
                        new String[] { "PFEISTER", "FASTAR" },
                        new String[] { "MCKEE", "MCY" },
                        new String[] { "MACKIE", "MCY" },
                        new String[] { "HEITSCHMIDT", "HATSNAD" },
                        new String[] { "BART", "BAD" },
                        new String[] { "HURD", "HAD" },
                        new String[] { "HUNT", "HAD" },
                        new String[] { "WESTERLUND", "WASTARLAD" },
                        new String[] { "CASSTEVENS", "CASTAFAN" },
                        new String[] { "VASQUEZ", "VASG" },
                        new String[] { "FRAZIER", "FRASAR" },
                        new String[] { "BOWMAN", "BANAN" },
                        new String[] { "RICKERT", "RACAD" },
                        new String[] { "DEUTSCH", "DAT" },
                        new String[] { "WESTPHAL", "WASTFAL" },
                        new String[] { "SHRIVER", "SRAVAR" },
                        new String[] { "KUHL", "CAL" },
                        new String[] { "RAWSON", "RASAN" },
                        new String[] { "JILES", "JAL" },
                        new String[] { "CARRAWAY", "CARY" },
                        new String[] { "YAMADA", "YANAD" });

        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests data gathered from around the internets.
     * 
     * @throws EncoderException
     */
    @Test
    public void testDropBy2() throws EncoderException {
        // Explanation of differences between this implementation and the one at dropby.com.
        //
        // Algorithm (taken from www.dropby.com/NYSIIS.html):
        //
        // 1.  Transcode first characters of name:
        //    MAC >   MCC
        //    KN  >   NN
        //    K   >   C
        //    PH  >   FF
        //    PF  >   FF
        //    SCH >   SSS
        //
        // 2.  Transcode last characters of name:
        //    EE, IE  >   Y
        //    DT,RT,RD,NT,ND  >   D
        //
        // 3.  First character of key = first character of name.
        //
        // 4.  Transcode remaining characters by following these rules, incrementing by one character each time:
        //   4a.   EV  >   AF  else A,E,I,O,U > A
        //   4b.   Q   >   G
        //   4c.   Z   >   S
        //   4d.   M   >   N
        //   4e.   KN  >   N   else K > C
        //   4f.   SCH >   SSS
        //   4g.   PH  >   FF
        //   4h.   H   >   If previous or next is nonvowel, previous
        //   4i.   W   >   If previous is vowel, previous
        //   4j.   Add current to key if current != last key character
        //
        // 5.  If last character is S, remove it
        // 6.  If last characters are AY, replace with Y
        // 7.  If last character is A, remove it
        // 8.  Collapse all strings of repeated characters
        // 9.  Add original first character of name as first character of key

        List<String[]> testValues =
                Arrays.asList(
                        // http://www.dropby.com/indexLF.html?content=/NYSIIS.html
                        // 1. Transcode first characters of name
                        new String[] { "MACINTOSH", "MCANT" },
                        // violates 4j: the second N should not be added, as the first
                        //              key char is already a N
                        new String[] { "KNUTH", "NAT" }, // Original: NNAT; modified: NATH
                        // O and E are transcoded to A because of rule 4a
                        // H also to A because of rule 4h
                        // the N gets mysteriously lost, maybe because of a wrongly implemented rule 4h
                        // that skips the next char in such a case?
                        // the remaining A is removed because of rule 7
                        new String[] { "KOEHN", "CAN" }, // Original: C
                        // violates 4j: see also KNUTH
                        new String[] { "PHILLIPSON", "FALAPSAN" }, // Original: FFALAP[SAN]
                        // violates 4j: see also KNUTH
                        new String[] { "PFEISTER", "FASTAR" }, // Original: FFASTA[R]
                        // violates 4j: see also KNUTH
                        new String[] { "SCHOENHOEFT", "SANAFT" }, // Original: SSANAF[T]
                        // http://www.dropby.com/indexLF.html?content=/NYSIIS.html
                        // 2.Transcode last characters of name: 
                        new String[] { "MCKEE", "MCY" },
                        new String[] { "MACKIE", "MCY" },
                        new String[] { "HEITSCHMIDT", "HATSNAD" },
                        new String[] { "BART", "BAD" },
                        new String[] { "HURD", "HAD" },
                        new String[] { "HUNT", "HAD" },
                        new String[] { "WESTERLUND", "WASTARLAD" },
                        // http://www.dropby.com/indexLF.html?content=/NYSIIS.html
                        // 4. Transcode remaining characters by following these rules, incrementing by one character each time: 
                        new String[] { "CASSTEVENS", "CASTAFAN" },
                        new String[] { "VASQUEZ", "VASG" },
                        new String[] { "FRAZIER", "FRASAR" },
                        new String[] { "BOWMAN", "BANAN" },
                        new String[] { "MCKNIGHT", "MCNAGT" },
                        new String[] { "RICKERT", "RACAD" },
                        // violates 5: the last S is not removed
                        // when comparing to DEUTS, which is phonetically similar
                        // the result it also DAT, which is correct for DEUTSCH too imo
                        new String[] { "DEUTSCH", "DAT" }, // Original: DATS
                        new String[] { "WESTPHAL", "WASTFAL" },
                        // violates 4h: the H should be transcoded to S and thus ignored as
                        // the first key character is also S
                        new String[] { "SHRIVER", "SRAVAR" }, // Original: SHRAVA[R]
                        // same as KOEHN, the L gets mysteriously lost
                        new String[] { "KUHL", "CAL" }, // Original: C
                        new String[] { "RAWSON", "RASAN" },
                        // If last character is S, remove it
                        new String[] { "JILES", "JAL" },
                        // violates 6: if the last two characters are AY, remove A
                        new String[] { "CARRAWAY", "CARY" }, // Original: CARAY
                        new String[] { "YAMADA", "YANAD" });

        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    @Test
    public void testFal() throws EncoderException {
        this.encodeAll(new String[] { "Phil" }, "FAL");
    }

    /**
     * Tests data gathered from around the internets.
     * 
     * @throws EncoderException
     */
    @Test
    public void testOthers() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "O'Daniel", "ODANAL" },
                        new String[] { "O'Donnel", "ODANAL" },
                        new String[] { "Cory", "CARY" },
                        new String[] { "Corey", "CARY" },
                        new String[] { "Kory", "CARY" },
                        //
                        new String[] { "FUZZY", "FASY" });

        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 1: Translate first characters of name: MAC → MCC, KN → N, K → C, PH, PF → FF, SCH → SSS
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule1() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "MACX", "MCX" },
                        new String[] { "KNX", "NX" },
                        new String[] { "KX", "CX" },
                        new String[] { "PHX", "FX" },
                        new String[] { "PFX", "FX" },
                        new String[] { "SCHX", "SX" });
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 2: Translate last characters of name: EE → Y, IE → Y, DT, RT, RD, NT, ND → D
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule2() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "XEE", "XY" },
                        new String[] { "XIE", "XY" },
                        new String[] { "XDT", "XD" },
                        new String[] { "XRT", "XD" },
                        new String[] { "XRD", "XD" },
                        new String[] { "XNT", "XD" },
                        new String[] { "XND", "XD" });
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 4.1: EV → AF else A, E, I, O, U → A
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule4Dot1() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "XEV", "XAF" },
                        new String[] { "XAX", "XAX" },
                        new String[] { "XEX", "XAX" },
                        new String[] { "XIX", "XAX" },
                        new String[] { "XOX", "XAX" },
                        new String[] { "XUX", "XAX" });
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 5: If last character is S, remove it.
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule5() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "XS", "X" },
                        new String[] { "XSS", "X" });
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 6: If last characters are AY, replace with Y.
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule6() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "XAY", "XY" },
                        new String[] { "XAYS", "XY" }); // Rules 5, 6
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }

    /**
     * Tests rule 7: If last character is A, remove it.
     * 
     * @throws EncoderException
     */
    @Test
    public void testRule7() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        new String[] { "XA", "X" },
                        new String[] { "XAS", "X" }); // Rules 5, 7
        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], this.fullNysiis.encode(arr[0]));
        }
    }
    @Test
    public void testSnad() throws EncoderException {
        // Data Quality and Record Linkage Techniques P.121 claims this is SNAT,
        // but it should be SNAD
        this.encodeAll(new String[] { "Schmidt" }, "SNAD");
    }

    @Test
    public void testSnat() throws EncoderException {
        this.encodeAll(new String[] { "Smith", "Schmit" }, "SNAT");
    }

    @Test
    public void testSpecialBranches() throws EncoderException {
        this.encodeAll(new String[] { "Kobwick" }, "CABWAC");
        this.encodeAll(new String[] { "Kocher" }, "CACAR");
        this.encodeAll(new String[] { "Fesca" }, "FASC");
        this.encodeAll(new String[] { "Shom" }, "SAN");
        this.encodeAll(new String[] { "Ohlo" }, "OL");
        this.encodeAll(new String[] { "Uhu" }, "UH");
        this.encodeAll(new String[] { "Um" }, "UN");
    }

    @Test
    public void testTranan() throws EncoderException {
        this.encodeAll(new String[] { "Trueman", "Truman" }, "TRANAN");
    }

    @Test
    public void testTrueVariant() {
        Nysiis encoder = new Nysiis(true);

        String encoded = encoder.encode("WESTERLUND");
        Assert.assertTrue(encoded.length() <= 6);
        Assert.assertEquals("WASTAR", encoded);
    }

}
