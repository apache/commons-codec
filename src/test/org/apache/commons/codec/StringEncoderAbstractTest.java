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

import java.util.Locale;

import junit.framework.TestCase;

/**
 * @author Apache Software Foundation
 * @version $Id$
 */
public abstract class StringEncoderAbstractTest extends TestCase {

    public StringEncoderAbstractTest(String name) {
        super(name);
    }

    protected abstract StringEncoder makeEncoder();

    // ------------------------------------------------------------------------

    public void testEncodeEmpty() throws Exception {
        Encoder encoder = makeEncoder();
        encoder.encode("");
        encoder.encode(" ");
        encoder.encode("\t");
    }        

    public void testEncodeNull() throws Exception {
        StringEncoder encoder = makeEncoder();
        
        try {
            encoder.encode(null);
        } catch( EncoderException ee ) {
            // An exception should be thrown
        }
    }        

    public void testEncodeWithInvalidObject() throws Exception {

        boolean exceptionThrown = false;
        try {
            StringEncoder encoder = makeEncoder();
            encoder.encode( new Float( 3.4 ) );
        } catch( Exception e ) {
            exceptionThrown = true;
        }

        assertTrue( "An exception was not thrown when we tried to encode " +
                    "a Float object", exceptionThrown );
    }

    public void testLocaleIndependence() throws Exception {
        StringEncoder encoder = makeEncoder();

        String[] data = { "I", "i", };

        Locale orig = Locale.getDefault();
        Locale[] locales = { Locale.ENGLISH, new Locale("tr"), Locale.getDefault() };

        try {
            for (int i = 0; i < data.length; i++) {
                String ref = null;
                for (int j = 0; j < locales.length; j++) {
                    Locale.setDefault(locales[j]);
                    if (j <= 0) {
                        ref = encoder.encode(data[i]);
                    } else {
                        String cur = null;
                        try {
                            cur = encoder.encode(data[i]);
                        } catch (Exception e) {
                            fail(Locale.getDefault().toString() + ": " + e.getMessage());
                        }
                        assertEquals(Locale.getDefault().toString() + ": ", ref, cur);
                    }
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }

}
