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
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests Caverphone2.
 *
 * @since 1.5
 */
public class Caverphone2Test extends StringEncoderAbstractTest<Caverphone2> {

    @Override
    protected Caverphone2 createStringEncoder() {
        return new Caverphone2();
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * AT11111111 words: add, aid, at, art, eat, earth, head, hit, hot, hold, hard, heart, it, out, old
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedCommonCodeAT11111111() throws EncoderException {
        this.checkEncodingVariations("AT11111111", new String[]{
            "add",
            "aid",
            "at",
            "art",
            "eat",
            "earth",
            "head",
            "hit",
            "hot",
            "hold",
            "hard",
            "heart",
            "it",
            "out",
            "old"});
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedExamples() throws EncoderException {
        final String[][] data = {{"Stevenson", "STFNSN1111"}, {"Peter", "PTA1111111"}};
        this.checkEncodings(data);
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedRandomNameKLN1111111() throws EncoderException {
        this.checkEncodingVariations("KLN1111111", new String[]{
            "Cailean",
            "Calan",
            "Calen",
            "Callahan",
            "Callan",
            "Callean",
            "Carleen",
            "Carlen",
            "Carlene",
            "Carlin",
            "Carline",
            "Carlyn",
            "Carlynn",
            "Carlynne",
            "Charlean",
            "Charleen",
            "Charlene",
            "Charline",
            "Cherlyn",
            "Chirlin",
            "Clein",
            "Cleon",
            "Cline",
            "Cohleen",
            "Colan",
            "Coleen",
            "Colene",
            "Colin",
            "Colleen",
            "Collen",
            "Collin",
            "Colline",
            "Colon",
            "Cullan",
            "Cullen",
            "Cullin",
            "Gaelan",
            "Galan",
            "Galen",
            "Garlan",
            "Garlen",
            "Gaulin",
            "Gayleen",
            "Gaylene",
            "Giliane",
            "Gillan",
            "Gillian",
            "Glen",
            "Glenn",
            "Glyn",
            "Glynn",
            "Gollin",
            "Gorlin",
            "Kalin",
            "Karlan",
            "Karleen",
            "Karlen",
            "Karlene",
            "Karlin",
            "Karlyn",
            "Kaylyn",
            "Keelin",
            "Kellen",
            "Kellene",
            "Kellyann",
            "Kellyn",
            "Khalin",
            "Kilan",
            "Kilian",
            "Killen",
            "Killian",
            "Killion",
            "Klein",
            "Kleon",
            "Kline",
            "Koerlin",
            "Kylen",
            "Kylynn",
            "Quillan",
            "Quillon",
            "Qulllon",
            "Xylon"});
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedRandomNameTN11111111() throws EncoderException {
        this.checkEncodingVariations("TN11111111", new String[]{
            "Dan",
            "Dane",
            "Dann",
            "Darn",
            "Daune",
            "Dawn",
            "Ddene",
            "Dean",
            "Deane",
            "Deanne",
            "DeeAnn",
            "Deeann",
            "Deeanne",
            "Deeyn",
            "Den",
            "Dene",
            "Denn",
            "Deonne",
            "Diahann",
            "Dian",
            "Diane",
            "Diann",
            "Dianne",
            "Diannne",
            "Dine",
            "Dion",
            "Dione",
            "Dionne",
            "Doane",
            "Doehne",
            "Don",
            "Donn",
            "Doone",
            "Dorn",
            "Down",
            "Downe",
            "Duane",
            "Dun",
            "Dunn",
            "Duyne",
            "Dyan",
            "Dyane",
            "Dyann",
            "Dyanne",
            "Dyun",
            "Tan",
            "Tann",
            "Teahan",
            "Ten",
            "Tenn",
            "Terhune",
            "Thain",
            "Thaine",
            "Thane",
            "Thanh",
            "Thayne",
            "Theone",
            "Thin",
            "Thorn",
            "Thorne",
            "Thun",
            "Thynne",
            "Tien",
            "Tine",
            "Tjon",
            "Town",
            "Towne",
            "Turne",
            "Tyne"});
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedRandomNameTTA1111111() throws EncoderException {
        this.checkEncodingVariations("TTA1111111", new String[]{
            "Darda",
            "Datha",
            "Dedie",
            "Deedee",
            "Deerdre",
            "Deidre",
            "Deirdre",
            "Detta",
            "Didi",
            "Didier",
            "Dido",
            "Dierdre",
            "Dieter",
            "Dita",
            "Ditter",
            "Dodi",
            "Dodie",
            "Dody",
            "Doherty",
            "Dorthea",
            "Dorthy",
            "Doti",
            "Dotti",
            "Dottie",
            "Dotty",
            "Doty",
            "Doughty",
            "Douty",
            "Dowdell",
            "Duthie",
            "Tada",
            "Taddeo",
            "Tadeo",
            "Tadio",
            "Tati",
            "Teador",
            "Tedda",
            "Tedder",
            "Teddi",
            "Teddie",
            "Teddy",
            "Tedi",
            "Tedie",
            "Teeter",
            "Teodoor",
            "Teodor",
            "Terti",
            "Theda",
            "Theodor",
            "Theodore",
            "Theta",
            "Thilda",
            "Thordia",
            "Tilda",
            "Tildi",
            "Tildie",
            "Tildy",
            "Tita",
            "Tito",
            "Tjader",
            "Toddie",
            "Toddy",
            "Torto",
            "Tuddor",
            "Tudor",
            "Turtle",
            "Tuttle",
            "Tutto"});
    }

    /**
     * See http://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios     */
    @Test
    public void testCaverphoneRevisitedRandomWords() throws EncoderException {
        this.checkEncodingVariations("RTA1111111", new String[]{"rather", "ready", "writer"});
        this.checkEncoding("SSA1111111", "social");
        this.checkEncodingVariations("APA1111111", new String[]{"able", "appear"});
    }

    @Test
    public void testEndMb() throws EncoderException {
        final String[][] data = {{"mb", "M111111111"}, {"mbmb", "MPM1111111"}};
        this.checkEncodings(data);
    }

    // Caverphone Revisited
    @Test
    public void testIsCaverphoneEquals() throws EncoderException {
        final Caverphone2 caverphone = new Caverphone2();
        Assert.assertFalse("Caverphone encodings should not be equal", caverphone.isEncodeEqual("Peter", "Stevenson"));
        Assert.assertTrue("Caverphone encodings should be equal", caverphone.isEncodeEqual("Peter", "Peady"));
    }

    @Test
    public void testSpecificationExamples() throws EncoderException {
        final String[][] data = {
            {"Peter", "PTA1111111"},
            {"ready", "RTA1111111"},
            {"social", "SSA1111111"},
            {"able", "APA1111111"},
            {"Tedder", "TTA1111111"},
            {"Karleen", "KLN1111111"},
            {"Dyun", "TN11111111"}};
        this.checkEncodings(data);
    }

}
