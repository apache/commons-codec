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

import java.util.Arrays;
import java.util.Objects;

/**
 * Implements the Blake3 algorithm providing a {@linkplain #initHash() hash function} with extensible output (XOF), a
 * {@linkplain #initKeyedHash(byte[]) keyed hash function} (MAC, PRF), and a
 * {@linkplain #initKeyDerivationFunction(byte[]) key derivation function} (KDF). Blake3 has a 128-bit security level
 * and a default output length of 256 bits (32 bytes) which can extended up to 2<sup>64</sup> bytes.
 * <p>
 * Adapted from the ISC-licensed O(1) Cryptography library by Matt Sicker and ported from the reference public domain
 * implementation by Jack O'Connor.
 * </p>
 *
 * @see <a href="https://github.com/BLAKE3-team/BLAKE3">BLAKE3 hash function</a>
 * @since 1.16
 */
public final class Blake3 {
    // TODO: migrate to Integer.BYTES after upgrading to Java 8
    private static final int INT_BYTES = Integer.SIZE / Byte.SIZE;

    private static final int BLOCK_LEN = 64;
    private static final int KEY_LEN = 32;
    private static final int OUT_LEN = 32;
    private static final int CHUNK_LEN = 1024;

    private static final int[] IV =
            { 0x6A09E667, 0xBB67AE85, 0x3C6EF372, 0xA54FF53A, 0x510E527F, 0x9B05688C, 0x1F83D9AB, 0x5BE0CD19 };

    // domain flags
    private static final int CHUNK_START = 1;
    private static final int CHUNK_END = 1 << 1;
    private static final int PARENT = 1 << 2;
    private static final int ROOT = 1 << 3;
    private static final int KEYED_HASH = 1 << 4;
    private static final int DERIVE_KEY_CONTEXT = 1 << 5;
    private static final int DERIVE_KEY_MATERIAL = 1 << 6;

    private final EngineState engineState;

    private Blake3(final int[] key, final int flags) {
        engineState = new EngineState(key, flags);
    }

    /**
     * Resets this instance back to its initial state when it was first constructed.
     */
    public void reset() {
        engineState.reset();
    }

    /**
     * Absorbs the provided bytes into this instance's state.
     *
     * @param in source array to absorb data from
     */
    public void absorb(final byte[] in) {
        Objects.requireNonNull(in);
        absorb(in, 0, in.length);
    }

    /**
     * Absorbs the provided bytes at an offset into this instance's state.
     *
     * @param in     source array to absorb data from
     * @param offset where in the array to begin absorbing bytes
     * @param length number of bytes to absorb in
     */
    public void absorb(final byte[] in, final int offset, final int length) {
        Objects.requireNonNull(in);
        engineState.inputData(in, offset, length);
    }

    /**
     * Squeezes hash output data that depends on the sequence of absorbed bytes preceding this invocation and any previously
     * squeezed bytes.
     *
     * @param out destination array to squeeze bytes into
     */
    public void squeeze(final byte[] out) {
        squeeze(out, 0, out.length);
    }

    /**
     * Squeezes an arbitrary number of bytes into the provided output array that depends on the sequence of previously
     * absorbed and squeezed bytes.
     *
     * @param out    destination array to squeeze bytes into
     * @param offset where in the array to begin writing bytes to
     * @param length number of bytes to squeeze out
     */
    public void squeeze(final byte[] out, final int offset, final int length) {
        Objects.requireNonNull(out);
        engineState.outputHash(out, offset, length);
    }

    /**
     * Squeezes and returns an arbitrary number of bytes dependent on the sequence of previously absorbed and squeezed bytes.
     *
     * @param nrBytes number of bytes to squeeze
     * @return requested number of squeezed bytes
     */
    public byte[] squeeze(final int nrBytes) {
        final byte[] hash = new byte[nrBytes];
        squeeze(hash);
        return hash;
    }

    /**
     * Constructs a fresh Blake3 hash function. The instance returned functions as an arbitrary length message digest.
     *
     * @return fresh Blake3 instance in hashed mode
     */
    public static Blake3 initHash() {
        return new Blake3(IV, 0);
    }

