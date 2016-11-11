package musicRecomSys;


import java.io.*;
import javax.sound.sampled.*;

public class Player implements Runnable {
	SourceDataLine sourceDataLine;
	AudioFormat audioFormat;
	AudioInputStream audioInputStream;
	File file;
	AudioFileFormat aff;
	
	long totalTime = 0;
	long curPosition = 0;
	double prop = 0;
	
	public Player(File file, AudioFormat audioFormat,
			AudioInputStream audioInputStream, SourceDataLine sourceDataLine,
			AudioFileFormat aff) {
		this.file = file;
		this.audioFormat = audioFormat;
		this.audioInputStream = audioInputStream;
		this.sourceDataLine = sourceDataLine;
		this.aff = aff;
	}

	@Override
	public void run() {
		playSong();
		System.out.println("Thread PlaySong ends");
	}
	byte audioData[] = new byte[320];
	int cnt;//if cnt == -1 then end 
	public void playSong() {
		// open I/O 
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
				audioFormat, AudioSystem.NOT_SPECIFIED);
		try {
			
//			boolean first = true, b = true;
			WaveGenerator wg = new WaveGenerator();
			Thread twg = new Thread(wg);
			twg.start();
			
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			byte[] tempBuffer = new byte[320];
			while ((cnt = audioInputStream.read(tempBuffer, 0,
					tempBuffer.length)) != -1) {
				if (cnt > 0) {
					sourceDataLine.write(tempBuffer, 0, cnt);
					wg.setData(tempBuffer);
				}
				wg.setCnt(cnt);
			}
			sourceDataLine.drain();
			sourceDataLine.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void continuePlay(){
		if(cnt!=-1){
			curPosition = sourceDataLine.getMicrosecondPosition() / 1000000;
			skipTo(curPosition/totalTime);
		}
		else{
			System.err.println("error because of ending");
		}
		
	}

	public void stopSong() {
		if (sourceDataLine.isRunning()) {
			curPosition = sourceDataLine.getMicrosecondPosition() / 1000000;
			sourceDataLine.stop();
		}
		System.out.println(strTime(curPosition));

	}
	public boolean skipTo(double ratio){
		prop = ratio;
		boolean bool = false;
		try {
			long bt = (long) (aff.getByteLength() * ratio);
			audioInputStream.skip(bt);
			bool = true;
		} catch (Exception e) {
			bool = false;
			e.printStackTrace();
		}
		return bool;
	}
	
	public long getTotalTime() {
		totalTime = 0;
		try {
			aff = AudioSystem.getAudioFileFormat(file);
			if (aff.properties().containsKey("duration")) {
				totalTime = (long) Math.round((((Long) aff.properties().get(
						"duration")).longValue()) / 1000) / 1000;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return totalTime;
	}
	
	public String strTime(long time){
		long hour=0,minute = time / 60, second = time % 60;
		String str = "Time";
		if(minute>=60){
			hour = minute / 60;
			minute = minute % 60;
			str += hour + "hours";
		}
		str += minute + "minutes" + second + "seconds";
		return str;
	}
	
	public void endPlay(){
		sourceDataLine.flush();
		sourceDataLine.stop();
		sourceDataLine.close();
	}
	
}
