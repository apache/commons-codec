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

import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.BaseNCodecOutputStream.AbstractBuilder; // NOPMD: Required by ECJ (Eclipse)

/**
 * Provides Base58 encoding in a streaming fashion (unlimited size). When encoding the default lineLength is 76 characters and the default lineEnding is CRLF,
 * but these can be overridden by using the appropriate constructor.
 * <p>
 * The default behavior of the Base58OutputStream is to ENCODE, whereas the default behavior of the Base58InputStream is to DECODE. But this behavior can be
 * overridden by using a different constructor.
 * </p>
 * <p>
 * Since this class operates directly on byte streams, and not character streams, it is hard-coded to only encode/decode character encodings which are
 * compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, UTF-8, etc).
 * </p>
 * <p>
 * <strong>Note:</strong> It is mandatory to close the stream after the last byte has been written to it, otherwise the final padding will be omitted and the
 * resulting data will be incomplete/inconsistent.
 * </p>
 * <p>
 * You can set the decoding behavior when the input bytes contain leftover trailing bits that cannot be created by a valid encoding. These can be bits that are
 * unused from the final character or entire characters. The default mode is lenient decoding.
 * </p>
 * <ul>
 * <li>Lenient: Any trailing bits are composed into 8-bit bytes where possible. The remainder are discarded.</li>
 * <li>Strict: The decoding will raise an {@link IllegalArgumentException} if trailing bits are not part of a valid encoding. Any unused bits from the final
 * character must be zero. Impossible counts of entire final characters are not allowed.</li>
 * </ul>
 * <p>
 * When strict decoding is enabled it is expected that the decoded bytes will be re-encoded to a byte array that matches the original, i.e. no changes occur on
 * the final character. This requires that the input bytes use the same padding and alphabet as the encoder.
 * </p>
 *
 * @see Base58
 * @since 1.15
 */
public class Base58OutputStream extends BaseNCodecOutputStream<Base58, Base58OutputStream, Base58OutputStream.Builder> {

    /**
     * Builds instances of Base58OutputStream.
     *
     * @since 1.20.0
     */
    public static class Builder extends AbstractBuilder<Base58OutputStream, Base58, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            setEncode(true);
        }

        @Override
        public Base58OutputStream get() {
            return new Base58OutputStream(this);
        }

        @Override
        protected Base58 newBaseNCodec() {
            return new Base58();
        }
    }

    /**
     * Constructs a new Builder.
     *
     * @return a new Builder.
     * @since 1.20.0
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

    /**
     * Constructs a Base58OutputStream such that all data written is either Base58-encoded or Base58-decoded to the original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     * @param encode     true if we should encode all data written to us, false if we should decode.
     * @deprecated Use {@link #builder()} and {@link Builder}.
     */
    @Deprecated
    public Base58OutputStream(final OutputStream outputStream, final boolean encode) {
        super(builder().setOutputStream(outputStream).setEncode(encode));
    }
}
