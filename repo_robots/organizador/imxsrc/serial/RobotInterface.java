package serial;

import com.fazecast.jSerialComm.*;
import java.io.OutputStream;
import java.io.InputStream;

public class RobotInterface{

	private SerialPort comPort;
	private int[][] mapMatrix;

	public RobotInterface(){

		mapMatrix = new int[3][3];
		
	}

	public void open(){
		comPort = SerialPort.getCommPorts()[0];
		comPort.openPort();
		System.out.println("Open: "+comPort.getDescriptivePortName());
	}

	public void write(char data){
		OutputStream os = comPort.getOutputStream();

		try{
			os.write((int)data);
			os.close();
		}catch(Exception ex){
			System.out.println("write "+ex);
		}
	}


	public void write(byte[] data){
		OutputStream os = comPort.getOutputStream();

		try{
			os.write(data);
			os.close();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}

	private void cleanMap(){
		for (int i = 0; i < 3; ++i){
	    	for (int j = 0; j < 3; ++j){
	    		mapMatrix[i][j] = 0;
			}
		}
	}

	public int[][] getMap(){
		InputStream in = comPort.getInputStream();
		int visionMap = 0;
		write('G');

		try{

			for (int j = 0; j < 32; ++j){
	      		//System.out.print((int)in.read()+",");
	      		visionMap = (int)in.read();
			}


			cleanMap();

			mapMatrix[1][1] = 1; //TU

			mapMatrix[1][2] = visionMap&1;

			visionMap = visionMap>>1;

			mapMatrix[0][2] = visionMap&1;

			visionMap = visionMap>>1;

			mapMatrix[0][1] = visionMap&1;


			visionMap = visionMap>>1;

			mapMatrix[0][0] = visionMap&1;

			visionMap = visionMap>>1;

			mapMatrix[1][0] = visionMap&1;


			for (int i = 0; i < 3; ++i){
		    	for (int j = 0; j < 3; ++j){
		    		System.out.print("["+mapMatrix[i][j]+"]");
				}
				System.out.println("");
			}

			in.close();

			return mapMatrix;

      	} catch (Exception e) { e.printStackTrace(); return null;}
	}

	public void close(){
		try{
			comPort.closePort();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}

}
