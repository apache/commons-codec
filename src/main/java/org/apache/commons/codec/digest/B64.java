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
package org.apache.commons.codec.digest;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Base64-like method to convert binary bytes into ASCII chars.
 * <p>
 * TODO: Can Base64 be reused?
 * </p>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @since 1.7
 */
class B64 {

    /**
     * Table with characters for Base64 transformation.
     */
    static final String B64T_STRING = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Table with characters for Base64 transformation.
     */
    static final char[] B64T_ARRAY = B64T_STRING.toCharArray();

    /**
     * Base64 like conversion of bytes to ASCII chars.
     *
     * @param b2
     *            A byte from the result.
     * @param b1
     *            A byte from the result.
     * @param b0
     *            A byte from the result.
     * @param outLen
     *            The number of expected output chars.
     * @param buffer
     *            Where the output chars is appended to.
     */
    static void b64from24bit(final byte b2, final byte b1, final byte b0, final int outLen,
                             final StringBuilder buffer) {
        // The bit masking is necessary because the JVM byte type is signed!
        int w = ((b2 << 16) & 0x00ffffff) | ((b1 << 8) & 0x00ffff) | (b0 & 0xff);
        // It's effectively a "for" loop but kept to resemble the original C code.
        int n = outLen;
        while (n-- > 0) {
            buffer.append(B64T_ARRAY[w & 0x3f]);
            w >>= 6;
        }
    }

  /**
   * Generates a string of random chars from the B64T set.
   * <p>
   * The salt is generated with {@link SecureRandom}.
   * </p>
   *
   * @param num Number of chars to generate.
   * @return a random salt {@link String}.
   */
  static String getRandomSalt(final int num) {
    return getRandomSalt(num, new SecureRandom());
  }

    /**
     * Generates a string of random chars from the B64T set.
     * <p>
     * The salt is generated with the {@link Random} provided.
     * </p>
     *
     * @param num Number of chars to generate.
     * @param random an instance of {@link Random}.
     * @return a random salt {@link String}.
     */
    static String getRandomSalt(final int num, final Random random) {
      final StringBuilder saltString = new StringBuilder(num);
      for (int i = 1; i <= num; i++) {
        saltString.append(B64T_STRING.charAt(random.nextInt(B64T_STRING.length())));
      }
      return saltString.toString();
    }
}
