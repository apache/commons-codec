/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.binary;

import org.apache.commons.codec.CodecPolicy;

import java.io.OutputStream;

/**
 * Provides Hex encoding and decoding in a streaming fashion (unlimited size).
 * <p>
 * The default behavior of the HexOutputStream is to ENCODE, whereas the default behavior of the
 * {@link Base16InputStream} is to DECODE. But this behavior can be overridden by using a different constructor.
 * </p>
 *
 * @since 1.15
 */
public class Base16OutputStream extends BaseNCodecOutputStream {

    /**
     * Creates a Base16OutputStream such that all data written is Hex-encoded to the original provided OutputStream.
     *
     * @param out OutputStream to wrap.
     */
    public Base16OutputStream(final OutputStream out) {
        this(out, true);
    }

    /**
     * Creates a Base16OutputStream such that all data written is either Hex-encoded or Hex-decoded to the
     * original provided OutputStream.
     *
     * @param out OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     */
    public Base16OutputStream(final OutputStream out, final boolean doEncode) {
        this(out, doEncode, false);
    }

    /**
     * Creates a Base16OutputStream such that all data written is either Hex-encoded or Hex-decoded to the
     * original provided OutputStream.
     *
     * @param out OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet.
     */
    public Base16OutputStream(final OutputStream out, final boolean doEncode,
            final boolean lowerCase) {
        this(out, doEncode, lowerCase, CodecPolicy.LENIENT);
    }

    /**
     * Creates a Base16OutputStream such that all data written is either Hex-encoded or Hex-decoded to the
     * original provided OutputStream.
     *
     * @param out OutputStream to wrap.
     * @param doEncode true if we should encode all data written to us, false if we should decode.
     * @param lowerCase if {@code true} then use a lower-case Base16 alphabet.
     * @param decodingPolicy Decoding policy.
     */
    public Base16OutputStream(final OutputStream out, final boolean doEncode,
            final boolean lowerCase, final CodecPolicy decodingPolicy) {
        super(out, new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
