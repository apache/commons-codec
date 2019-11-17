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

import java.nio.charset.StandardCharsets;

/**
 * MurmurHash3 yields a 32-bit or 128-bit value.
 *
 * MurmurHash is a non-cryptographic hash function suitable for general
 * hash-based lookup. The name comes from two basic operations, multiply (MU)
 * and rotate (R), used in its inner loop. Unlike cryptographic hash functions,
 * it is not specifically designed to be difficult to reverse by an adversary,
 * making it unsuitable for cryptographic purposes.
 *
 * 32-bit Java port of
 * https://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp#94
 * 128-bit Java port of
 * https://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp#255
 *
 * This is a public domain code with no copyrights. From homepage of MurmurHash
 * (https://code.google.com/p/smhasher/), "All MurmurHash versions are public
 * domain software, and the author disclaims all copyright to their code."
 *
 * Original methods copied from Apache Hive:
 * https://github.com/apache/hive/blob/master/storage-api/src/java/org/apache/hive/common/util/Murmur3.java
 *
 * hash128x64 and hash32x86 and supporting methods based on code from Yonik Seeley:
 * https://github.com/yonik/java_util
 *
 * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
 * @since 1.13
 */
public final class MurmurHash3 {

    /** TODO Replace on Java 8 with Long.BYTES. */
    static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    /** TODO Replace on Java 8 with Integer.BYTES. */
    static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    /** TODO Replace on Java 8 with Short.BYTES. */
    static final int SHORT_BYTES = Short.SIZE / Byte.SIZE;

    // from 64-bit linear congruential generator
    public static final long NULL_HASHCODE = 2862933555777941757L;

    // Constants for 32 bit variant
    private static final int C1_32 = 0xcc9e2d51;
    private static final int C2_32 = 0x1b873593;
    private static final int R1_32 = 15;
    private static final int R2_32 = 13;
    private static final int M_32 = 5;
    private static final int N_32 = 0xe6546b64;
    private static final int UBYTE_MASK = 0xff;

    // Constants for 128 bit variant
    private static final long C1 = 0x87c37b91114253d5L;
    private static final long C2 = 0x4cf5ad432745937fL;
    private static final int R1 = 31;
    private static final int R2 = 27;
    private static final int R3 = 33;
    private static final int M = 5;
    private static final int N1 = 0x52dce729;
    private static final int N2 = 0x38495ab5;
    private static final long UINT_MASK = 0xffffffffL;
    private static final long UBYTE_LONG_MASK = 0xffL;

    public static final int DEFAULT_SEED = 104729;

    // all methods static; private constructor.
    private MurmurHash3() {
    }

    /**
     * Generates 32 bit hash from two longs with default seed value.
     *
     * @param l0 long to hash
     * @param l1 long to hash
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final long l0, final long l1) {
        return hash32(l0, l1, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from a long with default seed value.
     *
     * @param l0 long to hash
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final long l0) {
        return hash32(l0, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from a long with the given seed.
     *
     * @param l0   long to hash
     * @param seed initial seed value
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final long l0, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(l0);

        hash = mix32((int) r0, hash);
        hash = mix32((int) (r0 >>> 32), hash);

        return fmix32(LONG_BYTES, hash);
    }

    /**
     * Generates 32 bit hash from two longs with the given seed.
     *
     * @param l0   long to hash
     * @param l1   long to hash
     * @param seed initial seed value
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final long l0, final long l1, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(l0);
        final long r1 = Long.reverseBytes(l1);

        hash = mix32((int) r0, hash);
        hash = mix32((int) (r0 >>> 32), hash);
        hash = mix32((int) (r1), hash);
        hash = mix32((int) (r1 >>> 32), hash);

        return fmix32(LONG_BYTES * 2, hash);
    }

    /**
     * Generates 32 bit hash from byte array with the default seed.
     *
     * @param data - input byte array
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final byte[] data) {
        return hash32(data, 0, data.length, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from a string with the default seed.
     *
     * @param data - input string
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final String data) {
        final byte[] origin = data.getBytes();
        return hash32(origin, 0, origin.length, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from byte array with the default seed.
     *
     * @param data   - input byte array
     * @param length - length of array
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final byte[] data, final int length) {
        return hash32(data, length, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from byte array with the given length and seed.
     *
     * @param data   - input byte array
     * @param length - length of array
     * @param seed   - seed. (default 0)
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final byte[] data, final int length, final int seed) {
        return hash32(data, 0, length, seed);
    }

    /**
     * Generates 32 bit hash from byte array with the given length, offset and seed.
     *
     * @param data   - input byte array
     * @param offset - offset of data
     * @param length - length of array
     * @param seed   - seed. (default 0)
     * @return 32 bit hash
     * @deprecated use hash32x86
     */
    @Deprecated
    public static int hash32(final byte[] data, final int offset, final int length, final int seed) {
        int hash = seed;
        final int nblocks = length >> 2;

        // body
        for (int i = 0; i < nblocks; i++) {
            final int i_4 = i << 2;
            final int k = (data[offset + i_4] & 0xff) | ((data[offset + i_4 + 1] & 0xff) << 8)
                    | ((data[offset + i_4 + 2] & 0xff) << 16) | ((data[offset + i_4 + 3] & 0xff) << 24);

            hash = mix32(k, hash);
        }

        // tail
        final int idx = nblocks << 2;
        int k1 = 0;
        switch (length - idx) {
        case 3:
            k1 ^= data[offset + idx + 2] << 16;
        case 2:
            k1 ^= data[offset + idx + 1] << 8;
        case 1:
            k1 ^= data[offset + idx];

            // mix functions
            k1 *= C1_32;
            k1 = Integer.rotateLeft(k1, R1_32);
            k1 *= C2_32;
            hash ^= k1;
        }

        return fmix32(length, hash);
    }

