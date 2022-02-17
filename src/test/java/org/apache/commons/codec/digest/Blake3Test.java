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

public class Blake3Test {
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenIncorrectKeySize() {
        for (int i = 0; i < 32; i++) {
            assertThrowsProperExceptionWithKeySize(i);
        }
        assertThrowsProperExceptionWithKeySize(33);
    }

    private static void assertThrowsProperExceptionWithKeySize(final int keySize) {
        assertThrows(IllegalArgumentException.class, () -> Blake3.initKeyedHash(new byte[keySize]), "Blake3 keys must be 32 bytes");
    }
}
