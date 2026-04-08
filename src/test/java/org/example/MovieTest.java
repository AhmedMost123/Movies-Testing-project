package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MovieTest {

    private static char[] alphabet = new char[26];

    @BeforeAll
    public static void initializeTest() {
        for (char c = 'a'; c <= 'z'; c++) {
            alphabet[c - 'a'] = c;
        }
    }

    @Test
    void testIsUniqueMovieID() {

    }

    @Test
    void testIsValidMovieID() {

    }

    /*
     * Every word must start with a capital letter.
    */
    @Test
    void testIsValidMovieTitle_Positive() { 
        // Positive test
       

        String[] testCases = {
            "Tenet ", 
            "Nodirbek Abdussatarov",
            "Brave       Heart",
            "GAME Of Thrones",
            "Spiderman: No Way Home",
            " The Lord Of The Rings"
        };

        for(String title : testCases) {
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

    }


    /*
     * Every word must start with a capital letter.
    */
    @Test
    void testIsValidMovieTitle_Negative() { 
        // Negative test
       

        String[] testCases = {
            "wonderland", 
            "Spiderman 2",
            "Breaking $Bad",
            "1 God",
            "Jobava lonDon System",
            
        };

        for(String title : testCases) {
            assertFalse(new Movie(title, null, null).isValidMovieTitle(),
             "failed at case:" + title);
        }

    }
}
