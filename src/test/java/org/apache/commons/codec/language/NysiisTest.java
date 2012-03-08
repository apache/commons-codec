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
 * @version $Id: NysiisTest.java 658834 2008-05-21 19:57:51Z niallp $
 */
public class NysiisTest extends StringEncoderAbstractTest {

    @Override
    protected StringEncoder createStringEncoder() {
        return new Nysiis();
    }

    protected StringEncoder createStringEncoder(boolean trueLength) {
        return new Nysiis(trueLength);
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
            Assert.assertEquals("Problem with " + arr[0], arr[1], createStringEncoder(false).encode(arr[0]));
        }
    }

    /**
     * Tests data gathered from around the internets.
     * 
     * @throws EncoderException
     */
    @Test
    public void testDropBy2() throws EncoderException {
        List<String[]> testValues =
                Arrays.asList(
                        // http://www.dropby.com/indexLF.html?content=/NYSIIS.html
                        // 1. Transcode first characters of name
                        new String[] { "MACINTOSH", "MCANT" },
                        //new String[] { "KNUTH", "NNATH" }, // Original: NNAT; modified: NATH
                        //new String[] { "KOEHN", "C" },
                        //new String[] { "PHILLIPSON", "FFALAP" },
                        //new String[] { "PFEISTER", "FFASTA" },
                        //new String[] { "SCHOENHOEFT", "SSANAF" },
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
                        //new String[] { "DEUTSCH", "DATS" },
                        new String[] { "WESTPHAL", "WASTFAL" },
                        //new String[] { "SHRIVER", "SHRAVA" },
                        //new String[] { "KUHL", "C" },
                        new String[] { "RAWSON", "RASAN" },
                        // If last character is S, remove it
                        new String[] { "JILES", "JAL" },
                        //new String[] { "CARRAWAY", "CARAY" },
                        new String[] { "YAMADA", "YANAD" });

        for (String[] arr : testValues) {
            Assert.assertEquals("Problem with " + arr[0], arr[1], createStringEncoder(false).encode(arr[0]));
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
            Assert.assertEquals("Problem with " + arr[0], arr[1], createStringEncoder(false).encode(arr[0]));
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
    public void testTranan() throws EncoderException {
        this.encodeAll(new String[] { "Trueman", "Truman" }, "TRANAN");
    }

}
