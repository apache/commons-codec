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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link CRC16}.
 */
class CRC16Test {

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
            CRC16::getArcTable,
            CRC16::getCcittTable,
            CRC16::getDnpTable,
            CRC16::getMcrf4xxTable,
            CRC16::getMaximTable,
            CRC16::getModbusTable,
            CRC16::getNrsc5Table
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
        testUpdateReset(source, expected, CRC16.arc());
    }

    @ParameterizedTest
    @MethodSource
    void testCcittDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.ccitt());
    }

    @ParameterizedTest
    @MethodSource
    void testDnpDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.dnp());
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
        testUpdateReset(source, expected, CRC16.ibmSdlc());
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

    @ParameterizedTest
    @MethodSource
    void testMaximDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.maxim());
    }

    @ParameterizedTest
    @MethodSource
    void testMcrf4xxDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.mcrf4xx());
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

    @ParameterizedTest
    @MethodSource
    void testNrsc5Default(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.nrsc5());
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

    @ParameterizedTest
    @MethodSource
    void testUsbDefault(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.usb());
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("testDnpDefault")
    void testX(final String source, final long expected) {
        testUpdateReset(source, expected, CRC16.builder().setInit(0xC6C6).setXorOut(0x0000).setTable(
                new int[] {0x0000, 0x1189, 0x2312, 0x329B,   0x4624, 0x57AD, 0x6536, 0x74BF,
                        0x8C48, 0x9DC1, 0xAF5A, 0xBED3,   0xCA6C, 0xDBE5, 0xE97E, 0xF8F7,
                        0x1081, 0x0108, 0x3393, 0x221A,   0x56A5, 0x472C, 0x75B7, 0x643E,
                        0x9CC9, 0x8D40, 0xBFDB, 0xAE52,   0xDAED, 0xCB64, 0xF9FF, 0xE876,
                        0x2102, 0x308B, 0x0210, 0x1399,   0x6726, 0x76AF, 0x4434, 0x55BD,
                        0xAD4A, 0xBCC3, 0x8E58, 0x9FD1,   0xEB6E, 0xFAE7, 0xC87C, 0xD9F5,
                        0x3183, 0x200A, 0x1291, 0x0318,   0x77A7, 0x662E, 0x54B5, 0x453C,
                        0xBDCB, 0xAC42, 0x9ED9, 0x8F50,   0xFBEF, 0xEA66, 0xD8FD, 0xC974,
                        0x4204, 0x538D, 0x6116, 0x709F,   0x0420, 0x15A9, 0x2732, 0x36BB,
                        0xCE4C, 0xDFC5, 0xED5E, 0xFCD7,   0x8868, 0x99E1, 0xAB7A, 0xBAF3,
                        0x5285, 0x430C, 0x7197, 0x601E,   0x14A1, 0x0528, 0x37B3, 0x263A,
                        0xDECD, 0xCF44, 0xFDDF, 0xEC56,   0x98E9, 0x8960, 0xBBFB, 0xAA72,
                        0x6306, 0x728F, 0x4014, 0x519D,   0x2522, 0x34AB, 0x0630, 0x17B9,
                        0xEF4E, 0xFEC7, 0xCC5C, 0xDDD5,   0xA96A, 0xB8E3, 0x8A78, 0x9BF1,
                        0x7387, 0x620E, 0x5095, 0x411C,   0x35A3, 0x242A, 0x16B1, 0x0738,
                        0xFFCF, 0xEE46, 0xDCDD, 0xCD54,   0xB9EB, 0xA862, 0x9AF9, 0x8B70,
                        0x8408, 0x9581, 0xA71A, 0xB693,   0xC22C, 0xD3A5, 0xE13E, 0xF0B7,
                        0x0840, 0x19C9, 0x2B52, 0x3ADB,   0x4E64, 0x5FED, 0x6D76, 0x7CFF,
                        0x9489, 0x8500, 0xB79B, 0xA612,   0xD2AD, 0xC324, 0xF1BF, 0xE036,
                        0x18C1, 0x0948, 0x3BD3, 0x2A5A,   0x5EE5, 0x4F6C, 0x7DF7, 0x6C7E,
                        0xA50A, 0xB483, 0x8618, 0x9791,   0xE32E, 0xF2A7, 0xC03C, 0xD1B5,
                        0x2942, 0x38CB, 0x0A50, 0x1BD9,   0x6F66, 0x7EEF, 0x4C74, 0x5DFD,
                        0xB58B, 0xA402, 0x9699, 0x8710,   0xF3AF, 0xE226, 0xD0BD, 0xC134,
                        0x39C3, 0x284A, 0x1AD1, 0x0B58,   0x7FE7, 0x6E6E, 0x5CF5, 0x4D7C,
                        0xC60C, 0xD785, 0xE51E, 0xF497,   0x8028, 0x91A1, 0xA33A, 0xB2B3,
                        0x4A44, 0x5BCD, 0x6956, 0x78DF,   0x0C60, 0x1DE9, 0x2F72, 0x3EFB,
                        0xD68D, 0xC704, 0xF59F, 0xE416,   0x90A9, 0x8120, 0xB3BB, 0xA232,
                        0x5AC5, 0x4B4C, 0x79D7, 0x685E,   0x1CE1, 0x0D68, 0x3FF3, 0x2E7A,
                        0xE70E, 0xF687, 0xC41C, 0xD595,   0xA12A, 0xB0A3, 0x8238, 0x93B1,
                        0x6B46, 0x7ACF, 0x4854, 0x59DD,   0x2D62, 0x3CEB, 0x0E70, 0x1FF9,
                        0xF78F, 0xE606, 0xD49D, 0xC514,   0xB1AB, 0xA022, 0x92B9, 0x8330,
                        0x7BC7, 0x6A4E, 0x58D5, 0x495C,   0x3DE3, 0x2C6A, 0x1EF1, 0x0F78}
                ).get());
    }
}
