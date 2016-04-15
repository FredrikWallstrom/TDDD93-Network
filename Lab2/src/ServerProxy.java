import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */

/**
 * This is the class will act as the Server part of the proxy.
 */
public class ServerProxy implements Runnable{
    private Socket socket;
    private ClientProxy client;

    public ServerProxy(Socket socket) {
        this.socket = socket;
        this.client = new ClientProxy();
    }

    /**
     *This method will handle requests from the client (webbrowser) and make sure it is a valid URL and
     * send it to the "client side" of proxy. In cases of invalid URL we will redirect to another page.
     * Feature 3 is implemented in this method.
     */
    @Override
    public void run() {

        StringBuilder sb = new StringBuilder();
        try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String stringLine;
            //socket.setSoTimeout(30000);
            while ((stringLine = buffReader.readLine()) != null) {
                sb.append(stringLine);
                sb.append("\r\n");
                //see if it is end of request
                if (stringLine.isEmpty()) {
                    byte httpResponse[];
                    PortListener.LOGGER.log(Level.INFO, "This request is made = " + "\n" + sb.toString());
                    //Feature 3
                    if (!Filtering.isStringValid(getURL(sb.toString()))) {
                        String redirect = "HTTP/1.1 302 Found\r\n" +
                                          "Location: http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html\r\n\r\n\r\n";
                        httpResponse = redirect.getBytes();
                    } else {
                        //send request to client and let client talk to Webserver
                        httpResponse = client.makeRequest(sb.toString());
                    }

                    PortListener.LOGGER.log(Level.INFO, "This response is made = " + "\n" + new String(httpResponse));
                    // response to client
                    OutputStream os = socket.getOutputStream();
                    os.write(httpResponse);
                    os.flush();
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will extract the entire URL from the request
     * @param request
     * @return URL from the request
     */
    private String getURL(String request){
        int startIndex = request.indexOf(" ") + 1;
        String url = "";
        for (int i = startIndex; i < request.length(); i++) {
            if (request.charAt(i) == ' ') {
                url = request.substring(startIndex, i);
                break;
            }
        }
        return url;
    }


}
