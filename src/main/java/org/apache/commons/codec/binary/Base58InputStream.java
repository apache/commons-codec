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

import java.io.InputStream;

/**
 * Provides Base58 decoding through a stream interface.
 *
 * <p>
 * The default behavior of Base58InputStream is to decode, and the default behavior of Base58OutputStream is to encode. The builder can select either
 * behavior with {@code setEncode(boolean)}.
 * </p>
 *
 * <p>
 * Results are available only after EOF. Decoding accepts at most
 * {@link Base58#DEFAULT_MAX_DECODE_LENGTH} encoded bytes by default and throws {@link java.io.IOException} when an input chunk would exceed the cumulative
 * limit. To configure the limit, pass a codec built with {@link Base58.Builder#setMaxDecodeLength(int)} to the stream builder's
 * {@code setBaseNCodec(Base58)} method.
 * </p>
 *
 * <p>
 * Encoding accepts at most {@link Base58#DEFAULT_MAX_ENCODE_LENGTH} binary bytes by default and throws {@link java.io.IOException} when an input chunk would
 * exceed that cumulative limit. Configure it with {@link Base58.Builder#setMaxEncodeLength(int)} on the codec passed to {@code setBaseNCodec(Base58)}.
 * </p>
 * <p>
 * The complete input is retained until EOF. Memory usage is proportional to the accumulated input and conversion output. Configure both input limits
 * appropriately for larger trusted values; encoded output can exceed the decode limit.
 * </p>
 *
 * @see Base58
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-msporny-base58-03">The Base58 Encoding Scheme draft-msporny-base58-03</a>
 * @since 1.22.0
 */
public class Base58InputStream extends BaseNCodecInputStream<Base58, Base58InputStream, Base58InputStream.Builder> {

    /**
     * Builds instances of Base58InputStream.
     */
    public static class Builder extends BaseNCodecInputStream.AbstracBuilder<Base58InputStream, Base58, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        @Override
        public Base58InputStream get() {
            return new Base58InputStream(this);
        }

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

    private Base58InputStream(final Builder builder) {
        super(builder);
    }

    /**
     * Constructs a Base58InputStream such that all data read is Base58-decoded from the original provided InputStream.
     *
     * @param inputStream InputStream to wrap.
     */
    public Base58InputStream(final InputStream inputStream) {
        super(builder().setInputStream(inputStream));
    }
}
