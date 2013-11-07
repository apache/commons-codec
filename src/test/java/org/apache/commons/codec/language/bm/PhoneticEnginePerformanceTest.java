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

/**
 * Tests performance for {@link PhoneticEngine}.
 * <p>
 * See <a href="https://issues.apache.org/jira/browse/CODEC-174">[CODEC-174] Improve performance of Beider Morse
 * encoder</a>.
 * </p>
 * <p>
 * Results for November 7, 2013, project SVN revision 1539678.
 * </p>
 * <ol>
 * <li>Time for encoding 80,000 times the input 'Angelo': 33,039 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 32,297 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 32,857 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': <b>31,561 millis.</b></li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 32,665 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 32,215 millis.</li>
 * </ol>
 * <p>
 * On this file's revision 1539678, with patch <a
 * href="https://issues.apache.org/jira/secure/attachment/12611963/CODEC-174-change-rules-storage-to-Map.patch"
 * >CODEC-174-change-rules-storage-to-Map</a>:
 * </p>
 * <ol>
 * <li>Time for encoding 80,000 times the input 'Angelo': 18,196 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 13,858 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 13,644 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': <b>13,591 millis.</b></li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 13,861 millis.</li>
 * <li>Time for encoding 80,000 times the input 'Angelo': 13,696 millis.</li>
 * </ol>
 */
public class PhoneticEnginePerformanceTest {

    private static final int LOOP = 80000;

    @Test
    public void test() {
        PhoneticEngine engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
        final String input = "Angelo";
        final long startMillis = System.currentTimeMillis();
        for (int i = 0; i < LOOP; i++) {
            engine.encode(input);
        }
        final long totalMillis = System.currentTimeMillis() - startMillis;
        System.out.println(String.format("Time for encoding %,d times the input '%s': %,d millis.", LOOP, input,
                totalMillis));
    }
}
