/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.language.bm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BeiderMorseEncoder.Builder} and {@link BeiderMorseEncoder#builder()}.
 */
class BeiderMorseEncoderBuilderTest {

    /**
     * Tests that each call to {@code get()} on the same builder returns a distinct encoder instance.
     */
    @Test
    void testBuilderGetReturnsDifferentInstances() {
        final BeiderMorseEncoder.Builder builder = BeiderMorseEncoder.builder();
        assertNotSame(builder.get(), builder.get());
    }

    /**
     * Tests that the builder's {@code get()} method returns a non-null encoder with default settings.
     */
    @Test
    void testBuilderGetReturnsNonNull() {
        assertNotNull(BeiderMorseEncoder.builder().get());
    }

    /**
     * Tests that {@link BeiderMorseEncoder#builder()} returns a non-null builder.
     */
    @Test
    void testBuilderIsNotNull() {
        assertNotNull(BeiderMorseEncoder.builder());
    }

    /**
     * Tests that each call to {@link BeiderMorseEncoder#builder()} returns a distinct builder instance.
     */
    @Test
    void testBuilderReturnsDifferentInstances() {
        assertNotSame(BeiderMorseEncoder.builder(), BeiderMorseEncoder.builder());
    }

    /**
     * Tests that an encoder built with concat disabled produces the expected behavior.
     */
    @Test
    void testBuilderWithConcatDisabled() {
        final PhoneticEngine engine = PhoneticEngine.builder().setConcat(false).get();
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().setPhoneticEngine(engine).get();
        assertFalse(encoder.isConcat());
    }

    /**
     * Tests that the builder with EXACT rule type produces a non-empty encoding for a known name.
     */
    @Test
    void testBuilderWithExactRuleType() throws EncoderException {
        final PhoneticEngine engine = PhoneticEngine.builder().setRuleType(RuleType.EXACT).get();
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().setPhoneticEngine(engine).get();
        assertEquals(RuleType.EXACT, encoder.getRuleType());
        final String result = encoder.encode("Cohen");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Tests that the default encoder has concat enabled.
     */
    @Test
    void testDefaultConcat() {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().get();
        assertTrue(encoder.isConcat());
    }

    /**
     * Tests that the default encoder built with no configuration produces a non-empty encoding.
     */
    @Test
    void testDefaultEncoderEncodes() throws EncoderException {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().get();
        final String result = encoder.encode("Smith");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Tests that the default encoder has the default name type (GENERIC).
     */
    @Test
    void testDefaultNameType() {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().get();
        assertEquals(NameType.GENERIC, encoder.getNameType());
    }

    /**
     * Tests that the default encoder has the default rule type (APPROX).
     */
    @Test
    void testDefaultRuleType() {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().get();
        assertEquals(RuleType.APPROX, encoder.getRuleType());
    }

    /**
     * Tests that an encoder built via the builder encodes {@code null} to {@code null}.
     */
    @Test
    void testEncodeNullReturnsNull() throws EncoderException {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().get();
        assertNotNull(encoder);
        // encode(null) should return null per BeiderMorseEncoder implementation.
        assertEquals(null, encoder.encode((String) null));
    }

    /**
     * Tests that passing {@code null} to {@link BeiderMorseEncoder.Builder#setPhoneticEngine(PhoneticEngine)} falls back to the default engine.
     */
    @Test
    void testSetPhoneticEngineNullFallsBackToDefault() {
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().setPhoneticEngine(null).get();
        assertNotNull(encoder);
        assertEquals(NameType.GENERIC, encoder.getNameType());
        assertEquals(RuleType.APPROX, encoder.getRuleType());
        assertTrue(encoder.isConcat());
    }

    /**
     * Tests that {@link BeiderMorseEncoder.Builder#setPhoneticEngine(PhoneticEngine)} supports chaining.
     */
    @Test
    void testSetPhoneticEngineReturnsBuilder() {
        final PhoneticEngine engine = PhoneticEngine.builder().get();
        final BeiderMorseEncoder.Builder builder = BeiderMorseEncoder.builder();
        assertNotNull(builder.setPhoneticEngine(engine));
    }

    /**
     * Tests building an encoder with a custom {@link PhoneticEngine} using the ASHKENAZI name type.
     */
    @Test
    void testSetPhoneticEngineWithAshkenazi() {
        // @formatter:off
        final PhoneticEngine engine = PhoneticEngine.builder()
                .setNameType(NameType.ASHKENAZI)
                .setRuleType(RuleType.EXACT)
                .get();
        // @formatter:on
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().setPhoneticEngine(engine).get();
        assertEquals(NameType.ASHKENAZI, encoder.getNameType());
        assertEquals(RuleType.EXACT, encoder.getRuleType());
    }

    /**
     * Tests building an encoder with a custom {@link PhoneticEngine} using the SEPHARDIC name type.
     */
    @Test
    void testSetPhoneticEngineWithSephardic() {
        final PhoneticEngine engine = PhoneticEngine.builder().setNameType(NameType.SEPHARDIC).get();
        final BeiderMorseEncoder encoder = BeiderMorseEncoder.builder().setPhoneticEngine(engine).get();
        assertEquals(NameType.SEPHARDIC, encoder.getNameType());
    }

    /**
     * Tests that two encoders built independently with identical configurations produce the same encoding.
     */
    @Test
    void testTwoDefaultBuildersProduceSameEncoding() throws EncoderException {
        final BeiderMorseEncoder enc1 = BeiderMorseEncoder.builder().get();
        final BeiderMorseEncoder enc2 = BeiderMorseEncoder.builder().get();
        assertEquals(enc1.encode("Levy"), enc2.encode("Levy"));
    }
}
