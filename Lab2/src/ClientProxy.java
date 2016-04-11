import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class ClientProxy {

    private String hostname = "";
    public ClientProxy() {
    }

    public ArrayList<byte[]> makeRequest(String request) {
        byte br[] = new byte[256];
        ArrayList<byte[]> byteArray = new ArrayList<>();
        String httpRequest = reformatHeader(request);
        InputStream is;
        int readBytes;

        //boolean isText = setContentType(request);

      //send request to webserver

        try (Socket socket = new Socket(hostname, 80)){
            BufferedWriter bw;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(httpRequest);
            bw.flush();

            // wait for data
            is = socket.getInputStream();
            readBytes = is.read(br, 0, 256);
            while(readBytes != -1){
                byte tmp[] = new byte[readBytes];
                System.arraycopy(br, 0, tmp, 0, readBytes);
                byteArray.add(tmp);
                readBytes = is.read(br, 0, 256);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* if(isText){
            if(!isContentValid(byteArray)){
                byteArray  = newResponse(byteArray);
            }
        }*/

        if(!isContentValid(byteArray)){
            System.out.println("bad content");
            byteArray = newResponse(byteArray);
        }
        return byteArray;
    }

    private boolean isContentValid(ArrayList<byte[]> byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte[] br : byteArray){
            sb.append(new String(br));
        }
        String httpResponse = sb.toString();
       // System.out.println(httpResponse);
/*
        int startIndex = httpResponse.indexOf("Content-Type: ")+13;
        int endIndex = 0;
        for (int i = startIndex; i < httpResponse.length(); i++) {
            String s = httpResponse.substring(i, i + 2);
            if (s.equals("\r\n")) {
                endIndex = i;
                break;
            }
        }*/
        boolean foundContent = false;
        Scanner scanner = new Scanner(httpResponse);
        while(scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if(s.contains("Content-Type: ")){

                if(s.contains("text") && !httpResponse.contains("Content-Encoding: ")){
                //if(s.contains("text")){
                return Filtering.isStringValid(httpResponse);
                }else{
                    break;
                }
            }
        }
        /*
        if((httpResponse.substring(startIndex, endIndex)).contains("text")) {
            if(!httpResponse.contains("Content-Encoding: ")){
                return Filtering.isStringValid(httpResponse);
            }
        }*/
        return true;
    }

    private ArrayList<byte[]> newResponse(ArrayList<byte[]> byteArray) {

        String redirect = "HTTP/1.1 302 Found\r\n" +
                "Location: http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html\r\n\r\n\r\n";
        byte br[] = redirect.getBytes();
        byteArray.clear();
        byteArray.add(br);
        return byteArray;
    }


    private boolean setContentType(String request) {
        int startIndex = request.indexOf("Accept: ")+8;
        int endIndex = 0;
            for (int i = startIndex; i < request.length(); i++) {
                String s = request.substring(i, i + 2);
                if (s.equals("\r\n")) {
                    endIndex = i;
                    break;
                }
            }
        if((request.substring(startIndex, endIndex)).contains("text")){
            return true;
        }
        return false;
    }

    public String reformatHeader(String request) {
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
/*
    public boolean isContentValid(ArrayList<byte[]> byteArray){
        StringBuilder sb = new StringBuilder();
        for (byte[] br : byteArray){
            sb.append(new String(br));
        }
        String s = sb.toString();
        //TODO decide if we should go through all or just content
//        s = s.substring(s.indexOf("\r\n\r\n"));;
        return Filtering.isStringValid(s);
        }
*/
    }

