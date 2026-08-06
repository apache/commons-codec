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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @ValueSource(ints = { 100_000, 1_000_000, 5_000_000 /*, 50_000_000*/ })
    void testLargeRounds(final int rounds) {
        final String salt = "$6$rounds=" + rounds + "$abcdefghijklmnop";
        final long t = System.nanoTime();
        Crypt.crypt("anything".getBytes(StandardCharsets.UTF_8), salt);
        Crypt.crypt("anything".getBytes(StandardCharsets.UTF_8), "$6$rounds=5000000$abcdefghijklmnop");
        // Full effect (WARNING: ~2 min):
        // Crypt.crypt("anything".getBytes(), "$6$rounds=999999999$abcdefghijklmnop");
    }
}
