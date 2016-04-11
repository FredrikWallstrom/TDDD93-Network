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
        String stringLine;
            try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    while ((stringLine = buffReader.readLine()) != null) {
                        sb.append(stringLine);
                        sb.append("\r\n");
                        //see if it is end of request
                        if (stringLine.isEmpty()) {
                            ArrayList<byte[]> httpResponse = new ArrayList<>();
                            //PortListener.LOGGER.log(Level.INFO, "This request is made = " + sb.toString());
                            if (!Filtering.isStringValid(getURL(sb.toString()))) {
                                String redirect = "HTTP/1.1 302 Found\r\n" +
                                        "Location: http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html\r\n\r\n\r\n";
                                byte br[] = redirect.getBytes();
                                httpResponse.add(br);
                            } else {
                                // check if the connection is closed or not
                                boolean connectionClosed = false;
                                if (sb.toString().toLowerCase().contains("connection: close")) {
                                    connectionClosed = true;
                                }
                                //send request to client and let client talk to Webserver
                                httpResponse = client.makeRequest(sb.toString());
                                ArrayList<byte[]> tmp = new ArrayList<>();
                                for (byte[] br : httpResponse) {
                                    tmp.add(br);
                                }
                                httpResponse.clear();
                                httpResponse = formatHeader(tmp, connectionClosed);
                            }

/*
                            StringBuilder stringb = new StringBuilder();
                            for (byte[] br : httpResponse) {
                                stringb.append(new String(br));
                            }
                            String s = stringb.toString();
                            System.out.println(s);
*/
                            // response to client
                            OutputStream os = socket.getOutputStream();
                            //PortListener.LOGGER.log(Level.INFO, "This response is made = " + httpResponse.toString());
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
        /*try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /*
    change header back to connection: keep alive
     */
    private ArrayList<byte[]> formatHeader(ArrayList<byte[]> httpResponse, boolean connectionClosed) throws UnsupportedEncodingException {
        int startIndex;
        int endIndex = -2;
        if(!connectionClosed){

            StringBuilder sb = new StringBuilder();
            for (byte[] br : httpResponse) {
                sb.append(new String(br, "ISO-8859-1"));
            }
            String s = sb.toString();
            s = s.replaceFirst("Connection:(.*)", "Connection: keep-alive");
            /*
            startIndex = s.indexOf("Connection: ");
            for (int i = startIndex; i < s.length(); i++) {
                String tmpString = s.substring(i, i + 1);
                if (tmpString.equals("\n")) {
                    endIndex = i;
                    break;
                }

            }

            s = s.replaceFirst((s.substring(startIndex, endIndex)), "Connection: keep-alive");

*/
            byte br[] = s.getBytes("ISO-8859-1");
            httpResponse.clear();
            httpResponse.add(br);
        }
        return httpResponse;
    }

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
