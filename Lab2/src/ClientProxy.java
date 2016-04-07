import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ClientProxy {

    public ClientProxy() {

    }

    public String makeRequest(String request) throws IOException {
        String host = getHost(request);
        System.out.println("hej" + " " + host);
        Socket socket = new Socket(host, 80);
        BufferedWriter bw;
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(request);
        bw.flush();

        // wait for data



        BufferedReader br;
        String stringLine;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while((stringLine = br.readLine()) != null){
                System.out.println(stringLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String reformatHeader(String request){
        int endIndex = 0;
        int startIndex = request.indexOf(" ") + 1;
        int startOfHost = request.indexOf("//")+2;
        String host;

        for (int i = startOfHost ; i < request.length() ; i++) {
            if (request.charAt(i) == '/'){
                endIndex = i;
                break;
            }
        }

        host = request.substring(startOfHost, endIndex);

        request = request.replaceAll(request.substring(startIndex, endIndex), "");
        request = request.substring(0,request.length()-2);


        request = request + "Host: " + host + "\r\n\r\n";
        System.out.println("<" + request + ">");

     return "";
    }

    public String getHost(String request){
        int startIndex = 0;
        int endIndex = 0;
        for (int i=0; i < request.length(); i++)
        {
            String s = request.substring(i,i+2);
            if (s.equals("//")){
                startIndex = i+2;
                break;
            }
        }
        for (int i = startIndex ; i < request.length() ; i++) {
            if (request.charAt(i) == '/'){
                endIndex = i;
                break;
            }
        }

        return request.substring(startIndex, endIndex);
    }
}
