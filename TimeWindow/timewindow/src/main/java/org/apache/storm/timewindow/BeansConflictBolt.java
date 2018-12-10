package org.apache.storm.timewindow;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.List;

import org.apache.storm.timewindow.rectanglePoints;

/**
 * @author zpmc
 * 计算两个Beans是否冲突，并记录到数据库
 */
public class BeansConflictBolt extends BaseBasicBolt {
    public void execute(Tuple arg0, BasicOutputCollector arg1) {
    	System.out.println("BeansConflictBolt execute start");
//        String word = (String) arg0.getValue(0);
//        String out = "Hello " + word + "!";
//        System.out.println(out);
    	rectanglePoints rectPt0 = (rectanglePoints)arg0.getValue(0);
    	rectanglePoints rectPt1 = (rectanglePoints)arg0.getValue(1);
    	
    	if (isConflict2Rect(rectPt0, rectPt1)) {
          String out = " " + rectPt0.toString() + " Vs " + rectPt1.toString();
          System.out.println(out);
		}
    	System.out.println("BeansConflictBolt execute end");
    	
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
        
    }
    
    public Boolean isConflict2Rect(rectanglePoints rectPt0, rectanglePoints rectPt1) {
    	// TODO
    	return true;
    }
}
