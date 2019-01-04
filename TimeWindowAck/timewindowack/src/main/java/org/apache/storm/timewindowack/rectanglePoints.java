package org.apache.storm.timewindowack;
import org.apache.storm.timewindowack.poseXYH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.storm.timewindowack.Vector2DM;

public class rectanglePoints  implements Serializable {
	private int x[] = new int [4];
	private int y[] = new int [4];
	private int centerx;
	private int centery;
	private int radius;
	
	public String toString() {
		return "poseXYH x[" + x[0] + ","+ x[1] + ","+ x[2] + ","+ x[3] + "],y["+ y[0] + ","+ y[1] + ","+ y[2] + ","+ y[3] + "],centerx=" + centerx + ",centery=" + centery +",radius=" + radius;
	}
	public rectanglePoints(int x1,int x2,int x3,int x4,int y1,int y2,int y3,int y4,int centerx, int centery, int radius) {
		super();
		this.x[0] = x1;
		this.x[1] = x2;
		this.x[2] = x3;
		this.x[3] = x4;
		this.y[0] = y1;
		this.y[1] = y2;
		this.y[2] = y3;
		this.y[3] = y4;
		this.centerx = centerx;
		this.centery = centery;
		this.radius = radius;
	}
	
	public rectanglePoints(poseXYH p, int halfHeight, int halfWidth) {
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
	
//	public String toString() {
//		return "rect:" + toString(x[0], y[0]) + "," + toString(x[1], y[1]) + "," + toString(x[2], y[2]) + "," + toString(x[3], y[3]) + ";"; 
//	}
//	
//	public String toString(int x1, int y1) {
//		return "(" + String.valueOf(x1) + "," + String.valueOf(y1) + ")";
//	}
	
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
