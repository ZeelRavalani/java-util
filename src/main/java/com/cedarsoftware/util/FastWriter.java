package com.cedarsoftware.util;

import java.io.IOException;
import java.io.Writer;

/**
 * Buffered Writer that does not use synchronization.   Much faster than the JDK variants because
 * they use synchronization.  Typically, this class is used with a separate instance per thread, so
 * synchronization is not needed.
 * <p>
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">License</a>
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class FastWriter extends Writer {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private Writer out;
    private char[] cb;
    private int nextChar;

    public FastWriter(Writer out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    public FastWriter(Writer out, int bufferSize) {
        super(out);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.out = out;
        this.cb = new char[bufferSize];
        this.nextChar = 0;
    }

    private void flushBuffer() throws IOException {
        if (nextChar == 0) {
            return;
        }
        out.write(cb, 0, nextChar);
        nextChar = 0;
    }

    public void write(int c) throws IOException {
        if (out == null) {
            throw new IOException("FastWriter stream is closed.");
        }
        if (nextChar >= cb.length) {
            flushBuffer();
        }
        cb[nextChar++] = (char) c;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (out == null) {
            throw new IOException("FastWriter stream is closed.");
        }
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        if (len >= cb.length) {
            // If the request length exceeds the size of the output buffer,
            // flush the buffer and then write the data directly.
            flushBuffer();
            out.write(cbuf, off, len);
            return;
        }
        if (len > cb.length - nextChar) {
            flushBuffer();
        }
        System.arraycopy(cbuf, off, cb, nextChar, len);
        nextChar += len;
    }

    public void write(String str, int off, int len) throws IOException {
        if (out == null) {
            throw new IOException("FastWriter stream is closed.");
        }
        int b = off, t = off + len;
        while (b < t) {
            int d = Math.min(cb.length - nextChar, t - b);
            str.getChars(b, b + d, cb, nextChar);
            b += d;
            nextChar += d;
            if (nextChar >= cb.length) {
                flushBuffer();
            }
        }
    }

    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

    public void close() throws IOException {
        if (out == null) {
            return;
        }
        try {
            flushBuffer();
        } finally {
            out.close();
            out = null;
            cb = null;
        }
    }
}