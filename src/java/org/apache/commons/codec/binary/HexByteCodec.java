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

import java.io.UnsupportedEncodingException;

public class HexByteCodec extends Hex {

    public HexByteCodec() {
        super();
    }

    public HexByteCodec(String charsetName) {
        super(charsetName);
    }

    /**
     * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each byte in order. The
     * returned array will be double the length of the passed array, as it takes two characters to represent any given byte.
     * <p>
     * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by {@link #getCharsetName()}.
     * </p>
     * 
     * @param array
     *            a byte[] to convert to Hex characters
     * @return A byte[] containing the bytes of the hexadecimal characters
     * @throws IllegalStateException
     *             if the charsetName is invalid. This API throws {@link IllegalStateException} instead of
     *             {@link UnsupportedEncodingException} for backward compatibility.
     * @see #encodeHex(byte[])
     */
    public byte[] encode(byte[] array) {
        return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
    }

}