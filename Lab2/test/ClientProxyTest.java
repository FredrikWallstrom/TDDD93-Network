import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClientProxyTest {
    private ClientProxy clientProxy;
    private String requests[] = {

            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest1.txt HTTP/1.1\r\n\r\n",
            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest2.html HTTP/1.1\r\n\r\n",
            "GET http://www.svd.se/ HTTP/1.1\r\n" +
                    "Connection: close\r\n\r\n",
            "GET http://www.google.se/ HTTP/1.1\r\n\r\n",
            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/SpongeBob.html HTTP/1.1\r\n\r\n"};

    private String answers[] = {"GET /~TDTS04/labs/2011/ass2/goodtest1.txt HTTP/1.1\r\n" +
                                "Host: www.ida.liu.se\r\n" +
                                "Connection: close\r\n\r\n",
                                "GET /~TDTS04/labs/2011/ass2/goodtest2.html HTTP/1.1\r\n" +
                                "Host: www.ida.liu.se\r\n" +
                                "Connection: close\r\n\r\n",
                                "GET / HTTP/1.1\r\n" +
                                "Connection: close\r\n" +
                                "Host: www.svd.se\r\n\r\n",
                                "GET / HTTP/1.1\r\n" +
                                "Host: www.google.se\r\n" +
                                "Connection: close\r\n\r\n",
                                "GET /~TDTS04/labs/2011/ass2/SpongeBob.html HTTP/1.1\r\n" +
                                        "Host: www.ida.liu.se\r\n" +
                                        "Connection: close\r\n\r\n"
    };

    @Test
    public void testMakeRequest() throws Exception {
        clientProxy = new ClientProxy();
        StringBuilder sb = new StringBuilder();
        String s ="";
        for (int i = 0; i < requests.length; i++) {
            ArrayList<byte[]> byteArray = clientProxy.makeRequest(requests[i]);
            for (byte[] br : byteArray) {
                sb.append(new String(br));
            }
            s = sb.toString();
            System.out.println(s);
            s = s.substring(0, s.indexOf("\r\n"));

            assert s.equals("HTTP/1.1 200 OK");
        }
    }


    @Test
    public void testRefactorHeader() throws Exception {
        clientProxy = new ClientProxy();
        for (int i = 0; i < requests.length; i++) {
            String s = clientProxy.reformatHeader(requests[i]);
            //System.out.println(s);
            assert s.equals(answers[i]);

        }
    }
/*
    @Test
    public void testIsContentValid() throws Exception {
        ClientProxy client = new ClientProxy();
        String s = "HTTP/1.1 200 OK\n" +
                "x-amz-id-2: YY59vD+Sq9Bi0UfO5t/1pRfDyKti4+dKyeADu7sGjaz7wIEGhR4gml9MlsLT5NGYYkUTP6v8i8g=\n" +
                "x-amz-request-id: 478FC44DE9D130F2\n" +
                "Content-Encoding: gzip\n" +
                "Last-Modified: Fri, 04 Mar 2016 11:55:43 GMT\n" +
                "ETag: \"bf0a6b8af58c760f0c9b6af97ab76d94\"\n" +
                "Accept-Ranges: bytes\n" +
                "Content-Type: text/javascript\n" +
                "Content-Length: 65936\n" +
                "Server: AmazonS3\n" +
                "Date: Mon, 11 Apr 2016 14:29:14 GMT\n" +
                "Connection: close\n" +
                "Vary: Accept-Encoding\n" +
                "\n" +
                "fjdkasflas";
        byte br[] = s.getBytes();
        ArrayList<byte[]> byteArray = new ArrayList<>();
        byteArray.add(br);
        boolean res = client.isContentValid(byteArray);
        System.out.println(res);


    }
    */
}