package behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import serial.RobotInterface;
import jade.core.AID;
import jaya.PlanObject;
import utils.RobotMap;
import utils.CameraUtils;
import utils.MathUtils;
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
	private PlanObject plan;
	private RobotInterface serialComm;
	private RobotMap mapa;

	private static final String PLAN_TRADE = "plan-trade";
	private static final String MAP_TRADE = "map-trade";	

	private static boolean DEBUG = false;


	public void onStart(){
		
		name = myAgent.getLocalName();
		nameTag = "("+name+")";

		mapa = new RobotMap(rows,columns);
		//mapa.print();

		serialComm = new RobotInterface();

	}

	public void action() {
	  	
	  	ACLMessage msg=myAgent.receive();

	  	if(msg !=null){
			
			
			// Si solicitan la posicion inicial
			if (msg.getPerformative() == ACLMessage.REQUEST){

				if(msg.getContent().equals("start")){
					start();
				
				}else if(msg.getContent().equals("map")){
					cfpMap();
				}else if(msg.getConversationId().equals("debug")){
					
					serialComm.open();
					serialComm.write(msg.getContent().charAt(0));
					serialComm.close();
				}

			}else if(msg.getPerformative() == ACLMessage.PROPOSE){

			
				if(msg.getConversationId().equals(MAP_TRADE)){
					receiveMapProposal(msg);
				}else{
					receiveProposal(msg);
				}
				
				
			}else if(msg.getPerformative() == ACLMessage.INFORM){

				if(msg.getConversationId().equals(MAP_TRADE)){

					doMovement(msg);

				}else{

					plan = PlanObject.getFromString(msg.getContent());

					System.out.println(nameTag + " Tiempo final del plan "+plan.getTime());
					System.out.println(nameTag + " Plan final: "+plan.toString());
					System.out.println(nameTag + " Orientacion del robot: "+plan.getOrientation());

					cfpMap();
				}

			}else if (msg.getPerformative() == ACLMessage.CFP){

				System.out.println(nameTag + " Propose discovered map "+mapa.getDiscoveredSpace());

				proposeMap(msg,mapa.getDiscoveredSpace());
			
			}else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){

				System.out.println(nameTag+" Aceptaron propuesta");

				inform(msg);

			}else if(msg.getPerformative() == ACLMessage.FAILURE){
				System.out.println("Ocurrio un error, se reintentara x segundos");
				//CREAR METODO RETRY PARA EL AGENTE QUE REPORTA EL FALLO
			}

		}else {
			block();
		}

	}

	//==================== Acciones individuales ====================


	private void doMovement(ACLMessage msg){


		//SCAM

		serialComm.open();
		serialComm.write('S');
		
		try{Thread.sleep(15*1000);}catch(Exception ex){}

		int [][] myArea = serialComm.getMap();
		

		//MOVEMENT

		RobotMap receivedMap = RobotMap.getFromString(msg.getContent(),rows,columns);

		mapa.merge(receivedMap);

		//plan.debugPoints();

		int resolution[] = CameraUtils.getResolution(bestCamera.getLocalName());

		ArrayList<int[]>  tpoints = plan.transformPoints(resolution[0],resolution[1],rows,columns);

		byte command[] = new byte[3+(tpoints.size()*2)];

		command[0] = 'M';
		command[1] = (byte)plan.getOrientation();

		command[command.length-1] = 127; 

		int next = 2;
		for(int []p: tpoints){
			command[next] = (byte)p[0];
			command[next+1] = (byte)p[1];
			next+=2;
		}

		mapa.mergeMatrixAt(myArea,tpoints.get(0)[1],tpoints.get(0)[0]);
		mapa.print();

		serialComm.write(command);

		serialComm.close();
	}

	//ACCIONES PARA OTRO AGENTE

	private void cfpMap(){

		int robotId = Integer.parseInt(myAgent.getLocalName().replace("robot",""));

		System.out.println(nameTag + " Solicitando Mapa "+robotId);
		
		ACLMessage cfp=new ACLMessage(ACLMessage.CFP);

		for (int i=0; i < totalAgents; i++) {

			if(i == (robotId-1))continue;

			System.out.println(nameTag + " send to "+"robot"+(i+1));
			cfp.addReceiver(new AID("robot"+(i+1),AID.ISLOCALNAME));
		}
		

		cfp.setContent("get-map");
		cfp.setConversationId(MAP_TRADE);
		myAgent.send(cfp);

	}


	private void proposeMap(ACLMessage msg,int discovered){
		
		ACLMessage proposal=msg.createReply();
		proposal.setPerformative(ACLMessage.PROPOSE);
					
		proposal.setContent(discovered+"");
		proposal.setConversationId(MAP_TRADE);
		myAgent.send(proposal);
	
	}

	private void receiveMapProposal(ACLMessage msg){
		
		System.out.println(nameTag + " Reply num: "+repliesAgCnt);
		System.out.println(nameTag + " Proposal: "+msg.getContent());

		int mapSize = Integer.parseInt(msg.getContent());
				
		if  (mapSize < bestMap){
			bestMap = mapSize;
			bestRobot = msg.getSender();
		}
				
		System.out.println(nameTag + " El nuevo mejor mapa es "+bestMap);

		repliesAgCnt++;

		if(repliesAgCnt >= 2){
			repliesAgCnt = 0;
			acceptMap(msg);
		}

	}

	private void acceptMap(ACLMessage msg){

		if (bestRobot !=null){
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(bestRobot);
			order.setContent("map-accepted");
			order.setConversationId(MAP_TRADE);
			myAgent.send(order);
		}	

	}


	private void inform(ACLMessage contract){

		System.out.println(nameTag+" Me contrataron");

		ACLMessage data = new ACLMessage(ACLMessage.INFORM);

		data.addReceiver(contract.getSender());
		data.setContent(mapa.toString());
		data.setConversationId(MAP_TRADE);
		myAgent.send(data);
	}

	//ACCIONES PARA LA CAMARA

	private void start(){
		System.out.println(nameTag + " Iniciando agente");
		
		ACLMessage cfp=new ACLMessage(ACLMessage.CFP);
		cfp.addReceiver(new AID("camera1",AID.ISLOCALNAME));
		cfp.addReceiver(new AID("camera2",AID.ISLOCALNAME));
		cfp.setContent("get-plan");
		cfp.setConversationId(PLAN_TRADE);
		myAgent.send(cfp);

	}

	private void receiveProposal(ACLMessage msg){
		
		System.out.println(nameTag + " Reply num: "+repliesCnt);
		System.out.println(nameTag + " Proposal: "+msg.getContent());

		int timeplan = Integer.parseInt(msg.getContent());
				
		if  (timeplan < besttimeplan){
			besttimeplan=timeplan;
			bestCamera=msg.getSender();
		}
				
		System.out.println(nameTag + " El nuevo mejor tiempo es "+besttimeplan);

		repliesCnt++;

		if(repliesCnt >= 2){
			repliesCnt = 0;
			accept(msg);
		}/*else if(repliesCnt >= 1 && failureCnt > 0){

		}*/

	}

	private void accept(ACLMessage msg){

		if (bestCamera !=null){
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(bestCamera);
			order.setContent("You won");
			order.setConversationId(PLAN_TRADE);
			myAgent.send(order);
		}	

	}

	private void move(ACLMessage msg){

		String data[] = msg.getContent().split(",");

		System.out.println(nameTag + " Move to "+data[0]+","+data[1]);

		//INTEGRAR CON ARDUINO
		
		next();

	}


	private void next(){

		if (bestCamera !=null){
			ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
			order.addReceiver(bestCamera);
			order.setContent("next");
			order.setConversationId(PLAN_TRADE);
			myAgent.send(order);
		}	

	}

}
