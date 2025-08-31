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
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.zip.Checksum;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link CRC16}.
 */
class CRC16Test {

    private static final byte[] TEST_BYTES = "123456789".getBytes(StandardCharsets.US_ASCII);
    private static final int TEST_BYTES_LEN = TEST_BYTES.length;

    static Object[][] testCcittDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0x0000 },
            { "1", 0x200A },
            { "12", 0xBDEB },
            { "123", 0x5A78 },
            { "1234", 0x8832 },
            { "12345", 0x7437 },
            { "123456", 0x11FD },
            { "1234567", 0x6947 },
            { "12345678", 0x8B19 },
            { "123456789", 0x2189 }
        };
        // @formatter:on
    }

    static Object[][] testModbusDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0xFFFF },
            { "1", 0x947E },
            { "12", 0xF595 },
            { "123", 0x7A75 },
            { "1234", 0x30BA },
            { "12345", 0xA471 },
            { "123456", 0x32E4 },
            { "1234567", 0x9D73 },
            { "12345678", 0x37DD },
            { "123456789", 0x4B37 }
        };
        // @formatter:on
    }

    private Supplier<String> messageSupplier(final Checksum crc16, final long expected) {
        return () -> String.format("Expected %X, actual %s", expected, crc16);
    }

    void stdUpdate(final Checksum crc16) {
        crc16.update(TEST_BYTES, 0, TEST_BYTES_LEN);
    }

    @ParameterizedTest
    @MethodSource
    void testCcittDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.ccitt());
    }

    @Test
    void testGetTables() {
        assertNotSame(CRC16.getCcittTable(), CRC16.getCcittTable());
        assertNotSame(CRC16.getModbusTable(), CRC16.getModbusTable());
        assertNotSame(CRC16.getCdma2000Table(), CRC16.getCdma2000Table());
    }

    @Test
    void testInit() {
        final Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0xFFFF).get();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
        stdUpdate(crc16);
        assertEquals(0x090A, crc16.getValue());
        crc16.reset();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
    }

    @Test
    void testModbusCustom() {
        final Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0xFFFF).get();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
        stdUpdate(crc16);
        assertEquals(0x090A, crc16.getValue());
        crc16.reset();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
    }

    @ParameterizedTest
    @MethodSource
    void testModbusDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.modbus());
    }

    @Test
    void testReset() {
        final Checksum crc16 = CRC16.modbus();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
        stdUpdate(crc16);
        assertEquals(0x090A, crc16.getValue());
        crc16.reset();
        stdUpdate(crc16);
        assertEquals(0x4B37, crc16.getValue());
    }

    @Test
    void testResetCustomModbus() {
        final Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0x0000).get();
        stdUpdate(crc16);
        assertEquals(0xBB3D, crc16.getValue());
        stdUpdate(crc16);
        assertEquals(0xED7B, crc16.getValue());
        crc16.reset();
        stdUpdate(crc16);
        assertEquals(0xbb3d, crc16.getValue());
    }

    @Test
    void testUpdateArray() {
        final Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0x0000).get();
        stdUpdate(crc16);
        assertEquals(0xBB3D, crc16.getValue());
    }

    @Test
    void testUpdateInt() {
        final Checksum crc16 = CRC16.builder().setTable(CRC16.getModbusTable()).setInit(0x0000).get();
        final byte[] bytes = TEST_BYTES;
        for (final byte element : bytes) {
            crc16.update(element);
        }
        assertEquals(0xBB3D, crc16.getValue());
    }

    void testUpdateReset(final String source, final long expected, final Checksum crc16) {
        final byte[] bytes = StringUtils.getBytesUsAscii(source);
        crc16.update(bytes, 0, bytes.length);
        long actual = crc16.getValue();
        assertEquals(expected, actual, messageSupplier(crc16, expected));
        crc16.reset();
        crc16.update(bytes, 0, bytes.length);
        actual = crc16.getValue();
        assertEquals(expected, actual, messageSupplier(crc16, expected));
    }

}
