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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * Tests RefinedSoundex.
 * 
 * @author Apache Software Foundation
 * @version $Id$
 */
public class RefinedSoundexTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return new TestSuite(RefinedSoundexTest.class);
    }

    private RefinedSoundex encoder = null;

    public RefinedSoundexTest(String name) {
        super(name);
    }

    /**
	 * @return Returns the encoder.
	 */
    private RefinedSoundex getEncoder() {
        return this.encoder;
    }

    protected StringEncoder makeEncoder() {
        return new RefinedSoundex();
    }

    /**
	 * @param encoder
	 *                  The encoder to set.
	 */
    private void setEncoder(RefinedSoundex encoder) {
        this.encoder = encoder;
    }

    public void setUp() throws Exception {
        super.setUp();
        this.setEncoder(new RefinedSoundex());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        this.setEncoder(null);
    }

    public void testDifference() throws EncoderException {
        // Edge cases
        assertEquals(0, this.getEncoder().difference(null, null));
        assertEquals(0, this.getEncoder().difference("", ""));
        assertEquals(0, this.getEncoder().difference(" ", " "));
        // Normal cases
        assertEquals(6, this.getEncoder().difference("Smith", "Smythe"));
        assertEquals(3, this.getEncoder().difference("Ann", "Andrew"));
        assertEquals(1, this.getEncoder().difference("Margaret", "Andrew"));
        assertEquals(1, this.getEncoder().difference("Janet", "Margaret"));
        // Examples from
		// http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp
        assertEquals(5, this.getEncoder().difference("Green", "Greene"));
        assertEquals(1, this.getEncoder().difference("Blotchet-Halls", "Greene"));
        // Examples from
		// http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_setu-sus_3o6w.asp
        assertEquals(6, this.getEncoder().difference("Smith", "Smythe"));
        assertEquals(8, this.getEncoder().difference("Smithers", "Smythers"));
        assertEquals(5, this.getEncoder().difference("Anothers", "Brothers"));
    }

    public void testEncode() {
        assertEquals("T6036084", this.getEncoder().encode("testing"));
        assertEquals("T6036084", this.getEncoder().encode("TESTING"));
        assertEquals("T60", this.getEncoder().encode("The"));
        assertEquals("Q503", this.getEncoder().encode("quick"));
        assertEquals("B1908", this.getEncoder().encode("brown"));
        assertEquals("F205", this.getEncoder().encode("fox"));
        assertEquals("J408106", this.getEncoder().encode("jumped"));
        assertEquals("O0209", this.getEncoder().encode("over"));
        assertEquals("T60", this.getEncoder().encode("the"));
        assertEquals("L7050", this.getEncoder().encode("lazy"));
        assertEquals("D6043", this.getEncoder().encode("dogs"));

        // Testing CODEC-56
        assertEquals("D6043", RefinedSoundex.US_ENGLISH.encode("dogs"));
    }

	public void testGetMappingCodeNonLetter() {
		char code = this.getEncoder().getMappingCode('#');
		assertEquals("Code does not equals zero", 0, code);
	}
}
