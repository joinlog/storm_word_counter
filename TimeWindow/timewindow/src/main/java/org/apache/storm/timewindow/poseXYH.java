package org.apache.storm.timewindow;

import java.io.Serializable;

public class poseXYH implements Serializable {

	private int x; // cm
	private int y; // cm
	private int h;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public String toString() {
		return "poseXYH [x=" + x + ", y=" + y + ", h=" + h + "]";
	}
	public poseXYH() {
		super();
		this.x = 0;
		this.y = 0;
		this.h = 0;
	}
	public poseXYH(int x, int y, int h) {
		super();
		this.x = x;
		this.y = y;
		this.h = h;
	}
}
