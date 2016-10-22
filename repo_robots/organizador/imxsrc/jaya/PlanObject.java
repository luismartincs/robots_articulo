package jaya;

import java.util.ArrayList;


public class PlanObject{

	private int time;
    private char orientation;
	private ArrayList<int []> points;
	
	public PlanObject(){}
	
	public PlanObject(ArrayList<int[]> points, int time){
		this.time = time;
		this.points = points;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	public void setPoints(ArrayList<int[]> points){
		this.points = points;
	}

	public int getTime(){
		return time;
	}
	
	public ArrayList<int[]> getPoints(){
		return this.points;
	}

    public void setOrientation(char o){
        this.orientation = o;
    }

    public char getOrientation(){
        return orientation;
    }

    public static PlanObject getFromString(String protocol){       
               
         PlanObject plan = new PlanObject();       
               
         ArrayList<int[]> points = new ArrayList<>();      
         String objects[] = protocol.split(";");       
               
         for (int i = 0; i < objects.length; i++) {        
             if(i==0){     
                 plan.setTime(Integer.parseInt(objects[i]));       
             }else if(i==1){

                plan.setOrientation(objects[i].charAt(0));

             }else{        
                 String xy[] = objects[i].split(",");      
                 points.add(new int[]{Integer.parseInt(xy[0]),Integer.parseInt(xy[1])});       
             }     
         }     
               
         plan.setPoints(points);       
               
         return plan;      
     }     

     public void debugPoints(){
        System.out.println("Plan points");
        for(int []point:points){
            System.out.println(point[0]+","+point[1]);
        }
     }

     public ArrayList<int[]> transformPoints(int rw, int rh, int f, int c){

        ArrayList<int[]> tpoints = new ArrayList<>();   

        double cw = rw/c;
        double ch = rh/f;
        System.out.println("Transformed points");

        int px = (int)(points.get(0)[0]/cw);
        int py = (int)(points.get(0)[1]/ch);

        tpoints.add(new int[]{px,py});
        
        System.out.println(points.get(0)[0]+","+points.get(0)[1]+"---"+px+","+py);

        for(int []point:points){

            if (px != ((int)(point[0]/cw)) || py != ((int)(point[1]/ch))){

                px = (int)(point[0]/cw);
                py = (int)(point[1]/ch);

                tpoints.add(new int[]{px,py});
                System.out.println(point[0]+","+point[1]+"---"+px+","+py);

            }
        }

        return tpoints;

     }
       
     @Override     
     public String toString() {        
               
         StringBuilder str =  new StringBuilder();     
               
         str.append(time+";"); 
         str.append(orientation);   
         for (int i = 0; i < points.size(); i++) {     
             int point[] = points.get(i);                  
             str.append(";"+point[0]+","+point[1]);        
         }     
               
         return str.toString();        
     }
}