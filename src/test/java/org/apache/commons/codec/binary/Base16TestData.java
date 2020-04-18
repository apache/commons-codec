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
 * This random data was encoded by OpenSSL. Java had nothing to do with it. This data helps us test interop between
 * Commons-Codec and OpenSSL.
 *
 * @since 1.15
 */
public class Base16TestData {

    final static String ENCODED_UTF8_LOWERCASE
            = "f483cd2b052f74b888029e9cb73d764a2426386b2d5b262f48f79ebee7c386bcdac2ceb9be8ca42a36c88f7dd85936bdc40edcfc51f2a56725ad9850ee89df737244f77049e5f4f847dcc011d8db8f2d61bf8658367113e1625e5cc2c9ff9a7ea81a53b0fa5ea56f03355632d5cd36ff5c320be92003a0af45477d712aff96df3c00476c4d5e063029f5f84c2e02261d8afc6ece7f9c2ccf2ada37b0aa5239dad3fd27b0acf2fa86ef5b3af960042cabe6fd4a2fbf268e8be39d3147e343424b88b907bbaa7d3b0520bd0aa20cacc4bff02e828d1d4cf67360613208fe4656b95edd041d81c8881e7a5d7785544cf19151b5de29b87ef94ddf6c922783fb105ccdc8601c8c67bc6df47592d4b51c45d43b3ebc27fc89505b138c7aaf8a6494a802f896b5db1ead7c876c811afff866af8a7f8fcd24d20f6adf986ad5ac8633df7ce002d349a67c59ec856d9c750b10bf428a9ff76507ff2941467489367e2c4b4a1adee51b36f3a7a640780fd57b52df5ab629c226bc3ec2225c325fbd929db9d48531046038714c61d1e6b19373835a7c08f79124b6658e2b00923f4c635b020cc438f2830006e51f1f93d1fd6d58b5b6131abe6e270dce2f68da1213546764d6306e25159553ccf44725bc959359dea2816780d0cc47000f2238ce559e6aa9fd618ced40eae7dac121d3500a879325a02412d02e2cbe73a2039ae5ef8ccd58ef0793184253a569a4ed42b440a576b967855f11a9eef542b67e2d53f2324f142d8f99779bc69d1b9cef955be6e0c8474822421009a66ad4c2960b72ae88e4bd049d6deced00d892892afa04e1436e9735764c6082f8a0279a346a401a979499a08c7400a0735928e99afee2103acbdf0e7aa271874399fc8262e57cf478c0d47f2d88327ce557ecac51dd71b3c0a0d0a98bae5ac094877dd3ced4d0ce34e2bef92ed8d155d4828618ac157863b61b0bcc20fe7a9c8a6af7966ded472a7e42c80acdd4446df3519341793cbc8b7ec50495ea63b3541d5777c45752c9b62cb0037b9b54d40947185b6316bf0bf5f2daac699babef74bf76977ab571c7afdf921c68e892c7b226fbf1b157695529d68ebd85460c3d730517ba6360b041bf69d3cf25dfff77a86479e7ddcd0a2b8f3d672c0d6c144a13351325b414d5f517c6e7cc79d88a3a3213f8dfe2cfe5f5b05def225d456442ca287659cca902238812406c1fe7424e06a5bbcb110e8321e1920105d8077e8654c9c2ea45e4052d96784a5e334a6cede6f43140584429bf7e7dfff8adbd4a028eb089f7a783155f22c6b66f99635f302a5ecec990fbe60ba7daed7e19667751a246b1625bb7720f0e57eb86ffa6001d98a5a3c6add0ea6490d74d16e870b8696406aad928d523fe04a2618734ea017faffcfa896023a572515b5aaadc22d95dd64567f56ba977958ed380bb60";

