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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.function.IORunnable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.SetSystemProperty;

/**
 * Tests single-pass standard-input hashing and preservation of other command-line inputs.
 */
@Isolated("Replaces System.in and System.out")
class DigestStreamingTest {

    /**
     * Generates data without storing it, supplies short reads, and rejects replay or reads after physical EOF.
     */
    private static final class GeneratedInputStream extends InputStream {
        private final long length;
        private final int chunkSize;
        private final long failureAt;
        private final IOException failure = new IOException("Standard input failed");
        private long position;
        private int eofReturns;
        private boolean closed;

        GeneratedInputStream(final long length, final int chunkSize) {
            this(length, chunkSize, -1);
        }

        GeneratedInputStream(final long length, final int chunkSize, final long failureAt) {
            this.length = length;
            this.chunkSize = chunkSize;
            this.failureAt = failureAt;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public synchronized void mark(final int readLimit) {
            throw new AssertionError("Standard input must not be marked");
        }

        @Override
        public int read() throws IOException {
            final byte[] one = new byte[1];
            return read(one, 0, 1) == -1 ? -1 : one[0] & 0xff;
        }

        @Override
        public int read(final byte[] buffer, final int offset, final int count) throws IOException {
            if (count == 0) {
                return 0;
            }
            if (position == failureAt) {
                throw failure;
            }
            if (position == length) {
                if (eofReturns++ != 0) {
                    throw new AssertionError("Standard input was read after EOF");
                }
                return -1;
            }
            final long remaining = (failureAt < 0 ? length : failureAt) - position;
            final int size = (int) Math.min(Math.min(count, chunkSize), remaining);
            for (int i = 0; i < size; i++) {
                buffer[offset + i] = valueAt(position++);
            }
            return size;
        }

        @Override
        public synchronized void reset() {
            throw new AssertionError("Standard input must not be reset");
        }
    }

    /**
     * Runs the public CLI in a small heap to detect retention of the complete standard input.
     */
    public static final class StreamingProcess {
        /**
         * Hashes generated standard input with every available algorithm.
         *
         * @param args The number of input bytes.
         * @throws IOException Thrown on input failure.
         */
        public static void main(final String[] args) throws IOException {
            final long length = Long.parseLong(args[0]);
            final GeneratedInputStream source = new GeneratedInputStream(length, 8192);
            System.setIn(source);
            Digest.main(new String[] { "ALL" });
            if (source.position != length || source.eofReturns != 1 || source.closed) {
                throw new AssertionError("Standard input must be consumed once and left open");
            }
        }
    }

    private static List<String> availableAlgorithms() {
        final List<String> algorithms = new ArrayList<>();
        for (final String algorithm : MessageDigestAlgorithms.values()) {
            try {
                MessageDigest.getInstance(algorithm);
                algorithms.add(algorithm);
            } catch (final NoSuchAlgorithmException ignored) {
                // ALL skips algorithms unavailable on the current runtime.
            }
        }
        return algorithms;
    }

    private static String capture(final InputStream source, final IORunnable command) throws IOException {
        final InputStream originalIn = System.in;
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8.name())) {
            try {
                System.setIn(source);
                System.setOut(output);
                command.run();
            } finally {
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
        }
        return captured.toString(StandardCharsets.UTF_8.name());
    }

    private static String expectedAll(final byte[] data) throws NoSuchAlgorithmException {
        return expectedAll(new byte[][] { data }, new String[] { null });
    }

