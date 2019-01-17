package org.apache.storm.YSTimeMatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import redis.clients.jedis.Jedis;

public class NewsNotifierBolt extends BaseRichBolt {
	private Jedis jedis;
	Timer timer;
	OutputCollector collector;
	String host;
	int port;
	long downloadTime;

	// ITEM:AGV-TASK -> SCORE
	HashMap<String, Integer> pendingToSave = new HashMap<String, Integer>(); 
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.host = (String)stormConf.get("redis-host");
		this.port = Integer.valueOf(stormConf.get("redis-port").toString());
		this.downloadTime = Long.valueOf(stormConf.get("download-time").toString());
		startDownloaderThread();
		this.collector = collector;
		reconnect();
	}
	
	public void reconnect() {
		this.jedis = new Jedis(host, port);
	}
	

	private String buildRedisKey(String agv) {
		return "agv:"+agv;
	}
	
	private String buildLocalKey(int agv, int task) {
		return String.valueOf(agv)+":"+String.valueOf(task);
	}
	
	private void storeProductCategoryCount(int agv, int task, int score) {
		String key = buildLocalKey(agv, task);
		synchronized (pendingToSave) {
			pendingToSave.put(key, score);	
		}
	}
	
	// Start a thread in charge of downloading metrics to redis.
	private void startDownloaderThread() {
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				HashMap<String, Integer> pendings;
				synchronized (pendingToSave) {
					pendings = pendingToSave;
					pendingToSave = new HashMap<String, Integer>();
				}
				
				for (String key : pendings.keySet()) {
					String[] keys = key.split(":");
					String agv = keys[0];
					String task = keys[1];
					Integer score = pendings.get(key);
					jedis.hset(buildRedisKey(agv), task, score.toString());
				}
			}
		};
		timer = new Timer("Item agv-task-score downloader");
		timer.scheduleAtFixedRate(t, downloadTime, downloadTime);
	}
	
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		int agv = input.getInteger(0);
		int task = input.getInteger(1);
		int score = input.getInteger(2);
		
		storeProductCategoryCount(agv, task, score);
		
	}

	public void cleanup() {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
