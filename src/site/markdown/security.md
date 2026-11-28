---
title: Apache Commons Security Reports
author: Apache Commons Team
email: dev@commons.apache.org
---

<!--
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
-->

# About Security

For information about reporting or asking questions about security, please see
[Apache Commons Security](https://commons.apache.org/security.html).

This page lists all security vulnerabilities fixed in released versions of this component.

Please note that binary patches are never provided. If you need to apply a source code patch, use the building
instructions for the component version that you are using.

If you need help on building this component or other help on following the instructions to mitigate the known
vulnerabilities listed here, please send your questions to the public [user mailing list](mail-lists.html).

If you have encountered an unlisted security vulnerability or other unexpected behavior that has security impact, or if
the descriptions here are incomplete, please report them privately to the Apache Security Team. Thank you.

# Security Vulnerabilities

## Security Vulnerabilities Fixed in 1.23.0

### CVE-2026-124077

- CVE-2026-124077: Apache Commons Codec: NUL bytes in tree entry names cause Git/SWHID identifier collisions (GitIdentifiers class)
- Severity: low 

#### Affected versions:

- Apache Commons Codec 1.22.0 before 1.23.0
- Apache Commons Codec 387120c901f4a75c3112dfdf8877dcdec7ae685d before 87f6aa2818836998528ff4b0d1ea35545a800ee5

#### Description:

The GitIdentifiers class in Apache Commons Codec did not reject NUL bytes in tree entry names. Because Git uses a NUL byte to mark the end of each entry name in a tree object, a crafted name could make one tree serialize to the same bytes as a different tree. Two different directory trees could then receive the same Git tree identifier or SWHID.


The impact is limited: common file systems do not allow NUL bytes in file names, so trees built by walking a file system are not affected. Only applications that build trees from names supplied by other sources, such as entries of archive formats that permit NUL bytes in names (e.g. ZIP) or names passed directly to the API, are exposed.

This issue affects Apache Commons Codec: from 1.22.0 before 1.23.0.

Users are recommended to upgrade to version 1.23.0, which fixes the issue.

#### Credit:

- Claude Security (tool)
- Gary D. Gregory (remediation developer)

#### References:

- https://github.com/apache/commons-codec/commit/87f6aa2818836998528ff4b0d1ea35545a800ee5.patch
- https://commons.apache.org/
- https://www.cve.org/CVERecord?id=CVE-2026-124077

### CVE-2026-119078

- CVE-2026-119078: Apache Commons Codec: Blake3 doFinalize is stateless; the documented KDF example derives identical keys
- Severity: important 

#### Affected versions:

- Apache Commons Codec 1.16.0 before 1.23.0

#### Description:

Apache Commons Codec's Blake3 class documented the output of its doFinalize methods as depending on any previously finalized bytes, so that consecutive calls would return different output. In fact, doFinalize does not change the hash state: calling it again without supplying more input returns the same bytes. The key-derivation example in the class Javadoc, shipped since 1.16.0, derives two keys with two consecutive doFinalize calls. Code that follows it gets identical keys, so the separation between the derived keys is lost. The same applies to any application that calls doFinalize more than once on one instance and expects new output each time.

This issue affects Apache Commons Codec from 1.16.0 before 1.23.0.

Version 1.23.0 corrects the documentation, but the behavior of doFinalize is unchanged. Upgrading alone does not fix affected code. Users should check their applications for repeated doFinalize calls on the same Blake3 instance and derive multiple keys by requesting the combined length in one call and splitting the output, as the updated Javadoc shows.

#### Credit:

- Claude Security (tool)
- Gary D. Gregory (remediation developer)

#### References:

https://commons.apache.org/
https://www.cve.org/CVERecord?id=CVE-2026-119078

# Safe Deserialization

For information about safe deserialization, please see
[Safe Deserialization](https://commons.apache.org/io/description.html#Safe_Deserialization).
