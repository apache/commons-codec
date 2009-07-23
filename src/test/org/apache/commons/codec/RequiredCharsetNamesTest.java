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

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Sanity checks.
 * 
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: $
 */
public class RequiredCharsetNamesTest extends TestCase {

    public void testIso8859_1() {
        Assert.assertEquals("ISO-8859-1", RequiredCharsetNames.ISO_8859_1);
    }

    public void testUsAscii() {
        Assert.assertEquals("US-ASCII", RequiredCharsetNames.US_ASCII);
    }

    public void testUtf16() {
        Assert.assertEquals("UTF-16", RequiredCharsetNames.UTF_16);
    }

    public void testUtf16Be() {
        Assert.assertEquals("UTF-16BE", RequiredCharsetNames.UTF_16BE);
    }

    public void testUtf16Le() {
        Assert.assertEquals("UTF-16LE", RequiredCharsetNames.UTF_16LE);
    }

    public void testUtf8() {
        Assert.assertEquals("UTF-8", RequiredCharsetNames.UTF_8);
    }

}
