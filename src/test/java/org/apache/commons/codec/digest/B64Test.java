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
package org.apache.commons.codec.digest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class B64Test {

    @Test
    public void testB64T() {
        assertNotNull(new B64()); // for the 100% code coverage :)
        assertEquals(64, B64.B64T_ARRAY.length);
    }

    @Test
    public void testB64from24bit() {
        final StringBuilder buffer = new StringBuilder("");
        B64.b64from24bit((byte) 8, (byte) 16, (byte) 64, 2, buffer);
        B64.b64from24bit((byte) 7, (byte) 77, (byte) 120, 4, buffer);
        assertEquals("./spo/", buffer.toString());
    }
}
