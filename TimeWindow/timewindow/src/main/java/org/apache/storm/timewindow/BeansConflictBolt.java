package org.apache.storm.timewindow;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.timewindow.rectanglePoints;

/**
 * @author zpmc
 * 计算两个Beans是否冲突，并记录到数据库
 */
public class BeansConflictBolt extends BaseBasicBolt {
	protected static final Logger LOG = LoggerFactory.getLogger(BeansConflictBolt.class);
	Map<String, Boolean> counterMap = null;
	
	public void prepare(Map stormConf, TopologyContext context) {
		this.counterMap = new HashMap<String, Boolean>();
		System.out.println("BeansConflictBolt prepare start");
	}

    public void execute(Tuple arg0, BasicOutputCollector arg1) {
    	//System.out.println("BeansConflictBolt execute start");
//        String word = (String) arg0.getValue(0);
//        String out = "Hello " + word + "!";
//        System.out.println(out);
    	rectanglePoints rectPt0 = (rectanglePoints)arg0.getValue(0);
    	rectanglePoints rectPt1 = (rectanglePoints)arg0.getValue(1);
    	
    	String out = " " + rectPt0.toString() + " Vs " + rectPt1.toString();
    	//System.out.println(out);
    	if (isConflict2Rect(rectPt0, rectPt1)) {
    		counterMap.put(out, true);
          System.out.println(out);
		} else {
			counterMap.put(out, false);
		}
    	//System.out.println("BeansConflictBolt execute end");
    	
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
        
    }
    
    public void cleanup() {
    	long endTime = System.currentTimeMillis();
    	LOG.info("clean up @ " + endTime);
    	System.out.println("clean up @ " + endTime);
        for(Map.Entry<String, Boolean> entry:counterMap.entrySet()){
           System.out.println(entry.getKey()+" : " + entry.getValue());
        }
     }
    
    public Boolean isConflict2Rect(rectanglePoints rectPt0, rectanglePoints rectPt1, Vector2DM vec) {
    	int []minmax1 = rectPt0.GetMinMaxValueAfterProject(vec);
    	int []minmax2 = rectPt1.GetMinMaxValueAfterProject(vec);
    	if (minmax1[0] > minmax2[1]) {
    		return false;
    	}
    	
    	if (minmax1[1] < minmax2[0]) {
    		return false;
    	}
    	return true;
    }
    
    public Boolean isConflict2RectWithVectorsOfRec0(rectanglePoints rectPt0, rectanglePoints rectPt1) {
    	
    	List<Vector2DM> vectors = rectPt0.GetVectors();
    	for (int i = 0; i < vectors.size(); i++) {
    		Vector2DM vec = vectors.get(i).NormalVector();
    		vec.NormalizeVector();
    		if (isConflict2Rect(rectPt0, rectPt1, vec)) {
    			return true;
    		}
		}
    	return false;
    }
    
    public Boolean isConflict2Rect(rectanglePoints rectPt0, rectanglePoints rectPt1) {
    	// TODO
    	if (rectPt0.GetRadius() + rectPt1.GetRadius() > Math.sqrt(Math.pow(rectPt0.GetCenterX() - rectPt1.GetCenterX() , rectPt0.GetCenterY() - rectPt1.GetCenterY())) ) {
    		return false;
    	}

    	if (isConflict2RectWithVectorsOfRec0(rectPt0, rectPt1)) {
    		return true;
    	}
    	
    	if (isConflict2RectWithVectorsOfRec0(rectPt1, rectPt0)) {
    		return true;
    	}
    	return false;
    }
}
