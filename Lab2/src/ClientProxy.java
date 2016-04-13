import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;


/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ClientProxy {

    private String hostname = "";
    public ClientProxy() {
    }

    public byte[] makeRequest(String request) {
        byte br[];
        ArrayList<byte[]> byteArray = new ArrayList<>();
        String httpRequest = reformatHeader(request);
        InputStream is;
        BufferedReader buffR;
        int readBytes;
        StringBuilder sb = new StringBuilder();
        String stringLine;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //send request to webserver

        try (Socket socket = new Socket(hostname, 80)){
            BufferedWriter bw;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(httpRequest);
            bw.flush();
            // wait for data
            is = socket.getInputStream();

            buffR = new BufferedReader(new InputStreamReader(is));
            while ((stringLine = buffR.readLine()) != null) {
                sb.append(stringLine);
                sb.append("\r\n");
                if(sb.toString().endsWith("\r\n\r\n")){
                    break;
                }
            }
            if(isFilteringNecessary(sb.toString())) {
                System.out.println("filter nodvandigt\n");
                bos.write(sb.toString().getBytes("ISO-8859-1"));

                while((readBytes = buffR.read()) != -1){
                    bos.write(readBytes);

                }
                br = bos.toByteArray();
               // if(isContentValid(br)){
                    return br;
                //}
            }else{
                bos.write(sb.toString().getBytes("ISO-8859-1"));

                System.out.println("ej nodvandigt\n");
                while((readBytes = buffR.read()) != -1){
                    bos.write(readBytes);
                }
                br = bos.toByteArray();
                return br;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("returnera noll\n");
        return null;
    }

    private boolean isFilteringNecessary(String httpHeader) {
        Scanner scanner = new Scanner(httpHeader);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (s.contains("Content-Type:")) {
                if (s.contains("text") && !httpHeader.contains("Content-Encoding:")) {
                    return true;
                }else{
                    break;
                }
            }
        }
        return false;
    }

    private boolean isContentValid(byte[] br) {
        StringBuilder sb = new StringBuilder();
   //     for (byte[] br : byteArray){*/
            String httpResponse = new String(br);
        //}

        return Filtering.isStringValid(httpResponse);
    }

    private ArrayList<byte[]> newResponse(ArrayList<byte[]> byteArray) {

        String redirect = "HTTP/1.1 302 Found\r\n" +
                "Location: http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html\r\n\r\n\r\n";
        byte br[] = redirect.getBytes();
        byteArray.clear();
        byteArray.add(br);
        return byteArray;
    }

    private String reformatHeader(String request) {
        int endIndex = 0;
        int startIndex = request.indexOf(" ") + 1;
        int startOfHost = request.indexOf("//") + 2;

        for (int i = startOfHost; i < request.length(); i++) {
            if (request.charAt(i) == '/') {
                endIndex = i;
                break;
            }
        }
        hostname = request.substring(startOfHost, endIndex);
        request = request.replaceFirst(request.substring(startIndex, endIndex), "");

        // add separate hostfield if it is not already in the header
        if (!(request.contains("Host: "))) {
            request = request.substring(0, request.length() - 2);
            request = request + "Host: " + hostname + "\r\n\r\n";
        }

        //change/add connection : close to header
        if (request.contains("Connection: ")) {
            startIndex = request.indexOf("Connection: ");
            for (int i = startIndex; i < request.length(); i++) {
                String s = request.substring(i, i + 2);
                if (s.equals("\r\n")) {
                    endIndex = i;
                    break;
                }
            }
            request = request.replaceFirst(request.substring(startIndex, endIndex), "Connection: close");
            //add connection header if it is not explicitly there
        } else {
            request = request.substring(0, request.length() - 2);
            request = request + "Connection: close" + "\r\n\r\n";
        }
        return request;
    }

}

