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
 * Encodes a string into a Soundex value. Soundex is an encoding used to relate similar names, but can also be used as a
 * general purpose scheme to find word with similar phonemes.
 * 
 * @author Apache Software Foundation
 * @version $Id: Soundex.java,v 1.26 2004/07/07 23:15:24 ggregory Exp $
 */
public class Soundex implements StringEncoder {

    /**
     * An instance of Soundex using the US_ENGLISH_MAPPING mapping.
     * 
     * @see #US_ENGLISH_MAPPING
     */
    public static final Soundex US_ENGLISH = new Soundex();

    /**
     * This is a default mapping of the 26 letters used in US English. A value of <code>0</code> for a letter position
     * means do not encode.
     * <p>
     * (This constant is provided as both an implementation convenience and to allow Javadoc to pick
     * up the value for the constant values page.)
     * </p>
     * 
     * @see #US_ENGLISH_MAPPING
     */
    public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";

    /**
     * This is a default mapping of the 26 letters used in US English. A value of <code>0</code> for a letter position
     * means do not encode.
     * 
     * @see Soundex#Soundex(char[])
     */
    public static final char[] US_ENGLISH_MAPPING = US_ENGLISH_MAPPING_STRING.toCharArray();

    /**
     * Encodes the Strings and returns the number of characters in the two encoded Strings that are the same. This
     * return value ranges from 0 through 4: 0 indicates little or no similarity, and 4 indicates strong similarity or
     * identical values.
     * 
     * @param s1
     *                  A String that will be encoded and compared.
     * @param s2
     *                  A String that will be encoded and compared.
     * @return The number of characters in the two encoded Strings that are the same from 0 to 4.
     * 
     * @see SoundexUtils#difference(StringEncoder,String,String)
     * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp"> MS
     *          T-SQL DIFFERENCE </a>
     * 
     * @throws EncoderException
     *                  if an error occurs encoding one of the strings
     * @since 1.3
     */
    public int difference(String s1, String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }

    /**
     * The maximum length of a Soundex code - Soundex codes are only four characters by definition.
     * 
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     */
    private int maxLength = 4;

    /**
     * Every letter of the alphabet is "mapped" to a numerical value. This char array holds the values to which each
     * letter is mapped. This implementation contains a default map for US_ENGLISH
     */
    private char[] soundexMapping;

    /**
     * Creates an instance using US_ENGLISH_MAPPING
     * 
     * @see Soundex#Soundex(char[])
     * @see Soundex#US_ENGLISH_MAPPING
     */
    public Soundex() {
        this(US_ENGLISH_MAPPING);
    }

    /**
     * Creates a soundex instance using the given mapping. This constructor can be used to provide an internationalized
     * mapping for a non-Western character set.
     * 
     * Every letter of the alphabet is "mapped" to a numerical value. This char array holds the values to which each
     * letter is mapped. This implementation contains a default map for US_ENGLISH
     * 
     * @param mapping
     *                  Mapping array to use when finding the corresponding code for a given character
     */
    public Soundex(char[] mapping) {
        this.setSoundexMapping(mapping);
    }

    /**
     * Encodes an Object using the soundex algorithm. This method is provided in order to satisfy the requirements of
     * the Encoder interface, and will throw an EncoderException if the supplied object is not of type java.lang.String.
     * 
     * @param pObject
     *                  Object to encode
     * @return An object (or type java.lang.String) containing the soundex code which corresponds to the String
     *             supplied.
     * @throws EncoderException
     *                  if the parameter supplied is not of type java.lang.String
     * @throws IllegalArgumentException
     *                  if a character is not mapped
     */
    public Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        }
        return soundex((String) pObject);
    }

    /**
     * Encodes a String using the soundex algorithm.
     * 
     * @param pString
     *                  A String object to encode
     * @return A Soundex code corresponding to the String supplied
     * @throws IllegalArgumentException
     *                  if a character is not mapped
     */
    public String encode(String pString) {
        return soundex(pString);
    }

    /**
     * Used internally by the SoundEx algorithm.
     * 
     * Consonants from the same code group separated by W or H are treated as one.
     * 
     * @param str
     *                  the cleaned working string to encode (in upper case).
     * @param index
     *                  the character position to encode
     * @return Mapping code for a particular character
     * @throws IllegalArgumentException
     *                  if the character is not mapped
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
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     * @return int
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Returns the soundex mapping.
     * 
     * @return soundexMapping.
     */
    private char[] getSoundexMapping() {
        return this.soundexMapping;
    }

    /**
     * Maps the given upper-case character to it's Soudex code.
     * 
     * @param ch
     *                  An upper-case character.
     * @return A Soundex code.
     * @throws IllegalArgumentException
     *                  Thrown if <code>ch</code> is not mapped.
     */
    private char map(char ch) {
        int index = ch - 'A';
        if (index < 0 || index >= this.getSoundexMapping().length) {
            throw new IllegalArgumentException("The character is not mapped: " + ch);
        }
        return this.getSoundexMapping()[index];
    }

    /**
     * Sets the maxLength.
     * 
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     * @param maxLength
     *                  The maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sets the soundexMapping.
     * 
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
     * @throws IllegalArgumentException
     *                  if a character is not mapped
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