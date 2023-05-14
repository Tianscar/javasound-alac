/*
** StreamUtils.java
**
** Copyright (c) 2011 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.alacdecoder;

import java.io.IOException;

class StreamUtils
{

    public static void stream_read(AlacInputStream stream, int size, int[] buf, int startPos) throws IOException {
        byte[] byteBuf = new byte[size];
        int bytes_read = stream_read(stream, size, byteBuf, 0);
        for(int i=0; i < bytes_read; i++) {
			buf[startPos + i] = byteBuf[i];
		}
    }

	public static int stream_read(AlacInputStream stream, int size, byte[] buf, int startPos) throws IOException {
        return stream.read(buf, startPos, size);
	}

	public static int stream_read_uint32(AlacInputStream stream) throws IOException {
		int v;
		int tmp;
		byte[] bytebuf = stream.read_buf;

		stream.read(bytebuf, 0, 4);
		tmp =  (bytebuf[0] & 0xff);

		v = tmp << 24;
		tmp =  (bytebuf[1] & 0xff);

		v = v | (tmp << 16);
		tmp =  (bytebuf[2] & 0xff);

		v = v | (tmp << 8);

		tmp =  (bytebuf[3] & 0xff);
		v = v | tmp;
		
		return v;
	}

	public static int stream_read_int16(AlacInputStream stream) throws IOException {
		return stream.readShort();
	}

	public static int stream_read_uint16(AlacInputStream stream) throws IOException {
		int v;
		int tmp;
		byte[] bytebuf = stream.read_buf;

		stream.read(bytebuf, 0, 2);
		tmp =  (bytebuf[0] & 0xff);
		v = tmp << 8;
		tmp =  (bytebuf[1] & 0xff);

		v = v | tmp;

		return v;
	}

	public static int stream_read_uint8(AlacInputStream stream) throws IOException {
		int v;
		byte[] bytebuf = stream.read_buf;

		stream.read(bytebuf, 0, 1);
		v =  (bytebuf[0] & 0xff);
			
		return v;
	}

	public static void stream_skip(AlacInputStream stream, int skip) throws IOException {

		if (skip < 0) {
			stream.seek(stream.offset() + skip);
			return;
		}

		stream.skipBytes(skip);
    }

	public static int stream_eof(AlacInputStream stream) throws IOException {
		return stream.available() == 0 ? 1 : 0;
	}

	public static int stream_tell(AlacInputStream stream) throws IOException {
		return stream.offset();
	}

	public static int stream_setpos(AlacInputStream stream, int pos)
	{
		try {
			stream.seek(pos);
			return 0;
		} catch (IOException e) {
			return -1;
		}
	}

}

