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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides Base64 encoding and decoding in a streaming fashion (unlimited size).
 * When encoding the default lineLength is 76 characters and the default
 * lineEnding is CRLF, but these can be overridden by using the appropriate
 * constructor.
 * <p>
 * The default behaviour of the Base64InputStream is to DECODE, whereas the
 * default behaviour of the Base64OutputStream is to ENCODE, but this
 * behaviour can be overridden by using a different constructor.
 * </p><p>
 * This class implements section <cite>6.8. Base64 Content-Transfer-Encoding</cite> from RFC 2045 <cite>Multipurpose
 * Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies</cite> by Freed and Borenstein.
 * </p>
 *
 * @author Apache Software Foundation
 * @version $Id $
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @since 1.0-dev
 */
public class Base64InputStream extends FilterInputStream {

    private final boolean doEncode;
    private final Base64 base64;
    private final byte[] singleByte = new byte[1];

    /**
     * Creates a Base64InputStream such that all data read is Base64-decoded
     * from the original provided InputStream.
     *
     * @param in InputStream to wrap.
     */
    public Base64InputStream(InputStream in) {
        this(in, false);
    }

    /**
     * Creates a Base64InputStream such that all data read is either
     * Base64-encoded or Base64-decoded from the original provided InputStream.
     *
     * @param in       InputStream to wrap.
     * @param doEncode true if we should encode all data read from us,
     *                 false if we should decode.
     */
    public Base64InputStream(InputStream in, boolean doEncode) {
        super(in);
        this.doEncode = doEncode;
        this.base64 = new Base64();
    }

    /**
     * Creates a Base64InputStream such that all data read is either
     * Base64-encoded or Base64-decoded from the original provided InputStream.
     *
     * @param in            InputStream to wrap.
     * @param doEncode      true if we should encode all data read from us,
     *                      false if we should decode.
     * @param lineLength    If doEncode is true, each line of encoded
     *                      data will contain lineLength characters.  If
     *                      doEncode is false, lineLength is ignored.
     * @param lineSeparator If doEncode is true, each line of encoded
     *                      data will be terminated with this byte sequence
     *                      (e.g. \r\n).  If doEncode is false lineSeparator
     *                      is ignored.
     */
    public Base64InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(in);
        this.doEncode = doEncode;
        this.base64 = new Base64(lineLength, lineSeparator);
    }

    /**
     * Reads one <code>byte</code> from this input stream.
     * Returns -1 if EOF has been reached.
     */
    public int read() throws IOException {
        int r = read(singleByte, 0, 1);
        while (r == 0) {
            r = read(singleByte, 0, 1);
        }
        if (r > 0) {
            return singleByte[0] < 0 ? 256 + singleByte[0] : singleByte[0];
        } else {
            return -1;
        }
    }

    /**
     * Attempts to read <code>len</code> bytes into the specified
     * <code>b</code> array starting at <code>offset</code> from
     * this InputStream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public int read(byte b[], int offset, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (offset < 0 || len < 0 || offset + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        } else {
            if (!base64.hasData()) {
                byte[] buf = new byte[doEncode ? 4096 : 8192];
                int c = in.read(buf);

                // A little optimization to avoid System.arraycopy()
                // when possible.
                if (c > 0 && b.length == len) {
                    base64.setInitialBuffer(b, offset, len);
                }

                if (doEncode) {
                    base64.encode(buf, 0, c);
                } else {
                    base64.decode(buf, 0, c);
                }
            }
            return base64.readResults(b, offset, len);
        }
    }


}
