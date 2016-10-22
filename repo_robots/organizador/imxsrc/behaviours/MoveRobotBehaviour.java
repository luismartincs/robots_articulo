package behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.Agent;
//import jade.core.behaviours.WakerBehaviour;


public class MoveRobotBehaviour extends Behaviour{

	private long offset = 1000;
	private long prevTime = 0;
	
	/*
	public MoveRobotBehaviour(Agent a, long timeout){
		super(a,timeout);
	}

	protected void onWake() { 
		System.out.println("Moving motors");
	}*/

	
	
	public void onStart(){
		System.out.println("The move is getting ready!");
		prevTime = System.currentTimeMillis();
	}

	public void action() {
	    while (true) {
	    	
	    	long currentTime = System.currentTimeMillis();

	    	if(currentTime - prevTime >= offset){
	    		prevTime = currentTime;
	    		System.out.println("Moving the motors");
			}
		} 
	}

	public boolean done() { 
		return true;
	}

}