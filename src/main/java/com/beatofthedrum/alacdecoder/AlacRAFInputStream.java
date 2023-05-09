package com.beatofthedrum.alacdecoder;

import java.io.*;
import java.util.Objects;

class AlacRAFInputStream extends AlacInputStream {

    protected final RandomAccessFile fIn;

    public AlacRAFInputStream(RandomAccessFile fIn) {
        this.fIn = Objects.requireNonNull(fIn);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return fIn.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fIn.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return fIn.skipBytes((int) Math.min(Integer.MAX_VALUE, n));
    }

    @Override
    public int available() throws IOException {
        return (int) Math.min(Integer.MAX_VALUE, fIn.length() - fIn.getFilePointer());
    }

    @Override
    public void close() throws IOException {
        fIn.close();
    }

    @Override
    public int read() throws IOException {
        return fIn.read();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        fIn.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        fIn.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return fIn.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return fIn.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return fIn.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return fIn.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return fIn.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return fIn.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return fIn.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return fIn.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return fIn.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return fIn.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return fIn.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return fIn.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return fIn.readUTF();
    }

    @Override
    public void seek(int pos) throws IOException {
        fIn.seek(pos);
    }

}
