package org.apache.storm.timewindow;
import org.apache.storm.timewindow.poseXYH;

public class rectanglePoints {
	private int x[] = new int [4];
	private int y[] = new int [4];
	
	rectanglePoints(poseXYH p, int halfHeight, int halfWidth) {
		int halfX = 0;
		int halfY = 0;
		if (p.getH() == 0) {
			halfX = halfHeight;
			halfY = halfWidth;
		} else {

			halfY = halfHeight;
			halfX = halfWidth;
		}
		
		x[0] = p.getX() - halfX;
		x[1] = x[0];
		x[2] = p.getX() + halfX;
		x[3] = x[2];
		
		y[0] = p.getY() - halfY;
		y[1] = p.getY() + halfY;
		y[2] = y[1];
		y[3] = y[0];
	}
	
	public int GetSize() {
		return 4;
	}
	
	public int GetX(int i) {
		return x[i];
	}
	
	public int GetY(int i) {
		return y[i];
	}
	
	public String toString() {
		return "rect:" + toString(x[0], y[0]) + "," + toString(x[1], y[1]) + "," + toString(x[2], y[2]) + "," + toString(x[3], y[3]) + ";"; 
	}
	
	public String toString(int x1, int y1) {
		return "(" + String.valueOf(x1) + "," + String.valueOf(y1) + ")";
	}
	
}
