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

package org.apache.commons.codec.language.bm;

import org.junit.Test;

public class CacheSubSequencePerformanceTest {

    @Test
    public void test() {
        final int times = 10000000;
        System.out.print("Test with String : ");
        test("Angelo", times);
        System.out.print("Test with StringBuilder : ");
        test(new StringBuilder("Angelo"), times);
        System.out.print("Test with cached String : ");
        test(cacheSubSequence("Angelo"), times);
        System.out.print("Test with cached StringBuilder : ");
        test(cacheSubSequence(new StringBuilder("Angelo")), times);
    }

    private void test(final CharSequence input, final int times) {
        final long beginTime = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            test(input);
        }
        System.out.println(System.currentTimeMillis() - beginTime + " millis");
    }

    private void test(final CharSequence input) {
        for (int i = 0; i < input.length(); i++) {
            for (int j = i; j <= input.length(); j++) {
                input.subSequence(i, j);
            }
        }
    }

    private CharSequence cacheSubSequence(final CharSequence cached) {
        final CharSequence[][] cache = new CharSequence[cached.length()][cached.length()];
        return new CharSequence() {
            @Override
            public char charAt(final int index) {
                return cached.charAt(index);
            }

            @Override
            public int length() {
                return cached.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                if (start == end) {
                    return "";
                }
                CharSequence res = cache[start][end - 1];
                if (res == null) {
                    res = cached.subSequence(start, end);
                    cache[start][end - 1] = res;
                }
                return res;
            }
        };
    }
}
