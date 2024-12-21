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

package org.apache.commons.codec;

import org.openjdk.jmh.annotations.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class CodecBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"1024", "4096", "16384"})
        public int dataSize;

        public byte[] data;
        public String hexData;

        @Setup(Level.Trial)
        public void setup() {
            this.data = new byte[dataSize];
            new SecureRandom().nextBytes(data);
            this.hexData = Hex.encodeHexString(data); // Ensure valid hex string for decoding
        }
    }

    @Benchmark
    public byte[] encodeBase64(BenchmarkState state) {
        return Base64.encodeBase64(state.data);
    }

    @Benchmark
    public byte[] decodeBase64(BenchmarkState state) {
        return Base64.decodeBase64(Base64.encodeBase64(state.data)); // Decode valid Base64 data
    }

    @Benchmark
    public char[] encodeHex(BenchmarkState state) {
        return Hex.encodeHex(state.data);
    }

    @Benchmark
    public byte[] decodeHex(BenchmarkState state) throws DecoderException {
        return Hex.decodeHex(state.hexData); // Decode valid hex string
    }
}
