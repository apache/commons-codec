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

package org.apache.commons.codec;

import java.io.InputStream;

/**
 * Consider this class package private. Helps load resources.
 *
 * @since 1.12
 */
public class Resources {

    /**
     * Gets an InputStream on the contents of the resource specified by {@code name}. The mapping between the resource name and the stream is managed by this
     * class's class loader.
     *
     * @param name The resource name.
     * @return An input stream.
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream getInputStream(final String name) {
        // Use java.lang.Class.getResourceAsStream(String) to make JPMS happy
        final InputStream inputStream = Resources.class.getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + name);
        }
        return inputStream;
    }

    /**
     * TODO Make private in 2.0.
     *
     * @deprecated TODO Make private in 2.0.
     */
    @Deprecated
    public Resources() {
        // empty
    }
}
