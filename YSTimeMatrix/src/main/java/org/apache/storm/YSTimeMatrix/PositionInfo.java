package org.apache.storm.YSTimeMatrix;

public class PositionInfo {
	public int agvTaskId; // AGV 信息表示的是agvid； 如果是任务信息，则任务起点信息表示的是任务id；
	public int id; // type是"PB", "WS","QC" 的id；
	public int x;
	public int y;
	public int degree;
	public String type; //"PB", "WS","QC"

	public PositionInfo(int agvTaskId, int id, int x, int y, int degree, String type) {
		this.agvTaskId =  agvTaskId;
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
		this.agvTaskId = Integer.valueOf(keys[0].toString()).intValue();
		this.id = Integer.valueOf(keys[1].toString()).intValue();
		this.x = Integer.valueOf(keys[2].toString()).intValue();
		this.y = Integer.valueOf(keys[3].toString()).intValue();
		this.degree = Integer.valueOf(keys[4].toString()).intValue();
		this.type = keys[5];
	}
	
	public String toString() {
		return String.valueOf(agvTaskId) + ":" + String.valueOf(id) + ":" + String.valueOf(x) + ":" + String.valueOf(y) + ":" + String.valueOf(degree) + ":" + type;
	}
}
