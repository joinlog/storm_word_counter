package org.apache.storm.timewindow;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.timewindow.poseXYH;
import org.apache.storm.timewindow.rectanglePoints;

/**
 * @author zpmc
 * 接收路径对，将其转为Beans列表，将两个列表中的Beans两两一对发送到下一个blot处理
 */
public class SubRoute2Beans extends BaseBasicBolt {

    public void execute(Tuple arg0, BasicOutputCollector collector) {
//        String word = (String) arg0.getValue(0);
//        String out = "Hello " + word + "!";
//        System.out.println(out);
        
        List<Object> agvRtPair = arg0.getValues();
        List<poseXYH> agvRt0 = (List<poseXYH>) agvRtPair.get(0);
        List<poseXYH> agvRt1 = (List<poseXYH>) agvRtPair.get(1);
        List<rectanglePoints> rectPts0 = route2Points(agvRt0);
        List<rectanglePoints> rectPts1 = route2Points(agvRt1);
        for (int i = 0; i < rectPts0.size(); i++) {
        	for (int j = 0; j < rectPts1.size(); j++) {
        		List<rectanglePoints> rectPtsPair = new ArrayList<rectanglePoints>();
        		rectPtsPair.add(rectPts0.get(i));
        		rectPtsPair.add(rectPts1.get(j));
        		collector.emit(new Values(rectPtsPair));
    		}
		}
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
    	arg0.declare(new Fields("rectPtsPair"));
    }
    
    
    private List<rectanglePoints> route2Points(List<poseXYH> argRt) {
    	List<rectanglePoints> rectPts = new ArrayList<rectanglePoints>();
    	for (int i = 0; i < argRt.size(); i++) {
    		rectanglePoints mRectPt = new rectanglePoints(argRt.get(i));
    		rectPts.add(mRectPt);
    		
		}
    	return rectPts;
    }
}