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

package org.apache.commons.codec.binary;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Hex encoder and decoder.
 * 
 * @author <a href="mailto:siege@preoccupied.net">Christopher O'Brien</a>
 * @author Tim O'Brien
 * @author Gary Gregory
 * @version $Id: Hex.java,v 1.9 2003/11/03 19:03:17 ggregory Exp $
 */
public class Hex implements BinaryEncoder, BinaryDecoder {

    /** 
     * Used building output as Hex 
     */
    private static char[] digits = {
        '0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Converts an array of characters representing hexidecimal values into an
     * array of bytes of those same values. The returned array will be half the
     * length of the passed array, as it takes two characters to represent any
     * given byte. An exception is thrown if the passed char array has an odd
     * number of elements.
     * 
     * @param data An array of characters containing hexidecimal digits
     * @return A byte array containing binary data decoded from
     *         the supplied char array.
     * @throws DecoderException Thrown if an odd number of characters is supplied
     *                   to this function
     */
    public static byte[] decodeHex(char[] data) throws DecoderException {

        int l = data.length;

           if ((l & 0x01) != 0) {
               throw new DecoderException("Odd number of characters.");
           }

           byte[] out = new byte[l >> 1];

           // two characters form the hex value.
           for (int i = 0, j = 0; j < l; i++) {
               int f = Character.digit(data[j++], 16) << 4;
               f = f | Character.digit(data[j++], 16);
               out[i] = (byte) (f & 0xFF);
           }

           return out;
    }

    /**
     * Converts an array of bytes into an array of characters representing the
     * hexidecimal values of each byte in order. The returned array will be
     * double the length of the passed array, as it takes two characters to
     * represent any given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexidecimal characters
     */
    public static char[] encodeHex(byte[] data) {

        int l = data.length;

           char[] out = new char[l << 1];

           // two characters form the hex value.
           for (int i = 0, j = 0; i < l; i++) {
               out[j++] = digits[(0xF0 & data[i]) >>> 4 ];
               out[j++] = digits[ 0x0F & data[i] ];
           }

           return out;
    }
	
    /**
     * Converts an array of character bytes representing hexidecimal values into an
     * array of bytes of those same values. The returned array will be half the
     * length of the passed array, as it takes two characters to represent any
     * given byte. An exception is thrown if the passed char array has an odd
     * number of elements.
     * 
     * @param array An array of character bytes containing hexidecimal digits
     * @return A byte array containing binary data decoded from
     *         the supplied byte array (representing characters).
     * @throws DecoderException Thrown if an odd number of characters is supplied
     *                   to this function
     * @see #decodeHex(char[])
     */
	public byte[] decode(byte[] array) throws DecoderException {
		return decodeHex(new String(array).toCharArray());
	}
	
    /**
     * Converts a String or an array of character bytes representing hexidecimal values into an
     * array of bytes of those same values. The returned array will be half the
     * length of the passed String or array, as it takes two characters to represent any
     * given byte. An exception is thrown if the passed char array has an odd
     * number of elements.
     * 
     * @param object A String or, an array of character bytes containing hexidecimal digits
     * @return A byte array containing binary data decoded from
     *         the supplied byte array (representing characters).
     * @throws DecoderException Thrown if an odd number of characters is supplied
     *                   to this function or the object is not a String or char[]
     * @see #decodeHex(char[])
     */
	public Object decode(Object object) throws DecoderException {
		try {
            char[] charArray = object instanceof String ? ((String) object).toCharArray() : (char[]) object;
		    return decodeHex(charArray);
		} catch (ClassCastException e) {
		    throw new DecoderException(e.getMessage());
		}
	}
	
    /**
     * Converts an array of bytes into an array of bytes for the characters representing the
     * hexidecimal values of each byte in order. The returned array will be
     * double the length of the passed array, as it takes two characters to
     * represent any given byte.
     *
     * @param array a byte[] to convert to Hex characters
     * @return A byte[] containing the bytes of the hexidecimal characters
     * @see #encodeHex(byte[])
     */
	public byte[] encode(byte[] array) {
		return new String(encodeHex(array)).getBytes();
	}

    /**
     * Converts a String or an array of bytes into an array of characters representing the
     * hexidecimal values of each byte in order. The returned array will be
     * double the length of the passed String or array, as it takes two characters to
     * represent any given byte.
     *
     * @param object a String, or byte[] to convert to Hex characters
     * @return A char[] containing hexidecimal characters
     * @throws EncoderException Thrown if the given object is not a String or byte[]
     * @see #encodeHex(byte[])
     */
	public Object encode(Object object) throws EncoderException {	
		try {
            byte[] byteArray = object instanceof String ? ((String) object).getBytes() : (byte[]) object;
			return encodeHex(byteArray);
		} catch (ClassCastException e) {
			throw new EncoderException(e.getMessage());
		}
	}

}