    /**
     * Murmur3 64-bit variant. This is essentially MSB 8 bytes of Murmur3 128-bit
     * variant.
     *
     * @param data - input byte array
     * @return 64 bit hash
     */
    public static long hash64(final byte[] data) {
        return hash64(data, 0, data.length, DEFAULT_SEED);
    }

    /**
     * Murmur3 64-bit variant. This is essentially MSB 8 bytes of Murmur3 128-bit
     * variant.
     *
     * @param data - input long
     * @return 64 bit hash
     */
    public static long hash64(final long data) {
        long hash = DEFAULT_SEED;
        long k = Long.reverseBytes(data);
        final int length = LONG_BYTES;
        // mix functions
        k *= C1;
        k = Long.rotateLeft(k, R1);
        k *= C2;
        hash ^= k;
        hash = Long.rotateLeft(hash, R2) * M + N1;
        // finalization
        hash ^= length;
        hash = fmix64(hash);
        return hash;
    }

    /**
     * Murmur3 64-bit variant. This is essentially MSB 8 bytes of Murmur3 128-bit
     * variant.
     *
     * @param data - input int
     * @return 64 bit hash
     */
    public static long hash64(final int data) {
        long k1 = Integer.reverseBytes(data) & (-1L >>> 32);
        final int length = INTEGER_BYTES;
        long hash = DEFAULT_SEED;
        k1 *= C1;
        k1 = Long.rotateLeft(k1, R1);
        k1 *= C2;
        hash ^= k1;
        // finalization
        hash ^= length;
        hash = fmix64(hash);
        return hash;
    }

    /**
     * Murmur3 64-bit variant. This is essentially MSB 8 bytes of Murmur3 128-bit
     * variant.
     *
     * @param data - input short
     * @return 64 bit hash
     */
    public static long hash64(final short data) {
        long hash = DEFAULT_SEED;
        long k1 = 0;
        k1 ^= ((long) data & 0xff) << 8;
        k1 ^= ((long) ((data & 0xFF00) >> 8) & 0xff);
        k1 *= C1;
        k1 = Long.rotateLeft(k1, R1);
        k1 *= C2;
        hash ^= k1;

        // finalization
        hash ^= SHORT_BYTES;
        hash = fmix64(hash);
        return hash;
    }

    /**
     * Generates 64 bit hash from byte array with the given length, offset and
     * default seed.
     *
     * @param data   - input byte array
     * @param offset - offset of data
     * @param length - length of array
     * @return 64 bit hash
     */
    public static long hash64(final byte[] data, final int offset, final int length) {
        return hash64(data, offset, length, DEFAULT_SEED);
    }

