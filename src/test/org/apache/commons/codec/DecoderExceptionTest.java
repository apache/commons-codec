/*
 * Copyright (C) 1993-2003 SEAGULL
 * 
 * DecoderException.java
 * Created on Jul 25, 2009, 9:28:09 AM
 * 
 */

package org.apache.commons.codec;

import junit.framework.TestCase;

/**
 * Tests DecoderException.
 * 
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: $
 */
public class DecoderExceptionTest extends TestCase {

    private static final String MSG = "TEST";

    private static final Throwable t = new Exception();

    public void testConstructor0() {
        DecoderException e = new DecoderException();
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    public void testConstructorString() {
        DecoderException e = new DecoderException(MSG);
        assertEquals(MSG, e.getMessage());
        assertNull(e.getCause());
    }

    public void testConstructorStringThrowable() {
        DecoderException e = new DecoderException(MSG, t);
        assertEquals(MSG, e.getMessage());
        assertEquals(t, e.getCause());
    }

    public void testConstructorThrowable() {
        DecoderException e = new DecoderException(t);
        assertEquals(t.getClass().getName(), e.getMessage());
        assertEquals(t, e.getCause());
    }

}
