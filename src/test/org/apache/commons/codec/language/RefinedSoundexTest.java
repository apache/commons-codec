/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//codec/src/test/org/apache/commons/codec/language/RefinedSoundexTest.java,v 1.2 2003/08/18 03:01:14 bayard Exp $
 * $Revision: 1.2 $
 * $Date: 2003/08/18 03:01:14 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * @version $Revision: 1.2 $ $Date: 2003/08/18 03:01:14 $
 * @author Rodney Waldhoff
 */
public class RefinedSoundexTest extends StringEncoderAbstractTest {

    public RefinedSoundexTest(String name) {
        super(name);
    }

    public static Test suite() {
        return (new TestSuite(RefinedSoundexTest.class));
    }

    public void setUp() throws Exception {        
        super.setUp();
        _encoder = new RefinedSoundex();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        _encoder = null;
    }

    protected StringEncoder makeEncoder() {
        return new RefinedSoundex();
    }

    // ------------------------------------------------------------------------

    public void testEncode() throws Exception {
        assertEquals("T6036084",_encoder.encode("testing"));
        assertEquals("T60",_encoder.encode("The"));
        assertEquals("Q503",_encoder.encode("quick"));
        assertEquals("B1908",_encoder.encode("brown"));
        assertEquals("F205",_encoder.encode("fox"));
        assertEquals("J408106",_encoder.encode("jumped"));
        assertEquals("O0209",_encoder.encode("over"));
        assertEquals("T60",_encoder.encode("the"));
        assertEquals("L7050",_encoder.encode("lazy"));
        assertEquals("D6043",_encoder.encode("dogs"));
    }

    private RefinedSoundex _encoder = null;
}
