/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.codec.net;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import junit.framework.TestCase;

/**
 * RFC 1522 compliant codec test cases
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */
public class RFC1522CodecTest extends TestCase {
    
    public RFC1522CodecTest(String name) {
        super(name);
    }


    class RFC1522TestCodec extends RFC1522Codec { 

        protected byte[] doDecoding(byte[] bytes) throws DecoderException {
            return bytes;
        }

        protected byte[] doEncoding(byte[] bytes) throws EncoderException {
            return bytes;
        }

        protected String getEncoding() {
            return "T";
        }

    }

    public void testNullInput() throws Exception {
        RFC1522TestCodec testcodec = new RFC1522TestCodec();
        assertNull(testcodec.decodeText(null));
        assertNull(testcodec.encodeText(null, "UTF-8"));
    }

    public void testDecodeInvalid() throws Exception {
        RFC1522TestCodec testcodec = new RFC1522TestCodec();
        try {
            testcodec.decodeText("whatever");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=?stuff?=");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=?UTF-8?stuff?=");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=?UTF-8?T?stuff");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=??T?stuff?=");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=?UTF-8??stuff?=");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
        try {
            testcodec.decodeText("=?UTF-8?W?stuff?=");
            fail("DecoderException should have been thrown");
        } catch(DecoderException e) {
            // Expected. Move on
        }
    }

}
