package behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import utils.RobotController;
import java.util.ArrayList;


public class CallForPosition extends CyclicBehaviour{

	private int repliesCnt=0;
	private int repliesAgCnt=0;
	private int failureCnt = 0;
	private int besttimeplan=10000;
	private int bestMap=10000;
	private int totalAgents = 3;
	private int rows = 10;
	private int columns = 15;

	private AID bestCamera;
	private AID bestRobot;
	private String name;
	private String nameTag;
	private RobotController robotController;

	private static final String PLAN_TRADE = "plan-trade";
	private static final String MAP_TRADE = "map-trade";	

	private static boolean DEBUG = false;


	public void onStart(){
		
		name = myAgent.getLocalName();
		nameTag = "("+name+")";
		robotController = new RobotController();
	}

	public void action() {
	  	
	  	ACLMessage msg=myAgent.receive();

	  	if(msg !=null){
			
			
			// Si solicitan la posicion inicial
			if (msg.getPerformative() == ACLMessage.REQUEST){

				if(msg.getContent().equals("start")){
					
				
				}else if(msg.getContent().equals("map")){
					
				}else if(msg.getConversationId().equals("debug")){
					
					robotController.doAction(msg.getContent().charAt(0));

				}

			}

		}else {
			block();
		}

	}


}
