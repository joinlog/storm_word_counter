package org.apache.storm.timewindow;
import org.apache.storm.timewindow.poseXYH;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.timewindow.Vector2DM;

public class rectanglePoints {
	private int x[] = new int [4];
	private int y[] = new int [4];
	private int centerx;
	private int centery;
	private int radius;
	
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
		
		centerx = p.getX();
		centery = p.getY();
		radius = (int)Math.sqrt(Math.pow(halfWidth, 2) + Math.pow(halfHeight, 2));
	}
	
	public int GetCenterX() {
		return centerx;
	}
	public int GetCenterY() {
		return centery;
	}
	public int GetRadius() {
		return radius;
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
	
	public List<Vector2DM> GetVectors() {
		List<Vector2DM> mVectors = new ArrayList<Vector2DM>();
		mVectors.add(new Vector2DM(x[3], y[3], x[0], y[0]));
		for (int i = 1; i < 4; ++i) {
			mVectors.add(new Vector2DM(x[i-1], y[i-1], x[i], y[i]));
		}
		return mVectors;
	}
	

	public int GetMinMaxValueAfterProject(int x, int y, Vector2DM vec) {
		return vec.VectorDot(new Vector2DM(0, 0, x, y));
	}
	
	// [min, max]
	public int[] GetMinMaxValueAfterProject(Vector2DM vec) {
		int [] val2 = new int[2];
		for (int i = 0; i < 4; i++) {
			int valRet = GetMinMaxValueAfterProject(x[i], y[i], vec);
			if (i == 0) {
				val2[0] = valRet;
				val2[1] = valRet;
			} else {
				if (val2[0] > valRet) {
					val2[0] = valRet;
				}
				if (val2[1] < valRet) {
					val2[1] = valRet;
				}
			}
		}
		return val2;
	}
}
