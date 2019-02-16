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

package org.apache.commons.codec;

import java.io.InputStream;

/**
 * Consider this class package private. Helps load resources.
 *
 * @since 1.12
 */
public class Resources {

    /**
     * Opens the given named resource from the given class.
     *
     * @param name The resource name.
     * @return An input stream.
     */
    public static InputStream getInputStream(final String name) {
        final InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + name);
        }
        return inputStream;
    }
}
