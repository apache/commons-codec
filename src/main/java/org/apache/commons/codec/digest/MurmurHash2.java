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

package org.apache.commons.codec.digest;

/**
 * Implementation of the MurmurHash2 32-bit and 64-bit hash functions.
 *
 * <p>MurmurHash is a non-cryptographic hash function suitable for general
 * hash-based lookup. The name comes from two basic operations, multiply (MU)
 * and rotate (R), used in its inner loop. Unlike cryptographic hash functions,
 * it is not specifically designed to be difficult to reverse by an adversary,
 * making it unsuitable for cryptographic purposes.</p>
 *
 * <p>This contains a Java port of the 32-bit hash function {@code MurmurHash2}
 * and the 64-bit hash function {@code MurmurHash64A} from Austin Applyby's
 * original {@code c++} code in SMHasher.</p>
 *
 * <p>This is a re-implementation of the original C code plus some additional
 * features.</p>
 *
 * <p>This is public domain code with no copyrights. From homepage of
 * <a href="https://github.com/aappleby/smhasher">SMHasher</a>:</p>
 *
 * <blockquote>
 * "All MurmurHash versions are public domain software, and the author
 * disclaims all copyright to their code."
 * </blockquote>
 *
 * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
 * @see <a href="https://github.com/aappleby/smhasher/blob/master/src/MurmurHash2.cpp">
 *   Original MurmurHash2 c++ code</a>
 * @since 1.13
 */
public final class MurmurHash2 {

    /** No instance methods. */
    private MurmurHash2() {
    }

