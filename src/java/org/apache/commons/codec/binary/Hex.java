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
 * @version $Id: Hex.java,v 1.11 2004/02/23 07:32:50 ggregory Exp $
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

