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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class Md5CryptTest {

    @Test
    public void testCtor() {
        assertNotNull(new Md5Crypt()); // for code-coverage
    }

    @Test
    public void testMd5CryptStrings() {
        // empty data
        assertEquals("$1$foo$9mS5ExwgIECGE5YKlD5o91", Crypt.crypt("", "$1$foo"));
        // salt gets cut at dollar sign
        assertEquals("$1$1234$ImZYBLmYC.rbBKg9ERxX70", Crypt.crypt("secret", "$1$1234"));
        assertEquals("$1$1234$ImZYBLmYC.rbBKg9ERxX70", Crypt.crypt("secret", "$1$1234$567"));
        assertEquals("$1$1234$ImZYBLmYC.rbBKg9ERxX70", Crypt.crypt("secret", "$1$1234$567$890"));
        // salt gets cut at maximum length
        assertEquals("$1$12345678$hj0uLpdidjPhbMMZeno8X/", Crypt.crypt("secret", "$1$1234567890123456"));
        assertEquals("$1$12345678$hj0uLpdidjPhbMMZeno8X/", Crypt.crypt("secret", "$1$123456789012345678"));
    }

    @Test
    public void testMd5CryptBytes() {
        // An empty Bytearray equals an empty String
        assertEquals("$1$foo$9mS5ExwgIECGE5YKlD5o91", Crypt.crypt(new byte[0], "$1$foo"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("$1$./$52agTEQZs877L9jyJnCNZ1", Crypt.crypt("t\u00e4st", "$1$./$"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("$1$./$J2UbKzGe0Cpe63WZAt6p//", Crypt.crypt("t\u00e4st".getBytes(StandardCharsets.ISO_8859_1), "$1$./$"));
    }

    @Test
    public void testMd5CryptExplicitCall() {
        assertTrue(Md5Crypt.md5Crypt("secret".getBytes()).matches("^\\$1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
        assertTrue(Md5Crypt.md5Crypt("secret".getBytes(), (String) null).matches("^\\$1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
    }

    @Test
    public void testMd5CryptExplicitCallWithThreadLocalRandom() {
        final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        assertTrue(Md5Crypt.md5Crypt("secret".getBytes(), threadLocalRandom).matches("^\\$1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
        assertTrue(Md5Crypt.md5Crypt("secret".getBytes(), (String) null).matches("^\\$1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
    }

    @Test
    public void testMd5CryptLongInput() {
        assertEquals("$1$1234$MoxekaNNUgfPRVqoeYjCD/", Crypt.crypt("12345678901234567890", "$1$1234"));
    }

    @Test(expected = NullPointerException.class)
    public void testMd5CryptNullData() {
        Md5Crypt.md5Crypt((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMd5CryptWithEmptySalt() {
        Md5Crypt.md5Crypt("secret".getBytes(), "");
    }
}
