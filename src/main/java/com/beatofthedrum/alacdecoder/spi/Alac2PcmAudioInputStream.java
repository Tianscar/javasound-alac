package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacUtils;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

class Alac2PcmAudioInputStream extends AsynchronousAudioInputStream {

    private final AlacContext ac;

    static final int PCM_BUFFER_SIZE = 65536;
    static final int DST_BUFFER_SIZE = 1024 * 24 * 3; // 24kb buffer = 4096 frames = 1 alac sample (we support max 24bps)

    private final byte[] pcmBuffer;
    private final int[] dstBuffer;
    private final int bps;

    Alac2PcmAudioInputStream(AlacAudioInputStream stream) throws IOException {
        this(stream.getAlacContext(), getPCMFormat(stream.getFormat()), stream.getFrameLength());
    }

    private Alac2PcmAudioInputStream(AlacContext ac, AudioFormat format, long length) throws IOException {
        super(ac.input_stream, format, length);
        this.ac = ac;

        pcmBuffer = new byte[PCM_BUFFER_SIZE];
        dstBuffer = new int[DST_BUFFER_SIZE];

        bps = AlacUtils.AlacGetBytesPerSample(ac);
    }

    private static AudioFormat getPCMFormat(AudioFormat sourceFormat) {
        return new AudioFormat(
                PCM_SIGNED,
                sourceFormat.getSampleRate(),
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                sourceFormat.getFrameSize(),
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian(),
                sourceFormat.properties()
        );
    }

    @Override
    public void execute() {
        try {
            int bytes_unpacked = AlacUtils.AlacUnpackSamples(ac, dstBuffer);
            if (ac.error) throw ac.error_message;

            if (bytes_unpacked > 0) {
                format_samples(pcmBuffer, bps, dstBuffer, bytes_unpacked);
                buffer.write(pcmBuffer, 0, bytes_unpacked);
            } else if (bytes_unpacked == 0) throw new EOFException();
        }
        catch (IOException e) {
            buffer.close();
        }
    }

    // Reformat samples from longs in processor's native endian mode to
    // little-endian data with (possibly) less than 3 bytes / sample.
    private static void format_samples(byte[] dst, int bps, int[] src, int samcnt) {
        int temp;
        int counter = 0;
        int counter2 = 0;

        switch (bps) {
            case 1:
                while (samcnt > 0) {
                    dst[counter] =  (byte) (0x00FF & (src[counter] + 128));
                    counter ++;
                    samcnt --;
                }
                break;
            case 2:
                while (samcnt > 0) {
                    temp = src[counter2];
                    dst[counter] = (byte) temp;
                    counter ++;
                    dst[counter] = (byte) (temp >>> 8);
                    counter ++;
                    counter2 ++;
                    samcnt = samcnt - 2;
                }
                break;
            case 3:
                while (samcnt > 0) {
                    dst[counter] = (byte) src[counter2];
                    counter ++;
                    counter2 ++;
                    samcnt --;
                }
                break;
        }

    }

}
