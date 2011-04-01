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

import org.apache.commons.codec.StringEncoder;

/**
 * Tests Caverphone2.
 * 
 * @author Apache Software Foundation
 * @version $Id: CaverphoneTest.java 1075947 2011-03-01 17:56:14Z ggregory $
 * @since 1.5
 */
public class Caverphone2Test extends CaverphoneTest {

    protected StringEncoder createStringEncoder() {
        return new Caverphone2();
    }

}
