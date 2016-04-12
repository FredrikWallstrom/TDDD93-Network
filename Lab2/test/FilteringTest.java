import org.junit.Test;

import static org.junit.Assert.*;

public class FilteringTest {
    String s[] = {"http://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest2.html",
           " <html> <title> Bad Content HTML File Test for CPSC 441 Assignment 1 </title> <body> <p> " +
                   "This is a simple HTML file with some bad words in it. </p> " +
                   "<p> SpongeBob is my hero! He is so yellow and soft. I just love him.</p> <p> " +
                   "A normal Web browser should be able to display this page just fine, " +
                   "but if you are running Net Ninny, this page should be blocked. " +
                   "Hopefully, you will get the " + "<a href=./error2.html> inappropriate content error page</a> instead. </p> </body> </html>"};

    boolean answers[] = {true, false};
    @Test
    public void testIsStringValid() throws Exception {
        for (int i = 0; i < s.length; i++) {
            assert Filtering.isStringValid(s[i]) == answers[i];
        }
    }
}