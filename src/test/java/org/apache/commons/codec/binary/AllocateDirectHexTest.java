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

package org.apache.commons.codec.binary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Test;

class AllocateDirectHexTest extends HexTest {

    @Override
    protected ByteBuffer allocate(final int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    @Test
    void testAllocateProducesDirectByteBufferWithoutArray() {
        final ByteBuffer directBuffer = allocate(8);
        assertTrue(directBuffer.isDirect());
        assertFalse(directBuffer.hasArray());
    }

    @Test
    void testEncodeDecodeDirectByteBufferWithPositionAndLimit() throws DecoderException {
        final ByteBuffer encodeInput = allocate(4);
        encodeInput.put((byte) 0x0A);
        encodeInput.put((byte) 0x0B);
        encodeInput.put((byte) 0x0C);
        encodeInput.put((byte) 0x0D);
        encodeInput.position(1);
        encodeInput.limit(3);

        assertEquals("0b0c", Hex.encodeHexString(encodeInput));
        assertEquals(0, encodeInput.remaining());

        final ByteBuffer decodeInput = allocate(4);
        decodeInput.put((byte) '0');
        decodeInput.put((byte) 'b');
        decodeInput.put((byte) '0');
        decodeInput.put((byte) 'c');
        decodeInput.flip();

        assertArrayEquals(new byte[] { 0x0B, 0x0C }, new Hex().decode(decodeInput));
        assertEquals(0, decodeInput.remaining());
    }
}
