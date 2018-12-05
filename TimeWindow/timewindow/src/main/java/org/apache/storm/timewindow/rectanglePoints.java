package org.apache.storm.timewindow;
import org.apache.storm.timewindow.poseXYH;

public class rectanglePoints {
	private double x[] = new double [4];
	private double y[] = new double [4];
	
	rectanglePoints(poseXYH p) {
		
	}
	
	int GetSize() {
		return 4;
	}
	
	double GetX(int i) {
		return x[i];
	}
	
	double GetY(int i) {
		return y[i];
	}
}
