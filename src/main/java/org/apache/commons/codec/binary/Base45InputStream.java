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
 * Provides <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285 Base45</a> decoding in a streaming fashion (unlimited size).
 *
 * @see Base45
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9285">RFC 9285 – The Base45 Data Encoding</a>
 * @since 1.23.0
 */
public class Base45InputStream extends BaseNCodecInputStream<Base45, Base45InputStream, Base45InputStream.Builder> {

    /**
     * Builds instances of Base45InputStream.
     */
    public static class Builder extends BaseNCodecInputStream.AbstracBuilder<Base45InputStream, Base45, Builder> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        @Override
        public Base45InputStream get() {
            return new Base45InputStream(this);
        }

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

    private Base45InputStream(final Builder builder) {
        super(builder);
    }

    /**
     * Constructs a Base45InputStream such that all data read is Base45-decoded from the original provided InputStream.
     *
     * @param inputStream InputStream to wrap.
     */
    public Base45InputStream(final InputStream inputStream) {
        super(builder().setInputStream(inputStream));
    }
}
