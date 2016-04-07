import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ServerProxy implements Runnable{
    private Socket socket;
    private ClientProxy client;

    public ServerProxy(Socket socket) {
        this.socket = socket;
        this.client = new ClientProxy();
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        BufferedReader br;
        String stringLine;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while((stringLine = br.readLine()) != null){
                sb.append(stringLine);
                sb.append("\r\n");
                //see if it is end of request
                if(stringLine.isEmpty()){
                    PortListener.LOGGER.log(Level.INFO, "This request is made = " + sb.toString());
                    //send request to client and let client talk to Webserver
                    client.makeRequest(sb.toString());
                    //prepare for new request
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkURL(String request){
        return true;
    }


}
