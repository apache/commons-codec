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

package org.apache.commons.codec.net;

import org.apache.commons.codec.DecoderException;

/**
 * Utility methods for this package.
 *
 * <p>This class is immutable and thread-safe.</p>
 *
 * @since 1.4
 */
class Utils {

    /**
     * Radix used in encoding and decoding.
     */
    private static final int RADIX = 16;

    /**
     * Returns the numeric value of the character {@code b} in radix 16.
     *
     * @param b
     *            The byte to be converted.
     * @return The numeric value represented by the character in radix 16.
     *
     * @throws DecoderException
     *             Thrown when the byte is not valid per {@link Character#digit(char,int)}
     */
    static int digit16(final byte b) throws DecoderException {
        final int i = Character.digit((char) b, RADIX);
        if (i == -1) {
            throw new DecoderException("Invalid URL encoding: not a valid digit (radix " + RADIX + "): " + b);
        }
        return i;
    }

    /**
     * Returns the upper case hex digit of the lower 4 bits of the int.
     *
     * @param b the input int
     * @return the upper case hex digit of the lower 4 bits of the int.
     */
    static char hexDigit(final int b) {
        return Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
    }

}
