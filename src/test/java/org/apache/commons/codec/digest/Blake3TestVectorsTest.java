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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the standard test vectors provided by the reference Blake3 implementation. Each test uses as input data the
 * cyclic byte sequence 0, 1, 2, ..., 249, 250, 0, 1, ..., up to a specified length. For each test, the hash of this
 * message up to a specific length is calculated. Then the same hash is calculated in keyed mode using the UTF-8
 * encoding of the string "whats the Elvish word for friend". Finally, the same hash is calculated in key derivation
 * mode using the KDF context string with the UTF-8 encoding of "BLAKE3 2019-12-27 16:29:52 test vectors context".
 * For each of these hashes, both the extended hash output and the truncated 32-byte hash outputs are validated against
 * these known answer tests (KATs).
 */
class Blake3TestVectorsTest {
    private static final byte[] KEY = "whats the Elvish word for friend".getBytes(StandardCharsets.UTF_8);
    private static final byte[] CTX =
            "BLAKE3 2019-12-27 16:29:52 test vectors context".getBytes(StandardCharsets.UTF_8);

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(
                        0,
                        "af1349b9f5f9a1a6a0404dea36dcc9499bcb25c9adc112b7cc9a93cae41f3262e00f03e7b69af26b7faaf09fcd333050338ddfe085b8cc869ca98b206c08243a26f5487789e8f660afe6c99ef9e0c52b92e7393024a80459cf91f476f9ffdbda7001c22e159b402631f277ca96f2defdf1078282314e763699a31c5363165421cce14d",
                        "92b2b75604ed3c761f9d6f62392c8a9227ad0ea3f09573e783f1498a4ed60d26b18171a2f22a4b94822c701f107153dba24918c4bae4d2945c20ece13387627d3b73cbf97b797d5e59948c7ef788f54372df45e45e4293c7dc18c1d41144a9758be58960856be1eabbe22c2653190de560ca3b2ac4aa692a9210694254c371e851bc8f",
                        "2cc39783c223154fea8dfb7c1b1660f2ac2dcbd1c1de8277b0b0dd39b7e50d7d905630c8be290dfcf3e6842f13bddd573c098c3f17361f1f206b8cad9d088aa4a3f746752c6b0ce6a83b0da81d59649257cdf8eb3e9f7d4998e41021fac119deefb896224ac99f860011f73609e6e0e4540f93b273e56547dfd3aa1a035ba6689d89a0"
                ),
                Arguments.of(
                        1,
                        "2d3adedff11b61f14c886e35afa036736dcd87a74d27b5c1510225d0f592e213c3a6cb8bf623e20cdb535f8d1a5ffb86342d9c0b64aca3bce1d31f60adfa137b358ad4d79f97b47c3d5e79f179df87a3b9776ef8325f8329886ba42f07fb138bb502f4081cbcec3195c5871e6c23e2cc97d3c69a613eba131e5f1351f3f1da786545e5",
                        "6d7878dfff2f485635d39013278ae14f1454b8c0a3a2d34bc1ab38228a80c95b6568c0490609413006fbd428eb3fd14e7756d90f73a4725fad147f7bf70fd61c4e0cf7074885e92b0e3f125978b4154986d4fb202a3f331a3fb6cf349a3a70e49990f98fe4289761c8602c4e6ab1138d31d3b62218078b2f3ba9a88e1d08d0dd4cea11",
                        "b3e2e340a117a499c6cf2398a19ee0d29cca2bb7404c73063382693bf66cb06c5827b91bf889b6b97c5477f535361caefca0b5d8c4746441c57617111933158950670f9aa8a05d791daae10ac683cbef8faf897c84e6114a59d2173c3f417023a35d6983f2c7dfa57e7fc559ad751dbfb9ffab39c2ef8c4aafebc9ae973a64f0c76551"
                ),
                Arguments.of(
                        2,
                        "7b7015bb92cf0b318037702a6cdd81dee41224f734684c2c122cd6359cb1ee63d8386b22e2ddc05836b7c1bb693d92af006deb5ffbc4c70fb44d0195d0c6f252faac61659ef86523aa16517f87cb5f1340e723756ab65efb2f91964e14391de2a432263a6faf1d146937b35a33621c12d00be8223a7f1919cec0acd12097ff3ab00ab1",
                        "5392ddae0e0a69d5f40160462cbd9bd889375082ff224ac9c758802b7a6fd20a9ffbf7efd13e989a6c246f96d3a96b9d279f2c4e63fb0bdff633957acf50ee1a5f658be144bab0f6f16500dee4aa5967fc2c586d85a04caddec90fffb7633f46a60786024353b9e5cebe277fcd9514217fee2267dcda8f7b31697b7c54fab6a939bf8f",
                        "1f166565a7df0098ee65922d7fea425fb18b9943f19d6161e2d17939356168e6daa59cae19892b2d54f6fc9f475d26031fd1c22ae0a3e8ef7bdb23f452a15e0027629d2e867b1bb1e6ab21c71297377750826c404dfccc2406bd57a83775f89e0b075e59a7732326715ef912078e213944f490ad68037557518b79c0086de6d6f6cdd2"
                ),
                Arguments.of(
                        3,
                        "e1be4d7a8ab5560aa4199eea339849ba8e293d55ca0a81006726d184519e647f5b49b82f805a538c68915c1ae8035c900fd1d4b13902920fd05e1450822f36de9454b7e9996de4900c8e723512883f93f4345f8a58bfe64ee38d3ad71ab027765d25cdd0e448328a8e7a683b9a6af8b0af94fa09010d9186890b096a08471e4230a134",
                        "39e67b76b5a007d4921969779fe666da67b5213b096084ab674742f0d5ec62b9b9142d0fab08e1b161efdbb28d18afc64d8f72160c958e53a950cdecf91c1a1bbab1a9c0f01def762a77e2e8545d4dec241e98a89b6db2e9a5b070fc110caae2622690bd7b76c02ab60750a3ea75426a6bb8803c370ffe465f07fb57def95df772c39f",
                        "440aba35cb006b61fc17c0529255de438efc06a8c9ebf3f2ddac3b5a86705797f27e2e914574f4d87ec04c379e12789eccbfbc15892626042707802dbe4e97c3ff59dca80c1e54246b6d055154f7348a39b7d098b2b4824ebe90e104e763b2a447512132cede16243484a55a4e40a85790038bb0dcf762e8c053cabae41bbe22a5bff7"
                ),
                Arguments.of(
                        4,
                        "f30f5ab28fe047904037f77b6da4fea1e27241c5d132638d8bedce9d40494f328f603ba4564453e06cdcee6cbe728a4519bbe6f0d41e8a14b5b225174a566dbfa61b56afb1e452dc08c804f8c3143c9e2cc4a31bb738bf8c1917b55830c6e65797211701dc0b98daa1faeaa6ee9e56ab606ce03a1a881e8f14e87a4acf4646272cfd12",
                        "7671dde590c95d5ac9616651ff5aa0a27bee5913a348e053b8aa9108917fe070116c0acff3f0d1fa97ab38d813fd46506089118147d83393019b068a55d646251ecf81105f798d76a10ae413f3d925787d6216a7eb444e510fd56916f1d753a5544ecf0072134a146b2615b42f50c179f56b8fae0788008e3e27c67482349e249cb86a",
                        "f46085c8190d69022369ce1a18880e9b369c135eb93f3c63550d3e7630e91060fbd7d8f4258bec9da4e05044f88b91944f7cab317a2f0c18279629a3867fad0662c9ad4d42c6f27e5b124da17c8c4f3a94a025ba5d1b623686c6099d202a7317a82e3d95dae46a87de0555d727a5df55de44dab799a20dffe239594d6e99ed17950910"
                ),
                Arguments.of(
                        5,
                        "b40b44dfd97e7a84a996a91af8b85188c66c126940ba7aad2e7ae6b385402aa2ebcfdac6c5d32c31209e1f81a454751280db64942ce395104e1e4eaca62607de1c2ca748251754ea5bbe8c20150e7f47efd57012c63b3c6a6632dc1c7cd15f3e1c999904037d60fac2eb9397f2adbe458d7f264e64f1e73aa927b30988e2aed2f03620",
                        "73ac69eecf286894d8102018a6fc729f4b1f4247d3703f69bdc6a5fe3e0c84616ab199d1f2f3e53bffb17f0a2209fe8b4f7d4c7bae59c2bc7d01f1ff94c67588cc6b38fa6024886f2c078bfe09b5d9e6584cd6c521c3bb52f4de7687b37117a2dbbec0d59e92fa9a8cc3240d4432f91757aabcae03e87431dac003e7d73574bfdd8218",
                        "1f24eda69dbcb752847ec3ebb5dd42836d86e58500c7c98d906ecd82ed9ae47f6f48a3f67e4e43329c9a89b1ca526b9b35cbf7d25c1e353baffb590fd79be58ddb6c711f1a6b60e98620b851c688670412fcb0435657ba6b638d21f0f2a04f2f6b0bd8834837b10e438d5f4c7c2c71299cf7586ea9144ed09253d51f8f54dd6bff719d"
                ),
                Arguments.of(
                        6,
                        "06c4e8ffb6872fad96f9aaca5eee1553eb62aed0ad7198cef42e87f6a616c844611a30c4e4f37fe2fe23c0883cde5cf7059d88b657c7ed2087e3d210925ede716435d6d5d82597a1e52b9553919e804f5656278bd739880692c94bff2824d8e0b48cac1d24682699e4883389dc4f2faa2eb3b4db6e39debd5061ff3609916f3e07529a",
                        "82d3199d0013035682cc7f2a399d4c212544376a839aa863a0f4c91220ca7a6dc2ffb3aa05f2631f0fa9ac19b6e97eb7e6669e5ec254799350c8b8d189e8807800842a5383c4d907c932f34490aaf00064de8cdb157357bde37c1504d2960034930887603abc5ccb9f5247f79224baff6120a3c622a46d7b1bcaee02c5025460941256",
                        "be96b30b37919fe4379dfbe752ae77b4f7e2ab92f7ff27435f76f2f065f6a5f435ae01a1d14bd5a6b3b69d8cbd35f0b01ef2173ff6f9b640ca0bd4748efa398bf9a9c0acd6a66d9332fdc9b47ffe28ba7ab6090c26747b85f4fab22f936b71eb3f64613d8bd9dfabe9bb68da19de78321b481e5297df9e40ec8a3d662f3e1479c65de0"
                ),
                Arguments.of(
                        7,
                        "3f8770f387faad08faa9d8414e9f449ac68e6ff0417f673f602a646a891419fe66036ef6e6d1a8f54baa9fed1fc11c77cfb9cff65bae915045027046ebe0c01bf5a941f3bb0f73791d3fc0b84370f9f30af0cd5b0fc334dd61f70feb60dad785f070fef1f343ed933b49a5ca0d16a503f599a365a4296739248b28d1a20b0e2cc8975c",
                        "af0a7ec382aedc0cfd626e49e7628bc7a353a4cb108855541a5651bf64fbb28a7c5035ba0f48a9c73dabb2be0533d02e8fd5d0d5639a18b2803ba6bf527e1d145d5fd6406c437b79bcaad6c7bdf1cf4bd56a893c3eb9510335a7a798548c6753f74617bede88bef924ba4b334f8852476d90b26c5dc4c3668a2519266a562c6c8034a6",
                        "dc3b6485f9d94935329442916b0d059685ba815a1fa2a14107217453a7fc9f0e66266db2ea7c96843f9d8208e600a73f7f45b2f55b9e6d6a7ccf05daae63a3fdd10b25ac0bd2e224ce8291f88c05976d575df998477db86fb2cfbbf91725d62cb57acfeb3c2d973b89b503c2b60dde85a7802b69dc1ac2007d5623cbea8cbfb6b181f5"
                ),
                Arguments.of(
                        8,
                        "2351207d04fc16ade43ccab08600939c7c1fa70a5c0aaca76063d04c3228eaeb725d6d46ceed8f785ab9f2f9b06acfe398c6699c6129da084cb531177445a682894f9685eaf836999221d17c9a64a3a057000524cd2823986db378b074290a1a9b93a22e135ed2c14c7e20c6d045cd00b903400374126676ea78874d79f2dd7883cf5c",
                        "be2f5495c61cba1bb348a34948c004045e3bd4dae8f0fe82bf44d0da245a060048eb5e68ce6dea1eb0229e144f578b3aa7e9f4f85febd135df8525e6fe40c6f0340d13dd09b255ccd5112a94238f2be3c0b5b7ecde06580426a93e0708555a265305abf86d874e34b4995b788e37a823491f25127a502fe0704baa6bfdf04e76c13276",
                        "2b166978cef14d9d438046c720519d8b1cad707e199746f1562d0c87fbd32940f0e2545a96693a66654225ebbaac76d093bfa9cd8f525a53acb92a861a98c42e7d1c4ae82e68ab691d510012edd2a728f98cd4794ef757e94d6546961b4f280a51aac339cc95b64a92b83cc3f26d8af8dfb4c091c240acdb4d47728d23e7148720ef04"
                ),
                Arguments.of(
                        63,
                        "e9bc37a594daad83be9470df7f7b3798297c3d834ce80ba85d6e207627b7db7b1197012b1e7d9af4d7cb7bdd1f3bb49a90a9b5dec3ea2bbc6eaebce77f4e470cbf4687093b5352f04e4a4570fba233164e6acc36900e35d185886a827f7ea9bdc1e5c3ce88b095a200e62c10c043b3e9bc6cb9b6ac4dfa51794b02ace9f98779040755",
                        "bb1eb5d4afa793c1ebdd9fb08def6c36d10096986ae0cfe148cd101170ce37aea05a63d74a840aecd514f654f080e51ac50fd617d22610d91780fe6b07a26b0847abb38291058c97474ef6ddd190d30fc318185c09ca1589d2024f0a6f16d45f11678377483fa5c005b2a107cb9943e5da634e7046855eaa888663de55d6471371d55d",
                        "b6451e30b953c206e34644c6803724e9d2725e0893039cfc49584f991f451af3b89e8ff572d3da4f4022199b9563b9d70ebb616efff0763e9abec71b550f1371e233319c4c4e74da936ba8e5bbb29a598e007a0bbfa929c99738ca2cc098d59134d11ff300c39f82e2fce9f7f0fa266459503f64ab9913befc65fddc474f6dc1c67669"
                ),
                Arguments.of(
                        64,
                        "4eed7141ea4a5cd4b788606bd23f46e212af9cacebacdc7d1f4c6dc7f2511b98fc9cc56cb831ffe33ea8e7e1d1df09b26efd2767670066aa82d023b1dfe8ab1b2b7fbb5b97592d46ffe3e05a6a9b592e2949c74160e4674301bc3f97e04903f8c6cf95b863174c33228924cdef7ae47559b10b294acd660666c4538833582b43f82d74",
                        "ba8ced36f327700d213f120b1a207a3b8c04330528586f414d09f2f7d9ccb7e68244c26010afc3f762615bbac552a1ca909e67c83e2fd5478cf46b9e811efccc93f77a21b17a152ebaca1695733fdb086e23cd0eb48c41c034d52523fc21236e5d8c9255306e48d52ba40b4dac24256460d56573d1312319afcf3ed39d72d0bfc69acb",
                        "a5c4a7053fa86b64746d4bb688d06ad1f02a18fce9afd3e818fefaa7126bf73e9b9493a9befebe0bf0c9509fb3105cfa0e262cde141aa8e3f2c2f77890bb64a4cca96922a21ead111f6338ad5244f2c15c44cb595443ac2ac294231e31be4a4307d0a91e874d36fc9852aeb1265c09b6e0cda7c37ef686fbbcab97e8ff66718be048bb"
                ),
                Arguments.of(
                        65,
                        "de1e5fa0be70df6d2be8fffd0e99ceaa8eb6e8c93a63f2d8d1c30ecb6b263dee0e16e0a4749d6811dd1d6d1265c29729b1b75a9ac346cf93f0e1d7296dfcfd4313b3a227faaaaf7757cc95b4e87a49be3b8a270a12020233509b1c3632b3485eef309d0abc4a4a696c9decc6e90454b53b000f456a3f10079072baaf7a981653221f2c",
                        "c0a4edefa2d2accb9277c371ac12fcdbb52988a86edc54f0716e1591b4326e72d5e795f46a596b02d3d4bfb43abad1e5d19211152722ec1f20fef2cd413e3c22f2fc5da3d73041275be6ede3517b3b9f0fc67ade5956a672b8b75d96cb43294b9041497de92637ed3f2439225e683910cb3ae923374449ca788fb0f9bea92731bc26ad",
                        "51fd05c3c1cfbc8ed67d139ad76f5cf8236cd2acd26627a30c104dfd9d3ff8a82b02e8bd36d8498a75ad8c8e9b15eb386970283d6dd42c8ae7911cc592887fdbe26a0a5f0bf821cd92986c60b2502c9be3f98a9c133a7e8045ea867e0828c7252e739321f7c2d65daee4468eb4429efae469a42763f1f94977435d10dccae3e3dce88d"
                ),
                Arguments.of(
                        127,
                        "d81293fda863f008c09e92fc382a81f5a0b4a1251cba1634016a0f86a6bd640de3137d477156d1fde56b0cf36f8ef18b44b2d79897bece12227539ac9ae0a5119da47644d934d26e74dc316145dcb8bb69ac3f2e05c242dd6ee06484fcb0e956dc44355b452c5e2bbb5e2b66e99f5dd443d0cbcaaafd4beebaed24ae2f8bb672bcef78",
                        "c64200ae7dfaf35577ac5a9521c47863fb71514a3bcad18819218b818de85818ee7a317aaccc1458f78d6f65f3427ec97d9c0adb0d6dacd4471374b621b7b5f35cd54663c64dbe0b9e2d95632f84c611313ea5bd90b71ce97b3cf645776f3adc11e27d135cbadb9875c2bf8d3ae6b02f8a0206aba0c35bfe42574011931c9a255ce6dc",
                        "c91c090ceee3a3ac81902da31838012625bbcd73fcb92e7d7e56f78deba4f0c3feeb3974306966ccb3e3c69c337ef8a45660ad02526306fd685c88542ad00f759af6dd1adc2e50c2b8aac9f0c5221ff481565cf6455b772515a69463223202e5c371743e35210bbbbabd89651684107fd9fe493c937be16e39cfa7084a36207c99bea3"
                ),
                Arguments.of(
                        128,
                        "f17e570564b26578c33bb7f44643f539624b05df1a76c81f30acd548c44b45efa69faba091427f9c5c4caa873aa07828651f19c55bad85c47d1368b11c6fd99e47ecba5820a0325984d74fe3e4058494ca12e3f1d3293d0010a9722f7dee64f71246f75e9361f44cc8e214a100650db1313ff76a9f93ec6e84edb7add1cb4a95019b0c",
                        "b04fe15577457267ff3b6f3c947d93be581e7e3a4b018679125eaf86f6a628ecd86bbe0001f10bda47e6077b735016fca8119da11348d93ca302bbd125bde0db2b50edbe728a620bb9d3e6f706286aedea973425c0b9eedf8a38873544cf91badf49ad92a635a93f71ddfcee1eae536c25d1b270956be16588ef1cfef2f1d15f650bd5",
                        "81720f34452f58a0120a58b6b4608384b5c51d11f39ce97161a0c0e442ca022550e7cd651e312f0b4c6afb3c348ae5dd17d2b29fab3b894d9a0034c7b04fd9190cbd90043ff65d1657bbc05bfdecf2897dd894c7a1b54656d59a50b51190a9da44db426266ad6ce7c173a8c0bbe091b75e734b4dadb59b2861cd2518b4e7591e4b83c9"
                ),
                Arguments.of(
                        129,
                        "683aaae9f3c5ba37eaaf072aed0f9e30bac0865137bae68b1fde4ca2aebdcb12f96ffa7b36dd78ba321be7e842d364a62a42e3746681c8bace18a4a8a79649285c7127bf8febf125be9de39586d251f0d41da20980b70d35e3dac0eee59e468a894fa7e6a07129aaad09855f6ad4801512a116ba2b7841e6cfc99ad77594a8f2d181a7",
                        "d4a64dae6cdccbac1e5287f54f17c5f985105457c1a2ec1878ebd4b57e20d38f1c9db018541eec241b748f87725665b7b1ace3e0065b29c3bcb232c90e37897fa5aaee7e1e8a2ecfcd9b51463e42238cfdd7fee1aecb3267fa7f2128079176132a412cd8aaf0791276f6b98ff67359bd8652ef3a203976d5ff1cd41885573487bcd683",
                        "938d2d4435be30eafdbb2b7031f7857c98b04881227391dc40db3c7b21f41fc18d72d0f9c1de5760e1941aebf3100b51d64644cb459eb5d20258e233892805eb98b07570ef2a1787cd48e117c8d6a63a68fd8fc8e59e79dbe63129e88352865721c8d5f0cf183f85e0609860472b0d6087cefdd186d984b21542c1c780684ed6832d8d"
                ),
                Arguments.of(
                        1023,
                        "10108970eeda3eb932baac1428c7a2163b0e924c9a9e25b35bba72b28f70bd11a182d27a591b05592b15607500e1e8dd56bc6c7fc063715b7a1d737df5bad3339c56778957d870eb9717b57ea3d9fb68d1b55127bba6a906a4a24bbd5acb2d123a37b28f9e9a81bbaae360d58f85e5fc9d75f7c370a0cc09b6522d9c8d822f2f28f485",
                        "c951ecdf03288d0fcc96ee3413563d8a6d3589547f2c2fb36d9786470f1b9d6e890316d2e6d8b8c25b0a5b2180f94fb1a158ef508c3cde45e2966bd796a696d3e13efd86259d756387d9becf5c8bf1ce2192b87025152907b6d8cc33d17826d8b7b9bc97e38c3c85108ef09f013e01c229c20a83d9e8efac5b37470da28575fd755a10",
                        "74a16c1c3d44368a86e1ca6df64be6a2f64cce8f09220787450722d85725dea59c413264404661e9e4d955409dfe4ad3aa487871bcd454ed12abfe2c2b1eb7757588cf6cb18d2eccad49e018c0d0fec323bec82bf1644c6325717d13ea712e6840d3e6e730d35553f59eff5377a9c350bcc1556694b924b858f329c44ee64b884ef00d"
                ),
                Arguments.of(
                        1024,
                        "42214739f095a406f3fc83deb889744ac00df831c10daa55189b5d121c855af71cf8107265ecdaf8505b95d8fcec83a98a6a96ea5109d2c179c47a387ffbb404756f6eeae7883b446b70ebb144527c2075ab8ab204c0086bb22b7c93d465efc57f8d917f0b385c6df265e77003b85102967486ed57db5c5ca170ba441427ed9afa684e",
                        "75c46f6f3d9eb4f55ecaaee480db732e6c2105546f1e675003687c31719c7ba4a78bc838c72852d4f49c864acb7adafe2478e824afe51c8919d06168414c265f298a8094b1ad813a9b8614acabac321f24ce61c5a5346eb519520d38ecc43e89b5000236df0597243e4d2493fd626730e2ba17ac4d8824d09d1a4a8f57b8227778e2de",
                        "7356cd7720d5b66b6d0697eb3177d9f8d73a4a5c5e968896eb6a6896843027066c23b601d3ddfb391e90d5c8eccdef4ae2a264bce9e612ba15e2bc9d654af1481b2e75dbabe615974f1070bba84d56853265a34330b4766f8e75edd1f4a1650476c10802f22b64bd3919d246ba20a17558bc51c199efdec67e80a227251808d8ce5bad"
                ),
                Arguments.of(
                        1025,
                        "d00278ae47eb27b34faecf67b4fe263f82d5412916c1ffd97c8cb7fb814b8444f4c4a22b4b399155358a994e52bf255de60035742ec71bd08ac275a1b51cc6bfe332b0ef84b409108cda080e6269ed4b3e2c3f7d722aa4cdc98d16deb554e5627be8f955c98e1d5f9565a9194cad0c4285f93700062d9595adb992ae68ff12800ab67a",
                        "357dc55de0c7e382c900fd6e320acc04146be01db6a8ce7210b7189bd664ea69362396b77fdc0d2634a552970843722066c3c15902ae5097e00ff53f1e116f1cd5352720113a837ab2452cafbde4d54085d9cf5d21ca613071551b25d52e69d6c81123872b6f19cd3bc1333edf0c52b94de23ba772cf82636cff4542540a7738d5b930",
                        "effaa245f065fbf82ac186839a249707c3bddf6d3fdda22d1b95a3c970379bcb5d31013a167509e9066273ab6e2123bc835b408b067d88f96addb550d96b6852dad38e320b9d940f86db74d398c770f462118b35d2724efa13da97194491d96dd37c3c09cbef665953f2ee85ec83d88b88d11547a6f911c8217cca46defa2751e7f3ad"
                ),
                Arguments.of(
                        2048,
                        "e776b6028c7cd22a4d0ba182a8bf62205d2ef576467e838ed6f2529b85fba24a9a60bf80001410ec9eea6698cd537939fad4749edd484cb541aced55cd9bf54764d063f23f6f1e32e12958ba5cfeb1bf618ad094266d4fc3c968c2088f677454c288c67ba0dba337b9d91c7e1ba586dc9a5bc2d5e90c14f53a8863ac75655461cea8f9",
                        "879cf1fa2ea0e79126cb1063617a05b6ad9d0b696d0d757cf053439f60a99dd10173b961cd574288194b23ece278c330fbb8585485e74967f31352a8183aa782b2b22f26cdcadb61eed1a5bc144b8198fbb0c13abbf8e3192c145d0a5c21633b0ef86054f42809df823389ee40811a5910dcbd1018af31c3b43aa55201ed4edaac74fe",
                        "7b2945cb4fef70885cc5d78a87bf6f6207dd901ff239201351ffac04e1088a23e2c11a1ebffcea4d80447867b61badb1383d842d4e79645d48dd82ccba290769caa7af8eaa1bd78a2a5e6e94fbdab78d9c7b74e894879f6a515257ccf6f95056f4e25390f24f6b35ffbb74b766202569b1d797f2d4bd9d17524c720107f985f4ddc583"
                ),
                Arguments.of(
                        2049,
                        "5f4d72f40d7a5f82b15ca2b2e44b1de3c2ef86c426c95c1af0b687952256303096de31d71d74103403822a2e0bc1eb193e7aecc9643a76b7bbc0c9f9c52e8783aae98764ca468962b5c2ec92f0c74eb5448d519713e09413719431c802f948dd5d90425a4ecdadece9eb178d80f26efccae630734dff63340285adec2aed3b51073ad3",
                        "9f29700902f7c86e514ddc4df1e3049f258b2472b6dd5267f61bf13983b78dd5f9a88abfefdfa1e00b418971f2b39c64ca621e8eb37fceac57fd0c8fc8e117d43b81447be22d5d8186f8f5919ba6bcc6846bd7d50726c06d245672c2ad4f61702c646499ee1173daa061ffe15bf45a631e2946d616a4c345822f1151284712f76b2b0e",
                        "2ea477c5515cc3dd606512ee72bb3e0e758cfae7232826f35fb98ca1bcbdf27316d8e9e79081a80b046b60f6a263616f33ca464bd78d79fa18200d06c7fc9bffd808cc4755277a7d5e09da0f29ed150f6537ea9bed946227ff184cc66a72a5f8c1e4bd8b04e81cf40fe6dc4427ad5678311a61f4ffc39d195589bdbc670f63ae70f4b6"
                ),
                Arguments.of(
                        3072,
                        "b98cb0ff3623be03326b373de6b9095218513e64f1ee2edd2525c7ad1e5cffd29a3f6b0b978d6608335c09dc94ccf682f9951cdfc501bfe47b9c9189a6fc7b404d120258506341a6d802857322fbd20d3e5dae05b95c88793fa83db1cb08e7d8008d1599b6209d78336e24839724c191b2a52a80448306e0daa84a3fdb566661a37e11",
                        "044a0e7b172a312dc02a4c9a818c036ffa2776368d7f528268d2e6b5df19177022f302d0529e4174cc507c463671217975e81dab02b8fdeb0d7ccc7568dd22574c783a76be215441b32e91b9a904be8ea81f7a0afd14bad8ee7c8efc305ace5d3dd61b996febe8da4f56ca0919359a7533216e2999fc87ff7d8f176fbecb3d6f34278b",
                        "050df97f8c2ead654d9bb3ab8c9178edcd902a32f8495949feadcc1e0480c46b3604131bbd6e3ba573b6dd682fa0a63e5b165d39fc43a625d00207607a2bfeb65ff1d29292152e26b298868e3b87be95d6458f6f2ce6118437b632415abe6ad522874bcd79e4030a5e7bad2efa90a7a7c67e93f0a18fb28369d0a9329ab5c24134ccb0"
                ),
                Arguments.of(
                        3073,
                        "7124b49501012f81cc7f11ca069ec9226cecb8a2c850cfe644e327d22d3e1cd39a27ae3b79d68d89da9bf25bc27139ae65a324918a5f9b7828181e52cf373c84f35b639b7fccbb985b6f2fa56aea0c18f531203497b8bbd3a07ceb5926f1cab74d14bd66486d9a91eba99059a98bd1cd25876b2af5a76c3e9eed554ed72ea952b603bf",
                        "68dede9bef00ba89e43f31a6825f4cf433389fedae75c04ee9f0cf16a427c95a96d6da3fe985054d3478865be9a092250839a697bbda74e279e8a9e69f0025e4cfddd6cfb434b1cd9543aaf97c635d1b451a4386041e4bb100f5e45407cbbc24fa53ea2de3536ccb329e4eb9466ec37093a42cf62b82903c696a93a50b702c80f3c3c5",
                        "72613c9ec9ff7e40f8f5c173784c532ad852e827dba2bf85b2ab4b76f7079081576288e552647a9d86481c2cae75c2dd4e7c5195fb9ada1ef50e9c5098c249d743929191441301c69e1f48505a4305ec1778450ee48b8e69dc23a25960fe33070ea549119599760a8a2d28aeca06b8c5e9ba58bc19e11fe57b6ee98aa44b2a8e6b14a5"
                ),
                Arguments.of(
                        4096,
                        "015094013f57a5277b59d8475c0501042c0b642e531b0a1c8f58d2163229e9690289e9409ddb1b99768eafe1623da896faf7e1114bebeadc1be30829b6f8af707d85c298f4f0ff4d9438aef948335612ae921e76d411c3a9111df62d27eaf871959ae0062b5492a0feb98ef3ed4af277f5395172dbe5c311918ea0074ce0036454f620",
                        "befc660aea2f1718884cd8deb9902811d332f4fc4a38cf7c7300d597a081bfc0bbb64a36edb564e01e4b4aaf3b060092a6b838bea44afebd2deb8298fa562b7b597c757b9df4c911c3ca462e2ac89e9a787357aaf74c3b56d5c07bc93ce899568a3eb17d9250c20f6c5f6c1e792ec9a2dcb715398d5a6ec6d5c54f586a00403a1af1de",
                        "1e0d7f3db8c414c97c6307cbda6cd27ac3b030949da8e23be1a1a924ad2f25b9d78038f7b198596c6cc4a9ccf93223c08722d684f240ff6569075ed81591fd93f9fff1110b3a75bc67e426012e5588959cc5a4c192173a03c00731cf84544f65a2fb9378989f72e9694a6a394a8a30997c2e67f95a504e631cd2c5f55246024761b245"
                ),
                Arguments.of(
                        4097,
                        "9b4052b38f1c5fc8b1f9ff7ac7b27cd242487b3d890d15c96a1c25b8aa0fb99505f91b0b5600a11251652eacfa9497b31cd3c409ce2e45cfe6c0a016967316c426bd26f619eab5d70af9a418b845c608840390f361630bd497b1ab44019316357c61dbe091ce72fc16dc340ac3d6e009e050b3adac4b5b2c92e722cffdc46501531956",
                        "00df940cd36bb9fa7cbbc3556744e0dbc8191401afe70520ba292ee3ca80abbc606db4976cfdd266ae0abf667d9481831ff12e0caa268e7d3e57260c0824115a54ce595ccc897786d9dcbf495599cfd90157186a46ec800a6763f1c59e36197e9939e900809f7077c102f888caaf864b253bc41eea812656d46742e4ea42769f89b83f",
                        "aca51029626b55fda7117b42a7c211f8c6e9ba4fe5b7a8ca922f34299500ead8a897f66a400fed9198fd61dd2d58d382458e64e100128075fc54b860934e8de2e84170734b06e1d212a117100820dbc48292d148afa50567b8b84b1ec336ae10d40c8c975a624996e12de31abbe135d9d159375739c333798a80c64ae895e51e22f3ad"
                ),
                Arguments.of(
                        5120,
                        "9cadc15fed8b5d854562b26a9536d9707cadeda9b143978f319ab34230535833acc61c8fdc114a2010ce8038c853e121e1544985133fccdd0a2d507e8e615e611e9a0ba4f47915f49e53d721816a9198e8b30f12d20ec3689989175f1bf7a300eee0d9321fad8da232ece6efb8e9fd81b42ad161f6b9550a069e66b11b40487a5f5059",
                        "2c493e48e9b9bf31e0553a22b23503c0a3388f035cece68eb438d22fa1943e209b4dc9209cd80ce7c1f7c9a744658e7e288465717ae6e56d5463d4f80cdb2ef56495f6a4f5487f69749af0c34c2cdfa857f3056bf8d807336a14d7b89bf62bef2fb54f9af6a546f818dc1e98b9e07f8a5834da50fa28fb5874af91bf06020d1bf0120e",
                        "7a7acac8a02adcf3038d74cdd1d34527de8a0fcc0ee3399d1262397ce5817f6055d0cefd84d9d57fe792d65a278fd20384ac6c30fdb340092f1a74a92ace99c482b28f0fc0ef3b923e56ade20c6dba47e49227166251337d80a037e987ad3a7f728b5ab6dfafd6e2ab1bd583a95d9c895ba9c2422c24ea0f62961f0dca45cad47bfa0d"
                ),
                Arguments.of(
                        5121,
                        "628bd2cb2004694adaab7bbd778a25df25c47b9d4155a55f8fbd79f2fe154cff96adaab0613a6146cdaabe498c3a94e529d3fc1da2bd08edf54ed64d40dcd6777647eac51d8277d70219a9694334a68bc8f0f23e20b0ff70ada6f844542dfa32cd4204ca1846ef76d811cdb296f65e260227f477aa7aa008bac878f72257484f2b6c95",
                        "6ccf1c34753e7a044db80798ecd0782a8f76f33563accaddbfbb2e0ea4b2d0240d07e63f13667a8d1490e5e04f13eb617aea16a8c8a5aaed1ef6fbde1b0515e3c81050b361af6ead126032998290b563e3caddeaebfab592e155f2e161fb7cba939092133f23f9e65245e58ec23457b78a2e8a125588aad6e07d7f11a85b88d375b72d",
                        "b07f01e518e702f7ccb44a267e9e112d403a7b3f4883a47ffbed4b48339b3c341a0add0ac032ab5aaea1e4e5b004707ec5681ae0fcbe3796974c0b1cf31a194740c14519273eedaabec832e8a784b6e7cfc2c5952677e6c3f2c3914454082d7eb1ce1766ac7d75a4d3001fc89544dd46b5147382240d689bbbaefc359fb6ae30263165"
                ),
                Arguments.of(
                        6144,
                        "3e2e5b74e048f3add6d21faab3f83aa44d3b2278afb83b80b3c35164ebeca2054d742022da6fdda444ebc384b04a54c3ac5839b49da7d39f6d8a9db03deab32aade156c1c0311e9b3435cde0ddba0dce7b26a376cad121294b689193508dd63151603c6ddb866ad16c2ee41585d1633a2cea093bea714f4c5d6b903522045b20395c83",
                        "3d6b6d21281d0ade5b2b016ae4034c5dec10ca7e475f90f76eac7138e9bc8f1dc35754060091dc5caf3efabe0603c60f45e415bb3407db67e6beb3d11cf8e4f7907561f05dace0c15807f4b5f389c841eb114d81a82c02a00b57206b1d11fa6e803486b048a5ce87105a686dee041207e095323dfe172df73deb8c9532066d88f9da7e",
                        "2a95beae63ddce523762355cf4b9c1d8f131465780a391286a5d01abb5683a1597099e3c6488aab6c48f3c15dbe1942d21dbcdc12115d19a8b8465fb54e9053323a9178e4275647f1a9927f6439e52b7031a0b465c861a3fc531527f7758b2b888cf2f20582e9e2c593709c0a44f9c6e0f8b963994882ea4168827823eef1f64169fef"
                ),
                Arguments.of(
                        6145,
                        "f1323a8631446cc50536a9f705ee5cb619424d46887f3c376c695b70e0f0507f18a2cfdd73c6e39dd75ce7c1c6e3ef238fd54465f053b25d21044ccb2093beb015015532b108313b5829c3621ce324b8e14229091b7c93f32db2e4e63126a377d2a63a3597997d4f1cba59309cb4af240ba70cebff9a23d5e3ff0cdae2cfd54e070022",
                        "9ac301e9e39e45e3250a7e3b3df701aa0fb6889fbd80eeecf28dbc6300fbc539f3c184ca2f59780e27a576c1d1fb9772e99fd17881d02ac7dfd39675aca918453283ed8c3169085ef4a466b91c1649cc341dfdee60e32231fc34c9c4e0b9a2ba87ca8f372589c744c15fd6f985eec15e98136f25beeb4b13c4e43dc84abcc79cd4646c",
                        "379bcc61d0051dd489f686c13de00d5b14c505245103dc040d9e4dd1facab8e5114493d029bdbd295aaa744a59e31f35c7f52dba9c3642f773dd0b4262a9980a2aef811697e1305d37ba9d8b6d850ef07fe41108993180cf779aeece363704c76483458603bbeeb693cffbbe5588d1f3535dcad888893e53d977424bb707201569a8d2"
                ),
                Arguments.of(
                        7168,
                        "61da957ec2499a95d6b8023e2b0e604ec7f6b50e80a9678b89d2628e99ada77a5707c321c83361793b9af62a40f43b523df1c8633cecb4cd14d00bdc79c78fca5165b863893f6d38b02ff7236c5a9a8ad2dba87d24c547cab046c29fc5bc1ed142e1de4763613bb162a5a538e6ef05ed05199d751f9eb58d332791b8d73fb74e4fce95",
                        "b42835e40e9d4a7f42ad8cc04f85a963a76e18198377ed84adddeaecacc6f3fca2f01d5277d69bb681c70fa8d36094f73ec06e452c80d2ff2257ed82e7ba348400989a65ee8daa7094ae0933e3d2210ac6395c4af24f91c2b590ef87d7788d7066ea3eaebca4c08a4f14b9a27644f99084c3543711b64a070b94f2c9d1d8a90d035d52",
                        "11c37a112765370c94a51415d0d651190c288566e295d505defdad895dae223730d5a5175a38841693020669c7638f40b9bc1f9f39cf98bda7a5b54ae24218a800a2116b34665aa95d846d97ea988bfcb53dd9c055d588fa21ba78996776ea6c40bc428b53c62b5f3ccf200f647a5aae8067f0ea1976391fcc72af1945100e2a6dcb88"
                ),
                Arguments.of(
                        7169,
                        "a003fc7a51754a9b3c7fae0367ab3d782dccf28855a03d435f8cfe74605e781798a8b20534be1ca9eb2ae2df3fae2ea60e48c6fb0b850b1385b5de0fe460dbe9d9f9b0d8db4435da75c601156df9d047f4ede008732eb17adc05d96180f8a73548522840779e6062d643b79478a6e8dbce68927f36ebf676ffa7d72d5f68f050b119c8",
                        "ed9b1a922c046fdb3d423ae34e143b05ca1bf28b710432857bf738bcedbfa5113c9e28d72fcbfc020814ce3f5d4fc867f01c8f5b6caf305b3ea8a8ba2da3ab69fabcb438f19ff11f5378ad4484d75c478de425fb8e6ee809b54eec9bdb184315dc856617c09f5340451bf42fd3270a7b0b6566169f242e533777604c118a6358250f54",
                        "554b0a5efea9ef183f2f9b931b7497995d9eb26f5c5c6dad2b97d62fc5ac31d99b20652c016d88ba2a611bbd761668d5eda3e568e940faae24b0d9991c3bd25a65f770b89fdcadabcb3d1a9c1cb63e69721cacf1ae69fefdcef1e3ef41bc5312ccc17222199e47a26552c6adc460cf47a72319cb5039369d0060eaea59d6c65130f1dd"
                ),
                Arguments.of(
                        8192,
                        "aae792484c8efe4f19e2ca7d371d8c467ffb10748d8a5a1ae579948f718a2a635fe51a27db045a567c1ad51be5aa34c01c6651c4d9b5b5ac5d0fd58cf18dd61a47778566b797a8c67df7b1d60b97b19288d2d877bb2df417ace009dcb0241ca1257d62712b6a4043b4ff33f690d849da91ea3bf711ed583cb7b7a7da2839ba71309bbf",
                        "dc9637c8845a770b4cbf76b8daec0eebf7dc2eac11498517f08d44c8fc00d58a4834464159dcbc12a0ba0c6d6eb41bac0ed6585cabfe0aca36a375e6c5480c22afdc40785c170f5a6b8a1107dbee282318d00d915ac9ed1143ad40765ec120042ee121cd2baa36250c618adaf9e27260fda2f94dea8fb6f08c04f8f10c78292aa46102",
                        "ad01d7ae4ad059b0d33baa3c01319dcf8088094d0359e5fd45d6aeaa8b2d0c3d4c9e58958553513b67f84f8eac653aeeb02ae1d5672dcecf91cd9985a0e67f4501910ecba25555395427ccc7241d70dc21c190e2aadee875e5aae6bf1912837e53411dabf7a56cbf8e4fb780432b0d7fe6cec45024a0788cf5874616407757e9e6bef7"
                ),
                Arguments.of(
                        8193,
                        "bab6c09cb8ce8cf459261398d2e7aef35700bf488116ceb94a36d0f5f1b7bc3bb2282aa69be089359ea1154b9a9286c4a56af4de975a9aa4a5c497654914d279bea60bb6d2cf7225a2fa0ff5ef56bbe4b149f3ed15860f78b4e2ad04e158e375c1e0c0b551cd7dfc82f1b155c11b6b3ed51ec9edb30d133653bb5709d1dbd55f4e1ff6",
                        "954a2a75420c8d6547e3ba5b98d963e6fa6491addc8c023189cc519821b4a1f5f03228648fd983aef045c2fa8290934b0866b615f585149587dda2299039965328835a2b18f1d63b7e300fc76ff260b571839fe44876a4eae66cbac8c67694411ed7e09df51068a22c6e67d6d3dd2cca8ff12e3275384006c80f4db68023f24eebba57",
                        "af1e0346e389b17c23200270a64aa4e1ead98c61695d917de7d5b00491c9b0f12f20a01d6d622edf3de026a4db4e4526225debb93c1237934d71c7340bb5916158cbdafe9ac3225476b6ab57a12357db3abbad7a26c6e66290e44034fb08a20a8d0ec264f309994d2810c49cfba6989d7abb095897459f5425adb48aba07c5fb3c83c0"
                ),
                Arguments.of(
                        16384,
                        "f875d6646de28985646f34ee13be9a576fd515f76b5b0a26bb324735041ddde49d764c270176e53e97bdffa58d549073f2c660be0e81293767ed4e4929f9ad34bbb39a529334c57c4a381ffd2a6d4bfdbf1482651b172aa883cc13408fa67758a3e47503f93f87720a3177325f7823251b85275f64636a8f1d599c2e49722f42e93893",
                        "9e9fc4eb7cf081ea7c47d1807790ed211bfec56aa25bb7037784c13c4b707b0df9e601b101e4cf63a404dfe50f2e1865bb12edc8fca166579ce0c70dba5a5c0fc960ad6f3772183416a00bd29d4c6e651ea7620bb100c9449858bf14e1ddc9ecd35725581ca5b9160de04060045993d972571c3e8f71e9d0496bfa744656861b169d65",
                        "160e18b5878cd0df1c3af85eb25a0db5344d43a6fbd7a8ef4ed98d0714c3f7e160dc0b1f09caa35f2f417b9ef309dfe5ebd67f4c9507995a531374d099cf8ae317542e885ec6f589378864d3ea98716b3bbb65ef4ab5e0ab5bb298a501f19a41ec19af84a5e6b428ecd813b1a47ed91c9657c3fba11c406bc316768b58f6802c9e9b57"
                ),
                Arguments.of(
                        31744,
                        "62b6960e1a44bcc1eb1a611a8d6235b6b4b78f32e7abc4fb4c6cdcce94895c47860cc51f2b0c28a7b77304bd55fe73af663c02d3f52ea053ba43431ca5bab7bfea2f5e9d7121770d88f70ae9649ea713087d1914f7f312147e247f87eb2d4ffef0ac978bf7b6579d57d533355aa20b8b77b13fd09748728a5cc327a8ec470f4013226f",
                        "efa53b389ab67c593dba624d898d0f7353ab99e4ac9d42302ee64cbf9939a4193a7258db2d9cd32a7a3ecfce46144114b15c2fcb68a618a976bd74515d47be08b628be420b5e830fade7c080e351a076fbc38641ad80c736c8a18fe3c66ce12f95c61c2462a9770d60d0f77115bbcd3782b593016a4e728d4c06cee4505cb0c08a42ec",
                        "39772aef80e0ebe60596361e45b061e8f417429d529171b6764468c22928e28e9759adeb797a3fbf771b1bcea30150a020e317982bf0d6e7d14dd9f064bc11025c25f31e81bd78a921db0174f03dd481d30e93fd8e90f8b2fee209f849f2d2a52f31719a490fb0ba7aea1e09814ee912eba111a9fde9d5c274185f7bae8ba85d300a2b"
                ),
                Arguments.of(
                        102400,
                        "bc3e3d41a1146b069abffad3c0d44860cf664390afce4d9661f7902e7943e085e01c59dab908c04c3342b816941a26d69c2605ebee5ec5291cc55e15b76146e6745f0601156c3596cb75065a9c57f35585a52e1ac70f69131c23d611ce11ee4ab1ec2c009012d236648e77be9295dd0426f29b764d65de58eb7d01dd42248204f45f8e",
                        "1c35d1a5811083fd7119f5d5d1ba027b4d01c0c6c49fb6ff2cf75393ea5db4a7f9dbdd3e1d81dcbca3ba241bb18760f207710b751846faaeb9dff8262710999a59b2aa1aca298a032d94eacfadf1aa192418eb54808db23b56e34213266aa08499a16b354f018fc4967d05f8b9d2ad87a7278337be9693fc638a3bfdbe314574ee6fc4",
                        "4652cff7a3f385a6103b5c260fc1593e13c778dbe608efb092fe7ee69df6e9c6d83a3e041bc3a48df2879f4a0a3ed40e7c961c73eff740f3117a0504c2dff4786d44fb17f1549eb0ba585e40ec29bf7732f0b7e286ff8acddc4cb1e23b87ff5d824a986458dcc6a04ac83969b80637562953df51ed1a7e90a7926924d2763778be8560"
                )
        );
    }

    private final Blake3 hasher = Blake3.initHash();
    private final Blake3 keyedHasher = Blake3.initKeyedHash(KEY);
    private final Blake3 kdfHasher = Blake3.initKeyDerivationFunction(CTX);

    private byte[] inputByteArray;
    private byte[] hashByteArray;
    private byte[] keyedHashByteArray;
    private byte[] deriveKeyByteArray;

    @ParameterizedTest
    @MethodSource("data")
    public void hashArbitraryOutputLength(final int inputLength, final String hash, final String keyedHash, final String deriveKey) throws DecoderException {
        initData(inputLength, hash, keyedHash, deriveKey);
        final byte[] actual = hasher.update(inputByteArray).doFinalize(hashByteArray.length);
        assertArrayEquals(hashByteArray, actual);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hashTruncatedOutput(final int inputLength, final String hash, final String keyedHash, final String deriveKey) throws DecoderException {
        initData(inputLength, hash, keyedHash, deriveKey);
        final byte[] actual = Blake3.hash(inputByteArray);
        assertArrayEquals(Arrays.copyOf(this.hashByteArray, 32), actual);
    }

    private void initData(final int inputLength, final String hash, final String keyedHash, final String deriveKey)
            throws DecoderException {
        this.inputByteArray = new byte[inputLength];
        for (int i = 0; i < inputByteArray.length; i++) {
            inputByteArray[i] = (byte) (i % 251);
        }
        this.hashByteArray = Hex.decodeHex(hash);
        this.keyedHashByteArray = Hex.decodeHex(keyedHash);
        this.deriveKeyByteArray = Hex.decodeHex(deriveKey);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void keyDerivation(final int inputLength, final String hash, final String keyedHash, final String deriveKey) throws DecoderException {
        initData(inputLength, hash, keyedHash, deriveKey);
        final byte[] actual = kdfHasher.update(inputByteArray).doFinalize(deriveKeyByteArray.length);
        assertArrayEquals(deriveKeyByteArray, actual);
        kdfHasher.reset().update(inputByteArray);
        final byte[] truncated = kdfHasher.doFinalize(32);
        assertArrayEquals(Arrays.copyOf(deriveKeyByteArray, 32), truncated);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void keyedHashArbitraryOutputLength(final int inputLength, final String hash, final String keyedHash, final String deriveKey) throws DecoderException {
        initData(inputLength, hash, keyedHash, deriveKey);
        final byte[] actual = keyedHasher.update(inputByteArray).doFinalize(keyedHashByteArray.length);
        assertArrayEquals(keyedHashByteArray, actual);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void keyedHashTruncatedOutput(final int inputLength, final String hash, final String keyedHash, final String deriveKey) throws DecoderException {
        initData(inputLength, hash, keyedHash, deriveKey);
        final byte[] actual = Blake3.keyedHash(KEY, inputByteArray);
        assertArrayEquals(Arrays.copyOf(keyedHashByteArray, 32), actual);
    }
}
