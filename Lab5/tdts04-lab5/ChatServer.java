import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 
import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;

import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;

 
class ChatImpl extends ChatPOA
{
	private static final int BOARDSIZE = 9;

    private ORB orb;
	private HashMap<String, Character> activePlayers = new HashMap();
	private HashMap<String, ChatCallback> activeUsers = new HashMap();
	private char[][] board = new char[BOARDSIZE][BOARDSIZE];
	private int boardCounter = BOARDSIZE*BOARDSIZE;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public String say(ChatCallback callobj, String msg)
    {
        callobj.callback(msg);
        return ("         ....Goodbye!\n");
    }

	public void join(ChatCallback callobj, String username) {
		if(activeUsers.containsKey(username) || activeUsers.containsValue(callobj)) {
			callobj.callback("Error: user " + username + 
					" already an active chatter or you are already active in the");		
		}else{
			activeUsers.put(username, callobj);
			notifyActiveUsers(callobj, username + " joined");
			callobj.callback("Welcome " + username + "\nTo start five-in-a-row type: play x or play o");	 
		}			
	} 
	
	public void list(ChatCallback callobj){
		StringBuilder list = new StringBuilder();
		list.append("List of registered users:");
		for(String username : activeUsers.keySet()){
			list.append("\n").append(username);				
		}
		callobj.callback(list.toString());
	}

	public void post(ChatCallback callobj, String msg){
		String user = getUserName(callobj);
		if(user != null){
			notifyActiveUsers(null, user + " said: " + msg);
		}
	}
	
	public void leave(ChatCallback callobj){
		String userToLeave = getUserName(callobj);
		if(userToLeave != null){
			notifyActiveUsers(callobj, userToLeave + " left");
			activeUsers.remove(userToLeave);
			if(activePlayers.containsKey(userToLeave)){
				activePlayers.remove(userToLeave);
			}
			callobj.callback("Goodbye " + userToLeave);													
		}						
	}

	public void leaveGame(ChatCallback callobj){
		String user = getUserName(callobj);
		if(activePlayers.containsKey(user)){
			broadcastToPlayers(user + " left the game");
			activePlayers.remove(user);
		}
	}

	public void playGame(ChatCallback callobj, char pieceType){
		String user = getUserName(callobj);
		if(user == null || activePlayers.containsKey(user)) return;	
		if(activePlayers.isEmpty()) initializeBoard();

		activePlayers.put(user, new Character(pieceType));
		broadcastToPlayers(user + " joined game");	
		displayGameInstructions(callobj);
		String boardAsText = convertBoardToText();
		callobj.callback(boardAsText);
	}

	private void displayGameInstructions(ChatCallback callobj){
		String instructions = "---------\nGAME INSTRUCTIONS\n---------\n" + 
			"COMMANDS:\n" + 
			"place x,y  : to place piece on row x and column y\n" +
			"leave game : to leave this current game\n\n" +
			"RULES:\n" + 
			"You can place your markers where the row and column contains a '-' symbol. A team wins when your symbol (x or o) occur 5 times in a row. This can be vertical, horizontal or diagonal.\n";
			callobj.callback(instructions);
	}

	public void placePiece(ChatCallback callobj, String positions){
		String user = getUserName(callobj);
		if(user == null || !activePlayers.containsKey(user)) return;	

		String[] data = positions.split(",");
		if(data.length != 2){
			callobj.callback("Error: Input format is invalid. It should be of form x,y");
			return;
		}
		int row = Integer.parseInt(data[0]);
		int col = Integer.parseInt(data[1]);

		if(isPositionValid(row, col, callobj)){
			boardCounter--;
			// Check if board is full.
			if(boardCounter == 0){
				broadcastToPlayers("Game ended in a tie\n");
				startNewGame();
				boardCounter = BOARDSIZE * BOARDSIZE;
			}else{
				Character team = activePlayers.get(user);
				board[row][col] = new Character(team);
				String boardAsText = convertBoardToText();
				broadcastToPlayers(boardAsText);
				if(gameOver(row, col, user)){
					presentWinners(team);	
					startNewGame();
				}
			}
		}
	}
	

	private void startNewGame(){
		initializeBoard();
		String boardAsText = convertBoardToText();
		broadcastToPlayers(boardAsText);
	}

	private void presentWinners(Character team){
		StringBuilder sb = new StringBuilder();
		sb.append("The Winners are team " + team + ": \n");
		for(String user : activePlayers.keySet()){
			if(activePlayers.get(user).equals(team)){
				sb.append(user+"\n");
			}
		}
		sb.append("A new game have started \n");
		broadcastToPlayers(sb.toString());
	}

