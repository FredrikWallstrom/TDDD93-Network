import java.net.Socket;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ServerProxy implements Runnable{
    private Socket socket;

    public ServerProxy(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }

    public boolean checkURL(String request){

        return true;
    }


}
