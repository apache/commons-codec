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
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

public class Sha512CryptTest {

    @Test
    public void testSha512CryptStrings() {
        // empty data
        assertEquals("$6$foo$Nywkte7LPWjaJhWjNeGJN.dFdY3pN1wYlGifyRLYOVlGS9EMSiZaDDe/BGSOYQ327q9.32I4UqQ5odsqvsBLX/", Crypt.crypt("", "$6$foo"));
        // salt gets cut at dollar sign
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678"));
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678$012"));
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678$012$456"));
        // salt gets cut at maximum length
        assertEquals("$6$1234567890123456$d2HCAnimIF5VMqUnwaZ/4JhNDJ.ttsjm0nbbmc9eE7xUYiw79GMvXUc5ZqG5BlqkXSbASZxrvR0QefAgdLbeH.", Crypt.crypt("secret", "$6$1234567890123456"));
        assertEquals("$6$1234567890123456$d2HCAnimIF5VMqUnwaZ/4JhNDJ.ttsjm0nbbmc9eE7xUYiw79GMvXUc5ZqG5BlqkXSbASZxrvR0QefAgdLbeH.", Crypt.crypt("secret", "$6$1234567890123456789"));
    }

    @Test
    public void testSha512CryptBytes() {
        // An empty Bytearray equals an empty String
        assertEquals("$6$foo$Nywkte7LPWjaJhWjNeGJN.dFdY3pN1wYlGifyRLYOVlGS9EMSiZaDDe/BGSOYQ327q9.32I4UqQ5odsqvsBLX/", Crypt.crypt(new byte[0], "$6$foo"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("$6$./$fKtWqslQkwI8ZxjdWoeS.jHHrte97bZxiwB5gwCRHX6LG62fUhT6Bb5MRrjWvieh0C/gxh8ItFuTsVy80VrED1", Crypt.crypt("t\u00e4st", "$6$./$"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("$6$./$L49DSK.d2df/LxGLJQMyS5A/Um.TdHqgc46j5FpScEPlqQHP5dEazltaDNDZ6UEs2mmNI6kPwtH/rsP9g5zBI.", Crypt.crypt("t\u00e4st".getBytes(StandardCharsets.ISO_8859_1), "$6$./$"));
    }

    @Test
    public void testSha512CryptExplicitCall() {
        assertTrue(Sha2Crypt.sha512Crypt("secret".getBytes()).matches("^\\$6\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
        assertTrue(Sha2Crypt.sha512Crypt("secret".getBytes(), null).matches("^\\$6\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
    }

    @Test
    public void testSha512CryptExplicitCallThreadLocalRandom() {
        final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        assertTrue(Sha2Crypt.sha512Crypt("secret".getBytes(), null, threadLocalRandom).matches("^\\$6\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
    }

    @Test(expected = NullPointerException.class)
    public void testSha512CryptNullData() {
        Sha2Crypt.sha512Crypt((byte[]) null);
    }

    @Ignore
    public void testSha512CryptNullSalt() {
        // cannot be tested as sha512Crypt() with all params is private and
        // all public methods check for salt==null.
    }

    @Test
    public void testSha2CryptRounds() {
        // minimum rounds?
        assertEquals("$5$rounds=1000$abcd$b8MCU4GEeZIekOy5ahQ8EWfT330hvYGVeDYkBxXBva.", Sha2Crypt.sha256Crypt("secret".getBytes(StandardCharsets.UTF_8), "$5$rounds=50$abcd$"));
        assertEquals("$5$rounds=1001$abcd$SQsJZs7KXKdd2DtklI3TY3tkD7UYA99RD0FBLm4Sk48", Sha2Crypt.sha256Crypt("secret".getBytes(StandardCharsets.UTF_8), "$5$rounds=1001$abcd$"));
        assertEquals("$5$rounds=9999$abcd$Rh/8ngVh9oyuS6lL3.fsq.9xbvXJsfyKWxSjO2mPIa7", Sha2Crypt.sha256Crypt("secret".getBytes(StandardCharsets.UTF_8), "$5$rounds=9999$abcd"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha2CryptWrongSalt() {
        Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "xx");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha512CryptWithEmptySalt() {
        Sha2Crypt.sha512Crypt("secret".getBytes(), "");
    }

    @Test
    public void testSha256LargetThanBlocksize() {
        final byte[] buffer = new byte[200];
        Arrays.fill(buffer, 0, 200, (byte)'A');
        assertEquals("$6$abc$oP/h8PRhCKIA66KSTjGwNsQMSLLZnuFOTjOhrqNrDkKgjTlpePSqibB0qtmDapMbP/zN1cUEYSeHFrpgqZ.GG1", Sha2Crypt.sha512Crypt(buffer, "$6$abc"));
    }
}
