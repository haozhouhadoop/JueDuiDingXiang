package Com.zhouhao;
/*
 *将初始地面测量坐标系下XY颠倒,默认为地面摄影测量坐标系下坐标
 *将结果中XY颠倒,转换为测量坐标系下坐标
 *加入测量坐标系转地面摄影测量坐标系
 *角度为10度06分
 * 
 */
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Jama.*;
public class JueDuiDingXiang {
	static final double hudu_dufenmiao=180*60*60/Math.PI;
	//控制点的摄影测量坐标
	double[][] ks=new double[4][3];
	//控制点的地面摄影测量坐标
	double[][] kds=new double[4][3];
	
	//控制点摄影测量坐标
	double  Xpg,Ypg,Zpg;
	//控制点地面摄影测量坐标
	double  Xtpg,Ytpg,Ztpg;
	//重心化矩阵
	double[][] g_ks=new double[4][3];
	double[][] g_kds=new double[4][3];
	//B阵hy 6666666666 hy6 b
	double[][] B=new double[12][7];
	//l阵
	double[][] l=new double[12][1];
	//旋转矩阵R2
	double[][] R2=new double[3][3];
	//待求的7个参数 △X,△Y,△Z,λ,φ,Ω,k
	double[][] X0={{0},{0},{0},{1},{0},{0},{0}};
	//待求7参数改正数
	double[][] xv=new double[7][1];
	//观测值改正数V
	double[][] V=new double[12][1]; 
	//NBB
	double[][] NBB=new double[7][7];
	double[][] R_1=new double[3][3];
	double[][] R_2=new double[3][3];
	//偏转角
	double xuanzhuanjiao=(23+6/60)*Math.PI/180;
//	// 计算转换坐标所用到的参数△U,△V
//	double U,V_V;
//	//3个变换坐标的参数a,b,λ
//	double[][] X1=new double[3][1];
//	//旋转矩阵R1
//	double[][] R1=new double[3][3];
	void R_1(){
		//测量坐标系转换地面摄影测量坐标系
		R_1[0][0]=Math.cos(xuanzhuanjiao);
		R_1[0][1]=-Math.sin(xuanzhuanjiao);
		R_1[0][2]=0;
		R_1[1][0]=Math.sin(xuanzhuanjiao);
		R_1[1][1]=Math.cos(xuanzhuanjiao);
		R_1[1][2]=0;
		R_1[2][0]=0;
		R_1[2][1]=0;
		R_1[2][2]=1;
	}
	void R_2(){
		//测量坐标系转换地面摄影测量坐标系
		R_2[0][0]=Math.cos(xuanzhuanjiao);
		R_2[0][1]=Math.sin(xuanzhuanjiao);
		R_2[0][2]=0;
		R_2[1][0]=-Math.sin(xuanzhuanjiao);
		R_2[1][1]=Math.cos(xuanzhuanjiao);
		R_2[1][2]=0;
		R_2[2][0]=0;
		R_2[2][1]=0;
		R_2[2][2]=1;
	}
	void fileReader(){
		int i=0;
		int j=0;
		String path_ks="C://Users//ASUS//Desktop//毕业设计//绝对定向//用于平差坐标//地面控制点摄影测量坐标//95-96.txt";
		String path_kds="C://Users//ASUS//Desktop//毕业设计//绝对定向//用于平差坐标//地面控制点坐标//95-96.txt";
		File file_ks=new File(path_ks);
		File file_kds=new File(path_kds);
		try {
			BufferedReader br_ks=new BufferedReader(new FileReader(file_ks));
			BufferedReader br_kds=new BufferedReader(new FileReader(file_kds));
			String str=null;
			
			while((str=br_ks.readLine())!=null){
				System.out.println(str);
				String[] string=str.split("	");
				//新加毫米转米
				ks[i][0]=Double.valueOf(string[0])/1000;
				ks[i][1]=Double.valueOf(string[1])/1000;
				ks[i][2]=Double.valueOf(string[2])/1000;
				++i;
			}
			
			
			while((str=br_kds.readLine())!=null){
				System.out.println(str);
				String[] string=str.split("	");
			
				kds[j][0]=Double.valueOf(string[0]);
				kds[j][1]=Double.valueOf(string[1]);
				kds[j][2]=Double.valueOf(string[2]);
				
				
				++j;
			}
			//数组打印
			System.out.println("控制点摄影测量坐标：");
			for (double[] k : ks) {
				System.out.println(Arrays.toString(k));
			}
			System.out.println("控制点地面测量坐标");
			for (double[] k : kds) {
				System.out.println(Arrays.toString(k));
			}
			//转换XY
			for(int k=0;k<4;k++){
				double t;
				t=kds[k][0];
				kds[k][0]=kds[k][1];
				kds[k][1]=t;
			}
			System.out.println("R_1");
			for (double[] d : R_1) {
				System.out.println(Arrays.toString(d));
			}
			System.out.println("R_2");
			for (double[] d : R_2) {
				System.out.println(Arrays.toString(d));
			}
		
			System.out.println("控制点地面测量坐标转换XY");
			for (double[] k : kds) {
				System.out.println(Arrays.toString(k));
			}
			//转换地面摄影测量坐标系
			kds=new Matrix(kds).times(new Matrix(R_1).transpose()).getArray();
			System.out.println("转换弧度"+xuanzhuanjiao);
			System.out.println("控制点地面摄影测量坐标");
			for (double[] k : kds) {
				System.out.println(Arrays.toString(k));
			}
	
			
			br_ks.close();
			br_kds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		
		
	}
//	//从文件中读取的作为已知值的地面测量坐标系下坐标转换为地面摄影测量坐标系下坐标,参与平差
//	void set_XYZ(){
//		
//	}

	//初始化R阵
	void set_R2(){

		
		R2[0][0]=Math.cos(X0[4][0])*Math.cos(X0[6][0])-Math.sin(X0[4][0])*Math.sin(X0[5][0])*Math.sin(X0[6][0]);//a1
    	R2[0][1]=-Math.cos(X0[4][0])*Math.sin(X0[6][0])-Math.sin(X0[4][0])*Math.sin(X0[5][0])*Math.cos(X0[6][0]);//a2
    	R2[0][2]=-Math.sin(X0[4][0])*Math.cos(X0[5][0]);                                    //a3
    	R2[1][0]=Math.cos(X0[5][0])*Math.sin(X0[6][0]);                                     //b1
    	R2[1][1]=Math.cos(X0[5][0])*Math.cos(X0[6][0]);                                     //b2
    	R2[1][2]=-Math.sin(X0[5][0]);                                                //b3
    	R2[2][0]=Math.sin(X0[4][0])*Math.cos(X0[6][0])+Math.cos(X0[4][0])*Math.sin(X0[5][0])*Math.sin(X0[6][0]); //c1
    	R2[2][1]=-Math.sin(X0[4][0])*Math.sin(X0[6][0])+Math.cos(X0[4][0])*Math.sin(X0[5][0])*Math.cos(X0[6][0]);//c2
    	R2[2][2]=Math.cos(X0[4][0])*Math.cos(X0[5][0]);                                     //c3
    	System.out.println("R2:");
    	for (double[] d : R2) {
			System.out.println(Arrays.toString(d));
		}
	}
	void  set_G(){
		Xpg=(ks[0][0]+ks[1][0]+ks[2][0]+ks[3][0])/4;
		Ypg=(ks[0][1]+ks[1][1]+ks[2][1]+ks[3][1])/4;
		Zpg=(ks[0][2]+ks[1][2]+ks[2][2]+ks[3][2])/4;
		Xtpg=(kds[0][0]+kds[1][0]+kds[2][0]+kds[3][0])/4;
		Ytpg=(kds[0][1]+kds[1][1]+kds[2][1]+kds[3][1])/4;
		Ztpg=(kds[0][2]+kds[1][2]+kds[2][2]+kds[3][2])/4;
	System.out.println("Xpg="+Xpg+" Ypg="+Ypg+" Zpg="+Zpg+" Xtpg="+Xtpg+" Ytpg="+Ytpg+" Ztpg="+Ztpg);
		
	}
	void get_Gds(){
		for(int i=0;i<4;i++){
			g_ks[i][0]=ks[i][0]-Xpg;
			g_ks[i][1]=ks[i][1]-Ypg;
			g_ks[i][2]=ks[i][2]-Zpg;
			g_kds[i][0]=kds[i][0]-Xtpg;
			g_kds[i][1]=kds[i][1]-Ytpg;
			g_kds[i][2]=kds[i][2]-Ztpg;
		
			
		}
		//给X0赋值
		X0[0][0]=(g_kds[0][0]+g_kds[1][0]+g_kds[2][0]+g_kds[3][0])/4-(g_ks[0][0]+g_ks[1][0]+g_ks[2][0]+g_ks[3][0])/4;
		X0[1][0]=(g_kds[0][1]+g_kds[1][1]+g_kds[2][1]+g_kds[3][1])/4-(g_ks[0][1]+g_ks[1][1]+g_ks[2][1]+g_ks[3][1])/4;
		X0[2][0]=(g_kds[0][2]+g_kds[1][2]+g_kds[2][2]+g_kds[3][2])/4-(g_ks[0][2]+g_ks[1][2]+g_ks[2][2]+g_ks[3][2])/4;
		System.out.println("∆X="+X0[0][0]+" ∆Y="+X0[1][0]+" ∆Z="+X0[2][0]);
		System.out.println("打印g_ks");
		for (double[] d : g_ks) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("打印g_kds");
		for (double[] d : g_kds) {
			System.out.println(Arrays.toString(d));
		}
		
	}
	void initialized_B(){
		
		//B阵
		 for(int i=0;i<4;i++){
			 B[0+i*3][0]=1;
			 B[0+i*3][1]=0;
			 B[0+i*3][2]=0;
			 B[0+i*3][3]=g_ks[i][0];
			 B[0+i*3][4]=-g_ks[i][2];
			 B[0+i*3][5]=0;
			 B[0+i*3][6]=-g_ks[i][1];
			 B[1+i*3][0]=0;
			 B[1+i*3][1]=1;
			 B[1+i*3][2]=0;
			 B[1+i*3][3]=g_ks[i][1];
			 B[1+i*3][4]=0;
			 B[1+i*3][5]=-g_ks[i][2];
			 B[1+i*3][6]=g_ks[i][0];
			 B[2+i*3][0]=0;
			 B[2+i*3][1]=0;
			 B[2+i*3][2]=1;
			 B[2+i*3][3]=g_ks[i][2];
			 B[2+i*3][4]=g_ks[i][0];
			 B[2+i*3][5]=g_ks[i][1];
			 B[2+i*3][6]=0;
			
			}
		 
		//打印B阵
		 System.out.println("B阵");
		 for (double[] d : B) {
			System.out.println(Arrays.toString(d));
			 
		 }
		 
	}
	//初始化l阵
	void set_l(){
		
		double[][] XYZ0={{X0[0][0]},{X0[1][0]},{X0[2][0]}};
		System.out.println("XYZ0:");
		for (double[] ds : XYZ0) {
			System.out.println(Arrays.toString(ds));
		}
		for(int i=0;i<4;i++){
			double[][] l_l=new double[3][1];
			double[][] l_kds=new double[3][1];
			double[][] l_ks=new double[3][1];
			l_kds[0][0]=g_kds[0+i][0];
			l_kds[1][0]=g_kds[0+i][1];
			l_kds[2][0]=g_kds[0+i][2];
			l_ks[0][0]=g_ks[0+i][0];
			l_ks[1][0]=g_ks[0+i][1];
			l_ks[2][0]=g_ks[0+i][2];
			System.out.println("λ="+X0[3][0]);;
			System.out.println("l_kds");
			for (double[] ds : l_ks) {
				System.out.println(Arrays.toString(ds));
			}
			System.out.println("l_ks");
			for (double[] ds : l_ks) {
				System.out.println(Arrays.toString(ds));
			}
			l_l=new Matrix(l_kds).minus(new Matrix(XYZ0)).minus(new Matrix(R2).times(new Matrix(l_ks)).times(X0[3][0])).getArray();
			l[0+i*3][0]=l_l[0][0];
			l[1+i*3][0]=l_l[1][0];
			l[2+i*3][0]=l_l[2][0];
		}
		//打印l阵
		System.out.println("l阵:");
		for (double[] ds : l) {
			System.out.println(Arrays.toString(ds));
		}
		 
	}
	//平差
	void pingcha(){
		double X00;
		NBB=new Matrix(B).transpose().times(new Matrix(B)).getArray();
		System.out.println("NBB");
		for (double[] d : NBB) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("NBB-1");
		for (double[] d : new Matrix(NBB).inverse().getArray()) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("w");
		for (double[] d : new Matrix(B).transpose().times(new Matrix(l)).getArray()) {
			System.out.println(Arrays.toString(d));
		}
		xv=new Matrix(NBB).inverse().times(new Matrix(B).transpose()).times(new Matrix(l)).getArray();
		X00=X0[3][0]*xv[3][0]+X0[3][0];
		X0=new Matrix(X0).plus(new Matrix(xv)).getArray();
		X0[3][0]=X00;
		//新加
		X0[3][0]=(X0[3][0]-1)*xv[3][0]+X0[3][0];
		V=new Matrix(B).times(new Matrix(xv)).minus(new Matrix(l)).getArray();
		System.out.println("xv:");
		for (double[] d : xv) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("l:");
		for (double[] d : l) {
			System.out.println(Arrays.toString(d));
		}

		System.out.println("X0:");
		for (double[] d : X0) {
			System.out.println(Arrays.toString(d));
		}
	
		System.out.println("V:");
		for (double[] d : V) {
			System.out.println(Arrays.toString(d));
		}
	}
	int  jingDuPingding(){
    	//平差方程自由度为1
    	
    	
		//精度，中误差
    	System.out.println("弧度转角度"+hudu_dufenmiao);
    	System.out.println("弧度改正数转角度改正数：");
    	double[][] xv_xv=new double[7][1];
    	
    	xv_xv[4][0]=xv[4][0]*hudu_dufenmiao;
    	xv_xv[5][0]=xv[5][0]*hudu_dufenmiao;
    	xv_xv[6][0]=xv[6][0]*hudu_dufenmiao;
    	xv_xv[0][0]=xv[0][0];
    	xv_xv[1][0]=xv[1][0];
    	xv_xv[2][0]=xv[2][0];
    	xv_xv[3][0]=xv[3][0];
    	
    	for (double[] d : xv_xv) {
			System.out.println(Arrays.toString(d));
		}
    	if(xv_xv[0][0]<1.0e-10&&xv_xv[1][0]<1.0e-10&&xv_xv[2][0]<1.0e-10&&xv_xv[3][0]<1.0e-10&&xv_xv[4][0]<1.0e-10&&xv_xv[5][0]<1.0e-10&&xv_xv[6][0]<1.0e-10)
    		return 0;
    	
    	else 
    		return 1;

    	
    	
    } 


	public static void main(String[] args) throws Exception {
		int i=0;
		JueDuiDingXiang jd=new JueDuiDingXiang();
		jd.R_1();
		jd.R_2();
		System.out.println("R_2");
		for (double[] d : jd.R_2) {
			System.out.println(Arrays.toString(d));
		}
		jd.fileReader();
		jd.set_G();
		jd.get_Gds();
//		jd.X0[0][0]=jd.Xtpg-jd.Xpg;
//		jd.X0[1][0]=jd.Ytpg-jd.Ypg;
//		jd.X0[2][0]=jd.Ztpg-jd.Zpg;
		jd.initialized_B();
		
		System.out.println("7个参数初始值X0:");
		for (double[] d : jd.X0) {
			System.out.println(Arrays.toString(d));
		}
		System.out.println("摄影测量重心化坐标原点："+jd.Xpg);
		System.out.println("重心化摄影测量坐标");
		for (double[] d : jd.g_ks) {
			System.out.println(Arrays.toString(d));
		}System.out.println("重心化地面摄影测量坐标");
		for (double[] d : jd.g_kds) {
			System.out.println(Arrays.toString(d));
		}
		do {
			if(i>100)
			break;
			jd.set_R2();
			jd.set_l();
			jd.pingcha();
			++i;
			System.out.println("迭代次数："+i);
		} while (true);
		System.out.println("总迭代次数："+i);
		System.out.println("七个参数△X,△Y,△Z,λ,φ,Ω,k：");
	    System.out.println("△X="+jd.X0[0][0]+"	△Y="+jd.X0[1][0]+"	△Z="+jd.X0[2][0]+"	λ="+jd.X0[3][0]+"	φ="+jd.X0[4][0]+"	Ω="+jd.X0[5][0]+"	k="+jd.X0[6][0]);
		//此处为平差完
		
		//读取批量求坐标文件
		BufferedReader br=new BufferedReader(new FileReader("C://Users//ASUS//Desktop//毕业设计//绝对定向//待求坐标//待求绝对定向摄影测量坐标.txt"));
		BufferedWriter bw=new BufferedWriter(new FileWriter("C://Users//ASUS//Desktop//毕业设计//绝对定向//待求坐标//绝对定向坐标成果.txt"));
		//用于存读取到的数据
    	ArrayList<String[]> array=new ArrayList< String[]>();
    	//用于存储处理后的摄影测量坐标
    	ArrayList<double[][]> arrayd=new ArrayList<>();
		String str=null;
		System.out.println("准备读取坐标");
		br.readLine();
		//读取txt文件中待测摄影测量坐标系下坐标，并存储到list
    	while((str=br.readLine())!=null){
    		System.out.println("开始读取模型点摄影测量坐标");
    		System.out.println(str);
    	    String[] arrystr=str.split("	");
    	    array.add(arrystr);
    	}
    
    	Iterator<String[]> it= array.iterator();
		
    	while(it.hasNext()){
    		double[][] d_XYZ=new double[3][1];
    		d_XYZ[0][0]=jd.X0[0][0];
    		d_XYZ[1][0]=jd.X0[1][0];
    		d_XYZ[2][0]=jd.X0[2][0];
    		double[][] d_Xtp=new double[3][1];//用于存储临时数据
    		double[][] ds=new double[3][1];
    		String[] string=it.next();//将list容器中的字符串数组地址复制给变量String
    		System.out.println("打印string");
    		for (String d : string) {
				System.out.println(d);
			}
    		ds[0][0]=Double.valueOf(string[0])/1000;//Xp
    		ds[1][0]=Double.valueOf(string[1])/1000;//Yp
    		ds[2][0]=Double.valueOf(string[2])/1000;//Zp
    		d_Xtp=new Matrix(jd.R2).times(new Matrix(ds)).times(jd.X0[3][0]).plus(new Matrix(d_XYZ)).getArray();
    		//重心坐标转换地面摄影测量坐标
    		d_Xtp[0][0]=d_Xtp[0][0]+jd.Xtpg;
    		d_Xtp[1][0]=d_Xtp[1][0]+jd.Ytpg;
    		d_Xtp[2][0]=d_Xtp[2][0]+jd.Ztpg;
    		System.out.println("绝对丁香");
    		for (double[]  d: d_Xtp) {
				System.out.println(Arrays.toString(d));
			}
    		//矩阵转置
    		d_Xtp=new Matrix(jd.R_2).times(new Matrix(d_Xtp)).getArray();
    		d_Xtp=new Matrix(d_Xtp).transpose().getArray();
    		
    		//此处还要转换地面测量坐标系，即转换XY
    		double t;
    		t=d_Xtp[0][0];
    		d_Xtp[0][0]=d_Xtp[0][1];
    		d_Xtp[0][1]=t;
    		arrayd.add(d_Xtp);
    		
		
		

    	}
    	Iterator<double[][]> iterator=arrayd.iterator();
    	System.out.println("地面测量坐标输入到记事本！");
    	while(iterator.hasNext()){
		    double[][] d=iterator.next();
			bw.write(d[0][0]+"\t");
			bw.write(d[0][1]+"\t");
			bw.write(d[0][2]+"\t");
			bw.write("\r\n");
		}
    	System.out.println("输入完毕！");
    	bw.flush();
    	br.close();
    	bw.close();
 
	}

}
