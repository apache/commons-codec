/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
 * <p>This class is thread-safe.
 * Although not strictly immutable, the mutable fields are not actually used.</p>
 */
public class Soundex implements StringEncoder {

    /**
     * The marker character used to indicate a silent (ignored) character.
     * These are ignored except when they appear as the first character.
     * <p>
     * Note: the {@link #US_ENGLISH_MAPPING_STRING} does not use this mechanism
     * because changing it might break existing code. Mappings that don't contain
     * a silent marker code are treated as though H and W are silent.
     * <p>
     * To override this, use the {@link #Soundex(String, boolean)} constructor.
     * @since 1.11
     */
    public static final char SILENT_MARKER = '-';

    /**
     * This is a default mapping of the 26 letters used in US English. A value of {@code 0} for a letter position
     * means do not encode, but treat as a separator when it occurs between consonants with the same code.
     * <p>
     * (This constant is provided as both an implementation convenience and to allow Javadoc to pick
     * up the value for the constant values page.)
     * <p>
     * <b>Note that letters H and W are treated specially.</b>
     * They are ignored (after the first letter) and don't act as separators
     * between consonants with the same code.
     */
    //                                                      ABCDEFGHIJKLMNOPQRSTUVWXYZ
    public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";

    /**
     * This is a default mapping of the 26 letters used in US English. A value of {@code 0} for a letter position
     * means do not encode.
     *
     * @see Soundex#Soundex(char[])
     */
    private static final char[] US_ENGLISH_MAPPING = US_ENGLISH_MAPPING_STRING.toCharArray();

    /**
     * An instance of Soundex using the US_ENGLISH_MAPPING mapping.
     * This treats H and W as silent letters.
     * Apart from when they appear as the first letter, they are ignored.
     * They don't act as separators between duplicate codes.
     *
     * @see #US_ENGLISH_MAPPING_STRING
     */
    public static final Soundex US_ENGLISH = new Soundex();

    /**
     * An instance of Soundex using the Simplified Soundex mapping, as described here:
     * http://west-penwith.org.uk/misc/soundex.htm
     * <p>
     * This treats H and W the same as vowels (AEIOUY).
     * Such letters aren't encoded (after the first), but they do
     * act as separators when dropping duplicate codes.
     * The mapping is otherwise the same as for {@link #US_ENGLISH}
     *
     * @since 1.11
     */
    public static final Soundex US_ENGLISH_SIMPLIFIED = new Soundex(US_ENGLISH_MAPPING_STRING, false);

    /**
     * An instance of Soundex using the mapping as per the Genealogy site:
     * http://www.genealogy.com/articles/research/00000060.html
     * <p>
     * This treats vowels (AEIOUY), H and W as silent letters.
     * Such letters are ignored (after the first) and do not
     * act as separators when dropping duplicate codes.
     * <p>
     * The codes for consonants are otherwise the same as for
     * {@link #US_ENGLISH_MAPPING_STRING} and {@link #US_ENGLISH_SIMPLIFIED}
     *
     * @since 1.11
     */
    public static final Soundex US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");
    //                                                              ABCDEFGHIJKLMNOPQRSTUVWXYZ

    /**
     * The maximum length of a Soundex code - Soundex codes are only four characters by definition.
     *
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     */
    @Deprecated
    private int maxLength = 4;

    /**
     * Every letter of the alphabet is "mapped" to a numerical value. This char array holds the values to which each
     * letter is mapped. This implementation contains a default map for US_ENGLISH
     */
    private final char[] soundexMapping;

    /**
     * Should H and W be treated specially?
     * <p>
     * In versions of the code prior to 1.11,
     * the code always treated H and W as silent (ignored) letters.
     * If this field is false, H and W are no longer special-cased.
     */
    private final boolean specialCaseHW;

    /**
     * Creates an instance using US_ENGLISH_MAPPING
     *
     * @see Soundex#Soundex(char[])
     * @see Soundex#US_ENGLISH_MAPPING_STRING
     */
    public Soundex() {
        this.soundexMapping = US_ENGLISH_MAPPING;
        this.specialCaseHW = true;
    }

