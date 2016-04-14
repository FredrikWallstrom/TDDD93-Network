import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */

/**
 * This class is the "Client side" of the proxy and will handle the request to the webserver and decide if filtering is needed
 * It sends the answer or redirect answer to "server side" of proxy.
 */
public class ClientProxy {

    private String hostname = "";

    public ClientProxy() {
    }

    /**
     * This is the method that take care of the request and sends it to the webserver.
     * We get the response and send it to the "server side" of the proxy after we go through content
     * @param request the request from the "server side" of proxy
     * @return bytearray with the response.
     */
    public byte[] makeRequest(String request) {
        // check if the connection is closed or not
        boolean connectionClosed = CheckConnection(request);
        byte br[] = new byte[1024];
        String httpRequest = reformatHeader(request);
        InputStream is;
        DataInputStream dataIs;
        int readBytes;
        StringBuilder sb = new StringBuilder();
        String stringLine;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Socket socket = new Socket(hostname, 80)){
            BufferedWriter bw;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(httpRequest);
            bw.flush();

            // wait for data
            is = socket.getInputStream();
            dataIs = new DataInputStream(is);
            //read in header
            while ((stringLine = dataIs.readLine()) != null) {
                sb.append(stringLine);
                sb.append("\r\n");
                if(sb.toString().endsWith("\r\n\r\n")){
                    break;
                }
            }
            String stringHeader= correctConnection(sb.toString(), connectionClosed);
            bos.write(stringHeader.getBytes("ISO-8859-1"));
            //read in content
            while((readBytes = dataIs.read(br)) != -1){
                bos.write(br, 0, readBytes);
            }
            if(isFilteringNecessary(sb.toString())) {
                if(!isContentValid(br)){
                    return newResponse();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }




    /**
     * This method will look through the Content-type field and eventuelly the Content-Encoding field in the Http-header.
     * Only if the value of the content-type is text and the Content-Encoding field is non-existence, will we consider it necessary
     * to filter the content.
     * @param httpHeader
     * @return true if filtering is necessary, otherwise false.
     */
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

    /**
     * This method will check whether content is valid or not with the help of Filter class.
     * @param br bytearray containing the content of the response received.
     * @return true if content is valid, otherwise false.
     */
    private boolean isContentValid(byte[] br) {
        String httpResponse = new String(br);
        return Filtering.isStringValid(httpResponse);
    }

    /**
     * This method will create a new response message to be forwarded if the content is not valid.
     * @return bytearray containing new response message.
     */
    private byte[] newResponse() {
        String redirect = "HTTP/1.1 302 Found\r\n" +
                "Location: http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html\r\n\r\n\r\n";
        byte br[] = redirect.getBytes();
        return br;
    }

    /**
     * We change the request so that we cut out the absolute path from the GET request, if Host field is not present we add one.
     * We change the connection: field to close or add the connection: close field if it is not present.
     * @param request httpRequest
     * @return the newly formatted request to be sent to server.
     */
    private String reformatHeader(String request) {
        int startIndex = request.indexOf(" ") + 1;
        int endIndex = setHostname(request);

        // cut out the absolute path from request.
        request = request.replaceFirst(request.substring(startIndex, endIndex), "");

        // add separate hostfield if it is not already in the header
        if (!(request.contains("Host:"))) {
            request = request.substring(0, request.length() - 2);
            request = request + "Host: " + hostname + "\r\n\r\n";
        }
        //change/add connection : close to header
        if (request.contains("Connection:")) {
            startIndex = request.indexOf("Connection: ");
            for (int i = startIndex; i < request.length(); i++) {
                String s = request.substring(i, i + 2);
                if (s.equals("\r\n")) {
                    endIndex = i;
                    break;
                }
            }
            request = request.replaceFirst(request.substring(startIndex, endIndex), "Connection: close");
        } else {
            request = request.substring(0, request.length() - 2);
            request = request + "Connection: close" + "\r\n\r\n";
        }
        return request;
    }

    /**
     * checks wether Client sent us connection keep alive before we change it so we can change it back in the response.
     * @param request the http request before we change it.
     * @return true if connection: close is found
     */
    private boolean CheckConnection(final String request) {
        if (request.toLowerCase().contains("connection: close")) {
            return  true;
        }
        return false;
    }

    /**
     * This will set the Hostname field (not header field, but variable in class) and return the
     * index of where it ends in the request.
     * @param request http request
     * @return endindex of where hostname ends in the http request.
     */
    private int setHostname(final String request) {
        int endIndex = 0;
        int startOfHost = request.indexOf("//") + 2;

        for (int i = startOfHost; i < request.length(); i++) {
            if (request.charAt(i) == '/') {
                endIndex = i;
                break;
            }
        }
        hostname = request.substring(startOfHost, endIndex);
        return endIndex;
    }

    /**
     * Change back connection: field to keep alive if it was sent with this value
     * @param httpHeader
     * @param connectionClosed
     * @return the new header, corrected if necessary.
     */
    private String correctConnection(String httpHeader, boolean connectionClosed){
        if(!connectionClosed){
            httpHeader = httpHeader.replaceFirst("\\sConnection:(.*)", "\nConnection: keep-alive");
        }
        return httpHeader;
    }
}

