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
 *    "Apache" nor may "Apache" appear in their names without prior 
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
 * Testcase for DoubleMetaphone
 * 
 * @author Mindbridge
 */
public class DoubleMetaphoneTest extends StringEncoderAbstractTest
{

    public DoubleMetaphoneTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return (new TestSuite(DoubleMetaphoneTest.class));
    }

    public void setUp() throws Exception
    {
        super.setUp();
        _doubleMetaphone = new DoubleMetaphone();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        _doubleMetaphone = null;
    }

    protected StringEncoder makeEncoder()
    {
        return new Metaphone();
    }

    // ------------------------------------------------------------------------

    public void testDoubleMetaphone()
    {
        assertEquals("TSTN", _doubleMetaphone.doubleMetaphone("testing"));
        assertEquals("0", _doubleMetaphone.doubleMetaphone("The"));
        assertEquals("KK", _doubleMetaphone.doubleMetaphone("quick"));
        assertEquals("PRN", _doubleMetaphone.doubleMetaphone("brown"));
        assertEquals("FKS", _doubleMetaphone.doubleMetaphone("fox"));
        assertEquals("JMPT", _doubleMetaphone.doubleMetaphone("jumped"));
        assertEquals("AFR", _doubleMetaphone.doubleMetaphone("over"));
        assertEquals("0", _doubleMetaphone.doubleMetaphone("the"));
        assertEquals("LS", _doubleMetaphone.doubleMetaphone("lazy"));
        assertEquals("TKS", _doubleMetaphone.doubleMetaphone("dogs"));
        assertEquals("MKFR", _doubleMetaphone.doubleMetaphone("MacCafferey"));

        assertEquals("TSTN", _doubleMetaphone.doubleMetaphone("testing", true));
        assertEquals("T", _doubleMetaphone.doubleMetaphone("The", true));
        assertEquals("KK", _doubleMetaphone.doubleMetaphone("quick", true));
        assertEquals("PRN", _doubleMetaphone.doubleMetaphone("brown", true));
        assertEquals("FKS", _doubleMetaphone.doubleMetaphone("fox", true));
        assertEquals("AMPT", _doubleMetaphone.doubleMetaphone("jumped", true));
        assertEquals("AFR", _doubleMetaphone.doubleMetaphone("over", true));
        assertEquals("T", _doubleMetaphone.doubleMetaphone("the", true));
        assertEquals("LS", _doubleMetaphone.doubleMetaphone("lazy", true));
        assertEquals("TKS", _doubleMetaphone.doubleMetaphone("dogs", true));
        assertEquals("MKFR", _doubleMetaphone.doubleMetaphone("MacCafferey", true));
    }

    public void testIsDoubleMetaphoneEqual()
    {
        doubleMetaphoneEqualTest(false);
        doubleMetaphoneEqualTest(true);
    }

    public void doubleMetaphoneEqualTest(boolean alternate)
    {
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("Case", "case", alternate));
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("CASE", "Case", alternate));
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("caSe", "cAsE", alternate));

        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("cookie", "quick", alternate));
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("quick", "cookie", alternate));

        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("Bryan", "Brian", alternate));
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("Brian", "Bryan", alternate));

        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("Otto", "Auto", alternate));
        assertTrue(_doubleMetaphone.isDoubleMetaphoneEqual("Auto", "Otto", alternate));

        assertTrue(!_doubleMetaphone.isDoubleMetaphoneEqual("Brain", "Band", alternate));
        assertTrue(!_doubleMetaphone.isDoubleMetaphoneEqual("Band", "Brain", alternate));
    }

    private DoubleMetaphone _doubleMetaphone = null;
}
