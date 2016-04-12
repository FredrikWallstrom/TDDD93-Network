import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClientProxyTest {
    private String requests[] = {

            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest1.txt HTTP/1.1\r\n\r\n",
            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest2.html HTTP/1.1\r\n\r\n",
            "GET http://www.svd.se/ HTTP/1.1\r\n\r\n",
            "GET http://www.google.se/ HTTP/1.1\r\n\r\n",
            "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/SpongeBob.html HTTP/1.1\r\n\r\n"};


    @Test
    public void testMakeRequest() throws Exception {
        ClientProxy clientProxy = new ClientProxy();
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
}