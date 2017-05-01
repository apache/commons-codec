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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link FilterInputStream} implementation to decode Brotli encoded stream.
 * Library relies on <a href="https://github.com/google/brotli">Google brotli</a>
 * 
 * @since 1.11
 */
public class BrotliInputStream extends FilterInputStream {
    
    private org.brotli.dec.BrotliInputStream decIS;

    public BrotliInputStream(InputStream in) throws IOException {
        super(in);
        this.decIS = new org.brotli.dec.BrotliInputStream(in);
    }

    /**
     * @return
     * @throws IOException
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return decIS.available();
    }

    /**
     * @throws IOException
     * @see org.brotli.dec.BrotliInputStream#close()
     */
    public void close() throws IOException {
        decIS.close();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return decIS.hashCode();
    }

    /**
     * @param b
     * @return
     * @throws IOException
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        return decIS.read(b);
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return decIS.equals(obj);
    }

    /**
     * @param n
     * @return
     * @throws IOException
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        return decIS.skip(n);
    }

    /**
     * @param readlimit
     * @see java.io.InputStream#mark(int)
     */
    public void mark(int readlimit) {
        decIS.mark(readlimit);
    }

    /**
     * @return
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return decIS.markSupported();
    }

    /**
     * @return
     * @throws IOException
     * @see org.brotli.dec.BrotliInputStream#read()
     */
    public int read() throws IOException {
        return decIS.read();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @throws IOException
     * @see org.brotli.dec.BrotliInputStream#read(byte[], int, int)
     */
    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        return decIS.read(arg0, arg1, arg2);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return decIS.toString();
    }

    /**
     * @throws IOException
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException {
        decIS.reset();
    }

}
