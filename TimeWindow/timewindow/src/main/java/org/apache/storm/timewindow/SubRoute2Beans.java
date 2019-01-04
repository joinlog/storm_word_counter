package org.apache.storm.timewindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.timewindow.poseXYH;
import org.apache.storm.timewindow.rectanglePoints;

/**
 * @author zpmc
 * 接收路径对，将其转为Beans列表，将两个列表中的Beans两两一对发送到下一个blot处理
 */
public class SubRoute2Beans extends BaseBasicBolt {

	private static int agvLength = 1500;
	private static int agvWidth = 400;
	
	public void prepare(Map stormConf, TopologyContext context) {
		System.out.println("SubRoute2Beans prepare start");
	}

	
    public void execute(Tuple arg0, BasicOutputCollector collector) {
//        String word = (String) arg0.getValue(0);
//        String out = "Hello " + word + "!";
//        System.out.println(out);
    	System.out.println("SubRoute2Beans execute start");
        List<poseXYH> agvRt0 = (List<poseXYH>) arg0.getValue(0);
        List<poseXYH> agvRt1 = (List<poseXYH>) arg0.getValue(1);
        List<rectanglePoints> rectPts0 = route2RectPoints(agvRt0);
        List<rectanglePoints> rectPts1 = route2RectPoints(agvRt1);
        for (int i = 0; i < 2/*rectPts0.size()*/; i++) {
        	for (int j = 0; j < 2/*rectPts1.size()*/; j++) {
        		collector.emit(new Values(rectPts0.get(i), rectPts1.get(j)));
    		}
		}
        System.out.println("SubRoute2Beans execute end");
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
    	arg0.declare(new Fields("rectPts1", "rectPts2"));
    }
    

    
     // 垂直的
    private List<poseXYH> line2PointsVertical(int min1, int max1, int value2) {
    	List<poseXYH> mPts = new ArrayList<poseXYH>();

		int maxValue = max1 - agvLength / 2;
		int minValue = min1 + agvLength / 2;
		if (maxValue <= minValue) {
			poseXYH mPose = new poseXYH();
			mPose.setX(value2);
			mPose.setY((minValue + maxValue) / 2);
			mPose.setH(90);
			mPts.add(mPose);
		} else {
			for (; minValue < maxValue; minValue += 10) {
				poseXYH mPose = new poseXYH();
				mPose.setX(value2);
				mPose.setY(minValue);
				mPose.setH(90);
				mPts.add(mPose);
			}
			poseXYH mPose = new poseXYH();
			mPose.setX(value2);
			mPose.setY(maxValue);
			mPose.setH(90);
			mPts.add(mPose);
		}
		return mPts;
    }
    
    // 水平的
    private List<poseXYH> line2PointsHorizontal(int min1, int max1, int value2) {
    	List<poseXYH> mPts = new ArrayList<poseXYH>();

		int maxValue = max1 - agvLength / 2;
		int minValue = min1 + agvLength / 2;
		if (maxValue <= minValue) {
			poseXYH mPose = new poseXYH();
			mPose.setY(value2);
			mPose.setX((minValue + maxValue) / 2);
			mPose.setH(0);
			mPts.add(mPose);
		} else {
			for (; minValue < maxValue; minValue += 10) {
				poseXYH mPose = new poseXYH();
				mPose.setY(value2);
				mPose.setX(minValue);
				mPose.setH(0);
				mPts.add(mPose);
			}
			poseXYH mPose = new poseXYH();
			mPose.setY(value2);
			mPose.setX(maxValue);
			mPose.setH(0);
			mPts.add(mPose);
		}
		return mPts;
    }
    
    // 一段子路径转换成路径上的点
    private List<poseXYH> route2Points(poseXYH pose1, poseXYH pose2) {
    	
    	if (Math.abs(pose1.getX() - pose2.getX()) < 1E-6) { // 垂直方向
    		
    		return line2PointsVertical(Math.min(pose1.getY(), pose2.getY()), Math.max(pose1.getY(), pose2.getY()), pose1.getX());
    	} else { // 水平方向
    		return line2PointsHorizontal(Math.min(pose1.getX(), pose2.getX()), Math.max(pose1.getX(), pose2.getX()), pose1.getY());
    	}
    }
    
    // 所有子路径都转换成子路径上的点
    private List<poseXYH> route2Points(List<poseXYH> argRt) {
    	List<poseXYH> mPts = new ArrayList<poseXYH>();
    	for (int i = 1; i < argRt.size(); i++) {
    		List<poseXYH> mPtsSubRoute = route2Points(argRt.get(i - 1), argRt.get(i ));
    		for (int j = 0; j < mPtsSubRoute.size(); ++j) {
    			mPts.add(mPtsSubRoute.get(j));
    		}
    	}
    	return mPts;
    }
    
    // 将子路径上的点转换成以点为中心车长宽为边长的四边形的四个顶点列表
    private List<rectanglePoints> route2RectPoints(List<poseXYH> argRt) {
    	List<rectanglePoints> rectPts = new ArrayList<rectanglePoints>();
    	List<poseXYH> mPose = route2Points(argRt);
    	for (int i = 0; i < mPose.size(); i++) {
    		rectanglePoints mRectPt = new rectanglePoints(mPose.get(i), agvLength / 2, agvWidth / 2);
    		rectPts.add(mRectPt);
		}
    	return rectPts;
    }
}