package musicRecomSys;

import java.io.File;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public class Controller implements Runnable {
	static final int start = 1;//play
	static final int skip = 2;
	static final int pause = 3;
	static final int continued = 4;
	static SourceDataLine sourceDataLine;
	static AudioFormat audioFormat;
	static AudioInputStream audioInputStream;
	static File file;
	static AudioFileFormat aff;
	
	public Controller(File file, AudioFormat audioFormat,
			AudioInputStream audioInputStream, SourceDataLine sourceDataLine,
			AudioFileFormat aff) {
		this.file = file;
		this.audioFormat = audioFormat;
		this.audioInputStream = audioInputStream;
		this.sourceDataLine = sourceDataLine;
		this.aff = aff;
	}

	
	public static void starter() {
		Scanner scan = new Scanner(System.in);
		Player player = new Player(file, audioFormat, audioInputStream,
				sourceDataLine, aff);
		

		Thread t1 = new Thread(player);
		
		int choice = -1;
		while (choice != 0) {
			System.out.println("choose: 1.start; 2.skip; 3.pause;\n"
					+ " 4.continue;0.exit;");
			choice = scan.nextInt();
			switch (choice) {
			// play songs
			case 1:
				t1.start();
				if(player.cnt==-1){
					try {
						t1.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
				break;
			case 2:
				System.out.println("input ratio:");
				double ratio = scan.nextDouble();
				player.skipTo(ratio);
				break;
			case 3:
				player.stopSong();
				break;
			case 4:
				player.continuePlay();
			case 5:
				player.endPlay();
				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				t1.start();
				break;
			case 6:
//				System.out.println(player.strTime(player.curPosition));
				System.out.println(player.strTime(player.getTotalTime()));
				break;
			case 0://may be a little latency, about 1~2 seconds
				player.endPlay();
				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				break;
			}
		}

	}


	@Override
	public void run() {
		starter();
	}
}