    /**
     * Generates 64 bit hash from byte array with the given length, offset and seed.
     *
     * @param data   - input byte array
     * @param offset - offset of data
     * @param length - length of array
     * @param seed   - seed. (default 0)
     * @return 64 bit hash
     */
    public static long hash64(final byte[] data, final int offset, final int length, final int seed) {
        long hash = seed;
        final int nblocks = length >> 3;

        // body
        for (int i = 0; i < nblocks; i++) {
            final int i8 = i << 3;
            long k = ((long) data[offset + i8] & 0xff) | (((long) data[offset + i8 + 1] & 0xff) << 8)
                    | (((long) data[offset + i8 + 2] & 0xff) << 16) | (((long) data[offset + i8 + 3] & 0xff) << 24)
                    | (((long) data[offset + i8 + 4] & 0xff) << 32) | (((long) data[offset + i8 + 5] & 0xff) << 40)
                    | (((long) data[offset + i8 + 6] & 0xff) << 48) | (((long) data[offset + i8 + 7] & 0xff) << 56);

            // mix functions
            k *= C1;
            k = Long.rotateLeft(k, R1);
            k *= C2;
            hash ^= k;
            hash = Long.rotateLeft(hash, R2) * M + N1;
        }

        // tail
        long k1 = 0;
        final int tailStart = nblocks << 3;
        switch (length - tailStart) {
        case 7:
            k1 ^= ((long) data[offset + tailStart + 6] & 0xff) << 48;
        case 6:
            k1 ^= ((long) data[offset + tailStart + 5] & 0xff) << 40;
        case 5:
            k1 ^= ((long) data[offset + tailStart + 4] & 0xff) << 32;
        case 4:
            k1 ^= ((long) data[offset + tailStart + 3] & 0xff) << 24;
        case 3:
            k1 ^= ((long) data[offset + tailStart + 2] & 0xff) << 16;
        case 2:
            k1 ^= ((long) data[offset + tailStart + 1] & 0xff) << 8;
        case 1:
            k1 ^= ((long) data[offset + tailStart] & 0xff);
            k1 *= C1;
            k1 = Long.rotateLeft(k1, R1);
            k1 *= C2;
            hash ^= k1;
        }

        // finalization
        hash ^= length;
        hash = fmix64(hash);

        return hash;
    }

    /**
     * Murmur3 128-bit variant.
     *
     * <p><b>
     * This implementation has a sign extension error and should not be used except where
     * backwards compatibility with an earlier release is requried.
     * </b></p>
     *
     * @param data - input byte array
     * @return - 128 bit hash (2 longs)
     * @deprecated use hash128x64
     */
    @Deprecated
    public static long[] hash128(final byte[] data) {
        return hash128(data, 0, data.length, DEFAULT_SEED);
    }

    /**
     * Murmur3 128-bit variant.
     *
     * <p><b>
     * This implementation has a sign extension error and should not be used except where
     * backwards compatibility with an earlier release is requried.
     * </b></p>
     *
     * @param data - input String
     * @return - 128 bit hash (2 longs)
     * @deprecated use hash128x64
     */
    @Deprecated
    public static long[] hash128(final String data) {
        final byte[] origin = data.getBytes();
        return hash128(origin, 0, origin.length, DEFAULT_SEED);
    }

