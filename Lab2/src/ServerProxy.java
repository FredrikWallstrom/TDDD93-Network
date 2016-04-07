import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
        StringBuilder sb = null;
        String subString;
        int character;
        try {
            InputStream is = socket.getInputStream();
            while((character = is.read()) != -1){
                sb.append(character);
                if(sb.length() >=4){
                    //see if it is end of request
                    subString = sb.substring(sb.length()-4, sb.length());
                    if(subString.equals("\r\n\r\n")){
                        client.makeRequest(sb.toString());
                        sb = null;
                    }
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
