<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Apache Commons Codec Threat Model version 1.23.0

This page applies the
[Apache Commons Threat Model 0.1.0](https://commons.apache.org/threat-model-0.1.0.html)
([Markdown](https://svn.apache.org/repos/asf/commons/cms-site/trunk/content/markdown/threat-model-0.1.0.md))
to Commons Codec.
The family model applies in full;
this page lists only the Codec profile and the points where Codec differs or adds detail.

This model applies to Commons Codec 1.23.0 and later.
A [short version](threat-model-short-1.23.0.html)
([Markdown](https://raw.githubusercontent.com/apache/commons-codec/master/src/site/markdown/threat-model-short-1.23.0.md))
is available for developers;
this page is authoritative where the two differ.

## Profile

- **Kind:** a low-level building block.
  `DigestUtils` and `HmacUtils` decorate the JCA (`MessageDigest`, `Mac`),
  so the JDK-parity principle applies to them directly.
- **Family group:** parsers / format readers.
- **Typical untrusted-input surface:**
  encoded text passed to decoders
  (Base16, Base32, Base58, Base64, Hex, `BinaryCodec`,
  `URLCodec`, `PercentCodec`, `QuotedPrintableCodec`, `QCodec`, `BCodec`);
  passwords passed to `Crypt` and its implementations;
  names passed to the phonetic encoders.
- **Side effects:**
  methods that take a `Path`, `File` or `InputStream` read what the caller designates;
  `GitIdentifiers.treeId(MessageDigest, Path)` reads the directory tree designated by the caller.
  Symbolic links are not followed; their targets are included as link content.
  `GitIdentifiers.TreeIdBuilder` constructs a virtual tree from caller-supplied values.
  Codec reads its own classpath resources (Beider-Morse rules)
  and the system properties listed below.
  Encoded data is never interpreted as an instruction to access other resources or run code.
- **Command-line tool:** `org.apache.commons.codec.cli.Digest` digests the files named on its command line;
  its arguments are trusted operator input.

## Secure parsing

**Codec does not claim the "secure parsing" guarantee.**
Applications that accept input from untrusted users
must bound it before passing it to Codec.
Reports that need malicious input are routed `OUT-OF-MODEL: no-secure-parsing-yet`
unless they break another property claimed by the family model or this profile.

## Documented default resource limits

The following inputs are bounded by default.
These limits are functional safeguards, not a claim that the entry points safely
process arbitrary malicious input.

| Entry point | Default bound | Knob |
| --- | --- | --- |
| `Base58` encode / decode, including the streams | 8192 bytes | `Base58.Builder.setMaxEncodeLength` / `setMaxDecodeLength` |
| Beider-Morse (`PhoneticEngine`, `BeiderMorseEncoder`) | 666 characters | `PhoneticEngine.Builder.setMaxInputLength` |
| `Sha2Crypt` password length | 4096 bytes | system property `org.apache.commons.codec.digest.Sha2Crypt.keyMax` |
| `Sha2Crypt` `rounds=` taken from the salt | 1,000,000 | system property `org.apache.commons.codec.digest.Sha2Crypt.roundsMax` (at most 999,999,999) |

Raising a bound is a trusted-operator decision.
Findings that require a raised bound or other trusted configuration remain out
of model under the family trusted-input rule.
Other encoders and decoders are expected to use resources appropriate to the
documented operation (family property 2); a violation there is a correctness bug.

## Properties Codec does not provide

### Canonical decoding

Decoders are not injective: many encoded strings can decode to the same bytes.

- The default `CodecPolicy.LENIENT` skips characters outside the alphabet,
  ignores data after padding,
  and accepts the standard and URL-safe Base64 alphabets together.
- `CodecPolicy.STRICT` rejects non-canonical Base32 and Base64 input:
  trailing bits since 1.15,
  alphabet, padding and line separators since 1.23.0.
  `Hex` accepts only ASCII hexadecimal digits since 1.23.0.

These are functional validation features,
not a security boundary, sanitizer or token-validation framework.
A representation they wrongly accept or reject is an ordinary bug
(family model: rejecting malformed input is not a security property).
Applications must not assume that two different encoded strings represent different values:
where a protocol defines a canonical textual representation, enforce it before
using the text as a security identity. Do not validate, cache, sign or deny-list
one representation while consuming a differently normalized representation.

### RFC-exact parsing of the `net` codecs

Differences between `QCodec`, `BCodec`, `URLCodec`, `PercentCodec` or
`QuotedPrintableCodec` and their documented RFC behavior or interoperable
implementations are functional correctness issues unless another claimed
security property is violated. Codec does not claim that these decoders are
security filters for downstream consumers.
Decoded output is as untrusted as its input (family output rule 1)
and may contain CR, LF, `/`, `..` or any other character.

### Exception contract on malformed input

Decoders document `DecoderException` or `IllegalArgumentException`.
An undeclared `RuntimeException` on malformed input is a robustness bug, not a vulnerability.

### Digests, checksums and identifiers

- `MurmurHash2`, `MurmurHash3`, `XXHash32`, `PureJavaCrc32`, `PureJavaCrc32C` and `Crc16`
  are not collision resistant (family false friends).
- `DigestUtils` and `GitIdentifiers` use the JCA digest algorithm selected by
  the caller; collision resistance is that algorithm's.
  Offering MD2, MD5 or SHA-1 is not a finding (JDK parity).
- `HmacUtils` uses the caller-selected JCA MAC and caller-supplied key. Its
  security depends on the selected MAC, the key and the protocol in which it is used.
- `GitIdentifiers` computes Git object identifiers and SWHIDs for interoperability.
  Its output is a content address,
  not a signature, MAC, attestation or proof of provenance.
  Rejecting tree entry names that Git cannot represent (since 1.23.0)
  is input validation; a gap in it is an ordinary bug (family output rule 3).

### Password hashing

`Crypt`, `UnixCrypt`, `Md5Crypt` (including APR1) and `Sha2Crypt`
implement the glibc `crypt(3)` formats for interoperability.

- As a Codec-specific interoperability rule, merely reproducing behavior
  defined by glibc `crypt(3)` is not a vulnerability.
  Offering the weak legacy schemes (DES `crypt`, MD5-crypt) is not a finding.
- In the ordinary credential-store model, the salt string, including its
  `rounds=` field, is trusted.
  In the verification idiom `stored.equals(Crypt.crypt(entered, stored))`,
  the stored hash is the trust anchor.
  An application that hashes with salts from an untrusted source
  must validate them.
- Codec does not provide constant-time comparison or password policy.

### Blake3

`Blake3` is a pure-Java implementation of the BLAKE3 hash, keyed hash and KDF.
Output that differs from the BLAKE3 reference test vectors is a correctness bug.
No constant-time or side-channel claim is made (family model).

### Phonetic encoders

The `language` and `language.bm` encoders are fuzzy-matching aids.
Their output is not an identifier; collisions are their purpose.

## Application responsibilities

In addition to the family list, applications using Codec must:

1. Bound untrusted input before passing it to Codec. Treat the default limits
   above as defense in depth, not as authorization to pass arbitrary malicious input.
2. Define and enforce a canonical form wherever the textual representation
   of encoded data affects a security decision.
3. Apply authentication (signature or MAC) at the protocol layer;
   a digest or identifier alone is not one.
4. Choose a collision-resistant algorithm when an identifier backs an integrity decision.
5. Validate salts and `rounds=` values that do not come from the application's own password store.
6. Validate tree entry names passed to `GitIdentifiers` against the source format.

## Known non-findings

| Report | Disposition |
| --- | --- |
| "Base64, Base32 or Hex decoding accepts several spellings of the same bytes." | `BY-DESIGN: property-disclaimed` |
| "`CodecPolicy.STRICT` accepts input X." | Ordinary bug; `VALID-HARDENING` at most |
| "`QCodec` or `BCodec` decodes differently from mail client Y." | `BY-DESIGN: property-disclaimed` |
| "A decoder throws an undeclared runtime exception on malformed input." | `KNOWN-NON-FINDING` |
| "`DigestUtils` offers MD5 or SHA-1" / "`UnixCrypt` uses DES." | `OUT-OF-MODEL: jdk-parity` |
| "`Sha2Crypt` honors `rounds=` from the salt" / "costs quadratic time in password length", after a trusted operator raises the default bounds. | `OUT-OF-MODEL: trusted-input` |
| "Two trees collide because of the caller-selected digest algorithm." | `BY-DESIGN: property-disclaimed` |
| "Invalid or non-representable tree names produce an ambiguous `GitIdentifiers` serialization." | Ordinary input-validation bug; `VALID-HARDENING` at most |
| "Two distinct valid Git trees are serialized identically before hashing." | Functional correctness bug unless an identifier API is designated as a security boundary |

## Conditions that would change this model

- Codec claims the "secure parsing" guarantee for a stated version.
- Codec documents an API as safe for arbitrary untrusted input.
- Codec gains network access, format-driven ambient I/O, or code execution.
- A validation mode or identifier API is designated as an authentication,
  authorization or attestation boundary.
