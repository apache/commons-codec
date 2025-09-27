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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.zip.Checksum;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.function.Uncheck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link Crc16}.
 */
class Crc16Test {

    private static final String BIG_TEXT = Uncheck.get(() -> PathUtils.readString(Paths.get("LICENSE.txt"), StandardCharsets.US_ASCII));
    private static final byte[] TEST_BYTES = "123456789".getBytes(StandardCharsets.US_ASCII);
    private static final int TEST_BYTES_LEN = TEST_BYTES.length;

    static Object[][] testArcDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0x0000 },
            { "1", 0xD4C1 },
            { "12", 0x4594 },
            { "123", 0xBA04 },
            { "1234", 0x14BA },
            { "12345", 0xA455 },
            { "123456", 0x29E4 },
            { "1234567", 0x9D68 },
            { "12345678", 0x3C9D },
            { "123456789", 0xBB3D },
            { BIG_TEXT, 0xD01C }
        };
        // @formatter:on
    }

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
            { "123456789", 0x2189 },
            { BIG_TEXT, 0xA912 }
        };
        // @formatter:on
    }

    static Object[][] testDnpDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0xFFFF },
            { "1", 0x5265 },
            { "12", 0x8FC8 },
            { "123", 0x26F7 },
            { "1234", 0x4213 },
            { "12345", 0x711C },
            { "123456", 0x57DE },
            { "1234567", 0xF8F8 },
            { "12345678", 0x182F },
            { "123456789", 0xEA82 },
            { BIG_TEXT, 0x7E33 }
        };
        // @formatter:on
    }

    @SuppressWarnings("unchecked")
    static Supplier<int[]>[] testGetTables() {
        // @formatter:off
        return new Supplier[] {
            Crc16::getArcTable,
            Crc16::getCcittTable,
            Crc16::getDnpTable,
            Crc16::getMcrf4xxTable,
            Crc16::getMaximTable,
            Crc16::getModbusTable,
            Crc16::getNrsc5Table
        };
        // @formatter:on
    }

    static Object[][] testIbmSdlcDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0x0000 },
            { "1", 0xD072 },
            { "12", 0xB2AC },
            { "123", 0x9CB4 },
            { "1234", 0x74EC },
            { "12345", 0xBB40 },
            { "123456", 0xE672 },
            { "1234567", 0xE537 },
            { "12345678", 0x086A },
            { "123456789", 0x906E },
            { BIG_TEXT, 0x6C3F }
        };
        // @formatter:on
    }

    static Object[][] testMaximDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0xFFFF },
            { "1", 0x2B3E },
            { "12", 0xBA6B },
            { "123", 0x45FB },
            { "1234", 0xEB45 },
            { "12345", 0x5BAA },
            { "123456", 0xD61B },
            { "1234567", 0x6297 },
            { "12345678", 0xC362 },
            { "123456789", 0x44C2 },
            { BIG_TEXT, 0x2FE3 }
        };
        // @formatter:on
    }

    static Object[][] testMcrf4xxDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0xFFFF },
            { "1", 0x2F8D },
            { "12", 0x4D53 },
            { "123", 0x634B },
            { "1234", 0x8B13 },
            { "12345", 0x44BF },
            { "123456", 0x198D },
            { "1234567", 0x1AC8 },
            { "12345678", 0xF795 },
            { "123456789", 0x6F91 },
            { BIG_TEXT, 0x93C0 }
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
            { "123456789", 0x4B37 },
            { BIG_TEXT, 0xEC67 }
        };
        // @formatter:on
    }

    static Object[][] testNrsc5Default() {
        // @formatter:off
        return new Object[][] {
            { "", 0xFFFF },
            { "1", 0x083E },
            { "12", 0xDB99 },
            { "123", 0x8286 },
            { "1234", 0x95DC },
            { "12345", 0xB42C },
            { "123456", 0x7CFF },
            { "1234567", 0xB565 },
            { "12345678", 0x9C8A },
            { "123456789", 0xA066 },
            { BIG_TEXT, 0x2A84 }
        };
        // @formatter:on
    }

    static Object[][] testUsbDefault() {
        // @formatter:off
        return new Object[][] {
            { "", 0x0000 },
            { "1", 0x6B81 },
            { "12", 0x0A6A },
            { "123", 0x858A },
            { "1234", 0xCF45 },
            { "12345", 0x5B8E },
            { "123456", 0xCD1B },
            { "1234567", 0x628C },
            { "12345678", 0xC822 },
            { "123456789", 0xB4C8 },
            { BIG_TEXT, 0x1398 }
        };
        // @formatter:on
    }

    private Supplier<String> messageSupplier(final Checksum crc16, final long expected) {
        return () -> String.format("Expected %04X but was %s", expected, crc16);
    }

    void stdUpdate(final Checksum crc16) {
        crc16.update(TEST_BYTES, 0, TEST_BYTES_LEN);
    }

    @ParameterizedTest
    @MethodSource
    void testArcDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.arc());
    }

    @ParameterizedTest
    @MethodSource
    void testCcittDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.ccitt());
    }

    @ParameterizedTest
    @MethodSource
    void testDnpDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.dnp());
    }

    @ParameterizedTest
    @MethodSource
    void testGetTables(final Supplier<int[]> supplier) {
        final int[] value1 = supplier.get();
        final int[] value2 = supplier.get();
        assertNotSame(value1, value2);
        assertArrayEquals(value1, value2);
    }

    @ParameterizedTest
    @MethodSource
    void testIbmSdlcDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.ibmSdlc());
    }

    @Test
    void testInit() {
        final Checksum crc16 = Crc16.builder().setTable(Crc16.getModbusTable()).setInit(0xFFFF).get();
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
    void testMaximDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.maxim());
    }

    @ParameterizedTest
    @MethodSource
    void testMcrf4xxDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.mcrf4xx());
    }

    @Test
    void testModbusCustom() {
        final Checksum crc16 = Crc16.builder().setTable(Crc16.getModbusTable()).setInit(0xFFFF).get();
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
        testUpdateReset(source, expected, Crc16.modbus());
    }

    @ParameterizedTest
    @MethodSource
    void testNrsc5Default(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.nrsc5());
    }

    @Test
    void testReset() {
        final Checksum crc16 = Crc16.modbus();
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
        final Checksum crc16 = Crc16.builder().setTable(Crc16.getModbusTable()).setInit(0x0000).get();
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
        final Checksum crc16 = Crc16.builder().setTable(Crc16.getModbusTable()).setInit(0x0000).get();
        stdUpdate(crc16);
        assertEquals(0xBB3D, crc16.getValue());
    }

    @Test
    void testUpdateInt() {
        final Checksum crc16 = Crc16.builder().setTable(Crc16.getModbusTable()).setInit(0x0000).get();
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

    @ParameterizedTest
    @MethodSource
    void testUsbDefault(final String source, final long expected) {
        testUpdateReset(source, expected, Crc16.usb());
    }

}