    /**
     * Constructs a fresh Blake3 keyed hash function. The instance returned functions as a pseudorandom function (PRF) or as a
     * message authentication code (MAC).
     *
     * @param key 32-byte secret key
     * @return fresh Blake3 instance in keyed mode using the provided key
     */
    public static Blake3 initKeyedHash(final byte[] key) {
        Objects.requireNonNull(key);
        if (key.length != KEY_LEN) {
            throw new IllegalArgumentException("Blake3 keys must be 32 bytes");
        }
        return new Blake3(unpackInts(key, 8), KEYED_HASH);
    }

    /**
     * Constructs a fresh Blake3 key derivation function using the provided key derivation context byte string.
     * The instance returned functions as a key-derivation function which can further absorb additional context data
     * before squeezing derived key data.
     *
     * @param kdfContext a globally unique key-derivation context byte string to separate key derivation contexts from each other
     * @return fresh Blake3 instance in key derivation mode
     */
    public static Blake3 initKeyDerivationFunction(final byte[] kdfContext) {
        Objects.requireNonNull(kdfContext);
        final EngineState kdf = new EngineState(IV, DERIVE_KEY_CONTEXT);
        kdf.inputData(kdfContext, 0, kdfContext.length);
        final byte[] key = new byte[KEY_LEN];
        kdf.outputHash(key, 0, key.length);
        return new Blake3(unpackInts(key, 8), DERIVE_KEY_MATERIAL);
    }

    /**
     * Calculates the Blake3 hash of the provided data.
     *
     * @param data source array to absorb data from
     * @return 32-byte hash squeezed from the provided data
     */
    public static byte[] hash(final byte[] data) {
        final Blake3 blake3 = Blake3.initHash();
        blake3.absorb(data);
        return blake3.squeeze(OUT_LEN);
    }

    /**
     * Calculates the Blake3 keyed hash (MAC) of the provided data.
     *
     * @param key  32-byte secret key
     * @param data source array to absorb data from
     * @return 32-byte mac squeezed from the provided data
     */
    public static byte[] keyedHash(final byte[] key, final byte[] data) {
        final Blake3 blake3 = Blake3.initKeyedHash(key);
        blake3.absorb(data);
        return blake3.squeeze(OUT_LEN);
    }

    private static void packInt(final int value, final byte[] dst, final int off, final int len) {
        for (int i = 0; i < len; i++) {
            dst[off + i] = (byte) (value >>> i * Byte.SIZE);
        }
    }

    private static int unpackInt(final byte[] buf, final int off) {
        return buf[off] & 0xFF | (buf[off + 1] & 0xFF) << 8 | (buf[off + 2] & 0xFF) << 16 | (buf[off + 3] & 0xFF) << 24;
    }

    private static int[] unpackInts(final byte[] buf, final int nrInts) {
        final int[] values = new int[nrInts];
        for (int i = 0, off = 0; i < nrInts; i++, off += INT_BYTES) {
            values[i] = unpackInt(buf, off);
        }
        return values;
    }

    // The mixing function, G, which mixes either a column or a diagonal.
    private static void g(
            final int[] state, final int a, final int b, final int c, final int d, final int mx, final int my) {
        state[a] += state[b] + mx;
        state[d] = Integer.rotateRight(state[d] ^ state[a], 16);
        state[c] += state[d];
        state[b] = Integer.rotateRight(state[b] ^ state[c], 12);
        state[a] += state[b] + my;
        state[d] = Integer.rotateRight(state[d] ^ state[a], 8);
        state[c] += state[d];
        state[b] = Integer.rotateRight(state[b] ^ state[c], 7);
    }

    private static void round(final int[] state, final int[] msg, final byte[] schedule) {
        // Mix the columns.
        g(state, 0, 4, 8, 12, msg[schedule[0]], msg[schedule[1]]);
        g(state, 1, 5, 9, 13, msg[schedule[2]], msg[schedule[3]]);
        g(state, 2, 6, 10, 14, msg[schedule[4]], msg[schedule[5]]);
        g(state, 3, 7, 11, 15, msg[schedule[6]], msg[schedule[7]]);

        // Mix the diagonals.
        g(state, 0, 5, 10, 15, msg[schedule[8]], msg[schedule[9]]);
        g(state, 1, 6, 11, 12, msg[schedule[10]], msg[schedule[11]]);
        g(state, 2, 7, 8, 13, msg[schedule[12]], msg[schedule[13]]);
        g(state, 3, 4, 9, 14, msg[schedule[14]], msg[schedule[15]]);
    }

