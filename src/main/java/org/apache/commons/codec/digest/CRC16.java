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

import java.util.function.Supplier;
import java.util.zip.Checksum;

/**
 * CRC-16 checksum implementation based on polynomial {@code x<sup>16</spu> + x^15 + x^2 + 1 (0x8005)} and the initial value {@code 0x0000}. This CRC variant is
 * also known as CRC-16-MODBUS.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Cyclic_redundancy_check">Cyclic redundancy check</a>
 * @see <a href="https://reveng.sourceforge.io/crc-catalogue/16.htm">Catalogue of parametrised CRC algorithms with 16 bits</a>
 * @see <a href="https://www.lammertbies.nl/comm/info/crc-calculation">On-line CRC calculation and free library</a>
 * @see <a href="https://crccalc.com/?crc=123456789&method=&datatype=ascii&outtype=hex">crccalc</a>
 * @since 1.20.0
 */
public class CRC16 implements Checksum {

    /**
     * Builds CRC16 instances.
     */
    public static class Builder implements Supplier<CRC16> {

        private int init = DEFAULT_INIT;
        private int[] t = T_DEFAULT;

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        /**
         * Creates a new {@link CRC16} instance.
         */
        @Override
        public CRC16 get() {
            return new CRC16(this);
        }

        /**
         * Sets the initial value.
         *
         * @param init the initial value.
         * @return {@code this} instance.
         */
        public Builder setInit(final int init) {
            this.init = init;
            return this;
        }

        /**
         * Sets the lookup table.
         *
         * @param t the lookup table, making a clone of the input array.
         * @return {@code this} instance.
         */
        public Builder setTable(final int[] t) {
            this.t = t != null ? t.clone() : T_DEFAULT;
            return this;
        }
    }

    private static final int DEFAULT_INIT = 0x0000;
    // @formatter:off
    private static final int[] T_MODBUS = {
        0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
        0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
        0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
        0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
        0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
        0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
    };

    private static final int[] T_DEFAULT = T_MODBUS;

    /**
     * Creates a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new default CRC16.
     *
     * @return a new default CRC16.
     */
    public static CRC16 crc16() {
        return builder().setInit(DEFAULT_INIT).setTable(T_DEFAULT).get();
    }

    /**
     * Creates a new MODBUS CRC16.
     *
     * @return a new MODBUS CRC16.
     */
    public static CRC16 modbus() {
        return builder().setInit(0xFFFF).setTable(T_MODBUS).get();
    }

    // @formatter:on
    /**
     * CRC.
     */
    private int crc;
    private final int init;
    private final int[] t;

    /**
     * Constructs a new instance.
     */
    public CRC16() {
        this(builder());
    }

    /**
     * Constructs a new instance.
     */
    private CRC16(final Builder builder) {
        this.init = builder.init;
        this.crc = builder.init;
        this.t = builder.t;
    }

    /**
     * Gets the init value.
     *
     * @return the init value.
     */
    int getInit() {
        return init;
    }

    @Override
    public long getValue() {
        return crc;
    }

    @Override
    public void reset() {
        crc = init;
    }

    @Override
    public void update(final byte[] b, final int off, final int len) {
        for (int i = 0; i < len; i++) {
            update(b[off + i]);
        }
    }

    @Override
    public void update(final int b) {
        crc = crc >>> 8 ^ t[(crc ^ b) & 0xff];
    }
}
