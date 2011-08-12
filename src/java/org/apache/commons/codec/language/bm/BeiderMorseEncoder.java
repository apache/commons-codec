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

package org.apache.commons.codec.language.bm;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * <p>
 * Encodes strings into their Beider-Morse phonetic encoding.
 * </p>
 * <p>
 * Beider-Morse phonetic encodings are optimised for family names. However, they may be useful for a wide range of words.
 * </p>
 * <p>
 * This encoder is intentionally mutable to allow dynamic configuration through bean properties. As such, it is mutable, and may not be
 * thread-safe. If you require a guaranteed thread-safe encoding then use {@link PhoneticEngine} directly.
 * </p>
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class BeiderMorseEncoder implements StringEncoder {
    // a cached object
    private PhoneticEngine engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);

    public Object encode(Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
        }
        return encode((String) source);
    }

    public String encode(String source) throws EncoderException {
        if (source == null) {
            return null;
        }
        return this.engine.encode(source);
    }

    /**
     * Gets the name type currently in operation.
     * 
     * @return the NameType currently being used
     */
    public NameType getNameType() {
        return this.engine.getNameType();
    }

    /**
     * Gets the rule type currently in operation.
     * 
     * @return the RuleType currently being used
     */
    public RuleType getRuleType() {
        return this.engine.getRuleType();
    }

    /**
     * Discovers if multiple possible encodings are concatenated.
     * 
     * @return true if multiple encodings are concatenated, false if just the first one is returned
     */
    public boolean isConcat() {
        return this.engine.isConcat();
    }

    /**
     * Sets how multiple possible phonetic encodings are combined.
     * 
     * @param concat
     *            true if multiple encodings are to be combined with a '|', false if just the first one is to be considered
     */
    public void setConcat(boolean concat) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat);
    }

    /**
     * Sets the type of name. Use {@link NameType#GENERIC} unless you specifically want phoentic encodings optimized for Ashkenazi or
     * Sephardic Jewish family names.
     * 
     * @param nameType
     *            the NameType in use
     */
    public void setNameType(NameType nameType) {
        this.engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat());
    }

    /**
     * Sets the rule type to apply. This will widen or narrow the range of phonetic encodings considered.
     * 
     * @param ruleType
     *            {@link RuleType#APPROX} or {@link RuleType#EXACT} for approximate or exact phonetic matches
     */
    public void setRuleType(RuleType ruleType) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat());
    }

}
