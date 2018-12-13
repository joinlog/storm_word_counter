package org.apache.storm.timewindow;

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

import org.apache.storm.timewindow.poseXYH;

/**
 * @author zpmc
 *  发送路径对,比如有300条路径，将其两两一对发送到下一级blot处理
 */
public class SubRouteSpout  extends BaseRichSpout {
	protected static final Logger LOG = LoggerFactory.getLogger(SubRouteSpout.class);
	private SpoutOutputCollector collector;
//    private static String[] words = {"Hadoop","Storm","Apache","Linux","Nginx","Tomcat","Spark"};
    private List<List<poseXYH>> agvRoutes = null;
    private static int agvNum = 300;

    public void nextTuple() {
//        String word = words[new Random().nextInt(words.length)];
//        collector.emit(new Values(word));
    	System.out.println("subRouteSpout nextTuple start");
        for (int i = 0; i < agvRoutes.size(); i++) {
        	for (int j = i + 1; j < agvRoutes.size(); j++) {
//        		List<List<poseXYH>> agvRtPair = new ArrayList<List<poseXYH>>();
//        		agvRtPair.add(agvRoutes.get(i));
//        		agvRtPair.add(agvRoutes.get(j));
        		collector.emit(new Values(agvRoutes.get(i), agvRoutes.get(j)));
    		}
		}
        System.out.println("subRouteSpout nextTuple end");
    }

    public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
    	long startTime = System.currentTimeMillis();
        LOG.info("start open @ " + startTime);
    	System.out.println("start open @ " + startTime);
        this.collector = arg2;
        agvRoutes = new ArrayList<List<poseXYH>>();
        System.out.println("subRouteSpout open start");
        // TODO 读取或随机生成agv 路径上的点列加入到agvRoutes中去
        for (int i = 0; i < agvNum; i++) {
        	List<poseXYH> mRtPoseList = getRoutePoseList();
        	agvRoutes.add(mRtPoseList);
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
}