    /**
     * Murmur3 128-bit variant.
     *
     * <p><b>
     * This implementation has a sign extension error and should not be used except where
     * backwards compatibility with an earlier release is requried.
     * </b></p>
     *
     * @param data   - input byte array
     * @param offset - the first element of array
     * @param length - length of array
     * @param seed   - seed. (default is 0)
     * @return - 128 bit hash (2 longs)
     * @deprecated use hash128x64
     */
    @Deprecated
    public static long[] hash128(final byte[] data, final int offset, final int length, final int seed) {
        long h1 = seed;
        long h2 = seed;
        final int nblocks = length >> 4;

        // body
        for (int i = 0; i < nblocks; i++) {
            final int i16 = i << 4;
            long k1 = ((long) data[offset + i16] & 0xff) | (((long) data[offset + i16 + 1] & 0xff) << 8)
                    | (((long) data[offset + i16 + 2] & 0xff) << 16) | (((long) data[offset + i16 + 3] & 0xff) << 24)
                    | (((long) data[offset + i16 + 4] & 0xff) << 32) | (((long) data[offset + i16 + 5] & 0xff) << 40)
                    | (((long) data[offset + i16 + 6] & 0xff) << 48) | (((long) data[offset + i16 + 7] & 0xff) << 56);

            long k2 = ((long) data[offset + i16 + 8] & 0xff) | (((long) data[offset + i16 + 9] & 0xff) << 8)
                    | (((long) data[offset + i16 + 10] & 0xff) << 16) | (((long) data[offset + i16 + 11] & 0xff) << 24)
                    | (((long) data[offset + i16 + 12] & 0xff) << 32) | (((long) data[offset + i16 + 13] & 0xff) << 40)
                    | (((long) data[offset + i16 + 14] & 0xff) << 48) | (((long) data[offset + i16 + 15] & 0xff) << 56);

            // mix functions for k1
            k1 *= C1;
            k1 = Long.rotateLeft(k1, R1);
            k1 *= C2;
            h1 ^= k1;
            h1 = Long.rotateLeft(h1, R2);
            h1 += h2;
            h1 = h1 * M + N1;

            // mix functions for k2
            k2 *= C2;
            k2 = Long.rotateLeft(k2, R3);
            k2 *= C1;
            h2 ^= k2;
            h2 = Long.rotateLeft(h2, R1);
            h2 += h1;
            h2 = h2 * M + N2;
        }

        // tail
        long k1 = 0;
        long k2 = 0;
        final int tailStart = nblocks << 4;
        switch (length - tailStart) {
        case 15:
            k2 ^= (long) (data[offset + tailStart + 14] & 0xff) << 48;
        case 14:
            k2 ^= (long) (data[offset + tailStart + 13] & 0xff) << 40;
        case 13:
            k2 ^= (long) (data[offset + tailStart + 12] & 0xff) << 32;
        case 12:
            k2 ^= (long) (data[offset + tailStart + 11] & 0xff) << 24;
        case 11:
            k2 ^= (long) (data[offset + tailStart + 10] & 0xff) << 16;
        case 10:
            k2 ^= (long) (data[offset + tailStart + 9] & 0xff) << 8;
        case 9:
            k2 ^= data[offset + tailStart + 8] & 0xff;
            k2 *= C2;
            k2 = Long.rotateLeft(k2, R3);
            k2 *= C1;
            h2 ^= k2;

        case 8:
            k1 ^= (long) (data[offset + tailStart + 7] & 0xff) << 56;
        case 7:
            k1 ^= (long) (data[offset + tailStart + 6] & 0xff) << 48;
        case 6:
            k1 ^= (long) (data[offset + tailStart + 5] & 0xff) << 40;
        case 5:
            k1 ^= (long) (data[offset + tailStart + 4] & 0xff) << 32;
        case 4:
            k1 ^= (long) (data[offset + tailStart + 3] & 0xff) << 24;
        case 3:
            k1 ^= (long) (data[offset + tailStart + 2] & 0xff) << 16;
        case 2:
            k1 ^= (long) (data[offset + tailStart + 1] & 0xff) << 8;
        case 1:
            k1 ^= data[offset + tailStart] & 0xff;
            k1 *= C1;
            k1 = Long.rotateLeft(k1, R1);
            k1 *= C2;
            h1 ^= k1;
        }

        // finalization
        h1 ^= length;
        h2 ^= length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return new long[] { h1, h2 };
    }

    private static int mix32(int k, int hash) {
        k *= C1_32;
        k = Integer.rotateLeft(k, R1_32);
        k *= C2_32;
        hash ^= k;
        return Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
    }

    private static int fmix32(final int length, int hash) {
        hash ^= length;
        hash ^= (hash >>> 16);
        hash *= 0x85ebca6b;
        hash ^= (hash >>> 13);
        hash *= 0xc2b2ae35;
        hash ^= (hash >>> 16);

        return hash;
    }

