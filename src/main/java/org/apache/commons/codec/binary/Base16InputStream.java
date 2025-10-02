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

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.BaseNCodecInputStream.AbstracBuilder; // NOPMD: Required by ECJ (Eclipse)

/**
 * Provides Base16 decoding in a streaming fashion (unlimited size).
 * <p>
 * The default behavior of the Base16InputStream is to DECODE, whereas the default behavior of the {@link Base16OutputStream} is to ENCODE, but this behavior
 * can be overridden by using a different constructor.
 * </p>
 *
 * @see Base16
 * @since 1.15
 */
public class Base16InputStream extends BaseNCodecInputStream<Base16, Base16InputStream, Base16InputStream.Builder> {

    /**
     * Builds instances of Base16InputStream.
     */
    public static class Builder extends AbstracBuilder<Base16InputStream, Base16, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        @Override
        public Base16InputStream get() {
            return new Base16InputStream(this);
        }

        @Override
        protected Base16 newBaseNCodec() {
            return new Base16();
        }
    }

    /**
     * Constructs a new Builder.
     *
     * @return a new Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    private Base16InputStream(final Builder builder) {
        super(builder);
    }

    /**
     * Constructs a Base16InputStream such that all data read is Base16-decoded from the original provided InputStream.
     *
     * @param inputStream InputStream to wrap.
     */
    public Base16InputStream(final InputStream inputStream) {
        super(builder().setInputStream(inputStream));
    }

    /**
     * Constructs a Base16InputStream such that all data read is either Base16-encoded or Base16-decoded from the original provided InputStream.
     *
     * @param inputStream InputStream to wrap.
     * @param encode    true if we should encode all data read from us, false if we should decode.
     */
    @Deprecated
    public Base16InputStream(final InputStream inputStream, final boolean encode) {
        super(builder().setInputStream(inputStream).setEncode(encode));
    }

    /**
     * Constructs a Base16InputStream such that all data read is either Base16-encoded or Base16-decoded from the original provided InputStream.
     *
     * @param inputStream InputStream to wrap.
     * @param encode    true if we should encode all data read from us, false if we should decode.
     * @param lowerCase   if {@code true} then use a lower-case Base16 alphabet.
     */
    @Deprecated
    public Base16InputStream(final InputStream inputStream, final boolean encode, final boolean lowerCase) {
        super(builder().setInputStream(inputStream).setEncode(encode).setBaseNCodec(new Base16(lowerCase)));
    }

    /**
     * Constructs a Base16InputStream such that all data read is either Base16-encoded or Base16-decoded from the original provided InputStream.
     *
     * @param inputStream    InputStream to wrap.
     * @param encode       true if we should encode all data read from us, false if we should decode.
     * @param lowerCase      if {@code true} then use a lower-case Base16 alphabet.
     * @param decodingPolicy Decoding policy.
     */
    @Deprecated
    public Base16InputStream(final InputStream inputStream, final boolean encode, final boolean lowerCase, final CodecPolicy decodingPolicy) {
        super(builder().setInputStream(inputStream).setEncode(encode).setBaseNCodec(new Base16(lowerCase, decodingPolicy)));
    }
}
