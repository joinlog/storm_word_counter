package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class UpQcpbReaderWriter extends JedisReaderWriter{
	
	public UpQcpbReaderWriter(String redisHost, int redisPort) {
	    super(redisHost, redisPort);
	}

	public String buildKey(String qcid) {
		return "upqcid:"+qcid;
	}
	

}
