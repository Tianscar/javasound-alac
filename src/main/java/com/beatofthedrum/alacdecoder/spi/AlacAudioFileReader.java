package com.beatofthedrum.alacdecoder.spi;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacException;
import com.beatofthedrum.alacdecoder.AlacInputStream;
import com.beatofthedrum.alacdecoder.AlacUtils;
import com.tianscar.javasound.sampled.spi.AudioResourceReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class AlacAudioFileReader extends AudioFileReader implements AudioResourceReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        final AlacContext ac;
        if (stream instanceof AlacInputStream) ac = AlacUtils.AlacOpenFileInput(new AlacContext(), (AlacInputStream) stream);
        else {
            stream.mark(1000);
            ac = AlacUtils.AlacOpenFileInput(new AlacContext(), stream);
        }
        if (ac.error) {
            if (!(stream instanceof AlacInputStream)) stream.reset();
            throwExceptions(ac);
        }
        return new AlacAudioFileFormat(ac, NOT_SPECIFIED);
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), connection.getInputStream());
        try {
            throwExceptions(ac);
            return new AlacAudioFileFormat(ac, connection.getContentLengthLong());
        }
        finally {
            AlacUtils.AlacCloseFile(ac);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), file);
        try {
            throwExceptions(ac);
            return new AlacAudioFileFormat(ac, file.length());
        }
        finally {
            AlacUtils.AlacCloseFile(ac);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof AlacInputStream) {
            AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), (AlacInputStream) stream);
            throwExceptions(ac);
            return new AlacAudioInputStream(ac, NOT_SPECIFIED);
        }
        stream.mark(1000);
        try {
            AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), stream);
            throwExceptions(ac);
            return new AlacAudioInputStream(ac, NOT_SPECIFIED);
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        try {
            AlacContext ac = AlacUtils.AlacOpenFileInput(new AlacContext(), connection.getInputStream());
            throwExceptions(ac);
            return new AlacAudioInputStream(ac, connection.getContentLengthLong());
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
        return new AlacAudioInputStream(ac, file.length());
    }

    private static void throwExceptions(AlacContext ac) throws UnsupportedAudioFileException, IOException {
        if (ac.error) {
            if (ac.error_message instanceof AlacException) throw new UnsupportedAudioFileException();
            else throw ac.error_message;
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(ClassLoader resourceLoader, String name) throws UnsupportedAudioFileException, IOException {
        return getAudioFileFormat(AlacInputStream.open(resourceLoader, name));
    }

    @Override
    public AudioInputStream getAudioInputStream(ClassLoader resourceLoader, String name) throws UnsupportedAudioFileException, IOException {
        return getAudioInputStream(AlacInputStream.open(resourceLoader, name));
    }

}
