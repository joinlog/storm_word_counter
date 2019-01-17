package org.apache.storm.YSTimeMatrix;

public class DownQcpbReaderWriter extends JedisReaderWriter {
	public DownQcpbReaderWriter(String redisHost, int redisPort) {
	    super(redisHost, redisPort);
	}

	public String buildKey(String qcid) {
		return "downqcid:"+qcid;
	}
}
