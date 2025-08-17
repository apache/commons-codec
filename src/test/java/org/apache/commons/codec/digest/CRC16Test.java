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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.zip.Checksum;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CRC16}.
 */
class CRC16Test {

    private static final byte[] BYTES_123456789 = "123456789".getBytes(StandardCharsets.US_ASCII);

    @Test
    void testDefault() {
        final Checksum crc16 = CRC16.crc16();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xBB3D, crc16.getValue());
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xED7B, crc16.getValue());
        crc16.reset();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xBB3D, crc16.getValue());
    }

    @Test
    void testInit() {
        final Checksum crc16 = CRC16.builder().setInit(0xFFFF).get();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x4B37, crc16.getValue());
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x090A, crc16.getValue());
        crc16.reset();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x4B37, crc16.getValue());
    }

    @Test
    void testModbus() {
        final Checksum crc16 = CRC16.modbus();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x4B37, crc16.getValue());
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x090A, crc16.getValue());
        crc16.reset();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0x4B37, crc16.getValue());
    }

    @Test
    void testReset() {
        final Checksum crc16 = new CRC16();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xBB3D, crc16.getValue());
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xED7B, crc16.getValue());
        crc16.reset();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xbb3d, crc16.getValue());
    }

    @Test
    void testUpdateArray() {
        final Checksum crc16 = new CRC16();
        crc16.update(BYTES_123456789, 0, 9);
        assertEquals(0xBB3D, crc16.getValue());
    }

    @Test
    void testUpdateInt() {
        final Checksum crc16 = new CRC16();
        final byte[] bytes = BYTES_123456789;
        for (final byte element : bytes) {
            crc16.update(element);
        }
        assertEquals(0xBB3D, crc16.getValue());
    }
}
