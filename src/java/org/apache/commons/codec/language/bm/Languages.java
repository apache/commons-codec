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

import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * <p>
 * Language codes.
 * </p>
 * <p>
 * Language codes are typically loaded from resource files. These are UTF-8 encoded text files. They are systematically named following the
 * pattern:
 * </p>
 * <blockquote>org/apache/commons/codec/language/bm/${{@link NameType#getName()} languages.txt</blockquote>
 * <p>
 * The format of these resources is the following:
 * </p>
 * <ul>
 * <li><b>Language:</b> a single string containing no whitespace</li>
 * <li><b>End-of-line comments:</b> Any occurance of '//' will cause all text following on that line to be discarded as a comment.</li>
 * <li><b>Multi-line comments:</b> Any line starting with '/*' will start multi-line commenting mode. This will skip all content until a
 * line ending in '*' and '/' is found.</li>
 * <li><b>Blank lines:</b> All blank lines will be skipped.</li>
 * </ul>
 * <p>
 * Ported from language.php
 * </p>
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class Languages {

    public static final String ANY = "any";

    private static final Map<NameType, Languages> LANGUAGES = new EnumMap<NameType, Languages>(NameType.class);

    static {
        for (NameType s : NameType.values()) {
            LANGUAGES.put(s, instance(langResourceName(s)));
        }
    }

    public static Languages instance(NameType nameType) {
        return LANGUAGES.get(nameType);
    }

    public static Languages instance(String languagesResourceName) {
        // read languages list
        Set<String> ls = new HashSet<String>();
        InputStream langIS = Languages.class.getClassLoader().getResourceAsStream(languagesResourceName);

        if (langIS == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + languagesResourceName);
        }

        Scanner lsScanner = new Scanner(langIS, ResourceConstants.ENCODING);
        boolean inExtendedComment = false;
        while (lsScanner.hasNextLine()) {
            String line = lsScanner.nextLine().trim();
            if (inExtendedComment) {
                if (line.endsWith(ResourceConstants.EXT_CMT_END)) {
                    inExtendedComment = false;
                } else {
                    // skip
                }
            } else {
                if (line.startsWith(ResourceConstants.EXT_CMT_START)) {
                    inExtendedComment = true;
                } else if (line.length() > 0) {
                    ls.add(line);
                } else {
                    // skip blank lines
                }
            }
        }

        return new Languages(Collections.unmodifiableSet(ls));
    }

    private static String langResourceName(NameType nameType) {
        return String.format("org/apache/commons/codec/language/bm/%s_languages.txt", nameType.getName());
    }

    private final Set<String> languages;

    private Languages(Set<String> languages) {
        this.languages = languages;
    }

    public Set<String> getLanguages() {
        return this.languages;
    }

    // // The original code mapped sets of languages to unique numerical codes - this doesn't seem to be needed in this impl
    // public static Languages instance(String languagesResourceName)
    // {
    // // read languages list
    // Map<String, Integer> ls = new HashMap<String, Integer>();
    // InputStream langIS = Languages.class.getClassLoader().getResourceAsStream(languagesResourceName);
    //
    // if(langIS == null)
    // throw new IllegalArgumentException("Unable to resolve required resource: " + languagesResourceName);
    //
    // Scanner lsScanner = new Scanner(langIS);
    // int i = 0;
    // while(lsScanner.hasNextLine()) {
    // String line = lsScanner.nextLine();
    // i++;
    // ls.put(line.trim(), i^2);
    // }
    //
    // return new Languages(Collections.unmodifiableSet(ls.keySet()), Collections.unmodifiableMap(ls));
    // }
    //
    // // todo: phoneticutils.php: LanguageIndex, LanguageName, LanguageCode, LanguageIndexFromCode
    //
    //
    // private final Set<String> languages;
    // private final Map<String, Integer> language_codes;
    //
    // private Languages(Set<String> languages, Map<String, Integer> language_codes) {
    // this.languages = languages;
    // this.language_codes = language_codes;
    // }
    //
    // public Set<String> getLanguages() {
    // return languages;
    // }
    //
    // public Map<String, Integer> getLanguage_codes() {
    // return language_codes;
    // }
}
