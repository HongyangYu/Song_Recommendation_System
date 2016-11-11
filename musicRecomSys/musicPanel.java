package musicRecomSys;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * 
 * It should be able to play mp3 format files. 
 * But to import a jar file, mp3spi, is needed.
 * So now this player can only play wav format files.
 * This is the music panel, you can add the panel to your frame.
 * Don't forget to change the path.
 * */ 




public class musicPanel extends JPanel {
	private String filename, filepath = "src/musicrecommendersystemtest/music/musicPlayer/";
	private float rate;
	private JPanel panel;
	private List list;
	private AudioInputStream audioInputStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;
	private DataLine.Info dataLineInfo;
	private JButton JB_go, JB_pause;

	JTextField tf;
	String text = "Current BMP: ";

	ChangeSpec cs;
	Spectrum spec;
	private boolean hasStop = true, isStop = true, halt = false,
			change = false;

	public musicPanel() {
		this.setLocation(500, 150);
		this.setLayout(new BorderLayout());
		this.setSize(500, 600);
		
		
		
		this.setLayout(new BorderLayout());

		JB_go = new JButton("Continue");
		JB_pause = new JButton("Pause");

		// Pause
		JB_pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				halt = true;
				JB_go.setEnabled(true);
				JB_pause.setEnabled(false);
			}
		});
		// Continue
		JB_go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				halt = false;
				JB_go.setEnabled(false);
				JB_pause.setEnabled(true);
			}
		});

	

		JB_go.setEnabled(false);
		JB_pause.setEnabled(false);


		// �����б�
		list = new List(15);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					filename = list.getSelectedItem();
					System.out.println(filepath + filename);
					JB_pause.setEnabled(true);
					play();

				}
			}
		});

		File filedir = new File(filepath);
		File[] filelist = filedir.listFiles();
		for (File file : filelist) {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(".wav") || filename.endsWith(".mp3")) {
				list.add(filename);
			}
		}

		this.add(list, "Center");
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
//		JLabel labelplay = new JLabel("Music Player");
//		panel.add(labelplay, "Center");
		JPanel JP_JB = new JPanel();
		JP_JB.add(JB_go);
		JP_JB.add(JB_pause);
		panel.add(JP_JB, "South");
		panel.setOpaque(false);
		this.add(panel, "North");

		spec = new Spectrum();
		cs = new ChangeSpec(spec);
		this.add(spec, "South");
		

	}


	private void play() {
		try {
			isStop = true;// stop if true

			while (!hasStop) {
				System.out.print(".");
				try {
					Thread.sleep(10);
				} catch (Exception e) {
				}
			}

			File file = new File(filepath + filename);
			audioInputStream = AudioSystem.getAudioInputStream(file);
			audioFormat = audioInputStream.getFormat();
			
			if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				if (change) {
					audioFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED, rate, 16,
							audioFormat.getChannels(),
							audioFormat.getChannels() * 2, rate, false);
				} else {
					rate = audioFormat.getSampleRate();
					audioFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							audioFormat.getSampleRate(), 16,
							audioFormat.getChannels(),
							audioFormat.getChannels() * 2, rate, false);
				}

				audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
						audioInputStream);
			}

			System.out.println(audioFormat.getFrameSize());
			System.out.println(audioFormat.getSampleRate());
			System.out.println(audioFormat.toString());

			// �������?
			dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
					AudioSystem.NOT_SPECIFIED);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			// ���������߳̽��в���
			isStop = false;
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PlayThread extends Thread {
		byte tempBuffer[] = new byte[320];

		public void run() {
			try {
				new Thread(cs).start();// cs�߳̿�

				int cnt;
				hasStop = false;

				// ��ȡ���ݵ���������
				while ((cnt = audioInputStream.read(tempBuffer, 0,
						tempBuffer.length)) != -1) {
					if (isStop)
						break;

					// ��ͣ��ָ�?
					while (true) {
						if (halt) {
							try {
								Thread.sleep(10);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							break;
						}
					}
					if (cnt > 0) {
						sourceDataLine.write(tempBuffer, 0, cnt);
						cs.setData(tempBuffer);
					}
				}
				sourceDataLine.drain();
				sourceDataLine.close();
				hasStop = true;

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

}