    private static long fmix64(long h) {
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >>> 33);
        return h;
    }

    /**
     * This class uses the hash32 algorithm with the sign extension bug.  It remains
     * here for use by existing applications that can not easily switch to the new
     * algorithm.
     * @deprecated use IncrementalHash32_x86
     */
    @Deprecated
    public static class IncrementalHash32 {
        byte[] tail = new byte[3];
        int tailLen;
        int totalLen;
        int hash;

        public final void start(final int hash) {
            tailLen = totalLen = 0;
            this.hash = hash;
        }

        public final void add(final byte[] data, int offset, final int length) {
            if (length == 0) {
                return;
            }
            totalLen += length;
            if (tailLen + length < 4) {
                System.arraycopy(data, offset, tail, tailLen, length);
                tailLen += length;
                return;
            }
            int offset2 = 0;
            if (tailLen > 0) {
                offset2 = (4 - tailLen);
                int k = -1;
                switch (tailLen) {
                case 1:
                    k = orBytes(tail[0], data[offset], data[offset + 1], data[offset + 2]);
                    break;
                case 2:
                    k = orBytes(tail[0], tail[1], data[offset], data[offset + 1]);
                    break;
                case 3:
                    k = orBytes(tail[0], tail[1], tail[2], data[offset]);
                    break;
                default:
                    throw new AssertionError(tailLen);
                }
                // mix functions
                k *= C1_32;
                k = Integer.rotateLeft(k, R1_32);
                k *= C2_32;
                hash ^= k;
                hash = Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
            }
            final int length2 = length - offset2;
            offset += offset2;
            final int nblocks = length2 >> 2;

            for (int i = 0; i < nblocks; i++) {
                final int i_4 = (i << 2) + offset;
                int k = orBytes(data[i_4], data[i_4 + 1], data[i_4 + 2], data[i_4 + 3]);

                // mix functions
                k *= C1_32;
                k = Integer.rotateLeft(k, R1_32);
                k *= C2_32;
                hash ^= k;
                hash = Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
            }

            final int consumed = (nblocks << 2);
            tailLen = length2 - consumed;
            if (consumed == length2) {
                return;
            }
            System.arraycopy(data, offset + consumed, tail, 0, tailLen);
        }

        public final int end() {
            int k1 = 0;
            switch (tailLen) {
            case 3:
                k1 ^= tail[2] << 16;
            case 2:
                k1 ^= tail[1] << 8;
            case 1:
                k1 ^= tail[0];

                // mix functions
                k1 *= C1_32;
                k1 = Integer.rotateLeft(k1, R1_32);
                k1 *= C2_32;
                hash ^= k1;
            }

            // finalization
            hash ^= totalLen;
            hash ^= (hash >>> 16);
            hash *= 0x85ebca6b;
            hash ^= (hash >>> 13);
            hash *= 0xc2b2ae35;
            hash ^= (hash >>> 16);
            return hash;
        }
    }

    public static class IncrementalHash32x86 {
        byte[] tail = new byte[3];
        int tailLen;
        int totalLen;
        int hash;

        public final void start(final int hash) {
            tailLen = totalLen = 0;
            this.hash = hash;
        }

        public final void add(final byte[] data, int offset, final int length) {
            if (length == 0) {
                return;
            }
            totalLen += length;
            if (tailLen + length < 4) {
                System.arraycopy(data, offset, tail, tailLen, length);
                tailLen += length;
                return;
            }
            int offset2 = 0;
            if (tailLen > 0) {
                offset2 = (4 - tailLen);
                int k = -1;
                switch (tailLen) {
                case 1:
                    k = orBytes(tail[0], data[offset], data[offset + 1], data[offset + 2]);
                    break;
                case 2:
                    k = orBytes(tail[0], tail[1], data[offset], data[offset + 1]);
                    break;
                case 3:
                    k = orBytes(tail[0], tail[1], tail[2], data[offset]);
                    break;
                default:
                    throw new AssertionError(tailLen);
                }
                // mix functions
                k *= C1_32;
                k = Integer.rotateLeft(k, R1_32);
                k *= C2_32;
                hash ^= k;
                hash = Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
            }
            final int length2 = length - offset2;
            offset += offset2;
            final int nblocks = length2 >> 2;

            for (int i = 0; i < nblocks; i++) {
                final int i_4 = (i << 2) + offset;
                int k = orBytes(data[i_4], data[i_4 + 1], data[i_4 + 2], data[i_4 + 3]);

                // mix functions
                k *= C1_32;
                k = Integer.rotateLeft(k, R1_32);
                k *= C2_32;
                hash ^= k;
                hash = Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
            }

            final int consumed = (nblocks << 2);
            tailLen = length2 - consumed;
            if (consumed == length2) {
                return;
            }
            System.arraycopy(data, offset + consumed, tail, 0, tailLen);
        }

        public final int end() {
            int k1 = 0;
            switch (tailLen) {
            case 3:
                k1 ^= (tail[2] & UBYTE_MASK) << 16;
            case 2:
                k1 ^= (tail[1] & UBYTE_MASK) << 8;
            case 1:
                k1 ^= (tail[0] & UBYTE_MASK);

                // mix functions
                k1 *= C1_32;
                k1 = Integer.rotateLeft(k1, R1_32);
                k1 *= C2_32;
                hash ^= k1;
            }

            // finalization
            hash ^= totalLen;
            hash ^= (hash >>> 16);
            hash *= 0x85ebca6b;
            hash ^= (hash >>> 13);
            hash *= 0xc2b2ae35;
            hash ^= (hash >>> 16);
            return hash;
        }
    }
    private static int orBytes(final byte b1, final byte b2, final byte b3, final byte b4) {
        return (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) | ((b4 & 0xff) << 24);
    }

    /**
     * Generates 32 bit hash from a long with default seed value.
     *
     * @param l0 long to hash
     * @return 32 bit hash
     */
    public static int hash32x86(final long l0) {
        return hash32x86(l0, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from a long with the given seed.
     *
     * @param l0   long to hash
     * @param seed initial seed value
     * @return 32 bit hash
     */
    public static int hash32x86(final long l0, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(l0);

        hash = mix32((int) r0, hash);
        hash = mix32((int) (r0 >>> 32), hash);

        return fmix32(LONG_BYTES, hash);
    }

    /**
     * Generates 32 bit hash from two longs with default seed value.
     *
     * @param l0 long to hash
     * @param l1 long to hash
     * @return 32 bit hash
     */
    public static int hash32x86(final long l0, final long l1) {
        return hash32x86(l0, l1, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from two longs with the given seed.
     *
     * @param l0   long to hash
     * @param l1   long to hash
     * @param seed initial seed value
     * @return 32 bit hash
     */
    public static int hash32x86(final long l0, final long l1, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(l0);
        final long r1 = Long.reverseBytes(l1);

        hash = mix32((int) r0, hash);
        hash = mix32((int) (r0 >>> 32), hash);
        hash = mix32((int) (r1), hash);
        hash = mix32((int) (r1 >>> 32), hash);

        return fmix32(LONG_BYTES * 2, hash);
    }

    /**
     * Generates 32 bit hash from byte array with the default seed.
     *
     * @param data input byte array
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(final byte[] data) {
        return hash32x86(data, 0, data.length, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from a string with the default seed.
     *
     * @param data input string
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(final String data) {
        return hash32x86(data, 0, data.length(), DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from byte array with the default seed.
     *
     * @param data   input byte array
     * @param length length of array
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(final byte[] data, final int length) {
        return hash32x86(data, length, DEFAULT_SEED);
    }

    /**
     * Generates 32 bit hash from byte array with the given length and seed.
     *
     * @param data   input byte array
     * @param length length of array
     * @param seed   seed. (default 0)
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(final byte[] data, final int length, final int seed) {
        return hash32x86(data, 0, length, seed);
    }


    /**
     * Generates 32 bit hash from byte array with the given length and seed.
     *
     * @param data   input byte array
     * @param offset start of the array
     * @param length length of array
     * @param seed   the  seed.
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(byte[] data, int offset, int length, int seed) {

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = seed;
        int roundedEnd = offset + (length & 0xfffffffc);  // round down to 4 byte block

        for (int i=offset; i<roundedEnd; i+=4) {
            // little endian load order
            int k1 = (data[i] & UBYTE_MASK) | ((data[i+1] & UBYTE_MASK) << 8) | ((data[i+2] & UBYTE_MASK) << 16) | (data[i+3] << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1*5+0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch(length & 0x03) {
        case 3:
            k1 = (data[roundedEnd + 2] & UBYTE_MASK) << 16;
            // fallthrough
        case 2:
            k1 |= (data[roundedEnd + 1] & UBYTE_MASK) << 8;
            // fallthrough
        case 1:
            k1 |= (data[roundedEnd] & UBYTE_MASK);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;
            h1 ^= k1;
        }

        // finalization
        h1 ^= length;

        // fmix(h1);
        h1 ^= h1 >>> 16;
            h1 *= 0x85ebca6b;
            h1 ^= h1 >>> 13;
            h1 *= 0xc2b2ae35;
            h1 ^= h1 >>> 16;

        return h1;
    }


    /** Returns the MurmurHash3_x86_32 hash of the UTF-8 bytes of the String without actually encoding
     * the string to a temporary buffer.  This is more than 2x faster than hashing the result
     * of String.getBytes().
     *
     * @param data the CharSequence to hash
     * @param offset the offset to start at.
     * @param len the number of characters to hash.
     * @param seed the seed.
     * @return 32 bit hash
     * @since 1.14
     */
    public static int hash32x86(CharSequence data, int offset, int len, int seed) {

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = seed;

        int pos = offset;
        int end = offset + len;
        int k1 = 0;
        int k2 = 0;
        int shift = 0;
        int bits = 0;
        int nBytes = 0;   // length in UTF8 bytes


        while (pos < end) {
            int code = data.charAt(pos++);
            if (code < 0x80) {
                k2 = code;
                bits = 8;
            }
            else if (code < 0x800) {
                k2 = (0xC0 | (code >> 6))
                    | ((0x80 | (code & 0x3f)) << 8);
                bits = 16;
            }
            else if (code < 0xd800 || code > 0xdfff || pos>=end) {
                // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
                k2 = (0xe0 | (code >> 12))
                    | ((0x80 | ((code >> 6) & 0x3f)) << 8)
                    | ((0x80 | (code & 0x3f)) << 16);
                bits = 24;
            } else {
                // surrogate pair
                // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
                int utf32 = data.charAt(pos++);
                utf32 = ((code - 0xd7c0) << 10) + (utf32 & 0x3ff);
                k2 = (UBYTE_MASK & (0xF0 | (utf32 >> 18)))
                    | ((0x80 | ((utf32 >> 12) & 0x3f))) << 8
                    | ((0x80 | ((utf32 >> 6) & 0x3f))) << 16
                    |  (0x80 | (utf32 & 0x3f)) << 24;
                bits = 32;
            }


            k1 |= k2 << shift;

            // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
            // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift

            shift += bits;
            if (shift >= 32) {
                // mix after we have a complete word

                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;

                h1 ^= k1;
                h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                h1 = h1*5+0xe6546b64;

                shift -= 32;
                // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
                if (shift != 0) {
                    k1 = k2 >>> (bits-shift);   // bits used == bits - newshift
                } else {
                    k1 = 0;
                }
                nBytes += 4;
            }

        } // inner

        // handle tail
        if (shift > 0) {
            nBytes += shift >> 3;
                    k1 *= c1;
                    k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                    k1 *= c2;
                    h1 ^= k1;
        }

        // finalization
        h1 ^= nBytes;

        // fmix(h1);
        h1 ^= h1 >>> 16;
            h1 *= 0x85ebca6b;
            h1 ^= h1 >>> 13;
            h1 *= 0xc2b2ae35;
            h1 ^= h1 >>> 16;

        return h1;
    }

    /**
     * Gets a long from a byte buffer in little endian byte order.
     * byte must be at least offset+7 bytes long.
     * @param buf The buffer.
     * @param offset the start of the long.
     * @return the 8 bytes as a little endian long.
     * @since 1.14
     */
    public static final long getLongLittleEndian(byte[] buf, int offset) {
        return     ((long)buf[offset+7]    << 56)   // no mask needed
            | ((buf[offset+6] & UBYTE_LONG_MASK) << 48)
            | ((buf[offset+5] & UBYTE_LONG_MASK) << 40)
            | ((buf[offset+4] & UBYTE_LONG_MASK) << 32)
            | ((buf[offset+3] & UBYTE_LONG_MASK) << 24)
            | ((buf[offset+2] & UBYTE_LONG_MASK) << 16)
            | ((buf[offset+1] & UBYTE_LONG_MASK) << 8)
            | ((buf[offset  ] & UBYTE_LONG_MASK));        // no shift needed
    }

    /**
     * Murmur3 128-bit variant.
     *
     * uses DEFAULT_SEED
     * @param data - input byte array
     * @return - 128 bit hash (2 longs)
     * @since 1.14
     */
    public static long[] hash128x64(final byte[] data) {
        return hash128x64(data, DEFAULT_SEED);
    }

    /**
     * Murmur3 128-bit variant.
     *
     * @param data input byte array
     * @param seed the seed
     * @return 128 bit hash (2 longs)
     * @since 1.14
     */
    public static long[] hash128x64(final byte[] data, int seed) {
        return hash128x64(data, 0, data.length, seed);
    }

    /**
     * Murmur3 128-bit x64 variant.
     *
     * Data is converted to byte array using UTF-8 encoding.
     * Uses DEFAULT_SEED.
     *
     * @param data input string.
     * @return 128 bit hash (2 longs)
     * @since 1.14
     */
    public static long[] hash128x64(final String data) {
        return hash128x64(data, DEFAULT_SEED);
    }

    /**
     * Murmur3 128-bit x64 variant.
     *
     * Data is converted to byte array using UTF-8 encoding.
     *
     * @param data Input string.
     * @param seed The seed.
     * @return 128 bit hash (2 longs)
     * @since 1.14
     */
    public static long[] hash128x64(final String data, int seed) {
        final byte[] origin = data.getBytes(StandardCharsets.UTF_8);
        return hash128x64(origin, 0, origin.length, seed);
    }

    /**
     * Murmur3 128-bit x64 variant.
     *
     * @param key   input byte array
     * @param offset the first element of array
     * @param length length of array
     * @param seed   seed.
     * @return 128 bit hash (2 longs)
     * @since 1.14
     */
    public static long[] hash128x64(byte[] key, int offset, int length, int seed) {
        /* The original algorithm does have a 32 bit unsigned seed.
         We have to mask to match the behavior of the unsigned types and prevent sign extension.
         */
        long h1 = seed & UINT_MASK;
        long h2 = seed & UINT_MASK;

        final long c1 = 0x87c37b91114253d5L;
        final long c2 = 0x4cf5ad432745937fL;

        int roundedEnd = offset + (length & 0xfffffff0);  // round down to 16 byte block
        for (int i=offset; i<roundedEnd; i+=16) {
            long k1 = getLongLittleEndian(key, i);
            long k2 = getLongLittleEndian(key, i+8);
            k1 *= c1; k1  = Long.rotateLeft(k1,31); k1 *= c2; h1 ^= k1;
            h1 = Long.rotateLeft(h1,27); h1 += h2; h1 = h1*5+0x52dce729;
            k2 *= c2; k2  = Long.rotateLeft(k2,33); k2 *= c1; h2 ^= k2;
            h2 = Long.rotateLeft(h2,31); h2 += h1; h2 = h2*5+0x38495ab5;
        }

        long k1 = 0;
        long k2 = 0;

        switch (length & 15) {
        case 15: k2  = (key[roundedEnd+14] & UBYTE_LONG_MASK) << 48;
        case 14: k2 |= (key[roundedEnd+13] & UBYTE_LONG_MASK) << 40;
        case 13: k2 |= (key[roundedEnd+12] & UBYTE_LONG_MASK) << 32;
        case 12: k2 |= (key[roundedEnd+11] & UBYTE_LONG_MASK) << 24;
        case 11: k2 |= (key[roundedEnd+10] & UBYTE_LONG_MASK) << 16;
        case 10: k2 |= (key[roundedEnd+ 9] & UBYTE_LONG_MASK) << 8;
        case  9: k2 |= (key[roundedEnd+ 8] & UBYTE_LONG_MASK);
        k2 *= c2; k2  = Long.rotateLeft(k2, 33); k2 *= c1; h2 ^= k2;
        case  8: k1  = ((long)key[roundedEnd+7]) << 56;
        case  7: k1 |= (key[roundedEnd+6] & UBYTE_LONG_MASK) << 48;
        case  6: k1 |= (key[roundedEnd+5] & UBYTE_LONG_MASK) << 40;
        case  5: k1 |= (key[roundedEnd+4] & UBYTE_LONG_MASK) << 32;
        case  4: k1 |= (key[roundedEnd+3] & UBYTE_LONG_MASK) << 24;
        case  3: k1 |= (key[roundedEnd+2] & UBYTE_LONG_MASK) << 16;
        case  2: k1 |= (key[roundedEnd+1] & UBYTE_LONG_MASK) << 8;
        case  1: k1 |= (key[roundedEnd  ] & UBYTE_LONG_MASK);
        k1 *= c1; k1  = Long.rotateLeft(k1,31); k1 *= c2; h1 ^= k1;
        }

        //----------
        // finalization

        h1 ^= length; h2 ^= length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return new long[] {h1,h2};
    }


}
