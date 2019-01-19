package org.apache.storm.YSTimeMatrix;

public class PbInfo {
	public int x;
	public int y;
	public double rate;
	
	public PbInfo(String pbinfo) {
		SetPbInfo(pbinfo);
	}
	
	public PbInfo(int x, int y, double rate) {
		SetPbInfo(x, y, rate);
	}
	
	public void SetPbInfo(int x, int y, double rate) {
		this.x = x;
		this.y = y;
		this.rate = rate;
	}
	
	public void SetPbInfo(String pbinfo) {
		String[] keys = pbinfo.split(",");
		Integer.valueOf(keys[0].toString());
		this.x = Integer.valueOf(keys[0].toString()).intValue();
		this.y = Integer.valueOf(keys[1].toString()).intValue();
		this.rate = Double.valueOf(keys[2].toString()).doubleValue();
	}
	
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(rate); // String.format("%.6f", rate);
	}
	
}