	private boolean gameOver(int row, int col, String user){
		char team = new Character(activePlayers.get(user));

		for(Directions direction: Directions.values()){
			boolean winner = fiveInARow(row, col, direction, team);
			if(winner) return true;
		}

		return false;
	}

	private boolean fiveInARow(int row, int col, 
		Directions direction, char team){
		int count = 1;
		int initialRow = row;
		int initialCol = col;
		int stepRow = direction.row;
		int stepCol = direction.col;

		for(int dir = 0; dir < 2; dir++){
			for(int k = 0; k < 4; k++){
				row += stepRow;
				col += stepCol;
				if(row == -1 || row == BOARDSIZE || col == -1 || 
					col == BOARDSIZE || board[row][col] != team) break;
				count++;
				if(count == 5) return true;
			
			}
			// Change to opposite direction.
			row = initialRow;
			col = initialCol;
			stepRow = stepRow*(-1);
			stepCol = stepCol*(-1);
		}
		return false;
	}

	private void broadcastToPlayers(String msg){
		for(String user : activePlayers.keySet()){
			(activeUsers.get(user)).callback(msg);
		}
	}
	
	private boolean isPositionValid(int row, int col, ChatCallback callobj){
		if((row >= BOARDSIZE || row < 0) || (col >= BOARDSIZE || row < 0)){
			callobj.callback("Error: Input position is not valid. Input position should be between 0 and " + (BOARDSIZE - 1));
			return false;
		}
		if(board[row][col] != '-'){
			callobj.callback("Error: Input position is not valid. Position is already taken");
			return false;
		}
		return true;
	}
	private void notifyActiveUsers(ChatCallback callobjToIgnore, String msg){
		for(ChatCallback callobj : activeUsers.values()){
			if (!callobj.equals(callobjToIgnore)){
				callobj.callback(msg);
			}
		}
	}

	private String getUserName(ChatCallback callobj){
		for(Map.Entry<String, ChatCallback> entry : activeUsers.entrySet()){
			if(entry.getValue().equals(callobj)){
				return entry.getKey();
			}
		}
		return null;
	}

	private void initializeBoard(){
		for(int row = 0; row < BOARDSIZE; row++){
			for(int col = 0; col < BOARDSIZE; col++){
				board[row][col] = '-';
			}
		}
	}

	private String convertBoardToText(){
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for(int i = 0; i < BOARDSIZE; i++){
			sb.append("  ").append(i);
		}
		for(int row = 0; row < BOARDSIZE; row++){
			sb.append("\n");		
			sb.append(row).append("  ");				
			for(int col = 0; col < BOARDSIZE; col++){
				sb.append(board[row][col]).append("  ");
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	enum Directions{
		HORIZONTAL(0,1),
		VERTICAL(1,0),
		DIAGONAL_DOWN_RIGHT(1,1),
		DIAGONAL_DOWN_LEFT(1,-1);

		private final int row;
		private final int col;

		private Directions(final int row, final int col){
		this.row = row;
		this.col = col;
		}
	}
}

public class ChatServer 
{
    public static void main(String args[]) 
    {
	try { 
	    // create and initialize the ORB
	    ORB orb = ORB.init(args, null); 

	    // create servant (impl) and register it with the ORB
	    ChatImpl chatImpl = new ChatImpl();
	    chatImpl.setORB(orb); 

	    // get reference to rootpoa & activate the POAManager
	    POA rootpoa = 
		POAHelper.narrow(orb.resolve_initial_references("RootPOA"));  
	    rootpoa.the_POAManager().activate(); 

	    // get the root naming context
	    org.omg.CORBA.Object objRef = 
		           orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	    // obtain object reference from the servant (impl)
	    org.omg.CORBA.Object ref = 
		rootpoa.servant_to_reference(chatImpl);
	    Chat cref = ChatHelper.narrow(ref);

	    // bind the object reference in naming
	    String name = "Chat";
	    NameComponent path[] = ncRef.to_name(name);
	    ncRef.rebind(path, cref);

	    // Application code goes below
	    System.out.println("ChatServer ready and waiting ...");
	    
	    // wait for invocations from clients
	    orb.run();
	}
	    
	catch(Exception e) {
	    System.err.println("ERROR : " + e);
	    e.printStackTrace(System.out);
	}

	System.out.println("ChatServer Exiting ...");
    }

}
