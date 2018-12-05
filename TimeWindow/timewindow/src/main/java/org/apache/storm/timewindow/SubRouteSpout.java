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
import org.apache.storm.timewindow.poseXYH;

/**
 * @author zpmc
 *  发送路径对,比如有300条路径，将其两两一对发送到下一级blot处理
 */
public class SubRouteSpout  extends BaseRichSpout {

	private SpoutOutputCollector collector;
//    private static String[] words = {"Hadoop","Storm","Apache","Linux","Nginx","Tomcat","Spark"};
    private List<List<poseXYH>> agvRoutes;

    public void nextTuple() {
//        String word = words[new Random().nextInt(words.length)];
//        collector.emit(new Values(word));

        for (int i = 0; i < agvRoutes.size(); i++) {
        	for (int j = i + 1; j < agvRoutes.size(); j++) {
        		List<List<poseXYH>> agvRtPair = new ArrayList<List<poseXYH>>();
        		agvRtPair.add(agvRoutes.get(i));
        		agvRtPair.add(agvRoutes.get(j));
        		collector.emit(new Values(agvRtPair));
    		}
		}
    }

    public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
        this.collector = arg2;
        agvRoutes = new ArrayList<List<poseXYH>>();
        // TODO 读取或随机生成agv 路径上的点列加入到agvRoutes中去
        
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
        arg0.declare(new Fields("agvRtPair"));
    }
}
