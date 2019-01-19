package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class AGVTaskTimeBolt extends BaseRichBolt {

	OutputCollector collector;
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector = collector;
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		int agv;
		int task;
		int score = 0;
		ArrayList<PositionInfo> agvTaskList = (ArrayList<PositionInfo>)input.getValue(0);
		agv = agvTaskList.get(0).agvTaskId;
		task = agvTaskList.get(1).agvTaskId;
		
		ArrayList<PbInfo> pbList = (ArrayList<PbInfo>)input.getValue(1);
		if (pbList.size() > 0) {
			for (int i = 0; i < pbList.size(); ++i) {
				score += getTaskScore(agvTaskList, pbList.get(i)) * pbList.get(i).rate ;
			}
			score /= pbList.size();
		} else {
			score += getTaskScore(agvTaskList);
		}

		collector.emit(new Values(agv, task, score));
	}

	public int getTaskScore(ArrayList<PositionInfo> agvTaskList) {
		return getTaskScore(agvTaskList.get(0), agvTaskList.get(1)) + getTaskScore(agvTaskList.get(1), agvTaskList.get(2));
	}
	
	public int getTaskScore(ArrayList<PositionInfo> agvTaskList, PbInfo pbif) {
		return getTaskScore(agvTaskList.get(0), agvTaskList.get(1)) + getTaskScore(agvTaskList.get(1), pbif) + getTaskScore(agvTaskList.get(2), pbif);
	}
	
	public int getTaskScore(PositionInfo agvTask, PbInfo pbif) {
		return getDistance(agvTask.x, agvTask.y, pbif.x, pbif.y);
	}
	
	public int getTaskScore(PositionInfo agvTask, PositionInfo agvTask1) {
		return getDistance(agvTask.x, agvTask.y, agvTask1.x, agvTask1.y);
	}
	public int getDistance(int x, int y, int x1, int y1) {
		return Math.abs(x - x1) + Math.abs(y - y1);
	}
	
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("agv", "task", "score"));
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
