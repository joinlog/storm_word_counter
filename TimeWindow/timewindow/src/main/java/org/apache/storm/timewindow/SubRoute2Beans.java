package org.apache.storm.timewindow;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class SubRoute2Beans extends BaseBasicBolt {
    public void execute(Tuple arg0, BasicOutputCollector arg1) {
        String word = (String) arg0.getValue(0);
        String out = "Hello " + word + "!";
        System.out.println(out);
    }

    public void declareOutputFields(OutputFieldsDeclarer arg0) {
        
    }
}