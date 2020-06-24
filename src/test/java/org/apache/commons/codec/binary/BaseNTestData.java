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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Data and functions common to BaseN tests.
 *
 * @since 1.15
 */
public class BaseNTestData {

    final static byte[] DECODED
            = {-12, -125, -51, 43, 5, 47, 116, -72, -120, 2, -98, -100, -73, 61, 118, 74, 36, 38, 56, 107, 45, 91, 38,
            47, 72, -9, -98, -66, -25, -61, -122, -68, -38, -62, -50, -71, -66, -116, -92, 42, 54, -56, -113, 125,
            -40, 89, 54, -67, -60, 14, -36, -4, 81, -14, -91, 103, 37, -83, -104, 80, -18, -119, -33, 115, 114, 68,
            -9, 112, 73, -27, -12, -8, 71, -36, -64, 17, -40, -37, -113, 45, 97, -65, -122, 88, 54, 113, 19, -31, 98,
            94, 92, -62, -55, -1, -102, 126, -88, 26, 83, -80, -6, 94, -91, 111, 3, 53, 86, 50, -43, -51, 54, -1, 92,
            50, 11, -23, 32, 3, -96, -81, 69, 71, 125, 113, 42, -1, -106, -33, 60, 0, 71, 108, 77, 94, 6, 48, 41, -11,
            -8, 76, 46, 2, 38, 29, -118, -4, 110, -50, 127, -100, 44, -49, 42, -38, 55, -80, -86, 82, 57, -38, -45,
            -3, 39, -80, -84, -14, -6, -122, -17, 91, 58, -7, 96, 4, 44, -85, -26, -3, 74, 47, -65, 38, -114, -117,
            -29, -99, 49, 71, -29, 67, 66, 75, -120, -71, 7, -69, -86, 125, 59, 5, 32, -67, 10, -94, 12, -84, -60, -65,
            -16, 46, -126, -115, 29, 76, -10, 115, 96, 97, 50, 8, -2, 70, 86, -71, 94, -35, 4, 29, -127, -56, -120,
            30, 122, 93, 119, -123, 84, 76, -15, -111, 81, -75, -34, 41, -72, 126, -7, 77, -33, 108, -110, 39, -125,
            -5, 16, 92, -51, -56, 96, 28, -116, 103, -68, 109, -12, 117, -110, -44, -75, 28, 69, -44, 59, 62, -68,
            39, -4, -119, 80, 91, 19, -116, 122, -81, -118, 100, -108, -88, 2, -8, -106, -75, -37, 30, -83, 124, -121,
            108, -127, 26, -1, -8, 102, -81, -118, 127, -113, -51, 36, -46, 15, 106, -33, -104, 106, -43, -84, -122,
            51, -33, 124, -32, 2, -45, 73, -90, 124, 89, -20, -123, 109, -100, 117, 11, 16, -65, 66, -118, -97, -9,
            101, 7, -1, 41, 65, 70, 116, -119, 54, 126, 44, 75, 74, 26, -34, -27, 27, 54, -13, -89, -90, 64, 120, 15,
            -43, 123, 82, -33, 90, -74, 41, -62, 38, -68, 62, -62, 34, 92, 50, 95, -67, -110, -99, -71, -44, -123,
            49, 4, 96, 56, 113, 76, 97, -47, -26, -79, -109, 115, -125, 90, 124, 8, -9, -111, 36, -74, 101, -114, 43,
            0, -110, 63, 76, 99, 91, 2, 12, -60, 56, -14, -125, 0, 6, -27, 31, 31, -109, -47, -3, 109, 88, -75, -74,
            19, 26, -66, 110, 39, 13, -50, 47, 104, -38, 18, 19, 84, 103, 100, -42, 48, 110, 37, 21, -107, 83, -52,
            -12, 71, 37, -68, -107, -109, 89, -34, -94, -127, 103, -128, -48, -52, 71, 0, 15, 34, 56, -50, 85, -98,
            106, -87, -3, 97, -116, -19, 64, -22, -25, -38, -63, 33, -45, 80, 10, -121, -109, 37, -96, 36, 18, -48,
            46, 44, -66, 115, -94, 3, -102, -27, -17, -116, -51, 88, -17, 7, -109, 24, 66, 83, -91, 105, -92, -19,
            66, -76, 64, -91, 118, -71, 103, -123, 95, 17, -87, -18, -11, 66, -74, 126, 45, 83, -14, 50, 79, 20, 45,
            -113, -103, 119, -101, -58, -99, 27, -100, -17, -107, 91, -26, -32, -56, 71, 72, 34, 66, 16, 9, -90, 106,
            -44, -62, -106, 11, 114, -82, -120, -28, -67, 4, -99, 109, -20, -19, 0, -40, -110, -119, 42, -6, 4, -31,
            67, 110, -105, 53, 118, 76, 96, -126, -8, -96, 39, -102, 52, 106, 64, 26, -105, -108, -103, -96, -116,
            116, 0, -96, 115, 89, 40, -23, -102, -2, -30, 16, 58, -53, -33, 14, 122, -94, 113, -121, 67, -103, -4,
            -126, 98, -27, 124, -12, 120, -64, -44, 127, 45, -120, 50, 124, -27, 87, -20, -84, 81, -35, 113, -77,
            -64, -96, -48, -87, -117, -82, 90, -64, -108, -121, 125, -45, -50, -44, -48, -50, 52, -30, -66, -7, 46,
            -40, -47, 85, -44, -126, -122, 24, -84, 21, 120, 99, -74, 27, 11, -52, 32, -2, 122, -100, -118, 106, -9,
            -106, 109, -19, 71, 42, 126, 66, -56, 10, -51, -44, 68, 109, -13, 81, -109, 65, 121, 60, -68, -117, 126,
            -59, 4, -107, -22, 99, -77, 84, 29, 87, 119, -60, 87, 82, -55, -74, 44, -80, 3, 123, -101, 84, -44, 9, 71,
            24, 91, 99, 22, -65, 11, -11, -14, -38, -84, 105, -101, -85, -17, 116, -65, 118, -105, 122, -75, 113,
            -57, -81, -33, -110, 28, 104, -24, -110, -57, -78, 38, -5, -15, -79, 87, 105, 85, 41, -42, -114, -67,
            -123, 70, 12, 61, 115, 5, 23, -70, 99, 96, -80, 65, -65, 105, -45, -49, 37, -33, -1, 119, -88, 100, 121,
            -25, -35, -51, 10, 43, -113, 61, 103, 44, 13, 108, 20, 74, 19, 53, 19, 37, -76, 20, -43, -11, 23, -58, -25,
            -52, 121, -40, -118, 58, 50, 19, -8, -33, -30, -49, -27, -11, -80, 93, -17, 34, 93, 69, 100, 66, -54, 40,
            118, 89, -52, -87, 2, 35, -120, 18, 64, 108, 31, -25, 66, 78, 6, -91, -69, -53, 17, 14, -125, 33, -31, -110,
            1, 5, -40, 7, 126, -122, 84, -55, -62, -22, 69, -28, 5, 45, -106, 120, 74, 94, 51, 74, 108, -19, -26, -12,
            49, 64, 88, 68, 41, -65, 126, 125, -1, -8, -83, -67, 74, 2, -114, -80, -119, -9, -89, -125, 21, 95, 34,
            -58, -74, 111, -103, 99, 95, 48, 42, 94, -50, -55, -112, -5, -26, 11, -89, -38, -19, 126, 25, 102, 119,
            81, -94, 70, -79, 98, 91, -73, 114, 15, 14, 87, -21, -122, -1, -90, 0, 29, -104, -91, -93, -58, -83, -48,
            -22, 100, -112, -41, 77, 22, -24, 112, -72, 105, 100, 6, -86, -39, 40, -43, 35, -2, 4, -94, 97, -121, 52,
            -22, 1, 127, -81, -4, -6, -119, 96, 35, -91, 114, 81, 91, 90, -86, -36, 34, -39, 93, -42, 69, 103, -11,
            107, -87, 119, -107, -114, -45, -128, -69, 96};

    /**
     * Read all bytes from an InputStream into a byte array.
     *
     * @param in the input stream.
     * @return the byte array
     *
     * @throws IOException if an error occurs whilst reading the input stream
     */
    static byte[] streamToBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buf = new byte[4096];
        int read;
        while ((read = in.read(buf)) > -1) {
            os.write(buf, 0, read);
        }
        return os.toByteArray();
    }

    private final static int SIZE_KEY = 0;
    private final static int LAST_READ_KEY = 1;

    /**
     * Read all bytes from an InputStream into a byte array
     * in chunks of {@code buf.length}.
     *
     * @param in the input stream.
     * @param buf the byte array to use for chunking
     *
     * @return the bytes read from the input stream
     *
     * @throws IOException if an error occurs whilst reading the input stream
     */
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
