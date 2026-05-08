package org.example;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieTest {

    private static char[] alphabet = new char[26];

    @BeforeAll
    private static void initializeTest() {
        for (char c = 'a'; c <= 'z'; c++) {
            alphabet[c - 'a'] = c;
        }
    }
 
    @BeforeEach
    void resetStaticState() throws Exception {

        Movie.movies.clear();

        Field usedIds = Movie.class.getDeclaredField("USED_IDS");
        usedIds.setAccessible(true);

        ((Set<?>) usedIds.get(null)).clear();
    }


    @Test
    void testIsUniqueMovieID() {

        Movie m1 = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("Action"));

        assertTrue(m1.isUniqueMovieID());

        m1.save();

        Movie m2 = new Movie(
                "Turak",
                "T413",
                Arrays.asList("Thriller"));

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
            {"Interstellar", null},
            {"Man Of Steel", "MOK312"}, // K not in title, and S is missing
            {"Tenet", "T1234"},
            {"Banana Republic", "BR00"},
            {"Up", "U12"},
            {"KING KONG", "KK659"}, // "ALL capital letters of the movie title, not just the first letter of each word"
            {"Time Traveler", "319TT"},
            {"The Underworld", "$@318"},
            {"La Casa De Papel", "LCDP#@!"},
            {"Jumanji", "JK419"}, // J is here, but K is not in title.
            {"Tenet", "T1@3"},
        };

        for(String[] testData : testCasesNegative) {
            assertFalse(new Movie(testData[0], testData[1], null).isValidMovieID()
            , "failed at case:" + Arrays.toString(testData));
        }

    }
    


    /*
     * Every word must start with a capital letter.
    */
    @Test
    void testIsValidMovieTitle() { 
        
        String[] testCasesPositive = {
            
            "Nodirbek Abdussatarov",
            
            "GAME Of Thrones",
            "Hunger Games",
            "The Nun",
            "Conjuring",
            "Anabelle Comes Home",
            "The Murder",
            
            
            
        };

        for(String title : testCasesPositive) {
            assertTrue(new Movie(title, null, null).isValidMovieTitle(),
             "failed at case:" + title);
        }

        // final case: covering all alphabets at word beginning
        StringBuilder finalTest = new StringBuilder();
        String word = "";

        for (int i = 0; i < 26; i++) {

            if (i > 0) {
                finalTest.append(" ");
            }

            finalTest.append(Character.toUpperCase(alphabet[i]))
                    .append(word);

            word += alphabet[i];
        }
        assertTrue(new Movie(finalTest.toString(), null, null).isValidMovieTitle(),
             "failed at case:" + finalTest);


        String[] testCasesNegative = {
            null,
            "",
            "wonderland", 
            "Spiderman 2",
            "Breaking $Bad",
            "1 God",
            "Jobava lonDon System",
            "Spiderman: No Way Home",
            
            "Brave       Heart",
            
            
        };

        for(String title : testCasesNegative) {
            assertFalse(new Movie(title, null, null).isValidMovieTitle(),
             "failed at case:" + title);
        }

    }
    @Test
    void testIsValidCategory() {

        Movie validMovie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", "drama"));

        assertTrue(validMovie.isValidCategory());

        Movie invalidMovie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", "fantasy"));

        assertFalse(invalidMovie.isValidCategory());

        Movie nullCategoryMovie = new Movie(
                "Tenet",
                "T413",
                null);

        assertTrue(nullCategoryMovie.isValidCategory());

        Movie emptyCategoryMovie = new Movie(
                "Tenet",
                "T413",
                new ArrayList<>());

        assertTrue(emptyCategoryMovie.isValidCategory());
    }
    @Test
    void testHasDuplicateCategories() {

        Movie noDuplicates = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", "drama"));

        assertFalse(noDuplicates.hasDuplicateCategories());

        Movie duplicates = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", "action"));

        assertTrue(duplicates.hasDuplicateCategories());

        Movie caseInsensitiveDuplicates = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("Action", "action"));

        assertTrue(caseInsensitiveDuplicates.hasDuplicateCategories());
    }
    @Test
    void testSaveMovie() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", "thriller"));

        movie.save();

        assertTrue(Movie.movies.containsKey("action"));
        assertTrue(Movie.movies.containsKey("thriller"));

        assertEquals(1, Movie.movies.get("action").size());
        assertEquals(1, Movie.movies.get("thriller").size());
    }
    @Test
    void testSaveMovieCategoryNormalization() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList(" Action ", "THRILLER"));

        movie.save();

        assertTrue(Movie.movies.containsKey("action"));
        assertTrue(Movie.movies.containsKey("thriller"));
    }
    @Test
    void testSaveMovieWithNullCategory() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                null);

        assertDoesNotThrow(movie::save);
    }
    @Test
    void testToString() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                null);

        assertEquals("T413-Tenet", movie.toString());
    }
    @Test
    void nullCategoryInsideListShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", null));

        assertFalse(movie.isValidCategory());
    }
    @Test
    void emptyCategoryShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList(""));

        assertFalse(movie.isValidCategory());
    }
    @Test
    void nullCategoryDoesNotCreateDuplicate() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", null));

        assertFalse(movie.hasDuplicateCategories());
    }
    @Test
    void saveShouldIgnoreBlankCategories() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action", " "));

        movie.save();

        assertTrue(Movie.movies.containsKey("action"));
        assertFalse(Movie.movies.containsKey(""));
    }
    @Test
    void saveShouldStoreMultipleMoviesInSameCategory() {

        Movie m1 = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action"));

        Movie m2 = new Movie(
                "Top Gun",
                "TG742",
                Arrays.asList("action"));

        m1.save();
        m2.save();

        assertEquals(2, Movie.movies.get("action").size());
    }
    @Test
    void minimumValidMovieIDLength() {

        Movie movie = new Movie(
                "Up",
                "U123",
                Arrays.asList("action"));

        assertTrue(movie.isValidMovieID());
    }
    @Test
    void emptyMovieIDShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "",
                Arrays.asList("action"));

        assertFalse(movie.isValidMovieID());
    }
    @Test
    void movieIDWithRepeatedDigitsShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "T111",
                Arrays.asList("action"));

        assertFalse(movie.isUniqueMovieID());
    }
    
    @Test
    void categoryShouldBeCaseInsensitive() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("ACTION", "Drama"));

        assertTrue(movie.isValidCategory());
    }
    @Test
    void categoryWithSpacesShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList(" action "));

        assertFalse(movie.isValidCategory());
    }
    @Test
    void movieIDWithoutDigitsShouldBeInvalid() {

        Movie movie = new Movie(
                "Tenet",
                "T",
                Arrays.asList("action"));

        assertFalse(movie.isValidMovieID());
    }
    @Test
    void singleLetterMovieTitleShouldBeValid() {

        Movie movie = new Movie(
                "A",
                "A123",
                Arrays.asList("action"));

        assertTrue(movie.isValidMovieTitle());
    }
    @Test
    void movieIDShorterThanMinimumShouldBeInvalid() {

        Movie movie = new Movie(
                "Up",
                "U12",
                Arrays.asList("action"));

        assertFalse(movie.isValidMovieID());
    }
    @Test
    void nullMovieTitleShouldBeInvalid() {

        Movie movie = new Movie(
                null,
                "T123",
                Arrays.asList("action"));

        assertFalse(movie.isValidMovieTitle());
    }
    @Test
    void recommendationsStateChangesAfterSavingMovie() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                Arrays.asList("action"));

        assertFalse(Movie.movies.containsKey("action"));

        movie.save();

        assertTrue(Movie.movies.containsKey("action"));
    }
    @Test
    void emptyCategoryListShouldNotHaveDuplicates() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                new ArrayList<>());

        assertFalse(movie.hasDuplicateCategories());
    }
    @Test
    void saveMovieWithEmptyCategoryList() {

        Movie movie = new Movie(
                "Tenet",
                "T413",
                new ArrayList<>());

        assertDoesNotThrow(movie::save);
    }
}
