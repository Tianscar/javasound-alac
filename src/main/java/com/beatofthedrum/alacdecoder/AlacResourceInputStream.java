package com.beatofthedrum.alacdecoder;

import java.io.*;

class AlacResourceInputStream extends AlacInputStream {

	private final String resource;
	private final ClassLoader resourceLoader;
	private boolean closed = false;
	private DataInputStream in = null;
	private BufferedReader lineReader = null;
	private int offset;

	public AlacResourceInputStream(ClassLoader resourceLoader, String resource) throws IOException {
		this.resourceLoader = resourceLoader;
		this.resource = resource;
		ensureStreamAvailable();
	}

	@Override
	public int read() throws IOException {
		ensureOpen();
		int i = in.read();
		if (i >= 0) offset ++;

		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		int i = in.read(b, off, len);

		if (i > 0) offset += i;

		return i;
	}

	@Override
	public long skip(long n) throws IOException {
		ensureOpen();
		long i = in.skip(n);

		if (i > 0) offset += i;

		return i;
	}

	@Override
	public void seek(int pos) throws IOException {
		checkNotClosed();
		if (pos < 0) throw new IndexOutOfBoundsException("negative position: " + pos);
		int bytesToSkip = pos - offset;
		if (bytesToSkip >= 0) {
			ensureStreamAvailable();
			skipBytes(bytesToSkip);
		}
		else {
			in.close();
			in = null;
			ensureStreamAvailable();
			skipBytes(Math.min(pos, MAX_BUFFER_SIZE));
		}
	}

	public void close() throws IOException {
		if (closed) return;
		closed = true;
		in.close();
	}

	private void checkNotClosed() throws IOException {
		if (closed) throw new IOException("Already closed");
	}

	private void ensureStreamAvailable() throws IOException {
		if (in == null) {
			InputStream resIn = resourceLoader.getResourceAsStream(resource);
			if (resIn == null) throw new IOException("Couldn't read resource \"" + resource + "\" with ClassLoader: " + resourceLoader);
			offset = 0;
			in = new DataInputStream(resIn);
			lineReader = new BufferedReader(new InputStreamReader(in));
		}
	}

	private void ensureOpen() throws IOException {
		checkNotClosed();
		ensureStreamAvailable();
	}

	@Override
	public int available() throws IOException {
		ensureOpen();
		return in.available();
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		ensureOpen();
		in.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		in.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		ensureOpen();
		return in.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		ensureOpen();
		return in.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		ensureOpen();
		return in.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		ensureOpen();
		return in.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		ensureOpen();
		return in.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		ensureOpen();
		return in.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		ensureOpen();
		return in.readChar();
	}

	@Override
	public int readInt() throws IOException {
		ensureOpen();
		return in.readInt();
	}

	@Override
	public long readLong() throws IOException {
		ensureOpen();
		return in.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		ensureOpen();
		return in.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		ensureOpen();
		return in.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		ensureOpen();
		return lineReader.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		ensureOpen();
		return in.readUTF();
	}

	@Override
	public int offset() {
		return offset;
	}

}
