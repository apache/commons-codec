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

import org.apache.commons.codec.binary.BaseNCodec.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class BaseNCodecTest {

    BaseNCodec codec;

    @BeforeEach
    public void setUp() {
        codec = new BaseNCodec(0, 0, 0, 0) {
            @Override
            protected boolean isInAlphabet(final byte b) {
                return b=='O' || b == 'K'; // allow OK
            }

            @Override
            void encode(final byte[] pArray, final int i, final int length, final Context context) {
            }

            @Override
            void decode(final byte[] pArray, final int i, final int length, final Context context) {
            }
        };
    }

    /**
     * Test the Context string representation has debugging info.
     * This is not part of the API and the test should be changed if the string
     * format is updated.
     */
    @Test
    public void testContextToString() {
        final Context context = new Context();
        context.buffer = new byte[3];
        context.currentLinePos = 13;
        context.eof = true;
        context.ibitWorkArea = 777;
        context.lbitWorkArea = 999;
        context.modulus = 5;
        context.pos = 42;
        context.readPos = 981;
        final String text = context.toString();
        assertTrue(text.contains("[0, 0, 0]"));
        assertTrue(text.contains("13"));
        assertTrue(text.contains("true"));
        assertTrue(text.contains("777"));
        assertTrue(text.contains("999"));
        assertTrue(text.contains("5"));
        assertTrue(text.contains("42"));
        assertTrue(text.contains("981"));
    }

    @Test
    public void testBaseNCodec() {
        assertNotNull(codec);
    }

//    @Test
//    public void testHasData() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testAvail() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEnsureBufferSize() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testReadResults() {
//        fail("Not yet implemented");
//    }
//
    @Test
    public void testIsWhiteSpace() {
        assertTrue(BaseNCodec.isWhiteSpace((byte) ' '));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\n'));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\r'));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\t'));
    }
//
//    @Test
//    public void testEncodeObject() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEncodeToString() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testDecodeObject() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testDecodeString() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testDecodeByteArray() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEncodeByteArray() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEncodeAsString() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEncodeByteArrayIntInt() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testDecodeByteArrayIntInt() {
//        fail("Not yet implemented");
//    }
//
    @Test
    public void testIsInAlphabetByte() {
        assertFalse(codec.isInAlphabet((byte) 0));
        assertFalse(codec.isInAlphabet((byte) 'a'));
        assertTrue(codec.isInAlphabet((byte) 'O'));
        assertTrue(codec.isInAlphabet((byte) 'K'));
    }

    @Test
    public void testIsInAlphabetByteArrayBoolean() {
        assertTrue(codec.isInAlphabet(new byte[]{}, false));
        assertTrue(codec.isInAlphabet(new byte[]{'O'}, false));
        assertFalse(codec.isInAlphabet(new byte[]{'O',' '}, false));
        assertFalse(codec.isInAlphabet(new byte[]{' '}, false));
        assertTrue(codec.isInAlphabet(new byte[]{}, true));
        assertTrue(codec.isInAlphabet(new byte[]{'O'}, true));
        assertTrue(codec.isInAlphabet(new byte[]{'O',' '}, true));
        assertTrue(codec.isInAlphabet(new byte[]{' '}, true));
    }

    @Test
    public void testIsInAlphabetString() {
        assertTrue(codec.isInAlphabet("OK"));
        assertTrue(codec.isInAlphabet("O=K= \t\n\r"));
    }

    @Test
    public void testContainsAlphabetOrPad() {
        assertFalse(codec.containsAlphabetOrPad(null));
        assertFalse(codec.containsAlphabetOrPad(new byte[]{}));
        assertTrue(codec.containsAlphabetOrPad("OK".getBytes()));
        assertTrue(codec.containsAlphabetOrPad("OK ".getBytes()));
        assertFalse(codec.containsAlphabetOrPad("ok ".getBytes()));
        assertTrue(codec.containsAlphabetOrPad(new byte[]{codec.pad}));
    }

