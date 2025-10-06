/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

import static org.apache.commons.codec.binary.StringUtils.getBytesUtf8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.RandomAccessFileMode;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link DigestUtils}.
 */
class DigestUtilsTest {

    private static final int SHAKE128_256_BYTE_LEN = 256 / Byte.SIZE;
    private static final int SHAKE128_512_BYTE_LEN = 512 / Byte.SIZE;
    private static final String EMPTY_STRING = "";
    /**
     * Tests SHAKE128 sample of 0-bit message.
     *
     * See https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
     *
     * See https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHAKE128_Msg0.pdf
     */
    // @formatter:off
    // The formatting below is matches the PDF.
    private static final String SHAKE128_MSG_0 =
            "7F 9C 2B A4 E8 8F 82 7D 61 60 45 50 76 05 85 3E" +
            "D7 3B 80 93 F6 EF BC 88 EB 1A 6E AC FA 66 EF 26" +
            "3C B1 EE A9 88 00 4B 93 10 3C FB 0A EE FD 2A 68" +
            "6E 01 FA 4A 58 E8 A3 63 9C A8 A1 E3 F9 AE 57 E2" +
            "35 B8 CC 87 3C 23 DC 62 B8 D2 60 16 9A FA 2F 75" +
            "AB 91 6A 58 D9 74 91 88 35 D2 5E 6A 43 50 85 B2" +
            "BA DF D6 DF AA C3 59 A5 EF BB 7B CC 4B 59 D5 38" +
            "DF 9A 04 30 2E 10 C8 BC 1C BF 1A 0B 3A 51 20 EA" +
            "17 CD A7 CF AD 76 5F 56 23 47 4D 36 8C CC A8 AF" +
            "00 07 CD 9F 5E 4C 84 9F 16 7A 58 0B 14 AA BD EF" +
            "AE E7 EE F4 7C B0 FC A9 76 7B E1 FD A6 94 19 DF" +
            "B9 27 E9 DF 07 34 8B 19 66 91 AB AE B5 80 B3 2D" +
            "EF 58 53 8B 8D 23 F8 77 32 EA 63 B0 2B 4F A0 F4" +
            "87 33 60 E2 84 19 28 CD 60 DD 4C EE 8C C0 D4 C9" +
            "22 A9 61 88 D0 32 67 5C 8A C8 50 93 3C 7A FF 15" +
            "33 B9 4C 83 4A DB B6 9C 61 15 BA D4 69 2D 86 19" +
            "F9 0B 0C DF 8A 7B 9C 26 40 29 AC 18 5B 70 B8 3F" +
            "28 01 F2 F4 B3 F7 0C 59 3E A3 AE EB 61 3A 7F 1B" +
            "1D E3 3F D7 50 81 F5 92 30 5F 2E 45 26 ED C0 96" +
            "31 B1 09 58 F4 64 D8 89 F3 1B A0 10 25 0F DA 7F" +
            "13 68 EC 29 67 FC 84 EF 2A E9 AF F2 68 E0 B1 70" +
            "0A FF C6 82 0B 52 3A 3D 91 71 35 F2 DF F2 EE 06" +
            "BF E7 2B 31 24 72 1D 4A 26 C0 4E 53 A7 5E 30 E7" +
            "3A 7A 9C 4A 95 D9 1C 55 D4 95 E9 F5 1D D0 B5 E9" +
            "D8 3C 6D 5E 8C E8 03 AA 62 B8 D6 54 DB 53 D0 9B" +
            "8D CF F2 73 CD FE B5 73 FA D8 BC D4 55 78 BE C2" +
            "E7 70 D0 1E FD E8 6E 72 1A 3F 7C 6C CE 27 5D AB" +
            "E6 E2 14 3F 1A F1 8D A7 EF DD C4 C7 B7 0B 5E 34" +
            "5D B9 3C C9 36 BE A3 23 49 1C CB 38 A3 88 F5 46" +
            "A9 FF 00 DD 4E 13 00 B9 B2 15 3D 20 41 D2 05 B4" +
            "43 E4 1B 45 A6 53 F2 A5 C4 49 2C 1A DD 54 45 12" +
            "DD A2 52 98 33 46 2B 71 A4 1A 45 BE 97 29 0B 6F";
    // @formatter:on
    /**
     * Tests SHAKE256 sample of 0-bit message.
     *
     * See https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
     *
     * See https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHAKE256_Msg0.pdf
     */
    // @formatter:off
    // The formatting below is matches the PDF.
    private static final String SHAKE256_MSG_0 =
            "46 B9 DD 2B 0B A8 8D 13 23 3B 3F EB 74 3E EB 24" +
            "3F CD 52 EA 62 B8 1B 82 B5 0C 27 64 6E D5 76 2F" +
            "D7 5D C4 DD D8 C0 F2 00 CB 05 01 9D 67 B5 92 F6" +
            "FC 82 1C 49 47 9A B4 86 40 29 2E AC B3 B7 C4 BE" +
            "14 1E 96 61 6F B1 39 57 69 2C C7 ED D0 B4 5A E3" +
            "DC 07 22 3C 8E 92 93 7B EF 84 BC 0E AB 86 28 53" +
            "34 9E C7 55 46 F5 8F B7 C2 77 5C 38 46 2C 50 10" +
            "D8 46 C1 85 C1 51 11 E5 95 52 2A 6B CD 16 CF 86" +
            "F3 D1 22 10 9E 3B 1F DD 94 3B 6A EC 46 8A 2D 62" +
            "1A 7C 06 C6 A9 57 C6 2B 54 DA FC 3B E8 75 67 D6" +
            "77 23 13 95 F6 14 72 93 B6 8C EA B7 A9 E0 C5 8D" +
            "86 4E 8E FD E4 E1 B9 A4 6C BE 85 47 13 67 2F 5C" +
            "AA AE 31 4E D9 08 3D AB 4B 09 9F 8E 30 0F 01 B8" +
            "65 0F 1F 4B 1D 8F CF 3F 3C B5 3F B8 E9 EB 2E A2" +
            "03 BD C9 70 F5 0A E5 54 28 A9 1F 7F 53 AC 26 6B" +
            "28 41 9C 37 78 A1 5F D2 48 D3 39 ED E7 85 FB 7F" +
            "5A 1A AA 96 D3 13 EA CC 89 09 36 C1 73 CD CD 0F" +
            "AB 88 2C 45 75 5F EB 3A ED 96 D4 77 FF 96 39 0B" +
            "F9 A6 6D 13 68 B2 08 E2 1F 7C 10 D0 4A 3D BD 4E" +
            "36 06 33 E5 DB 4B 60 26 01 C1 4C EA 73 7D B3 DC" +
            "F7 22 63 2C C7 78 51 CB DD E2 AA F0 A3 3A 07 B3" +
            "73 44 5D F4 90 CC 8F C1 E4 16 0F F1 18 37 8F 11" +
            "F0 47 7D E0 55 A8 1A 9E DA 57 A4 A2 CF B0 C8 39" +
            "29 D3 10 91 2F 72 9E C6 CF A3 6C 6A C6 A7 58 37" +
            "14 30 45 D7 91 CC 85 EF F5 B2 19 32 F2 38 61 BC" +
            "F2 3A 52 B5 DA 67 EA F7 BA AE 0F 5F B1 36 9D B7" +
            "8F 3A C4 5F 8C 4A C5 67 1D 85 73 5C DD DB 09 D2" +
            "B1 E3 4A 1F C0 66 FF 4A 16 2C B2 63 D6 54 12 74" +
            "AE 2F CC 86 5F 61 8A BE 27 C1 24 CD 8B 07 4C CD" +
            "51 63 01 B9 18 75 82 4D 09 95 8F 34 1E F2 74 BD" +
            "AB 0B AE 31 63 39 89 43 04 E3 58 77 B0 C2 8A 9B" +
            "1F D1 66 C7 96 B9 CC 25 8A 06 4A 8F 57 E2 7F 2A";
    // @formatter:on
    /**
     * Tests SHAKE128 sample of 1600-bit message.
     *
     * See https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
     *
     * See https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHAKE128_Msg1600.pdf
     */
    // @formatter:off
    // The formatting below is matches the PDF.
    private static final String SHAKE128_MSG_1600 =
            "13 1A B8 D2 B5 94 94 6B 9C 81 33 3F 9B B6 E0 CE" +
            "75 C3 B9 31 04 FA 34 69 D3 91 74 57 38 5D A0 37" +
            "CF 23 2E F7 16 4A 6D 1E B4 48 C8 90 81 86 AD 85" +
            "2D 3F 85 A5 CF 28 DA 1A B6 FE 34 38 17 19 78 46" +
            "7F 1C 05 D5 8C 7E F3 8C 28 4C 41 F6 C2 22 1A 76" +
            "F1 2A B1 C0 40 82 66 02 50 80 22 94 FB 87 18 02" +
            "13 FD EF 5B 0E CB 7D F5 0C A1 F8 55 5B E1 4D 32" +
            "E1 0F 6E DC DE 89 2C 09 42 4B 29 F5 97 AF C2 70" +
            "C9 04 55 6B FC B4 7A 7D 40 77 8D 39 09 23 64 2B" +
            "3C BD 05 79 E6 09 08 D5 A0 00 C1 D0 8B 98 EF 93" +
            "3F 80 64 45 BF 87 F8 B0 09 BA 9E 94 F7 26 61 22" +
            "ED 7A C2 4E 5E 26 6C 42 A8 2F A1 BB EF B7 B8 DB" +
            "00 66 E1 6A 85 E0 49 3F 07 DF 48 09 AE C0 84 A5" +
            "93 74 8A C3 DD E5 A6 D7 AA E1 E8 B6 E5 35 2B 2D" +
            "71 EF BB 47 D4 CA EE D5 E6 D6 33 80 5D 2D 32 3E" +
            "6F D8 1B 46 84 B9 3A 26 77 D4 5E 74 21 C2 C6 AE" +
            "A2 59 B8 55 A6 98 FD 7D 13 47 7A 1F E5 3E 5A 4A" +
            "61 97 DB EC 5C E9 5F 50 5B 52 0B CD 95 70 C4 A8" +
            "26 5A 7E 01 F8 9C 0C 00 2C 59 BF EC 6C D4 A5 C1" +
            "09 25 89 53 EE 5E E7 0C D5 77 EE 21 7A F2 1F A7" +
            "01 78 F0 94 6C 9B F6 CA 87 51 79 34 79 F6 B5 37" +
            "73 7E 40 B6 ED 28 51 1D 8A 2D 7E 73 EB 75 F8 DA" +
            "AC 91 2F F9 06 E0 AB 95 5B 08 3B AC 45 A8 E5 E9" +
            "B7 44 C8 50 6F 37 E9 B4 E7 49 A1 84 B3 0F 43 EB" +
            "18 8D 85 5F 1B 70 D7 1F F3 E5 0C 53 7A C1 B0 F8" +
            "97 4F 0F E1 A6 AD 29 5B A4 2F 6A EC 74 D1 23 A7" +
            "AB ED DE 6E 2C 07 11 CA B3 6B E5 AC B1 A5 A1 1A" +
            "4B 1D B0 8B A6 98 2E FC CD 71 69 29 A7 74 1C FC" +
            "63 AA 44 35 E0 B6 9A 90 63 E8 80 79 5C 3D C5 EF" +
            "32 72 E1 1C 49 7A 91 AC F6 99 FE FE E2 06 22 7A" +
            "44 C9 FB 35 9F D5 6A C0 A9 A7 5A 74 3C FF 68 62" +
            "F1 7D 72 59 AB 07 52 16 C0 69 95 11 64 3B 64 39";
    // @formatter:on
    /**
     * Tests SHAKE256 sample of 1600-bit message.
     *
     * See https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
     *
     * See https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHAKE256_Msg1600.pdf
     */
    // @formatter:off
    // The formatting below is matches the PDF.
    private static final String SHAKE256_MSG_1600 =
            "CD 8A 92 0E D1 41 AA 04 07 A2 2D 59 28 86 52 E9" +
            "D9 F1 A7 EE 0C 1E 7C 1C A6 99 42 4D A8 4A 90 4D" +
            "2D 70 0C AA E7 39 6E CE 96 60 44 40 57 7D A4 F3" +
            "AA 22 AE B8 85 7F 96 1C 4C D8 E0 6F 0A E6 61 0B" +
            "10 48 A7 F6 4E 10 74 CD 62 9E 85 AD 75 66 04 8E" +
            "FC 4F B5 00 B4 86 A3 30 9A 8F 26 72 4C 0E D6 28" +
            "00 1A 10 99 42 24 68 DE 72 6F 10 61 D9 9E B9 E9" +
            "36 04 D5 AA 74 67 D4 B1 BD 64 84 58 2A 38 43 17" +
            "D7 F4 7D 75 0B 8F 54 99 51 2B B8 5A 22 6C 42 43" +
            "55 6E 69 6F 6B D0 72 C5 AA 2D 9B 69 73 02 44 B5" +
            "68 53 D1 69 70 AD 81 7E 21 3E 47 06 18 17 80 01" +
            "C9 FB 56 C5 4F EF A5 FE E6 7D 2D A5 24 BB 3B 0B" +
            "61 EF 0E 91 14 A9 2C DB B6 CC CB 98 61 5C FE 76" +
            "E3 51 0D D8 8D 1C C2 8F F9 92 87 51 2F 24 BF AF" +
            "A1 A7 68 77 B6 F3 71 98 E3 A6 41 C6 8A 7C 42 D4" +
            "5F A7 AC C1 0D AE 5F 3C EF B7 B7 35 F1 2D 4E 58" +
            "9F 7A 45 6E 78 C0 F5 E4 C4 47 1F FF A5 E4 FA 05" +
            "14 AE 97 4D 8C 26 48 51 3B 5D B4 94 CE A8 47 15" +
            "6D 27 7A D0 E1 41 C2 4C 78 39 06 4C D0 88 51 BC" +
            "2E 7C A1 09 FD 4E 25 1C 35 BB 0A 04 FB 05 B3 64" +
            "FF 8C 4D 8B 59 BC 30 3E 25 32 8C 09 A8 82 E9 52" +
            "51 8E 1A 8A E0 FF 26 5D 61 C4 65 89 69 73 D7 49" +
            "04 99 DC 63 9F B8 50 2B 39 45 67 91 B1 B6 EC 5B" +
            "CC 5D 9A C3 6A 6D F6 22 A0 70 D4 3F ED 78 1F 5F" +
            "14 9F 7B 62 67 5E 7D 1A 4D 6D EC 48 C1 C7 16 45" +
            "86 EA E0 6A 51 20 8C 0B 79 12 44 D3 07 72 65 05" +
            "C3 AD 4B 26 B6 82 23 77 25 7A A1 52 03 75 60 A7" +
            "39 71 4A 3C A7 9B D6 05 54 7C 9B 78 DD 1F 59 6F" +
            "2D 4F 17 91 BC 68 9A 0E 9B 79 9A 37 33 9C 04 27" +
            "57 33 74 01 43 EF 5D 2B 58 B9 6A 36 3D 4E 08 07" +
            "6A 1A 9D 78 46 43 6E 4D CA 57 28 B6 F7 60 EE F0" +
            "CA 92 BF 0B E5 61 5E 96 95 9D 76 71 97 A0 BE EB";
    // @formatter:on

