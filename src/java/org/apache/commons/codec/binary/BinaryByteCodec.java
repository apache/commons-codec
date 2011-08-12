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

import org.apache.commons.codec.BinaryEncoder;

public class BinaryByteCodec extends BinaryCodec implements BinaryEncoder {

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 characters.
     * 
     * @param raw
     *            the raw binary data to convert
     * @return 0 and 1 ASCII character bytes one for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public byte[] encode(byte[] raw) {
        return toAsciiBytes(raw);
    }

}
