package org.apache.storm.TopologyMain;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordCounter extends BaseBasicBolt {
	//private static final Logger LOG = LoggerFactory.getLogger(WordCounter.class);
	Integer id;
	String name;
	Map<String, Integer> counters;

	/**
	 * At the end of the spout (when the cluster is shutdown
	 * We will show the word counters
	 */

	public void cleanup() {
		System.out.println("-- Word Counter ["+name+"-"+id+"] --");
		//LOG.info("WordCounter Result:");
		//LOG.info("-- Word Counter [{} - {}] --", name, id);
		for(Map.Entry<String, Integer> entry : counters.entrySet()){
			System.out.println(entry.getKey()+": "+entry.getValue());
			//LOG.info("{} : {}", entry.getKey(), entry.getValue());
		}
	}

	/**
	 * On create 
	 */

	public void prepare(Map stormConf, TopologyContext context) {
		this.counters = new HashMap<String, Integer>();
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
	}



	public void execute(Tuple input, BasicOutputCollector collector) {
		//LOG.info("WordCounter execute:");
		String str = input.getString(0);
		/**
		 * If the word dosn't exist in the map we will create
		 * this, if not We will add 1 
		 */
		if(!counters.containsKey(str)){
			counters.put(str, 1);
		}else{
			Integer c = counters.get(str) + 1;
			counters.put(str, c);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}
}
