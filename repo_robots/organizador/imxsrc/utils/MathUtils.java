package utils;

public class MathUtils{


	public static int getDegrees(int x1,int y1, int x2,int y2){

		if(x1 == x2)return 90;

		double pendiente = (double)(y2 - y1) / (double)(x2 - x1);
		double angulo = Math.toDegrees(Math.atan (pendiente));

		System.out.println(pendiente+","+(int)angulo);

		return (int)angulo;
	}

}