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

package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Utility methods for {@link Soundex} and {@link RefinedSoundex} classes.
 * 
 * @author Gary D. Gregory
 * @version $Id: SoundexUtils.java,v 1.1 2003/12/11 23:44:11 ggregory Exp $
 */
final class SoundexUtils {

    /**
	 * Cleans up the input string before Soundex processing by only returning
	 * upper case letters.
	 * 
	 * @param str
	 *                  The String to clean.
	 * @return A clean String.
	 */
    static String clean(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        int len = str.length();
        char[] chars = new char[len];
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (Character.isLetter(str.charAt(i))) {
                chars[count++] = str.charAt(i);
            }
        }
        if (count == len) {
            return str.toUpperCase();
        }
        return new String(chars, 0, count).toUpperCase();
    }

    /**
	 * Encodes the Strings and returns the number of characters in the two
	 * encoded Strings that are the same.
	 * <ul>
	 * <li>For Soundex, this return value ranges from 0 through 4: 0 indicates
	 * little or no similarity, and 4 indicates strong similarity or identical
	 * values.</li>
	 * <li>For refined Soundex, the return value can be greater than 4.</li>
	 * </ul>
	 * 
	 * @param encoder
	 *                  The encoder to use to encode the Strings.
	 * @param s1
	 *                  A String that will be encoded and compared.
	 * @param s2
	 *                  A String that will be encoded and compared.
	 * @return The number of characters in the two Soundex encoded Strings that
	 *             are the same.
	 * 
	 * @see #differenceEncoded(String,String)
	 * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp">
	 *          MS T-SQL DIFFERENCE</a>
	 * 
	 * @throws EncoderException
	 *                  if an error occurs encoding one of the strings
	 */
    static int difference(StringEncoder encoder, String s1, String s2) throws EncoderException {
        return differenceEncoded(encoder.encode(s1), encoder.encode(s2));
    }

    /**
	 * Returns the number of characters in the two Soundex encoded Strings that
	 * are the same.
	 * <ul>
	 * <li>For Soundex, this return value ranges from 0 through 4: 0 indicates
	 * little or no similarity, and 4 indicates strong similarity or identical
	 * values.</li>
	 * <li>For refined Soundex, the return value can be greater than 4.</li>
	 * </ul>
	 * 
	 * @param es1
	 *                  An encoded String.
	 * @param es2
	 *                  An encoded String.
	 * @return The number of characters in the two Soundex encoded Strings that
	 *             are the same.
	 * 
	 * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/tsqlref/ts_de-dz_8co5.asp">
	 *          MS T-SQL DIFFERENCE</a>
	 */
    static int differenceEncoded(String es1, String es2) {

        if (es1 == null || es2 == null) {
            return 0;
        }
        int lengthToMatch = Math.min(es1.length(), es2.length());
        int diff = 0;
        for (int i = 0; i < lengthToMatch; i++) {
            if (es1.charAt(i) == es2.charAt(i)) {
                diff++;
            }
        }
        return diff;
    }

}