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

import org.junit.Assert;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.digest.MurmurHash3.IncrementalHash32;
import org.junit.Test;

/**
 * Test for {@link MurmurHash3}.
 */
public class MurmurHash3Test {

    /** Test data for the hash64 method. */
    private static final String TEST_HASH64 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";

    /**
     * 256 integers in the range [0,255] arranged in random order.
     * This ensure all bytes are covered in a full hash of the bytes.
     *
     * <p>To create test data using the python library mmh3 (which invokes the c++ code):</p>
     *
     * <pre>
     * import mmh3
     * import numpy as np
     * # Put random bytes in here:
     * bytes = np.uint8([46,246,249,184,247,84,99,144,62,77,195,220,92,20,150,159,38,40,124,252,185,28,63,13,213,172,85,198,118,74,109,157,132,216,76,177,173,23,140,86,146,95,54,176,114,179,234,174,183,141,122,12,60,116,200,142,6,167,59,240,33,29,165,111,243,30,219,110,255,53,32,35,64,225,96,152,70,41,133,80,244,127,57,199,5,164,151,49,26,180,203,83,108,39,126,208,42,206,178,19,69,223,71,231,250,125,211,232,189,55,44,82,48,221,43,192,241,103,155,27,51,163,21,169,91,94,217,191,78,72,93,102,104,105,8,113,100,143,89,245,227,120,160,251,153,145,45,218,168,233,229,253,67,22,182,98,137,128,135,11,214,66,73,171,188,170,131,207,79,106,24,75,237,194,7,129,215,81,248,242,16,25,136,147,156,97,52,10,181,17,205,58,101,68,230,1,37,0,222,88,130,148,224,47,50,197,34,212,196,209,14,36,139,228,154,31,175,202,236,161,3,162,190,254,134,119,4,61,65,117,186,107,204,9,187,201,90,149,226,56,239,238,235,112,87,18,121,115,138,123,210,2,193,166,158,15])
     * # Seed as appropriate. The default seed in mmh3 is zero.
     * seed = 789
     * # Output data. Adjust as appropriate.
     * for x in range(0, 256):
     *   # 32-bit hash
     *   mmh3.hash(bytes[:x])
     *   mmh3.hash(bytes[:x], seed)
     *   # 128-bit hash as two 64 bit signed ints
     *   mmh3.hash64(bytes[:x])
     *   mmh3.hash64(bytes[:x], seed)
     * # Sub-arrays: lower inclusive, upper exclusive:
     * mmh3.hash(bytes[13:15])
     * </pre>
     */
    private static final int[] RANDOM_INTS = {
        46,246,249,184,247,84,99,144,62,77,195,220,92,20,150,159,38,40,124,252,185,28,63,13,213,172,85,198,118,74,109,157,132,216,76,177,173,23,140,86,146,95,54,176,114,179,234,174,183,141,122,12,60,116,200,142,6,167,59,240,33,29,165,111,243,30,219,110,255,53,32,35,64,225,96,152,70,41,133,80,244,127,57,199,5,164,151,49,26,180,203,83,108,39,126,208,42,206,178,19,69,223,71,231,250,125,211,232,189,55,44,82,48,221,43,192,241,103,155,27,51,163,21,169,91,94,217,191,78,72,93,102,104,105,8,113,100,143,89,245,227,120,160,251,153,145,45,218,168,233,229,253,67,22,182,98,137,128,135,11,214,66,73,171,188,170,131,207,79,106,24,75,237,194,7,129,215,81,248,242,16,25,136,147,156,97,52,10,181,17,205,58,101,68,230,1,37,0,222,88,130,148,224,47,50,197,34,212,196,209,14,36,139,228,154,31,175,202,236,161,3,162,190,254,134,119,4,61,65,117,186,107,204,9,187,201,90,149,226,56,239,238,235,112,87,18,121,115,138,123,210,2,193,166,158,15
    };

