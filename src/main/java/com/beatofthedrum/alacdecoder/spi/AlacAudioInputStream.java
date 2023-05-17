package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;

import javax.sound.sampled.AudioInputStream;

class AlacAudioInputStream extends AudioInputStream {

    private final AlacContext ac;

    AlacAudioInputStream(AlacContext ac, long length) {
        super(ac.input_stream, new AlacAudioFormat(ac), length);
        this.ac = ac;
    }

    public AlacContext getAlacContext() {
        return ac;
    }

}
