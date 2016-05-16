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

import java.util.Arrays;

import org.apache.commons.codec.Charsets;
import org.junit.Test;

public class Sha256CryptTest {

    @Test
    public void testSha256CryptStrings() {
        // empty data
        assertEquals("$5$foo$Fq9CX624QIfnCAmlGiPKLlAasdacKCRxZztPoeo7o0B", Crypt.crypt("", "$5$foo"));
        // salt gets cut at dollar sign
        assertEquals("$5$45678$LulJuUIJIn.1uU.KPV9x92umMYFopzVDD.o2ZqA1i2/", Crypt.crypt("secret", "$5$45678"));
        assertEquals("$5$45678$LulJuUIJIn.1uU.KPV9x92umMYFopzVDD.o2ZqA1i2/", Crypt.crypt("secret", "$5$45678$012"));
        assertEquals("$5$45678$LulJuUIJIn.1uU.KPV9x92umMYFopzVDD.o2ZqA1i2/", Crypt.crypt("secret", "$5$45678$012$456"));
        // salt gets cut at maximum length
        assertEquals("$5$1234567890123456$GUiFKBSTUAGvcK772ulTDPltkTOLtFvPOmp9o.9FNPB", Crypt.crypt("secret", "$5$1234567890123456"));
        assertEquals("$5$1234567890123456$GUiFKBSTUAGvcK772ulTDPltkTOLtFvPOmp9o.9FNPB", Crypt.crypt("secret", "$5$1234567890123456789"));
    }

    @Test
    public void testSha256CryptBytes() {
        // An empty Bytearray equals an empty String
        assertEquals("$5$foo$Fq9CX624QIfnCAmlGiPKLlAasdacKCRxZztPoeo7o0B", Crypt.crypt(new byte[0], "$5$foo"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("$5$./$iH66LwY5sTDTdHeOxq5nvNDVAxuoCcyH/y6Ptte82P8", Crypt.crypt("t\u00e4st", "$5$./$"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("$5$./$qx5gFfCzjuWUOvsDDy.5Nor3UULPIqLVBZhgGNS0c14", Crypt.crypt("t\u00e4st".getBytes(Charsets.ISO_8859_1), "$5$./$"));
    }

    @Test
    public void testSha2CryptRounds() {
        // minimum rounds?
        assertEquals("$5$rounds=1000$abcd$b8MCU4GEeZIekOy5ahQ8EWfT330hvYGVeDYkBxXBva.", Sha2Crypt.sha256Crypt("secret".getBytes(Charsets.UTF_8), "$5$rounds=50$abcd$"));
        assertEquals("$5$rounds=1001$abcd$SQsJZs7KXKdd2DtklI3TY3tkD7UYA99RD0FBLm4Sk48", Sha2Crypt.sha256Crypt("secret".getBytes(Charsets.UTF_8), "$5$rounds=1001$abcd$"));
        assertEquals("$5$rounds=9999$abcd$Rh/8ngVh9oyuS6lL3.fsq.9xbvXJsfyKWxSjO2mPIa7", Sha2Crypt.sha256Crypt("secret".getBytes(Charsets.UTF_8), "$5$rounds=9999$abcd"));
    }

    @Test
    public void testSha256CryptExplicitCall() {
        assertTrue(Sha2Crypt.sha256Crypt("secret".getBytes()).matches("^\\$5\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
        assertTrue(Sha2Crypt.sha256Crypt("secret".getBytes(), null).matches("^\\$5\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
    }

    @Test(expected = NullPointerException.class)
    public void testSha256CryptNullData() {
        Sha2Crypt.sha256Crypt((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha256CryptWithEmptySalt() {
        Sha2Crypt.sha256Crypt("secret".getBytes(), "");
    }

    @Test
    public void testSha256LargetThanBlocksize() {
        final byte[] buffer = new byte[200];
        Arrays.fill(buffer, 0, 200, (byte)'A');
        assertEquals("$5$abc$HbF3RRc15OwNKB/RZZ5F.1I6zsLcKXHQoSdB9Owx/Q8", Sha2Crypt.sha256Crypt(buffer, "$5$abc"));
    }
}
