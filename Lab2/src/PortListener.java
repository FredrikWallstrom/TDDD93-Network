import com.sun.corba.se.spi.activation.Server;

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
public class PortListener implements Runnable{
    public final static Logger LOGGER = Logger.getLogger("myLogger");

    private static PortListener INSTANCE = null;
    private static int port = 8080;

    /**
     * Singleton hence the private constructor.
     */
    private PortListener(){
    }

    /**
     * Main function that can start the listener that listens to request on a specific port.
     * Can be started with argument so user can choose port. If no arguments are given it listens to port 8080.
     * Feature 7 is implemented in this method.
     * @param args
     */
    public static void main(String[] args) {
        // Feature 7
        if (args.length >0) {
            port = Integer.parseInt(args[0]);
        }
        new Thread(getInstance()).start();
    }

    /**
     * Create a instance of PortListener if there is none.
     * @return the Instance of this class.
     */
    public static PortListener getInstance() {
        if (INSTANCE == null){
            INSTANCE = new PortListener();
        }
        LOGGER.setLevel(Level.SEVERE);
        return INSTANCE;
    }

    /**
     * Listen to port number and create a new thread that will run the server proxy.
     */
    @Override
    public void run() {
        try(ServerSocket listener = new ServerSocket(port)) {
            LOGGER.log(Level.SEVERE, "listening to port number = " + port);
            while (true) {
                // waits for TCP connection
                Socket socket = listener.accept();
                new Thread(new ServerProxy(socket)).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
