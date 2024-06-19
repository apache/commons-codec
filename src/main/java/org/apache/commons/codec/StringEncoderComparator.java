/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec;

import java.util.Comparator;

/**
 * Compares Strings using a {@link StringEncoder}. This comparator is used to sort Strings by an encoding scheme such as
 * Soundex, Metaphone, etc. This class can come in handy if one needs to sort Strings by an encoded form of a name such
 as Soundex.
 *
 * <p>This class is immutable and thread-safe.</p>
 */
public class StringEncoderComparator implements Comparator<String> {
    /**
     * Internal encoder instance.
     */
    private final StringEncoder stringEncoder;

    /**
     * Constructs a new instance with the given algorithm.
     *
     * @param stringEncoder the StringEncoder used for comparisons.
     */
    public StringEncoderComparator(final StringEncoder stringEncoder) {
        if (stringEncoder == null) {
            throw new IllegalArgumentException("StringEncoder must not be null");
        }
        this.stringEncoder = stringEncoder;
    }

    /**
     * Compares two strings based not on the strings themselves, but on an encoding of the two strings using the
     * StringEncoder this Comparator was created with.
     *
     * If an {@link EncoderException} is encountered, return {@code 0}.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare to
     * @return the Comparable.compareTo() return code or 0 if an encoding error was caught.
     * @see Comparable
     */
    @Override
    public int compare(final String s1, final String s2) {
        try {
            @SuppressWarnings("unchecked") // May fail with CCE if encode returns something that is not Comparable
            final Comparable<Comparable<?>> encoded1 = (Comparable<Comparable<?>>) stringEncoder.encode(s1);
            final Comparable<?> encoded2 = (Comparable<?>) stringEncoder.encode(s2);
            return encoded1.compareTo(encoded2);
        } catch (final EncoderException e) {
            return 0;
        }
    }
}

