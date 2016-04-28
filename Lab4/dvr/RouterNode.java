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
	private ArrayList<Integer> neighbours = new ArrayList<>();
	

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
					neighbours.add(i);
					}
			}else{
				routeThrough[i]	= -1;	
			}
		}
		// initialize map for neighbours distance vectors
		int[] neighboursCost = new int[RouterSimulator.NUM_NODES];
		for(int i = 0; i < RouterSimulator.NUM_NODES ; i++){
			neighboursCost[i] = RouterSimulator.INFINITY;	
		}
		for(int j = 0; j < neighbours.size(); j++){
			neighboursDistVectors.put(neighbours.get(j), neighboursCost);

		}
		// Send update to neighbours
		for (int n : neighbours){
			RouterPacket pkt = new RouterPacket(myID, n, costs);
			sendUpdate(pkt);
		}


		printDistanceTable();
		
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
		int[] neighboursCost = pkt.mincost;
		neighboursDistVectors.put(pkt.sourceid, neighboursCost);
		
		
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
		

		for (int nbr : neighbours){
			sb.append(F.format("nbr  "+ nbr + " |", 9));
			int[] nbrCosts = neighboursDistVectors.get(nbr);
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
		

  }

}
