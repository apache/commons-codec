/*
 * Copyright (C) 1993-2003 SEAGULL
 * 
 * AllTests.java
 * Created on Nov 5, 2003, 8:25:55 PM
 * 
 */
 
package org.apache.commons.codec.language;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests all test cases in this package.
 * 
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: AllTests.java,v 1.1 2003/11/06 16:31:47 ggregory Exp $
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
