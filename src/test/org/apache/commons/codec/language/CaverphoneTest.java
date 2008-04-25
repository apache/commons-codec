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

package org.apache.commons.codec.language;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * @author Apache Software Foundation
 * @version $Id$
 */
public class CaverphoneTest extends StringEncoderAbstractTest {

    public static Test suite() {
        return new TestSuite(CaverphoneTest.class);
    }

    public CaverphoneTest(String name) {
        super(name);
    }

    protected StringEncoder makeEncoder() {
        return new Caverphone();
    }

    public void testSpecificationExamples() {
        Caverphone caverphone = new Caverphone();
        String[][] data = {
            {"Stevenson", "STFNSN1111"},
            {"Peter",     "PTA1111111"},
            {"ready",     "RTA1111111"},
            {"social",    "SSA1111111"},
            {"able",      "APA1111111"},
            {"Tedder",    "TTA1111111"},
            {"Karleen",   "KLN1111111"},
            {"Dyun",      "TN11111111"},
        };

        for(int i=0; i<data.length; i++) {
            assertEquals( data[i][1], caverphone.caverphone(data[i][0]) );
        }
    }

}
