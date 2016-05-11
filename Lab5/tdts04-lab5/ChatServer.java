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
			callobj.callback("Welcome " + username);	 
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
			//TODO kolla att leave tar ut fran active players genom att joina ett spel sen ga ut och kolla att 
			//spelaren inte ar kvar nar vinnarna presenteras
			if(activePlayers.containsKey(userToLeave)){
				activePlayers.remove(userToLeave);
			}
			callobj.callback("Goodbye " + userToLeave);													
		}						
	}

	public void playGame(ChatCallback callobj, char pieceType){
		String user = getUserName(callobj);
		if(user == null || activePlayers.containsKey(user)) return;	
		if(activePlayers.isEmpty()) initializeBoard();

		activePlayers.put(user, new Character(pieceType));
		String boardAsText = convertBoardToText();
		callobj.callback(boardAsText);
		


	}
	
	private void notifyActiveUsers(ChatCallback callobjToIgnore, String msg){
		for(ChatCallback callobjRef : activeUsers.values()){
			if (!callobjRef.equals(callobjToIgnore)){
				callobjRef.callback(msg);
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
		//sb.append("\n");
		for(int row = 0; row < BOARDSIZE; row++){
			sb.append("\n");		
			sb.append(row).append("  ");				
			for(int col = 0; col < BOARDSIZE; col++){
				sb.append(board[row][col]).append("  ");
			}
		
		}
		return sb.toString();
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
