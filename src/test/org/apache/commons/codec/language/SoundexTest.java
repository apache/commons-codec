/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their name without prior 
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

import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * @version $Revision: 1.5 $ $Date: 2003/11/04 02:43:09 $
 * @author Rodney Waldhoff
 * @author Gary Gregory
 */
public class SoundexTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return (new TestSuite(SoundexTest.class));
    }

    private Soundex _encoder = null;

    public SoundexTest(String name) {
        super(name);
    }
    /**
     * @return Returns the _encoder.
     */
    public Soundex getEncoder() {
        return this._encoder;
    }

    protected StringEncoder makeEncoder() {
        return new Soundex();
    }

    /**
     * @param _encoder The _encoder to set.
     */
    public void setEncoder(Soundex encoder) {
        this._encoder = encoder;
    }

    public void setUp() throws Exception {        
        super.setUp();
        this.setEncoder(new Soundex());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        this.setEncoder(null);
    }

    // ------------------------------------------------------------------------

    public void testEncode() throws Exception {
        assertEquals("T235",this.getEncoder().encode("testing"));
        assertEquals("T000",this.getEncoder().encode("The"));
        assertEquals("Q200",this.getEncoder().encode("quick"));
        assertEquals("B650",this.getEncoder().encode("brown"));
        assertEquals("F200",this.getEncoder().encode("fox"));
        assertEquals("J513",this.getEncoder().encode("jumped"));
        assertEquals("O160",this.getEncoder().encode("over"));
        assertEquals("T000",this.getEncoder().encode("the"));
        assertEquals("L200",this.getEncoder().encode("lazy"));
        assertEquals("D200",this.getEncoder().encode("dogs"));
    }

    /**
     * Examples from
     * http://www.bradandkathy.com/genealogy/overviewofsoundex.html
     */
    public void testEncode2() throws Exception {
        assertEquals("A462",this.getEncoder().encode("Allricht"));
        assertEquals("E166",this.getEncoder().encode("Eberhard"));
        assertEquals("E521",this.getEncoder().encode("Engebrethson"));
        assertEquals("H512",this.getEncoder().encode("Heimbach"));
        assertEquals("H524",this.getEncoder().encode("Hanselmann"));
        assertEquals("H431",this.getEncoder().encode("Hildebrand"));
        assertEquals("K152",this.getEncoder().encode("Kavanagh"));
        assertEquals("L530",this.getEncoder().encode("Lind, Van"));
        assertEquals("L222",this.getEncoder().encode("Lukaschowsky"));
        assertEquals("M235",this.getEncoder().encode("McDonnell"));
        assertEquals("M200",this.getEncoder().encode("McGee"));
        // Fix me?
        //assertEquals("O165",this.getEncoder().encode("O'Brien"));
        assertEquals("O155",this.getEncoder().encode("Opnian"));
        assertEquals("O155",this.getEncoder().encode("Oppenheimer"));
        // Fix me?
        //assertEquals("S460",this.getEncoder().encode("Swhgler"));
        assertEquals("R355",this.getEncoder().encode("Riedemanas"));
        assertEquals("Z300",this.getEncoder().encode("Zita"));
        assertEquals("Z325",this.getEncoder().encode("Zitzmeinn"));    
    }
    
    public void testMaxLength() throws Exception {
        Soundex soundex = new Soundex();
        soundex.setMaxLength( soundex.getMaxLength() );
    }

}
