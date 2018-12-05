package org.apache.storm.timewindow;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.timewindow.SubRoute2Beans;
import org.apache.storm.timewindow.BeansConflictBolt;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class TimeWindow {

    public static void main(String[] args) throws AuthorizationException {
        TopologyBuilder builder = new TopologyBuilder();
        // 发送路径对,比如有300条路径,将其两两一对发送到下一级blot处理
        builder.setSpout("subRouteSpout", new SubRouteSpout());
        //  接收路径对，将其转为Beans列表，将两个列表中的Beans两两一对发送到下一个blot处理
        builder.setBolt("subRoute2BeansBolt", new SubRoute2Beans()).shuffleGrouping("subRouteSpout");
        // 计算两个Beans是否冲突，并记录到数据库
        builder.setBolt("beansConflictBolt", new BeansConflictBolt()).shuffleGrouping("subRoute2BeansBolt");
        Config conf = new Config();
        conf.setDebug(false);
        if(args != null && args.length > 0) {
        	System.out.println("storm remote cluster mode!");
            conf.setNumWorkers(3);
            try {
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (AlreadyAliveException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("firststorm", conf, builder.createTopology());
            Utils.sleep(30000);
            cluster.killTopology("firststorm");
            cluster.shutdown();
        }
    }

}
