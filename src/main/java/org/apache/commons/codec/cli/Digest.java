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

package org.apache.commons.codec.cli;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

/**
 * A minimal command line to run digest over files, directories or a string.
 *
 * @see #main(String[])
 * @since 1.11
 */
public class Digest {

    private static final String EMPTY = "";

    /**
     * Runs the digest algorithm in {@code args[0]} on the file in {@code args[1]}. If there is no {@code args[1]}, use standard input.
     *
     * <p>
     * The algorithm can also be {@code ALL} or {@code *} to output one line for each known algorithm.
     * </p>
     *
     * @param args {@code args[0]} is one of {@link MessageDigestAlgorithms} name, {@link MessageDigest} name, {@code ALL}, or {@code *}. {@code args[1+]} is a
     *             FILE/DIRECTORY/String.
     * @throws IOException if an error occurs.
     */
    public static void main(final String[] args) throws IOException {
        new Digest(args).run();
    }

    private final String algorithm;
    private final String[] args;
    private final String[] inputs;

    private Digest(final String[] args) {
        Objects.requireNonNull(args, "args");
        final int argsLength = args.length;
        if (argsLength == 0) {
            throw new IllegalArgumentException(String.format("Usage: java %s [algorithm] [FILE|DIRECTORY|string] ...", Digest.class.getName()));
        }
        this.args = args;
        this.algorithm = args[0];
        this.inputs = argsLength > 1 ? Arrays.copyOfRange(args, 1, argsLength) : null;
    }

    private void println(final String prefix, final byte[] digest) {
        println(prefix, digest, null);
    }

    private void println(final String prefix, final byte[] digest, final String fileName) {
        // The standard appears to be to print
        // hex, space, then either space or '*' followed by file name
        // where '*' is used for binary files
        // shasum(1) has a -b option which generates " *" separator
        // we don't distinguish binary files at present
        System.out.println(prefix + Hex.encodeHexString(digest) + (fileName != null ? "  " + fileName : EMPTY));
    }

    private void run() throws IOException {
        final BufferedInputStream systemIn = inputs != null ? null : new BufferedInputStream(System.in);
        if (algorithm.equalsIgnoreCase("ALL") || algorithm.equals("*")) {
            run(systemIn, MessageDigestAlgorithms.values());
            return;
        }
        final MessageDigest messageDigest = DigestUtils.getDigest(algorithm, null);
        if (messageDigest != null) {
            run(systemIn, EMPTY, messageDigest);
        } else {
            run(systemIn, EMPTY, DigestUtils.getDigest(algorithm.toUpperCase(Locale.ROOT)));
        }
    }

    private void run(final BufferedInputStream systemIn, final String prefix, final MessageDigest messageDigest) throws IOException {
        if (inputs == null) {
            println(prefix, DigestUtils.digest(messageDigest, systemIn));
            return;
        }
        for (final String source : inputs) {
            final File file = new File(source);
            if (file.isFile()) {
                println(prefix, DigestUtils.digest(messageDigest, file), source);
            } else if (file.isDirectory()) {
                final File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    run(prefix, messageDigest, listFiles);
                }
            } else {
                // use the default charset for the command-line parameter
                final byte[] bytes = source.getBytes(Charset.defaultCharset());
                println(prefix, DigestUtils.digest(messageDigest, bytes));
            }
        }
    }

    private void run(final BufferedInputStream systemIn, final String prefix, final String messageDigestAlgorithm) throws IOException {
        run(systemIn, prefix, DigestUtils.getDigest(messageDigestAlgorithm));
    }

    private void run(final BufferedInputStream systemIn, final String[] digestAlgorithms) throws IOException {
        for (final String messageDigestAlgorithm : digestAlgorithms) {
            if (DigestUtils.isAvailable(messageDigestAlgorithm)) {
                if (systemIn != null) {
                    // 1 GB arbitrary default.
                    systemIn.mark(Integer.getInteger(getClass().getName() + ".markReadLimit", 1_073_741_824));
                }
                run(systemIn, messageDigestAlgorithm + " ", messageDigestAlgorithm);
                if (systemIn != null) {
                    systemIn.reset();
                }
            }
        }
    }

    private void run(final String prefix, final MessageDigest messageDigest, final File[] files) throws IOException {
        for (final File file : files) {
            if (file.isFile()) {
                println(prefix, DigestUtils.digest(messageDigest, file), file.getName());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), Arrays.toString(args));
    }
}
