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

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Test;

/**
 * Tests RefinedSoundex.
 * 
 * @version $Id$
 */
public class RefinedSoundexTest extends StringEncoderAbstractTest {

    @Override
    protected StringEncoder createStringEncoder() {
        return new RefinedSoundex();
    }

    /**
     * @return Returns the encoder.
     */
    private RefinedSoundex getRefinedSoundex() {
        return (RefinedSoundex)this.getStringEncoder();
    }

    @Test
    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, this.getRefinedSoundex().difference(null, null));
        assertEquals(0, this.getRefinedSoundex().difference("", ""));
        assertEquals(0, this.getRefinedSoundex().difference(" ", " "));
        // Normal cases
        assertEquals(6, this.getRefinedSoundex().difference("Smith", "Smythe"));
        assertEquals(3, this.getRefinedSoundex().difference("Ann", "Andrew"));
        assertEquals(1, this.getRefinedSoundex().difference("Margaret", "Andrew"));
        assertEquals(1, this.getRefinedSoundex().difference("Janet", "Margaret"));
        // Examples from
        // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(5, this.getRefinedSoundex().difference("Green", "Greene"));
        assertEquals(1, this.getRefinedSoundex().difference("Blotchet-Halls", "Greene"));
        // Examples from
        // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(6, this.getRefinedSoundex().difference("Smith", "Smythe"));
        assertEquals(8, this.getRefinedSoundex().difference("Smithers", "Smythers"));
        assertEquals(5, this.getRefinedSoundex().difference("Anothers", "Brothers"));
    }

    @Test
    public void testEncode() {
        assertEquals("T6036084", this.getRefinedSoundex().encode("testing"));
        assertEquals("T6036084", this.getRefinedSoundex().encode("TESTING"));
        assertEquals("T60", this.getRefinedSoundex().encode("The"));
        assertEquals("Q503", this.getRefinedSoundex().encode("quick"));
        assertEquals("B1908", this.getRefinedSoundex().encode("brown"));
        assertEquals("F205", this.getRefinedSoundex().encode("fox"));
        assertEquals("J408106", this.getRefinedSoundex().encode("jumped"));
        assertEquals("O0209", this.getRefinedSoundex().encode("over"));
        assertEquals("T60", this.getRefinedSoundex().encode("the"));
        assertEquals("L7050", this.getRefinedSoundex().encode("lazy"));
        assertEquals("D6043", this.getRefinedSoundex().encode("dogs"));

        // Testing CODEC-56
        assertEquals("D6043", RefinedSoundex.US_ENGLISH.encode("dogs"));
    }

    @Test
    public void testGetMappingCodeNonLetter() {
        char code = this.getRefinedSoundex().getMappingCode('#');
        assertEquals("Code does not equals zero", 0, code);
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
