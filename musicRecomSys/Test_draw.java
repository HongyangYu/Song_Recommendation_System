package musicRecomSys;

import javax.swing.*;

public class Test_draw {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Frequency Spectrum");

		Spectrum spec = new Spectrum();

		frame.getContentPane().add(spec);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		
		ChangeSpec cs = new ChangeSpec(spec);
		Thread td = new Thread(cs);
		td.start();
	}

}
