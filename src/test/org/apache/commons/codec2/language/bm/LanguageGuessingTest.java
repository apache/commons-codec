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

package org.apache.commons.codec2.language.bm;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests guessLanguages API.
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
@RunWith(Parameterized.class)
public class LanguageGuessingTest {

    private static String EXACT = "exact";
    private static String ONE_OF = "one of";

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Renault", "french", EXACT },
                { "Mickiewicz", "polish", EXACT },
                { "Thompson", "english", ONE_OF }, // this also hits german and greeklatin
                { "Nuñez", "spanish", EXACT },
                { "Carvalho", "portuguese", EXACT },
                { "Čapek", "czech", EXACT },
                { "Sjneijder", "dutch", EXACT },
                { "Klausewitz", "german", EXACT },
                { "Küçük", "turkish", EXACT },
                { "Giacometti", "italian", EXACT },
                { "Nagy", "hungarian", EXACT },
                { "Ceauşescu", "romanian", EXACT },
                { "Angelopoulos", "greeklatin", EXACT },
                { "Αγγελόπουλος", "greek", EXACT },
                { "Пушкин", "cyrillic", EXACT },
                { "כהן", "hebrew", EXACT },
                { "ácz", "any", EXACT },
                { "átz", "any", EXACT } });
    }

    private final String exactness;

    private final Lang lang = Lang.instance(NameType.GENERIC);
    private final String language;
    private final String name;

    public LanguageGuessingTest(String name, String language, String exactness) {
        this.name = name;
        this.language = language;
        this.exactness = exactness;
    }

    @Test
    public void testLanguageGuessing() {
        Languages.LanguageSet guesses = this.lang.guessLanguages(this.name);

        assertTrue("language predicted for name '" + this.name + "' is wrong: " + guesses + " should contain '" + this.language + "'",
                guesses.contains(this.language));

    }
}
