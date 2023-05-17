package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacUtils;

import javax.sound.sampled.AudioFileFormat;

import static com.beatofthedrum.alacdecoder.spi.AlacAudioFileFormat.Type.MP4_ALAC;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class AlacAudioFileFormat extends AudioFileFormat {

    public static class Type extends AudioFileFormat.Type {
        public static final AudioFileFormat.Type MP4_ALAC = new Type("MPEG-4 ALAC", "m4a");
        private Type(String name, String extension) {
            super(name, extension);
        }
    }

    public AlacAudioFileFormat(AlacContext ac, long byteLength) {
        super(MP4_ALAC, (int) byteLength, new AlacAudioFormat(ac), AlacUtils.AlacGetNumSamples(ac));
    }

}
