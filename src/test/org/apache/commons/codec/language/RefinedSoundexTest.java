/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * @version $Id: RefinedSoundexTest.java,v 1.6 2004/01/02 07:05:36 ggregory Exp $
 * @author Rodney Waldhoff
 * @author Gary D. Gregory
 */
public class RefinedSoundexTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return (new TestSuite(RefinedSoundexTest.class));
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

    public void testEncode() throws EncoderException {
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
    }
}