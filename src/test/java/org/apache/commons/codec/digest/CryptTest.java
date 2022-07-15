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

import static org.junit.jupiter.api.Assertions.*;

public class CryptTest {

    @Test
    public void testCrypt() {
        assertNotNull(new Crypt()); // just for Test Coverage
    }

    @Test
    public void testDefaultCryptVariant() {
        // If salt is null or completely omitted, a random "$6$" is used.
        assertTrue(Crypt.crypt("secret").startsWith("$6$"));
        assertTrue(Crypt.crypt("secret", null).startsWith("$6$"));
    }

    @Test
    public void testCryptWithBytes() {
        final byte[] keyBytes = { 'b', 'y', 't', 'e' };
        final String hash = Crypt.crypt(keyBytes);
        assertEquals(hash, Crypt.crypt("byte", hash));
    }

    /**
     * An empty string as salt is invalid.
     *
     * The C and Perl implementations return an empty string, PHP threads it
     * as NULL. Our implementation should throw an Exception as any resulting
     * hash would not be verifiable with other implementations of crypt().
     */
    @Test
    public void testCryptWithEmptySalt() {
        assertThrows(IllegalArgumentException.class, () -> Crypt.crypt("secret", ""));
    }

}
