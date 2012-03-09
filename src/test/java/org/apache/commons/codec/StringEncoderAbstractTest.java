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

import junit.framework.Assert;

import org.junit.Test;

/**
 * @version $Id$
 */
public abstract class StringEncoderAbstractTest {

    protected StringEncoder stringEncoder = this.createStringEncoder();

    public void checkEncoding(String expected, String source) throws EncoderException {
        Assert.assertEquals("Source: " + source, expected, this.getStringEncoder().encode(source));
    }

    protected void checkEncodings(String[][] data) throws EncoderException {
        for (String[] element : data) {
            this.checkEncoding(element[1], element[0]);
        }
    }

    protected void checkEncodingVariations(String expected, String data[]) throws EncoderException {
        for (String element : data) {
            this.checkEncoding(expected, element);
        }
    }

    protected abstract StringEncoder createStringEncoder();

    public StringEncoder getStringEncoder() {
        return this.stringEncoder;
    }

    @Test
    public void testEncodeEmpty() throws Exception {
        Encoder encoder = this.getStringEncoder();
        encoder.encode("");
        encoder.encode(" ");
        encoder.encode("\t");
    }

    @Test
    public void testEncodeNull() throws Exception {
        StringEncoder encoder = this.getStringEncoder();
        try {
            encoder.encode(null);
        } catch (EncoderException ee) {
            // An exception should be thrown
        }
    }

    @Test
    public void testEncodeWithInvalidObject() throws Exception {
        boolean exceptionThrown = false;
        try {
            StringEncoder encoder = this.getStringEncoder();
            encoder.encode(new Float(3.4));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue("An exception was not thrown when we tried to encode " + "a Float object", exceptionThrown);
    }

    @Test
    public void testLocaleIndependence() throws Exception {
        StringEncoder encoder = this.getStringEncoder();

        String[] data = {"I", "i",};

        Locale orig = Locale.getDefault();
        Locale[] locales = {Locale.ENGLISH, new Locale("tr"), Locale.getDefault()};

        try {
            for (String element : data) {
                String ref = null;
                for (int j = 0; j < locales.length; j++) {
                    Locale.setDefault(locales[j]);
                    if (j <= 0) {
                        ref = encoder.encode(element);
                    } else {
                        String cur = null;
                        try {
                            cur = encoder.encode(element);
                        } catch (Exception e) {
                            Assert.fail(Locale.getDefault().toString() + ": " + e.getMessage());
                        }
                        Assert.assertEquals(Locale.getDefault().toString() + ": ", ref, cur);
                    }
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }

}