    // pre-permuted for all 7 rounds; the second row (2,6,3,...) indicates the base permutation
    private static final byte[][] MSG_SCHEDULE = {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
            { 2, 6, 3, 10, 7, 0, 4, 13, 1, 11, 12, 5, 9, 14, 15, 8 },
            { 3, 4, 10, 12, 13, 2, 7, 14, 6, 5, 9, 0, 11, 15, 8, 1 },
            { 10, 7, 12, 9, 14, 3, 13, 15, 4, 0, 11, 2, 5, 8, 1, 6 },
            { 12, 13, 9, 11, 15, 10, 14, 8, 7, 2, 5, 3, 0, 1, 6, 4 },
            { 9, 14, 11, 5, 8, 12, 15, 1, 13, 3, 0, 10, 2, 6, 4, 7 },
            { 11, 15, 5, 0, 1, 9, 8, 6, 14, 10, 2, 12, 3, 4, 7, 13 }
    };

    private static int[] compress(
            final int[] chainingValue, final int[] blockWords, final int blockLength, final long counter,
            final int flags) {
        final int[] state = Arrays.copyOf(chainingValue, 16);
        System.arraycopy(IV, 0, state, 8, 4);
        state[12] = (int) counter;
        state[13] = (int) (counter >> Integer.SIZE);
        state[14] = blockLength;
        state[15] = flags;
        for (int i = 0; i < 7; i++) {
            final byte[] schedule = MSG_SCHEDULE[i];
            round(state, blockWords, schedule);
        }
        for (int i = 0; i < 8; i++) {
            state[i] ^= state[i + 8];
            state[i + 8] ^= chainingValue[i];
        }
        return state;
    }

    private static Output parentOutput(
            final int[] leftChildCV, final int[] rightChildCV, final int[] key, final int flags) {
        final int[] blockWords = Arrays.copyOf(leftChildCV, 16);
        System.arraycopy(rightChildCV, 0, blockWords, 8, 8);
        return new Output(key.clone(), blockWords, 0, BLOCK_LEN, flags | PARENT);
    }

    private static int[] parentChainingValue(
            final int[] leftChildCV, final int[] rightChildCV, final int[] key, final int flags) {
        return parentOutput(leftChildCV, rightChildCV, key, flags).chainingValue();
    }

    /**
     * Represents the state just prior to either producing an eight word chaining value or any number of output bytes when the
     * ROOT flag is set.
     */
    private static class Output {
        private final int[] inputChainingValue;
        private final int[] blockWords;
        private final long counter;
        private final int blockLength;
        private final int flags;

        Output(
                final int[] inputChainingValue, final int[] blockWords, final long counter, final int blockLength,
                final int flags) {
            this.inputChainingValue = inputChainingValue;
            this.blockWords = blockWords;
            this.counter = counter;
            this.blockLength = blockLength;
            this.flags = flags;
        }

        int[] chainingValue() {
            return Arrays.copyOf(compress(inputChainingValue, blockWords, blockLength, counter, flags), 8);
        }

        void rootOutputBytes(final byte[] out, int offset, int length) {
            int outputBlockCounter = 0;
            while (length > 0) {
                int chunkLength = Math.min(OUT_LEN * 2, length);
                length -= chunkLength;
                final int[] words =
                        compress(inputChainingValue, blockWords, blockLength, outputBlockCounter++, flags | ROOT);
                int wordCounter = 0;
                while (chunkLength > 0) {
                    final int wordLength = Math.min(INT_BYTES, chunkLength);
                    packInt(words[wordCounter++], out, offset, wordLength);
                    offset += wordLength;
                    chunkLength -= wordLength;
                }
            }
        }
    }

    private static class ChunkState {
        private int[] chainingValue;
        private final long chunkCounter;
        private final int flags;

        private final byte[] block = new byte[BLOCK_LEN];
        private int blockLength;
        private int blocksCompressed;

