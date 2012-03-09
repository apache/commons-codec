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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests PhoneticEngine.
 * 
 * @since 1.6
 */
@RunWith(Parameterized.class)
public class PhoneticEngineTest {

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays
                .asList(new Object[] { "Renault", "rinD|rinDlt|rina|rinalt|rino|rinolt|rinu|rinult", NameType.GENERIC, RuleType.APPROX, true, 10 },
                        new Object[] { "Renault", "rYnDlt|rYnalt|rYnult|rinDlt|rinalt|rinult", NameType.ASHKENAZI, RuleType.APPROX, true, 10 },
                        new Object[] { "Renault", "rYnDlt", NameType.ASHKENAZI, RuleType.APPROX, true, 1 },
                        new Object[] { "Renault", "rinDlt", NameType.SEPHARDIC, RuleType.APPROX, true, 10 },
                        new Object[] { "SntJohn-Smith", "sntjonsmit", NameType.GENERIC, RuleType.EXACT, true, 10 },
                        new Object[] { "d'ortley", "(ortlaj|ortlej)-(dortlaj|dortlej)", NameType.GENERIC, RuleType.EXACT, true, 10 },
                        new Object[] {
                                "van helsing",
                                "(elSink|elsink|helSink|helsink|helzink|xelsink)-(banhelsink|fanhelsink|fanhelzink|vanhelsink|vanhelzink|vanjelsink)",
                                NameType.GENERIC,
                                RuleType.EXACT,
                                false, 10 });
    }

    private final boolean concat;
    private final String name;
    private final NameType nameType;
    private final String phoneticExpected;
    private final RuleType ruleType;
    private final int maxPhonemes;

    public PhoneticEngineTest(String name, String phoneticExpected, NameType nameType,
                              RuleType ruleType, boolean concat, int maxPhonemes) {
        this.name = name;
        this.phoneticExpected = phoneticExpected;
        this.nameType = nameType;
        this.ruleType = ruleType;
        this.concat = concat;
        this.maxPhonemes = maxPhonemes;
    }

    @Test(timeout = 10000L)
    public void testEncode() {
        PhoneticEngine engine = new PhoneticEngine(this.nameType, this.ruleType, this.concat, this.maxPhonemes);

        String phoneticActual = engine.encode(this.name);

        //System.err.println("expecting: " + this.phoneticExpected);
        //System.err.println("actual:    " + phoneticActual);
        assertEquals("phoneme incorrect", this.phoneticExpected, phoneticActual);

        if (this.concat) {
            String[] split = phoneticActual.split("\\|");
            assertTrue(split.length <= this.maxPhonemes);
        } else {
            String[] words = phoneticActual.split("-");
            for (String word : words) {
                String[] split = word.split("\\|");
                assertTrue(split.length <= this.maxPhonemes);
            }
        }
    }
}
