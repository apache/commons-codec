/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a soundex value. Soundex is an encoding used to relate
 * similar names, but can also be used as a general purpose scheme to find word
 * with similar phonemes.
 * 
 * @author Apache Software Foundation
 * @version $Id: Soundex.java,v 1.20 2004/02/29 04:08:31 tobrien Exp $
 */
public class Soundex implements StringEncoder {

    /**
	 * This static variable contains an instance of the Soundex using the
	 * US_ENGLISH mapping.
	 */
    public static final Soundex US_ENGLISH = new Soundex();

    /**
	 * This is a default mapping of the 26 letters used in US english. A value
	 * of <code>0</code> for a letter position means do not encode.
	 */
    public static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();

    /**
	 * Encodes the Strings and returns the number of characters in the two
	 * encoded Strings that are the same. This return value ranges from 0
	 * through 4: 0 indicates little or no similarity, and 4 indicates strong
	 * similarity or identical values.
	 * 
	 * @param s1
	 *                  A String that will be encoded and compared.
	 * @param s2
	 *                  A String that will be encoded and compared.
	 * @return The number of characters in the two encoded Strings that are the
	 *             same from 0 to 4.
	 * 
	 * @see SoundexUtils#difference(StringEncoder,String,String)
	 * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp">
	 *          MS T-SQL DIFFERENCE</a>
	 * 
	 * @throws EncoderException
	 *                  if an error occurs encoding one of the strings
	 */
    public int difference(String s1, String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }

    /**
	 * The maximum length of a Soundex code - Soundex codes are only four
	 * characters by definition.
	 * 
	 * @deprecated This feature is not needed since the encoding size must be
	 *                     constant.
	 */
    private int maxLength = 4;

    /**
	 * Every letter of the alphabet is "mapped" to a numerical value. This char
	 * array holds the values to which each letter is mapped. This
	 * implementation contains a default map for US_ENGLISH
	 */
    private char[] soundexMapping;

    /**
	 * Creates an instance of the Soundex object using the default US_ENGLISH
	 * mapping.
	 */
    public Soundex() {
        this(US_ENGLISH_MAPPING);
    }

    /**
	 * Creates a soundex instance using a custom mapping. This constructor can
	 * be used to customize the mapping, and/or possibly provide an
	 * internationalized mapping for a non-Western character set.
	 * 
	 * @param mapping
	 *                  Mapping array to use when finding the corresponding code for
	 *                  a given character
	 */
    public Soundex(char[] mapping) {
        this.setSoundexMapping(mapping);
    }

    /**
	 * Encodes an Object using the soundex algorithm. This method is provided
	 * in order to satisfy the requirements of the Encoder interface, and will
	 * throw an EncoderException if the supplied object is not of type
	 * java.lang.String.
	 * 
	 * @param pObject
	 *                  Object to encode
	 * @return An object (or type java.lang.String) containing the soundex code
	 *             which corresponds to the String supplied.
	 * @throws EncoderException
	 *                  if the parameter supplied is not of type java.lang.String
	 */
    public Object encode(Object pObject) throws EncoderException {

        Object result;

        if (!(pObject instanceof java.lang.String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        } else {
            result = soundex((String) pObject);
        }

        return result;

    }

    /**
	 * Encodes a String using the soundex algorithm.
	 * 
	 * @param pString
	 *                  A String object to encode
	 * @return A Soundex code corresponding to the String supplied
	 */
    public String encode(String pString) {
        return soundex(pString);
    }

    /**
	 * Used internally by the SoundEx algorithm.
	 * 
	 * Consonants from the same code group separated by W or H are treated as
	 * one.
	 * 
	 * @param str
	 *                  the cleaned working string to encode (in upper case).
	 * @param index
	 *                  the character position to encode
	 * @return Mapping code for a particular character
	 */
    private char getMappingCode(String str, int index) {
        char mappedChar = this.map(str.charAt(index));
        // HW rule check
        if (index > 1 && mappedChar != '0') {
            char hwChar = str.charAt(index - 1);
            if ('H' == hwChar || 'W' == hwChar) {
                char preHWChar = str.charAt(index - 2);
                char firstCode = this.map(preHWChar);
                if (firstCode == mappedChar || 'H' == preHWChar || 'W' == preHWChar) {
                    return 0;
                }
            }
        }
        return mappedChar;
    }

    /**
	 * Returns the maxLength. Standard Soundex
	 * 
	 * @deprecated This feature is not needed since the encoding size must be
	 *                     constant.
	 * @return int
	 */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Returns the soundex mapping.
	 * @return soundexMapping.
	 */
    private char[] getSoundexMapping() {
        return this.soundexMapping;
    }

    /**
	 * Maps the given upper-case character to it's Soudex code.
	 * 
	 * @param c
	 *                  An upper-case character.
	 * @return A Soundex code.
	 */
    private char map(char c) {
        return this.getSoundexMapping()[c - 'A'];
    }

    /**
	 * Sets the maxLength.
	 * 
	 * @deprecated This feature is not needed since the encoding size must be
	 *                     constant.
	 * @param maxLength
	 *                  The maxLength to set
	 */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sets the soundexMapping.
	 * @param soundexMapping
	 *                  The soundexMapping to set.
	 */
    private void setSoundexMapping(char[] soundexMapping) {
        this.soundexMapping = soundexMapping;
    }

    /**
	 * Retreives the Soundex code for a given String object.
	 * 
	 * @param str
	 *                  String to encode using the Soundex algorithm
	 * @return A soundex code for the String supplied
	 */
    public String soundex(String str) {
        if (str == null) {
            return null;
        }
        str = SoundexUtils.clean(str);
        if (str.length() == 0) {
            return str;
        }
        char out[] = {'0', '0', '0', '0'};
        char last, mapped;
        int incount = 1, count = 1;
        out[0] = str.charAt(0);
        last = getMappingCode(str, 0);
        while ((incount < str.length()) && (count < out.length)) {
            mapped = getMappingCode(str, incount++);
            if (mapped != 0) {
                if ((mapped != '0') && (mapped != last)) {
                    out[count++] = mapped;
                }
                last = mapped;
            }
        }
        return new String(out);
    }

}
