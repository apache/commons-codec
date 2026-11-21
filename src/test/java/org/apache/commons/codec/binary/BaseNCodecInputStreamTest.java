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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.codec.CodecPolicy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests EOF handling and availability shared by all Base-N input streams.
 */
class BaseNCodecInputStreamTest {

    /**
     * Splits the input into bounded chunks and either reports physical EOF once or fails on any attempt to read beyond the supplied data.
     */
    private static final class EofTrackingInputStream extends InputStream {

        private final byte[] data;

        private final int chunkSize;

        private final boolean allowEof;

        private final IOException unexpectedRead = new IOException("Unexpected read beyond the supplied input");

        private int position;

        private int readCalls;

        private int eofReturns;

        private boolean closed;

        EofTrackingInputStream(final byte[] data, final int chunkSize, final boolean allowEof) {
            this.data = data;
            this.chunkSize = chunkSize;
            this.allowEof = allowEof;
        }

        @Override
        public int available() {
            throw new AssertionError("The codec stream must not query the source's availability");
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public int read() throws IOException {
            final byte[] one = new byte[1];
            return read(one, 0, 1) == -1 ? -1 : one[0] & 0xff;
        }

        @Override
        public int read(final byte[] buffer, final int offset, final int length) throws IOException {
            if (length == 0) {
                return 0;
            }
            readCalls++;
            if (position == data.length) {
                if (allowEof && eofReturns == 0) {
                    eofReturns++;
                    return -1;
                }
                throw unexpectedRead;
            }
            final int count = Math.min(Math.min(length, chunkSize), data.length - position);
            System.arraycopy(data, position, buffer, offset, count);
            position += count;
            return count;
        }
    }

    private static final int[] READ_SIZES = { -64, -3, 0, 1, 3, 64 };

    private static void assertAvailable(final int expected, final InputStream stream, final EofTrackingInputStream source) throws IOException {
        final int calls = source.readCalls;
        assertEquals(expected, stream.available());
        assertEquals(expected, stream.available(), "Repeated availability checks must not change stream state");
        assertEquals(calls, source.readCalls, "Availability checks must not read from the source");
        assertFalse(source.closed, "Availability checks must not close the source");
    }

    private static void assertAvailableWhileReading(final byte[] expected, final InputStream stream, final EofTrackingInputStream source,
            final int readSize) throws IOException {
        assertAvailable(1, stream, source);
        int position = 0;
        while (position < expected.length) {
            assertAvailable(1, stream, source);
            assertEquals(0, stream.read(new byte[0]));
            assertEquals(0, stream.skip(0));
            assertAvailable(1, stream, source);
            if (readSize == 0) {
                assertEquals(expected[position++] & 0xff, stream.read());
            } else {
                final int capacity = Math.abs(readSize);
                final int offset = readSize < 0 ? 0 : 1;
                final byte[] buffer = new byte[capacity + 2 * offset];
                Arrays.fill(buffer, (byte) 0x5a);
                final int count = readSize < 0 ? stream.read(buffer) : stream.read(buffer, offset, capacity);
                assertEquals(Math.min(capacity, expected.length - position), count);
                assertArrayEquals(Arrays.copyOfRange(expected, position, position + count), Arrays.copyOfRange(buffer, offset, offset + count));
                if (offset != 0) {
                    assertEquals(0x5a, buffer[0]);
                    assertEquals(0x5a, buffer[buffer.length - 1]);
                }
                position += count;
            }
        }
        if (source.eofReturns != 0) {
            assertAvailable(0, stream, source);
        }
        assertEquals(-1, stream.read());
        assertFinished(stream, source);
    }