    final static String ENCODED_UTF8_UPPERCASE
            = "F483CD2B052F74B888029E9CB73D764A2426386B2D5B262F48F79EBEE7C386BCDAC2CEB9BE8CA42A36C88F7DD85936BDC40EDCFC51F2A56725AD9850EE89DF737244F77049E5F4F847DCC011D8DB8F2D61BF8658367113E1625E5CC2C9FF9A7EA81A53B0FA5EA56F03355632D5CD36FF5C320BE92003A0AF45477D712AFF96DF3C00476C4D5E063029F5F84C2E02261D8AFC6ECE7F9C2CCF2ADA37B0AA5239DAD3FD27B0ACF2FA86EF5B3AF960042CABE6FD4A2FBF268E8BE39D3147E343424B88B907BBAA7D3B0520BD0AA20CACC4BFF02E828D1D4CF67360613208FE4656B95EDD041D81C8881E7A5D7785544CF19151B5DE29B87EF94DDF6C922783FB105CCDC8601C8C67BC6DF47592D4B51C45D43B3EBC27FC89505B138C7AAF8A6494A802F896B5DB1EAD7C876C811AFFF866AF8A7F8FCD24D20F6ADF986AD5AC8633DF7CE002D349A67C59EC856D9C750B10BF428A9FF76507FF2941467489367E2C4B4A1ADEE51B36F3A7A640780FD57B52DF5AB629C226BC3EC2225C325FBD929DB9D48531046038714C61D1E6B19373835A7C08F79124B6658E2B00923F4C635B020CC438F2830006E51F1F93D1FD6D58B5B6131ABE6E270DCE2F68DA1213546764D6306E25159553CCF44725BC959359DEA2816780D0CC47000F2238CE559E6AA9FD618CED40EAE7DAC121D3500A879325A02412D02E2CBE73A2039AE5EF8CCD58EF0793184253A569A4ED42B440A576B967855F11A9EEF542B67E2D53F2324F142D8F99779BC69D1B9CEF955BE6E0C8474822421009A66AD4C2960B72AE88E4BD049D6DECED00D892892AFA04E1436E9735764C6082F8A0279A346A401A979499A08C7400A0735928E99AFEE2103ACBDF0E7AA271874399FC8262E57CF478C0D47F2D88327CE557ECAC51DD71B3C0A0D0A98BAE5AC094877DD3CED4D0CE34E2BEF92ED8D155D4828618AC157863B61B0BCC20FE7A9C8A6AF7966DED472A7E42C80ACDD4446DF3519341793CBC8B7EC50495EA63B3541D5777C45752C9B62CB0037B9B54D40947185B6316BF0BF5F2DAAC699BABEF74BF76977AB571C7AFDF921C68E892C7B226FBF1B157695529D68EBD85460C3D730517BA6360B041BF69D3CF25DFFF77A86479E7DDCD0A2B8F3D672C0D6C144A13351325B414D5F517C6E7CC79D88A3A3213F8DFE2CFE5F5B05DEF225D456442CA287659CCA902238812406C1FE7424E06A5BBCB110E8321E1920105D8077E8654C9C2EA45E4052D96784A5E334A6CEDE6F43140584429BF7E7DFFF8ADBD4A028EB089F7A783155F22C6B66F99635F302A5ECEC990FBE60BA7DAED7E19667751A246B1625BB7720F0E57EB86FFA6001D98A5A3C6ADD0EA6490D74D16E870B8696406AAD928D523FE04A2618734EA017FAFFCFA896023A572515B5AAADC22D95DD64567F56BA977958ED380BB60";

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

    static byte[] streamToBytes(final InputStream is) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buf = new byte[4096];
        int read;
        while ((read = is.read(buf)) > -1) {
            os.write(buf, 0, read);
        }
        return os.toByteArray();
    }

    /**
     * Returns an encoded and decoded copy of the same random data.
     *
     * @param size amount of random data to generate and encode
     * @return two byte[] arrays:  [0] = decoded, [1] = encoded
     */
    static byte[][] randomData(final int size) {
        final Random r = new Random();
        final byte[] decoded = new byte[size];
        r.nextBytes(decoded);
        final char[] encodedChars = Hex.encodeHex(decoded);
        final byte[] encoded = new String(encodedChars).getBytes(Hex.DEFAULT_CHARSET);
        return new byte[][] {decoded, encoded};
    }
}
