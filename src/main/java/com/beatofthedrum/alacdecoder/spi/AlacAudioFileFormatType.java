package com.beatofthedrum.alacdecoder.spi;

import javax.sound.sampled.AudioFileFormat;

public class AlacAudioFileFormatType {

    private AlacAudioFileFormatType() {
        throw new UnsupportedOperationException();
    }

    public static final AudioFileFormat.Type MP4_ALAC = new AudioFileFormat.Type("MPEG-4 ALAC", "m4a");

}
