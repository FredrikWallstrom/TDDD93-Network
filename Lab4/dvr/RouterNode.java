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

	//Change here to activate/deactivate poisoned reverse
	private boolean poisonedReverse = true;
		

  /**
	*	The constructor will initialize the distancevectors and our neighbours list
	*/
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
		
		//Set the distance vectors for the neighbours to all routers to infinity in the beggining
		//Set the neighbours that each router have and who we route via (next hop).
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
			}
		}
		notifyNeighbours();
		printDistanceTable();
		
  }
	/**
	* add the received updated and check if this leads to new routes, if so we notify the neighbours.
	*/
  public void recvUpdate(RouterPacket pkt) {
 		System.arraycopy(pkt.mincost, 0, distanceVectors[pkt.sourceid], 0, RouterSimulator.NUM_NODES);
		boolean update = changeMinCost();
		if(update){
			notifyNeighbours();		
		}
  }

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);

  }
  

  /**
	*	Prints the distancetable, routes and costs for each router
	*/
  public void printDistanceTable() {
		StringBuilder sb = new StringBuilder();
	  myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());
		myGUI.println(F.SPACES);

		// build distanceTable
		myGUI.println("Distancetable:");
		printTableHeader();
		for(int nbr : neighbours){
			if(nbr != myID){
				sb.append(F.format("nbr  "+ nbr + " |", 9));
				for(int j = 0; j < RouterSimulator.NUM_NODES; j++) {			
					sb.append(F.format(distanceVectors[nbr][j], 5));
				}
				sb.append("\n");	
			}	
		}	
		//Build our distance vector and routes
		sb.append(F.SPACES).append("\n").append("Our distance vector and routes:");
		myGUI.println(sb.toString());
		sb.setLength(0);
		printTableHeader();
		sb.append(F.format("cost   |", 9));
		for(int c : costs){
			sb.append(F.format(c,5));
		}
		sb.append("\n");
		sb.append(F.format(" route  |", 9));
		for(int v : routeThrough){
			sb.append(F.format(v,5));
		}
		myGUI.println(sb.toString());
	}

	/**
	*	Iterate over the neighbours and send them a packet that has our new update.
	*	If poisoned Reverse is enabled then we will set a cost to infinity where we route through the 
	*	neighbour that we are about to send the update to.
	*/
	private void notifyNeighbours(){
		for(int nbr : neighbours){
			if(nbr == myID) continue;
			int[] tmpCosts = new int[RouterSimulator.NUM_NODES];
			if(poisonedReverse){	
				for(int i = 0; i < RouterSimulator.NUM_NODES; i++){
					if(nbr == routeThrough[i]){
						tmpCosts[i] = RouterSimulator.INFINITY;
					}else{
						tmpCosts[i] = costs[i];
					}
				}
			}else{
				System.arraycopy(costs, 0, tmpCosts, 0, RouterSimulator.NUM_NODES);
			}
			RouterPacket pkt = new RouterPacket(myID, nbr, tmpCosts);
			sendUpdate(pkt);
		}
	}

	/**
	*	Help function for the printDistanceTable.
	*/
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
		
		int cost = 0;
		int[] tmpCosts = new int[RouterSimulator.NUM_NODES];
		int[] tmpRouteThrough = routeThrough;

		for(int i = 0; i < RouterSimulator.NUM_NODES; i++){
			int minCost = RouterSimulator.INFINITY;
			if(i == myID) continue;	
			for(int nbr : neighbours) {
				if(nbr == myID)continue;	
				cost = distanceVectors[nbr][i];
				if(cost + distanceVectors[myID][nbr] < minCost){
					minCost = cost + distanceVectors[myID][nbr];
					tmpCosts[i] = minCost;
					tmpRouteThrough[i] = nbr;								
				}
			}
		}
		for(int i = 0; i < RouterSimulator.NUM_NODES; i++){
			if (costs[i] != tmpCosts[i] || routeThrough[i] != tmpRouteThrough[i]){
				System.arraycopy(tmpCosts, 0, costs, 0, RouterSimulator.NUM_NODES);
				System.arraycopy(tmpRouteThrough, 0, routeThrough, 0, RouterSimulator.NUM_NODES);
				update = true;
			}
		}
		return update;
	}
	
	/**
	*	add the received update for the link cost and check if this
	* leads to new routes, if so we notify the neighbours.
	*/	
  public void updateLinkCost(int dest, int newcost) {
		distanceVectors[myID][dest] = newcost;
		boolean update = false;;
		update = changeMinCost();
		if(update){	 
			notifyNeighbours();		
		}
	}
}
