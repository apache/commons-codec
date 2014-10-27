/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.commons.codec.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.commons.codec.Charsets;
import org.junit.Test;

public class Base32Test {

    private static final String [][] BASE32_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"MY======"},
        {"fo"     ,"MZXQ===="},
        {"foo"    ,"MZXW6==="},
        {"foob"   ,"MZXW6YQ="},
        {"fooba"  ,"MZXW6YTB"},
        {"foobar" ,"MZXW6YTBOI======"},
    };

    private static final String [][] BASE32HEX_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"CO======"},
        {"fo"     ,"CPNG===="},
        {"foo"    ,"CPNMU==="},
        {"foob"   ,"CPNMUOG="},
        {"fooba"  ,"CPNMUOJ1"},
        {"foobar" ,"CPNMUOJ1E8======"},
    };


    private static final String [][] BASE32_TEST_CASES_CHUNKED = { //Chunked
        {""       ,""},
        {"f"      ,"MY======\r\n"},
        {"fo"     ,"MZXQ====\r\n"},
        {"foo"    ,"MZXW6===\r\n"},
        {"foob"   ,"MZXW6YQ=\r\n"},
        {"fooba"  ,"MZXW6YTB\r\n"},
        {"foobar" ,"MZXW6YTBOI======\r\n"},
    };

    private static final String [][] BASE32_PAD_TEST_CASES = { // RFC 4648
        {""       ,""},
        {"f"      ,"MY%%%%%%"},
        {"fo"     ,"MZXQ%%%%"},
        {"foo"    ,"MZXW6%%%"},
        {"foob"   ,"MZXW6YQ%"},
        {"fooba"  ,"MZXW6YTB"},
        {"foobar" ,"MZXW6YTBOI%%%%%%"},
    };

    @Test
    public void testBase32Samples() throws Exception {
        final Base32 codec = new Base32();
        for (final String[] element : BASE32_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(Charsets.UTF_8)));
        }
    }

    @Test
    public void testBase32HexSamples() throws Exception {
        final Base32 codec = new Base32(true);
        for (final String[] element : BASE32HEX_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(Charsets.UTF_8)));
        }
    }

    @Test
    public void testBase32Chunked () throws Exception {
        final Base32 codec = new Base32(20);
        for (final String[] element : BASE32_TEST_CASES_CHUNKED) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(Charsets.UTF_8)));
        }
    }

    @Test
    public void testSingleCharEncoding() {
        for (int i = 0; i < 20; i++) {
            Base32 codec = new Base32();
            final BaseNCodec.Context context = new BaseNCodec.Context();
            final byte unencoded[] = new byte[i];
            final byte allInOne[] = codec.encode(unencoded);
            codec = new Base32();
            for (int j=0; j< unencoded.length; j++) {
                codec.encode(unencoded, j, 1, context);
            }
            codec.encode(unencoded, 0, -1, context);
            final byte singly[] = new byte[allInOne.length];
            codec.readResults(singly, 0, 100, context);
            if (!Arrays.equals(allInOne, singly)){
                fail();
            }
        }
    }

    @Test
    public void testRandomBytes() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32();
            final byte[][] b = Base32TestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testRandomBytesChunked() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32(10);
            final byte[][] b = Base32TestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testRandomBytesHex() {
        for (int i = 0; i < 20; i++) {
            final Base32 codec = new Base32(true);
            final byte[][] b = Base32TestData.randomData(codec, i);
            assertEquals(""+i+" "+codec.lineLength,b[1].length,codec.getEncodedLength(b[0]));
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    public void testBase32SamplesNonDefaultPadding() throws Exception {
        final Base32 codec = new Base32((byte)0x25); // '%' <=> 0x25

        for (final String[] element : BASE32_PAD_TEST_CASES) {
                assertEquals(element[1], codec.encodeAsString(element[0].getBytes(Charsets.UTF_8)));
        }
    }
}
