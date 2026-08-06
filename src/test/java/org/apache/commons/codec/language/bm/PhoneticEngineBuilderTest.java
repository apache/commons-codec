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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link PhoneticEngine.Builder}.
 *
 * @since 1.23.0
 */
class PhoneticEngineBuilderTest {

    /**
     * Tests that the builder produces a non-null {@link PhoneticEngine}.
     */
    @Test
    void testBuilderGet() {
        final PhoneticEngine engine = PhoneticEngine.builder().get();
        assertNotNull(engine);
    }

    /**
     * Tests that {@link PhoneticEngine#builder()} returns a non-null builder.
     */
    @Test
    void testBuilderNotNull() {
        assertNotNull(PhoneticEngine.builder());
    }

    /**
     * Tests that successive calls to {@link PhoneticEngine#builder()} return distinct builder instances.
     */
    @Test
    void testBuilderReturnsNewInstance() {
        assertNotSame(PhoneticEngine.builder(), PhoneticEngine.builder());
    }

    /**
     * Tests the default values of the builder.
     */
    @Test
    void testDefaultValues() {
        final PhoneticEngine engine = PhoneticEngine.builder().get();
        assertEquals(NameType.GENERIC, engine.getNameType());
        assertEquals(RuleType.APPROX, engine.getRuleType());
        assertTrue(engine.isConcat());
        assertEquals(20, engine.getMaxPhonemes());
    }

    /**
     * Tests that the builder produces engines that encode correctly (ASHKENAZI/APPROX/concat).
     */
    @Test
    void testEngineEncodeAshkenaziApprox() {
        // @formatter:off
        final PhoneticEngine engine = PhoneticEngine.builder()
                .setNameType(NameType.ASHKENAZI)
                .setRuleType(RuleType.APPROX)
                .setConcat(true)
                .setMaxPhonemes(10)
                .get();
        // @formatter:on
        final String result = engine.encode("Renault");
        assertEquals("rYnDlt|rYnalt|rYnult|rinDlt|rinalt|rinolt|rinult", result);
    }

    /**
     * Tests that the builder produces engines that encode correctly (GENERIC/APPROX/concat).
     */
    @Test
    void testEngineEncodeGenericApprox() {
        // @formatter:off
        final PhoneticEngine engine = PhoneticEngine.builder()
                .setNameType(NameType.GENERIC)
                .setRuleType(RuleType.APPROX)
                .setConcat(true)
                .setMaxPhonemes(10)
                .get();
        // @formatter:on
        final String result = engine.encode("Renault");
        assertEquals("rinD|rinDlt|rina|rinalt|rino|rinolt|rinu|rinult", result);
    }

    /**
     * Tests that the builder produces engines that encode correctly (GENERIC/EXACT/concat).
     */
    @Test
    void testEngineEncodeGenericExact() {
        // @formatter:off
        final PhoneticEngine engine = PhoneticEngine.builder()
                .setNameType(NameType.GENERIC)
                .setRuleType(RuleType.EXACT)
                .setConcat(true)
                .setMaxPhonemes(10)
                .get();
        // @formatter:on
        final String result = engine.encode("SntJohn-Smith");
        assertEquals("sntjonsmit", result);
    }

    /**
     * Tests that a builder-created engine is not the same instance when built twice.
     */
    @Test
    void testGetReturnsDifferentInstances() {
        final PhoneticEngine.Builder builder = PhoneticEngine.builder();
        final PhoneticEngine engine1 = builder.get();
        final PhoneticEngine engine2 = builder.get();
        assertNotSame(engine1, engine2);
    }

    /**
     * Tests method chaining: all setters return the same builder instance.
     */
    @Test
    void testMethodChaining() {
        // @formatter:off
        final PhoneticEngine.Builder builder = PhoneticEngine.builder();
        assertNotNull(builder
                .setNameType(NameType.SEPHARDIC)
                .setRuleType(RuleType.APPROX)
                .setConcat(true)
                .setMaxPhonemes(15)
                .get());
        // @formatter:on
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setAll(PhoneticEngine)} copies all properties.
     */
    @Test
    void testSetAll() {
        // @formatter:off
        final PhoneticEngine original = PhoneticEngine.builder()
                .setNameType(NameType.ASHKENAZI)
                .setRuleType(RuleType.EXACT)
                .setConcat(false)
                .setMaxPhonemes(5)
                .get();
        // @formatter:on
        final PhoneticEngine copy = PhoneticEngine.builder().setAll(original).get();
        assertEquals(original.getNameType(), copy.getNameType());
        assertEquals(original.getRuleType(), copy.getRuleType());
        assertEquals(original.isConcat(), copy.isConcat());
        assertEquals(original.getMaxPhonemes(), copy.getMaxPhonemes());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setConcat(boolean)} with {@code false}.
     */
    @Test
    void testSetConcatFalse() {
        final PhoneticEngine engine = PhoneticEngine.builder().setConcat(false).get();
        assertFalse(engine.isConcat());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setConcat(boolean)} with {@code true}.
     */
    @Test
    void testSetConcatTrue() {
        final PhoneticEngine engine = PhoneticEngine.builder().setConcat(true).get();
        assertTrue(engine.isConcat());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setMaxPhonemes(int)}.
     */
    @Test
    void testSetMaxPhonemes() {
        final int maxPhonemes = 10;
        final PhoneticEngine engine = PhoneticEngine.builder().setMaxPhonemes(maxPhonemes).get();
        assertEquals(maxPhonemes, engine.getMaxPhonemes());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setMaxPhonemes(int)} with {@link Integer#MAX_VALUE}.
     */
    @Test
    void testSetMaxPhonemesMaxValue() {
        final PhoneticEngine engine = PhoneticEngine.builder().setMaxPhonemes(Integer.MAX_VALUE).get();
        assertEquals(Integer.MAX_VALUE, engine.getMaxPhonemes());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setNameType(NameType)} with ASHKENAZI.
     */
    @Test
    void testSetNameTypeAshkenazi() {
        final PhoneticEngine engine = PhoneticEngine.builder().setNameType(NameType.ASHKENAZI).get();
        assertEquals(NameType.ASHKENAZI, engine.getNameType());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setNameType(NameType)}.
     */
    @Test
    void testSetNameTypeGeneric() {
        final PhoneticEngine engine = PhoneticEngine.builder().setNameType(NameType.GENERIC).get();
        assertEquals(NameType.GENERIC, engine.getNameType());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setNameType(NameType)} with SEPHARDIC.
     */
    @Test
    void testSetNameTypeSephardic() {
        final PhoneticEngine engine = PhoneticEngine.builder().setNameType(NameType.SEPHARDIC).get();
        assertEquals(NameType.SEPHARDIC, engine.getNameType());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setRuleType(RuleType)} with APPROX.
     */
    @Test
    void testSetRuleTypeApprox() {
        final PhoneticEngine engine = PhoneticEngine.builder().setRuleType(RuleType.APPROX).get();
        assertEquals(RuleType.APPROX, engine.getRuleType());
    }

    /**
     * Tests {@link PhoneticEngine.Builder#setRuleType(RuleType)} with EXACT.
     */
    @Test
    void testSetRuleTypeExact() {
        final PhoneticEngine engine = PhoneticEngine.builder().setRuleType(RuleType.EXACT).get();
        assertEquals(RuleType.EXACT, engine.getRuleType());
    }
}
