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

package org.apache.commons.codec.binary ;

import junit.framework.TestCase;

/**
 * TestCase for Binary class.
 *
 * @author <a href="mailto:akarasulu@apache.org">Alex Karasulu</a>
 * @author $Author: ggregory $
 * @version $Rev$
 */
public class BinaryTest extends TestCase
{
    Binary instance = null ;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        instance = new Binary() ;
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        instance = null ;
    }

    /**
     * Constructor for BinaryTest.
     * @param arg0
     */
    public BinaryTest(String arg0)
    {
        super(arg0);
    }

    // ------------------------------------------------------------------------
    //
    // Test decode(Object)
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for Object decode(Object)
     */
    public void testDecodeObject() throws Exception
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        byte [] decoded = ( byte[] ) instance.decode( ( Object ) "00000000" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00000001" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00000011" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00000111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00001111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00011111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "00111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        decoded = ( byte[] ) instance.decode( ( Object ) "01111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "11111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0000000011111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0000000111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0000001111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0000011111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0000111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0001111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0011111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "0111111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = ( byte[] ) instance.decode( ( Object ) "1111111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test decode(byte[])
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for byte[] decode(byte[])
     */
    public void testDecodebyteArray()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        byte [] decoded = instance.decode( "00000000".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        decoded = instance.decode( "00000001".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        decoded = instance.decode( "00000011".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        decoded = instance.decode( "00000111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        decoded = instance.decode( "00001111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        decoded = instance.decode( "00011111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        decoded = instance.decode( "00111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        decoded = instance.decode( "01111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "11111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000000011111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000000111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000001111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000011111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0001111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0011111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0111111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "1111111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test decode(String)
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for byte[] decode(String)
     */
    public void testDecodeString()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        byte [] decoded = instance.decode( "00000000" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        decoded = instance.decode( "00000001" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        decoded = instance.decode( "00000011" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        decoded = instance.decode( "00000111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        decoded = instance.decode( "00001111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        decoded = instance.decode( "00011111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        decoded = instance.decode( "00111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        decoded = instance.decode( "01111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "11111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000000011111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000000111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000001111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000011111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0000111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0001111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0011111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "0111111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = instance.decode( "1111111111111111" ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test fromAscii(char[])
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for byte[] fromAscii(char[])
     */
    public void testFromAsciicharArray()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        byte [] decoded = Binary.fromAscii( "00000000".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        decoded = Binary.fromAscii( "00000001".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        decoded = Binary.fromAscii( "00000011".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        decoded = Binary.fromAscii( "00000111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        decoded = Binary.fromAscii( "00001111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        decoded = Binary.fromAscii( "00011111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        decoded = Binary.fromAscii( "00111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        decoded = Binary.fromAscii( "01111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "11111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000000011111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000000111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000001111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000011111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000111111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0001111111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0011111111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0111111111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "1111111111111111".toCharArray() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;
    }

    
    // ------------------------------------------------------------------------
    //
    // Test fromAscii(byte[])
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for byte[] fromAscii(byte[])
     */
    public void testFromAsciibyteArray()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        byte [] decoded = Binary.fromAscii( "00000000".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        decoded = Binary.fromAscii( "00000001".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        decoded = Binary.fromAscii( "00000011".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        decoded = Binary.fromAscii( "00000111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        decoded = Binary.fromAscii( "00001111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        decoded = Binary.fromAscii( "00011111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        decoded = Binary.fromAscii( "00111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        decoded = Binary.fromAscii( "01111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "11111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000000011111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000000111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000001111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000011111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0000111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0001111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0011111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "0111111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

        bits = new byte[2] ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        decoded = Binary.fromAscii( "1111111111111111".getBytes() ) ;
        assertEquals( new String( bits ), new String( decoded ) ) ;

    }
    
    
    // ------------------------------------------------------------------------
    //
    // Test encode(byte[])
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for byte[] encode(byte[])
     */
    public void testEncodebyteArray()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        String l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00000000", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00000001", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00000011", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00000111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00001111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00011111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "00111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "01111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "11111111", l_encoded ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000000000", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000000001", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000000011", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000000111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000001111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000011111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000000111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000001111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000011111111", l_encoded ) ;

                    // work on the other byte now
        
        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000000111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000001111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000011111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0000111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0001111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0011111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "0111111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( instance.encode( bits ) ) ;
        assertEquals( "1111111111111111", l_encoded ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test toAsciiBytes
    //
    // ------------------------------------------------------------------------

    
    public void testToAsciiBytes()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        String l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00000000", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00000001", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00000011", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00000111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00001111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00011111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "00111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "01111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "11111111", l_encoded ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000000000", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000000001", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000000011", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000000111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000001111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000011111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000000111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000001111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000011111111", l_encoded ) ;

                    // work on the other byte now
        
        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000000111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000001111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000011111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0000111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0001111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0011111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "0111111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiBytes( bits ) ) ;
        assertEquals( "1111111111111111", l_encoded ) ;
    }

    // ------------------------------------------------------------------------
    //
    // Test toAsciiChars
    //
    // ------------------------------------------------------------------------

    
    public void testToAsciiChars()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        String l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00000000", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00000001", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00000011", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00000111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00001111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00011111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "00111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "01111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "11111111", l_encoded ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000000000", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000000001", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000000011", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000000111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000001111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000011111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000000111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000001111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000011111111", l_encoded ) ;

                    // work on the other byte now
        
        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000000111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000001111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000011111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0000111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0001111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0011111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "0111111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( Binary.toAsciiChars( bits ) ) ;
        assertEquals( "1111111111111111", l_encoded ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test toAsciiString
    //
    // ------------------------------------------------------------------------

    
    public void testToAsciiString()
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        String l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00000000", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00000001", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00000011", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00000111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00001111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00011111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "00111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "01111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "11111111", l_encoded ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000000000", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000000001", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000000011", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000000111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000001111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000011111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000000111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000001111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000011111111", l_encoded ) ;

                    // work on the other byte now
        
        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000000111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000001111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000011111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0000111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0001111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0011111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "0111111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = Binary.toAsciiString( bits ) ;
        assertEquals( "1111111111111111", l_encoded ) ;
    }


    // ------------------------------------------------------------------------
    //
    // Test encode(Object)
    //
    // ------------------------------------------------------------------------

    
    /*
     * Class to test for Object encode(Object)
     */
    public void testEncodeObject() throws Exception
    {
        // With a single raw binary
        
        byte [] bits = new byte[1] ;
        String l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00000000", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00000001", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00000011", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00000111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00001111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00011111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "00111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "01111111", l_encoded ) ;

        bits = new byte[1] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "11111111", l_encoded ) ;

        // With a two raw binaries
        
        bits = new byte[2] ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000000000", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000000001", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000000011", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000000111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000001111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000011111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000000111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000001111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000011111111", l_encoded ) ;

                    // work on the other byte now
        
        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000000111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000001111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000011111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0000111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0001111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0011111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[1] = Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "0111111111111111", l_encoded ) ;

        bits = new byte[2] ;
        bits[0] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        bits[1] = ( byte ) ( Binary.BIT_0 | Binary.BIT_1 | Binary.BIT_2 
            | Binary.BIT_3 | Binary.BIT_4 | Binary.BIT_5 | Binary.BIT_6 
            | Binary.BIT_7 ) ;
        l_encoded = new String( ( char [] ) 
                instance.encode( ( Object ) bits ) ) ;
        assertEquals( "1111111111111111", l_encoded ) ;
    }
}
