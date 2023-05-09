package com.beatofthedrum.alacdecoder.test;

import com.beatofthedrum.alacdecoder.AlacContext;
import com.beatofthedrum.alacdecoder.AlacUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class PlayerExample {

	static SourceDataLine output_stream;
	static int output_opened;
	static String input_file_n = "src/test/resources/fbodemo1.m4a";

    // Reformat samples from longs in processor's native endian mode to
    // little-endian data with (possibly) less than 3 bytes / sample.

    public static byte[] format_samples(int bps, int[] src, int samcnt) {
        int temp = 0;
        int counter = 0;
        int counter2 = 0;
        byte[] dst = new byte[65536];

        switch (bps)
        {
            case 1:
                while (samcnt > 0)
                {
                    dst[counter] =  (byte)(0x00FF & (src[counter] + 128));
                    counter++;
                    samcnt--;
                }
				break;

			case 2:
				while (samcnt > 0)
				{
					temp = src[counter2];
					dst[counter] =  (byte)temp;
					counter++;
					dst[counter] =  (byte)(temp >>> 8);
					counter++;
					counter2++;
					samcnt = samcnt - 2;
                }
				break;

            case 3:
                while (samcnt > 0)
                {
                    dst[counter] =  (byte)src[counter2];
                    counter++;
                    counter2++;
                    samcnt--;
                }
				break;
        }

        return dst;
    }

	static void GetBuffer(AlacContext ac)
	{
		int destBufferSize = 1024 *24 * 3; // 24kb buffer = 4096 frames = 1 alac sample (we support max 24bps)
		byte[] pcmBuffer = new byte[65536];
		int total_unpacked_bytes = 0;
		int bytes_unpacked;
		
		int[] pDestBuffer = new int[destBufferSize]; 

		int bps = AlacUtils.AlacGetBytesPerSample(ac);
		
		
		while (true)
		{
			bytes_unpacked = AlacUtils.AlacUnpackSamples(ac, pDestBuffer);

			total_unpacked_bytes += bytes_unpacked;

			if (bytes_unpacked > 0)
			{
				pcmBuffer = format_samples(bps, pDestBuffer, bytes_unpacked);
				output_stream.write(pcmBuffer, 0, bytes_unpacked);
			}

			if (bytes_unpacked == 0)
				break;
		} // end of while
		

	}

	public static void main(String [] args) {
		AlacContext ac;
		int output_size;
		int total_samples; 
		int sample_rate;
        int num_channels;
		int byteps;
		int bitps;

		output_opened = 0;
		
		ac = AlacUtils.AlacOpenFileInput(new AlacContext(), input_file_n);
		
		if (ac.error) {
            System.err.println("Sorry an error has occured");
            System.err.println(ac.error_message);
            System.exit(1);
        }
		
		num_channels = AlacUtils.AlacGetNumChannels(ac);

        total_samples = AlacUtils.AlacGetNumSamples(ac);

        byteps = AlacUtils.AlacGetBytesPerSample(ac);
		
		sample_rate = AlacUtils.AlacGetSampleRate(ac);
		
		bitps = AlacUtils.AlacGetBitsPerSample(ac);

		AudioFormat audioFormat = new AudioFormat(sample_rate, bitps, num_channels, true, false);

		try
		{
			output_stream = AudioSystem.getSourceDataLine(audioFormat);
			output_stream.open();
			output_stream.start();
			output_opened = 1;
		}
		catch(LineUnavailableException e)
		{
			System.out.println("Cannot open output line with audio format: " + audioFormat + " : Error : " + 3);
			output_opened = 0;
			System.exit(1);
		}

		/* will convert the entire buffer */
		GetBuffer(ac);

		AlacUtils.AlacCloseInput(ac);

		if (output_opened != 0)
		{
			output_stream.drain();
			output_stream.stop();
			output_stream.close();
		}
	}

}

