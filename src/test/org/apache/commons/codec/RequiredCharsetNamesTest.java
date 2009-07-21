/*
 * Copyright (C) 1993-2003 SEAGULL
 * 
 * RequiredCharsetNamesTest.java
 * Created on Jul 20, 2009, 6:08:58 PM
 * 
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