    private static String expectedAll(final byte[][] inputs, final String[] names) throws NoSuchAlgorithmException {
        final StringBuilder expected = new StringBuilder();
        for (final String algorithm : availableAlgorithms()) {
            for (int i = 0; i < inputs.length; i++) {
                expected.append(algorithm).append(' ').append(Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(inputs[i])));
                if (names[i] != null) {
                    expected.append("  ").append(names[i]);
                }
                expected.append(System.lineSeparator());
            }
        }
        return expected.toString();
    }

    private static byte[] payload(final int length) {
        final byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = valueAt(i);
        }
        return bytes;
    }

    static Stream<Arguments> replayLimitInputs() {
        return Stream.of("ALL", "all", "aLl", "*").flatMap(selector ->
            IntStream.of(8191, 8192, 8193, 32769).mapToObj(length -> Arguments.of(selector, length)));
    }

    static Stream<Arguments> standardInputs() {
        return Stream.of("ALL", "all", "aLl", "*").flatMap(selector ->
            IntStream.of(0, 1, 15, 16, 17, 63, 64, 65, 127, 128, 129, 1023, 1024, 1025, 8191, 8192, 8193, 16385).boxed()
                .flatMap(length -> IntStream.of(1, 7, 8192).mapToObj(chunk -> Arguments.of(selector, length, chunk))));
    }

    private static byte valueAt(final long position) {
        return (byte) (position * 31 + 7);
    }

    @ParameterizedTest(name = "{0}, length={1}, chunk={2}")
    @MethodSource("standardInputs")
    void testAllAlgorithmsConsumeStandardInputOnce(final String selector, final int length, final int chunkSize) throws Exception {
        final GeneratedInputStream source = new GeneratedInputStream(length, chunkSize);
        assertFalse(source.markSupported());
        final String output = capture(source, () -> Digest.main(new String[] { selector }));
        assertEquals(expectedAll(payload(length)), output);
        assertEquals(length, source.position);
        assertEquals(1, source.eofReturns);
        assertFalse(source.closed, "The CLI must not close standard input");
    }

    @Test
    void testAllAlgorithmsUseBoundedMemory(@TempDir final Path tempDir) throws Exception {
        // The input exceeds the child's entire heap; retaining it for replay must fail.
        final int length = 32 * 1024 * 1024 + 1;
        final Path java = Paths.get(System.getProperty("java.home"), "bin", File.separatorChar == '\\' ? "java.exe" : "java");
        final String classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        final Path errors = tempDir.resolve("errors.txt");
        final Process process = new ProcessBuilder(java.toString(), "-Xmx32m", "-cp", classpath, StreamingProcess.class.getName(),
                Integer.toString(length)).redirectError(errors.toFile()).start();
        try {
            assertTrue(process.waitFor(60, TimeUnit.SECONDS), "The bounded-memory hashing process timed out");
            final ByteArrayOutputStream captured = new ByteArrayOutputStream();
            try (InputStream output = process.getInputStream()) {
                final byte[] buffer = new byte[1024];
                int read;
                while ((read = output.read(buffer)) != -1) {
                    captured.write(buffer, 0, read);
                }
            }
            final String output = captured.toString(StandardCharsets.UTF_8.name());
            assertEquals(0, process.exitValue(), output + new String(Files.readAllBytes(errors), StandardCharsets.UTF_8));
            final List<String> algorithms = availableAlgorithms();
            final String[] lines = output.split("\\r?\\n");
            assertEquals(algorithms.size(), lines.length, output);
            for (int i = 0; i < lines.length; i++) {
                assertTrue(lines[i].startsWith(algorithms.get(i) + " "), output);
            }
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            final byte[] buffer = new byte[8192];
            try (InputStream input = new GeneratedInputStream(length, buffer.length)) {
                int read;
                while ((read = input.read(buffer)) != -1) {
                    sha256.update(buffer, 0, read);
                }
            }
            assertTrue(output.contains("SHA-256 " + Hex.encodeHexString(sha256.digest()) + System.lineSeparator()), output);
        } finally {
            process.destroyForcibly();
        }
    }

    @ParameterizedTest(name = "{0}, length={1}")
    @MethodSource("replayLimitInputs")
    @SetSystemProperty(key = "org.apache.commons.codec.cli.Digest.markReadLimit", value = "8192")
    void testFormerMarkLimitDoesNotLimitInput(final String selector, final int length) throws Exception {
        final byte[] data = payload(length);
        // Allow repeated EOF reads so this regression isolates mark invalidation from the single-pass assertions.
        final ByteArrayInputStream source = new ByteArrayInputStream(data);
        assertEquals(expectedAll(data), capture(source, () -> Digest.main(new String[] { selector })));
    }

    @ParameterizedTest
    @ValueSource(strings = { "ALL", "all", "aLl", "*" })
    void testOtherInputsPreserveOutputOrderAndDoNotReadStandardInput(final String selector, @TempDir final Path tempDir) throws Exception {
        final byte[] first = payload(257);
        final byte[] second = payload(1025);
        final Path file = Files.write(tempDir.resolve("first.bin"), first);
        final Path directory = Files.createDirectory(tempDir.resolve("directory"));
        Files.write(directory.resolve("second.bin"), second);
        final Path subdirectory = Files.createDirectory(directory.resolve("ignored"));
        Files.write(subdirectory.resolve("ignored.bin"), payload(1));
        final Path emptyDirectory = Files.createDirectory(tempDir.resolve("empty"));
        final String literal = "Digest CLI literal with spaces and \u00e9";
        final GeneratedInputStream source = new GeneratedInputStream(1, 1, 0);
        final String output = capture(source, () -> Digest.main(new String[] { selector, file.toString(), literal, directory.toString(),
                emptyDirectory.toString() }));
        assertEquals(expectedAll(new byte[][] { first, literal.getBytes(Charset.defaultCharset()), second },
                new String[] { file.toString(), null, "second.bin" }), output);
        assertEquals(0, source.position);
        assertEquals(0, source.eofReturns);
        assertFalse(source.closed);
    }

    @ParameterizedTest
    @ValueSource(strings = { "MD5", "md5", "SHA-256" })
    void testSingleAlgorithmStandardInput(final String algorithm) throws Exception {
        for (final int length : new int[] { 0, 1, 8193 }) {
            final GeneratedInputStream source = new GeneratedInputStream(length, 7);
            final String expected = Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(payload(length))) + System.lineSeparator();
            assertEquals(expected, capture(source, () -> Digest.main(new String[] { algorithm })));
            assertEquals(length, source.position);
            assertEquals(1, source.eofReturns);
            assertFalse(source.closed);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "ALL", "all", "aLl", "*", "SHA-256" })
    void testStandardInputFailureIsPropagatedWithoutOutput(final String selector) throws IOException {
        for (final int failureAt : new int[] { 0, 1, 8192, 8193 }) {
            final GeneratedInputStream source = new GeneratedInputStream(failureAt + 1, 7, failureAt);
            final String output = capture(source, () ->
                assertSame(source.failure, assertThrows(IOException.class, () -> Digest.main(new String[] { selector }))));
            assertEquals("", output);
            assertEquals(failureAt, source.position);
            assertFalse(source.closed, "Failure must not close standard input");
        }
    }
}
