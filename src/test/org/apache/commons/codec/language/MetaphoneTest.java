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
 * @version $Revision: 1.4 $ $Date: 2003/10/05 21:45:49 $
 * @author Rodney Waldhoff
 */
public class MetaphoneTest extends StringEncoderAbstractTest {

    public MetaphoneTest(String name) {
        super(name);
    }

    public static Test suite() {
        return (new TestSuite(MetaphoneTest.class));
    }

    public void setUp() throws Exception {        
        super.setUp();
        _metaphone = new Metaphone();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        _metaphone = null;
    }

    protected StringEncoder makeEncoder() {
        return new Metaphone();
    }
    
    // ------------------------------------------------------------------------

    public void testMetaphone() {
        assertEquals("TSTN",_metaphone.metaphone("testing"));
        assertEquals("0",_metaphone.metaphone("The"));
        assertEquals("KK",_metaphone.metaphone("quick"));
        assertEquals("BRN",_metaphone.metaphone("brown"));
        assertEquals("FKS",_metaphone.metaphone("fox"));
        assertEquals("JMPT",_metaphone.metaphone("jumped"));
        assertEquals("OFR",_metaphone.metaphone("over"));
        assertEquals("0",_metaphone.metaphone("the"));
        assertEquals("LS",_metaphone.metaphone("lazy"));
        assertEquals("TKS",_metaphone.metaphone("dogs"));
    }

    public void testIsMetaphoneEqual() {
        assertTrue(_metaphone.isMetaphoneEqual("Case","case"));
        assertTrue(_metaphone.isMetaphoneEqual("CASE","Case"));
        assertTrue(_metaphone.isMetaphoneEqual("caSe","cAsE"));
        
        assertTrue(_metaphone.isMetaphoneEqual("cookie","quick"));
        assertTrue(_metaphone.isMetaphoneEqual("quick","cookie"));
    }

    private Metaphone _metaphone = null;
}
