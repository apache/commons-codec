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
 * Encodes a string into a soundex value.  Sounde is an encoding used to
 * relate similar names, but can also be used as a general purpose
 * scheme to find word with similar phonemes. 
 * More information may be found at: http://www.bluepoof.com/Soundex/info2.html
 * 
 * @author tobrien@transolutions.net
 * @author ggregory@seagullsw.com
 * @version $Revision: 1.1 $ $Date: 2003/04/25 17:50:56 $
 */
public class RefinedSoundex implements StringEncoder {

    public static final char[] US_ENGLISH_MAPPING =
        "01360240043788015936020505".toCharArray();

    public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
    
    private char[] soundexMapping;

    public RefinedSoundex() {
        this(US_ENGLISH_MAPPING);
    }

    public RefinedSoundex(char[] mapping) {
        this.soundexMapping = mapping;
    }

    /**
     * Get the SoundEx value of a string.
     * This implementation is taken from the code-snippers on 
     * http://www.sourceforge.net/
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
            } 
            else if (current != 0) {
                sBuf.append(current);   
            }
            
            last = current;             
            
        }
        
        return sBuf.toString();
    }

    public String encode(String pString) throws EncoderException {
        return (soundex(pString));   
    }

    public Object encode(Object pObject) throws EncoderException {

        Object result;

        if (!(pObject instanceof java.lang.String)) {
            throw new EncoderException("Parameter supplied to " 
                                       + "RefinedSoundex " 
                                       + "encode is not of type " 
                                       + "java.lang.String"); 
        } 
        else {
            result = soundex((String) pObject);
        }

        return result;

    }


    /**
     * Used internally by the SoundEx algorithm.
     */
    private char getMappingCode(char c) {
        if (!Character.isLetter(c)) {
            return 0;
        } 
        else {
            return soundexMapping[Character.toUpperCase(c) - 'A'];
        }
    }
}
