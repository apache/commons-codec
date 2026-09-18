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
 * Tests the maximum decode length limit that bounds the quadratic-cost Base58 decode against oversized untrusted input.
 */
class Base58MaxDecodeLengthTest {

    private static byte[] ones(final int length) {
        final byte[] bytes = new byte[length];
        // '1' encodes a leading zero byte, so decoding stays cheap regardless of length.
        Arrays.fill(bytes, (byte) '1');
        return bytes;
    }

    @Test
    void testConfiguredLimit() {
        final Base58 small = Base58.builder().setMaxDecodeLength(10).get();
        assertThrows(IllegalArgumentException.class, () -> small.decode(ones(11)));
        assertEquals(10, small.decode(ones(10)).length);
        final Base58 unlimited = Base58.builder().setMaxDecodeLength(Integer.MAX_VALUE).get();
        assertEquals(Base58.DEFAULT_MAX_DECODE_LENGTH + 1, unlimited.decode(ones(Base58.DEFAULT_MAX_DECODE_LENGTH + 1)).length);
    }

    @Test
    void testDecodeAtLimitAccepted() {
        assertEquals(Base58.DEFAULT_MAX_DECODE_LENGTH, new Base58().decode(ones(Base58.DEFAULT_MAX_DECODE_LENGTH)).length);
    }

    @Test
    void testDecodeOverLimitRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Base58().decode(ones(Base58.DEFAULT_MAX_DECODE_LENGTH + 1)));
    }

    @Test
    void testDefaultStreamsRejectOversizedInput() throws IOException {
        final byte[] encoded = ones(Base58.DEFAULT_MAX_DECODE_LENGTH + 1);
        try (Base58InputStream stream = new Base58InputStream(new ByteArrayInputStream(encoded))) {
            assertThrows(IOException.class, stream::read);
        }
        try (Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(new ByteArrayOutputStream()).setEncode(false).get()) {
            assertThrows(IOException.class, () -> stream.write(encoded));
        }
    }

    @Test
    void testEncodingMayExceedDecodeLimit() throws IOException {
        final byte[] input = new byte[Base58.DEFAULT_MAX_DECODE_LENGTH + 1];
        final Base58 codec = new Base58();
        final byte[] encoded = codec.encode(input);
        assertEquals(input.length, encoded.length);
        assertThrows(IllegalArgumentException.class, () -> codec.decode(encoded));
        final ByteArrayOutputStream sink = new ByteArrayOutputStream();
        try (Base58OutputStream stream = new Base58OutputStream(sink)) {
            stream.write(input);
        }
        assertArrayEquals(encoded, sink.toByteArray());
        try (Base58InputStream stream = Base58InputStream.builder().setByteArray(input).setEncode(true).get()) {
            assertArrayEquals(encoded, IOUtils.toByteArray(stream));
        }
        final ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        try (Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(decoded).setEncode(false)
                .setBaseNCodec(Base58.builder().setMaxDecodeLength(encoded.length).get()).get()) {
            stream.write(encoded);
        }
        assertArrayEquals(input, decoded.toByteArray());
    }

    @ParameterizedTest
    @ValueSource(ints = { 10, Base58.DEFAULT_MAX_DECODE_LENGTH, Base58.DEFAULT_MAX_DECODE_LENGTH + 1 })
    void testInputStreamAtConfiguredLimit(final int limit) throws IOException {
        try (Base58InputStream stream = Base58InputStream.builder().setByteArray(ones(limit)).setBaseNCodec(Base58.builder().setMaxDecodeLength(limit).get())
                .get()) {
            assertArrayEquals(new byte[limit], IOUtils.toByteArray(stream));
        }
    }

    @Test
    void testInputStreamRejectsBeforeEof() throws IOException {
        final ByteArrayInputStream source = new ByteArrayInputStream(ones(100)) {

            @Override
            public synchronized int read(final byte[] bytes, final int offset, final int length) {
                return super.read(bytes, offset, Math.min(length, 3));
            }
        };
        try (Base58InputStream stream = Base58InputStream.builder().setInputStream(source).setBaseNCodec(Base58.builder().setMaxDecodeLength(10).get()).get()) {
            final IOException exception = assertThrows(IOException.class, stream::read);
            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals(88, source.available());
        }
    }

    @Test
    void testInvalidLimitRejected() {
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setMaxDecodeLength(0));
        assertThrows(IllegalArgumentException.class, () -> Base58.builder().setMaxDecodeLength(-1));
    }

    @Test
    void testNonzeroDigitsAtLimit() {
        final Base58 codec = new Base58();
        final byte[] encoded = new byte[Base58.DEFAULT_MAX_DECODE_LENGTH];
        Arrays.fill(encoded, (byte) 'z');
        assertArrayEquals(encoded, codec.encode(codec.decode(encoded)));
        final byte[] over = Arrays.copyOf(encoded, encoded.length + 1);
        over[encoded.length] = 'z';
        assertThrows(IllegalArgumentException.class, () -> codec.decode(over));
        assertThrows(IllegalArgumentException.class, () -> codec.decode(StringUtils.newStringUsAscii(over)));
        assertThrows(IllegalArgumentException.class, () -> codec.decode((Object) over));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 3, 10 })
    void testOutputStreamRejectsCrossingChunk(final int chunkSize) throws IOException {
        final ByteArrayOutputStream sink = new ByteArrayOutputStream();
        try (Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(sink).setEncode(false)
                .setBaseNCodec(Base58.builder().setMaxDecodeLength(10).get()).get()) {
            int written = 0;
            while (written < 10) {
                final int length = Math.min(chunkSize, 10 - written);
                stream.write(ones(length));
                written += length;
            }
            final IOException exception = assertThrows(IOException.class, () -> stream.write('1'));
            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals(0, sink.size());
        }
        assertArrayEquals(new byte[10], sink.toByteArray());
    }

    @Test
    void testRejectsBeforeBuffering() {
        final Base58 codec = Base58.builder().setMaxDecodeLength(10).get();
        final BaseNCodec.Context context = new BaseNCodec.Context();
        codec.decode(ones(8), 0, 8, context);
        assertThrows(IllegalArgumentException.class, () -> codec.decode(ones(3), 0, 3, context));
        assertArrayEquals(ones(8), context.buffer);
        // The cumulative check must not wrap around when adding the chunk length.
        assertThrows(IllegalArgumentException.class, () -> codec.decode(ones(1), 0, Integer.MAX_VALUE, context));
        assertArrayEquals(ones(8), context.buffer);
    }

    @Test
    void testRoundTripUnaffected() {
        final Base58 codec = new Base58();
        final byte[] data = { 0, 0, 1, 2, 3, 4, 5, -1, 127 };
        assertArrayEquals(data, codec.decode(codec.encode(data)));
    }
}
