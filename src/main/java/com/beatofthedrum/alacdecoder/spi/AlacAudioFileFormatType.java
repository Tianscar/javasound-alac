package com.beatofthedrum.alacdecoder.spi;

import javax.sound.sampled.AudioFileFormat;

public class AlacAudioFileFormatType extends AudioFileFormat.Type {

    public static final AudioFileFormat.Type MP4_ALAC = new AlacAudioFileFormatType("MPEG-4 ALAC", "m4a");

    private AlacAudioFileFormatType(String name, String extension) {
        super(name, extension);
    }

}
