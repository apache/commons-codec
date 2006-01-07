/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
 * Provides Base64 encoding and decoding as defined by RFC 2045.
 * 
 * <p>This class is deprecated, but the static interface has been preserved
 * as a convenience.  Please use the Base64 class in the base subpackage.</p> 
 *
 * @deprecated 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class Base64 implements BinaryEncoder, BinaryDecoder {

    /**
     * Tests a given byte array to see if it contains
     * only valid characters within the Base64 alphabet.
     *
     * @deprecated
     * @param arrayOctect byte array to test
     * @return <code>true</code> if all bytes are valid characters in the Base64
     *         alphabet or if the byte array is empty; false, otherwise
     */
    public static boolean isArrayByteBase64(byte[] arrayOctect) {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.isArrayByteBase64( arrayOctect );
    }

    /**
     * Encodes binary data using the base64 algorithm but
     * does not chunk the output.
     *
     * @deprecated
     * @param binaryData binary data to encode
     * @return Base64 characters
     */
    public static byte[] encodeBase64(byte[] binaryData) {
        return org.apache.commons.codec.binary.base.Base64.DEFAULT.encodeBase64(binaryData, false);
    }

    /**
     * Encodes binary data using the base64 algorithm and chunks
     * the encoded output into 76 character blocks
     *
     * @deprecated
     * @param binaryData binary data to encode
     * @return Base64 characters chunked in 76 character blocks
     */
    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return org.apache.commons.codec.binary.base.Base64.DEFAULT.encodeBase64(binaryData, true);
    }


    /**
     * Decodes an Object using the base64 algorithm.  This method
     * is provided in order to satisfy the requirements of the
     * Decoder interface, and will throw a DecoderException if the
     * supplied object is not of type byte[].
     *
     * @deprecated
     * @param pObject Object to decode
     * @return An object (of type byte[]) containing the 
     *         binary data which corresponds to the byte[] supplied.
     * @throws DecoderException if the parameter supplied is not
     *                          of type byte[]
     */
    public Object decode(Object pObject) throws DecoderException {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.decode(pObject);
    }

    /**
     * Decodes a byte[] containing containing
     * characters in the Base64 alphabet.
     *
     * @deprecated
     * @param pArray A byte array containing Base64 character data
     * @return a byte array containing binary data
     */
    public byte[] decode(byte[] pArray) {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.decodeBase64(pArray);
    }

    /**
     * Encodes binary data using the base64 algorithm, optionally
     * chunking the output into 76 character blocks.
     *
     * @deprecated
     * @param binaryData Array containing binary data to encode.
     * @param isChunked if <code>true</code> this encoder will chunk
     *                  the base64 output into 76 character blocks
     * @return Base64-encoded data.
     */
    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.encodeBase64(binaryData,isChunked);
    }

    /**
     * Decodes Base64 data into octects
     *
     * @deprecated
     * @param base64Data Byte array containing Base64 data
     * @return Array containing decoded data.
     */
    public static byte[] decodeBase64(byte[] base64Data) {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.decodeBase64(base64Data);
    }
    
    // Implementation of the Encoder Interface

    /**
     * Encodes an Object using the base64 algorithm.  This method
     * is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the
     * supplied object is not of type byte[].
     *
     * @param pObject Object to encode
     * @return An object (of type byte[]) containing the 
     *         base64 encoded data which corresponds to the byte[] supplied.
     * @throws EncoderException if the parameter supplied is not
     *                          of type byte[]
     */
    public Object encode(Object pObject) throws EncoderException {
    	return org.apache.commons.codec.binary.base.Base64.DEFAULT.encode(pObject);
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing
     * characters in the Base64 alphabet.
     *
     * @param pArray a byte array containing binary data
     * @return A byte array containing only Base64 character data
     */
    public byte[] encode(byte[] pArray) {
        return org.apache.commons.codec.binary.base.Base64.DEFAULT.encodeBase64(pArray, false);
    }

}
