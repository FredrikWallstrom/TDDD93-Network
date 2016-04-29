import javax.swing.*;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.HashMap;        

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[] costs = new int[RouterSimulator.NUM_NODES];
	
	private int[] routeThrough = new int[RouterSimulator.NUM_NODES];
	private HashMap<Integer, int[]> neighboursDistVectors = new HashMap<>();
	private HashMap<Integer, Integer> neighbours = new HashMap<>();
	

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
		

		//initialize neighbours and who routing is done via.
		int[] neighboursCost = new int[RouterSimulator.NUM_NODES];
		for(int i = 0 ; i < costs.length; i++){
			neighboursCost[i] = RouterSimulator.INFINITY;	
			//TODO maybe need change after poissons
			if (costs[i] < RouterSimulator.INFINITY){
				routeThrough[i] = i;
				if(i != myID){
					neighbours.put(i, costs[i]);
					}
			}else{
				routeThrough[i]	= -1;	
			}
		}
		// initialize map for neighbours distance vectors
		for(int key : neighbours.keySet()){
			neighboursDistVectors.put(key, neighboursCost);
		}

		// Send update to neighbours
		notifyNeighbours();


		printDistanceTable();
		
  }
/*
  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
		int[] neighboursCost = pkt.mincost;
		neighboursDistVectors.put(pkt.sourceid, neighboursCost);
		boolean update = false;
		for(int i = 0; i <costs.length; i++){
			if(neighboursCost[i] + costs[pkt.sourceid] < costs[i]){
				costs[i] = neighboursCost[i] + costs[pkt.sourceid];
				routeThrough[i] = routeThrough[pkt.sourceid]; 
				update = true;
			}
		}
		if(update){
			notifyNeighbours();		
		}
		

		
  }

*/
  public void recvUpdate(RouterPacket pkt) {
		int[] neighboursCost = pkt.mincost;
		neighboursDistVectors.put(pkt.sourceid, neighboursCost);
		boolean update = false;
		for(int i = 0; i <costs.length; i++){
			for(int nbr : neighbours.keySet()){ 
				if(neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr) < costs[i]){


					costs[i] = neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr);
					routeThrough[i] = nbr; 
					update = true;
			}
		}
		if(update){
			notifyNeighbours();		
		}
  }

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);

  }
  

  //--------------------------------------------------
  public void printDistanceTable() {
		StringBuilder sb = new StringBuilder();
	  myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());
		myGUI.println(F.SPACES);

		// build distanceTable
		myGUI.println("Distancetable:");
		printTableHeader();
		

		for (int key : neighbours.keySet()){
			sb.append(F.format("nbr  "+ key + " |", 9));
			int[] nbrCosts = neighboursDistVectors.get(key);
			for(int c : nbrCosts){
				sb.append(F.format(c, 5));			
			}
			myGUI.println(sb.toString());
			sb.setLength(0);
		}

		//Build our distance vector and routes
		myGUI.println(F.SPACES);
		myGUI.println("Our distance vector and routes:");
		printTableHeader();
		sb.append(F.format("cost   |", 9));
		for(int c : costs){
			sb.append(F.format(c,5));
		}
		myGUI.println(sb.toString());
		sb.setLength(0);
		sb.append(F.format(" route  |", 9));
		for(int v : routeThrough){
			sb.append(F.format(v,5));
		}
		myGUI.println(sb.toString());
	}


	private void notifyNeighbours(){
	for (int key : neighbours.keySet()){
			RouterPacket pkt = new RouterPacket(myID, key, costs);
			sendUpdate(pkt);
		}
	}


	private void printTableHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append(F.format("dst |", 9));
		int i =0;
		while(i != costs.length){
			sb.append(F.format(i, 5));
			i++;
		}
		myGUI.println(sb.toString());
		myGUI.println(new String(new char[9+(i)*5]).replace("\0", "-"));
	}				

  

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
		int oldNeighbourValue = neighbours.get(dest);		
		neighbours.put(dest, newcost);

		notifyNeighbours();
  }
}