    /**
     * 256 bytes in the range [0,255] arranged in random order.
     * This ensure all bytes are covered in a full hash of the bytes.
     */
    private static final byte[] RANDOM_BYTES;

    static {
        RANDOM_BYTES = new byte[RANDOM_INTS.length];
        for (int i=0; i<RANDOM_BYTES.length; i++) {
            RANDOM_BYTES[i] = (byte)RANDOM_INTS[i];
        }
    }

    // Tests match the declared order of the class:
    // hash32
    // hash64
    // hash128
    // IncrementalHash32
    //
    // There is no reference for the hash64 method. It is not in the original murmurhash3 code.
    // Thus hash64 is not extensively tested and the reference results are assumed to be from the
    // Apache Hive project which was the source of the method.

    /**
     * Test the convenience method {@link MurmurHash3#hash32(long, long)} works as documented.
     */
    @Test
    public void testHash32LongLong() {
        final int length = MurmurHash3.LONG_BYTES * 2;
        final ByteBuffer buffer = ByteBuffer.allocate(length);
        final long[] data = createLongTestData();
        final int seed = MurmurHash3.DEFAULT_SEED;
        for (final long i : data) {
            for (final long j : data) {
                buffer.putLong(0, i);
                buffer.putLong(MurmurHash3.LONG_BYTES, j);
                Assert.assertEquals(MurmurHash3.hash32(buffer.array(), length, seed), MurmurHash3.hash32(i, j));
            }
        }
    }

    /**
     * Test the convenience method {@link MurmurHash3#hash32(long, long, int)} works as documented.
     */
    @Test
    public void testHash32LongLongSeed() {
        final int length = MurmurHash3.LONG_BYTES * 2;
        final ByteBuffer buffer = ByteBuffer.allocate(length);
        final long[] data = createLongTestData();
        final int seed = ThreadLocalRandom.current().nextInt();
        for (final long i : data) {
            for (final long j : data) {
                buffer.putLong(0, i);
                buffer.putLong(MurmurHash3.LONG_BYTES, j);
                Assert.assertEquals(MurmurHash3.hash32(buffer.array(), length, seed), MurmurHash3.hash32(i, j, seed));
            }
        }
    }

    /**
     * Test the convenience method {@link MurmurHash3#hash32(long)} works as documented.
     */
    @Test
    public void testHash32Long() {
        final int length = MurmurHash3.LONG_BYTES;
        final ByteBuffer buffer = ByteBuffer.allocate(length);
        final long[] data = createLongTestData();
        final int seed = MurmurHash3.DEFAULT_SEED;
        for (final long i : data) {
            buffer.putLong(0, i);
            Assert.assertEquals(MurmurHash3.hash32(buffer.array(), length, seed), MurmurHash3.hash32(i));
        }
    }

    /**
     * Test the convenience method {@link MurmurHash3#hash32(long, int)} works as documented.
     */
    @Test
    public void testHash32LongSeed() {
        final int length = MurmurHash3.LONG_BYTES;
        final ByteBuffer buffer = ByteBuffer.allocate(length);
        final long[] data = createLongTestData();
        final int seed = ThreadLocalRandom.current().nextInt();
        for (final long i : data) {
            buffer.putLong(0, i);
            Assert.assertEquals(MurmurHash3.hash32(buffer.array(), length, seed), MurmurHash3.hash32(i, seed));
        }
    }

    /**
     * Creates a set of long values to test the hash utility methods.
     *
     * @return the data
     */
    private static long[] createLongTestData() {
        final long[] data = new long[100];
        // Edge cases:
        data[0] = 0;
        data[1] = Long.MIN_VALUE;
        data[2] = Long.MAX_VALUE;
        data[3] = -1L;
        for (int i = 4; i < data.length; i++) {
            data[i] = ThreadLocalRandom.current().nextLong();
        }
        return data;
    }

