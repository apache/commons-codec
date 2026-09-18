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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link BaseNCodecOutputStream}.
 */
public class BaseNCodecOutputStreamTest extends AbstractBaseNOutputStreamTest {

    @Override
    OutputStream newOutputStream() {
        return new BaseNCodecOutputStream<>(new ByteArrayOutputStream(), new NoOpBaseNCodec(), true);
    }

    @Test
    void testCloseAfterInvalidBase58() throws IOException {
        final boolean[] closed = { false };
        final ByteArrayOutputStream sink = new ByteArrayOutputStream() {
            @Override
            public void close() {
                closed[0] = true;
            }
        };
        final Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(sink).setEncode(false).get();
        stream.write('0');
        final IOException failure = assertThrows(IOException.class, stream::close);
        assertTrue(failure.getCause() instanceof IllegalArgumentException);
        assertTrue(closed[0]);
        assertEquals(0, sink.size());
    }

    @Test
    void testCloseFailureAfterSuccessfulConversion() throws IOException {
        final IOException closeFailure = new IOException("close");
        final ByteArrayOutputStream sink = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                throw closeFailure;
            }
        };
        final Base58OutputStream stream = new Base58OutputStream(sink);
        stream.write(1);
        assertSame(closeFailure, assertThrows(IOException.class, stream::close));
        assertEquals("2", sink.toString("US-ASCII"));
    }

    @Test
    void testCloseFailureSuppressedAfterFlushFailure() {
        final IOException flushFailure = new IOException("flush");
        final IOException closeFailure = new IOException("close");
        final ByteArrayOutputStream sink = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                throw closeFailure;
            }

            @Override
            public void flush() throws IOException {
                throw flushFailure;
            }
        };
        final Base64OutputStream stream = new Base64OutputStream(sink);
        assertSame(flushFailure, assertThrows(IOException.class, stream::close));
        assertEquals(1, flushFailure.getSuppressed().length);
        assertSame(closeFailure, flushFailure.getSuppressed()[0]);
    }

    @Test
    void testCloseFailureSuppressedAfterInvalidBase58() throws IOException {
        final IOException closeFailure = new IOException("close");
        final ByteArrayOutputStream sink = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                throw closeFailure;
            }
        };
        final Base58OutputStream stream = Base58OutputStream.builder().setOutputStream(sink).setEncode(false).get();
        stream.write('0');
        final IOException failure = assertThrows(IOException.class, stream::close);
        assertTrue(failure.getCause() instanceof IllegalArgumentException);
        assertEquals(1, failure.getSuppressed().length);
        assertSame(closeFailure, failure.getSuppressed()[0]);
    }
}
