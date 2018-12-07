package org.apache.storm.StormKafkaTopo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class MessageScheme implements Scheme {
    public List<Object> deserialize(byte[] arg0) {
        try {
            String msg = new String(arg0, "UTF-8");
            return new Values(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Fields getOutputFields() {
        return new Fields("msg");
    }

	public List<Object> deserialize(ByteBuffer ser) {
		// TODO Auto-generated method stub
		return null;
	}
}
