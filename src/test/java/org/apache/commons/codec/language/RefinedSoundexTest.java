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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests RefinedSoundex.
 *
 */
public class RefinedSoundexTest extends StringEncoderAbstractTest<RefinedSoundex> {

    @Override
    protected RefinedSoundex createStringEncoder() {
        return new RefinedSoundex();
    }

    @Test
    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, this.getStringEncoder().difference(null, null));
        assertEquals(0, this.getStringEncoder().difference("", ""));
        assertEquals(0, this.getStringEncoder().difference(" ", " "));
        // Normal cases
        assertEquals(6, this.getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(3, this.getStringEncoder().difference("Ann", "Andrew"));
        assertEquals(1, this.getStringEncoder().difference("Margaret", "Andrew"));
        assertEquals(1, this.getStringEncoder().difference("Janet", "Margaret"));
        // Examples from
        // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(5, this.getStringEncoder().difference("Green", "Greene"));
        assertEquals(1, this.getStringEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from
        // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(6, this.getStringEncoder().difference("Smith", "Smythe"));
        assertEquals(8, this.getStringEncoder().difference("Smithers", "Smythers"));
        assertEquals(5, this.getStringEncoder().difference("Anothers", "Brothers"));
    }

    @Test
    public void testEncode() {
        assertEquals("T6036084", this.getStringEncoder().encode("testing"));
        assertEquals("T6036084", this.getStringEncoder().encode("TESTING"));
        assertEquals("T60", this.getStringEncoder().encode("The"));
        assertEquals("Q503", this.getStringEncoder().encode("quick"));
        assertEquals("B1908", this.getStringEncoder().encode("brown"));
        assertEquals("F205", this.getStringEncoder().encode("fox"));
        assertEquals("J408106", this.getStringEncoder().encode("jumped"));
        assertEquals("O0209", this.getStringEncoder().encode("over"));
        assertEquals("T60", this.getStringEncoder().encode("the"));
        assertEquals("L7050", this.getStringEncoder().encode("lazy"));
        assertEquals("D6043", this.getStringEncoder().encode("dogs"));

        // Testing CODEC-56
        assertEquals("D6043", RefinedSoundex.US_ENGLISH.encode("dogs"));
    }

    @Test
    public void testGetMappingCodeNonLetter() {
        final char code = this.getStringEncoder().getMappingCode('#');
        assertEquals(0, code, "Code does not equals zero");
    }

    @Test
    public void testNewInstance() {
        assertEquals("D6043", new RefinedSoundex().soundex("dogs"));
    }

    @Test
    public void testNewInstance2() {
        assertEquals("D6043", new RefinedSoundex(RefinedSoundex.US_ENGLISH_MAPPING_STRING.toCharArray()).soundex("dogs"));
    }

    @Test
    public void testNewInstance3() {
        assertEquals("D6043", new RefinedSoundex(RefinedSoundex.US_ENGLISH_MAPPING_STRING).soundex("dogs"));
    }
}
