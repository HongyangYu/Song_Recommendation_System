package musicRecomSys;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;

public class Spectrum extends JPanel {

	private static final long serialVersionUID = 9062919348972518813L;
	final int BAND = 32; // 32bands
	final int WIDTH = 383, HEIGHT = 124; // spectrum panel size 383x124
	final int B_WIDTH = 9;// spectrum band width
	BufferedImage barImage;// specturm band image
	Graphics gf[];

	byte b_height[];
	int b_x = 0, b_y = 0;

	public Spectrum() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.white);
		gf = new Graphics[32];
		b_height = new byte[32];
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(new Color(0x2E,0x9A,0xFE));
		for (int i = 0; i < BAND; i++) {
			g.fillRect(b_x + i * B_WIDTH+1, HEIGHT - b_height[i], B_WIDTH,
					b_height[i]);// b_y+b_height
			g.draw3DRect(b_x + i * B_WIDTH + 1, HEIGHT - b_height[i] + 1,
					B_WIDTH + 1, b_height[i] + 1, true);
		}
	}

	public byte[] getB_height() {
		return b_height;
	}

	public void setB_height(byte b_height[]) {
		this.b_height = b_height;
	}

}
