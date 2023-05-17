package com.beatofthedrum.alacdecoder.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.io.IOException;

import static com.beatofthedrum.alacdecoder.spi.AlacAudioFormat.Encoding.ALAC;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class AlacFormatConversionProvider extends FormatConversionProvider {

    private static final AudioFormat.Encoding[] SOURCE_ENCODINGS = new AudioFormat.Encoding[] { ALAC };
    private static final AudioFormat.Encoding[] TARGET_ENCODINGS = new AudioFormat.Encoding[] { PCM_SIGNED };

    @Override
    public AudioFormat.Encoding[] getSourceEncodings() {
        return SOURCE_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings() {
        return TARGET_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        if (sourceFormat.getEncoding().equals(ALAC)) return TARGET_ENCODINGS.clone();
        else return new AudioFormat.Encoding[0];
    }

    @Override
    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        if (sourceFormat.getEncoding().equals(ALAC)) {
            return new AudioFormat[] { getTargetFormat(sourceFormat) };
        }
        else return new AudioFormat[0];
    }

    private static AudioFormat getTargetFormat(AudioFormat sourceFormat) {
        return new AudioFormat(
                PCM_SIGNED,
                NOT_SPECIFIED,
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                NOT_SPECIFIED,
                NOT_SPECIFIED,
                sourceFormat.isBigEndian()
        );
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream) {
        return getAudioInputStream(getTargetFormat(sourceStream.getFormat()), sourceStream);
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        if (targetFormat.getEncoding() == PCM_SIGNED && sourceStream instanceof AlacAudioInputStream) {
            AudioFormat sourceFormat = sourceStream.getFormat();
            if (sourceFormat.isBigEndian() == targetFormat.isBigEndian() &&
            sourceFormat.getChannels() == targetFormat.getChannels() &&
            sourceFormat.getSampleSizeInBits() == targetFormat.getSampleSizeInBits()) {
                try {
                    return new Alac2PcmAudioInputStream((AlacAudioInputStream) sourceStream);
                }
                catch (IOException ignored) {}
            }
            throw new IllegalArgumentException("unable to convert "
                    + sourceFormat + " to "
                    + targetFormat);
        }
        else throw new IllegalArgumentException("conversion not supported");
    }

}
