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

import java.io.OutputStream;

/**
 * Provides Base58 encoding through a stream interface.
 *
 * <p>The default behavior of Base58InputStream is to decode, and the default behavior of Base58OutputStream is to encode. The builder can select either
 * behavior with {@code setEncode(boolean)}.</p>
 *
 * <p>Results are available only after EOF. Decoding accepts at most
 * {@link Base58#DEFAULT_MAX_DECODE_LENGTH} encoded bytes by default and throws {@link java.io.IOException} when an input chunk would exceed the cumulative
 * limit. To configure the limit, pass a codec built with {@link Base58.Builder#setMaxDecodeLength(int)} to the stream builder's
 * {@code setBaseNCodec(Base58)} method.</p>
 *
 * <p>Encoding has no input limit. Callers should bound untrusted binary input before encoding, and explicitly raise the decode limit when decoding larger
 * trusted values. Encoded output can exceed the default decode limit.</p>
 *
 * <p>Close the output stream or call {@link #eof()} after the last write to complete conversion.</p>
 *
 * @see Base58
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-msporny-base58-03">The Base58 Encoding Scheme draft-msporny-base58-03</a>
 * @since 1.22.0
 */
public class Base58OutputStream extends BaseNCodecOutputStream<Base58, Base58OutputStream, Base58OutputStream.Builder> {

    /**
     * Builds instances of Base58OutputStream.
     */
    public static class Builder extends BaseNCodecOutputStream.AbstractBuilder<Base58OutputStream, Base58, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            setEncode(true);
        }

        /**
         * Builds a new Base58OutputStream instance with the configured settings.
         *
         * @return A new Base58OutputStream.
         */
        @Override
        public Base58OutputStream get() {
            return new Base58OutputStream(this);
        }

        /**
         * Creates a new Base58 codec instance.
         *
         * @return A new Base58 codec.
         */
        @Override
        protected Base58 newBaseNCodec() {
            return new Base58();
        }
    }

    /**
     * Constructs a new Builder.
     *
     * @return A new Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    private Base58OutputStream(final Builder builder) {
        super(builder);
    }

    /**
     * Constructs a Base58OutputStream such that all data written is Base58-encoded to the original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     */
    public Base58OutputStream(final OutputStream outputStream) {
        this(builder().setOutputStream(outputStream));
    }

}
