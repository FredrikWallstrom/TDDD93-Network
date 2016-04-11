import javax.xml.bind.Marshaller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ServerProxyTest {
    private Socket s;
    BufferedWriter bw;

    @org.junit.Before
    public void setUp() throws Exception {
        new Thread(PortListener.getInstance()).start();
        s = new Socket("localhost", 8080);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        //bw.close();
        //s.close();
    }

    @org.junit.Test
    public void testRun() throws Exception {
        bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        bw.write("GET http://www.bbc.com/ HTTP/1.1\r\n\r\n");
        bw.flush();
        sleep(10000);
    }

  /*  @org.junit.Test
    public void testCheckURL() throws Exception {

    }*/
}