//    @Test
//    public void testGetEncodedLength() {
//        fail("Not yet implemented");
//    }

    @Test
    public void testProvidePaddingByte() {
        // Given
        codec = new BaseNCodec(0, 0, 0, 0, (byte)0x25) {
            @Override
            protected boolean isInAlphabet(final byte b) {
                return b=='O' || b == 'K'; // allow OK
            }

            @Override
            void encode(final byte[] pArray, final int i, final int length, final Context context) {
            }

            @Override
            void decode(final byte[] pArray, final int i, final int length, final Context context) {
            }
        };

        // When
        final byte actualPaddingByte = codec.pad;

        // Then
        assertEquals(0x25, actualPaddingByte);
    }

    @Test
    public void testEnsureBufferSize() {
        final BaseNCodec ncodec = new NoOpBaseNCodec();
        final Context context = new Context();
        assertNull(context.buffer, "Initial buffer should be null");

        // Test initialization
        context.pos = 76979;
        context.readPos = 273;
        ncodec.ensureBufferSize(0, context);
        assertNotNull(context.buffer, "buffer should be initialized");
        assertEquals(ncodec.getDefaultBufferSize(), context.buffer.length, "buffer should be initialized to default size");
        assertEquals(0, context.pos, "context position");
        assertEquals(0, context.readPos, "context read position");

        // Test when no expansion is required
        ncodec.ensureBufferSize(1, context);
        assertEquals(ncodec.getDefaultBufferSize(), context.buffer.length, "buffer should not expand unless required");

        // Test expansion
        int length = context.buffer.length;
        context.pos = length;
        int extra = 1;
        ncodec.ensureBufferSize(extra, context);
        assertTrue(context.buffer.length >= length + extra, "buffer should expand");

        // Test expansion beyond double the buffer size.
        // Hits the edge case where the required capacity is more than the default expansion.
        length = context.buffer.length;
        context.pos = length;
        extra = length * 10;
        ncodec.ensureBufferSize(extra, context);
        assertTrue(context.buffer.length >= length + extra, "buffer should expand beyond double capacity");
    }

    /**
     * Test to expand to the max buffer size.
     */
    @Test
    public void testEnsureBufferSizeExpandsToMaxBufferSize() {
        assertEnsureBufferSizeExpandsToMaxBufferSize(false);
    }

    /**
     * Test to expand to beyond the max buffer size.
     *
     * <p>Note: If the buffer is required to expand to above the max buffer size it may not work
     * on all VMs and may have to be annotated with @Ignore.</p>
     */
    @Test
    public void testEnsureBufferSizeExpandsToBeyondMaxBufferSize() {
        assertEnsureBufferSizeExpandsToMaxBufferSize(true);
    }

    private static void assertEnsureBufferSizeExpandsToMaxBufferSize(final boolean exceedMaxBufferSize) {
        // This test is memory hungry.
        // By default expansion will double the buffer size.
        // Using a buffer that must be doubled to get close to 2GiB requires at least 3GiB
        // of memory for the test (1GiB existing + 2GiB new).
        // As a compromise we use an empty buffer and rely on the expansion switching
        // to the minimum required capacity if doubling is not enough.

        // To effectively use a full buffer of ~1GiB change the following for: 1 << 30.
        // Setting to zero has the lowest memory footprint for this test.
        final int length = 0;

        final long presumableFreeMemory = getPresumableFreeMemory();
        // 2GiB + 32 KiB + length
        // 2GiB: Buffer to allocate
        // 32KiB: Some headroom
        // length: Existing buffer
        final long estimatedMemory = (1L << 31) + 32 * 1024 + length;
        assumeTrue(presumableFreeMemory > estimatedMemory, "Not enough free memory for the test");

        final int max = Integer.MAX_VALUE - 8;

        // Check the conservative maximum buffer size can actually be exceeded by the VM
        // otherwise the test is not valid.
        if (exceedMaxBufferSize) {
            assumeCanAllocateBufferSize(max + 1);
            // Free-memory.
            // This may not be necessary as the byte[] is now out of scope
            System.gc();
        }

        final BaseNCodec ncodec = new NoOpBaseNCodec();
        final Context context = new Context();

        // Allocate the initial buffer
        context.buffer = new byte[length];
        context.pos = length;
        // Compute the extra to reach or exceed the max buffer size
        int extra = max - length;
        if (exceedMaxBufferSize) {
            extra++;
        }
        ncodec.ensureBufferSize(extra, context);
        assertTrue(context.buffer.length >= length + extra);
    }

    /**
     * Verify this VM can allocate the given size byte array. Otherwise skip the test.
     */
    private static void assumeCanAllocateBufferSize(final int size) {
        byte[] bytes = null;
        try {
            bytes = new byte[size];
        } catch (final OutOfMemoryError ignore) {
            // ignore
        }
        assumeTrue(bytes != null, "Cannot allocate array of size: " + size);
    }

    /**
     * Gets the presumable free memory; an estimate of the amount of memory that could be allocated.
     *
     * <p>This performs a garbage clean-up and the obtains the presumed amount of free memory
     * that can be allocated in this VM. This is computed as:<p>
     *
     * <pre>
     * System.gc();
     * long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
     * long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
     * </pre>
     *
     * @return the presumable free memory
     * @see <a href="https://stackoverflow.com/a/18366283">
     *     Christian Fries StackOverflow answer on Java available memory</a>
     */
    static long getPresumableFreeMemory() {
        System.gc();
        final long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return Runtime.getRuntime().maxMemory() - allocatedMemory;
    }

    @Test
    public void testEnsureBufferSizeThrowsOnOverflow() {
        final BaseNCodec ncodec = new NoOpBaseNCodec();
        final Context context = new Context();

        final int length = 10;
        context.buffer = new byte[length];
        context.pos = length;
        final int extra = Integer.MAX_VALUE;
        assertThrows(OutOfMemoryError.class, () -> ncodec.ensureBufferSize(extra, context));
    }

    /**
     * Extend BaseNCodec without implementation (no operations = NoOp).
     * Used for testing the memory allocation in {@link BaseNCodec#ensureBufferSize(int, Context)}.
     */
    private static class NoOpBaseNCodec extends BaseNCodec {
        NoOpBaseNCodec() {
            super(0, 0, 0, 0);
        }

        @Override
        void encode(final byte[] pArray, final int i, final int length, final Context context) {
        }

        @Override
        void decode(final byte[] pArray, final int i, final int length, final Context context) {
        }

        @Override
        protected boolean isInAlphabet(final byte value) {
            return false;
        }
    }
}
