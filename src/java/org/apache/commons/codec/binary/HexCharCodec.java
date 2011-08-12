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
package org.apache.commons.codec.binary;

import org.apache.commons.codec.Encoder;

public class HexCharCodec extends Hex implements Encoder<byte[], char[]> {

    public HexCharCodec() {
        super();
    }

    public HexCharCodec(String charsetName) {
        super(charsetName);
    }

    /**
     * Converts a String or an array of bytes into an array of characters representing the hexadecimal values of each byte in order. The
     * returned array will be double the length of the passed String or array, as it takes two characters to represent any given byte.
     * <p>
     * The conversion from hexadecimal characters to bytes to be encoded to performed with the charset named by {@link #getCharsetName()}.
     * </p>
     * 
     * @param byteArray
     *            a String, or byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     * @see #encodeHex(byte[])
     */
    public char[] encode(byte[] byteArray) {
        return encodeHex(byteArray);
    }

}