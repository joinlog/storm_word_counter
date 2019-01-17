package org.apache.storm.YSTimeMatrix;


import org.apache.log4j.Logger;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class TopologyStarter2 {
	public final static String REDIS_HOST = "localhost";
	public final static int REDIS_PORT = 6379;
	public final static String WEBSERVER = "http://localhost:3000/news";
	public final static long DOWNLOAD_TIME = 100;
	public static boolean testing = false;
	public final static String AGV_TASK_LIST_KEY = "agv_task_list_key";

	public static void main(String[] args) {
        Logger.getRootLogger().getAllAppenders();

		TopologyBuilder builder = new TopologyBuilder();
        
        builder.setSpout("agv-task", new AGVTaskSpout(), 3);
        
        builder.setBolt("get-pbs", new GetLargeProbablityPBBolt(), 3)
        				.shuffleGrouping("agv-task");
        
        builder.setBolt("agv-task-time", new AGVTaskTimeBolt(), 5)
        				.shuffleGrouping("get-pbs");
        				//.fieldsGrouping("get-pbs", new Fields("user"));

        if(!testing)
        	builder.setBolt("news-notifier", new NewsNotifierBolt(), 5)
        				.shuffleGrouping("agv-task-time");
        
        Config conf = new Config();
        conf.setDebug(true);

        conf.put("redis-host", REDIS_HOST);
        conf.put("redis-port", REDIS_PORT);
        conf.put("webserver", WEBSERVER);
        conf.put("download-time", DOWNLOAD_TIME);
        conf.put("agv-task-list-key", AGV_TASK_LIST_KEY);
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("YSTimeMatrixCalc", conf, builder.createTopology());
	}
}

