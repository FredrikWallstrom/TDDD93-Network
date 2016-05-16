import ChatApp.*;          // The package containing our stubs
import org.omg.CosNaming.*; // HelloClient will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;     // All CORBA applications need these classes.
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;

 
class ChatCallbackImpl extends ChatCallbackPOA
{
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public void callback(String notification)
    {
        System.out.println(notification);
    }
}

public class ChatClient
{
    static Chat chatImpl;
    
    public static void main(String args[])
    {
	try {
	    // create and initialize the ORB
	    ORB orb = ORB.init(args, null);

	    // create servant (impl) and register it with the ORB
	    ChatCallbackImpl chatCallbackImpl = new ChatCallbackImpl();
	    chatCallbackImpl.setORB(orb);

	    // get reference to RootPOA and activate the POAManager
	    POA rootpoa = 
		POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
	    
	    // get the root naming context 
	    org.omg.CORBA.Object objRef = 
		orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    
	    // resolve the object reference in naming
	    String name = "Chat";
	    chatImpl = ChatHelper.narrow(ncRef.resolve_str(name));
	    
	    // obtain callback reference for registration w/ server
	    org.omg.CORBA.Object ref = 
		rootpoa.servant_to_reference(chatCallbackImpl);
	    ChatCallback cref = ChatCallbackHelper.narrow(ref);
	    
	    // Application code goes below
		String input; 
		while(true){
			input = System.console().readLine();
			if(input.equals("quit")) {
				chatImpl.leave(cref);
				break;
			}else if(input.startsWith("join") && input.length() > 5){
				chatImpl.join(cref, input.substring(5));		
			}else if(input.equals("list")){
				chatImpl.list(cref);
			}else if(input.equals("leave")){
				chatImpl.leave(cref);			
			}else if(input.startsWith("post") && input.length() > 5){
				chatImpl.post(cref, input.substring(5));		
			}else if(input.startsWith("play") && input.length() == 6 &&
				(input.charAt(5) == 'x' || input.charAt(5) == 'o')){
				chatImpl.playGame(cref, input.charAt(5));
			}else if(input.startsWith("place") && input.length() >= 9){
				chatImpl.placePiece(cref, input.substring(6));
			}else if(input.equals("leave game")){
				chatImpl.leaveGame(cref);
			}else{
				System.out.println("Command not recognized");
			}
		}
	    
	} catch(Exception e){
	    System.out.println("ERROR : " + e);
	    e.printStackTrace(System.out);
	}
    }
}
