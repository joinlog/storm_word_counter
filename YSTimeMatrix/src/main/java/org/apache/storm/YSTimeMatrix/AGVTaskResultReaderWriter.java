package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;

public class AGVTaskResultReaderWriter {
	String redisHost;
	int redisPort;
	String LIROListKey; //左进右出list所在的key
	Jedis jedis;

	public AGVTaskResultReaderWriter(String redisHost, int redisPort) {
	    this.redisHost = redisHost;
	    this.redisPort = redisPort;
	    //this.LIROListKey = buildRedisKey(String.valueOf(agv));
	    reconnect();
	}
	
	public void reconnect() {
        jedis = new Jedis(redisHost, redisPort);
    }
	
	private String buildRedisKey(String agv) {
		return "agvTaskResult:"+agv;
	}
	
	// 返回value 以“：”间隔pbid组成字符串
	public ArrayList<AGVTaskResult> readItem(int agv){

		ArrayList<AGVTaskResult> agvTaskRes = new ArrayList<AGVTaskResult>();
		do {
			String content = jedis.rpop(buildRedisKey(String.valueOf(agv)));
			if(content==null || "nil".equals(content)) {
				break;
			} else {
				AGVTaskResult res = new AGVTaskResult(content);
				agvTaskRes.add(res);
			}
		} while(true);
        return agvTaskRes;
    }

	
	// value 以“：”间隔pbid组成字符串
	public void writeItem(String agv, String task, String result) { 
		jedis.lpush(buildRedisKey(agv), agv + ":" + task + ":" + result);
    }
}
