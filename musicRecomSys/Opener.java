package musicRecomSys;


import java.io.*;
import java.util.Scanner;
import javax.sound.sampled.*;

public class Opener {

	SourceDataLine sourceDataLine;
	AudioInputStream audioInputStream;
	AudioFormat audioFormat;
	AudioFileFormat aff;

	File file;
	String str_file;

	public Opener(String str_file) {
		this.str_file = str_file;
		file = new File(str_file);
		musicTranslate();
	}

	public void musicTranslate() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
			audioFormat = audioInputStream.getFormat();

			AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;// ��Ƶ���뼼��
			float sampleRate = audioFormat.getSampleRate();// 
			int sampleSizeInBits = 16;
			int channels = audioFormat.getChannels();// 1: sigle track��2: stereo)
			int frameSize = channels * 2;
			float frameRate = audioFormat.getSampleRate();
			boolean bigEndian = false;

			if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {

				audioFormat = new AudioFormat(encoding, sampleRate,
						sampleSizeInBits, channels, frameSize, frameRate,
						bigEndian);

				audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
						audioInputStream);
			}
			aff = AudioSystem.getAudioFileFormat(file);
			Controller.starter();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

}
