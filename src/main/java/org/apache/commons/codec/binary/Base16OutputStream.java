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

/**
 * Provides Base16 encoding in a streaming fashion (unlimited size).
 * <p>
 * The default behavior of the Base16OutputStream is to ENCODE, whereas the default behavior of the
 * {@link Base16InputStream} is to DECODE. But this behavior can be overridden by using a different constructor.
 * </p>
 *
 * @since 1.15
 */
public class Base16OutputStream extends BaseNCodecOutputStream {

    /**
     * Constructs a Base16OutputStream such that all data written is Base16-encoded to the original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     */
    public Base16OutputStream(final OutputStream outputStream) {
        this(outputStream, true);
    }

    /**
     * Constructs a Base16OutputStream such that all data written is either Base16-encoded or Base16-decoded to the
     * original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     */
    public Base16OutputStream(final OutputStream outputStream, final boolean doEncode) {
        this(outputStream, doEncode, false);
    }

    /**
     * Constructs a Base16OutputStream such that all data written is either Base16-encoded or Base16-decoded to the
     * original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet.
     */
    public Base16OutputStream(final OutputStream outputStream, final boolean doEncode, final boolean lowerCase) {
        this(outputStream, doEncode, lowerCase, CodecPolicy.LENIENT);
    }

    /**
     * Constructs a Base16OutputStream such that all data written is either Base16-encoded or Base16-decoded to the
     * original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet.
     * @param decodingPolicy Decoding policy.
     */
    public Base16OutputStream(final OutputStream outputStream, final boolean doEncode, final boolean lowerCase, final CodecPolicy decodingPolicy) {
        super(outputStream, new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
