package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;

public class JedisReaderWriter{
	String redisHost;
	int redisPort;
	Jedis jedis;
	
	public JedisReaderWriter(String redisHost, int redisPort) {
	    this.redisHost = redisHost;
	    this.redisPort = redisPort;
	    reconnect();
	}
	
	public void reconnect() {
        jedis = new Jedis(redisHost, redisPort);
    }
	
	// 返回value 以“：”间隔pbid组成字符串
	public String readItem(String key) throws Exception{
		String value= jedis.get((key));
		if(value == null  || ("nil".equals(value)))
			return null;
		//String[] pbs = pbidlist.split(":");
		
        return value;
    }
	
	// value 以“：”间隔pbid组成字符串
	public void writeItem(String key, String value) throws Exception{
		jedis.set((key), value);

    }
	
	public String buildKey(String qcid) {
		return qcid;
	}
	
	public ArrayList<PbInfo> ReadQcpb(int qcid) {
		ArrayList<PbInfo> pbArray = new ArrayList<PbInfo>();
		try {
			String pbs = readItem(buildKey(String.valueOf(qcid)));
			//System.out.println(pbs);
			String[] keys = pbs.split(":");
			for (int i = 0; i < keys.length; ++i) {
				PbInfo pbinfo = new PbInfo(keys[i].toString());
				pbArray.add(pbinfo);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pbArray;
	}
	
	public void WriteQcpb(int qcid, ArrayList<PbInfo> pbArray) {
		String Qcpb = new String();
		for (int i = 0; i < pbArray.size(); ++i) {
			Qcpb = Qcpb + pbArray.get(i).toString();
			if (i + 1 != pbArray.size()) {
				Qcpb = Qcpb + ":";
			}
		}
		
		try {
			writeItem(buildKey(String.valueOf(qcid)), Qcpb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
