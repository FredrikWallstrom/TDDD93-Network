import javax.xml.bind.Marshaller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ServerProxyTest {

    BufferedWriter bw;

    @org.junit.Before
    public void setUp() throws Exception {
        new Thread(PortListener.getInstance()).start();

    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void testRun() throws Exception {
        Socket s = new Socket("localhost", 8080);
        bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        bw.write("GET http://www.bbc.com/ HTTP/1.1\r\n\r\n");
        bw.flush();
        sleep(7000);
    }
}