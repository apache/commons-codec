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

package org.apache.commons.codec.cli;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Digest}.
 */
class DigestTest {

    @Test
    void testAllAlgorithmsUseTheSameStandardInput() throws Exception {
        final InputStream originalIn = System.in;
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
            Digest.main(new String[] { "ALL" });
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        final String output = captured.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("MD2 da853b0d3f88d99b30283a69e6ded6bb"), output);
        assertTrue(output.contains("MD5 900150983cd24fb0d6963f7d28e17f72"), output);
        assertTrue(output.contains("SHA-1 a9993e364706816aba3e25717850c26c9cd0d89d"), output);
        assertTrue(output.contains("SHA-224 23097d223405d8228642a477bda255b32aadbce4bda0b3f7e36c9da7"), output);
        assertTrue(output.contains("SHA-256 ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"), output);
        assertTrue(output.contains("SHA-384 cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7"), output);
        assertTrue(output.contains(
                "SHA-512 ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"),
                output);
        assertTrue(output.contains("SHA-512/224 4634270f707b6a54daae7530460842e20e37ed265ceee9a43e8924aa"), output);
        assertTrue(output.contains("SHA-512/256 53048e2681941ef99b2e29b76b4c7dabe4c2d0c634fc6d46e0e2f13107e7af23"), output);
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            assertTrue(output.contains("SHA3-224 e642824c3f8cf24ad09234ee7d3c766fc9a3a5168d0c94ad73b46fdf"), output);
            assertTrue(output.contains("SHA3-256 3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532"), output);
            assertTrue(output.contains("SHA3-384 ec01498288516fc926459f58e2c6ad8df9b473cb0fc08c2596da7cf0e49be4b298d88cea927ac7f539f1edf228376d25"), output);
            assertTrue(output.contains(
                    "SHA3-512 b751850b1a57168a5693cd924b6b096e08f621827444f70d884f5d0240d2712e10e116e9192af3c91a7ec57647e3934057340b4cf408d5a56592f8274eec53f0"),
                    output);
        }
    }

    /**
     * Tests if empty arguments are handled correctly.
     */
    @Test
    void testEmptyArguments() {
        assertThrows(IllegalArgumentException.class, () -> Digest.main(new String[0]));
    }

    /**
     * Tests if null arguments are handled correctly.
     */
    @Test
    void testNullArguments() {
        assertThrows(NullPointerException.class, () -> Digest.main(null));
    }
}
