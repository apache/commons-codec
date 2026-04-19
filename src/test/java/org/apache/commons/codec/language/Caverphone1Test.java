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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.codec.AbstractStringEncoderTest;
import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

/**
 * Tests Caverphone1.
 */
class Caverphone1Test extends AbstractStringEncoderTest<Caverphone1> {

    @Override
    protected Caverphone1 createStringEncoder() {
        return new Caverphone1();
    }

    /**
     * Tests example adapted from version 2.0  https://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * AT1111 words: add, aid, at, art, eat, earth, head, hit, hot, hold, hard, heart, it, out, old
     *
     * @throws EncoderException for some failure scenarios.
     */
    @Test
    void testCaverphoneRevisitedCommonCodeAT1111() throws EncoderException {
        // @formatter:off
        checkEncodingVariations("AT1111",
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
            "old");
        // @formatter:on
    }

    @Test
    void testEndMb() throws EncoderException {
        checkEncodings(new String[][] {{"mb", "M11111"}, {"mbmb", "MPM111"}});
    }

    /**
     * Tests some examples from version 2.0 https://caversham.otago.ac.nz/files/working/ctp150804.pdf
     *
     * @throws EncoderException for some failure scenarios.
     */
    @Test
    void testIsCaverphoneEquals() throws EncoderException {
        final Caverphone1 caverphone = new Caverphone1();
        assertFalse(caverphone.isEncodeEqual("Peter", "Stevenson"), "Caverphone encodings should not be equal");
        assertTrue(caverphone.isEncodeEqual("Peter", "Peady"), "Caverphone encodings should be equal");
    }

    /**
     * Tests example from https://caversham.otago.ac.nz/files/working/ctp060902.pdf
     *
     * @throws EncoderException for some failure scenarios.
     */
    @Test
    void testSpecificationV1Examples() throws EncoderException {
        checkEncodings(new String[][] {{"David", "TFT111"}, {"Whittle", "WTL111"}});
    }

    /**
     * Tests examples from https://en.wikipedia.org/wiki/Caverphone
     *
     * @throws EncoderException for some failure scenarios.
     */
    @Test
    void testWikipediaExamples() throws EncoderException {
        checkEncodings(new String[][] {{"Lee", "L11111"}, {"Thompson", "TMPSN1"}});
    }

}
