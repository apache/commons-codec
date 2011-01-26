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

import junit.framework.TestCase;

public class Base32Test extends TestCase {
    
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


    public void testBase32Samples() throws Exception {
        for (int i = 0; i < BASE32_TEST_CASES.length; i++) {
                assertEquals(BASE32_TEST_CASES[i][1], Base32.encodeBase32String(BASE32_TEST_CASES[i][0].getBytes("UTF-8")));
        }
    }

    public void testBase32HexSamples() throws Exception {
        for (int i = 0; i < BASE32HEX_TEST_CASES.length; i++) {
                assertEquals(BASE32HEX_TEST_CASES[i][1], Base32.encodeBase32HexString(BASE32HEX_TEST_CASES[i][0].getBytes("UTF-8")));
        }
    }

}
