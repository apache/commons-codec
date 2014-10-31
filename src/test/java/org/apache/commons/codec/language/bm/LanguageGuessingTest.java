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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests guessLanguages API.
 *
 * @since 1.6
 */
@RunWith(Parameterized.class)
public class LanguageGuessingTest {

    private static String EXACT = "exact";
    private static String ONE_OF = "one of";

    @Parameterized.Parameters(name = "{0}-{1}-{2}")
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Renault", "french", EXACT },
                { "Mickiewicz", "polish", EXACT },
                { "Thompson", "english", ONE_OF }, // this also hits german and greeklatin
                { "Nu\u00f1ez", "spanish", EXACT }, // Nuñez
                { "Carvalho", "portuguese", EXACT },
                { "\u010capek", "czech", EXACT }, // Čapek
                { "Sjneijder", "dutch", EXACT },
                { "Klausewitz", "german", EXACT },
                { "K\u00fc\u00e7\u00fck", "turkish", EXACT }, // Küçük
                { "Giacometti", "italian", EXACT },
                { "Nagy", "hungarian", EXACT },
                { "Ceau\u015fescu", "romanian", EXACT }, // Ceauşescu
                { "Angelopoulos", "greeklatin", EXACT },
                { "\u0391\u03b3\u03b3\u03b5\u03bb\u03cc\u03c0\u03bf\u03c5\u03bb\u03bf\u03c2", "greek", EXACT }, // Αγγελόπουλος
                { "\u041f\u0443\u0448\u043a\u0438\u043d", "cyrillic", EXACT }, // Пушкин
                { "\u05db\u05d4\u05df", "hebrew", EXACT }, // כהן
                { "\u00e1cz", "any", EXACT }, // ácz
                { "\u00e1tz", "any", EXACT } }); // átz
    }

    private final String exactness;

    private final Lang lang = Lang.instance(NameType.GENERIC);
    private final String language;
    private final String name;

    public LanguageGuessingTest(final String name, final String language, final String exactness) {
        this.name = name;
        this.language = language;
        this.exactness = exactness;
    }

    @Test
    public void testLanguageGuessing() {
        final Languages.LanguageSet guesses = this.lang.guessLanguages(this.name);

        assertTrue("language predicted for name '" + this.name + "' is wrong: " + guesses + " should contain '" + this.language + "'",
                guesses.contains(this.language));

    }
}
