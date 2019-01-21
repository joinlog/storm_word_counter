 编译redis 
make MALLOC=jemalloc
make intall
 
 启动redis服务器
 nohup redis-server --protected-mode no &
 
 
 使用mvn package编译org.apache.storm.YSTimeMatrix.TopologyStarter2得到YSTimeMatrix-0.0.1-SNAPSHOT.jar和YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar
 
将YSTimeMatrix-0.0.1-SNAPSHOT.jar和YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar 用winscp上传到服务器上test目录下
 
 启动拓扑
 [root@Storm-master test]# /cloud/apache-storm-1.2.2/bin/storm jar YSTimeMatrix-0.0.1-SNAPSHOT.jar org.apache.storm.YSTimeMatrix.TopologyStarter2 TopologyStarter2
 
 建议使用下一个
 [root@Storm-master test]# /cloud/apache-storm-1.2.2/bin/storm jar YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar org.apache.storm.YSTimeMatrix.TopologyStarter2 TopologyStarter2
 
 
	查看拓扑
/cloud/apache-storm-1.2.2/bin/storm list	

	关闭拓扑
/cloud/apache-storm-1.2.2/bin/storm kill TopologyStarter2

 Storm UI：?
 
http://10.28.254.167:8080/index.html