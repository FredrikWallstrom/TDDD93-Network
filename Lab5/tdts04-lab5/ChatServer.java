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
    private ORB orb;
	private HashMap<String, ChatCallback> activeUsers = new HashMap();

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public String say(ChatCallback callobj, String msg)
    {
        callobj.callback(msg);
        return ("         ....Goodbye!\n");
    }

	public boolean join(ChatCallback callobj, String username) {
		if(activeUsers.containsKey(username) || activeUsers.containsValue(callobj)) {
			return false;		
		}else{
			activeUsers.put(username, callobj);
			notifyActiveUsers(callobj, username + " joined");
			callobj.callback("Welcome " + username);	 
		}
		return true;			
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
		for(Map.Entry<String, ChatCallback> entry : activeUsers.entrySet()){
			if(entry.getValue().equals(callobj)){
				notifyActiveUsers(null, entry.getKey() + " said: " + msg);
			}
		}
	}
	
	public void leave(ChatCallback callobj){
		String userToLeave = null;		
		for(Map.Entry<String, ChatCallback> entry : activeUsers.entrySet()){
			if(entry.getValue().equals(callobj)){
				userToLeave = entry.getKey();
				notifyActiveUsers(callobj, userToLeave + " left");								
				activeUsers.remove(userToLeave);
				break;											
			}		
		}	
		callobj.callback("Goodbye " + userToLeave);					
	}
	
	private void notifyActiveUsers(ChatCallback callobjToIgnore, String msg){
		for(ChatCallback callobjRef : activeUsers.values()){
			if (!callobjRef.equals(callobjToIgnore)){
				callobjRef.callback(msg);
			}
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
