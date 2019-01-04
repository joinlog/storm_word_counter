package org.apache.storm.timewindowack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.timewindowack.poseXYH;

/*
 * Storm中Spout用于读取并向计算拓扑中发送数据源，最近在调试一个topology时遇到了系统qps低，处理速度达不到要求的问题，经过排查后发现是由于对Spout的使用模式不当导致的多线程同步等待。这里罗列几点个人觉得编写Spout代码时需要特别注意的地方：

1. 最常用的模式是使用一个线程安全的queue，如BlockingQueue，spout主线程从queue中读取数据；另外的一个或多个线程负责从数据源（如各种消息中间件、db等）读取数据并放入queue中。

2. 如果不关心数据是否丢失（例如数据统计分析的典型场景），不要启用ack机制。

3. Spout的nextTuple和ack方法是在同一个线程中被执行的（可能最初觉得这块不会成为瓶颈，为了简单实现起见就单线程了，jstorm应该是已经改成了多线程），因此不能在nextTuple或ack方法里block住当前线程，这样将直接影响spout的处理速度，很关键。

4. Spout的nextTuple发送数据时，不能阻塞当前线程（见上一条），比如从queue中取数据时，使用poll接口而不是take，且poll方法尽量不要传参阻塞固定时间，如果queue中没有数据则直接返回；如果有多条待发送的数据，则一次调用nextTuple时遍历全部发出去。

5. Spout从0.8.1之后在调用nextTuple方法时，如果没有emit tuple，那么默认需要休眠1ms，这个具体的策略是可配置的，因此可以根据自己的具体场景，进行设置，以达到合理利用cpu资源。
 * */
/**
 * @author zpmc
 *  发送路径对,比如有300条路径，将其两两一对发送到下一级blot处理
 */
public class SubRouteSpout  extends BaseRichSpout {
	protected static final Logger LOG = LoggerFactory.getLogger(SubRouteSpout.class);
	private SpoutOutputCollector collector;
//    private static String[] words = {"Hadoop","Storm","Apache","Linux","Nginx","Tomcat","Spark"};
	
    private List<List<rectanglePoints>> agvRouteRectPoints = null;
    private static int agvNum = 2;
	private static int agvLength = 1500;
	private static int agvWidth = 400;
	
    private int agv1Index;
    private int agv2Index;

    private int agvRect1Index;
    private int agvRect2Index;
    
    long startTime = 0;
    long cnt = 0;
    long lastCnt = 0;
    
    public void nextTuple() {
//        String word = words[new Random().nextInt(words.length)];
//        collector.emit(new Values(word));
    	//System.out.println("subRouteSpout nextTuple start");

        if (agv1Index < agvRouteRectPoints.size()) {
        	if (agv2Index < agvRouteRectPoints.size()) {
        		List<rectanglePoints> rectList1 = agvRouteRectPoints.get(agv1Index);
        		List<rectanglePoints> rectList2 = agvRouteRectPoints.get(agv2Index);
        		if (agvRect1Index < rectList1.size()) {
        			if (agvRect2Index < rectList2.size()) {
        				collector.emit(new Values(rectList1.get(agvRect1Index), rectList2.get(agvRect2Index)));
        				++agvRect2Index;
        				++cnt;
            		} else {
            			++agvRect1Index;
            			agvRect2Index = 0;
            		}
        		} else {
        			++agv2Index;
        			agvRect1Index = 0;
        			agvRect2Index = 0;
        		}
        		
        		
        	} else {
        		++agv1Index;
        		agv2Index = agv1Index + 1;
        		agvRect1Index = 0;
    			agvRect2Index = 0;
        	}
        } else {
        	if (lastCnt != cnt) {
        		lastCnt = cnt;
	        	long endTime = System.currentTimeMillis();
	        	System.out.println("subRouteSpout nextTuple end @" + endTime);
	        	System.out.println("subRouteSpout total count:" + cnt);
	        	System.out.println("total time:" + (endTime - startTime));
	        	System.out.println("count per time:" + cnt / (endTime - startTime));
        	}
        }
        //System.out.println("subRouteSpout nextTuple end");
        
    }

