package org.apache.storm.YSTimeMatrix;

import java.util.ArrayList;
import java.util.Set;

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
	
	// 返回value 以“：”间隔pbid组成字符串
	public Set<String> readSetItem(String key) throws Exception{
		Set<String> value= jedis.smembers((key));
        return value;
    }
	
	// 
	public void writeSetItem(String key, String value) throws Exception{
		//jedis.del(key);
		jedis.sadd((key), value);

    }
	
	public void DelSet(String key) throws Exception{
		jedis.del(key);
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
			System.out.println(buildKey(String.valueOf(qcid)) + ":"+Qcpb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<PbInfo> ReadQcpbSet(int qcid) {
		ArrayList<PbInfo> pbArray = new ArrayList<PbInfo>();
		try {
			Set<String> pbs = readSetItem(buildKey(String.valueOf(qcid)));
			//System.out.println(buildKey(String.valueOf(qcid)));
			for (String keys:pbs) {
				//System.out.print(keys);
				PbInfo pbinfo = new PbInfo(keys);
				pbArray.add(pbinfo);
			}
			//System.out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pbArray;
	}
	
	public void WriteQcpbSet(int qcid, ArrayList<PbInfo> pbArray) {
		String Qcpb = new String();
		
		try {
			//System.out.println(buildKey(String.valueOf(qcid)) );
			for (int i = 0; i < pbArray.size(); ++i) {
				writeSetItem(buildKey(String.valueOf(qcid)), pbArray.get(i).toString()); 
				//System.out.print(pbArray.get(i).toString());
			}
			//System.out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ClearQcpbSet(int qcid) {
		try {
			DelSet(buildKey(String.valueOf(qcid)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