    /**
     * EOF must be stable for all read and skip overloads, while zero-length reads and argument validation retain their normal behavior.
     */
    private static void assertFinished(final InputStream stream, final EofTrackingInputStream source) throws IOException {
        final int calls = source.readCalls;
        assertAvailable(0, stream, source);
        assertEquals(-1, stream.read());
        assertEquals(-1, stream.read());
        assertEquals(-1, stream.read(new byte[4]));
        assertEquals(-1, stream.read(new byte[4], 1, 2));
        assertEquals(0, stream.read(new byte[0]));
        assertEquals(0, stream.read(new byte[4], 4, 0));
        assertEquals(0, stream.skip(0));
        assertEquals(0, stream.skip(Long.MAX_VALUE));
        assertThrows(NullPointerException.class, () -> stream.read(null, 0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> stream.read(new byte[1], 2, 0));
        assertThrows(IllegalArgumentException.class, () -> stream.skip(-1));
        assertAvailable(0, stream, source);
        assertEquals(calls, source.readCalls, "EOF operations must not read from the source");
        assertFalse(source.closed, "Reaching EOF must not close the source");
    }

    static Stream<Arguments> codecs() {
        final List<Arguments> arguments = new ArrayList<>();
        for (final CodecPolicy policy : CodecPolicy.values()) {
            arguments.add(Arguments.of("Base16 " + policy, Base16.builder().setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base16 lower " + policy, Base16.builder().setLowerCase(true).setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base32 " + policy, Base32.builder().setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base32 hex " + policy, Base32.builder().setHexEncodeTable(true).setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base32 custom pad " + policy, Base32.builder().setPadding((byte) '.').setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base32 chunked " + policy, Base32.builder().setLineLength(16).setDecodingPolicy(policy).get()));
            if (policy == CodecPolicy.STRICT) {
                arguments.add(Arguments.of("Base45 " + policy, Base45.builder().setDecodingPolicy(policy).get()));
            }
            arguments.add(Arguments.of("Base58 " + policy, Base58.builder().setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base64 " + policy, Base64.builder().setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base64 URL " + policy, Base64.builder().setUrlSafe(true).setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base64 custom pad " + policy, Base64.builder().setPadding((byte) '.').setDecodingPolicy(policy).get()));
            arguments.add(Arguments.of("Base64 chunked " + policy, Base64.builder().setLineLength(8).setDecodingPolicy(policy).get()));
        }
        return arguments.stream();
    }

    static Stream<Arguments> largeCodecs() {
        return codecs().map(arguments -> {
            final BaseNCodec codec = (BaseNCodec) arguments.get()[1];
            if (codec instanceof Base58) {
                // Raise the Base58 input limits so these tests can cross the stream's 4096- and 8192-byte buffer boundaries.
                return Arguments.of(arguments.get()[0], Base58.builder().setMaxEncodeLength(16384).setMaxDecodeLength(16384)
                        .setDecodingPolicy(codec.isStrictDecoding() ? CodecPolicy.STRICT : CodecPolicy.LENIENT).get());
            }
            return arguments;
        });
    }

    private static InputStream newStream(final InputStream source, final BaseNCodec codec, final boolean encode) {
        if (codec instanceof Base16) {
            return Base16InputStream.builder().setInputStream(source).setBaseNCodec((Base16) codec).setEncode(encode).get();
        }
        if (codec instanceof Base32) {
            return Base32InputStream.builder().setInputStream(source).setBaseNCodec((Base32) codec).setEncode(encode).get();
        }
        if (codec instanceof Base45) {
            return Base45InputStream.builder().setInputStream(source).setBaseNCodec((Base45) codec).setEncode(encode).get();
        }
        if (codec instanceof Base58) {
            return Base58InputStream.builder().setInputStream(source).setBaseNCodec((Base58) codec).setEncode(encode).get();
        }
        if (codec instanceof Base64) {
            return Base64InputStream.builder().setInputStream(source).setBaseNCodec((Base64) codec).setEncode(encode).get();
        }
        throw new IllegalArgumentException("Unknown codec: " + codec);
    }

    static Stream<Arguments> paddedCodecs() {
        return Stream.of(Arguments.of("Base32", new Base32()), Arguments.of("Base32 hex", Base32.builder().setHexEncodeTable(true).get()),
                Arguments.of("Base32 custom pad", Base32.builder().setPadding((byte) '.').get()), Arguments.of("Base64", new Base64()),
                Arguments.of("Base64 custom pad", Base64.builder().setPadding((byte) '.').get()));
    }

    private static byte[] payload(final int length) {
        final byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = (byte) (i * 31);
        }
        return data;
    }

    /**
     * Zero selects single-byte reads, negative sizes select read(byte[]), and positive sizes select reads with a nonzero destination offset.
     */
    private static byte[] readAll(final InputStream stream, final int readSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (readSize == 0) {
            int value;
            while ((value = stream.read()) != -1) {
                output.write(value);
            }
        } else {
            final byte[] buffer = new byte[readSize < 0 ? -readSize : readSize + 2];
            final int offset = readSize < 0 ? 0 : 1;
            while (true) {
                Arrays.fill(buffer, (byte) 0x5a);
                final int count = readSize < 0 ? stream.read(buffer) : stream.read(buffer, offset, readSize);
                if (readSize > 0) {
                    assertEquals(0x5a, buffer[0]);
                    assertEquals(0x5a, buffer[buffer.length - 1]);
                }
                if (count == -1) {
                    break;
                }
                assertTrue(count > 0, "A nonempty read must return data or EOF");
                output.write(buffer, offset, count);
            }
        }
        return output.toByteArray();
    }

    static Stream<Arguments> strictInvalidInputs() {
        final Base64 base64 = Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).get();
        final Base32 base32 = Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).get();
        return Stream.of(Arguments.of(base64, "Zg==A"), Arguments.of(base64, "Zg==!"), Arguments.of(base64, "Zg="), Arguments.of(base64, "Zh=="),
                Arguments.of(base64, "Zm9vYg==A"), Arguments.of(base32, "MY======A"), Arguments.of(base32, "MY======!"), Arguments.of(base32, "MY====="),
                Arguments.of(base32, "MZ======"), Arguments.of(base32, "MZXW6YTBOI======A"),
                Arguments.of(Base16.builder().setDecodingPolicy(CodecPolicy.STRICT).get(), "0"), Arguments.of(new Base45(), "0"),
                Arguments.of(new Base58(), "0"));
    }

    static Stream<Arguments> strictStreamingCodecs() {
        return codecs().filter(arguments -> {
            final BaseNCodec codec = (BaseNCodec) arguments.get()[1];
            return codec.isStrictDecoding() && (codec instanceof Base32 || codec instanceof Base64);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("largeCodecs")
    void testAvailableAcrossInternalBufferBoundaries(final String name, final BaseNCodec codec) throws IOException {
        for (final int length : new int[] { 4095, 4096, 4097, 8191, 8192, 8193 }) {
            final byte[] decoded = payload(length);
            final byte[] encoded = codec.encode(decoded);
            for (final boolean encode : new boolean[] { false, true }) {
                for (final int chunkSize : new int[] { 257, 8192 }) {
                    final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, chunkSize, true);
                    try (InputStream stream = newStream(source, codec, encode)) {
                        assertAvailableWhileReading(encode ? encoded : decoded, stream, source, 333);
                    }
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strictStreamingCodecs")
    void testAvailableDoesNotHidePendingStrictValidation(final String name, final BaseNCodec codec) throws IOException {
        final byte[] decoded = payload(15);
        final byte[] encoded = codec.encode(decoded);
        final EofTrackingInputStream source = new EofTrackingInputStream(encoded, encoded.length, false);
        try (InputStream stream = newStream(source, codec, false)) {
            final byte[] output = new byte[decoded.length];
            assertEquals(output.length, stream.read(output));
            assertArrayEquals(decoded, output);
            assertAvailable(1, stream, source);
            assertEquals(0, source.eofReturns);
            assertSame(source.unexpectedRead, assertThrows(IOException.class, stream::read));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testAvailableDrainsPaddingOutputWithoutReadingTheNextChunk(final String name, final BaseNCodec codec) throws IOException {
        final byte[] decoded = payload(7);
        final byte[] encoded = codec.encode(decoded);
        final byte[] input = Arrays.copyOf(encoded, encoded.length + 1);
        input[encoded.length] = '!';
        final EofTrackingInputStream source = new EofTrackingInputStream(input, encoded.length, false);
        try (InputStream stream = newStream(source, codec, false)) {
            for (int i = 0; i < decoded.length; i++) {
                assertAvailable(1, stream, source);
                if (i % 2 == 0) {
                    assertEquals(decoded[i] & 0xff, stream.read());
                } else {
                    assertEquals(1, stream.skip(1));
                }
            }
            assertAvailable(0, stream, source);
            assertFinished(stream, source);
            assertEquals(encoded.length, source.position);
            assertEquals(0, source.eofReturns);
            assertEquals('!', source.read());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testAvailableWhileReading(final String name, final BaseNCodec codec) throws IOException {
        for (final boolean encode : new boolean[] { false, true }) {
            for (int length = 0; length <= 11; length++) {
                final byte[] decoded = payload(length);
                final byte[] encoded = codec.encode(decoded);
                for (final int chunkSize : new int[] { 1, 7, 8192 }) {
                    for (final int readSize : READ_SIZES) {
                        final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, chunkSize, true);
                        try (InputStream stream = newStream(source, codec, encode)) {
                            assertAvailableWhileReading(encode ? encoded : decoded, stream, source, readSize);
                        }
                    }
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testAvailableWhileSkipping(final String name, final BaseNCodec codec) throws IOException {
        for (final boolean encode : new boolean[] { false, true }) {
            for (int length = 0; length <= 11; length++) {
                final byte[] decoded = payload(length);
                final byte[] encoded = codec.encode(decoded);
                final byte[] expected = encode ? encoded : decoded;
                for (final int chunkSize : new int[] { 1, 8192 }) {
                    final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, chunkSize, true);
                    try (InputStream stream = newStream(source, codec, encode)) {
                        assertAvailable(1, stream, source);
                        for (int i = 0; i < expected.length; i++) {
                            assertAvailable(1, stream, source);
                            assertEquals(1, stream.skip(1));
                        }
                        if (source.eofReturns != 0) {
                            assertAvailable(0, stream, source);
                        }
                        assertEquals(0, stream.skip(Long.MAX_VALUE));
                        assertFinished(stream, source);
                    }
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testLenientPaddingCanFinishWithoutOutput(final String name, final BaseNCodec codec) throws IOException {
        for (final byte[] input : new byte[][] { { codec.pad }, { ' ', '\n', codec.pad } }) {
            for (final int readSize : READ_SIZES) {
                final EofTrackingInputStream source = new EofTrackingInputStream(input, 1, false);
                try (InputStream stream = newStream(source, codec, false)) {
                    assertArrayEquals(new byte[0], readAll(stream, readSize));
                    assertFinished(stream, source);
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testLenientPaddingDoesNotConsumeTheNextChunk(final String name, final BaseNCodec codec) throws IOException {
        final byte[] decoded = payload(7);
        final byte[] encoded = codec.encode(decoded);
        final byte[] input = Arrays.copyOf(encoded, encoded.length + 1);
        input[encoded.length] = '!';
        final EofTrackingInputStream source = new EofTrackingInputStream(input, encoded.length, false);
        try (InputStream stream = newStream(source, codec, false)) {
            assertArrayEquals(decoded, readAll(stream, 64));
            assertFinished(stream, source);
            assertEquals(encoded.length, source.position);
            assertEquals('!', source.read(), "The next chunk belongs to the caller");
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testLenientPaddingDoesNotWaitForPipeToClose(final String name, final BaseNCodec codec) throws IOException {
        final byte[] decoded = payload(7);
        try (PipedInputStream source = new PipedInputStream();
                PipedOutputStream writer = new PipedOutputStream(source);
                InputStream stream = newStream(source, codec, false)) {
            writer.write(codec.encode(decoded));
            writer.flush();
            // Keep the writer open until the decoder has returned all its output and EOF.
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                for (final byte value : decoded) {
                    assertEquals(1, stream.available());
                    assertEquals(value & 0xff, stream.read());
                }
                assertEquals(0, stream.available());
                assertEquals(-1, stream.read());
            });
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testLenientPaddingStopsAtEveryInputBoundary(final String name, final BaseNCodec codec) throws IOException {
        // Cover every Base32 and Base64 partial-block size, with and without preceding complete blocks.
        for (int length = 1; length <= 11; length++) {
            final byte[] decoded = payload(length);
            final byte[] encoded = codec.encode(decoded);
            for (int pad = 0; pad < encoded.length; pad++) {
                if (encoded[pad] != codec.pad) {
                    continue;
                }
                // Lenient decoding ends at the first pad; no physical EOF or remaining padding may be requested.
                final byte[] throughPad = Arrays.copyOf(encoded, pad + 1);
                for (int chunkSize = 1; chunkSize <= throughPad.length; chunkSize++) {
                    for (final int readSize : READ_SIZES) {
                        final EofTrackingInputStream source = new EofTrackingInputStream(throughPad, chunkSize, false);
                        try (InputStream stream = newStream(source, codec, false)) {
                            assertArrayEquals(decoded, readAll(stream, readSize), name + ", length=" + length + ", chunk=" + chunkSize);
                            assertFinished(stream, source);
                            assertEquals(0, source.eofReturns);
                        }
                    }
                }
                break;
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddedCodecs")
    void testReadAcrossInternalBufferBoundaries(final String name, final BaseNCodec codec) throws IOException {
        for (final int length : new int[] { 4095, 4096, 4097, 8191, 8192, 8193 }) {
            final byte[] decoded = payload(length);
            final byte[] encoded = codec.encode(decoded);
            for (final boolean encode : new boolean[] { false, true }) {
                final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, 8192, true);
                try (InputStream stream = newStream(source, codec, encode)) {
                    assertArrayEquals(encode ? encoded : decoded, readAll(stream, 333), name + ", length=" + length + ", encode=" + encode);
                    assertFinished(stream, source);
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testReadDoesNotRepeatPhysicalEof(final String name, final BaseNCodec codec) throws IOException {
        for (final boolean encode : new boolean[] { false, true }) {
            for (int length = 0; length <= 11; length++) {
                final byte[] decoded = payload(length);
                final byte[] encoded = codec.encode(decoded);
                for (final int chunkSize : new int[] { 1, 7, 8192 }) {
                    for (final int readSize : READ_SIZES) {
                        final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, chunkSize, true);
                        try (InputStream stream = newStream(source, codec, encode)) {
                            assertEquals(0, stream.read(new byte[0]));
                            assertEquals(0, stream.read(new byte[3], 1, 0));
                            assertEquals(0, stream.skip(0));
                            assertEquals(0, source.readCalls);
                            assertArrayEquals(encode ? encoded : decoded, readAll(stream, readSize),
                                    name + ", encode=" + encode + ", length=" + length + ", chunk=" + chunkSize + ", read=" + readSize);
                            assertFinished(stream, source);
                            if (encode || codec.isStrictDecoding()) {
                                assertEquals(1, source.eofReturns, "Encoding and strict decoding must reach physical EOF");
                            }
                        }
                        assertTrue(source.closed, "Closing the wrapper must still close the source");
                    }
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testSkipPastEofDoesNotReadAgain(final String name, final BaseNCodec codec) throws IOException {
        final byte[] decoded = payload(7);
        final byte[] encoded = codec.encode(decoded);
        for (final boolean encode : new boolean[] { false, true }) {
            final byte[] expected = encode ? encoded : decoded;
            final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, 1, true);
            try (InputStream stream = newStream(source, codec, encode)) {
                assertEquals(2, stream.skip(2));
                assertEquals(expected[2] & 0xff, stream.read());
                assertEquals(expected.length - 3, stream.skip(Long.MAX_VALUE));
                assertFinished(stream, source);
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testSourceFailureAfterPartialInputIsPropagated(final String name, final BaseNCodec codec) throws IOException {
        // Fifteen bytes form complete Base32 and Base64 blocks, so lenient decoders cannot finish early at padding.
        final byte[] decoded = payload(15);
        final byte[] encoded = codec.encode(decoded);
        for (final boolean encode : new boolean[] { false, true }) {
            for (final int readSize : new int[] { 0, 64 }) {
                final EofTrackingInputStream source = new EofTrackingInputStream(encode ? decoded : encoded, 2, false);
                try (InputStream stream = newStream(source, codec, encode)) {
                    assertSame(source.unexpectedRead, assertThrows(IOException.class, () -> readAll(stream, readSize)));
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("codecs")
    void testSourceFailureBeforeEofIsPropagated(final String name, final BaseNCodec codec) throws IOException {
        for (final boolean encode : new boolean[] { false, true }) {
            final EofTrackingInputStream source = new EofTrackingInputStream(new byte[0], 1, false);
            try (InputStream stream = newStream(source, codec, encode)) {
                assertSame(source.unexpectedRead, assertThrows(IOException.class, stream::read));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strictInvalidInputs")
    void testStrictDecodingStillRejectsTrailingInput(final BaseNCodec codec, final String input) throws IOException {
        final byte[] encoded = StringUtils.getBytesUsAscii(input);
        for (int chunkSize = 1; chunkSize <= encoded.length; chunkSize++) {
            for (final int readSize : READ_SIZES) {
                final EofTrackingInputStream source = new EofTrackingInputStream(encoded, chunkSize, true);
                try (InputStream stream = newStream(source, codec, false)) {
                    final IOException failure = assertThrows(IOException.class, () -> readAll(stream, readSize), input);
                    assertTrue(failure.getCause() instanceof IllegalArgumentException, "The codec must reject the input, not reread physical EOF");
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strictStreamingCodecs")
    void testStrictDecodingStillRequiresPhysicalEof(final String name, final BaseNCodec codec) throws IOException {
        final byte[] encoded = codec.encode(payload(7));
        for (int chunkSize = 1; chunkSize <= encoded.length; chunkSize++) {
            final EofTrackingInputStream source = new EofTrackingInputStream(encoded, chunkSize, false);
            try (InputStream stream = newStream(source, codec, false)) {
                assertSame(source.unexpectedRead, assertThrows(IOException.class, () -> readAll(stream, 64)));
            }
        }
    }
}
