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

	private static final String TEST = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";

	@Test
	public void test32_String() {
		// Arrange
		String origin = TEST;
		
		// Act
		int result = MurmurHash3.hash32(origin);

		// Assert
		assertEquals(-436507231, result);
	}
	
	@Test
	public void testHashCodeM3_64() {
		byte[] origin =TEST.getBytes();
		long hash = MurmurHash3.hash64(origin, 0, origin.length);
		assertEquals(5785358552565094607L, hash);

		byte[] originOffset = new byte[origin.length + 150];
		Arrays.fill(originOffset, (byte) 123);
		System.arraycopy(origin, 0, originOffset, 150, origin.length);
		hash = MurmurHash3.hash64(originOffset, 150, origin.length);
		assertEquals(5785358552565094607L, hash);
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
	public void test128_Short() {
		// Arrange
		ByteBuffer BUFFER = ByteBuffer.allocate(Short.BYTES);
		BUFFER.putShort(0, (short) 2);

		// Act
		long[] result = MurmurHash3.hash128(BUFFER.array());

		// Assert
		assertEquals(result.length, 2);
		assertEquals(8673501042631707204L, result[0]);
		assertEquals(491907755572407714L, result[1]);
	}

	@Test
	public void test128_Int() {
		// Arrange
		ByteBuffer BUFFER = ByteBuffer.allocate(Integer.BYTES);
		BUFFER.putInt(0, 3);

		// Act
		long[] result = MurmurHash3.hash128(BUFFER.array());

		// Assert
		assertEquals(result.length, 2);
		assertEquals(2448828847287705405L, result[0]);
		assertEquals(-4568642211270719983L, result[1]);
	}

	@Test
	public void test128_Long() {
		// Arrange
		ByteBuffer BUFFER = ByteBuffer.allocate(Long.BYTES);
		BUFFER.putLong(0, 8675309L);

		// Act
		long[] result = MurmurHash3.hash128(BUFFER.array());

		// Assert
		assertEquals(result.length, 2);
		assertEquals(2339756411022791995L, result[0]);
		assertEquals(8242951144762217305L, result[1]);
	}

	@Test
	public void test128_Double() {
		// Arrange
		ByteBuffer BUFFER = ByteBuffer.allocate(Double.BYTES);
		BUFFER.putDouble(0, 456.987);

		// Act
		long[] result = MurmurHash3.hash128(BUFFER.array());

		// Assert
		assertEquals(result.length, 2);
		assertEquals(6877430437712399133L, result[0]);
		assertEquals(-8576421050167250536L, result[1]);
	}

	@Test
	public void test128_String() {
		// Arrange
		String origin = TEST;
		
		// Act
		long[] result = MurmurHash3.hash128(origin);

		// Assert
		assertEquals(result.length, 2);
		assertEquals(6409160382500807310L, result[0]);
		assertEquals(-7835827609130513921L, result[1]);
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
