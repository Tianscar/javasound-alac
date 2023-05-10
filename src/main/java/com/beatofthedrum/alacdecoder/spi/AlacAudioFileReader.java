package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacException;
import com.beatofthedrum.alacdecoder.AlacInputStream;
import com.beatofthedrum.alacdecoder.AlacUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.alacdecoder.spi.AlacAudioFileFormatType.MP4_ALAC;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class AlacAudioFileReader extends AudioFileReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        stream.mark(1000);
        AlacContext ac = AlacUtils.AlacOpenStreamInput(new AlacContext(), stream);
        if (ac.error) {
            stream.reset();
            throwExceptions(ac);
        }
        return getAudioFileFormat(ac, new HashMap<>(), new HashMap<>());
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        AlacContext ac = AlacUtils.AlacOpenStreamInput(new AlacContext(), url.openStream());
        throwExceptions(ac);
        try {
            return getAudioFileFormat(ac, new HashMap<>(), new HashMap<>());
        }
        finally {
            AlacUtils.AlacCloseInput(ac);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), file);
        throwExceptions(ac);
        try {
            return getAudioFileFormat(ac, new HashMap<>(), new HashMap<>());
        }
        finally {
            AlacUtils.AlacCloseInput(ac);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof AlacInputStream) {
            AlacContext ac = AlacUtils.AlacOpenInput(new AlacContext(), (AlacInputStream) stream);
            throwExceptions(ac);
            return new AlacAudioInputStream(ac, getAudioFormat(ac, new HashMap<>()), AlacUtils.AlacGetNumSamples(ac));
        }
        stream.mark(1000);
        try {
            return getAudioInputStreamNoMark(stream);
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    private static AudioInputStream getAudioInputStreamNoMark(InputStream stream) throws UnsupportedAudioFileException, IOException {
        AlacContext ac = AlacUtils.AlacOpenStreamInput(new AlacContext(), stream);
        throwExceptions(ac);
        return new AlacAudioInputStream(ac, getAudioFormat(ac, new HashMap<>()), AlacUtils.AlacGetNumSamples(ac));
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        InputStream stream = url.openStream();
        try {
            return getAudioInputStreamNoMark(stream);
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), file);
        throwExceptions(ac);
        return new AlacAudioInputStream(ac, getAudioFormat(ac, new HashMap<>()), AlacUtils.AlacGetNumSamples(ac));
    }

    private static void throwExceptions(AlacContext ac) throws UnsupportedAudioFileException, IOException {
        if (ac.error) {
            if (ac.error_message instanceof AlacException) throw new UnsupportedAudioFileException();
            else if (ac.error_message instanceof IOException) throw (IOException) ac.error_message;
            else throw new IOException(ac.error_message);
        }
    }

    private static AudioFileFormat getAudioFileFormat(AlacContext ac,
                                                      Map<String, Object> fileProperties,
                                                      Map<String, Object> formatProperties) {
        int samples = AlacUtils.AlacGetNumSamples(ac);
        int sample_rate = AlacUtils.AlacGetSampleRate(ac);
        int channels = AlacUtils.AlacGetNumChannels(ac);
        int bytes_per_sample = AlacUtils.AlacGetBytesPerSample(ac);
        int bits_per_sample = AlacUtils.AlacGetBitsPerSample(ac);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        return new AudioFileFormat(MP4_ALAC,
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                channels, frameSize(channels, bits_per_sample),
                sample_rate, false, formatProperties), samples, fileProperties);
    }

    private static AudioFormat getAudioFormat(AlacContext ac, Map<String, Object> formatProperties) {
        int samples = AlacUtils.AlacGetNumSamples(ac);
        int sample_rate = AlacUtils.AlacGetSampleRate(ac);
        int channels = AlacUtils.AlacGetNumChannels(ac);
        int bytes_per_sample = AlacUtils.AlacGetBytesPerSample(ac);
        int bits_per_sample = AlacUtils.AlacGetBitsPerSample(ac);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                channels, frameSize(channels, bits_per_sample),
                sample_rate, false, formatProperties);
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

}
