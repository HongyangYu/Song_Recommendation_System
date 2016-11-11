package musicRecomSys;


public class WaveGenerator implements Runnable {
	byte data[];
	int cnt = 320;
	boolean b=true;
	public WaveGenerator() {

	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}


	@Override
	public void run() {
//		try {
//			for(int i=0; i<32; i++) System.out.print(i+"\t");
//			while (cnt==320) {
//				Thread.sleep(1000);
//				for(int i=0; i<data.length; i++){
//					System.out.print( Math.abs(data[i]) + "\t");
//				}
//				System.out.println();
//			}
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

}
