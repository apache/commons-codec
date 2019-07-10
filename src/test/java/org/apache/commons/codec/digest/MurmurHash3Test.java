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

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.digest.MurmurHash3.IncrementalHash32;
import org.junit.Test;

public class MurmurHash3Test {

	@Test
	public void testHashCodeM3_64() {
		byte[] origin = ("It was the best of times, it was the worst of times,"
				+ " it was the age of wisdom, it was the age of foolishness,"
				+ " it was the epoch of belief, it was the epoch of incredulity,"
				+ " it was the season of Light, it was the season of Darkness,"
				+ " it was the spring of hope, it was the winter of despair,"
				+ " we had everything before us, we had nothing before us," + " we were all going direct to Heaven,"
				+ " we were all going direct the other way.").getBytes();
		long hash = MurmurHash3.hash64(origin, 0, origin.length);
		assertEquals(305830725663368540L, hash);

		byte[] originOffset = new byte[origin.length + 150];
		Arrays.fill(originOffset, (byte) 123);
		System.arraycopy(origin, 0, originOffset, 150, origin.length);
		hash = MurmurHash3.hash64(originOffset, 150, origin.length);
		assertEquals(305830725663368540L, hash);
	}

	@Test
	public void test64() {
		final int seed = 123, iters = 1000000;
		ByteBuffer SHORT_BUFFER = ByteBuffer.allocate(Short.BYTES);
		ByteBuffer INT_BUFFER = ByteBuffer.allocate(Integer.BYTES);
		ByteBuffer LONG_BUFFER = ByteBuffer.allocate(Long.BYTES);
		Random rdm = new Random(seed);
		for (int i = 0; i < iters; ++i) {
			long ln = rdm.nextLong();
			int in = rdm.nextInt();
			short sn = (short) (rdm.nextInt(2 * Short.MAX_VALUE - 1) - Short.MAX_VALUE);
			float fn = rdm.nextFloat();
			double dn = rdm.nextDouble();
			SHORT_BUFFER.putShort(0, sn);
			assertEquals(MurmurHash3.hash64(SHORT_BUFFER.array()), MurmurHash3.hash64(sn));
			INT_BUFFER.putInt(0, in);
			assertEquals(MurmurHash3.hash64(INT_BUFFER.array()), MurmurHash3.hash64(in));
			LONG_BUFFER.putLong(0, ln);
			assertEquals(MurmurHash3.hash64(LONG_BUFFER.array()), MurmurHash3.hash64(ln));
			INT_BUFFER.putFloat(0, fn);
			assertEquals(MurmurHash3.hash64(INT_BUFFER.array()), MurmurHash3.hash64(Float.floatToIntBits(fn)));
			LONG_BUFFER.putDouble(0, dn);
			assertEquals(MurmurHash3.hash64(LONG_BUFFER.array()), MurmurHash3.hash64(Double.doubleToLongBits(dn)));
		}
	}

	@Test
	public void testIncremental() {
		final int seed = 123, arraySize = 1023;
		byte[] bytes = new byte[arraySize];
		new Random(seed).nextBytes(bytes);
		int expected = MurmurHash3.hash32(bytes, arraySize);
		MurmurHash3.IncrementalHash32 same = new IncrementalHash32(), diff = new IncrementalHash32();
		for (int blockSize = 1; blockSize <= arraySize; ++blockSize) {
			byte[] block = new byte[blockSize];
			same.start(MurmurHash3.DEFAULT_SEED);
			diff.start(MurmurHash3.DEFAULT_SEED);
			for (int offset = 0; offset < arraySize; offset += blockSize) {
				int length = Math.min(arraySize - offset, blockSize);
				same.add(bytes, offset, length);
				System.arraycopy(bytes, offset, block, 0, length);
				diff.add(block, 0, length);
			}
			assertEquals("Block size " + blockSize, expected, same.end());
			assertEquals("Block size " + blockSize, expected, diff.end());
		}
	}

	@Test
	public void testTwoLongOrdered() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
		for (long i = 0; i < 1000; i++) {
			for (long j = 0; j < 1000; j++) {
				buffer.putLong(0, i);
				buffer.putLong(Long.BYTES, j);
				assertEquals(MurmurHash3.hash32(buffer.array()), MurmurHash3.hash32(i, j));
			}
		}
	}

	@Test
	public void testTwoLongRandom() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
		Random random = new Random();
		for (long i = 0; i < 1000; i++) {
			for (long j = 0; j < 1000; j++) {
				long x = random.nextLong();
				long y = random.nextLong();
				buffer.putLong(0, x);
				buffer.putLong(Long.BYTES, y);
				assertEquals(MurmurHash3.hash32(buffer.array()), MurmurHash3.hash32(x, y));
			}
		}
	}

	@Test
	public void testSingleLongOrdered() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		for (long i = 0; i < 1000; i++) {
			buffer.putLong(0, i);
			assertEquals(MurmurHash3.hash32(buffer.array()), MurmurHash3.hash32(i));
		}
	}

	@Test
	public void testSingleLongRandom() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		Random random = new Random();
		for (long i = 0; i < 1000; i++) {
			long x = random.nextLong();
			buffer.putLong(0, x);
			assertEquals(MurmurHash3.hash32(buffer.array()), MurmurHash3.hash32(x));
		}
	}

}
