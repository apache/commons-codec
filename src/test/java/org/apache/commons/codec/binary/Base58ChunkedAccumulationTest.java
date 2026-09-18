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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests that Base58 chunk-by-chunk accumulation (the streaming path used by {@link Base58InputStream} and {@link Base58OutputStream}) produces the same
 * results as one-shot coding, guarding the amortized-growth accumulation logic.
 */
class Base58ChunkedAccumulationTest {

    private static byte[] drain(final Base58 codec, final BaseNCodec.Context context) {
        final byte[] out = new byte[context.pos];
        codec.readResults(out, 0, out.length, context);
        return out;
    }

    private static byte[] newData() {
        final byte[] data = new byte[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 31 + 7);
        }
        return data;
    }

    @Test
    void testChunkedDecodeMatchesOneShot() {
        final byte[] data = newData();
        final Base58 codec = new Base58();
        final byte[] encoded = codec.encode(data);
        final BaseNCodec.Context context = new BaseNCodec.Context();
        for (int i = 0; i < encoded.length; i += 7) {
            codec.decode(encoded, i, Math.min(7, encoded.length - i), context);
        }
        codec.decode(encoded, 0, -1, context);
        assertArrayEquals(data, drain(codec, context));
    }

    @Test
    void testChunkedEncodeMatchesOneShot() {
        final byte[] data = newData();
        final Base58 codec = new Base58();
        final byte[] expected = codec.encode(data);
        final BaseNCodec.Context context = new BaseNCodec.Context();
        for (int i = 0; i < data.length; i += 11) {
            codec.encode(data, i, Math.min(11, data.length - i), context);
        }
        codec.encode(data, 0, -1, context);
        assertArrayEquals(expected, drain(codec, context));
    }

    @Test
    void testEmptyInput() {
        final Base58 codec = new Base58();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        codec.decode(new byte[0], 0, 0, context);
        codec.decode(new byte[0], 0, -1, context);
        assertArrayEquals(new byte[0], drain(codec, context));
    }

    @Test
    void testEncodeLimits() {
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setMaxEncodeLength(0));
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setMaxEncodeLength(-1));
        final byte[] input = new byte[Base58.DEFAULT_MAX_ENCODE_LENGTH + 1];
        assertThrows(IllegalArgumentException.class, () -> new Base58().encode(input));
        assertEquals(Base58.DEFAULT_MAX_ENCODE_LENGTH, new Base58().encode(new byte[Base58.DEFAULT_MAX_ENCODE_LENGTH]).length);
        assertEquals(input.length, Base58.builder().setMaxEncodeLength(Integer.MAX_VALUE).get().encode(input).length);
        final Base58 codec = Base58.builder().setMaxEncodeLength(10).get();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        codec.encode(new byte[8], 0, 8, context);
        final byte[] buffer = context.buffer;
        assertThrows(IllegalArgumentException.class, () -> codec.encode(input, 0, Integer.MAX_VALUE, context));
        assertSame(buffer, context.buffer);
        assertEquals(8, context.ibitWorkArea);
    }

    @Test
    void testEncodingInputStreamRejectsBeforeEof() throws IOException {
        final ByteArrayInputStream source = new ByteArrayInputStream(new byte[100]) {
            @Override
            public synchronized int read(final byte[] bytes, final int offset, final int length) {
                return super.read(bytes, offset, Math.min(length, 3));
            }
        };
        try (Base58InputStream stream = Base58InputStream.builder().setInputStream(source).setEncode(true)
                .setBaseNCodec(Base58.builder().setMaxEncodeLength(10).get()).get()) {
            assertThrows(IOException.class, stream::read);
            assertEquals(88, source.available());
        }
    }

    @Test
    void testEncodingOutputStreamRejectsCrossingChunk() throws IOException {
        final ByteArrayOutputStream sink = new ByteArrayOutputStream();
        try (Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(sink)
                .setBaseNCodec(Base58.builder().setMaxEncodeLength(10).get()).get()) {
            for (int i = 0; i < 10; i++) {
                stream.write(0);
            }
            assertThrows(IOException.class, () -> stream.write(0));
            assertEquals(0, sink.size());
        }
        assertArrayEquals(new Base58().encode(new byte[10]), sink.toByteArray());
    }

    @Test
    void testEncodingStreamsAtDefaultLimit() throws IOException {
        final byte[] input = new byte[Base58.DEFAULT_MAX_ENCODE_LENGTH];
        final byte[] expected = new Base58().encode(input);
        try (Base58InputStream stream = Base58InputStream.builder().setByteArray(input).setEncode(true).get()) {
            assertArrayEquals(expected, IOUtils.toByteArray(stream));
        }
        final ByteArrayOutputStream sink = new ByteArrayOutputStream();
        try (Base58OutputStream stream = new Base58OutputStream(sink)) {
            stream.write(input);
        }
        assertArrayEquals(expected, sink.toByteArray());
    }

    @Test
    void testEncodingStreamsRejectDefaultLimit() throws IOException {
        final byte[] input = new byte[Base58.DEFAULT_MAX_ENCODE_LENGTH + 1];
        try (Base58InputStream stream = Base58InputStream.builder().setByteArray(input).setEncode(true).get()) {
            assertThrows(IOException.class, stream::read);
        }
        try (Base58OutputStream stream = new Base58OutputStream(new ByteArrayOutputStream())) {
            assertThrows(IOException.class, () -> stream.write(input));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testGeometricGrowth(final boolean encode) throws IOException {
        final Base58 codec = new Base58();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        final byte[] input = { '1' };
        int copied = 0;
        for (int i = 0; i < 1000; i++) {
            final byte[] previous = context.buffer;
            BaseNCodec.code(encode, codec, input, 0, 1, context);
            if (previous != null && previous != context.buffer) {
                copied += previous.length;
            }
        }
        assertTrue(copied < 2000, "Accumulation copies must grow linearly");
        assertEquals(1000, context.ibitWorkArea);
        final byte[] all = new byte[1000];
        Arrays.fill(all, (byte) '1');
        BaseNCodec.code(encode, codec, input, 0, -1, context);
        assertArrayEquals(encode ? codec.encode(all) : codec.decode(all), drain(codec, context));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testIrregularChunksAtLimit(final boolean encode) throws IOException {
        final Base58 codec = Base58.builder().setMaxDecodeLength(10).setMaxEncodeLength(10).get();
        final byte[] input = new byte[10];
        Arrays.fill(input, (byte) 'z');
        final byte[] expected = encode ? codec.encode(input) : codec.decode(input);
        final BaseNCodec.Context context = new BaseNCodec.Context();
        int offset = 0;
        for (final int length : new int[] { 6, 1, 1, 2 }) {
            BaseNCodec.code(encode, codec, input, offset, length, context);
            offset += length;
            assertEquals(offset, context.ibitWorkArea);
            assertTrue(context.buffer.length <= 10);
            assertEquals(0, context.pos);
        }
        final byte[] buffer = context.buffer;
        final IOException exception = assertThrows(IOException.class, () -> BaseNCodec.code(encode, codec, input, 0, 1, context));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertSame(buffer, context.buffer);
        assertEquals(10, context.ibitWorkArea);
        BaseNCodec.code(encode, codec, input, 0, -1, context);
        assertArrayEquals(expected, drain(codec, context));
    }
}
