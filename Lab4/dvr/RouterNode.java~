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
	private int[][] distanceVectors = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
	private ArrayList<Integer> neighbours = new ArrayList<>();
	private HashMap<Integer, int[]> neighboursDistVectors = new HashMap<>();	
	

	//private HashMap<Integer, Integer> neighbourss = new HashMap<>();
	

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
		

		//initialize neighbours and who routing is done via.
		
		for(int i = 0 ; i < RouterSimulator.NUM_NODES; i++){
			if (costs[i] < RouterSimulator.INFINITY){
				routeThrough[i] = i;
				neighbours.add(i);		
				for(int j = 0; j < RouterSimulator.NUM_NODES; j++) {
					if(i != myID){
								
						distanceVectors[i][j] = RouterSimulator.INFINITY;
					}else{
						distanceVectors[i][j] = costs[j];		
					}
				}
			}else{
					routeThrough[i]	= -1;
					//distanceVectors[i] = null;	
			}
		}
	
		// initialize map for neighbours distance vectors
/*

		for(int key : neighbours.keySet()){
			int[] neighboursCost = new int[RouterSimulator.NUM_NODES];
			Arrays.fill(neighboursCost, RouterSimulator.INFINITY);
			neighboursDistVectors.put(key, neighboursCost);
		}
*/
		// Send update to neighbours
		notifyNeighbours();
		

		printDistanceTable();
		
  }

  public void recvUpdate(RouterPacket pkt) {
		//int[] neighboursCost = new int[RouterSimulator.NUM_NODES]; // pkt.mincost;
 		System.arraycopy(pkt.mincost, 0, distanceVectors[pkt.sourceid], 0, RouterSimulator.NUM_NODES);
		//distanceVectors[pkt.sourceid] = neighboursCost;		
		//neighboursDistVectors.put(pkt.sourceid, neighboursCost);
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
		
		for(int nbr : neighbours){
			//if(nbr != myID){
				sb.append(F.format("nbr  "+ nbr + " |", 9));
				for(int j = 0; j < RouterSimulator.NUM_NODES; j++) {			
					sb.append(F.format(distanceVectors[nbr][j], 5));
				}	
				myGUI.println(sb.toString());
				sb.setLength(0);
			//}	
		}	
		
/*
		for(int i = 0 ; i < RouterSimulator.NUM_NODES; i++){
			if(distanceVectors[i] != null && i != myID){	
				sb.append(F.format("nbr  "+ i + " |", 9));
				for(int j = 0; j < RouterSimulator.NUM_NODES; j++) {			
					sb.append(F.format(distanceVectors[i][j], 5));
				}	
				myGUI.println(sb.toString());
				sb.setLength(0);	
			}	
		}
*/
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
		for(int nbr : neighbours){
			if(nbr != myID){
				RouterPacket pkt = new RouterPacket(myID, nbr, costs);
				sendUpdate(pkt);
			}
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
	*/
	private boolean changeMinCost(){
		boolean update = false;
		
		int tmpCost = 0;
		int[] tmpMinCosts = new int[RouterSimulator.NUM_NODES];

		for(int i = 0; i < RouterSimulator.NUM_NODES; i++){
			int minCost = RouterSimulator.INFINITY;
			if(i != myID){	
				for(int nbr : neighbours) {
					if(nbr != myID){	
					tmpCost = distanceVectors[nbr][i];
						if(tmpCost + distanceVectors[myID][nbr] < minCost){
							minCost = tmpCost + distanceVectors[myID][nbr];
							tmpMinCosts[i] = minCost;
						//	myGUI.println("my id  " + myID + "  min cost is  " +  );
							routeThrough[i] = nbr;							
						}
					}
				}
			}
		}
		//System.out.println("\n\n");
		for(int i = 0; i < RouterSimulator.NUM_NODES; i++){
			//	System.out.println(costs[i]+ "    " +tmpMinCosts[i]);
			if (costs[i] != tmpMinCosts[i]){
				System.arraycopy(tmpMinCosts, 0, costs, 0, RouterSimulator.NUM_NODES);
				update = true;
			}
		}
/*
		if(!(tmpMinCosts.equals(costs))){
			update = true;
		}
*/
			

/*
			for(int nbr : neighbours.keySet()){ 
				//myGUI.println("grannen " + nbr + " till nod : " + i+ "är " + neighboursDistVectors.get(nbr)[i] + "vi " + myID + "till grannen : " + neighbours.get(nbr) + " och den nuvarande misnta kostnaden är " +  costs[i]);
				if(neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr) < costs[i]){
					costs[i] = neighboursDistVectors.get(nbr)[i] + neighbours.get(nbr);
					routeThrough[i] = nbr; 
					update = true;
				}
			}
		}*/
		return update;
	}
	

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
		//int oldNeighbourValue = neighbours.get(dest);	
		int tmp = costs[dest];	
		//neighbours.put(dest, newcost);
		costs[dest] = newcost;

		boolean update = false;
// update value if we route through the node that is our dest.
		/*for(int i = 0; i<costs.length; i++){
			if(routeThrough[i] == dest){
				costs[i] += (newcost - oldNeighbourValue);
				update = true;
			}
		}*/
		//if(update){
		update = changeMinCost();
		if(update){	 //&& changeMinCost()){
			notifyNeighbours();
			//}			
		}
	}
}
