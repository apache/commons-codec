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

package org.apache.commons.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * This random data was encoded by OpenSSL. Java had nothing to do with it. This data helps us test interop between
 * Commons-Codec and OpenSSL. Notice that OpenSSL creates 64 character lines instead of the 76 of Commons-Codec.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @version $Id $
 * @since 1.4
 */
public class Base32TestData {

    static final String STRING_FIXTURE = "Hello World";

    static final String BASE32_FIXTURE = "JBSWY3DPEBLW64TMMQ======\r\n";
//  static final String BASE32HEX_FIXTURE = "91IMOR3F41BMUSJCCG======";

    // Some utility code to help test chunked reads of the InputStream.

    private final static int SIZE_KEY = 0;
    private final static int LAST_READ_KEY = 1;

    static byte[] streamToBytes(final InputStream in) throws IOException {
        // new byte[7] is obviously quite slow, but helps exercise the code.
        return streamToBytes(in, new byte[7]);
    }

    static byte[] streamToBytes(final InputStream in, byte[] buf) throws IOException {
        try {
            int[] status = fill(buf, 0, in);
            int size = status[SIZE_KEY];
            int lastRead = status[LAST_READ_KEY];
            while (lastRead != -1) {
                buf = resizeArray(buf);
                status = fill(buf, size, in);
                size = status[SIZE_KEY];
                lastRead = status[LAST_READ_KEY];
            }
            if (buf.length != size) {
                final byte[] smallerBuf = new byte[size];
                System.arraycopy(buf, 0, smallerBuf, 0, size);
                buf = smallerBuf;
            }
        }
        finally {
            in.close();
        }
        return buf;
    }

    private static int[] fill(final byte[] buf, final int offset, final InputStream in)
            throws IOException {
        int read = in.read(buf, offset, buf.length - offset);
        int lastRead = read;
        if (read == -1) {
            read = 0;
        }
        while (lastRead != -1 && read + offset < buf.length) {
            lastRead = in.read(buf, offset + read, buf.length - read - offset);
            if (lastRead != -1) {
                read += lastRead;
            }
        }
        return new int[]{offset + read, lastRead};
    }

    private static byte[] resizeArray(final byte[] bytes) {
        final byte[] biggerBytes = new byte[bytes.length * 2];
        System.arraycopy(bytes, 0, biggerBytes, 0, bytes.length);
        return biggerBytes;
    }


    /**
     * Returns an encoded and decoded copy of the same random data.
     *
     * @param codec the codec to use
     * @param size amount of random data to generate and encode
     * @return two byte[] arrays:  [0] = decoded, [1] = encoded
     */
    static byte[][] randomData(final BaseNCodec codec, final int size) {
        final Random r = new Random();
        final byte[] decoded = new byte[size];
        r.nextBytes(decoded);
        final byte[] encoded = codec.encode(decoded);
        return new byte[][] {decoded, encoded};
    }

    /**
     * Tests the supplied byte[] array to see if it contains the specified byte c.
     *
     * @param bytes byte[] array to test
     * @param c byte to look for
     * @return true if bytes contains c, false otherwise
     */
    static boolean bytesContain(final byte[] bytes, final byte c) {
        for (final byte b : bytes) {
            if (b == c) { return true; }
        }
        return false;
    }

}