    /**
     * Test the {@link MurmurHash3#hash32(byte[])} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash32() {
        // mmh3.hash(bytes, 104729)
        Assert.assertEquals(1905657630, MurmurHash3.hash32(RANDOM_BYTES));

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash(bytes[:x], 104729), ',')
        final int[] answers = {-965378730, 418246248, 1175981702, -616767012, -12304673, 1697005142, -1212417875,
            -420043393, -826068069, -1721451528, -544986914, 892942691, 27535194, 974863697, 1835661694, -894915836,
            1826914566, -677571679, 1218764493, -375719050, -1320048170, -503583763, 1321750696, -175065786, -496878386,
            -12065683, 512351473, 716560510, -1944803590, 10253199, 1105638211, 525704533,};
        for (int i = 0; i < answers.length; i++) {
            final byte[] bytes = Arrays.copyOf(RANDOM_BYTES, i);
            // Known bug: Incorrect result for non modulus of 4 byte arrays if there are
            // negative bytes
            if (i % 4 == 0 || !negativeBytes(bytes, (i / 4) * 4, i % 4)) {
                Assert.assertEquals(answers[i], MurmurHash3.hash32(bytes));
            } else {
                Assert.assertNotEquals(answers[i], MurmurHash3.hash32(bytes));
            }
        }
    }

    /**
     * Test the {@link MurmurHash3#hash32(byte[], int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash32WithLength() {
        // mmh3.hash(bytes, 104729)
        Assert.assertEquals(1905657630, MurmurHash3.hash32(RANDOM_BYTES, RANDOM_BYTES.length));

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash(bytes[:x], 104729), ',')
        final int[] answers = {-965378730, 418246248, 1175981702, -616767012, -12304673, 1697005142, -1212417875,
            -420043393, -826068069, -1721451528, -544986914, 892942691, 27535194, 974863697, 1835661694, -894915836,
            1826914566, -677571679, 1218764493, -375719050, -1320048170, -503583763, 1321750696, -175065786, -496878386,
            -12065683, 512351473, 716560510, -1944803590, 10253199, 1105638211, 525704533,};
        for (int i = 0; i < answers.length; i++) {
            // Known bug: Incorrect result for non modulus of 4 byte arrays if there are
            // negative bytes
            if (i % 4 == 0 || !negativeBytes(RANDOM_BYTES, (i / 4) * 4, i % 4)) {
                Assert.assertEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, i));
            } else {
                Assert.assertNotEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, i));
            }
        }
    }

    /**
     * Test the {@link MurmurHash3#hash32(byte[], int, int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash32WithLengthAndSeed() {
        final int seed = -42;
        // mmh3.hash(bytes, -42)
        Assert.assertEquals(1693958011, MurmurHash3.hash32(RANDOM_BYTES, RANDOM_BYTES.length, seed));

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash(bytes[:x], -42), ',')
        final int[] answers = {192929823, 7537536, -99368911, -1261039957, -1719251056, -399594848, 372285930,
            -80756529, 1770924588, -1071759082, 1832217706, 1921413466, 1701676113, 675584253, 1620634486, 427719405,
            -973727623, 533209078, 136016960, 1947798330, 428635832, -1125743884, 793211715, -2068889169, -136818786,
            -720841364, -891446378, 1990860976, -710528065, -1602505694, -1493714677, 1911121524,};
        for (int i = 0; i < answers.length; i++) {
            // Known bug: Incorrect result for non modulus of 4 byte arrays if there are
            // negative bytes
            if (i % 4 == 0 || !negativeBytes(RANDOM_BYTES, (i / 4) * 4, i % 4)) {
                Assert.assertEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, i, seed));
            } else {
                Assert.assertNotEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, i, seed));
            }
        }
    }

    /**
     * Test the {@link MurmurHash3#hash32(byte[], int, int, int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash32WithOffsetLengthAndSeed() {
        final int seed = -42;
        final int offset = 13;

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash(bytes[13:x+13], -42), ',')
        final int[] answers = {192929823, -27171978, -1282326280, -816314453, -1176217753, -1904531247, 1962794233,
            -1302316624, -1151850323, -1464386748, -369299427, 972232488, 1747314487, 2137398916, 690986564,
            -1985866226, -678669121, -2123325690, -253319081, 46181235, 656058278, 1401175653, 1750113912, -1567219725,
            2032742772, -2024269989, -305340794, 1161737942, -661265418, 172838872, -650122718, -1934812417,};
        for (int i = 0; i < answers.length; i++) {
            // Known bug: Incorrect result for non modulus of 4 byte arrays if there are
            // negative bytes
            if (i % 4 == 0 || !negativeBytes(RANDOM_BYTES, offset + (i / 4) * 4, i % 4)) {
                Assert.assertEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, offset, i, seed));
            } else {
                Assert.assertNotEquals(answers[i], MurmurHash3.hash32(RANDOM_BYTES, offset, i, seed));
            }
        }
    }

    /**
     * Check if the bytes are negative in the given range.
     *
     * @param bytes the bytes
     * @param start the start
     * @param length the length
     * @return true, if negative bytes exist
     */
    private static boolean negativeBytes(byte[] bytes, int start, int length) {
        for (int i = start; i < start + length; i++) {
            if (bytes[i] < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test the {@link MurmurHash3#hash32(String)} algorithm. This only tests it can return
     * the same value as {@link MurmurHash3#hash32(byte[], int, int, int)} if the string
     * is converted to bytes using the method {@link String#getBytes()}.
     *
     * <p>The test uses random strings created with random unicode code points.</p>
     */
    @Test
    public void testHash32String() {
        final int seed = 104729;
        // Range is end exclusive so this is random strings of length 1-10
        final int minSize = 1;
        final int maxSize = 11;
        // The Unicode Standard, Version 7.0, contains 112,956 characters
        final int codePoints = 112956;
        final char[] chars = new char[(maxSize - minSize) * 2];
        for (int i = 0; i < 1000; i++) {
            int pos = 0;
            final int size = ThreadLocalRandom.current().nextInt(minSize, maxSize);
            for (int j = 0; j < size; j++) {
                final int codePoint = ThreadLocalRandom.current().nextInt(codePoints);
                pos += Character.toChars(codePoint, chars, pos);
            }
            final String text = String.copyValueOf(chars, 0, pos);
            final byte[] bytes = text.getBytes();
            final int h1 = MurmurHash3.hash32(bytes, 0, bytes.length, seed);
            final int h2 = MurmurHash3.hash32(text);
            Assert.assertEquals(h1, h2);
        }
    }

    /**
     * Test to demonstrate the errors in {@link MurmurHash3#hash32(byte[], int, int, int)}
     * if the final 1, 2, or 3 bytes are negative.
     */
    @Test
    public void testHash32With1TrailingSignedByteIsInvalid() {
        // import mmh3
        // import numpy as np
        // mmh3.hash(np.uint8([-1]))
        // mmh3.hash(np.uint8([0, -1]))
        // mmh3.hash(np.uint8([0, 0, -1]))
        // mmh3.hash(np.uint8([-1, 0]))
        // mmh3.hash(np.uint8([-1, 0, 0]))
        // mmh3.hash(np.uint8([0, -1, 0]))
        Assert.assertNotEquals(-43192051, MurmurHash3.hash32(new byte[] {-1}, 0, 1, 0));
        Assert.assertNotEquals(-582037868, MurmurHash3.hash32(new byte[] {0, -1}, 0, 2, 0));
        Assert.assertNotEquals(922088087, MurmurHash3.hash32(new byte[] {0, 0, -1}, 0, 3, 0));
        Assert.assertNotEquals(-1309567588, MurmurHash3.hash32(new byte[] {-1, 0}, 0, 2, 0));
        Assert.assertNotEquals(-363779670, MurmurHash3.hash32(new byte[] {-1, 0, 0}, 0, 3, 0));
        Assert.assertNotEquals(-225068062, MurmurHash3.hash32(new byte[] {0, -1, 0}, 0, 3, 0));
    }

    /**
     * Test the {@link MurmurHash3#hash64(byte[])} algorithm.
     * Unknown origin of test data. It may be from the Apache Hive project.
     */
    @Test
    public void testHash64() {
        final byte[] origin = TEST_HASH64.getBytes();
        final long hash = MurmurHash3.hash64(origin);
        Assert.assertEquals(5785358552565094607L, hash);
    }

    /**
     * Test the {@link MurmurHash3#hash64(byte[], int, int)} algorithm.
     * Unknown origin of test data. It may be from the Apache Hive project.
     */
    @Test
    public void testHash64WithOffsetAndLength() {
        final byte[] origin = TEST_HASH64.getBytes();
        final byte[] originOffset = new byte[origin.length + 150];
        Arrays.fill(originOffset, (byte) 123);
        System.arraycopy(origin, 0, originOffset, 150, origin.length);
        final long hash = MurmurHash3.hash64(originOffset, 150, origin.length);
        Assert.assertEquals(5785358552565094607L, hash);
    }

    /**
     * Test the hash64() helper methods that work directly on primitives work as documented.
     * This test the methods return the same value as {@link MurmurHash3#hash64(byte[])}
     * with the byte[] created from the same primitive data via a {@link ByteBuffer}.
     */
    @Test
    public void testHash64WithPrimitives() {
        final int iters = 1000;
        final ByteBuffer shortBuffer = ByteBuffer.allocate(MurmurHash3.SHORT_BYTES);
        final ByteBuffer intBuffer = ByteBuffer.allocate(MurmurHash3.INTEGER_BYTES);
        final ByteBuffer longBuffer = ByteBuffer.allocate(MurmurHash3.LONG_BYTES);
        final byte[] shortBytes = shortBuffer.array();
        final byte[] intBytes = intBuffer.array();
        final byte[] longBytes = longBuffer.array();
        for (int i = 0; i < iters; ++i) {
            final long ln = ThreadLocalRandom.current().nextLong();
            // Shift bits to bet different bytes
            final int in = (int) (ln >>> 3);
            final short sn = (short) (ln >>> 5);
            shortBuffer.putShort(0, sn);
            Assert.assertEquals(MurmurHash3.hash64(shortBytes), MurmurHash3.hash64(sn));
            intBuffer.putInt(0, in);
            Assert.assertEquals(MurmurHash3.hash64(intBytes), MurmurHash3.hash64(in));
            longBuffer.putLong(0, ln);
            Assert.assertEquals(MurmurHash3.hash64(longBytes), MurmurHash3.hash64(ln));
        }
    }

    /**
     * Test the {@link MurmurHash3#hash64(byte[])} method is Murmur3-like but does not match
     * the bits returned from {@link MurmurHash3#hash128(byte[])}.
     *
     * <p>The hash64 method is not in the MurmurHash3 reference code and has been inherited
     * from the port from Apache Hive.<p>
     */
    @Test
    public void testHash64InNotEqualToHash128() {
        for (int i = 0; i < 32; i++) {
            final byte[] bytes = Arrays.copyOf(RANDOM_BYTES, i);
            final long h1 = MurmurHash3.hash64(bytes);
            final long[] hash = MurmurHash3.hash128(bytes);
            Assert.assertNotEquals("Did not expect hash64 to match upper bits of hash128", hash[0], h1);
            Assert.assertNotEquals("Did not expect hash64 to match lower bits of hash128", hash[1], h1);
        }
    }

    /**
     * Test the {@link MurmurHash3#hash128(byte[])} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash128() {
        // mmh3.hash64(bytes, 104729)
        Assert.assertArrayEquals(new long[] {-5614308156300707300L, -4165733009867452172L},
            MurmurHash3.hash128(RANDOM_BYTES));

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash64(bytes[:x], 104729), ',')
        final long[][] answers = {
            {-7122613646888064702L, -8341524471658347240L}, {5659994275039884826L, -962335545730945195L},
            {-7641758224504050283L, 4083131074855072837L}, {-9123564721037921804L, -3321998102976419641L},
            {-7999620158275145567L, -7769992740725283391L}, {2419143614837736468L, -5474637306496300103L},
            {7781591175729494939L, -9023178611551692650L}, {-3431043156265556247L, -6589990064676612981L},
            {6315693694262400182L, -6219942557302821890L}, {-8249934145502892979L, -5646083202776239948L},
            {7500109050276796947L, 5350477981718987260L}, {-6102338673930022315L, 3413065069102535261L},
            {-6440683413407781313L, -2374360388921904146L}, {-3071236194203069122L, 7531604855739305895L},
            {-7629408037856591130L, -4070301959951145257L}, {860008171111471563L, -9026008285726889896L},
            {8059667613600971894L, 3236009436748930210L}, {1833746055900036985L, 1418052485321768916L},
            {8161230977297923537L, -2668130155009407119L}, {3653111839268905630L, 5525563908135615453L},
            {-9163026480602019754L, 6819447647029564735L}, {1102346610654592779L, -6881918401879761029L},
            {-3109499571828331931L, -3782255367340446228L}, {-7467915444290531104L, 4704551260862209500L},
            {1237530251176898868L, 6144786892208594932L}, {2347717913548230384L, -7461066668225718223L},
            {-7963311463560798404L, 8435801462986138227L}, {-7493166089060196513L, 8163503673197886404L},
            {6807249306539951962L, -1438886581269648819L}, {6752656991043418179L, 6334147827922066123L},
            {-4534351735605790331L, -4530801663887858236L}, {-7886946241830957955L, -6261339648449285315L},        };
        for (int i = 0; i < answers.length; i++) {
            final byte[] bytes = Arrays.copyOf(RANDOM_BYTES, i);
            Assert.assertArrayEquals(answers[i], MurmurHash3.hash128(bytes));
        }
    }

    /**
     * Test the {@link MurmurHash3#hash128(byte[], int, int, int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash128WithOffsetLengthAndSeed() {
        // Seed must be positive
        final int seed = 42;
        final int offset = 13;

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash64(bytes[13:x+13], 42), ',')
        final long[][] answers = {{-1140915396076141277L, -3386313222241793095L},
            {2745805417334040752L, -3045882272665292331L}, {6807939080212835946L, -1975749467247671127L},
            {-7924884987449335214L, -4468571497642087939L}, {3005389733967167773L, -5809440073240597398L},
            {8032745196600164727L, 4545709434702374224L}, {2095398623732573832L, 1778447136435513908L},
            {4492723708121417255L, -7411125500882394867L}, {8467397417110552178L, -1503802302645548949L},
            {4189760269121918355L, -8004336343217265057L}, {4939298084211301953L, -8419135013628844658L},
            {5497136916151148085L, -394028342910298191L}, {3405983294878231737L, -3216533807498089078L},
            {5833223403351466775L, -1792451370239813325L}, {7730583391236194819L, 5356157313842354092L},
            {3111977482488580945L, -3119414725698132191L}, {3314524606404365027L, -1923219843083192742L},
            {7299569240140613949L, 4176392429810027494L}, {6398084683727166117L, 7703960505857395788L},
            {-8594572031068184774L, 4394224719145783692L}, {-7589785442804461713L, 4110439243215224554L},
            {-5343610105946840628L, -4423992782020122809L}, {-522490326525787270L, 289136460641968781L},
            {-5320637070354802556L, -7845553044730489027L}, {1344456408744313334L, 3803048032054968586L},
            {1131205296221907191L, -6256656049039287019L}, {8583339267101027117L, 8934225022848628726L},
            {-6379552869905441749L, 8973517768420051734L}, {5076646564516328801L, 8561479196844000567L},
            {-4610341636137642517L, -6694266039505142069L}, {-758896383254029789L, 4050360662271552727L},
            {-6123628195475753507L, 4283875822581966645L},};
        for (int i = 0; i < answers.length; i++) {
            Assert.assertArrayEquals("Length: " + i, answers[i], MurmurHash3.hash128(RANDOM_BYTES, offset, i, seed));
        }
    }

    /**
     * Test the {@link MurmurHash3#hash128(String)} algorithm. This only tests it can return
     * the same value as {@link MurmurHash3#hash128(byte[], int, int, int)} if the string
     * is converted to bytes using the method {@link String#getBytes()}.
     *
     * <p>The test uses random strings created with random unicode code points.</p>
     */
    @Test
    public void testHash128String() {
        final int seed = 104729;
        // Range is end exclusive so this is random strings of length 1-10
        final int minSize = 1;
        final int maxSize = 11;
        // The Unicode Standard, Version 7.0, contains 112,956 characters
        final int codePoints = 112956;
        final char[] chars = new char[(maxSize - minSize) * 2];
        for (int i = 0; i < 1000; i++) {
            int pos = 0;
            final int size = ThreadLocalRandom.current().nextInt(minSize, maxSize);
            for (int j = 0; j < size; j++) {
                final int codePoint = ThreadLocalRandom.current().nextInt(codePoints);
                pos += Character.toChars(codePoint, chars, pos);
            }
            final String text = String.copyValueOf(chars, 0, pos);
            final byte[] bytes = text.getBytes();
            final long[] h1 = MurmurHash3.hash128(bytes, 0, bytes.length, seed);
            final long[] h2 = MurmurHash3.hash128(text);
            Assert.assertArrayEquals(h1, h2);
        }
    }

    /**
     * Test {@link IncrementalHash32} returns the same values as
     * {@link MurmurHash3#hash32(byte[], int, int, int)}.
     */
    @Test
    public void testIncrementalHash32() {
        final byte[] bytes = new byte[1023];
        ThreadLocalRandom.current().nextBytes(bytes);
        // The seed does not matter
        for (final int seed : new int[] {-567, 0, 6787990}) {
            // Cases are constructed to hit all edge cases of processing:
            // Nothing added
            assertIncrementalHash32(bytes, seed, 0, 0);
            // Add single bytes
            assertIncrementalHash32(bytes, seed, 1, 1, 1, 1, 1, 1, 1, 1);
            // Leading unprocessed 1, 2, 3
            assertIncrementalHash32(bytes, seed, 1, 4);
            assertIncrementalHash32(bytes, seed, 2, 4);
            assertIncrementalHash32(bytes, seed, 3, 4);
            // Trailing unprocessed 1, 2, 3
            assertIncrementalHash32(bytes, seed, 4, 1);
            assertIncrementalHash32(bytes, seed, 4, 2);
            assertIncrementalHash32(bytes, seed, 4, 3);
            // Complete blocks
            assertIncrementalHash32(bytes, seed, 4, 16, 64);
        }
    }

    /**
     * Assert {@link IncrementalHash32} returns the same values as
     * {@link MurmurHash3#hash32(byte[], int, int, int)}.
     *
     * <p>The bytes are added to the incremental hash in the given blocks.</p>
     *
     * @param bytes the bytes
     * @param seed the seed
     * @param blocks the blocks
     */
    private static void assertIncrementalHash32(byte[] bytes, int seed, int... blocks) {
        int offset = 0;
        int total = 0;
        final IncrementalHash32 inc = new IncrementalHash32();
        inc.start(seed);
        for (final int block : blocks) {
            total += block;
            final int h1 = MurmurHash3.hash32(bytes, 0, total, seed);
            inc.add(bytes, offset, block);
            offset += block;
            final int h2 = inc.end();
            Assert.assertEquals("Hashes differ", h1, h2);
            Assert.assertEquals("Hashes differ after no additional data", h1, inc.end());
        }
    }
}
