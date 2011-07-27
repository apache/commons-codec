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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests Rule.
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
@RunWith(Parameterized.class)
public class RuleTest {

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                new Object[] { "matching language sets with ALL",
                        new Rule("e", "", "", "o", new HashSet<String>(Arrays.asList("english", "french")), Rule.ALL),
                        new HashSet<String>(Arrays.asList("english", "french")), true },
                new Object[] { "non-matching language sets with ALL",
                        new Rule("e", "", "", "o", new HashSet<String>(Arrays.asList("english", "french")), Rule.ALL),
                        new HashSet<String>(Arrays.asList("english")), false });
    }

    private final String caseName;
    private final boolean expected;
    private final Set<String> langs;
    private final Rule rule;

    public RuleTest(String caseName, Rule rule, Set<String> langs, boolean expected) {
        this.caseName = caseName;
        this.rule = rule;
        this.langs = langs;
        this.expected = expected;
    }

    @Test
    public void testRuleLanguageMatches() {
        assertEquals(this.caseName, this.expected, this.rule.languageMatches(this.langs));
    }

}
