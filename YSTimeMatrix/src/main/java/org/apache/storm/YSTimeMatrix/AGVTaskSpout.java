package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;
import java.util.Map;

import redis.clients.jedis.Jedis;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichSpout;

import org.apache.storm.tuple.Fields;

import org.apache.storm.tuple.Values;

import org.apache.storm.topology.OutputFieldsDeclarer;


@SuppressWarnings("serial")
public class AGVTaskSpout extends BaseRichSpout {
	//private static final long serialVersionUID = 1L;
	Jedis jedis;
	String host; 
	int port;
	SpoutOutputCollector collector;
	AGVTaskReaderWriter AGVTaskReader;
	
	@SuppressWarnings("rawtypes")
	public void open(Map stormConf, TopologyContext context, SpoutOutputCollector collector) {
		// TODO Auto-generated method stub
		host = stormConf.get("redis-host").toString();
		port = Integer.valueOf(stormConf.get("redis-port").toString());
		this.collector = collector;
		AGVTaskReader = new AGVTaskReaderWriter(host, port, stormConf.get("agv-task-list-key").toString());
		reconnect();
		
		
	}

	private void reconnect() {
		//jedis = new Jedis(host, port);
		AGVTaskReader.reconnect();
	}


	public void nextTuple() {
		// TODO Auto-generated method stub
		try {
			ArrayList<PositionInfo> listPos = AGVTaskReader.readItem();
			
			//collector.emit(new Values(listPos.get(0).toString(), listPos.get(1).toString(), listPos.get(2).toString()));
			collector.emit(new Values(listPos));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		//declarer.declare(new Fields("agvPos", "TaskStartPos", "TaskEndPos"));
		declarer.declare(new Fields("agvTaskList"));
	}

}
