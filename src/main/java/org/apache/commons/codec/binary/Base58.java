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

package org.apache.commons.codec.binary;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

public class Base58 extends BaseNCodec {

    private transient Map<Context, byte[]> accumulated = new WeakHashMap<>();

    private static final byte[] ENCODE_TABLE = {
        '1','2','3','4','5','6','7','8','9',
        'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    };

    private static final byte[] DECODE_TABLE = {
         //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
            -1,  0,  1,  2,  3,  4,  5,  6,  7,  7, -1, -1, -1, -1, -1, -1, // 30-3f '0'-'9' (note: '0' -> -1, '1'-'9' -> 0-8)
            -1,  8,  9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, -1, 21, // 40-4f 'A'-'O' (note: 'O' -> -1)
            22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,                     // 50-5a 'P'-'Z' (note: no 'O')
            -1, -1, -1, -1, -1, // 5b-5f
            -1, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, -1, 44, 45, 46, // 60-6f 'a'-'o' (note: 'l' -> -1)
            47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,                     // 70-7a 'p'-'z'
    };

    public Base58() {
        this(new Builder());
    }

    public Base58(final Builder builder) {
        super(builder);
    }

    @Override
    void decode(byte[] array, int i, int length, Context context) {
    }

    @Override
    void encode(byte[] array, int offset, int length, Context context) {
        if (context.eof) {
            return;
        }

        if (length < 0) {
            context.eof = true;
            byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
            if (accumulate.length > 0) {
                convertToBase58(accumulate, context);
            }
            accumulated.remove(context);
            return;
        }

        byte[] accumulate = accumulated.getOrDefault(context, new byte[0]);
        byte[] newAccumulated = new byte[accumulate.length + length];
        if (accumulate.length > 0) {
            System.arraycopy(accumulate, 0, newAccumulated, 0, accumulate.length);
        }
        System.arraycopy(array, offset, newAccumulated, accumulate.length, length);
        accumulated.put(context, newAccumulated);
    }

    private byte[] convertToBase58(byte[] accumulate, Context context) {
        StringBuilder base58 = getStringBuilder(accumulate);
        String encoded = base58.reverse().toString();

        byte[] encodedBytes = encoded.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = ensureBufferSize(encodedBytes.length, context);
        System.arraycopy(encodedBytes, 0, buffer, context.pos, encodedBytes.length);
        context.pos += encodedBytes.length;
        return buffer;
    }

    private StringBuilder getStringBuilder(byte[] accumulate) {
        BigInteger value = new BigInteger(1, accumulate);
        int leadingZeros = 0;

        for (byte b : accumulate) {
            if (b == 0) leadingZeros++;
            else break;
        }

        StringBuilder base58 = new StringBuilder();
        while (value.signum() > 0) {
            BigInteger[] divRem = value.divideAndRemainder(BigInteger.valueOf(58));
            base58.append((char) ENCODE_TABLE[divRem[1].intValue()]);
            value = divRem[0];
        }

        for (int i = 0; i < leadingZeros; i++) base58.append('1');
        return base58;
    }

    @Override
    protected boolean isInAlphabet(byte value) {
        return value >= 0 && value < DECODE_TABLE.length && DECODE_TABLE[value] != -1;
    }

    public static class Builder extends AbstractBuilder<Base58, Base58.Builder> {

        public Builder() {
            super(ENCODE_TABLE);
            setDecodeTable(DECODE_TABLE);
            setEncodeTable(ENCODE_TABLE);
        }

        @Override
        public Base58 get() {
            return new Base58(this);
        }

        @Override
        public Base58.Builder setEncodeTable(final byte... encodeTable) {
            super.setDecodeTableRaw(DECODE_TABLE);
            return super.setEncodeTable(encodeTable);
        }
    }

}
