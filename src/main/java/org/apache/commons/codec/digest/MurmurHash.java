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

/**
 * Commons implementation methods for MurmurHash* classes in this package.
 */
final class MurmurHash {

    /**
     * Gets the little-endian int from 4 bytes starting at the specified index.
     *
     * @param data  The data.
     * @param index The index.
     * @return The little-endian int.
     */
    static int getLittleEndianInt(final byte[] data, final int index) {
        // @formatter:off
        return data[index    ] & 0xff |
               (data[index + 1] & 0xff) <<  8 |
               (data[index + 2] & 0xff) << 16 |
               (data[index + 3] & 0xff) << 24;
        // @formatter:on
    }

    /**
     * Gets the little-endian long from 8 bytes starting at the specified index.
     *
     * @param data  The data.
     * @param index The index.
     * @return The little-endian long.
     */
    static long getLittleEndianLong(final byte[] data, final int index) {
        // @formatter:off
        return (long) data[index    ] & 0xff |
               ((long) data[index + 1] & 0xff) <<  8 |
               ((long) data[index + 2] & 0xff) << 16 |
               ((long) data[index + 3] & 0xff) << 24 |
               ((long) data[index + 4] & 0xff) << 32 |
               ((long) data[index + 5] & 0xff) << 40 |
               ((long) data[index + 6] & 0xff) << 48 |
               ((long) data[index + 7] & 0xff) << 56;
        // @formatter:on
    }
}
