package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.alacdecoder.spi.AlacAudioFormat.Encoding.ALAC;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class AlacAudioFormat extends AudioFormat {

    public static class Encoding extends AudioFormat.Encoding {
        public static final AudioFormat.Encoding ALAC = new Encoding("ALAC");
        private Encoding(String name) {
            super(name);
        }
    }

    public AlacAudioFormat(AlacContext ac) {
        super(
                ALAC,
                AlacUtils.AlacGetSampleRate(ac),
                AlacUtils.AlacGetBitsPerSample(ac),
                AlacUtils.AlacGetNumChannels(ac),
                frameSize(AlacUtils.AlacGetNumChannels(ac), AlacUtils.AlacGetBitsPerSample(ac)),
                AlacUtils.AlacGetSampleRate(ac),
                false,
                generateProperties(ac)
        );
    }

    private static Map<String, Object> generateProperties(AlacContext ac) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("bitrate", AlacUtils.AlacGetBitsPerSample(ac) * AlacUtils.AlacGetSampleRate(ac));
        return properties;
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

    @Override
    public String toString() {
        String sEndian = "";
        if (getEncoding().equals(ALAC) && ((getSampleSizeInBits() > 8)
                || (getSampleSizeInBits() == AudioSystem.NOT_SPECIFIED))) {
            if (isBigEndian()) {
                sEndian = "big-endian";
            } else {
                sEndian = "little-endian";
            }
        }
        return super.toString() + sEndian;
    }

}
