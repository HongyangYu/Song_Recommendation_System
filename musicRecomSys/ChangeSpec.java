package musicRecomSys;
public class ChangeSpec implements Runnable {

	byte data[];
	Spectrum spec;

	public ChangeSpec(Spectrum spec) {
		this.spec = spec;
		data = new byte[32];
	}

	public void setData(byte data[]) {
		this.data = data;
		for(int i=0; i<data.length; i++){
			this.data[i]=(byte) Math.abs(data[i]);
		}
	}

	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(200);
				spec.setB_height(data);
				spec.repaint();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
