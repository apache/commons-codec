/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.commons.codec.binary;

import junit.framework.TestCase;

public class BaseNCodecTest extends TestCase {

    BaseNCodec codec;
    
    public void setUp(){
        codec = new BaseNCodec(0, 0, 0, 0) {
            protected boolean isInAlphabet(byte b) {
                return b=='O' || b == 'K'; // allow OK
            }
            
            void encode(byte[] pArray, int i, int length) {
            }
            
            void decode(byte[] pArray, int i, int length) {
            }
        };        
    }

    public void testBaseNCodec() {
        assertNotNull(codec);
    }

//    public void testHasData() {
//        fail("Not yet implemented");
//    }
//
//    public void testAvail() {
//        fail("Not yet implemented");
//    }
//
//    public void testEnsureBufferSize() {
//        fail("Not yet implemented");
//    }
//
//    public void testReadResults() {
//        fail("Not yet implemented");
//    }
//
    public void testIsWhiteSpace() {
        assertTrue(BaseNCodec.isWhiteSpace((byte) ' '));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\n'));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\r'));
        assertTrue(BaseNCodec.isWhiteSpace((byte) '\t'));
    }
//
//    public void testEncodeObject() {
//        fail("Not yet implemented");
//    }
//
//    public void testEncodeToString() {
//        fail("Not yet implemented");
//    }
//
//    public void testDecodeObject() {
//        fail("Not yet implemented");
//    }
//
//    public void testDecodeString() {
//        fail("Not yet implemented");
//    }
//
//    public void testDecodeByteArray() {
//        fail("Not yet implemented");
//    }
//
//    public void testEncodeByteArray() {
//        fail("Not yet implemented");
//    }
//
//    public void testEncodeAsString() {
//        fail("Not yet implemented");
//    }
//
//    public void testEncodeByteArrayIntInt() {
//        fail("Not yet implemented");
//    }
//
//    public void testDecodeByteArrayIntInt() {
//        fail("Not yet implemented");
//    }
//
    public void testIsInAlphabetByte() {
        assertFalse(codec.isInAlphabet((byte) 0));
        assertFalse(codec.isInAlphabet((byte) 'a'));
        assertTrue(codec.isInAlphabet((byte) 'O'));
        assertTrue(codec.isInAlphabet((byte) 'K'));
    }

    public void testIsInAlphabetByteArrayBoolean() {
        assertTrue(codec.isInAlphabet(new byte[]{}, false));
        assertTrue(codec.isInAlphabet(new byte[]{'O'}, false));
        assertFalse(codec.isInAlphabet(new byte[]{'O',' '}, false));
        assertFalse(codec.isInAlphabet(new byte[]{' '}, false));
        assertTrue(codec.isInAlphabet(new byte[]{}, true));
        assertTrue(codec.isInAlphabet(new byte[]{'O'}, true));
        assertTrue(codec.isInAlphabet(new byte[]{'O',' '}, true));
        assertTrue(codec.isInAlphabet(new byte[]{' '}, true));
    }

    public void testIsInAlphabetString() {
        assertTrue(codec.isInAlphabet("OK"));
        assertTrue(codec.isInAlphabet("O=K= \t\n\r"));
    }

    public void testContainsAlphabetOrPad() {
        assertFalse(codec.containsAlphabetOrPad(null));
        assertFalse(codec.containsAlphabetOrPad(new byte[]{}));
        assertTrue(codec.containsAlphabetOrPad("OK".getBytes()));
        assertTrue(codec.containsAlphabetOrPad("OK ".getBytes()));
        assertFalse(codec.containsAlphabetOrPad("ok ".getBytes()));
        assertTrue(codec.containsAlphabetOrPad(new byte[]{codec.PAD}));
    }

//    public void testGetEncodedLength() {
//        fail("Not yet implemented");
//    }
}
