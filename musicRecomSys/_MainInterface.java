package musicRecomSys;

import java.awt.*;
import javax.swing.*;


public class _MainInterface extends JFrame {
	private static final long serialVersionUID = 10L;

	public _MainInterface() {
            try{
                System.out.println("here1");
		this.setTitle("Music Player");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                System.out.println("here2");
		this.setLocation(500, 150);
                System.out.println("here3");
		this.setLayout(new BorderLayout());
                System.out.println("here4");
		this.setSize(500, 600);
		System.out.println("here5");
		
		//these is the music panel, you can add the panel to your frame
		this.add(new musicPanel());
		System.out.println("here6");
		this.setVisible(true);
                System.out.println("herelast");
            }
            catch(Exception e){
                System.out.println("Yup, caught the exception here!");
                System.out.println("Exception : " + e);
            }
	}

	public void mainnew() {
            System.out.println("Was this called!! inside Mainnew");
		new _MainInterface();
	}
}
