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
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

class Sha2CryptKeyLengthTest {

    private static final String SHA256_SALT = "$5$abcdefghijklmnop";

    private static final String SHA512_SALT = "$6$abcdefghijklmnop";

    @Test
    void testKeyLengthAboveCeilingRejectedByCrypt() {
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt(new byte[4097], SHA256_SALT));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt(new byte[4097], SHA512_SALT));
        assertThrowsExactly(IllegalArgumentException.class, () -> Crypt.crypt(new byte[4097]));
    }

    @Test
    void testKeyLengthAboveCeilingRejectedBySha2Crypt() {
        assertThrowsExactly(IllegalArgumentException.class, () -> Sha2Crypt.sha256Crypt(new byte[4097], SHA256_SALT));
        assertThrowsExactly(IllegalArgumentException.class, () -> Sha2Crypt.sha512Crypt(new byte[4097], SHA512_SALT));
    }

    @Test
    void testKeyLengthAtCeilingAccepted() {
        assertNotNull(Sha2Crypt.sha256Crypt(new byte[4096], SHA256_SALT));
        assertNotNull(Sha2Crypt.sha512Crypt(new byte[4096], SHA512_SALT));
    }

    @Test
    @SetSystemProperty(key = Sha2Crypt.KEY_MAX_PROPERTY, value = "8192")
    void testKeyLengthCeilingOverrideRestoresPreviousValue() {
        assertNotNull(Sha2Crypt.sha512Crypt(new byte[8192], SHA512_SALT));
    }
}
