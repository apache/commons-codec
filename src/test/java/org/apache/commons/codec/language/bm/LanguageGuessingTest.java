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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests guessLanguages API.
 *
 * @since 1.6
 */
public class LanguageGuessingTest {

    public static Stream<Arguments> data() {
        return Stream.of(
            Arguments.of("Renault", "french"),
            Arguments.of("Mickiewicz", "polish"),
            Arguments.of("Thompson", "english"), // this also hits german and greek latin
            Arguments.of("Nu\u00f1ez", "spanish"), // Nuñez
            Arguments.of("Carvalho", "portuguese"),
            Arguments.of("\u010capek", "czech"), // Čapek
            Arguments.of("Sjneijder", "dutch"),
            Arguments.of("Klausewitz", "german"),
            Arguments.of("K\u00fc\u00e7\u00fck", "turkish"), // Küçük
            Arguments.of("Giacometti", "italian"),
            Arguments.of("Nagy", "hungarian"),
            Arguments.of("Ceau\u015fescu", "romanian"), // Ceauşescu
            Arguments.of("Angelopoulos", "greeklatin"),
            Arguments.of("\u0391\u03b3\u03b3\u03b5\u03bb\u03cc\u03c0\u03bf\u03c5\u03bb\u03bf\u03c2", "greek"), // Αγγελόπουλος
            Arguments.of("\u041f\u0443\u0448\u043a\u0438\u043d", "cyrillic"), // Пушкин
            Arguments.of("\u05db\u05d4\u05df", "hebrew"), // כהן
            Arguments.of("\u00e1cz", "any"), // ácz
            Arguments.of("\u00e1tz", "any") // átz
        );
    }

    private final Lang lang = Lang.instance(NameType.GENERIC);

    @ParameterizedTest
    @MethodSource("data")
    public void testLanguageGuessing(final String name, final String language) {
        final Languages.LanguageSet guesses = this.lang.guessLanguages(name);

        assertTrue(guesses.contains(language),
                "language predicted for name '" + name + "' is wrong: " + guesses + " should contain '" + language + "'");
    }
}
