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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class UnixCryptTest {

    @Test
    public void testCtor() {
        assertNotNull(new UnixCrypt());
    }

    @Test
    public void testUnixCryptStrings() {
        // trivial test
        assertEquals("xxWAum7tHdIUw", Crypt.crypt("secret", "xx"));
        // empty data
        assertEquals("12UFlHxel6uMM", Crypt.crypt("", "12"));
        // salt gets cut at maximum length
        assertEquals("12FJgqDtVOg7Q", Crypt.crypt("secret", "12"));
        assertEquals("12FJgqDtVOg7Q", Crypt.crypt("secret", "12345678"));
    }

    @Test
    public void testUnixCryptBytes() {
        // An empty Bytearray equals an empty String
        assertEquals("12UFlHxel6uMM", Crypt.crypt(new byte[0], "12"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("./287bds2PjVw", Crypt.crypt("t\u00e4st", "./"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("./bLIFNqo9XKQ", Crypt.crypt("t\u00e4st".getBytes(StandardCharsets.ISO_8859_1), "./"));
        assertEquals("./bLIFNqo9XKQ", Crypt.crypt(new byte[]{(byte) 0x74, (byte) 0xe4, (byte) 0x73, (byte) 0x74}, "./"));
    }

    /**
     * Some salts are invalid for crypt(3) but not for unixCrypt().
     */
    @Test
    public void testUnixCryptExplicitCall() {
        // A call to crypt() with an empty salt would result in a "$6$" hash.
        // Using unixCrypt() explicitly results in a random salt.
        assertTrue(UnixCrypt.crypt("secret".getBytes()).matches("^[a-zA-Z0-9./]{13}$"));
        assertTrue(UnixCrypt.crypt("secret".getBytes(), null).matches("^[a-zA-Z0-9./]{13}$"));
    }

    /**
     * Single character salts are illegal!
     * E.g. with glibc 2.13, crypt("secret", "x") = "xxZREZpkHZpkI" but
     * crypt("secret", "xx") = "xxWAum7tHdIUw" which makes it unverifiable.
     */
    @Test
    public void testUnixCryptWithHalfSalt() {
        assertThrows(IllegalArgumentException.class, () -> UnixCrypt.crypt("secret", "x"));
    }

    /**
     * Unimplemented "$foo$" salt prefixes would be treated as UnixCrypt salt.
     */
    @Test
    public void testUnixCryptInvalidSalt() {
        assertThrows(IllegalArgumentException.class, () -> UnixCrypt.crypt("secret", "$a"));
    }

    @Test
    public void testUnixCryptNullData() {
        assertThrows(NullPointerException.class, () -> UnixCrypt.crypt((byte[]) null));
    }

    @Test
    public void testUnixCryptWithEmptySalt() {
        assertThrows(IllegalArgumentException.class, () -> UnixCrypt.crypt("secret", ""));
    }

    @Test
    public void testUnixCryptWithoutSalt() {
        final String hash = UnixCrypt.crypt("foo");
        assertTrue(hash.matches("^[a-zA-Z0-9./]{13}$"));
        final String hash2 = UnixCrypt.crypt("foo");
        assertNotSame(hash, hash2);
    }
}