        ChunkState(final int[] key, final long chunkCounter, final int flags) {
            chainingValue = key;
            this.chunkCounter = chunkCounter;
            this.flags = flags;
        }

        int length() {
            return BLOCK_LEN * blocksCompressed + blockLength;
        }

        int startFlag() {
            return blocksCompressed == 0 ? CHUNK_START : 0;
        }

        void update(final byte[] input, int offset, int length) {
            while (length > 0) {
                if (blockLength == BLOCK_LEN) {
                    // If the block buffer is full, compress it and clear it. More
                    // input is coming, so this compression is not CHUNK_END.
                    final int[] blockWords = unpackInts(block, 16);
                    chainingValue = Arrays.copyOf(
                            compress(chainingValue, blockWords, BLOCK_LEN, chunkCounter, flags | startFlag()), 8);
                    blocksCompressed++;
                    blockLength = 0;
                    Arrays.fill(block, (byte) 0);
                }

                final int want = BLOCK_LEN - blockLength;
                final int take = Math.min(want, length);
                System.arraycopy(input, offset, block, blockLength, take);
                blockLength += take;
                offset += take;
                length -= take;
            }
        }

        Output output() {
            final int[] blockWords = unpackInts(block, 16);
            final int outputFlags = flags | startFlag() | CHUNK_END;
            return new Output(chainingValue, blockWords, chunkCounter, blockLength, outputFlags);
        }
    }

    private static class EngineState {
        private final int[] key;
        private final int flags;
        // Space for 54 subtree chaining values: 2^54 * CHUNK_LEN = 2^64
        private final int[][] cvStack = new int[54][];
        private int stackLen;
        private ChunkState state;

        EngineState(final int[] key, final int flags) {
            this.key = key;
            this.flags = flags;
            state = new ChunkState(key, 0, flags);
        }

        void inputData(final byte[] in, int offset, int length) {
            while (length > 0) {
                // If the current chunk is complete, finalize it and reset the
                // chunk state. More input is coming, so this chunk is not ROOT.
                if (state.length() == CHUNK_LEN) {
                    final int[] chunkCV = state.output().chainingValue();
                    final long totalChunks = state.chunkCounter + 1;
                    addChunkCV(chunkCV, totalChunks);
                    state = new ChunkState(key, totalChunks, flags);
                }

                // Compress input bytes into the current chunk state.
                final int want = CHUNK_LEN - state.length();
                final int take = Math.min(want, length);
                state.update(in, offset, take);
                offset += take;
                length -= take;
            }
        }

        void outputHash(final byte[] out, final int offset, final int length) {
            // Starting with the Output from the current chunk, compute all the
            // parent chaining values along the right edge of the tree, until we
            // have the root Output.
            Output output = state.output();
            int parentNodesRemaining = stackLen;
            while (parentNodesRemaining-- > 0) {
                final int[] parentCV = cvStack[parentNodesRemaining];
                output = parentOutput(parentCV, output.chainingValue(), key, flags);
            }
            output.rootOutputBytes(out, offset, length);
        }

        void reset() {
            stackLen = 0;
            Arrays.fill(cvStack, null);
            state = new ChunkState(key, 0, flags);
        }

        // Section 5.1.2 of the BLAKE3 spec explains this algorithm in more detail.
        private void addChunkCV(final int[] firstCV, final long totalChunks) {
            // This chunk might complete some subtrees. For each completed subtree,
            // its left child will be the current top entry in the CV stack, and
            // its right child will be the current value of `newCV`. Pop each left
            // child off the stack, merge it with `newCV`, and overwrite `newCV`
            // with the result. After all these merges, push the final value of
            // `newCV` onto the stack. The number of completed subtrees is given
            // by the number of trailing 0-bits in the new total number of chunks.
            int[] newCV = firstCV;
            long chunkCounter = totalChunks;
            while ((chunkCounter & 1) == 0) {
                newCV = parentChainingValue(popCV(), newCV, key, flags);
                chunkCounter >>= 1;
            }
            pushCV(newCV);
        }

        private void pushCV(final int[] cv) {
            cvStack[stackLen++] = cv;
        }

        private int[] popCV() {
            return cvStack[--stackLen];
        }
    }

}
