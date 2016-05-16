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

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.codec.binary.Hex;

/**
 * A minimal command line to run digest over files.
 *
 * @see #main(String[])
 */
public class Digest {

    /**
     * Runs the digest algorithm in {@code args[0]} on the file in {@code args[1]}. If there is no {@code args[1]}, use
     * standard input.
     * 
     * <p>
     * The algorithm can also be {@code ALL} or {@code *} to output one line for each known algorithm.
     * </p>
     * 
     * @param args
     *            {@code args[0]} is one of {@link MessageDigestAlgorithm} name, {@link MessageDigest} name, {@code ALL}
     *            , or {@code *}. {@code args[1]} is a FILE.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new Digest(args).run();
    }

    private final String algorithm;
    private final String[] args;
    private final String source;

    private Digest(final String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args");
        }
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    String.format("Usage: java %s [algorithm] [FILE|DIRECTORY]", Digest.class.getName()));
        }
        this.args = args;
        algorithm = args[0];
        source = args.length == 1 ? null : args[1];
    }

    private void println(String prefix, final byte[] digest) {
        final String sourceDesc = source == null ? "-" : source;
        // The standard appears to be to print 
        // hex, space, then either space or '*' followed by file
        // where '*' is used for binary files
        // shasum(1) has a -b option which generates " *" separator
        System.out.println(prefix + Hex.encodeHexString(digest) + "  " + sourceDesc);
    }

    private void run() throws IOException {
        if (algorithm.equalsIgnoreCase("ALL") || algorithm.equals("*")) {
            run(MessageDigestAlgorithm.values());
            return;
        }
        final MessageDigest messageDigest = DigestUtils.getDigest(algorithm, null);
        if (messageDigest != null) {
            run("", messageDigest);
        } else {
            run("", MessageDigestAlgorithm.valueOf(algorithm.toUpperCase(Locale.ROOT)).getMessageDigest());
        }
    }

    private void run(MessageDigestAlgorithm[] digestAlgorithms) throws IOException {
        for (MessageDigestAlgorithm messageDigestAlgorithm : digestAlgorithms) {
            if (messageDigestAlgorithm.isAvailable()) {
                run(messageDigestAlgorithm.getName() + " ", messageDigestAlgorithm);
            }
        }
    }

    private void run(String prefix, final MessageDigest messageDigest) throws IOException {
        if (source == null) {
            println(prefix, DigestUtils.digest(messageDigest, System.in));
            return;
        }
        final File file = new File(source);
        if (file.isFile()) {
            println(prefix, DigestUtils.digest(messageDigest, file));
        } else if (file.isDirectory()) {
            run(prefix, messageDigest, file.listFiles());
        } else {
            System.err.println("Parameter is file nor directory: '" + file + "'");
        }
    }

    private void run(String prefix, MessageDigest messageDigest, File[] files) throws IOException {
        for (File file : files) {
            println(prefix, DigestUtils.digest(messageDigest, file));
        }
    }

    private void run(String prefix, final MessageDigestAlgorithm messageDigestAlgorithm) throws IOException {
        run(prefix, messageDigestAlgorithm.getMessageDigest());
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), Arrays.toString(args));
    }
}
