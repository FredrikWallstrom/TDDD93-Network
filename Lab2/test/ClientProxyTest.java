import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientProxyTest {
    private  ClientProxy clientProxy;
    private String requests[] = {"GET http://www.net.tutsplus.com/tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\n" +
                "Host: net.tutsplus.com\n" +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Accept-Language: en-us,en;q=0.5\n" +
                "Accept-Encoding: gzip,deflate\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" +
                "Keep-Alive: 300\n" +
                "Connection: keep-alive\n" +
                "Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\n" +
                "Pragma: no-cache\n" +
                "Cache-Control: no-cache",
                "GET http://www.svd.se/naringsliv HTTP/1.1\r\n\r\n",
                "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest2.html HTTP/1.1\r\n\r\n"};

    private String answers[] = {"www.net.tutsplus.com", "www.svd.se", "www.goal.com"};

/*
    @Test
    public void testGetHost() throws Exception {
        clientProxy = new ClientProxy();
        for (int i = 0; i < requests.length; i++) {
            assert clientProxy.getHost(requests[i]).equals(answers[i]);
        }
    }*/
/*
    @Test
    public void testMakeRequest() throws Exception {
        clientProxy = new ClientProxy();
        clientProxy.makeRequest(requests[2]);

    }
*/
    @Test
    public void testRefactorHeader() throws Exception {
        clientProxy = new ClientProxy();
        clientProxy.reformatHeader(requests[2]);

    }
}