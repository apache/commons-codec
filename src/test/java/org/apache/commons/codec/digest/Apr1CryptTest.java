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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.Charsets;
import org.junit.Test;

public class Apr1CryptTest {

    @Test
    public void testApr1CryptStrings() {
        // A random example using htpasswd
        assertEquals("$apr1$TqI9WECO$LHZB2DqRlk9nObiB6vJG9.", Md5Crypt.apr1Crypt("secret", "$apr1$TqI9WECO"));
        // empty data
        assertEquals("$apr1$foo$P27KyD1htb4EllIPEYhqi0", Md5Crypt.apr1Crypt("", "$apr1$foo"));
        // salt gets cut at dollar sign
        assertEquals("$apr1$1234$mAlH7FRST6FiRZ.kcYL.j1", Md5Crypt.apr1Crypt("secret", "$apr1$1234"));
        assertEquals("$apr1$1234$mAlH7FRST6FiRZ.kcYL.j1", Md5Crypt.apr1Crypt("secret", "$apr1$1234$567"));
        assertEquals("$apr1$1234$mAlH7FRST6FiRZ.kcYL.j1", Md5Crypt.apr1Crypt("secret", "$apr1$1234$567$890"));
        // salt gets cut at maximum length
        assertEquals("$apr1$12345678$0lqb/6VUFP8JY/s/jTrIk0", Md5Crypt.apr1Crypt("secret", "$apr1$1234567890123456"));
        assertEquals("$apr1$12345678$0lqb/6VUFP8JY/s/jTrIk0", Md5Crypt.apr1Crypt("secret", "$apr1$123456789012345678"));
    }

    @Test
    public void testApr1CryptBytes() {
        // random salt
        final byte[] keyBytes = new byte[] { '!', 'b', 'c', '.' };
        final String hash = Md5Crypt.apr1Crypt(keyBytes);
        assertEquals(hash, Md5Crypt.apr1Crypt("!bc.", hash));

        // An empty Bytearray equals an empty String
        assertEquals("$apr1$foo$P27KyD1htb4EllIPEYhqi0", Md5Crypt.apr1Crypt(new byte[0], "$apr1$foo"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("$apr1$./$EeFrYzWWbmTyGdf4xULYc.", Md5Crypt.apr1Crypt("t\u00e4st", "$apr1$./$"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("$apr1$./$kCwT1pY9qXAJElYG9q1QE1", Md5Crypt.apr1Crypt("t\u00e4st".getBytes(Charsets.ISO_8859_1), "$apr1$./$"));
    }

    @Test
    public void testApr1CryptExplicitCall() {
        // When explicitly called the prefix is optional
        assertEquals("$apr1$1234$mAlH7FRST6FiRZ.kcYL.j1", Md5Crypt.apr1Crypt("secret", "1234"));
        // When explicitly called without salt, a random one will be used.
        assertTrue(Md5Crypt.apr1Crypt("secret".getBytes()).matches("^\\$apr1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
        assertTrue(Md5Crypt.apr1Crypt("secret".getBytes(), null).matches("^\\$apr1\\$[a-zA-Z0-9./]{0,8}\\$.{1,}$"));
    }

    @Test
    public void testApr1LongSalt() {
        assertEquals("$apr1$12345678$0lqb/6VUFP8JY/s/jTrIk0", Md5Crypt.apr1Crypt("secret", "12345678901234567890"));
    }

    @Test(expected = NullPointerException.class)
    public void testApr1CryptNullData() {
        Md5Crypt.apr1Crypt((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApr1CryptWithEmptySalt() {
        Md5Crypt.apr1Crypt("secret".getBytes(), "");
    }

    @Test
    public void testApr1CryptWithoutSalt() {
        // Without salt, a random is generated
        final String hash = Md5Crypt.apr1Crypt("secret");
        assertTrue(hash.matches("^\\$apr1\\$[a-zA-Z0-9\\./]{8}\\$[a-zA-Z0-9\\./]{22}$"));
        final String hash2 = Md5Crypt.apr1Crypt("secret");
        assertNotSame(hash, hash2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApr1CryptWithInvalidSalt() {
        Md5Crypt.apr1Crypt(new byte[0], "!");
    }
}
