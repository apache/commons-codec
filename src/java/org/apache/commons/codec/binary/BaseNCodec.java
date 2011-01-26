/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.commons.codec.binary;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

public abstract class BaseNCodec implements BinaryEncoder, BinaryDecoder {

    /**
     *  MIME chunk size per RFC 2045 section 6.8.
     * 
     * <p>
     * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
     * equal signs.
     * </p>
     * 
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 6.8</a>
     */
    public static final int MIME_CHUNK_SIZE = 76;

    /**
     * PEM chunk size per RFC 1421 section 4.3.2.4.
     * 
     * <p>
     * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
     * equal signs.
     * </p>
     * 
     * @see <a href="http://tools.ietf.org/html/rfc1421">RFC 1421 section 4.3.2.4</a>
     */
    public static final int PEM_CHUNK_SIZE = 64;

    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    /**
     * Buffer for streaming.
     */
    protected byte[] buffer;

    /**
     * Position where next character should be written in the buffer.
     */
    protected int pos;

    /**
     * Position where next character should be read from the buffer.
     */
    private int readPos;

    /**
     * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
     * and must be thrown away.
     */
    protected boolean eof;

    /**
     * Place holder for the bytes we're dealing with for our based logic. Bitwise operations store and extract the
     * encoding or decoding from this variable.
     */
    protected long x;

    /**
     * Variable tracks how many characters have been written to the current line. Only used when encoding. We use it to
     * make sure each encoded line never goes beyond lineLength (if lineLength > 0).
     */
    protected int currentLinePos;

    /**
     * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding.
     * This variable helps track that.
     */
    protected int modulus;

    protected BaseNCodec(){
    }

    /**
     * Returns true if this object has buffered data for reading.
     * 
     * @return true if there is data still available for reading.
     */
    boolean hasData() {  // package protected for access from I/O streams
        return this.buffer != null;
    }

    /**
     * Returns the amount of buffered data available for reading.
     * 
     * @return The amount of buffered data available for reading.
     */
    int avail() {  // package protected for access from I/O streams
        return buffer != null ? pos - readPos : 0;
    }

    /** Doubles our buffer. */
    protected void resizeBuffer() {
        if (buffer == null) {
            buffer = new byte[DEFAULT_BUFFER_SIZE];
            pos = 0;
            readPos = 0;
        } else {
            byte[] b = new byte[buffer.length * DEFAULT_BUFFER_RESIZE_FACTOR];
            System.arraycopy(buffer, 0, b, 0, buffer.length);
            buffer = b;
        }
    }

    /**
     * Extracts buffered data into the provided byte[] array, starting at position bPos, up to a maximum of bAvail
     * bytes. Returns how many bytes were actually extracted.
     * 
     * @param b
     *            byte[] array to extract the buffered data into.
     * @param bPos
     *            position in byte[] array to start extraction at.
     * @param bAvail
     *            amount of bytes we're allowed to extract. We may extract fewer (if fewer are available).
     * @return The number of bytes successfully extracted into the provided byte[] array.
     */
    int readResults(byte[] b, int bPos, int bAvail) {  // package protected for access from I/O streams
        if (buffer != null) {
            int len = Math.min(avail(), bAvail);
            System.arraycopy(buffer, readPos, b, bPos, len);
            readPos += len;
            if (readPos >= pos) {
                buffer = null;
            }
            return len;
        }
        return eof ? -1 : 0;
    }

    /**
     * Checks if a byte value is whitespace or not.
     * Whitespace is taken to mean: space, tab, CR, LF
     * @param byteToCheck
     *            the byte to check
     * @return true if byte is whitespace, false otherwise
     */
    protected static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case ' ' :
            case '\n' :
            case '\r' :
            case '\t' :
                return true;
            default :
                return false;
        }
    }

    /**
     * Resets this Base32 object to its initial newly constructed state.
     */
    private void reset() {
        buffer = null;
        pos = 0;
        readPos = 0;
        currentLinePos = 0;
        modulus = 0;
        eof = false;
    }
    /**
     * Encodes an Object using the Base32 algorithm. This method is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the supplied object is not of type byte[].
     * 
     * @param pObject
     *            Object to encode
     * @return An object (of type byte[]) containing the Base32 encoded data which corresponds to the byte[] supplied.
     * @throws EncoderException
     *             if the parameter supplied is not of type byte[]
     */
    public Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof byte[])) {
            throw new EncoderException("Parameter supplied to Base32 encode is not a byte[]");
        }
        return encode((byte[]) pObject);
    }
    /**
     * Encodes a byte[] containing binary data, into a String containing characters in the Base32 alphabet.
     *
     * @param pArray
     *            a byte array containing binary data
     * @return A String containing only Base32 character data
     */
    public String encodeToString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }
    /**
     * Decodes an Object using the Base32 algorithm. This method is provided in order to satisfy the requirements of the
     * Decoder interface, and will throw a DecoderException if the supplied object is not of type byte[] or String.
     * 
     * @param pObject
     *            Object to decode
     * @return An object (of type byte[]) containing the binary data which corresponds to the byte[] or String supplied.
     * @throws DecoderException
     *             if the parameter supplied is not of type byte[]
     */
    public Object decode(Object pObject) throws DecoderException {        
        if (pObject instanceof byte[]) {
            return decode((byte[]) pObject);
        } else if (pObject instanceof String) {
            return decode((String) pObject);
        } else {
            throw new DecoderException("Parameter supplied to Base32 decode is not a byte[] or a String");
        }
    }
    /**
     * Decodes a String containing characters in the Base32 alphabet.
     *
     * @param pArray
     *            A String containing Base32 character data
     * @return a byte array containing binary data
     */
    public byte[] decode(String pArray) {
        return decode(StringUtils.getBytesUtf8(pArray));
    }
    /**
     * Decodes a byte[] containing characters in the Base32 alphabet.
     * 
     * @param pArray
     *            A byte array containing Base32 character data
     * @return a byte array containing binary data
     */
    public byte[] decode(byte[] pArray) {
        reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        decode(pArray, 0, pArray.length);
        decode(pArray, 0, -1); // Notify decoder of EOF.
        byte[] result = new byte[pos];
        readResults(result, 0, result.length);
        return result;
    }
    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters in the Base32 alphabet.
     * 
     * @param pArray
     *            a byte array containing binary data
     * @return A byte array containing only Base32 character data
     */
    public byte[] encode(byte[] pArray) {
        reset();        
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        encode(pArray, 0, pArray.length);
        encode(pArray, 0, -1); // Notify encoder of EOF.
        byte[] buf = new byte[pos - readPos];
        readResults(buf, 0, buf.length);
        return buf;
    }
    
    abstract void encode(byte[] pArray, int i, int length);  // package protected for access from I/O streams

    abstract void decode(byte[] pArray, int i, int length); // package protected for access from I/O streams
}
