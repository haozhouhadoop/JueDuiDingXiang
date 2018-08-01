package Com.zhouhao;
import java.util.Arrays;

import Jama.*;
public class TEst {

	public static void main(String[] args) {
		double[][] d1={{1,0},{0,1}};
		double[][] d2={{2,2},{2,2}};
		double[][] d3=new double[2][2];
		d3=new Matrix(d1).plus(new Matrix(d1)).times(new Matrix(d2)).getArray();
		for (double[] ds : d3) {
			System.out.println(Arrays.toString(ds));
		}
	}

}
