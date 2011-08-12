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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Encodes a string into a Caverphone value.
 * 
 * This is an algorithm created by the Caversham Project at the University of Otago. It implements the Caverphone 2.0
 * algorithm:
 * 
 * @author Apache Software Foundation
 * @version $Id: Caverphone.java 1075947 2011-03-01 17:56:14Z ggregory $
 * @see <a href="http://en.wikipedia.org/wiki/Caverphone">Wikipedia - Caverphone</a>
 * @since 1.5
 */
public abstract class AbstractCaverphone implements StringEncoder {

    /**
     * Creates an instance of the Caverphone encoder
     */
    public AbstractCaverphone() {
        super();
    }

    /**
     * Tests if the encodings of two strings are equal.
     * 
     * This method might be promoted to a new AbstractStringEncoder superclass.
     * 
     * @param str1
     *            First of two strings to compare
     * @param str2
     *            Second of two strings to compare
     * @return <code>true</code> if the encodings of these strings are identical, <code>false</code> otherwise.
     * @throws EncoderException
     */
    public boolean isEncodeEqual(String str1, String str2) throws EncoderException {
        return this.encode(str1).equals(this.encode(str2));
    }

}
