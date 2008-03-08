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
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Soundex;

/**
 * Test cases for the StingEncoderComparator.
 * 
 * @author Apache Software Foundation
 * @version $Id$
 */
public class StringEncoderComparatorTest extends TestCase {

    public StringEncoderComparatorTest(String name) {
        super(name);
    }

    public void testComparatorNoArgCon() throws Exception {
        new StringEncoderComparator();
    }        

    public void testComparatorWithSoundex() throws Exception {
        StringEncoderComparator sCompare = 
            new StringEncoderComparator( new Soundex() );

        assertTrue( "O'Brien and O'Brian didn't come out with " +
                    "the same Soundex, something must be wrong here",
                    0 == sCompare.compare( "O'Brien", "O'Brian" ) );
    }
    
    public void testComparatorWithDoubleMetaphone() throws Exception {
        StringEncoderComparator sCompare =
            new StringEncoderComparator( new DoubleMetaphone() );
            
        String[] testArray = { "Jordan", "Sosa", "Prior", "Pryor" };
        List testList = Arrays.asList( testArray );        
        
        String[] controlArray = { "Jordan", "Prior", "Pryor", "Sosa" };

        Collections.sort( testList, sCompare);            
        
        String[] resultArray = (String[]) testList.toArray(new String[0]);
        
        for( int i = 0; i < resultArray.length; i++) {
            assertEquals( "Result Array not Equal to Control Array at index: " + i, controlArray[i], resultArray[i] );
        }
    }

    public void testComparatorWithDoubleMetaphoneAndInvalidInput() throws Exception {
        StringEncoderComparator sCompare =
            new StringEncoderComparator( new DoubleMetaphone() );
           
        int compare = sCompare.compare(new Double(3.0), new Long(3));
        assertEquals( "Trying to compare objects that make no sense to the underlying encoder should return a zero compare code",
                                0, compare);        
        
    }
}
