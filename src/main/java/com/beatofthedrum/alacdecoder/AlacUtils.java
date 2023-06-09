/*
** AlacUtils.java
**
** Copyright (c) 2011 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/
package com.beatofthedrum.alacdecoder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.File;

public class AlacUtils
{

	public static AlacContext AlacOpenFileInput(AlacContext ac, String inputfilename)
	{
		try
		{
			return AlacOpenFileInput(ac, AlacInputStream.open(new RandomAccessFile(inputfilename, "r")));
		}
		catch (java.io.FileNotFoundException fe)
		{
			ac.error_message = new FileNotFoundException("Input file not found");
			ac.error = true;
			return (ac);
		}
	}

	public static AlacContext AlacOpenFileInput(AlacContext ac, File inputfile)
	{
		try
		{
			return AlacOpenFileInput(ac, AlacInputStream.open(new RandomAccessFile(inputfile, "r")));
		}
		catch (java.io.FileNotFoundException fe)
		{
			ac.error_message = new FileNotFoundException("Input file not found");
			ac.error = true;
			return (ac);
		}
	}

	public static AlacContext AlacOpenFileInput(AlacContext ac, ClassLoader resourceLoader, String inputfilename)
	{
		try
		{
			return AlacOpenFileInput(ac, AlacInputStream.open(resourceLoader, inputfilename));
		}
		catch (IOException ioe)
		{
			ac.error_message = new IOException("Could not load resource");
			ac.error = true;
			return (ac);
		}
	}

	public static AlacContext AlacOpenFileInput(AlacContext ac, InputStream input)
	{
		return AlacOpenFileInput(ac, AlacInputStream.open(input));
	}

    public static AlacContext AlacOpenFileInput(AlacContext ac, AlacInputStream input_stream)
    {
		int headerRead;
		QTMovieT qtmovie = new QTMovieT();
		DemuxResT demux_res = new DemuxResT();
		AlacFile alac;
		
		ac.error = false;
		
		ac.input_stream = input_stream;
		
		/* if qtmovie_read returns successfully, the stream is up to
		 * the movie data, which can be used directly by the decoder */
		try {
			headerRead = DemuxUtils.qtmovie_read(input_stream, qtmovie, demux_res);
		}
		catch (IOException e) {
			ac.error_message = e;
			ac.error = true;
			return ac;
		}

		if (headerRead == 0)
		{
			ac.error = true;
			if (demux_res.format_read == 0)
			{
				String error_message = "Failed to load the QuickTime movie headers.";
				if (demux_res.format_read != 0)
					error_message = error_message + " File type: " + DemuxUtils.SplitFourCC(demux_res.format);
				ac.error_message = new AlacException(error_message);
			}
			else
			{
				ac.error_message = new IOException("Error while loading the QuickTime movie headers.");
			}
			return (ac);
		}
		else if (headerRead == 3)
		{
			ac.error_message = new AlacException("Error when seeking to start of music data");
			ac.error = true;
			return (ac);
		}
		
		/* initialise the sound converter */
		
		alac = AlacDecodeUtils.create_alac(demux_res.sample_size, demux_res.num_channels);

		AlacDecodeUtils.alac_set_info(alac, demux_res.codecdata);

		ac.demux_res = demux_res;
		ac.alac = alac;
		
		return (ac);
			
	}
	
	public static AlacContext AlacCloseFile(AlacContext ac)
	{
		if(null != ac.input_stream)
		{
			try
			{
				ac.input_stream.close();
			}
			catch(java.io.IOException ioe)
			{
				ac.error_message = new IOException("Error when closing file");
				ac.error = true;
			}
		}
		return ac;
	}
	
	// Heres where we extract the actual music data
	
	public static int AlacUnpackSamples(AlacContext ac, int[] pDestBuffer)
	{
        int sample_byte_size;
		SampleDuration sampleinfo = new SampleDuration();
        byte[] read_buffer = ac.read_buffer;
		int destBufferSize = 1024 *24 * 3; // 24kb buffer = 4096 frames = 1 alac sample (we support max 24bps)
		int outputBytes;
		AlacInputStream inputStream = ac.input_stream;
		
		// if current_sample_block is beyond last block then finished
		
		if(ac.current_sample_block >= ac.demux_res.sample_byte_size.length)
		{
			return 0;
		}

		try {
			get_sample_info(ac.demux_res, ac.current_sample_block , sampleinfo);
		}
		catch (AlacException e) {
			ac.error = true;
			ac.error_message = e;
			// getting sample failed
			return 0;
		}

        sample_byte_size = sampleinfo.sample_byte_size;

		try {
			StreamUtils.stream_read(inputStream, sample_byte_size, read_buffer, 0);
		}
		catch (IOException e) {
			ac.error = true;
			ac.error_message = e;
			return 0;
		}
		
		/* now fetch */
		outputBytes = destBufferSize;

		outputBytes = AlacDecodeUtils.decode_frame(ac.alac, read_buffer, pDestBuffer, outputBytes);
		
		ac.current_sample_block = ac.current_sample_block + 1;
		outputBytes -= ac.offset * AlacGetBytesPerSample(ac);
        System.arraycopy(pDestBuffer, ac.offset, pDestBuffer, 0, outputBytes);
        ac.offset = 0;
        return outputBytes;
	
	}
	

	// Returns the sample rate of the specified ALAC file

    public static int AlacGetSampleRate(AlacContext ac)
    {
        if ( null != ac && ac.demux_res.sample_rate != 0)
        {
            return ac.demux_res.sample_rate;
        }
        else
        {
            return (44100);
        }
    }
	
	public static int AlacGetNumChannels(AlacContext ac)
    {
        if ( null != ac && ac.demux_res.num_channels != 0)
        {
            return ac.demux_res.num_channels;
        }
        else
        {
            return 2;
        }
    }
	
	public static int AlacGetBitsPerSample(AlacContext ac)
    {
        if (null != ac && ac.demux_res.sample_size != 0)
        {
            return ac.demux_res.sample_size;
        }
        else
        {
            return 16;
        }
    }
	

	public static int AlacGetBytesPerSample(AlacContext ac)
    {
        if ( null != ac && ac.demux_res.sample_size != 0)
        {
            return (int)Math.ceil(ac.demux_res.sample_size/8.0);
        }
        else
        {
            return 2;
        }
    }
	
	
	// Get total number of samples contained in the Apple Lossless file, or -1 if unknown

    public static int AlacGetNumSamples(AlacContext ac)
    {
		/* calculate output size */
		int num_samples = 0;
		int thissample_duration;
		int thissample_bytesize = 0;
		SampleDuration sampleinfo = new SampleDuration();
		int i;
		boolean error_found = false;
			
		for (i = 0; i < ac.demux_res.sample_byte_size.length; i++)
		{
			thissample_duration = 0;
			thissample_bytesize = 0;

			try {
				get_sample_info(ac.demux_res, ac.current_sample_block , sampleinfo);
			}
			catch (AlacException e) {
				ac.error = true;
				ac.error_message = e;
				// getting sample failed
				return (-1);
			}

			thissample_duration = sampleinfo.sample_duration;
			thissample_bytesize = sampleinfo.sample_byte_size;

			num_samples += thissample_duration;
		}
		
		return (num_samples);
	}
	

	static void get_sample_info(DemuxResT demux_res, int samplenum, SampleDuration sampleinfo) throws AlacException {
		int duration_index_accum = 0;
		int duration_cur_index = 0;

		if (samplenum >= demux_res.sample_byte_size.length)
		{
			throw new AlacException("sample " + samplenum + " does not exist ");
		}

		if (demux_res.num_time_to_samples == 0)		// was null
		{
			throw new AlacException("no time to samples");
		}
		while ((demux_res.time_to_sample[duration_cur_index].sample_count + duration_index_accum) <= samplenum)
		{
			duration_index_accum += demux_res.time_to_sample[duration_cur_index].sample_count;
			duration_cur_index++;
			if (duration_cur_index >= demux_res.num_time_to_samples)
			{
				throw new AlacException("sample " + samplenum + " does not have a duration");
			}
		}

		sampleinfo.sample_duration = demux_res.time_to_sample[duration_cur_index].sample_duration;
		sampleinfo.sample_byte_size = demux_res.sample_byte_size[samplenum];

	}

    /**
     * sets position in pcm samples
     * @param ac alac context
     * @param position position in pcm samples to go to
     */

    public static AlacContext AlacSetPosition(AlacContext ac, long position) {
        DemuxResT res = ac.demux_res;

        int current_position = 0;
        int current_sample = 0;
        SampleDuration sample_info = new SampleDuration();
        for (int i = 0; i < res.stsc.length; i++) {
            ChunkInfo chunkInfo = res.stsc[i];
            int last_chunk;

            if (i < res.stsc.length - 1) {
                last_chunk = res.stsc[i + 1].first_chunk;
            } else {
                last_chunk = res.stco.length;
            }

            for (int chunk = chunkInfo.first_chunk; chunk <= last_chunk; chunk ++) {
                int pos = res.stco[chunk - 1];
                int sample_count = chunkInfo.samples_per_chunk;
                while (sample_count > 0) {
					try {
						get_sample_info(res, current_sample, sample_info);
					}
					catch (AlacException e) {
						ac.error = true;
						ac.error_message = new IOException("Error while reading sample info");
						return ac;
					}
                    current_position += sample_info.sample_duration;
                    if (position < current_position) {
						try {
							ac.input_stream.seek(pos);
						} catch (IOException e) {
							ac.error_message = new IOException("Error when seeking");
							ac.error = true;
							return ac;
						}
						ac.current_sample_block = current_sample;
                        ac.offset =
                                (int) (position - (current_position - sample_info.sample_duration))
                                        * AlacGetNumChannels(ac);
                        return ac;
                    }
                    pos += sample_info.sample_byte_size;
                    current_sample++;
                    sample_count--;
                }
            }
        }
		return ac;
    }
}