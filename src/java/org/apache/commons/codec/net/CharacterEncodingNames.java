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

package org.apache.commons.codec.net;

/**
 * Character encoding names required of every implementation of the Java platform.
 * 
 * According to the Java documentation <a
 * href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character encoding names
 * </a>:
 * <p>
 * <cite>Every implementation of the Java platform is required to support the following character encodings. Consult the
 * release documentation for your implementation to see if any other encodings are supported. </cite>
 * </p>
 * 
 * This interface is private to the package since it perhaps would best belong in the [lang] project with other required
 * encoding names. As is, this interface only defines the names used in this package. Even if a similar interface is
 * defined in [lang], it is not forseen that [codec] would be made to depend on [lang].
 * 
 * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character encoding
 *          names </a>
 * @author Apache Software Foundation
 * @since 1.4
 * @version $Id$
 */
class CharacterEncodingNames {
    /**
     * <p>
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *          encoding names </a>
     */
    static final String US_ASCII = "US-ASCII";

    /**
     * <p>
     * Eight-bit Unicode Transformation Format.
     * </p>
     * <p>
     * Every implementation of the Java platform is required to support this character encoding.
     * </p>
     * 
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">JRE character
     *          encoding names </a>
     */
    static final String UTF8 = "UTF-8";
}