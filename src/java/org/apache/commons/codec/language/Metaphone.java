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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a metaphone value.
 * The initial Java implementation, William B. Brogden.  December, 1997
 * Permission given by wbrogden for code to be used anywhere.
 * 
 *  "Hanging on the Metaphone" by Lawrence Philips
 *      <i>Computer Language</i> of Dec. 1990, p 39   
 * 
 * @author wbrogden@bga.com
 * @author bayard@generationjava.com
 * @author Tim O'Brien
 * @author Gary Gregory
 * @version $Id: Metaphone.java,v 1.10 2003/11/07 23:12:54 ggregory Exp $
 */
public class Metaphone implements StringEncoder {

    /**
     * Five values in the English language 
     */
    private String vowels = "AEIOU" ;

    /**
     * Variable used in Metaphone algorithm
     */
    private String frontv = "EIY"   ;

    /**
     * Variable used in Metaphone algorithm
     */
    private String varson = "CSPTG" ;

    /**
     * The max code length for metaphone is 4
     */
    private int maxCodeLen = 4 ;

    /**
     * Creates an instance of the Metaphone encoder
     */
    public Metaphone() {
        super();
    }

    /**
     * Find the metaphone value of a String. This is similar to the
     * soundex algorithm, but better at finding similar sounding words.
     * All input is converted to upper case.
     * Limitations: Input format is expected to be a single ASCII word
     * with only characters in the A - Z range, no punctuation or numbers.
     *
     * @param txt String to find the metaphone code for
     * @return A metaphone code corresponding to the String supplied
     */
    public String metaphone(String txt) {
        int mtsz = 0  ;
        boolean hard = false ;
        if ((txt == null) || (txt.length() == 0)) {
            return "" ;
        }
        // single character is itself
        if (txt.length() == 1) {
            return txt.toUpperCase() ;
        }
      
        char[] inwd = txt.toUpperCase().toCharArray() ;
      
        String tmpS ;
        StringBuffer local = new StringBuffer(40); // manipulate
        StringBuffer code = new StringBuffer(10) ; //   output
        // handle initial 2 characters exceptions
        switch(inwd[0]) {
        case 'K' : 
        case 'G' : 
        case 'P' : /* looking for KN, etc*/
            if (inwd[1] == 'N') {
                local.append(inwd, 1, inwd.length - 1);
            } else {
                local.append(inwd);
            }
            break;
        case 'A': /* looking for AE */
            if (inwd[1] == 'E') {
                local.append(inwd, 1, inwd.length - 1);
            } else {
                local.append(inwd);
            }
            break;
        case 'W' : /* looking for WR or WH */
            if (inwd[1] == 'R') {   // WR -> R
                local.append(inwd, 1, inwd.length - 1); 
                break ;
            }
            if (inwd[1] == 'H') {
                local.append(inwd, 1, inwd.length - 1);
                local.setCharAt(0, 'W'); // WH -> W
            } else {
                local.append(inwd);
            }
            break;
        case 'X' : /* initial X becomes S */
            inwd[0] = 'S';
            local.append(inwd);
            break ;
        default :
            local.append(inwd);
        } // now local has working string with initials fixed

        int wdsz = local.length();
        int n = 0 ;

        while ((mtsz < this.getMaxCodeLen()) && (n < wdsz)) { // max code size of 4 works well
            char symb = local.charAt(n) ;
            // remove duplicate letters except C
            if ((symb != 'C') && (n > 0) && (local.charAt(n - 1) == symb)) {
                n++ ;
            } else { // not dup
                switch(symb) {
                case 'A' : case 'E' : case 'I' : case 'O' : case 'U' :
                    if (n == 0) { 
                        code.append(symb);
                        mtsz++;
                    }
                    break ; // only use vowel if leading char
                case 'B' :
                    if ((n > 0) && !(n + 1 == wdsz) && (local.charAt(n - 1) == 'M')) { // not MB at end of word 
                        code.append(symb);
                    } else {
                        code.append(symb);
                    }
                    mtsz++;
                    break;
                case 'C' : // lots of C special cases
                    /* discard if SCI, SCE or SCY */
                    if ((n > 0) && (local.charAt(n - 1) == 'S') && (n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0)) { 
                        break ;
                    }
                    tmpS = local.toString();
                    if (tmpS.indexOf("CIA", n) == n) { // "CIA" -> X
                        code.append('X'); mtsz++; break ;
                    }
                    if ((n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0)) {
                        code.append('S');
                        mtsz++; 
                        break ; // CI,CE,CY -> S
                    }
                    if ((n > 0) && (tmpS.indexOf("SCH", n - 1) == n - 1)) { // SCH->sk
                        code.append('K') ; 
                        mtsz++;
                        break ;
                    }
                    if (tmpS.indexOf("CH", n) == n) { // detect CH
                        if ((n == 0) && (wdsz >= 3) && (vowels.indexOf(local.charAt(2)) < 0)) { // CH consonant -> K consonant
                            code.append('K');
                        } else { 
                            code.append('X'); // CHvowel -> X
                        }
                        mtsz++;
                    } else { 
                        code.append('K');
                        mtsz++;
                    }
                    break ;
                case 'D' :
                    if ((n + 2 < wdsz)   && (local.charAt(n + 1) == 'G') && (frontv.indexOf(local.charAt(n + 2)) >= 0)) { // DGE DGI DGY -> J 
                        code.append('J'); n += 2 ;
                    } else { 
                        code.append('T');
                    }
                    mtsz++;
                    break ;
                case 'G' : // GH silent at end or before consonant
                    if ((n + 2 == wdsz) && (local.charAt(n + 1) == 'H')) {
                        break;
                    }
                    if ((n + 2 < wdsz) && (local.charAt(n + 1) == 'H') && (vowels.indexOf(local.charAt(n + 2)) < 0)) {
                        break;
                    }
                    tmpS = local.toString();
                    if ((n > 0) && (tmpS.indexOf("GN", n) == n) || (tmpS.indexOf("GNED", n) == n)) {
                        break; // silent G
                    }
                    if ((n > 0) && (local.charAt(n - 1) == 'G')) {
                        hard = true ;
                    } else {
                        hard = false ;
                    }
                    if ((n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0) && (!hard)) {
                        code.append('J');
                    } else {
                        code.append('K');
                    }
                    mtsz++;
                    break ;
                case 'H':
                    if (n + 1 == wdsz) {
                        break ; // terminal H
                    }
                    if ((n > 0) && (varson.indexOf(local.charAt(n - 1)) >= 0)) {
                        break;
                    }
                    if (vowels.indexOf(local.charAt(n + 1)) >= 0) {
                        code.append('H'); 
                        mtsz++;// Hvowel
                    }
                    break;
                case 'F': 
                case 'J' : 
                case 'L' :
                case 'M': 
                case 'N' : 
                case 'R' :
                    code.append(symb); 
                    mtsz++; 
                    break;
                case 'K' :
                    if (n > 0) { // not initial
                        if (local.charAt(n - 1) != 'C') {
                            code.append(symb);
                        }
                    } else {
                        code.append(symb); // initial K
                    }
                    mtsz++ ;
                    break ;
                case 'P' :
                    if ((n + 1 < wdsz) && (local.charAt(n + 1) == 'H')) {
                        // PH -> F
                        code.append('F');
                    } else {
                        code.append(symb);
                    }
                    mtsz++;
                    break ;
                case 'Q' :
                    code.append('K');
                    mtsz++; 
                    break;
                case 'S' :
                    tmpS = local.toString();
                    if ((tmpS.indexOf("SH", n) == n) || (tmpS.indexOf("SIO", n) == n) || (tmpS.indexOf("SIA", n) == n)) {
                        code.append('X');
                    } else {
                        code.append('S');
                    }
                    mtsz++;
                    break;
                case 'T' :
                    tmpS = local.toString(); // TIA TIO -> X
                    if ((tmpS.indexOf("TIA", n) == n) || (tmpS.indexOf("TIO", n) == n)) {
                        code.append('X'); 
                        mtsz++; 
                        break;
                    }
                    if (tmpS.indexOf("TCH", n) == n) {
                        break;
                    }
                    // substitute numeral 0 for TH (resembles theta after all)
                    if (tmpS.indexOf("TH", n) == n) {
                        code.append('0');
                    } else {
                        code.append('T');
                    }
                    mtsz++ ;
                    break ;
                case 'V' :
                    code.append('F'); mtsz++;break ;
                case 'W' : case 'Y' : // silent if not followed by vowel
                    if ((n + 1 < wdsz) && (vowels.indexOf(local.charAt(n + 1)) >= 0)) {
                        code.append(symb);
                        mtsz++;
                    }
                    break ;
                case 'X' :
                    code.append('K'); code.append('S');mtsz += 2;
                    break ;
                case 'Z' :
                    code.append('S'); mtsz++; break ;
                } // end switch
                n++ ;
            } // end else from symb != 'C'
            if (mtsz > this.getMaxCodeLen()) { code.setLength(this.getMaxCodeLen()); }
        }
        return code.toString();
    } 
    
    
    /**
     * Encodes an Object using the metaphone algorithm.  This method
     * is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the
     * supplied object is not of type java.lang.String.
     *
     * @param pObject Object to encode
     * @return An object (or type java.lang.String) containing the 
     *         metaphone code which corresponds to the String supplied.
     * @throws EncoderException if the parameter supplied is not
     *                          of type java.lang.String
     */
    public Object encode(Object pObject) throws EncoderException {
        Object result;
        if (!(pObject instanceof java.lang.String)) {
            throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String"); 
        } else {
            result = metaphone((String) pObject);
        }
        return result;
    }

    /**
     * Encodes a String using the Metaphone algorithm. 
     *
     * @param pString String object to encode
     * @return The metaphone code corresponding to the String supplied
     * @throws EncoderException thrown if a Metaphone specific exception
     *                          is encountered.
     */
    public String encode(String pString) {
        return (metaphone(pString));   
    }

    /**
     * Tests is the metaphones of two strings are identical.
     *
     * @param str1 First of two strings to compare
     * @param str2 Second of two strings to compare
     * @return true if the metaphones of these strings are identical, 
     *         false otherwise.
     */
    public boolean isMetaphoneEqual(String str1, String str2) {
        return metaphone(str1).equals(metaphone(str2));
    }

    /**
     * Returns the maxCodeLen.
     * @return int
     */
    public int getMaxCodeLen() { return this.maxCodeLen; }

    /**
     * Sets the maxCodeLen.
     * @param maxCodeLen The maxCodeLen to set
     */
    public void setMaxCodeLen(int maxCodeLen) { this.maxCodeLen = maxCodeLen; }

}
