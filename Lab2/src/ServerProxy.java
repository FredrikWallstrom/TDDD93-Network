import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ServerProxy implements Runnable{
    private Socket socket;
    private ClientProxy client;
    String badURL = "http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html";

    public ServerProxy(Socket socket) {
        this.socket = socket;
        this.client = new ClientProxy();
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        boolean connectionClosed = false;

        String stringLine;
        try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            while((stringLine = buffReader.readLine()) != null) {
                sb.append(stringLine);
                sb.append("\r\n");
                //see if it is end of request
                if (stringLine.isEmpty()) {

                    if (!Filtering.isStringValid(sb.toString())) {
                        int startIndex = sb.toString().indexOf(" ") + 1;

                        // change current url to the redirected one
                        for (int i = startIndex; i < sb.toString().length(); i++) {
                            if (sb.toString().charAt(i) == ' ') {
                                String tmp = sb.toString();
                                tmp = tmp.replaceFirst(tmp.substring(startIndex, i), badURL);
                                sb.setLength(0);
                                sb.append(tmp);
                                break;
                            }
                        }
                    }


                    // check if the connection is closed or not
                    if (sb.toString().toLowerCase().contains("connection: close")) {
                        connectionClosed = true;
                    }

                    //send request to client and let client talk to Webserver
                    PortListener.LOGGER.log(Level.INFO, "This request is made = " + sb.toString());
                    ArrayList<byte[]> httpResponse = client.makeRequest(sb.toString());

                    // response to client
                    OutputStream os = socket.getOutputStream();
                    PortListener.LOGGER.log(Level.INFO, "This response is made = " + httpResponse.toString());
                    for (byte[] br : httpResponse) {
                        os.write(br);
                    }
                    os.flush();
                    sb.setLength(0);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    change header back to connection: keep alive
     */
    private String formatHeader(String httpResponse, boolean connectionClosed) {
        int startIndex;
        int endIndex = -1;
        if(!connectionClosed){
            startIndex = httpResponse.indexOf("Connection: ");
            for (int i = startIndex; i < httpResponse.length(); i++) {
                String s = httpResponse.substring(i, i + 2);
                if (s.equals("\r\n")) {
                    endIndex = i;
                    break;
                }
            }
            httpResponse = httpResponse.replaceFirst(httpResponse.substring(startIndex, endIndex), "Connection: keep-alive");
        }
        return httpResponse;
    }


    public boolean checkURL(String request){
        return true;
    }


}
