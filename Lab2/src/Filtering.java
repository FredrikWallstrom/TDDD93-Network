import java.lang.reflect.Array;

/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */
public class Filtering {

    private static final String words[] = {"spongebob", "britney spears", "paris hilton", "norrk??ping"};

    public Filtering() {
    }

    public static boolean isStringValid(String string){
        for (int i = 0; i < words.length; i++) {
            if(string.toLowerCase().contains(words[i])) {
                return false;
            }
        }
        return true;
    }
}
