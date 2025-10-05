/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

class Binary {

    private static final int RADIX = 2;

    /**
     * Converts a string of 0s and 1s to a byte array.
     *
     * @param binary a string of 0s and 1s to a byte array.
     * @return a byte array
     */
    static byte[] toByteArray(final String binary) {
        // Ensure the binary string length is a multiple of 8
        final int inLen = binary.length();
        if (inLen % Byte.SIZE != 0) {
            throw new IllegalArgumentException(String.format("Binary string length must be a multiple of %,d.", Byte.SIZE));
        }
        final byte[] byteArray = new byte[inLen / Byte.SIZE];
        for (int i = 0; i < byteArray.length; i += Byte.SIZE) {
            byteArray[i] = (byte) Integer.parseInt(binary.substring(i, i + Byte.SIZE), RADIX);
        }
        return byteArray;
    }
}