    public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
    	startTime = System.currentTimeMillis();
        LOG.info("start open @ " + startTime);
    	System.out.println("start open @ " + startTime);
        this.collector = arg2;
        agv1Index = 0;
        agv2Index = agv1Index + 1;
        agvRect1Index = 0;
        agvRect2Index = 0;
        agvRouteRectPoints = new ArrayList<List<rectanglePoints>>();
        System.out.println("subRouteSpout open start");
        // TODO 读取或随机生成agv 路径上的点列加入到agvRoutes中去
        for (int i = 0; i < agvNum; i++) {
        	List<poseXYH> mRtPoseList = getRoutePoseList();
            List<rectanglePoints> rectPts0 = route2RectPoints(mRtPoseList);
            System.out.println("route pose size:"+mRtPoseList.size()+" ("+mRtPoseList.get(0).getX() +","+mRtPoseList.get(0).getY() +"):"+mRtPoseList.get(0).getH() +"-("+mRtPoseList.get(mRtPoseList.size() - 1).getX() +","+mRtPoseList.get(mRtPoseList.size() - 1).getY() +"):"+mRtPoseList.get(mRtPoseList.size() - 1).getH() );
            System.out.println("rect size:" + rectPts0.size());
            agvRouteRectPoints.add(rectPts0);
		}
        System.out.println("subRouteSpout open end");
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
        arg0.declare(new Fields("agvRt1", "agvRt2"));
    }
    
    public List<poseXYH> getRoutePoseList()
    {
    	List<poseXYH> mRtPoseList = new ArrayList<poseXYH>();
    	int []yStartEnd = {24100, 30400};
    	int []yXFromSmall2Big = {25300, 26100, 26900};
    	int []yXFromBig2Small = {25700, 26500, 27650};
    	int []xStartEnd = {10, 240000};
    	// pb
    	List<Integer> xPb = new ArrayList<Integer>();
    	int pbInterval = 600;
    	for (int i = xStartEnd[0]; i < xStartEnd[1]; i += pbInterval) {
			xPb.add(i);
		}
    	// tp
    	List<Integer> xTp = new ArrayList<Integer>();
    	int tpInterval = 500;
    	for (int i = xStartEnd[0]; i < xStartEnd[1]; i += tpInterval) {
    		xTp.add(i);
		}
    	//pb (x,y,h)
    	List<poseXYH> mStartEnd = new ArrayList<poseXYH>();
    	for (int i = 0; i < xPb.size(); i++) {
    		poseXYH mp = new poseXYH(xPb.get(i), yStartEnd[0], 90);
//    		mp.setH(90);
//    		mp.setX(xPb.get(i));
//    		mp.setY(yStartEnd[0]);
    		mStartEnd.add(mp);
		}
    	//tp (x,y,h)
    	for (int i = 0; i < xTp.size(); i++) {
    		poseXYH mp = new poseXYH(xTp.get(i), yStartEnd[1], 90);
//    		mp.setH(90);
//    		mp.setX(xTp.get(i));
//    		mp.setY(yStartEnd[1]);
    		mStartEnd.add(mp);
		}
    	//
    	int startEndInterval = 10000;
    	int iStart = new Random().nextInt(mStartEnd.size());
    	int iEnd ;
    	do {
    	    iEnd = new Random().nextInt(mStartEnd.size());
    	}while(Math.abs(mStartEnd.get(iStart).getX() - mStartEnd.get(iEnd).getX()) < startEndInterval);
    	
    	int yMid;
    	if (mStartEnd.get(iStart).getX() < mStartEnd.get(iEnd).getX()) {
    		yMid = yXFromSmall2Big[new Random().nextInt(yXFromSmall2Big.length)];
		} else {
			yMid = yXFromBig2Small[new Random().nextInt(yXFromBig2Small.length)];
		}
    	//
    	mRtPoseList.add(mStartEnd.get(iStart));
    	poseXYH mp = new poseXYH(mStartEnd.get(iStart).getX(), yMid, 0);
//		mp.setH(0);
//		mp.setX(mStartEnd.get(iStart).getX());
//		mp.setY(yMid);
		mRtPoseList.add(mp);
		
		mp.setH(0);
		mp.setX(mStartEnd.get(iEnd).getX());
		mp.setY(yMid);
		mRtPoseList.add(mp);
		
		mRtPoseList.add(mStartEnd.get(iEnd));
    	
    	return mRtPoseList;
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
