package com.beatofthedrum.alacdecoder.test;

import com.beatofthedrum.alacdecoder.cli.DecoderDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CLITest {

    @Test
    @DisplayName("mp4 -> wav")
    public void decode() {
        DecoderDemo.main(new String[] {"src/test/resources/fbodemo1.m4a", "fbodemo1.wav"});
    }

}
