package com.beatofthedrum.alacdecoder;

import java.io.*;

/**
 * Author: Denis Tulskiy
 * Date: 4/7/11
 */
public abstract class AlacInputStream extends InputStream implements DataInput {

    static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    byte[] read_buf = new byte[8];

    public static AlacInputStream open(RandomAccessFile file) {
        return new AlacRAFInputStream(file);
    }

    public static AlacInputStream open(ClassLoader resourceLoader, String resource) throws IOException {
        return new AlacResourceInputStream(resourceLoader, resource);
    }

    public static AlacInputStream open(InputStream in) {
        return new AlacMarkResetInputStream(in.markSupported() ? in : new BufferedInputStream(in));
    }

    public abstract void seek(int pos) throws IOException;
    public abstract int offset() throws IOException;

}
