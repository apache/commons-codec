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
 * Encodes a string into a refined soundex value.  
 * A refined soundex code is optimized for spell checking word. 
 * "Soundex" method originally developed by Margaret Odell and 
 * Robert Russell
 * 
 * @author Tim O'Brien
 * @author ggregory@seagullsw.com
 * @version $Id: RefinedSoundex.java,v 1.9 2003/10/05 21:45:48 tobrien Exp $
 */
public class RefinedSoundex implements StringEncoder {

    /**
     * RefinedSoundex is *refined* for a number of
     * reasons one being that the mappings have been
     * altered.  This implementation contains default
     * mappings for US English.
     */
    public static final char[] US_ENGLISH_MAPPING =
        "01360240043788015936020505".toCharArray();

    /**
     * This static variable contains an instance of the
     * RefinedSoundex using the US_ENGLISH mapping.
     */
    public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
    
    /**
     * Every letter of the alphabet is "mapped" to a numerical 
     * value.  This char array holds the values to which each
     * letter is mapped.  This implementation contains a default
     * map for US_ENGLISH
     */
    private char[] soundexMapping;

    /**
     * Creates an instance of the RefinedSoundex object using the
     * default US English mapping.
     */
    public RefinedSoundex() {
        this(US_ENGLISH_MAPPING);
    }

    /**
     * Creates a refined soundex instance using a custom mapping.  This
     * constructor can be used to customize the mapping, and/or possibly
     * provide an internationalized mapping for a non-Western character
     * set.
     *
     * @param mapping Mapping array to use when finding the corresponding
     *                code for a given character
     */
    public RefinedSoundex(char[] mapping) {
        this.soundexMapping = mapping;
    }

    /**
     * Retreives the Refined Soundex code for a given String object.
     *
     * @param str String to encode using the Refined Soundex algorithm
     * @return A soundex code for the String supplied
     */
    public String soundex(String str) {
        if (null == str || str.length() == 0) { return str; }
       
        StringBuffer sBuf = new StringBuffer();        
        str = str.toUpperCase();

        sBuf.append(str.charAt(0));

        char last, current;
        last = '*';

        for (int i = 0; i < str.length(); i++) {

            current = getMappingCode(str.charAt(i));
            if (current == last) {
                continue;
            } else if (current != 0) {
                sBuf.append(current);   
            }
            
            last = current;             
            
        }
        
        return sBuf.toString();
    }

    /**
     * Encodes a String using the refined soundex algorithm. 
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
     * Encodes an Object using the refined soundex algorithm.  This method
     * is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the
     * supplied object is not of type java.lang.String.
     *
     * @param pObject Object to encode
     * @return An object (or type java.lang.String) containing the 
     *         refined soundex code which corresponds to the String supplied.
     * @throws EncoderException if the parameter supplied is not
     *                          of type java.lang.String
     */
    public Object encode(Object pObject) throws EncoderException {
        Object result;
        if (!(pObject instanceof java.lang.String)) {
            throw new EncoderException("Parameter supplied to " 
                                       + "RefinedSoundex " 
                                       + "encode is not of type " 
                                       + "java.lang.String"); 
        } else {
            result = soundex((String) pObject);
        }
        return result;
    }

    /**
     * Returns the mapping code for a given character.  The mapping
     * codes are maintained in an internal char array named soundexMapping,
     * and the default values of these mappings are US English.
     *
     * @param c char to get mapping for
     * @return A character (really a numeral) to return for the given char
     */
    private char getMappingCode(char c) {
        if (!Character.isLetter(c)) {
            return 0;
        } else {
            return soundexMapping[Character.toUpperCase(c) - 'A'];
        }
    }
}