    /**
     * Generates 32 bit hash from byte array with the given length and seed.
     *
     * @param data   byte array to hash
     * @param length length of the array to hash
     * @param seed   initial seed value
     * @return 32 bit hash of the given array
     */
    public static int hash32(final byte[] data, final int length, final int seed) {
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        final int m = 0x5bd1e995;
        final int r = 24;

        // Initialize the hash to a random value
        int h = seed ^ length;

        // Mix 4 bytes at a time into the hash
        final int nblocks = length >> 2;

        // body
        for (int i = 0; i < nblocks; i++) {
            final int index = (i << 2);
            int k = getLittleEndianInt(data, index);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // Handle the last few bytes of the input array
        final int index = (nblocks << 2);
        switch (length - index) {
        case 3:
            h ^= (data[index + 2] & 0xff) << 16;
        case 2:
            h ^= (data[index + 1] & 0xff) << 8;
        case 1:
            h ^= (data[index] & 0xff);
            h *= m;
        }

        // Do a few final mixes of the hash to ensure the last few
        // bytes are well-incorporated.
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    /**
     * Generates 32 bit hash from byte array with the given length and a default seed value.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0x9747b28c;
     * int hash = MurmurHash2.hash32(data, offset, length, seed);
     * </pre>
     *
     * @param data   byte array to hash
     * @param length length of the array to hash
     * @return 32 bit hash of the given array
     * @see #hash32(byte[], int, int)
     */
    public static int hash32(final byte[] data, final int length) {
        return hash32(data, length, 0x9747b28c);
    }

    /**
     * Generates 32 bit hash from a string with a default seed.
     * The string is converted to bytes using the default encoding.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0x9747b28c;
     * byte[] bytes = data.getBytes();
     * int hash = MurmurHash2.hash32(bytes, bytes.length, seed);
     * </pre>
     *
     * @param text string to hash
     * @return 32 bit hash of the given string
     * @see #hash32(byte[], int, int)
     */
    public static int hash32(final String text) {
        final byte[] bytes = text.getBytes();
        return hash32(bytes, bytes.length);
    }

    /**
     * Generates 32 bit hash from a substring with a default seed value.
     * The string is converted to bytes using the default encoding.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0x9747b28c;
     * byte[] bytes = text.substring(from, from + length).getBytes();
     * int hash = MurmurHash2.hash32(bytes, bytes.length, seed);
     * </pre>
     *
     * @param text   string to hash
     * @param from   starting index
     * @param length length of the substring to hash
     * @return 32 bit hash of the given string
     * @see #hash32(byte[], int, int)
     */
    public static int hash32(final String text, final int from, final int length) {
        return hash32(text.substring(from, from + length));
    }

    /**
     * Generates 64 bit hash from byte array of the given length and seed.
     *
     * @param data   byte array to hash
     * @param length length of the array to hash
     * @param seed   initial seed value
     * @return 64 bit hash of the given array
     */
    public static long hash64(final byte[] data, final int length, final int seed) {
        final long m = 0xc6a4a7935bd1e995L;
        final int r = 47;

        long h = (seed & 0xffffffffl) ^ (length * m);

        final int nblocks = length >> 3;

        // body
        for (int i = 0; i < nblocks; i++) {
            final int index = (i << 3);
            long k = getLittleEndianLong(data, index);

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        final int index = (nblocks << 3);
        switch (length - index) {
        case 7:
            h ^= ((long) data[index + 6] & 0xff) << 48;
        case 6:
            h ^= ((long) data[index + 5] & 0xff) << 40;
        case 5:
            h ^= ((long) data[index + 4] & 0xff) << 32;
        case 4:
            h ^= ((long) data[index + 3] & 0xff) << 24;
        case 3:
            h ^= ((long) data[index + 2] & 0xff) << 16;
        case 2:
            h ^= ((long) data[index + 1] & 0xff) << 8;
        case 1:
            h ^= ((long) data[index] & 0xff);
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        return h;
    }

    /**
     * Generates 64 bit hash from byte array with given length and a default seed value.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0xe17a1465;
     * int hash = MurmurHash2.hash64(data, offset, length, seed);
     * </pre>
     *
     * @param data   byte array to hash
     * @param length length of the array to hash
     * @return 64 bit hash of the given string
     * @see #hash64(byte[], int, int)
     */
    public static long hash64(final byte[] data, final int length) {
        return hash64(data, length, 0xe17a1465);
    }

    /**
     * Generates 64 bit hash from a string with a default seed.
     * The string is converted to bytes using the default encoding.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0xe17a1465;
     * byte[] bytes = data.getBytes();
     * int hash = MurmurHash2.hash64(bytes, bytes.length, seed);
     * </pre>
     *
     * @param text string to hash
     * @return 64 bit hash of the given string
     * @see #hash64(byte[], int, int)
     */
    public static long hash64(final String text) {
        final byte[] bytes = text.getBytes();
        return hash64(bytes, bytes.length);
    }

    /**
     * Generates 64 bit hash from a substring with a default seed value.
     * The string is converted to bytes using the default encoding.
     * This is a helper method that will produce the same result as:
     *
     * <pre>
     * int seed = 0xe17a1465;
     * byte[] bytes = text.substring(from, from + length).getBytes();
     * int hash = MurmurHash2.hash64(bytes, bytes.length, seed);
     * </pre>
     *
     * @param text   string to hash
     * @param from   starting index
     * @param length length of the substring to hash
     * @return 64 bit hash of the given array
     * @see #hash64(byte[], int, int)
     */
    public static long hash64(final String text, final int from, final int length) {
        return hash64(text.substring(from, from + length));
    }

    /**
     * Gets the little-endian int from 4 bytes starting at the specified index.
     *
     * @param data The data
     * @param index The index
     * @return The little-endian int
     */
    private static int getLittleEndianInt(final byte[] data, final int index) {
        return ((data[index    ] & 0xff)      ) |
               ((data[index + 1] & 0xff) <<  8) |
               ((data[index + 2] & 0xff) << 16) |
               ((data[index + 3] & 0xff) << 24);
    }

    /**
     * Gets the little-endian long from 8 bytes starting at the specified index.
     *
     * @param data The data
     * @param index The index
     * @return The little-endian long
     */
    private static long getLittleEndianLong(final byte[] data, final int index) {
        return (((long) data[index    ] & 0xff)      ) |
               (((long) data[index + 1] & 0xff) <<  8) |
               (((long) data[index + 2] & 0xff) << 16) |
               (((long) data[index + 3] & 0xff) << 24) |
               (((long) data[index + 4] & 0xff) << 32) |
               (((long) data[index + 5] & 0xff) << 40) |
               (((long) data[index + 6] & 0xff) << 48) |
               (((long) data[index + 7] & 0xff) << 56);
    }
}
