/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaz;

import com.fazecast.jSerialComm.*;
import java.io.OutputStream;
import java.io.InputStream;

public class RobotInterface{

	private SerialPort comPort;

	public RobotInterface(){

		
	}

	public void open(){
		comPort = SerialPort.getCommPorts()[0];
//                comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                
		comPort.openPort();
		System.out.println("Open: "+comPort.getDescriptivePortName());
	}

	public void write(char data){
		OutputStream os = comPort.getOutputStream();

		try{
			os.write(data);
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

	public void close(){
		try{
			comPort.closePort();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}

}