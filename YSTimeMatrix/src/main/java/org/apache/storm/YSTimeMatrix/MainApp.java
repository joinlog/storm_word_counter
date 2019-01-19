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
		
		for (int i = 0; i < pbNum; ++i) {
			PositionInfo posif = new PositionInfo(0, 0, 24150, 0, "PB");
			posif.id = 1001 + i;
			posif.x = 200 + 600 * i;
			pbList.add(posif);
		}
	}
	
	public static void initQC() {
		qcList = new ArrayList<PositionInfo>();

		for (int i = 0; i < qcNum; ++i) {
			PositionInfo posif = new PositionInfo(0, 0, 20600, 0, "QC");
			posif.id = 101 + i;
			posif.x = 7000 + 8000 * i;
			qcList.add(posif);
		}
	}
	
	public static void initWS() {
		wsList = new ArrayList<PositionInfo>();

		for (int i = 0; i < wsNum; ++i) {
			PositionInfo posif = new PositionInfo(0, 0, 30400, 90, "WS");
			posif.id = 201 + i;
			posif.x = 2000 + 4000 * i;
			wsList.add(posif);
		}
	}
	
	public static void initAGV() {
		agvList = new ArrayList<PositionInfo>();
		for (int i = 0; i < agvTaskNum; ++i) {
			agvList.add(getRandomPBorQCorWS());
		}
		//System.out.println("initAGV:");
		for (int i = 0; i < agvList.size(); ++i) {
			agvList.get(i).id = 801 + i;
			//System.out.println(agvList.get(i).toString());
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
		
		//System.out.println("initTask:");
		for (int i = 0; i < taskStartList.size(); ++i) {
			taskStartList.get(i).id = 601 + i;
			taskEndList.get(i).id  = 0;
			//System.out.println(taskStartList.get(i).toString() + " " + taskEndList.get(i).toString());
		}
	}
	
	public static PositionInfo getRandomPBorQCorWS() {
		 
		if (Math.random() > 0.7) {
			return getRandomQC();
		} else {
			return getRandomPBorWS();
		}
	}
	
	public static PositionInfo getRandomPBorWS() {
		if (Math.random() > 0.5) {
			return getRandomPB();
		} else {
			return getRandomWS();
		}
	}
	
	public static int getRandomInteger(int maxNum) {
		return (int)(Math.random() * maxNum);
	}
	public static PositionInfo getRandomPB() {

		PositionInfo mPosIf = new PositionInfo(pbList.get(getRandomInteger( pbNum - 1)).toString());
		return mPosIf;
	}
	
	public static PositionInfo getRandomQC() {
		PositionInfo mPosIf = new PositionInfo(qcList.get(getRandomInteger( qcNum - 1)).toString());
		return mPosIf;
	}
	
	public static PositionInfo getRandomWS() {
		PositionInfo mPosIf = new PositionInfo(wsList.get(getRandomInteger( wsNum - 1)).toString());
		return mPosIf;
	}
	
	public static void storeAGVTask() {
		System.out.println("storeAGVTask");
		for (int i = 0; i < agvList.size(); ++i) {
			for (int j = 0; j < taskStartList.size(); ++j) {
				try {
					agvTaskRW.writeItem(agvList.get(i).toString(), taskStartList.get(j).toString(), taskEndList.get(j).toString());
					System.out.println("["+ String.valueOf(i) + "," + String.valueOf(j) + "] "+ agvList.get(i).toString() +" "+ taskStartList.get(j).toString() +" "+ taskEndList.get(j).toString());
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
		int j = qcList.size()  - 1;
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
	
	//正态分布函数
	// https://zhidao.baidu.com/question/433502765.html
	public static double getNormalDistribution(int idx, int size) {
		double x = idx + 1;
		double len = size + 1;
		double u = len / 2.0;
		double sigma = len / 3.0;

		return Math.pow(Math.E, (-0.5 *( x - u) *( x - u)/(sigma * sigma))) / (sigma * Math.sqrt(2 * Math.PI));
	}
	
	
	// normal distribution正态分布
	public static ArrayList<PbInfo> getNormalDistributionPB(ArrayList<PositionInfo> pbPosList) {
		ArrayList<PbInfo> pbInfoList = new ArrayList<PbInfo>();
		for (int i = 0; i < pbPosList.size(); ++i) {
			PbInfo pbIf = new PbInfo(pbPosList.get(i).x, pbPosList.get(i).y, getNormalDistribution(i, pbPosList.size()));
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
		System.out.println("storeUpPbInfo");
		storeUpPbInfo();
		System.out.println("storeDownPbInfo");
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
