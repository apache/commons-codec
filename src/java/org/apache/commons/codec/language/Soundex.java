/* ====================================================================
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Commons", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 */
package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a soundex value.  Soundex is an encoding used to
 * relate similar names, but can also be used as a general purpose
 * scheme to find word with similar phonemes. 
 * 
 * @author bayard@generationjava.com
 * @author Tim O'Brien
 * @author ggregory@seagullsw.com
 * @version $Id: Soundex.java,v 1.6 2003/07/30 22:34:18 tobrien Exp $
 */
public class Soundex implements StringEncoder {

    /**
     * This is a default mapping of the 26 letters used
     * in US english.
     */
    public static final char[] US_ENGLISH_MAPPING =
        "01230120022455012623010202".toCharArray();

    /**
     * This static variable contains an instance of the
     * Soundex using the US_ENGLISH mapping.
     */
    public static final Soundex US_ENGLISH = new Soundex();
    
    /**
     * Every letter of the alphabet is "mapped" to a numerical 
     * value.  This char array holds the values to which each
     * letter is mapped.  This implementation contains a default
     * map for US_ENGLISH
     */
    private char[] soundexMapping;

    /**
     * The maximum length of a Soundex code - Soundex codes are
     * only four characters by definition.
     */
    private int maxLength = 4;

    /**
     * Creates an instance of the Soundex object using the default
     * US_ENGLISH mapping.
     */
    public Soundex() {
        this(US_ENGLISH_MAPPING);
    }

    /**
     * Creates a soundex instance using a custom mapping.  This
     * constructor can be used to customize the mapping, and/or possibly
     * provide an internationalized mapping for a non-Western character
     * set.
     *
     * @param mapping Mapping array to use when finding the corresponding
     *                code for a given character
     */
    public Soundex(char[] mapping) {
        this.soundexMapping = mapping;
    }

    /**
     * Retreives the Soundex code for a given String object.
     *
     * @param str String to encode using the Soundex algorithm
     * @return A soundex code for the String supplied
     */
    public String soundex(String str) {
        if (null == str || str.length() == 0) { return str; }
        
        char out[] = { '0', '0', '0', '0' };
        char last, mapped;
        int incount = 1, count = 1;
        out[0] = Character.toUpperCase(str.charAt(0));
        last = getMappingCode(str.charAt(0));
        while ((incount < str.length()) 
              && (mapped = getMappingCode(str.charAt(incount++))) != 0 
              && (count < maxLength)) {
                if ((mapped != '0') && (mapped != last)) {
                    out[count++] = mapped;
                }
                last = mapped;
            }
        return new String(out);
    }

    /**
     * Encodes an Object using the soundex algorithm.  This method
     * is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the
     * supplied object is not of type java.lang.String.
     *
     * @param pObject Object to encode
     * @return An object (or type java.lang.String) containing the 
     *         soundex code which corresponds to the String supplied.
     * @throws EncoderException if the parameter supplied is not
     *                          of type java.lang.String
     */
    public Object encode(Object pObject) throws EncoderException {

        Object result;

        if (!(pObject instanceof java.lang.String)) {
            throw new EncoderException("Parameter supplied to " 
                                       + "Soundex " 
                                       + "encode is not of type " 
                                       + "java.lang.String"); 
        } else {
            result = soundex((String) pObject);
        }

        return result;

    }

    /**
     * Encodes a String using the soundex algorithm. 
     *
     * @param pString A String object to encode
     * @return A Soundex code corresponding to the String supplied
     * @throws EncoderException throws exception if there is an
     *                          encoding-specific problem
     */
    public String encode(String pString) throws EncoderException {
        return (soundex(pString));   
    }

    /**
     * Used internally by the SoundEx algorithm.
     *
     * @param c character to use to retrieve mapping code
     * @return Mapping code for a particular character
     */
    private char getMappingCode(char c) {
        if (!Character.isLetter(c)) {
            return 0;
        } else {
            return soundexMapping[Character.toUpperCase(c) - 'A'];
        }
    }

    /**
     * Returns the maxLength.  Standard Soundex
     * @return int
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maxLength.
     * @param maxLength The maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

}
