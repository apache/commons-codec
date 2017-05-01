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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.junit.Assert;
import org.junit.Test;

public class BrotliInputStreamTest {

    private static final String BROTLI_ENCODED_AS_HEX = "1b130000a4b0b2ea8147028a";
    private static final String EXPECTED = "XXXXXXXXXXYYYYYYYYYY";

    /**
     * Test bridge works fine
     * @throws DecoderException 
     * @throws {@link IOException}
     */
    @Test
    public void testBrotliDecode() throws IOException, DecoderException {
        BrotliInputStream brotliInputStream = null;
        try {
            brotliInputStream = new BrotliInputStream(
                    new ByteArrayInputStream(Hex.decodeHex(BROTLI_ENCODED_AS_HEX.toCharArray())));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int readByte = -1;
            while((readByte = brotliInputStream.read()) != -1) {
                bos.write(readByte);
            }
            Assert.assertEquals(EXPECTED, new String(bos.toByteArray()));
        } finally {
            if(brotliInputStream != null) {
                try {
                    brotliInputStream.close();
                } catch (Exception e) {
                    // NOOP
                }
            }
        }
    }
}