    static Stream<Arguments> testShake128_256() {
        // @formatter:off
        return Stream.of(
                // empty
                Arguments.of("", 0, SHAKE128_MSG_0),
                Arguments.of(
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1", 1_600, SHAKE128_MSG_1600));
        // @formatter:on
    }

    static Stream<Arguments> testShake256_512() {
        // @formatter:off
        return Stream.of(
                // empty
                Arguments.of("", 0, SHAKE256_MSG_0),
                Arguments.of(
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1" +
                        "1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1 1 1 0 0 0 1 0 1", 1_600, SHAKE256_MSG_1600));
        // @formatter:on
    }

    private final byte[] testData = new byte[DigestUtils.BUFFER_SIZE * DigestUtils.BUFFER_SIZE];
    private Path testFile;
    private Path testRandomAccessFile;
    private RandomAccessFile testRandomAccessFileWrapper;

    private String clean(final String input) {
        return StringUtils.replaceChars(input, " ", "").toLowerCase(Locale.ROOT);
    }

    byte[] getTestData() {
        return testData;
    }

    Path getTestPath() {
        return testFile;
    }

    RandomAccessFile getTestRandomAccessFile() {
        return testRandomAccessFileWrapper;
    }

    private boolean isJava25Plus() {
        return SystemUtils.JAVA_SPECIFICATION_VERSION.equals("25") || SystemUtils.JAVA_SPECIFICATION_VERSION.startsWith("25.");
        // TODO Commons Lang
        // return SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_25);
    }

    private boolean isJava9Plus() {
        return SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9);
    }

    @BeforeEach
    void setUp() throws Exception {
        new Random().nextBytes(testData);
        testFile = Files.createTempFile(DigestUtilsTest.class.getName(), ".dat");
        try (OutputStream fos = Files.newOutputStream(testFile)) {
            fos.write(testData);
        }
        testRandomAccessFile = Files.createTempFile(DigestUtilsTest.class.getName(), ".dat");
        try (OutputStream fos = Files.newOutputStream(testRandomAccessFile)) {
            fos.write(testData);
        }
        testRandomAccessFileWrapper = RandomAccessFileMode.READ_WRITE.create(testRandomAccessFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (testRandomAccessFileWrapper != null) {
            testRandomAccessFileWrapper.close();
        }
        Files.deleteIfExists(testFile);
        Files.deleteIfExists(testRandomAccessFile);
    }

    @SuppressWarnings("deprecation")
    @Test
    void testDigestDeprecated() {
        assertNull(new DigestUtils().getMessageDigest());
    }

    @Test
    void testDigestFileAs() throws IOException {
        final String expected = "d41d8cd98f00b204e9800998ecf8427e";
        final String pathName = "src/test/resources/org/apache/commons/codec/empty.bin";
        final String algo = MessageDigestAlgorithms.MD5;
        final Path path = Paths.get(pathName);
        assertEquals(expected, new DigestUtils(algo).digestAsHex(path.toFile()));
        try (InputStream inputStream = Files.newInputStream(path)) {
            assertEquals(expected, new DigestUtils(algo).digestAsHex(inputStream));
        }
        final byte[] allBytes = Files.readAllBytes(path);
        assertEquals(expected, new DigestUtils(algo).digestAsHex(allBytes));
        assertEquals(expected, new DigestUtils(algo).digestAsHex(ByteBuffer.wrap(allBytes)));
    }

    @Test
    void testGetDigests() {
        assertEquals("MD2", DigestUtils.getMd2Digest().getAlgorithm());
        assertEquals("MD5", DigestUtils.getMd5Digest().getAlgorithm());
        assertEquals("SHA-1", DigestUtils.getSha1Digest().getAlgorithm());
        assertEquals("SHA-256", DigestUtils.getSha256Digest().getAlgorithm());
        assertEquals("SHA-384", DigestUtils.getSha384Digest().getAlgorithm());
        assertEquals("SHA-512/224", DigestUtils.getSha512_224Digest().getAlgorithm());
        assertEquals("SHA-512/256", DigestUtils.getSha512_256Digest().getAlgorithm());
        assertEquals("SHA-512", DigestUtils.getSha512Digest().getAlgorithm());
        assertEquals("SHA-1", DigestUtils.getShaDigest().getAlgorithm());
    }

    @Test
    void testGetDigestsJava25() {
        if (isJava25Plus()) {
            assertEquals("SHAKE128-256", DigestUtils.getShake128_256Digest().getAlgorithm());
            assertEquals("SHAKE256-512", DigestUtils.getShake256_512Digest().getAlgorithm());
        } else {
            assertThrows(IllegalArgumentException.class, DigestUtils::getShake128_256Digest);
            assertThrows(IllegalArgumentException.class, DigestUtils::getShake256_512Digest);
        }
    }

    @Test
    void testGetDigestsJava9() {
        if (isJava9Plus()) {
            assertEquals("SHA3-224", DigestUtils.getSha3_224Digest().getAlgorithm());
            assertEquals("SHA3-256", DigestUtils.getSha3_256Digest().getAlgorithm());
            assertEquals("SHA3-384", DigestUtils.getSha3_384Digest().getAlgorithm());
            assertEquals("SHA3-512", DigestUtils.getSha3_512Digest().getAlgorithm());
        } else {
            assertThrows(IllegalArgumentException.class, DigestUtils::getSha3_224Digest);
            assertThrows(IllegalArgumentException.class, DigestUtils::getSha3_256Digest);
            assertThrows(IllegalArgumentException.class, DigestUtils::getSha3_384Digest);
            assertThrows(IllegalArgumentException.class, DigestUtils::getSha3_512Digest);
        }
    }

    @Test
    void testGetMessageDigest() {
        final DigestUtils digestUtils = new DigestUtils(MessageDigestAlgorithms.MD5);
        assertNotNull(digestUtils.getMessageDigest());
        assertEquals(MessageDigestAlgorithms.MD5, digestUtils.getMessageDigest().getAlgorithm());
    }

    @Test
    void testInternalNoSuchAlgorithmException() {
        assertThrows(IllegalArgumentException.class, () -> DigestUtils.getDigest("Bogus Bogus"));
    }

    @Test
    void testIsAvailable() {
        assertTrue(DigestUtils.isAvailable(MessageDigestAlgorithms.MD5));
        assertFalse(DigestUtils.isAvailable("FOO"));
        assertFalse(DigestUtils.isAvailable(null));
    }

    @Test
    void testMd2Hex() throws IOException {
        // Examples from RFC 1319
        assertEquals("8350e5a3e24c153df2275c9f80692773", DigestUtils.md2Hex(EMPTY_STRING));
        assertEquals("32ec01ec4a6dac72c0ab96fb34c0b5d1", DigestUtils.md2Hex("a"));
        assertEquals("da853b0d3f88d99b30283a69e6ded6bb", DigestUtils.md2Hex("abc"));
        assertEquals("ab4f496bfb2a530b219ff33031fe06b0", DigestUtils.md2Hex("message digest"));
        assertEquals("4e8ddff3650292ab5a4108c3aa47940b", DigestUtils.md2Hex("abcdefghijklmnopqrstuvwxyz"));
        // @formatter:off
        assertEquals("da33def2a42df13975352846c30338cd", DigestUtils.md2Hex(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789"));
        assertEquals("d5976f79d83d3a0dc9806c3c66f3efd8",
                DigestUtils.md2Hex(
                        "1234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890"));
        // @formatter:on
        assertEquals(DigestUtils.md2Hex(testData), DigestUtils.md2Hex(new ByteArrayInputStream(testData)));
    }

    /**
     * An MD2 hash converted to hexadecimal should always be 32 characters.
     */
    @Test
    void testMd2HexLength() {
        String hashMe = "this is some string that is longer than 32 characters";
        String hash = DigestUtils.md2Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
        hashMe = "length < 32";
        hash = DigestUtils.md2Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
    }

    /**
     * An MD2 hash should always be a 16 element byte[].
     */
    @Test
    void testMd2Length() {
        String hashMe = "this is some string that is longer than 16 characters";
        byte[] hash = DigestUtils.md2(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
        hashMe = "length < 16";
        hash = DigestUtils.md2(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
    }

    @Test
    void testMd5Hex() throws IOException {
        // Examples from RFC 1321
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtils.md5Hex(EMPTY_STRING));
        assertEquals("0cc175b9c0f1b6a831c399e269772661", DigestUtils.md5Hex("a"));
        assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex("abc"));
        assertEquals("f96b697d7cb7938d525a2f31aaf161d0", DigestUtils.md5Hex("message digest"));
        assertEquals("c3fcd3d76192e4007dfb496cca67e13b", DigestUtils.md5Hex("abcdefghijklmnopqrstuvwxyz"));
        // @formatter:off
        assertEquals("d174ab98d277d9f5a5611c2c9f419d9f", DigestUtils.md5Hex(
              "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
              "abcdefghijklmnopqrstuvwxyz" +
              "0123456789"));

        assertEquals("57edf4a22be3c955ac49da2e2107b67a",
                DigestUtils.md5Hex(
                      "1234567890123456789012345678901234567890" +
                      "1234567890123456789012345678901234567890"));
        // @formatter:on
        assertEquals(DigestUtils.md5Hex(testData), DigestUtils.md5Hex(new ByteArrayInputStream(testData)));
    }

    /**
     * An MD5 hash converted to hexadecimal should always be 32 characters.
     */
    @Test
    void testMd5HexLengthForBytes() {
        String hashMe = "this is some string that is longer than 32 characters";
        String hash = DigestUtils.md5Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
        hashMe = "length < 32";
        hash = DigestUtils.md5Hex(getBytesUtf8(hashMe));
        assertEquals(32, hash.length());
    }

    /**
     * An MD5 hash should always be a 16 element byte[].
     */
    @Test
    void testMd5LengthForBytes() {
        String hashMe = "this is some string that is longer than 16 characters";
        byte[] hash = DigestUtils.md5(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
        hashMe = "length < 16";
        hash = DigestUtils.md5(getBytesUtf8(hashMe));
        assertEquals(16, hash.length);
    }

    @Test
    void testSha1Hex() throws IOException {
        // Examples from FIPS 180-1
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.sha1Hex("abc"));
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.sha1Hex(getBytesUtf8("abc")));
        assertEquals("84983e441c3bd26ebaae4aa1f95129e5e54670f1", DigestUtils.sha1Hex("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"));
        assertEquals(DigestUtils.sha1Hex(testData), DigestUtils.sha1Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    void testSha1UpdateWithByteArray() {
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";
        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());
        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, d1.getBytes());
        DigestUtils.updateDigest(messageDigest, d2.getBytes());
        final String actualResult = Hex.encodeHexString(messageDigest.digest());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSha1UpdateWithByteBuffer() {
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";
        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());
        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, ByteBuffer.wrap(d1.getBytes()));
        DigestUtils.updateDigest(messageDigest, ByteBuffer.wrap(d2.getBytes()));
        final String actualResult = Hex.encodeHexString(messageDigest.digest());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSha1UpdateWithString() {
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";
        MessageDigest messageDigest = DigestUtils.getSha1Digest();
        messageDigest.update(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(d1));
        messageDigest.update(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(d2));
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());
        messageDigest = DigestUtils.getSha1Digest();
        DigestUtils.updateDigest(messageDigest, d1);
        DigestUtils.updateDigest(messageDigest, d2);
        final String actualResult = Hex.encodeHexString(messageDigest.digest());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSha224_FileAsHex() throws IOException {
        final String expected = "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f";
        final String pathName = "src/test/resources/org/apache/commons/codec/empty.bin";
        final String algo = MessageDigestAlgorithms.SHA_224;
        final DigestUtils digestUtils = new DigestUtils(algo);
        final Path path = Paths.get(pathName);
        assertEquals(expected, digestUtils.digestAsHex(path.toFile()));
        try (InputStream inputStream = Files.newInputStream(path)) {
            assertEquals(expected, digestUtils.digestAsHex(inputStream));
        }
        final byte[] allBytes = Files.readAllBytes(path);
        assertEquals(expected, digestUtils.digestAsHex(allBytes));
        assertEquals(expected, digestUtils.digestAsHex(ByteBuffer.wrap(allBytes)));
    }

    @Test
    void testSha224_PathAsHex() throws IOException {
        assertEquals("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
                new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex(Paths.get("src/test/resources/org/apache/commons/codec/empty.bin")));
    }

    @Test
    void testSha224_StringAsHex() {
        assertEquals("d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f", new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex(EMPTY_STRING));
        assertEquals("730e109bd7a8a32b1cb9d9a09aa2325d2430587ddbc0c38bad911525",
                new DigestUtils(MessageDigestAlgorithms.SHA_224).digestAsHex("The quick brown fox jumps over the lazy dog"));
        // Examples from FIPS 180-4?
    }

    @Test
    void testSha256() throws IOException {
        // Examples from FIPS 180-2
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", DigestUtils.sha256Hex("abc"));
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", DigestUtils.sha256Hex(getBytesUtf8("abc")));
        assertEquals("248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
                DigestUtils.sha256Hex("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"));
        assertEquals(DigestUtils.sha256Hex(testData), DigestUtils.sha256Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    void testSha256HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha256Hex(testData), DigestUtils.sha256Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    void testSha3_224() {
        if (isJava9Plus()) {
            // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
            //
            // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-224_Msg0.pdf
            assertEquals("6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7", DigestUtils.sha3_224Hex(EMPTY_STRING));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_224Hex(EMPTY_STRING));
        }
    }

    @Test
    void testSha3_224HexInputStream() throws IOException {
        if (isJava9Plus()) {
            assertEquals(DigestUtils.sha3_224Hex(testData), DigestUtils.sha3_224Hex(new ByteArrayInputStream(testData)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_224Hex(new ByteArrayInputStream(testData)));
        }
    }

    @Test
    void testSha3_256() {
        if (isJava9Plus()) {
            // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
            //
            // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-256_Msg0.pdf
            assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", DigestUtils.sha3_256Hex(EMPTY_STRING));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_256Hex(EMPTY_STRING));
        }
    }

    @Test
    void testSha3_256HexInputStream() throws IOException {
        if (isJava9Plus()) {
            assertEquals(DigestUtils.sha3_256Hex(testData), DigestUtils.sha3_256Hex(new ByteArrayInputStream(testData)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_256Hex(new ByteArrayInputStream(testData)));
        }
    }

    @Test
    void testSha3_384() {
        if (isJava9Plus()) {
            // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
            //
            // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-384_Msg0.pdf
            assertEquals("0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
                    DigestUtils.sha3_384Hex(EMPTY_STRING));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_384Hex(EMPTY_STRING));
        }
    }

    @Test
    void testSha3_384HexInputStream() throws IOException {
        if (isJava9Plus()) {
            assertEquals(DigestUtils.sha3_384Hex(testData), DigestUtils.sha3_384Hex(new ByteArrayInputStream(testData)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_384Hex(new ByteArrayInputStream(testData)));
        }
    }

    @Test
    void testSha3_512() {
        if (isJava9Plus()) {
            // Examples from https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines/example-values
            //
            // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA3-512_Msg0.pdf
            assertEquals("a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
                    DigestUtils.sha3_512Hex(EMPTY_STRING));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_512Hex(EMPTY_STRING));
        }
    }

    @Test
    void testSha3_512HexInputStream() throws IOException {
        if (isJava9Plus()) {
            assertEquals(DigestUtils.sha3_512Hex(testData), DigestUtils.sha3_512Hex(new ByteArrayInputStream(testData)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.sha3_512Hex(new ByteArrayInputStream(testData)));
        }
    }

    @Test
    void testSha384() throws IOException {
        // Examples from FIPS 180-2
        assertEquals("cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7", DigestUtils.sha384Hex("abc"));
        assertEquals("cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7",
                DigestUtils.sha384Hex(getBytesUtf8("abc")));
        assertEquals("09330c33f71147e83d192fc782cd1b4753111b173b3b05d22fa08086e3b0f712fcc7c71a557e2db966c3e9fa91746039",
                DigestUtils.sha384Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
        assertEquals(DigestUtils.sha384Hex(testData), DigestUtils.sha384Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    void testSha384HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha384Hex(testData), DigestUtils.sha384Hex(new ByteArrayInputStream(testData)));
    }

    @Test
    void testSha512() {
        // Examples from FIPS 180-2
        assertEquals("ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
                DigestUtils.sha512Hex("abc"));
        assertEquals("ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
                DigestUtils.sha512Hex(getBytesUtf8("abc")));
        assertEquals("8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909",
                DigestUtils.sha512Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    void testSha512_224() throws Exception {
        // Examples from
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA512_224.pdf
        final String stringInput = "abc";
        final byte[] bytesInput = getBytesUtf8(stringInput);
        final String resultString = "4634270F707B6A54DAAE7530460842E20E37ED265CEEE9A43E8924AA".toLowerCase(Locale.ROOT);
        final byte[] resultBytes = Hex.decodeHex(resultString);
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(bytesInput));
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(new ByteArrayInputStream(bytesInput)));
        assertArrayEquals(resultBytes, DigestUtils.sha512_224(stringInput));
        //
        assertEquals(resultString, DigestUtils.sha512_224Hex(bytesInput));
        assertEquals(resultString, DigestUtils.sha512_224Hex(new ByteArrayInputStream(bytesInput)));
        assertEquals(resultString, DigestUtils.sha512_224Hex(stringInput));
        // Example 2
        assertEquals("23FEC5BB94D60B23308192640B0C453335D664734FE40E7268674AF9".toLowerCase(Locale.ROOT),
                DigestUtils.sha512_224Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    void testSha512_256() throws Exception {
        // Examples from
        // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA512_256.pdf
        final String stringInput = "abc";
        final byte[] bytesInput = getBytesUtf8(stringInput);
        final String resultString = "53048E2681941EF99B2E29B76B4C7DABE4C2D0C634FC6D46E0E2F13107E7AF23".toLowerCase(Locale.ROOT);
        final byte[] resultBytes = Hex.decodeHex(resultString);
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(bytesInput));
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(new ByteArrayInputStream(bytesInput)));
        assertArrayEquals(resultBytes, DigestUtils.sha512_256(stringInput));
        //
        assertEquals(resultString, DigestUtils.sha512_256Hex(bytesInput));
        assertEquals(resultString, DigestUtils.sha512_256Hex(new ByteArrayInputStream(bytesInput)));
        assertEquals(resultString, DigestUtils.sha512_256Hex(stringInput));
        // Example 2
        assertEquals("3928E184FB8690F840DA3988121D31BE65CB9D3EF83EE6146FEAC861E19B563A".toLowerCase(Locale.ROOT),
                DigestUtils.sha512_256Hex("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu"));
    }

    @Test
    void testSha512HexInputStream() throws IOException {
        assertEquals(DigestUtils.sha512Hex(testData), DigestUtils.sha512Hex(new ByteArrayInputStream(testData)));
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    void testShaHex() throws IOException {
        // Examples from FIPS 180-1
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex("abc"));
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", DigestUtils.shaHex(getBytesUtf8("abc")));
        assertEquals("84983e441c3bd26ebaae4aa1f95129e5e54670f1", DigestUtils.shaHex("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"));
        assertEquals(DigestUtils.shaHex(testData), DigestUtils.shaHex(new ByteArrayInputStream(testData)));
    }

    @ParameterizedTest
    @MethodSource
    void testShake128_256(final String binaryInputText, final int inputBitSize, final String hexOutputText) throws Exception {
        final String cleanBinary = clean(binaryInputText);
        final String cleanBinaryInput = StringUtils.reverse(cleanBinary);
        final byte[] bytesInput = Binary.toByteArray(cleanBinaryInput);
        // sanity check on the test fixture
        assertEquals(inputBitSize, bytesInput.length * Byte.SIZE);
        final String resultString = clean(hexOutputText).substring(0, SHAKE128_256_BYTE_LEN * 2);
        final byte[] resultBytes = Arrays.copyOf(Hex.decodeHex(resultString), SHAKE128_256_BYTE_LEN);
        if (isJava25Plus()) {
            // byte[]
            assertArrayEquals(resultBytes, DigestUtils.shake128_256(bytesInput));
            assertArrayEquals(resultBytes, DigestUtils.shake128_256(new ByteArrayInputStream(bytesInput)));
            // Hex string
            assertEquals(resultString, DigestUtils.shake128_256Hex(bytesInput));
            assertEquals(resultString, DigestUtils.shake128_256Hex(new ByteArrayInputStream(bytesInput)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256(bytesInput));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256(new ByteArrayInputStream(bytesInput)));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256Hex(bytesInput));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256Hex(new ByteArrayInputStream(bytesInput)));
        }
    }

    @Test
    void testShake128_256_String() throws Exception {
        if (isJava25Plus()) {
            // String
            assertArrayEquals(new byte[] { 88, -127, 9, 45, -40, 24, -65, 92, -8, -93, -35, -73, -109, -5, -53, -89, 64, -105, -43, -59, 38, -90, -45, 95, -105,
                    -72, 51, 81, -108, 15, 44, -56 }, DigestUtils.shake128_256("abc"));
            // Hex string
            assertEquals("5881092dd818bf5cf8a3ddb793fbcba74097d5c526a6d35f97b83351940f2cc8", DigestUtils.shake128_256Hex("abc"));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256("abc"));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake128_256Hex("abc"));
        }
    }

    @ParameterizedTest
    @MethodSource
    void testShake256_512(final String binaryInputText, final int inputBitSize, final String hexOutputText) throws Exception {
        final String cleanBinary = clean(binaryInputText);
        final String cleanBinaryInput = StringUtils.reverse(cleanBinary);
        final byte[] bytesInput = Binary.toByteArray(cleanBinaryInput);
        // sanity check on the test fixture
        assertEquals(inputBitSize, bytesInput.length * Byte.SIZE);
        final String resultString = clean(hexOutputText).substring(0, SHAKE128_512_BYTE_LEN * 2);
        final byte[] resultBytes = Arrays.copyOf(Hex.decodeHex(resultString), SHAKE128_512_BYTE_LEN);
        if (isJava25Plus()) {
            // byte[]
            assertArrayEquals(resultBytes, DigestUtils.shake256_512(bytesInput));
            assertArrayEquals(resultBytes, DigestUtils.shake256_512(new ByteArrayInputStream(bytesInput)));
            // Hex string
            assertEquals(resultString, DigestUtils.shake256_512Hex(bytesInput));
            assertEquals(resultString, DigestUtils.shake256_512Hex(new ByteArrayInputStream(bytesInput)));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake256_512(bytesInput));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake256_512(new ByteArrayInputStream(bytesInput)));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake256_512Hex(bytesInput));
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake256_512Hex(new ByteArrayInputStream(bytesInput)));
        }
    }

    @Test
    void testShake256_512_String() throws Exception {
        if (isJava25Plus()) {
            // String
            assertArrayEquals(new byte[] { 72, 51, 102, 96, 19, 96, -88, 119, 28, 104, 99, 8, 12, -60, 17, 77, -115, -76, 69, 48, -8, -15, -31, -18, 79, -108,
                    -22, 55, -25, -117, 87, 57, -43, -95, 91, -17, 24, 106, 83, -122, -57, 87, 68, -64, 82, 126, 31, -86, -97, -121, 38, -28, 98, -95, 42, 79,
                    -21, 6, -67, -120, 1, -25, 81, -28 }, DigestUtils.shake256_512("abc"));
            // Hex string
            assertEquals("483366601360a8771c6863080cc4114d8db44530f8f1e1ee4f94ea37e78b5739d5a15bef186a5386c75744c0527e1faa9f8726e462a12a4feb06bd8801e751e4",
                    DigestUtils.shake256_512Hex("abc"));
        } else {
            assertThrows(IllegalArgumentException.class, () -> DigestUtils.shake256_512Hex("abc"));
        }
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    void testShaUpdateWithByteArray() {
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";
        MessageDigest messageDigest = DigestUtils.getShaDigest();
        messageDigest.update(d1.getBytes());
        messageDigest.update(d2.getBytes());
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());
        messageDigest = DigestUtils.getShaDigest();
        DigestUtils.updateDigest(messageDigest, d1.getBytes());
        DigestUtils.updateDigest(messageDigest, d2.getBytes());
        final String actualResult = Hex.encodeHexString(messageDigest.digest());
        assertEquals(expectedResult, actualResult);
    }

    @SuppressWarnings("deprecation") // deliberate tests of deprecated code
    @Test
    void testShaUpdateWithString() {
        final String d1 = "C'est un homme qui rentre dans un café, et plouf";
        final String d2 = "C'est un homme, c'est qu'une tête, on lui offre un cadeau: 'oh... encore un chapeau!'";
        MessageDigest messageDigest = DigestUtils.getShaDigest();
        messageDigest.update(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(d1));
        messageDigest.update(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(d2));
        final String expectedResult = Hex.encodeHexString(messageDigest.digest());
        messageDigest = DigestUtils.getShaDigest();
        DigestUtils.updateDigest(messageDigest, d1);
        DigestUtils.updateDigest(messageDigest, d2);
        final String actualResult = Hex.encodeHexString(messageDigest.digest());
        assertEquals(expectedResult, actualResult);
    }
}
