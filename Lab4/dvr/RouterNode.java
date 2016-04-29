import javax.swing.*;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Arrays;       

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
		
		for(int i = 0 ; i < costs.length; i++){
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
			int[] neighboursCost = new int[RouterSimulator.NUM_NODES];
			Arrays.fill(neighboursCost, RouterSimulator.INFINITY);
			//neighboursCost[i] = RouterSimulator.INFINITY;	
			neighboursDistVectors.put(key, neighboursCost);
		}

		// Send update to neighbours
		notifyNeighbours();


		printDistanceTable();
		
  }

  public void recvUpdate(RouterPacket pkt) {
		int[] neighboursCost = new int[RouterSimulator.NUM_NODES]; // pkt.mincost;
 		System.arraycopy(pkt.mincost, 0, neighboursCost, 0, RouterSimulator.NUM_NODES);
		neighboursDistVectors.put(pkt.sourceid, neighboursCost);
		boolean update = changeMinCost();
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

	/**
	 * changes costs if there is a cheaper path. Returns true if an update was made, otherwise false.
	**/
	private boolean changeMinCost(){
		boolean update = false;
		for(int i = 0; i <costs.length; i++){
			for(int nbr : neighbours.keySet()){ 
				//myGUI.println("grannen " + nbr + " till nod : " + i+ "är " + neighboursDistVectors.get(nbr)[i] + "vi " + myID + "till grannen : " + neighbours.get(nbr) + " och den nuvarande misnta kostnaden är " +  costs[i]);
				if(neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr) < costs[i]){
					costs[i] = neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr);
					routeThrough[i] = nbr; 
					update = true;
				}
			}
		}
		return update;
	}				

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
		int oldNeighbourValue = neighbours.get(dest);	
		int tmp = costs[dest];	
		neighbours.put(dest, newcost);
		boolean update = false;
// update value if we route through the node that is our dest.
		for(int i = 0; i<costs.length; i++){
			if(routeThrough[i] == dest){
				costs[i] += (newcost - oldNeighbourValue);
				update = true;
			}
		}
		//if(update){
		if(update &&changeMinCost()){
			notifyNeighbours();
			//}			
		}
	}
}
