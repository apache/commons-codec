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
# Apache Commons Codec Threat Model (summary) version 1.23.0

This page is the short version of the
[Apache Commons Codec Threat Model](threat-model-1.23.0.html)
([Markdown](https://raw.githubusercontent.com/apache/commons-codec/master/src/site/markdown/threat-model-1.23.0.md)),
which is authoritative where the two differ.
Both apply the
[Apache Commons Threat Model 0.1.0](https://commons.apache.org/threat-model-0.1.0.html)
([Markdown](https://svn.apache.org/repos/asf/commons/cms-site/trunk/content/markdown/threat-model-0.1.0.md))
to Codec 1.23.0 and later.

Security researchers should use the
[full Codec threat model](threat-model-1.23.0.html)
([Markdown](https://raw.githubusercontent.com/apache/commons-codec/master/src/site/markdown/threat-model-1.23.0.md)).
Developers should use this page.

## The model in brief

Commons Codec is an in-process library of encoders, decoders, digests,
checksums, password-hash implementations, phonetic algorithms and related
utilities. It has no network listener, authentication or authorization layer.
The calling application is responsible for validating and bounding input at
its own trust boundary.

**Codec does not claim the Commons "secure parsing" guarantee.**
Its documented default resource limits are functional safeguards and defense
in depth, not a promise that an API safely accepts arbitrary malicious input.

Methods accepting a `Path`, `File` or `InputStream` read only the resources
designated by the caller. Encoded data is not interpreted as an instruction to
access other resources or execute code.

## Decoding

- Lenient decoders intentionally accept some non-canonical and malformed
  representations for compatibility.
- `CodecPolicy.STRICT` provides stricter functional validation. It is not a
  security boundary, sanitizer, authentication mechanism or token validator.
- Where encoded text affects a security decision, the application must define
  and consistently enforce the protocol's canonical representation. It must
  not validate, cache, sign or deny-list one representation while consuming a
  differently normalized representation.
- Decoder differences from an RFC or another implementation are functional
  correctness issues unless another claimed security property is violated.
- Decoded output is as untrusted as its input. Codec does not make decoded
  paths, names, headers or other values safe for downstream use.

## Digests, identifiers and passwords

- Plain digests and checksums do not authenticate data. Use an appropriate MAC
  or signature at the protocol layer.
- Security depends on the algorithm, key and protocol selected by the caller.
  Codec continues to offer weak legacy algorithms for interoperability.
- `GitIdentifiers` produces content addresses, not signatures, attestations or
  proofs of provenance. The caller must validate virtual tree names and choose
  an appropriate digest algorithm.
- The `crypt(3)` implementations reproduce legacy formats for interoperability.
  Codec does not provide password policy or constant-time comparison.
  Applications importing salts or stored hashes from an untrusted source must
  validate their parameters.
- Phonetic encoders are fuzzy-matching aids; collisions are intentional.

## Developer responsibilities

Applications using Codec must:

1. Validate and bound untrusted input before passing it to Codec.
2. Treat Codec's default resource limits as defense in depth.
3. Enforce a canonical textual representation where a protocol requires one.
4. Apply authentication with a suitable signature or MAC rather than a plain
   digest or content identifier.
5. Select suitable algorithms and securely manage keys.
6. Validate password-hash parameters received from outside the application's
   trusted credential store.
7. Validate virtual-tree names against the source format and Git object model.
8. Treat all output derived from untrusted input as untrusted.

## Reporting

A report is a Codec vulnerability only if it violates a security property
claimed by the
[Commons family model](https://commons.apache.org/threat-model-0.1.0.html)
([Markdown](https://svn.apache.org/repos/asf/commons/cms-site/trunk/content/markdown/threat-model-0.1.0.md))
or the [full Codec model](threat-model-1.23.0.html)
([Markdown](https://raw.githubusercontent.com/apache/commons-codec/master/src/site/markdown/threat-model-1.23.0.md)),
is reachable by an in-scope
adversary, and is not legitimate behavior for the underlying JDK facility.

Report undisclosed vulnerabilities privately as described on the
[Apache Commons Security page](https://commons.apache.org/security.html).
Questions about published vulnerabilities belong on the public
[user mailing list](mail-lists.html).
