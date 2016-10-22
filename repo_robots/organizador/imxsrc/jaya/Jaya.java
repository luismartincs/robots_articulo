//javac -cp jade.jar jaya/Jaya.java  ---- compilar fuera de la carpeta de jaya
package jaya;

import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;

public class Jaya {

    public Jaya() {
    }

    public static PlanObject optimizar(int[] x1, int[] y1,int goalX, int goalY) {
		// TODO Auto-generated method stub
// declarando k (#de agentes),j(# de coordenadas),x(coordenadas iniciales
//de cada agente, a(limite inferior),b(limite superior)
        
        int size = x1.length;
        int k = size;

        PlanObject result = new PlanObject();
        ArrayList<int[]> path = new ArrayList<int[]>();
        ArrayList<int[]> filterPath = new ArrayList<int[]>();

        int[] x = x1;
        int[] y = y1;
        int startX = x[0];
        int startY = y[0];

        System.out.println("Iniciales:");
        System.out.println("("+x[0]+","+y[0]+"),("+x[1]+","+y[1]+")");
  
//Evaluamos la funcion objetivo con los valores actuales de x & y;

        int[] F = new int[size];
        for (int i = 0; i < k; i++) {
            int f1 = x[i] * x[i];
            int f2 = y[i] * y[i];
            F[i] = f1 + f2;
        }
        
        int stop = 0;
        int iter = 0;
        while (stop == 0) {
            
//-------------------------Aqui empiezan las iteraciones---------------------------------------------
// Encontrando Best y Worst
            int[] F0 = new int[size];
            System.arraycopy(F, 0, F0, 0, size);
            // Copiando el F para no moverlo

            Arrays.sort(F0);
            int BB = F0[0];
            int ww = F0[size - 1];
            int BBI = 0;
            int wwI = 0;
            for (int i = 0; i < k; i++) {
                if (F[i] == BB) {
                    BBI = i;
                } else if (F[i] == ww) {
                    wwI = i;
                }
            }

// Generando numeros random
            Random rnd = new Random();
            double rx1 = rnd.nextDouble();
            double rx2 = rnd.nextDouble();
//System.out.println(rx1);
//System.out.println(rx2);
            double ry1 = rnd.nextDouble();
            double ry2 = rnd.nextDouble();
//System.out.println(ry1);
//System.out.println(ry2);
            double[] rx = {rx1, rx2};
            double[] ry = {ry1, ry2};
//Formula de Jaya

            double[] X = new double[size];
            double[] Y = new double[size];
            
            for (int i = 0; i < k; i++) {
                X[i] = x[i] + rx[0] * (x[BBI] - Math.abs(x[i])) - rx[1] * (x[wwI] - Math.abs(x[i]));
                //X[i] = Math.round(X[i]);
                Y[i] = y[i] + ry[0] * (y[BBI] - Math.abs(y[i])) - ry[1] * (y[wwI] - Math.abs(y[i]));
                //Y[i] = Math.round(Y[i]);
            }

//Evaluando la funcion objetivo con los nuevos elementos de X&Y
            int[] F1 = new int[size];
            for (int i = 0; i < k; i++) {
                double f1 = X[i] * X[i];
                double f2 = Y[i] * Y[i];
                F1[i] = (int) (f1 + f2);
            }

            ArrayList<int[]> points = new ArrayList<int[]>();
//Ahora comparamos si F1 es mejor a F 
            for (int i = 0; i < k; i++) {
                if (F1[i] < F[i]) {
                    x[i] = (int) X[i];
                    y[i] = (int) Y[i];
                }
                points.add(new int[]{x[i], y[i]});

                if (i == 0) { //Ignoramos el agente dummy 20,20
                    path.add(new int[]{x[i], y[i]});
                }

            }
           
//            System.out.println("camino:");
//            System.out.println(points.get(0)[0] + "," + points.get(0)[1]);
//            System.out.println(points.get(1)[0] + "," + points.get(1)[1]);

//Evaluamos finalmente las x & y ganadoras de la iteracion
            for (int i = 0; i < k; i++) {
                int f1 = x[i] * x[i];
                int f2 = y[i] * y[i];
                F[i] = f1 + f2;
            }

//revisando tolerancia de error o numero de iteraciones
            for (int i = 0; i < k; i++) {
                int tol = 0 + Math.abs(F[i]);
                if (tol < 1) {
                    stop = 1;
                }
            }
            iter = iter + 1;
            if (iter > 300) {
                stop = 1;
            }
            //System.out.println(iter);

        }

//encontrando los mejores
        int[] F11 = new int[size];
        System.arraycopy(F, 0, F11, 0, size);
// Copiando el F para no moverlo
        Arrays.sort(F11);
        int BB = F11[0];
        int ww = F11[size - 1];
        int BBI = 0;
        int wwI = 0;
        for (int i = 0; i < k; i++) {
            if (F[i] == BB) {
                BBI = i;
            }
        }
        
        
        //SE FILTRAN LOS RESULTADOS
        
        path.add(new int[]{0,0});
        filterPath.add(new int[]{startX + goalX,startY + goalY});
        
        int px = path.get(0)[0] + goalX;
        int py = path.get(0)[1] + goalY;
        
        for (int i = 0; i < path.size(); i++) {
            if (px != (path.get(i)[0] + goalX) || py != (path.get(i)[1] + goalY)){
                px = path.get(i)[0] + goalX;
                py = path.get(i)[1] + goalY;
                //filterPath.add(path.get(i));
                filterPath.add(new int[]{px,py});;
            }
        }
        
        System.out.println("Resultados:");
        System.out.println("X: "+x[BBI]+", Y: "+y[BBI]);
        
        
        System.out.println("Final path");

        for(int []p : filterPath){
            System.out.println(p[0]+","+p[1]);
        }
        
        int speed = 10;
        int timeplan = 0;
        for (int i = 0; i < filterPath.size() - 1; i++) {
            int px1 = filterPath.get(i)[0];
            int py1 = filterPath.get(i)[1];
            int px2 = filterPath.get(i+1)[0];
            int py2 = filterPath.get(i+1)[1];
            
            double distance = Math.sqrt(Math.pow((px2-px1),2)+Math.pow((py2-py1),2));
            timeplan += (speed * distance);
            System.out.println("distance from ("+px1+","+py1+") to ("+px2+","+py2+") = "+distance);
        }
        
        System.out.println("Time: "+timeplan);
        System.out.println("");
        
        result.setTime(timeplan);
        result.setPoints(filterPath);

        return result;
// no toques estos dos son el final del programa
    }
}

