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
 * Provides <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285 Base45</a> encoding in a streaming fashion (unlimited size).
 *
 * @see Base45
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285 – The Base45 Data Encoding</a>
 * @since 1.23.0
 */
public class Base45OutputStream extends BaseNCodecOutputStream<Base45, Base45OutputStream, Base45OutputStream.Builder> {

    /**
     * Builds instances of Base45OutputStream.
     */
    public static class Builder extends BaseNCodecOutputStream.AbstractBuilder<Base45OutputStream, Base45, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            setEncode(true);
        }

        /**
         * Builds a new Base45OutputStream instance with the configured settings.
         *
         * @return A new Base45OutputStream.
         */
        @Override
        public Base45OutputStream get() {
            return new Base45OutputStream(this);
        }

        /**
         * Creates a new Base45 codec instance.
         *
         * @return A new Base45 codec.
         */
        @Override
        protected Base45 newBaseNCodec() {
            return new Base45();
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

    private Base45OutputStream(final Builder builder) {
        super(builder);
    }

    /**
     * Constructs a Base45OutputStream such that all data written is Base45-encoded to the original provided OutputStream.
     *
     * @param outputStream OutputStream to wrap.
     */
    public Base45OutputStream(final OutputStream outputStream) {
        this(builder().setOutputStream(outputStream));
    }
}
