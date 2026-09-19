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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Sha2CryptTest {

    @Test
    void testCtor() {
        assertNotNull(new Sha2Crypt());
    }

    @ParameterizedTest
    @ValueSource(ints = { 100_000, 1_000_000 })
    void testLargeRounds(final int rounds) {
        final String salt = "$6$rounds=" + rounds + "$abcdefghijklmnop";
        Crypt.crypt("anything".getBytes(StandardCharsets.UTF_8), salt);
    }

    @Test
    void testRoundsAboveCeilingRejected() {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=1000001$abcdefghijklmnop"));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=999999999$abcdefghijklmnop"));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=99999999999$abcdefghijklmnop"));
    }

    @Test
    void testRoundsCeilingOverride() {
        final String previous = System.getProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY);
        System.setProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY, "2000000");
        try {
            assertNotNull(Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=2000000$abcdefghijklmnop"));
        } finally {
            if (previous == null) {
                System.clearProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY);
            } else {
                System.setProperty(Sha2Crypt.ROUNDS_MAX_PROPERTY, previous);
            }
        }
    }

    @Test
    void testRoundsLeadingZeroes() {
        final String expected = Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=1000$abcdefghijklmnop");
        final String actual = Sha2Crypt.sha512Crypt("secret".getBytes(StandardCharsets.UTF_8), "$6$rounds=0000001000$abcdefghijklmnop");
        assertEquals(expected, actual);
    }
}
