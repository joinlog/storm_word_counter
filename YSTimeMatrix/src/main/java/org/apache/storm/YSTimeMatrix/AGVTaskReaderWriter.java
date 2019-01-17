package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;

// 在@LIROListKey所在的队列中从有到左连续的三个分别是AGVPosInfo，TaskStartPosInfo，TaskEndPosInfo
// 因此在调用writeItem时，连续写入3个，从左写入队列；
// 在调用readItem时，从右读取队列，放回ArrayList返回，按照顺序
public class AGVTaskReaderWriter{
	String redisHost;
	int redisPort;
	String LIROListKey; //左进右出list所在的key
	Jedis jedis;

	
	public AGVTaskReaderWriter(String redisHost, int redisPort, String LIROListKey) {
	    this.redisHost = redisHost;
	    this.redisPort = redisPort;
	    this.LIROListKey = LIROListKey;
	    reconnect();
	}
	
	public void reconnect() {
        jedis = new Jedis(redisHost, redisPort);
    }
	
	
	// 返回value 以“：”间隔pbid组成字符串
	public ArrayList<PositionInfo> readItem() throws Exception{

		ArrayList<PositionInfo> posInfoArray = new ArrayList<PositionInfo>();
		for (int i = 0; i < 3; ++i) {
			String content = jedis.rpop(LIROListKey);
			if(content==null || "nil".equals(content)) {
				try { Thread.sleep(300); } catch (InterruptedException e) {}
			} else {
				PositionInfo posInfo = new PositionInfo(content);
				posInfoArray.add(posInfo);
			}
		}
        return posInfoArray;
    }

	
	// value 以“：”间隔pbid组成字符串
	public void writeItem(String AGVPos, String TaskStartPos, String TaskEndPos) throws Exception{
		jedis.lpush(LIROListKey, AGVPos);
		jedis.lpush(LIROListKey, TaskStartPos);
		jedis.lpush(LIROListKey, TaskEndPos);
    }
	
}
