package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MovieTest {

    private static char[] alphabet = new char[26];

    @BeforeAll
    private static void initializeTest() {
        for (char c = 'a'; c <= 'z'; c++) {
            alphabet[c - 'a'] = c;
        }
    }


    @Test
    void testIsUniqueMovieID() {

        Movie m1 = new Movie("Tenet", "T413", new ArrayList(Arrays.asList("Action", "Thriller"))),
        m2 = new Movie("Turak", "T413", new ArrayList(Arrays.asList("Action", "Thriller"))),
        m3 = new Movie("Prince Of Persia", "POP851", new ArrayList(Arrays.asList("Action", "Thriller")));

        m1.save();
        m3.save();

        assertTrue(m1.isUniqueMovieID());
        assertTrue(m3.isUniqueMovieID());

        m2.save();
        assertFalse(m2.isUniqueMovieID());

    }

    /*
    ID: Must consist of all capital letters from the movie title followed by three unique digits.
    testing for: 
      - ID consisting of all capital letters from the movie title
      - followed by exactly 3 digits
    */
    @Test
    void testIsValidMovieID() {

        String[][] testCasesPositive = {
            {"Man Of Steel", "MOS312"},
            {"Oppenheimer", "O999"},
            {"Assassins Creed", "CA864"}, // no letter ordering constraint
            {"Vikings", "V007"}
        }; 

        for(String[] testData : testCasesPositive) {
            assertTrue(new Movie(testData[0], testData[1], null).isValidMovieID()
            , "failed at case:" + Arrays.toString(testData));
        }

        String[][] testCasesNegative = {
            {null, "IRGC314"},
            {"Man Of Steel", "MOK312"}, // K not in title, and S is missing
            {"Tenet", "T1234"},
            {"Banana Republic", "BR00"},
            {"KING KONG", "KK659"}, // "ALL capital letters of the movie title, not just the first letter of each word"
            {"Time Traveler", "319TT"},
            {"The Underworld", "$@318"},
            {"La Casa De Papel", "LCDP#@!"},
            {"Jumanji", "JK419"}, // J is here, but K is not in title.
        };

        for(String[] testData : testCasesNegative) {
            assertFalse(new Movie(testData[0], testData[1], null).isValidMovieID()
            , "failed at case:" + Arrays.toString(testData));
        }

    }

    /*
    ID: Must consist of all capital letters from the movie title followed by three unique digits.
    testing for: 
      - the three digits must be unique
    */
    @Test
    void testisValidMovieID_uniqueDigits() {
        Movie m1 = new Movie("Tenet", "T413", new ArrayList(Arrays.asList("Action", "Thriller"))),
        m2 = new Movie("Prince Of Persia", "POP413", new ArrayList(Arrays.asList("Action", "History")));
        
        m1.save();
        m2.save();

        assertFalse(m1.isValidMovieID());
        assertFalse(m2.isValidMovieID());

    }

    /*
     * Every word must start with a capital letter.
    */
    @Test
    void testIsValidMovieTitle() { 
        
        String[] testCasesPositive = {
            "Tenet ", 
            "Nodirbek Abdussatarov",
            "Brave       Heart",
            "GAME Of Thrones",
            "Spiderman: No Way Home",
            " The Lord Of The Rings"
        };

        for(String title : testCasesPositive) {
            assertTrue(new Movie(title, null, null).isValidMovieTitle(),
             "failed at case:" + title);
        }

        // final case: covering all alphabets at word beginning
        String finalTest = "", word = "";
        for(int i=0;i < 26;i++) {
            finalTest += " " + Character.toUpperCase(alphabet[i]) + word;
            word += alphabet[i]; 
        }
        assertTrue(new Movie(finalTest, null, null).isValidMovieTitle(),
             "failed at case:" + finalTest);


        String[] testCasesNegative = {
            "wonderland", 
            "Spiderman 2",
            "Breaking $Bad",
            "1 God",
            "Jobava lonDon System",
            
        };

        for(String title : testCasesNegative) {
            assertFalse(new Movie(title, null, null).isValidMovieTitle(),
             "failed at case:" + title);
        }

    }
}