    /**
     * Creates a soundex instance using the given mapping. This constructor can be used to provide an internationalized
     * mapping for a non-Western character set.
     *
     * Every letter of the alphabet is "mapped" to a numerical value. This char array holds the values to which each
     * letter is mapped. This implementation contains a default map for US_ENGLISH
     * <p>
     * If the mapping contains an instance of {@link #SILENT_MARKER} then H and W are not given special treatment
     *
     * @param mapping
     *                  Mapping array to use when finding the corresponding code for a given character
     */
    public Soundex(final char[] mapping) {
        this.soundexMapping = mapping.clone();
        this.specialCaseHW = !hasMarker(this.soundexMapping);
    }

    private boolean hasMarker(final char[] mapping) {
        for(final char ch : mapping) {
            if (ch == SILENT_MARKER) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a refined soundex instance using a custom mapping. This constructor can be used to customize the mapping,
     * and/or possibly provide an internationalized mapping for a non-Western character set.
     * <p>
     * If the mapping contains an instance of {@link #SILENT_MARKER} then H and W are not given special treatment
     *
     * @param mapping
     *            Mapping string to use when finding the corresponding code for a given character
     * @since 1.4
     */
    public Soundex(final String mapping) {
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = !hasMarker(this.soundexMapping);
    }

    /**
     * Creates a refined soundex instance using a custom mapping. This constructor can be used to customize the mapping,
     * and/or possibly provide an internationalized mapping for a non-Western character set.
     *
     * @param mapping
     *            Mapping string to use when finding the corresponding code for a given character
     * @param specialCaseHW if true, then
     * @since 1.11
     */
    public Soundex(final String mapping, final boolean specialCaseHW) {
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = specialCaseHW;
    }

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
    public int difference(final String s1, final String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }

    /**
     * Encodes an Object using the soundex algorithm. This method is provided in order to satisfy the requirements of
     * the Encoder interface, and will throw an EncoderException if the supplied object is not of type java.lang.String.
     *
     * @param obj
     *                  Object to encode
     * @return An object (or type java.lang.String) containing the soundex code which corresponds to the String
     *             supplied.
     * @throws EncoderException
     *                  if the parameter supplied is not of type java.lang.String
     * @throws IllegalArgumentException
     *                  if a character is not mapped
     */
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        }
        return soundex((String) obj);
    }

    /**
     * Encodes a String using the soundex algorithm.
     *
     * @param str
     *                  A String object to encode
     * @return A Soundex code corresponding to the String supplied
     * @throws IllegalArgumentException
     *                  if a character is not mapped
     */
    @Override
    public String encode(final String str) {
        return soundex(str);
    }

    /**
     * Returns the maxLength. Standard Soundex
     *
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     * @return int
     */
    @Deprecated
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Maps the given upper-case character to its Soundex code.
     *
     * @param ch
     *                  An upper-case character.
     * @return A Soundex code.
     * @throws IllegalArgumentException
     *                  Thrown if {@code ch} is not mapped.
     */
    private char map(final char ch) {
        final int index = ch - 'A';
        if (index < 0 || index >= this.soundexMapping.length) {
            throw new IllegalArgumentException("The character is not mapped: " + ch + " (index=" + index + ")");
        }
        return this.soundexMapping[index];
    }

    /**
     * Sets the maxLength.
     *
     * @deprecated This feature is not needed since the encoding size must be constant. Will be removed in 2.0.
     * @param maxLength
     *                  The maxLength to set
     */
    @Deprecated
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Retrieves the Soundex code for a given String object.
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
        if (str.isEmpty()) {
            return str;
        }
        final char[] out = {'0', '0', '0', '0'};
        int count = 0;
        final char first = str.charAt(0);
        out[count++] = first;
        char lastDigit = map(first); // previous digit
        for(int i = 1; i < str.length() && count < out.length ; i++) {
            final char ch = str.charAt(i);
            if ((this.specialCaseHW) && (ch == 'H' || ch == 'W')) { // these are ignored completely
                continue;
            }
            final char digit = map(ch);
            if (digit == SILENT_MARKER) {
                continue;
            }
            if (digit != '0' && digit != lastDigit) { // don't store vowels or repeats
                out[count++] = digit;
            }
            lastDigit = digit;
        }
        return new String(out);
    }

}
