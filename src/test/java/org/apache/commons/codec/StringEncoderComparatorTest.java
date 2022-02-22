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

package org.apache.commons.codec;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Soundex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for the StingEncoderComparator.
 */
public class StringEncoderComparatorTest {

    @Test
    public void testComparatorWithSoundex() throws Exception {
        final StringEncoderComparator sCompare =
            new StringEncoderComparator( new Soundex() );

        assertEquals(0, sCompare.compare("O'Brien", "O'Brian"),
                "O'Brien and O'Brian didn't come out with the same Soundex, something must be wrong here");
    }

    @SuppressWarnings("unchecked") // cannot easily avoid this warning
    @Test
    public void testComparatorWithDoubleMetaphone() throws Exception {
        final StringEncoderComparator sCompare = new StringEncoderComparator(new DoubleMetaphone());

        final String[] testArray = { "Jordan", "Sosa", "Prior", "Pryor" };
        final List<String> testList = Arrays.asList(testArray);

        final String[] controlArray = { "Jordan", "Prior", "Pryor", "Sosa" };

        testList.sort(sCompare); // unchecked

        final String[] resultArray = testList.toArray(new String[0]);

        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(controlArray[i], resultArray[i],
                    "Result Array not Equal to Control Array at index: " + i);
        }
    }

    @Test
    public void testComparatorWithDoubleMetaphoneAndInvalidInput() throws Exception {
        final StringEncoderComparator sCompare =
            new StringEncoderComparator( new DoubleMetaphone() );

        final int compare = sCompare.compare(Double.valueOf(3.0d), Long.valueOf(3));
        assertEquals(0, compare,
                "Trying to compare objects that make no sense to the underlying encoder" +
                        " should return a zero compare code");
    }
}
