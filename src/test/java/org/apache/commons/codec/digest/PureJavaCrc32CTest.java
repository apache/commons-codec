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

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for PureJavaCrc32C.
 * Test data was derived from
 * https://tools.ietf.org/html/rfc3720#appendix-B.4
 */
public class PureJavaCrc32CTest {

    private final PureJavaCrc32C crc = new PureJavaCrc32C();

    private final byte[] data = new byte[32];

    @Test
    public void testZeros() {
        Arrays.fill(data, (byte) 0);
        check(0x8a9136aa); // aa 36 91 8a
    }

    @Test
    public void testOnes() {
        Arrays.fill(data, (byte) 0xFF);
        check(0x62a8ab43); // 43 ab a8 62
    }

    @Test
    public void testIncreasing() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) i;
        }
        check(0x46dd794e); // 4e 79 dd 46
    }

    @Test
    public void testDecreasing() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) (31-i);
        }
        check(0x113fdb5c); // 5c db 3f 11
    }

    // Using int because only want 32 bits
    private void check(final int expected) {
        crc.reset();
        crc.update(data, 0, data.length);
        final int actual = (int) crc.getValue();
        assertEquals(Integer.toHexString(expected), Integer.toHexString(actual));
    }

}
