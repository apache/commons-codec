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
 *
 *
 * NOTE ABOUT PROVENANCE:
 * ----------------------
 * This source file is called ApacheModifiedMiGBase64.java.
 * We took the BSD-licensed MiGBase64.java file from SourceForge
 * on January 28th, 2013 (http://migbase64.sourceforge.net/), and
 * modified it to make it suitable for inclusion inside Apache
 * Commons-Codec.
 *
 * The original file is licensed according to the BSD 2-clause
 * license (see below, after the section titled "Licence (BSD)".
 * You should also be able to obtain the original file as
 * "MiGBase64.original" within the same source directory as this file.
 *
 */

package org.apache.commons.codec.binary;

import java.util.Arrays;

/**
 * <pre>====================================================</pre>
 * Modified by Apache Software Foundation on February 18th, 2013, in the following ways:
 * <p/>
 * - Set all methods to "package" level visibility, since this is strictly
 * meant to be back-end for our non-streaming Base64 implementation.
 * (Streaming Base64 still uses our original implementation).
 * <p/>
 * - Added support for the Apache Commons Codec variations to make all the Commons-Codec
 * unit tests pass:
 * <ol>
 * <li> Ability to alter line-length from default of 76</li>
 * <li> If we are using line-separators, must always end with a line-separator, no matter
 * length of final line.</li>
 * <li> Make '=' and '==' padding optional when decoding.</li>
 * <li> Make decoding of Base64 with inner padding (e.g., AA==AA==) consistent with Commons-Codec..</li>
 * <li> Add support for URL-Safe Base64 alphabet (which, incidentally, omits '=' and '==' padding).</li>
 * </ol>
 * <p/>
 * - And thus Apache Commons-Codec is now as fast as MiGBase64, since it uses MiGBase64 under the
 * hood.  Yay!  (Non-streaming encode speed-up is around 200%).
 * <p/>
 * And now, back to your regular scheduled programming:
 * <pre>====================================================</pre>
 * <p/>
 * A very fast and memory efficient class to encode and decode to and from BASE64 in full accordance
 * with RFC 2045.<br><br>
 * On Windows XP sp1 with 1.4.2_04 and later ;), this encoder and decoder is about 10 times faster
 * on small arrays (10 - 1000 bytes) and 2-3 times as fast on larger arrays (10000 - 1000000 bytes)
 * compared to <code>sun.misc.Encoder()/Decoder()</code>.<br><br>
 * <p/>
 * On byte arrays the encoder is about 20% faster than Jakarta Commons Codec for encode and
 * about 50% faster for decoding large arrays. This implementation is about twice as fast on very small
 * arrays (&lt 30 bytes). If source/destination is a <code>String</code> this
 * version is about three times as fast due to the fact that the Commons Codec result has to be recoded
 * to a <code>String</code> from <code>byte[]</code>, which is very expensive.<br><br>
 * <p/>
 * This encode/decode algorithm doesn't create any temporary arrays as many other codecs do, it only
 * allocates the resulting array. This produces less garbage and it is possible to handle arrays twice
 * as large as algorithms that create a temporary array. (E.g. Jakarta Commons Codec). It is unknown
 * whether Sun's <code>sun.misc.Encoder()/Decoder()</code> produce temporary arrays but since performance
 * is quite low it probably does.<br><br>
 * <p/>
 * The encoder produces the same output as the Sun one except that the Sun's encoder appends
 * a trailing line separator if the last character isn't a pad. Unclear why but it only adds to the
 * length and is probably a side effect. Both are in conformance with RFC 2045 though.<br>
 * Commons codec seem to always att a trailing line separator.<br><br>
 * <p/>
 * <b>Note!</b>
 * The encode/decode method pairs (types) come in three versions with the <b>exact</b> same algorithm and
 * thus a lot of code redundancy. This is to not create any temporary arrays for transcoding to/from different
 * format types. The methods not used can simply be commented out.<br><br>
 * <p/>
 * <S>There is also a "fast" version of all decode methods that works the same way as the normal ones, but
 * har a few demands on the decoded input. Normally though, these fast verions should be used if the source if
 * the input is known and it hasn't bee tampered with.</S> (- removed for ApacheModifiedMiGBase64). <br><br>
 * <p/>
 * If you find the code useful or you find a bug, please send me a note at base64 @ miginfocom . com.
 * <p/>
 * Licence (BSD):
 * ==============
 * <p/>
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (base64 @ miginfocom . com)
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @author Mikael Grev
 *         Date: 2004-aug-02
 *         Time: 11:31:11
 * @version 2.2
 */

public final class ApacheModifiedMiGBase64 {
    // Marked the class public so that it shows up in javadoc generation.  All methods are static "package" level.

    private final static byte[] CRLF = {'\r', '\n'};

    private static final char[] CA =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static final char[] CA_URL_SAFE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

    private static final int[] IA = new int[256];

    /**
     * Private constructor to prevent instantiation.
     */
    private ApacheModifiedMiGBase64() {}

    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++) {
            IA[CA[i]] = i;

            // Store the URL_SAFE values in the same IA array.
            // This way we can auto-decode URL-SAFE or standard alphabet, without
            // consumer needing to specify decode alphabet ahead of time:
            IA[CA_URL_SAFE[i]] = i;
        }
        IA['='] = 0;
    }

    /**
     * Decodes a BASE64 encoded char array. All illegal characters will be ignored and can handle both arrays with
     * and without line separators.
     *
     * @param sArr The source array.
     * @return The decoded array of bytes. May be of length 0.
     */
    static byte[] decode(final char[] sArr) {
        final int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }

        // Find earliest pad character so that we can decode things like "AA==AA==" consistently.
        int padPos = sLen;
        int padCount = 0;
        for (int i = sLen - 1; i >= 0; i--) {
            if (sArr[i] == '=') {
                padPos = i;
                padCount = 1;
                if (i + 1 < sLen && sArr[i + 1] == '=') {
                    padCount = 2;
                }
            }
        }

        // Count illegal characters (including '\r', '\n') to know what size the returned array will be,
        // so we don't have to reallocate & copy it later.
        int sepCnt = 0; // Number of separator characters. (Actually illegal characters, but that's a bonus...)
        for (int i = 0; i < padPos; i++) {
            if (IA[sArr[i]] < 0) {
                sepCnt++;
            }
        }

        final int len = ((padPos + padCount - sepCnt) * 6 >> 3) - padCount;
        if (len <= 0) {
            return new byte[0];
        }
        final byte[] dArr = new byte[len];       // Preallocate byte[] of exact length
        int d = 0;
        int i = 0;
        try {
            for (int s = 0; d < len; ) {
                // Assemble three bytes into an int from four "valid" characters.
                i = 0;
                for (int j = 0; j < 4; j++) {   // j only increased if a valid char was found.
                    int c = IA[sArr[s++]];
                    if (c >= 0) {
                        i |= c << (18 - j * 6);
                    } else {
                        j--;
                    }
                }
                // Add the bytes
                dArr[d++] = (byte) (i >> 16);
                if (d < len) {
                    dArr[d++] = (byte) (i >> 8);
                    if (d < len) {
                        dArr[d++] = (byte) i;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            // Handle url-safe input (with no padding).
            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len) {
                    dArr[d] = (byte) i;
                }
            }
        }
        return dArr;
    }

    /**
     * Decodes a BASE64 encoded byte array. All illegal characters will be ignored and can handle both arrays with
     * and without line separators.
     *
     * @param sArr The source array.
     * @return The decoded array of bytes. May be of length 0.
     */
    static byte[] decode(final byte[] sArr) {
        final int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }

        // Find earliest pad character so that we can decode things like "AA==AA==" consistently.
        int padPos = sLen;
        int padCount = 0;
        for (int i = sLen - 1; i >= 0; i--) {
            if (sArr[i] == '=') {
                padPos = i;
                padCount = 1;
                if (i + 1 < sLen && sArr[i + 1] == '=') {
                    padCount = 2;
                }
            }
        }

        // Count illegal characters (including '\r', '\n') to know what size the returned array will be,
        // so we don't have to reallocate & copy it later.
        int sepCnt = 0; // Number of separator characters. (Actually illegal characters, but that's a bonus...)
        for (int i = 0; i < padPos; i++) {
            if (IA[sArr[i] & 0xff] < 0) {
                sepCnt++;
            }
        }

        final int len = ((padPos + padCount - sepCnt) * 6 >> 3) - padCount;
        if (len <= 0) {
            return new byte[0];
        }
        final byte[] dArr = new byte[len];       // Preallocate byte[] of exact length
        int d = 0;
        int i = 0;
        try {
            for (int s = 0; d < len; ) {
                // Assemble three bytes into an int from four "valid" characters.
                i = 0;
                for (int j = 0; j < 4; j++) {   // j only increased if a valid char was found.
                    int c = IA[sArr[s++] & 0xff];
                    if (c >= 0) {
                        i |= c << (18 - j * 6);
                    } else {
                        j--;
                    }
                }

                // Add the bytes
                dArr[d++] = (byte) (i >> 16);
                if (d < len) {
                    dArr[d++] = (byte) (i >> 8);
                    if (d < len) {
                        dArr[d++] = (byte) i;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            // Handle url-safe input (with no padding).
            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len) {
                    dArr[d] = (byte) i;
                }
            }
        }
        return dArr;
    }

    /**
     * Encodes a raw byte array into a BASE64 <code>byte[]</code> representation i accordance with RFC 2045.
     *
     * @param sArr          The bytes to convert.
     * @param lineSep       Optional "\r\n" after 76 characters, unless end of file.<br>
     *                      No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
     *                      little faster.
     * @param urlSafe       If true, use the URL_SAFE base64 alphabet (-_) instead of the standard alphabet (+/).
     * @param maxResultSize Largest size of result we are willing to encode (typically Integer.MAX_VALUE).
     * @return A BASE64 encoded array.
     */
    static byte[] encodeToByte(byte[] sArr, final boolean lineSep, boolean urlSafe, int maxResultSize) {
        return encodeToByte(sArr, lineSep, urlSafe, maxResultSize, CRLF, 76);
    }

    /**
     * Encodes a raw byte array into a BASE64 <code>byte[]</code> representation i accordance with RFC 2045.
     *
     * @param sArr          The bytes to convert.
     * @param lineSep       Optional "\r\n" after 76 characters, unless end of file.<br>
     *                      No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
     *                      little faster.
     * @param urlSafe       If true, use the URL_SAFE base64 alphabet (-_) instead of the standard alphabet (+/).
     * @param maxResultSize Largest size of result we are willing to encode (typically Integer.MAX_VALUE).
     * @param lineSeparator Sequence of bytes to use as the line separator (typically {'\r','\n'}).  Ignored
     *                      if <code>lineSep</code> is set to false.
     * @param lineLen       Number of characters to write out per line before writing the lineSeparator
     *                      sequence.  Ignored if <code>lineSep</code> is set to false.
     * @return A BASE64 encoded array.
     */
    static byte[] encodeToByte(
            final byte[] sArr, final boolean lineSep, final boolean urlSafe, final int maxResultSize,
            final byte[] lineSeparator, final int lineLen
    ) {
        if (sArr == null || sArr.length == 0) { return sArr; }

        final int sLen = sArr.length;
        final int eLen = (sLen / 3) * 3;                        // Length of even 24-bits.
        final int left = sLen - eLen;                           // A value between 0 and 2.
        final int cCnt = ((sLen - 1) / 3 + 1) << 2;             // Returned character count
        int dLen = cCnt + (lineSep ? (cCnt - 1) / lineLen * lineSeparator.length : 0); // Length of returned array

        // org.apache.commons.binary.codec.Base64 always ends with CRLF in chunking mode.
        if (lineSep) {
            dLen += lineSeparator.length;
        }

        final char[] ENCODE_ARRAY = urlSafe ? ApacheModifiedMiGBase64.CA_URL_SAFE : ApacheModifiedMiGBase64.CA;
        if (urlSafe && left > 0) {
            dLen--;
            if (left != 2) {
                dLen--;
            }
        }
        checkLen(dLen, maxResultSize);
        final byte[] dArr = new byte[dLen];

        // Encode even 24-bits
        int charCount = 0;
        for (int s = 0, d = 0; s < eLen; ) {
            // Copy next three bytes into lower 24 bits of int, paying attension to sign.
            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8 | (sArr[s++] & 0xff);

            // Encode the int into four chars
            dArr[d++] = (byte) ENCODE_ARRAY[(i >>> 18) & 0x3f];
            dArr[d++] = (byte) ENCODE_ARRAY[(i >>> 12) & 0x3f];
            dArr[d++] = (byte) ENCODE_ARRAY[(i >>> 6) & 0x3f];
            dArr[d++] = (byte) ENCODE_ARRAY[i & 0x3f];
            charCount += 4;

            // Add optional line separator
            if (lineSep && charCount % lineLen <= 3 && d < dLen - lineSeparator.length) {
                System.arraycopy(lineSeparator, 0, dArr, d, lineSeparator.length);
                d += lineSeparator.length;
            }
        }

        // Make space for our final CRLF.
        if (lineSep) {
            dLen -= lineSeparator.length;
        }

        // Pad and encode last bits if source isn't an even 24 bits.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10) | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            // (url-safe omits the '=' padding).
            if (urlSafe && left == 2) {
                dArr[dLen - 3] = (byte) ENCODE_ARRAY[i >> 12];
                dArr[dLen - 2] = (byte) ENCODE_ARRAY[(i >>> 6) & 0x3f];
                dArr[dLen - 1] = (byte) ENCODE_ARRAY[i & 0x3f];
            } else if (urlSafe) {
                dArr[dLen - 2] = (byte) ENCODE_ARRAY[i >> 12];
                dArr[dLen - 1] = (byte) ENCODE_ARRAY[(i >>> 6) & 0x3f];
            } else {
                dArr[dLen - 4] = (byte) ENCODE_ARRAY[i >> 12];
                dArr[dLen - 3] = (byte) ENCODE_ARRAY[(i >>> 6) & 0x3f];
                dArr[dLen - 2] = (byte) (left == 2 ? ENCODE_ARRAY[i & 0x3f] : '=');
                dArr[dLen - 1] = '=';
            }
        }

        // And now we append our final CRLF if necessary.
        if (lineSep) {
            dLen += lineSeparator.length;
            System.arraycopy(lineSeparator, 0, dArr, dLen - lineSeparator.length, lineSeparator.length);
        }
        return dArr;
    }

    /**
     * Encodes a raw byte array into a BASE64 <code>char[]</code> representation in accordance with RFC 2045.
     *
     * @param sArr          The bytes to convert.
     * @param lineSep       Optional "\r\n" after 76 characters, unless end of file.<br>
     *                      No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
     *                      little faster.
     * @param urlSafe       If true, use the URL_SAFE base64 alphabet (-_) instead of the standard alphabet (+/).
     * @param maxResultSize Largest size of result we are willing to encode (typically Integer.MAX_VALUE).
     * @return A BASE64 encoded array.
     */
    static char[] encodeToChar(
            final byte[] sArr, final boolean lineSep, final boolean urlSafe, final int maxResultSize
    ) {
        if (sArr == null) { return null; }
        if (sArr.length == 0) { return new char[0]; }

        final int sLen = sArr.length;
        final int eLen = (sLen / 3) * 3;                        // Length of even 24-bits.
        final int left = sLen - eLen;                           // A value between 0 and 2.
        final int cCnt = ((sLen - 1) / 3 + 1) << 2;             // Returned character count
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0); // Length of returned array

        // org.apache.commons.binary.codec.Base64 always ends with CRLF in chunking mode.
        if (lineSep) {
            dLen += 2;
        }

        final char[] ENCODE_ARRAY = urlSafe ? ApacheModifiedMiGBase64.CA_URL_SAFE : ApacheModifiedMiGBase64.CA;
        if (urlSafe && left > 0) {
            dLen--;
            if (left != 2) {
                dLen--;
            }
        }
        checkLen(dLen, maxResultSize);
        final char[] dArr = new char[dLen];

        // Encode even 24-bits
        for (int s = 0, d = 0, cc = 0; s < eLen; ) {
            // Copy next three bytes into lower 24 bits of int, paying attension to sign.
            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8 | (sArr[s++] & 0xff);

            // Encode the int into four chars
            dArr[d++] = ENCODE_ARRAY[(i >>> 18) & 0x3f];
            dArr[d++] = ENCODE_ARRAY[(i >>> 12) & 0x3f];
            dArr[d++] = ENCODE_ARRAY[(i >>> 6) & 0x3f];
            dArr[d++] = ENCODE_ARRAY[i & 0x3f];

            // Add optional line separator
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        // Make space for our final CRLF.
        if (lineSep) {
            dLen -= 2;
        }

        // Pad and encode last bits if source isn't even 24 bits.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10) | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            // (url-safe omits the '=' padding).
            if (urlSafe && left == 2) {
                dArr[dLen - 3] = ENCODE_ARRAY[i >> 12];
                dArr[dLen - 2] = ENCODE_ARRAY[(i >>> 6) & 0x3f];
                dArr[dLen - 1] = ENCODE_ARRAY[i & 0x3f];
            } else if (urlSafe) {
                dArr[dLen - 2] = ENCODE_ARRAY[i >> 12];
                dArr[dLen - 1] = ENCODE_ARRAY[(i >>> 6) & 0x3f];
            } else {
                dArr[dLen - 4] = ENCODE_ARRAY[i >> 12];
                dArr[dLen - 3] = ENCODE_ARRAY[(i >>> 6) & 0x3f];
                dArr[dLen - 2] = left == 2 ? ENCODE_ARRAY[i & 0x3f] : '=';
                dArr[dLen - 1] = '=';
            }
        }

        // And now we append our final CRLF if necessary.
        if (lineSep) {
            dLen += 2;
            dArr[dLen - 2] = '\r';
            dArr[dLen - 1] = '\n';
        }
        return dArr;
    }

    /**
     * Encodes a raw byte array into a BASE64 <code>String</code> representation i accordance with RFC 2045.
     *
     * @param sArr          The bytes to convert.
     * @param lineSep       Optional "\r\n" after 76 characters, unless end of file.<br>
     *                      No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
     *                      little faster.
     * @param urlSafe       If true, use the URL_SAFE base64 alphabet (-_) instead of the standard alphabet (+/).
     * @param maxResultSize Largest size of result we are willing to encode (typically Integer.MAX_VALUE).
     * @return A BASE64 encoded array.
     */
    static String encodeToString(
            byte[] sArr, boolean lineSep, boolean urlSafe, int maxResultSize
    ) {
        if (sArr == null) { return null; }
        if (sArr.length == 0) { return ""; }

        // Reuse char[] since we can't create a String incrementally anyway and StringBuffer/Builder would be slower.
        return new String(encodeToChar(sArr, lineSep, urlSafe, maxResultSize));
    }


    private static void checkLen(int dLen, int maxResultSize) {
        if (dLen > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                    dLen +
                    ") than the specified maximum size of " +
                    maxResultSize);
        }
    }

}