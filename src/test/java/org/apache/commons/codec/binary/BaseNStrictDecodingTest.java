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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.BaseNCodec.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Tests canonical decoding across array and stream boundaries. */
class BaseNStrictDecodingTest {

    private static final Base64 BASE64 = Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).get();
    private static final Base32 BASE32 = Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).get();

    private static void assertCanonicalOrRejected(final BaseNCodec codec, final byte[] encoded) {
        final byte[] decoded;
        try {
            decoded = codec.decode(encoded);
        } catch (final IllegalArgumentException expected) {
            return;
        }
        assertArrayEquals(encoded, codec.encode(decoded));
    }

    private static void assertInvalid(final BaseNCodec codec, final String... inputs) {
        for (final String input : inputs) {
            assertThrows(IllegalArgumentException.class, () -> codec.decode(input), input);
        }
    }

    private static Stream<BaseNCodec> codecs() {
        final byte[] custom64 = StringUtils.getBytesUsAscii("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");
        // A custom alphabet uses the encoder's unpadded form, even when it is not the built-in URL-safe alphabet.
        custom64[0] = '!';
        final byte[] custom32 = StringUtils.getBytesUsAscii("abcdefghijklmnopqrstuvwxyz234567");
        return Stream.of(BASE64, BASE32,
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setUrlSafe(true).get(),
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setEncodeTable(custom64).get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setEncodeTable(custom32).get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setHexEncodeTable(true).get(),
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setPadding((byte) '.').get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setPadding((byte) '.').get(),
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(9).setLineSeparator((byte) '!', (byte) '?').get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(17).setLineSeparator((byte) '!', (byte) '?').get(),
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setUrlSafe(true).setLineLength(8).get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(16).get(),
                Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(1).get(),
                Base32.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(8).setLineSeparator(new byte[0]).get());
    }

    private static byte[] decodeInParts(final BaseNCodec codec, final byte[] encoded, final int split) {
        final Context context = new Context();
        codec.decode(encoded, 0, split, context);
        codec.decode(encoded, split, encoded.length - split, context);
        codec.decode(encoded, 0, BaseNCodec.EOF, context);
        final byte[] result = new byte[codec.available(context)];
        codec.readResults(result, 0, result.length, context);
        return result;
    }

    private static InputStream oneByteAtATime(final byte[] encoded) {
        return new ByteArrayInputStream(encoded) {
            @Override
            public synchronized int read(final byte[] buffer, final int offset, final int length) {
                return super.read(buffer, offset, Math.min(1, length));
            }
        };
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void testCanonicalRoundTripAtEverySplit(final BaseNCodec codec) {
        final Random random = new Random(87654);
        for (int length = 0; length <= 32; length++) {
            final byte[] input = new byte[length];
            random.nextBytes(input);
            final byte[] encoded = codec.encode(input);
            assertArrayEquals(input, codec.decode(encoded));
            for (int split = 0; split <= encoded.length; split++) {
                assertArrayEquals(input, decodeInParts(codec, encoded, split));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void testCanonicalStreams(final BaseNCodec codec) throws IOException {
        final byte[] input = new byte[67];
        new Random(23456).nextBytes(input);
        final byte[] encoded = codec.encode(input);
        final ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        try (InputStream stream = new BaseNCodecInputStream<>(oneByteAtATime(encoded), codec, false)) {
            for (int value; (value = stream.read()) != -1;) {
                decoded.write(value);
            }
        }
        assertArrayEquals(input, decoded.toByteArray());
        decoded.reset();
        try (BaseNCodecOutputStream<?, ?, ?> stream = new BaseNCodecOutputStream<>(decoded, codec, false)) {
            for (final byte value : encoded) {
                stream.write(value & 0xff);
            }
            stream.flush();
        }
        assertArrayEquals(input, decoded.toByteArray());
    }

    @Test
    void testGarbageWhitespaceAndAlphabetAliases() {
        assertInvalid(BASE64, "QU JD", "QU$JD", "QU\u001cJD", "QQ==JUNK", "QQ==\n", "QUJ-", "QUJ_", "QR==", "QUJ=");
        assertInvalid(BASE32, "M Y======", "MY!======", "MY======JUNK", "my======", "MY======\n", "MZ======");
        final Base64 url = Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setUrlSafe(true).get();
        assertInvalid(url, "QQ==", "QUJ+", "QUJ/");
        assertArrayEquals(new byte[] {65}, url.decode("QQ"));
    }

    @Test
    void testLenientBehaviorIsUnchanged() {
        assertArrayEquals(new byte[] {65}, new Base64().decode("Q $Q==JUNK"));
        assertArrayEquals(new byte[] {102}, new Base32().decode("m !y======JUNK"));
        assertArrayEquals(new Base64().decode("QUJ+"), new Base64().decode("QUJ-"));
    }

    @Test
    void testLineSeparators() {
        final Base64 codec = Base64.builder().setDecodingPolicy(CodecPolicy.STRICT).setLineLength(8).get();
        assertEquals("ABC", StringUtils.newStringUsAscii(codec.decode("QUJD\r\n")));
        assertEquals("ABCDEF", StringUtils.newStringUsAscii(codec.decode("QUJDREVG\r\n")));
        assertInvalid(codec, "QUJD", "QUJD\r", "QUJD\n", "QUJD\rX", "\r\n", "QUJD\r\nREVG\r\n", "QUJDREVGQUJD\r\n",
                "QUJDREVG\r\n\r\n", "QQ==\r\nQUJD\r\n", "QUJDREVG=", "QUJDREVG\rX");
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void testMutationsAreCanonicalOrRejected(final BaseNCodec codec) {
        final byte[] input = new byte[7];
        new Random(34567).nextBytes(input);
        final byte[] encoded = codec.encode(input);
        for (int pos = 0; pos < encoded.length; pos++) {
            final byte[] deleted = new byte[encoded.length - 1];
            System.arraycopy(encoded, 0, deleted, 0, pos);
            System.arraycopy(encoded, pos + 1, deleted, pos, encoded.length - pos - 1);
            assertCanonicalOrRejected(codec, deleted);
            final byte[] changed = encoded.clone();
            for (int value = 0; value <= 255; value++) {
                changed[pos] = (byte) value;
                assertCanonicalOrRejected(codec, changed);
            }
        }
        for (int pos = 0; pos <= encoded.length; pos++) {
            final byte[] inserted = new byte[encoded.length + 1];
            System.arraycopy(encoded, 0, inserted, 0, pos);
            System.arraycopy(encoded, pos, inserted, pos + 1, encoded.length - pos);
            for (int value = 0; value <= 255; value++) {
                inserted[pos] = (byte) value;
                assertCanonicalOrRejected(codec, inserted);
            }
        }
    }

    @Test
    void testPadding() {
        assertInvalid(BASE64, "QQ", "QQ=", "QQ===", "QQ=====", "=", "====", "QUJD=", "Q===", "QQ==QQ==");
        assertInvalid(BASE32, "MY", "MY=", "MY=====", "MY=======", "=", "========", "MZXW6YTB=", "M=======", "MY======MY======");
    }

    @Test
    void testPostPaddingDataAtEverySplit() {
        for (final BaseNCodec codec : Arrays.asList(BASE64, BASE32)) {
            final byte[] encoded = StringUtils.getBytesUsAscii(codec.encodeToString(new byte[] {65}) + "JUNK");
            for (int split = 0; split <= encoded.length; split++) {
                final int boundary = split;
                assertThrows(IllegalArgumentException.class, () -> decodeInParts(codec, encoded, boundary));
            }
            final IOException exception = assertThrows(IOException.class, () -> {
                try (InputStream stream = new BaseNCodecInputStream<>(oneByteAtATime(encoded), codec, false)) {
                    while (stream.read() != -1) {
                        // Consume the complete stream to validate its suffix.
                    }
                }
            });
            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertThrows(IOException.class, () -> {
                try (BaseNCodecOutputStream<?, ?, ?> stream = new BaseNCodecOutputStream<>(new ByteArrayOutputStream(), codec, false)) {
                    for (final byte value : encoded) {
                        stream.write(value & 0xff);
                    }
                }
            });
        }
    }

    @Test
    void testTruncatedStreamsFailAtEof() {
        for (final BaseNCodec codec : Arrays.asList(BASE64, BASE32)) {
            final byte[] encoded = codec.encode(new byte[] {65});
            final byte[] truncated = Arrays.copyOf(encoded, encoded.length - 1);
            assertThrows(IOException.class, () -> {
                try (InputStream stream = new BaseNCodecInputStream<>(oneByteAtATime(truncated), codec, false)) {
                    while (stream.read() != -1) {
                        // Consume the stream to validate missing padding at EOF.
                    }
                }
            });
            assertThrows(IOException.class, () -> {
                try (BaseNCodecOutputStream<?, ?, ?> stream = new BaseNCodecOutputStream<>(new ByteArrayOutputStream(), codec, false)) {
                    stream.write(truncated);
                }
            });
        }
    }
}
