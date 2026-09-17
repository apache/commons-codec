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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Blake3Test {
    private static void assertThrowsProperExceptionWithKeySize(final int keySize) {
        assertThrows(IllegalArgumentException.class, () -> Blake3.initKeyedHash(new byte[keySize]), "Blake3 keys must be 32 bytes");
    }

    private static byte[] input(final int length) {
        final byte[] input = new byte[length];
        for (int i = 0; i < length; i++) {
            input[i] = (byte) (i % 251);
        }
        return input;
    }

    private static Blake3[] newHashers() {
        return new Blake3[] {
            Blake3.initHash(),
            Blake3.initKeyedHash("whats the Elvish word for friend".getBytes(StandardCharsets.UTF_8)),
            Blake3.initKeyDerivationFunction("org.apache.commons.codec.digest.Blake3Test".getBytes(StandardCharsets.UTF_8))
        };
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 63, 64, 65, 1023, 1024, 1025, 2048, 3073})
    void testFinalizeIsIdempotent(final int inputLength) {
        for (final Blake3 hasher : newHashers()) {
            hasher.update(input(inputLength));
            final byte[] expected = hasher.doFinalize(131);
            assertArrayEquals(expected, hasher.doFinalize(131));
            assertArrayEquals(Arrays.copyOf(expected, 32), hasher.doFinalize(32));
            assertArrayEquals(new byte[0], hasher.doFinalize(0));
            final byte[] actual = new byte[131];
            hasher.doFinalize(actual);
            assertArrayEquals(expected, actual);
            final byte[] destination = new byte[137];
            Arrays.fill(destination, (byte) 0x5a);
            final byte[] expectedDestination = destination.clone();
            System.arraycopy(expected, 0, expectedDestination, 3, expected.length);
            hasher.doFinalize(destination, 3, expected.length);
            assertArrayEquals(expectedDestination, destination);
            assertArrayEquals(expected, hasher.doFinalize(131));
        }
    }

    @Test
    void testKdfExampleDerivesDistinctKeys() {
        final Blake3 kdf = Blake3.initKeyDerivationFunction("org.apache.commons.codec.digest.Blake3Example".getBytes(StandardCharsets.UTF_8));
        kdf.update("shared secret".getBytes(StandardCharsets.UTF_8));
        kdf.update("sender".getBytes(StandardCharsets.UTF_8));
        kdf.update("recipient".getBytes(StandardCharsets.UTF_8));
        final byte[] keys = kdf.doFinalize(64);
        final byte[] txKey = Arrays.copyOfRange(keys, 0, 32);
        final byte[] rxKey = Arrays.copyOfRange(keys, 32, 64);
        assertFalse(Arrays.equals(txKey, rxKey));
        assertArrayEquals(txKey, kdf.doFinalize(32));
        assertArrayEquals(txKey, kdf.doFinalize(32));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 63, 64, 65, 1023, 1024, 1025, 2048, 3073})
    void testResetAfterFinalize(final int inputLength) {
        final Blake3[] hashers = newHashers();
        final Blake3[] freshHashers = newHashers();
        final byte[] message = input(inputLength);
        for (int i = 0; i < hashers.length; i++) {
            final Blake3 hasher = hashers[i];
            final byte[] expected = hasher.update(message).doFinalize(131);
            assertArrayEquals(freshHashers[i].doFinalize(131), hasher.reset().doFinalize(131));
            assertArrayEquals(expected, hasher.update(message).doFinalize(131));
        }
    }

    @Test
    void testShouldThrowIllegalArgumentExceptionWhenIncorrectKeySize() {
        for (int i = 0; i < 32; i++) {
            assertThrowsProperExceptionWithKeySize(i);
        }
        assertThrowsProperExceptionWithKeySize(33);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 63, 64, 65, 1023, 1024, 1025, 2048, 3073})
    void testUpdateAfterFinalize(final int inputLength) {
        final byte[] message = input(inputLength + 1025);
        final Blake3[] hashers = newHashers();
        final Blake3[] freshHashers = newHashers();
        for (int i = 0; i < hashers.length; i++) {
            final Blake3 hasher = hashers[i];
            hasher.update(message, 0, inputLength).doFinalize(131);
            hasher.update(message, inputLength, message.length - inputLength);
            final byte[] expected = freshHashers[i].update(message).doFinalize(131);
            assertArrayEquals(expected, hasher.doFinalize(131));
            assertArrayEquals(expected, hasher.doFinalize(131));
        }
    }
}
