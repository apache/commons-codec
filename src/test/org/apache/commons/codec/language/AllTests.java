/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

/**
 * Tests all test cases in this package.
 * 
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: AllTests.java,v 1.4 2004/02/23 07:32:50 ggregory Exp $
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.apache.commons.codec.language");
        //$JUnit-BEGIN$
        suite.addTest(MetaphoneTest.suite());
        suite.addTest(SoundexTest.suite());
        suite.addTest(RefinedSoundexTest.suite());
        suite.addTest(DoubleMetaphoneTest.suite());
        //$JUnit-END$
        return suite;
    }
}
