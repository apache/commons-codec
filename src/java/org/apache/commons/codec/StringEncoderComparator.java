/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Commons", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.codec;

import java.util.Comparator;

/**
 * String are comparable, and this comparator allows 
 * you to configure it with an instance of a class
 * which implements the StringEncoder.  This comparator
 * is used to sort Strings by an encoding shceme such
 * as Soundex, Metaphone, etc.  This class can come in
 * handy if one need to sort Strings by an encoded
 * form of a name such as Soundex.
 *
 * @author Tim O'Brien
 * @author Gary Gregory
 * @version $Id: StringEncoderComparator.java,v 1.4 2003/08/14 07:40:17 ggregory Exp $
 */
public class StringEncoderComparator implements Comparator {

    /**
     * Internal encoder instance.
     */
    private StringEncoder stringEncoder;

    /**
     * Constructs a new instance.
     */
    public StringEncoderComparator() {
    }

    /**
     * Constructs a new instance with the given soundex algorithm.
     */
    public StringEncoderComparator(StringEncoder en) {
        this.stringEncoder = en;
    }

    /**
     * Compares 2 strings based not on the strings 
     * themselves, but on an encoding of the two 
     * strings using the StringEncoder this Comparator
     * was created with.
     */
    public int compare(Object o1, Object o2) {

        int compareCode = 0;

        try {
            String s1 = stringEncoder.encode(o1.toString());
            String s2 = stringEncoder.encode(o2.toString());
            compareCode = s1.compareTo(s2);
        } 
        catch (EncoderException ee) {
            compareCode = 0;
        }
        return (compareCode);
    }

}
