//C:\Users\alejandra\Downloads\JADE-all-4.4.0\JADE-bin-4.4.0\jade>javac    ----   este es el directoria donde tienes jadex!!
// javac -cp jade.jar behaviours/CallForPosition.java --- compilar
 //java -cp jade.jar;. jade.Boot -agents agent1:CameraAgent  -- crear el agente "agent1" de clase "Camera Agent"
 //javac -cp jade.jar;. CameraAgent.java
//java -cp jade.jar:. jade.Boot -container -host 192.168.15.190 -port 1099 -agents agent2:RobotAgent
//java -cp jade.jar:jSerialComm.jar:. jade.Boot -agents camera1:CameraAgent -gui
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import behaviours.CameraPlanner;

public class CameraAgent extends Agent{

	protected void setup() { 
		
		System.out.println("Camera Agent"); 

		addBehaviour(new CameraPlanner());

    }

} 
