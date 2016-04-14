/**
 * Names George Yildiz, Fredrik Wallström
 * Email: Geoyi478@student.liu.se,Frewa814@student.liu.se
 * Group 3
 */

/**
 * This class is responsible to filter a string and decide wether it is valid or not.
 */
public class Filtering {

    private static final String keyWords[] = {"spongebob", "britney spears", "paris hilton", "norrk%c3%b6ping"};

    public Filtering() {
    }

    /**
     * This function will go through a string and return true if it can't find one of the forbidden keywords.
     * @param string the string that is going to be checked for keywords
     * @return true if string is valid, false otherwise.
     */
    public static boolean isStringValid(String string){
        for (int i = 0; i < keyWords.length; i++) {
            if(string.toLowerCase().contains(keyWords[i])) {
                return false;
            }
        }
        return true;
    }
}
