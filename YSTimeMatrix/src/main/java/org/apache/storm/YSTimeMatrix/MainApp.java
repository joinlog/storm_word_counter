package org.apache.storm.YSTimeMatrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainApp {
	public final static String REDIS_HOST = "10.28.254.167";
	public final static int REDIS_PORT = 6379;
	public final static String WEBSERVER = "http://localhost:3000/news";
	public final static long DOWNLOAD_TIME = 100;
	public static boolean testing = false;
	public final static String AGV_TASK_LIST_KEY = "agv_task_list_key";
	static AGVTaskReaderWriter agvTaskRW;
	static DownQcpbReaderWriter downQcpbRW;
	static UpQcpbReaderWriter upQcpbRW;
	static AGVTaskResultReaderWriter AGVTaskResRW;
	
	public final static int pbToNearQcDist = 2700;
	public final static int pbToFarQcDist = 1700;
	public final static int agvTaskNum = 30;
	
	public final static int pbNum = 300;
	public final static int qcNum = 30;
	public final static int wsNum = 60;
	public final static double const_e = 2.71828;
	
	static ArrayList<PositionInfo> agvList;
	static ArrayList<PositionInfo> taskStartList;
	static ArrayList<PositionInfo> taskEndList;
	
	static ArrayList<PositionInfo> pbList;
	static ArrayList<PositionInfo> qcList;
	static ArrayList<PositionInfo> wsList;
	
	static ArrayList<AGVTaskResult> agvTaskResult;
	
	static PrintWriter pw = null;
	static FileWriter fw = null;
    
	public static void initPB() {
		pbList = new ArrayList<PositionInfo>();
		PositionInfo posif = new PositionInfo(0, 0, 24150, 0, "PB");
		for (int i = 0; i < pbNum; ++i) {
			posif.id = 100 + i;
			posif.x = 200 + 600 * i;
			pbList.add(posif);
		}
	}
	
	public static void initQC() {
		qcList = new ArrayList<PositionInfo>();
		PositionInfo posif = new PositionInfo(0, 0, 20600, 0, "QC");
		for (int i = 0; i < qcNum; ++i) {
			posif.id = 100 + i;
			posif.x = 7000 + 8000 * i;
			qcList.add(posif);
		}
	}
	
	public static void initWS() {
		wsList = new ArrayList<PositionInfo>();
		PositionInfo posif = new PositionInfo(0, 0, 30400, 90, "WS");
		for (int i = 0; i < wsNum; ++i) {
			posif.id = 100 + i;
			posif.x = 2000 + 4000 * i;
			wsList.add(posif);
		}
	}
	
	public static void initAGV() {
		agvList = new ArrayList<PositionInfo>();
		for (int i = 0; i < agvTaskNum; ++i) {
			agvList.add(getRandomPBorQCorWS());
		}
		for (int i = 0; i < agvTaskNum; ++i) {
			agvList.get(i).id = 0;
		}
	}
	
	public static void initTask() {
		taskStartList = new ArrayList<PositionInfo>();
		taskEndList = new ArrayList<PositionInfo>();
		for (int i = 0; i < agvTaskNum / 2; ++i) {
			taskStartList.add(getRandomPBorWS());
			taskEndList.add(getRandomQC());
		}
		
		for (int i = agvTaskNum / 2; i < agvTaskNum; ++i) {
			taskEndList.add(getRandomPBorWS());
			taskStartList.add(getRandomQC());
		}
		
		for (int i = 0; i < agvTaskNum; ++i) {
			taskStartList.get(i).id = i;
		}
	}
	
	public static PositionInfo getRandomPBorQCorWS() {
		 
		if (Math.random() > 0.5) {
			return getRandomPB();
		} else {
			return getRandomPBorWS();
		}
	}
	
	public static PositionInfo getRandomPBorWS() {
		if (Math.random() > 0.5) {
			return getRandomQC();
		} else {
			return getRandomWS();
		}
	}
	
	public static int getRandomInteger(int maxNum) {
		return (int)(Math.random() * maxNum);
	}
	public static PositionInfo getRandomPB() {
		int pbIndex = getRandomInteger(pbNum - 1);
		
		return pbList.get(pbIndex);
	}
	
	public static PositionInfo getRandomQC() {
		return qcList.get(getRandomInteger( qcNum - 1));
	}
	
	public static PositionInfo getRandomWS() {
		return wsList.get(getRandomInteger( wsNum - 1));
	}
	
	public static void storeAGVTask() {
		for (int i = 0; i < agvList.size(); ++i) {
			for (int j = 0; j < taskStartList.size(); ++j) {
				try {
					agvTaskRW.writeItem(agvList.get(i).toString(), taskStartList.get(j).toString(), taskEndList.get(j).toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//获得当前qcIndex值的上档pb
	public static ArrayList<PositionInfo> getUpQcpbs(int qcIndex) {
		ArrayList<PositionInfo> upQcpbList = new ArrayList<PositionInfo>();
		// index小的坐标也小
		int j = 0;
		for (int i = 0; i < pbList.size(); ++i) {
			// 上档pb从坐标小的一侧
			if (pbList.get(i).x > qcList.get(qcIndex).x) {
				break;
			}
			if (pbList.get(i).x < qcList.get(j).x - pbToNearQcDist) {
				upQcpbList.add(pbList.get(i));
			} else if (pbList.get(i).x < qcList.get(j).x + pbToFarQcDist) {
				continue;
			} else {
				++j;
			}
		}
		return upQcpbList;
	}
	
	//获得当前qcIndex值的下档pb
	public static ArrayList<PositionInfo> getDownQcpbs(int qcIndex) {
		ArrayList<PositionInfo> downQcpbList = new ArrayList<PositionInfo>();
		// index大的坐标也大
		int j = qcList.size();
		for (int i = pbList.size() - 1; i >= 0 ; --i) {
			// 下档pb从坐标大的一侧
			if (pbList.get(i).x < qcList.get(qcIndex).x) {
				break;
			}
			if (pbList.get(i).x > qcList.get(j).x + pbToNearQcDist) {
				downQcpbList.add(pbList.get(i));
			} else if (pbList.get(i).x > qcList.get(j).x - pbToFarQcDist) {
				continue;
			} else {
				--j;
			}
		}
		return downQcpbList;
	}
	
	public static double getNormalDistribution(int idx, int size) {
		double x = (idx + 1) / (size + 1);
		return Math.pow(Math.E, (-0.5 * x * x)) / Math.sqrt(2 * Math.PI);
	}
	
	
	// normal distribution正态分布
	public static ArrayList<PbInfo> getNormalDistributionPB(ArrayList<PositionInfo> pbList) {
		ArrayList<PbInfo> pbInfoList = new ArrayList<PbInfo>();
		for (int i = 0; i < pbList.size(); ++i) {
			PbInfo pbIf = new PbInfo(pbList.get(i).x, pbList.get(i).y, getNormalDistribution(i, pbList.size()));
			pbInfoList.add(pbIf);
		}
		return pbInfoList;
	}
	
	public static void storeUpPbInfo() {
		
		for (int i = 0; i < qcList.size(); ++i) {
			ArrayList<PositionInfo> pbPos = getUpQcpbs(i);
			ArrayList<PbInfo> pbIf = getNormalDistributionPB(pbPos);
			upQcpbRW.WriteQcpb(qcList.get(i).id, pbIf);
		}
	}
	public static void storeDownPbInfo() {
		for (int i = 0; i < qcList.size(); ++i) {
			ArrayList<PositionInfo> pbPos = getDownQcpbs(i);
			ArrayList<PbInfo> pbIf = getNormalDistributionPB(pbPos);
			downQcpbRW.WriteQcpb(qcList.get(i).id, pbIf);
		}
	}
	public static void storePbInfo() {
		storeUpPbInfo();
		storeDownPbInfo();
	}
	
	public static void readAGVTaskReuslt() {
		agvTaskResult = new ArrayList<AGVTaskResult> ();
		for (int i = 0; i < agvList.size(); ++i) {
			ArrayList<AGVTaskResult> agvTaskRes = AGVTaskResRW.readItem(agvList.get(i).id);
			if (agvTaskRes.size() > 0) {
				agvTaskResult.addAll(agvTaskRes);
			}
			
		}
	}
	
	public static void dumpResultToFile() {
        String filename = "agvTaskResult.txt";
        // 关联文件
        File file = new File(filename);
        if(!file.exists()){
            // 判断文件不存在就new新文件,写数据
            try {
                file.createNewFile();
                // java IO流和文件关联
                pw = new PrintWriter(file);
                for (int i = 0; i < agvTaskResult.size(); ++i) {
                	pw.print(agvTaskResult.get(i).toString());
                	pw.println();
                }

                pw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
             
        }else{
            // 判断文件存在,就以FileWriter文件追加的方式写文件
            try {
                fw = new FileWriter(filename,true);
                for (int i = 0; i < agvTaskResult.size(); ++i) {
                	fw.write(agvTaskResult.get(i).toString() + "\n");
                }
                fw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}
	public static void main(String[] args) {
		agvTaskRW = new AGVTaskReaderWriter(REDIS_HOST, REDIS_PORT, AGV_TASK_LIST_KEY);
		downQcpbRW = new DownQcpbReaderWriter(REDIS_HOST, REDIS_PORT);
		upQcpbRW = new UpQcpbReaderWriter(REDIS_HOST, REDIS_PORT);
		AGVTaskResRW = new AGVTaskResultReaderWriter(REDIS_HOST, REDIS_PORT);
		
		initPB();
		initQC();
		initWS();
		
		initAGV();
		initTask();
		
		//组合agv和任务
		storeAGVTask();
		storePbInfo();
		
		System.out.println("init and store config complete!");
		try {
			Thread.sleep(10000, 1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("readAGVTaskReuslt start!");
		//读取最终结果
		readAGVTaskReuslt();
		System.out.println("dumpResultToFile start!");
		dumpResultToFile();
		System.out.println("dumpResultToFile end!");
	}		
}
