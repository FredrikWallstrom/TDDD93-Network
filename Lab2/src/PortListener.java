import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class PortListener {
    private final static Logger LOGGER = Logger.getLogger("PortListener");
    private static final PortListener INSTANCE = new PortListener();

    private static int port = 8080;

    private PortListener(){

    }

    public static void main(String[] args) {
        if (args.length >0) {
            port = Integer.parseInt(args[0]);
        }

        try{
            ServerSocket listener = new ServerSocket(port);
            LOGGER.log( Level.INFO, "listening to port number = " + port);
            while(true){
                // waits for TCP connection

                Socket socket = listener.accept();
                new Thread(new ServerProxy(socket)).start();

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PortListener getInstance() {
        return INSTANCE;
    }
}
