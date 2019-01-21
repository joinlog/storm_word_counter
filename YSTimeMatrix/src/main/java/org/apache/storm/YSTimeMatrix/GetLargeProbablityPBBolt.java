package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;

import org.apache.storm.tuple.Fields;

public class GetLargeProbablityPBBolt extends BaseRichBolt {

	OutputCollector collector;
	String host;
	int port;
	Jedis jedis;
	
	UpQcpbReaderWriter upPbRW;
	DownQcpbReaderWriter downPbRW;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector = collector;
		this.host = (String)stormConf.get("redis-host");
		this.port = Integer.valueOf(stormConf.get("redis-port").toString());
		upPbRW = new UpQcpbReaderWriter(this.host, this.port);
		downPbRW = new DownQcpbReaderWriter(this.host, this.port);
		reconnect();
	}
	
	public void reconnect() {
		upPbRW.reconnect();
		downPbRW.reconnect();
	}
	
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		ArrayList<PositionInfo> agvTaskList = (ArrayList<PositionInfo>)input.getValue(0);
		if (agvTaskList.size() != 3) {
			return;
		}
		ArrayList<PbInfo> pbList = new ArrayList<PbInfo>();
		if (false == "QC".equals( agvTaskList.get(1).type) && true == "QC".equals( agvTaskList.get(2).type)) { //起点非QC，终点是QC
			//获取上档pb
			pbList = upPbRW.ReadQcpbSet(agvTaskList.get(2).id);
		}
		else if (false == "QC".equals( agvTaskList.get(2).type) && true == "QC".equals( agvTaskList.get(1).type)) { //起点是QC，终点是非QC
			// 获取下档pb
			pbList = downPbRW.ReadQcpbSet(agvTaskList.get(1).id);
		}
		else {
			
		}
		collector.emit(new Values(agvTaskList, pbList));
	}

	public void cleanup() {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("agvTaskList", "pbList"));
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
