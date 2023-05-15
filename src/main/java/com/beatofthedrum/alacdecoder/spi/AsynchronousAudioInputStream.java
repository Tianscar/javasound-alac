package com.beatofthedrum.alacdecoder.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class AsynchronousAudioInputStream extends AudioInputStream implements CircularBuffer.Trigger {

	private byte[] singleByte;
	protected final CircularBuffer buffer;

	AsynchronousAudioInputStream(InputStream in, AudioFormat format, long length) throws IOException {
		super(in, format, length);
		buffer = new CircularBuffer(this);
	}

	@Override
	public int read() throws IOException {
		final int i;
		if (singleByte == null)
			singleByte = new byte[1];
		if (buffer.read(singleByte, 0, 1) == -1)
			i = -1;
		else
			i = singleByte[0] & 0xFF;
		return i;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return buffer.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return buffer.read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		return buffer.availableRead();
	}

	@Override
	public void close() throws IOException {
		buffer.close();
		super.close();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void mark(int limit) {}

	@Override
	public void reset() throws IOException {
		throw new IOException("mark not supported");
	}

}
