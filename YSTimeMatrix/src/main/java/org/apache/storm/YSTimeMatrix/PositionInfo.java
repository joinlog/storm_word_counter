package org.apache.storm.YSTimeMatrix;

public class PositionInfo {
	public int id; // AGV 信息表示的是agvid； 如果是任务信息，则任务起点信息表示的是任务id；
	public int x;
	public int y;
	public int degree;
	public String type; //"PB", "WS","QC"

	public PositionInfo(int id, int x, int y, int degree, String type) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.degree = degree;
		this.type = type;
	}

	public PositionInfo(String positionString) {
		ParsePositionInfo(positionString);
	}
	public void ParsePositionInfo(String positionString) {
		String[] keys = positionString.split(":");
		this.id = Integer.valueOf(keys[0].toString()).intValue();
		this.x = Integer.valueOf(keys[1].toString()).intValue();
		this.y = Integer.valueOf(keys[2].toString()).intValue();
		this.degree = Integer.valueOf(keys[3].toString()).intValue();
		this.type = keys[4];
	}
	
	public String toString() {
		return String.valueOf(id) + ":" + String.valueOf(x) + ":" + String.valueOf(y) + ":" + String.valueOf(degree) + ":" + type;
	}
}
