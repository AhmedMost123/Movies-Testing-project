package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//White Box Testing for Movie.java
/*
Techniques Used:

- Statement Coverage
- Branch Coverage
- Condition Coverage
- Loop Coverage
- Path Coverage

Methods Tested:

- hasDuplicateCategories()
- isValidCategory()
- isValidMovieID()
- isUniqueMovieID()
- isValidMovieTitle()
- save()
- toString()

*/

class MovieWhiteBoxTest {

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field field = Movie.class.getDeclaredField("USED_IDS");
        field.setAccessible(true);

        ((Set<?>) field.get(null)).clear();
    }

    // Testing hasDuplicateCategories()

    @Test
    void testing_duplicate_categories_true() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                Arrays.asList("romance","Romance")
        );

        assertTrue(movie.hasDuplicateCategories());
    }

    @Test
    void testing_duplicate_categories_false() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance","drama")
        );

        assertFalse(movie.hasDuplicateCategories());
    }

    @Test
    void testing_duplicate_categories_null_category() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                null
        );

        assertFalse(movie.hasDuplicateCategories());
    }

    @Test
    void testing_duplicate_categories_null_item() {

        List<String> categories = new ArrayList<>();
        categories.add(null);

        Movie movie = new Movie(
                "Titanic",
                "T123",
                categories
        );

        assertFalse(movie.hasDuplicateCategories());
    }

    // Testing isValidCategory()

    @Test
    void testing_valid_category_true() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance","drama")
        );

        assertTrue(movie.isValidCategory());
    }

    @Test
    void testing_valid_category_invalid_name() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("anime")
        );

        assertFalse(movie.isValidCategory());
    }

    @Test
    void testing_valid_category_null_value() {

        List<String> categories = new ArrayList<>();
        categories.add(null);

        Movie movie = new Movie(
                "Titanic",
                "T123",
                categories
        );

        assertFalse(movie.isValidCategory());
    }

    @Test
    void testing_valid_category_null_list() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                null
        );

        assertTrue(movie.isValidCategory());
    }

    // Testing isValidMovieID()

    @Test
    void testing_valid_movie_id_true() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertTrue(movie.isValidMovieID());
    }

    @Test
    void testing_valid_movie_id_wrong_letters() {

        Movie movie = new Movie(
                "Titanic",
                "X123",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieID());
    }

    @Test
    void testing_valid_movie_id_wrong_numbers() {

        Movie movie = new Movie(
                "Titanic",
                "T12A",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieID());
    }

    @Test
    void testing_valid_movie_id_short_length() {

        Movie movie = new Movie(
                "Titanic",
                "T12",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieID());
    }

    @Test
    void testing_valid_movie_id_null_values() {

        Movie movie = new Movie(
                null,
                null,
                List.of("romance")
        );

        assertFalse(movie.isValidMovieID());
    }

    // Testing isUniqueMovieID()

    @Test
    void testing_unique_movie_id_true() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertTrue(movie.isUniqueMovieID());
    }

    @Test
    void testing_unique_movie_id_duplicate_numbers() {

        Movie movie = new Movie(
                "Titanic",
                "T111",
                List.of("romance")
        );

        assertFalse(movie.isUniqueMovieID());
    }

    @Test
    void testing_unique_movie_id_existing_id() {

        Movie movie1 = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        movie1.save();

        Movie movie2 = new Movie(
                "Avatar",
                "T123",
                List.of("action")
        );

        assertFalse(movie2.isUniqueMovieID());
    }

    // Testing isValidMovieTitle()

    @Test
    void testing_valid_movie_title_true() {

        Movie movie = new Movie(
                "The Matrix",
                "TM123",
                List.of("action")
        );

        assertTrue(movie.isValidMovieTitle());
    }

    @Test
    void testing_valid_movie_title_lowercase_start() {

        Movie movie = new Movie(
                "the Matrix",
                "TM123",
                List.of("action")
        );

        assertFalse(movie.isValidMovieTitle());
    }

    @Test
    void testing_valid_movie_title_special_character() {

        Movie movie = new Movie(
                "Titanic@",
                "T123",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieTitle());
    }

    @Test
    void testing_valid_movie_title_empty() {

        Movie movie = new Movie(
                "",
                "T123",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieTitle());
    }

    // Testing save()

    @Test
    void testing_save_movie() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance","drama")
        );

        movie.save();

        assertTrue(Movie.movies.containsKey("romance"));
        assertTrue(Movie.movies.containsKey("drama"));

        assertEquals(1,Movie.movies.get("romance").size());
    }

    // Testing toString()

    @Test
    void testing_to_string() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertEquals("T123-Titanic",movie.toString());
    }
}