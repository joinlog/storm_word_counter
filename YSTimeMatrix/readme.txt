 ����redis 
make MALLOC=jemalloc
make intall
 
 ����redis������
 nohup redis-server --protected-mode no &
 
 
 ʹ��mvn package����org.apache.storm.YSTimeMatrix.TopologyStarter2�õ�YSTimeMatrix-0.0.1-SNAPSHOT.jar��YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar
 
��YSTimeMatrix-0.0.1-SNAPSHOT.jar��YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar ��winscp�ϴ�����������testĿ¼��
 
 ��������
 [root@Storm-master test]# /cloud/apache-storm-1.2.2/bin/storm jar YSTimeMatrix-0.0.1-SNAPSHOT.jar org.apache.storm.YSTimeMatrix.TopologyStarter2 TopologyStarter2
 
 ����ʹ����һ��
 [root@Storm-master test]# /cloud/apache-storm-1.2.2/bin/storm jar YSTimeMatrix-0.0.1-SNAPSHOT-jar-with-dependencies.jar org.apache.storm.YSTimeMatrix.TopologyStarter2 TopologyStarter2
 
 
	�鿴����
/cloud/apache-storm-1.2.2/bin/storm list	

	�ر�����
/cloud/apache-storm-1.2.2/bin/storm kill TopologyStarter2

 Storm UI��?
 
http://10.28.254.167:8080/index.html