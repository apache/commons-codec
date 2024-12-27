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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.Resources;

/**
 * Language codes.
 * <p>
 * Language codes are typically loaded from resource files. These are UTF-8
 * encoded text files. They are systematically named following the pattern:
 * </p>
 * <blockquote>org/apache/commons/codec/language/bm/${{@link NameType#getName()}
 * languages.txt</blockquote>
 * <p>
 * The format of these resources is the following:
 * </p>
 * <ul>
 * <li><strong>Language:</strong> a single string containing no whitespace</li>
 * <li><strong>End-of-line comments:</strong> Any occurrence of '//' will cause all text
 * following on that line to be discarded as a comment.</li>
 * <li><strong>Multi-line comments:</strong> Any line starting with '/*' will start
 * multi-line commenting mode. This will skip all content until a line ending in
 * '*' and '/' is found.</li>
 * <li><strong>Blank lines:</strong> All blank lines will be skipped.</li>
 * </ul>
 * <p>
 * Ported from language.php
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @since 1.6
 */
public class Languages {
    // Implementation note: This class is divided into two sections. The first part
    // is a static factory interface that
    // exposes org/apache/commons/codec/language/bm/%s_languages.txt for %s in
    // NameType.* as a list of supported
    // languages, and a second part that provides instance methods for accessing
    // this set for supported languages.

    /**
     * A set of languages.
     */
    public static abstract class LanguageSet {

        public static LanguageSet from(final Set<String> langs) {
            return langs.isEmpty() ? NO_LANGUAGES : new SomeLanguages(langs);
        }

        public abstract boolean contains(String language);

        public abstract String getAny();

        public abstract boolean isEmpty();

        public abstract boolean isSingleton();

        abstract LanguageSet merge(LanguageSet other);

        public abstract LanguageSet restrictTo(LanguageSet other);
    }

    /**
     * Some languages, explicitly enumerated.
     */
    public static final class SomeLanguages extends LanguageSet {
        private final Set<String> languages;

        private SomeLanguages(final Set<String> languages) {
            this.languages = Collections.unmodifiableSet(languages);
        }

        @Override
        public boolean contains(final String language) {
            return this.languages.contains(language);
        }

        @Override
        public String getAny() {
            return this.languages.iterator().next();
        }

        public Set<String> getLanguages() {
            return this.languages;
        }

        @Override
        public boolean isEmpty() {
            return this.languages.isEmpty();
        }

        @Override
        public boolean isSingleton() {
            return this.languages.size() == 1;
        }

        @Override
        public LanguageSet merge(final LanguageSet other) {
            if (other == NO_LANGUAGES) {
                return this;
            }
            if (other == ANY_LANGUAGE) {
                return other;
            }
            final SomeLanguages someLanguages = (SomeLanguages) other;
            final Set<String> set = new HashSet<>(languages);
            set.addAll(someLanguages.languages);
            return from(set);
        }

        @Override
        public LanguageSet restrictTo(final LanguageSet other) {
            if (other == NO_LANGUAGES) {
                return other;
            }
            if (other == ANY_LANGUAGE) {
                return this;
            }
            final SomeLanguages someLanguages = (SomeLanguages) other;
            return from(languages.stream().filter(lang -> someLanguages.languages.contains(lang)).collect(Collectors.toSet()));
        }

        @Override
        public String toString() {
            return "Languages(" + languages.toString() + ")";
        }

    }

    public static final String ANY = "any";

    private static final Map<NameType, Languages> LANGUAGES = new EnumMap<>(NameType.class);

    /**
     * No languages at all.
     */
    public static final LanguageSet NO_LANGUAGES = new LanguageSet() {
        @Override
        public boolean contains(final String language) {
            return false;
        }

        @Override
        public String getAny() {
            throw new NoSuchElementException("Can't fetch any language from the empty language set.");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

        @Override
        public LanguageSet merge(final LanguageSet other) {
            return other;
        }

        @Override
        public LanguageSet restrictTo(final LanguageSet other) {
            return this;
        }

        @Override
        public String toString() {
            return "NO_LANGUAGES";
        }
    };

    /**
     * Any/all languages.
     */
    public static final LanguageSet ANY_LANGUAGE = new LanguageSet() {
        @Override
        public boolean contains(final String language) {
            return true;
        }

        @Override
        public String getAny() {
            throw new NoSuchElementException("Can't fetch any language from the any language set.");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

        @Override
        public LanguageSet merge(final LanguageSet other) {
            return other;
        }

        @Override
        public LanguageSet restrictTo(final LanguageSet other) {
            return other;
        }

        @Override
        public String toString() {
            return "ANY_LANGUAGE";
        }
    };

    static {
        for (final NameType s : NameType.values()) {
            LANGUAGES.put(s, getInstance(langResourceName(s)));
        }
    }

    public static Languages getInstance(final NameType nameType) {
        return LANGUAGES.get(nameType);
    }

    public static Languages getInstance(final String languagesResourceName) {
        // read languages list
        final Set<String> ls = new HashSet<>();
        try (final Scanner lsScanner = new Scanner(Resources.getInputStream(languagesResourceName),
                ResourceConstants.ENCODING)) {
            boolean inExtendedComment = false;
            while (lsScanner.hasNextLine()) {
                final String line = lsScanner.nextLine().trim();
                if (inExtendedComment) {
                    if (line.endsWith(ResourceConstants.EXT_CMT_END)) {
                        inExtendedComment = false;
                    }
                } else if (line.startsWith(ResourceConstants.EXT_CMT_START)) {
                    inExtendedComment = true;
                } else if (!line.isEmpty()) {
                    ls.add(line);
                }
            }
            return new Languages(Collections.unmodifiableSet(ls));
        }
    }

    private static String langResourceName(final NameType nameType) {
        return String.format("/org/apache/commons/codec/language/bm/%s_languages.txt", nameType.getName());
    }

    private final Set<String> languages;

    private Languages(final Set<String> languages) {
        this.languages = languages;
    }

    public Set<String> getLanguages() {
        return this.languages;
    }
}
