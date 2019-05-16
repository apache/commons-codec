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

import java.io.IOException;
import java.io.InputStream;

/**
 * Emits three line-feeds '\n' in a row, one at a time, and then EOF.
 *
 * Recreates the bug described in CODEC-105.
 *
 * @since 1.5
 */
public class Codec105ErrorInputStream extends InputStream {
    private static final int EOF = -1;

    int countdown = 3;

    @Override
    public int read() throws IOException {
        if (this.countdown-- > 0) {
            return '\n';
        }
        return EOF;
    }

    @Override
    public int read(final byte b[], final int pos, final int len) throws IOException {
        if (this.countdown-- > 0) {
            b[pos] = '\n';
            return 1;
        }
        return EOF;
    }
}
