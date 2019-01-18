package org.apache.storm.YSTimeMatrix;

public class AGVTaskResult {
	int agv;
	int task;
	int score;
	public AGVTaskResult(String agvTaskScore) {
		ParseAGVTaskResult(agvTaskScore);
	}
	
	public AGVTaskResult(int agv, int task, int score) {
		this.agv = agv;
		this.task = task;
		this.score = score;
	}
	
	public void ParseAGVTaskResult(String agvTaskScore) {
		String[] keys = agvTaskScore.split(":");
		this.agv = Integer.valueOf(keys[0].toString()).intValue();
		this.task = Integer.valueOf(keys[0].toString()).intValue();
		this.score = Integer.valueOf(keys[0].toString()).intValue();
	}
	
	public String toString() {
		return String.valueOf(agv) + ":" + String.valueOf(task) + ":" + String.valueOf(score);
	}
}
