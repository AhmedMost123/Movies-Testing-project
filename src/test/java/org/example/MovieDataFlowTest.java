package org.example;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Data Flow Testing for Movie.java
/*
DFT Techniques Used:

- All Definitions Coverage
- All Uses Coverage
- Definition-Clear Paths
- Variable Lifecycle Testing

Variables Tested:

- movieTitle
- movieID
- category
- USED_IDS
- movies

*/

class MovieDataFlowTest {

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field field = Movie.class.getDeclaredField("USED_IDS");
        field.setAccessible(true);

        ((Set<?>) field.get(null)).clear();
    }

    @Test
    void testing_movie_title_definition_to_use() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertTrue(movie.isValidMovieTitle());
    }

    @Test
    void testing_movie_id_definition_to_use() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertTrue(movie.isValidMovieID());
    }

    @Test
    void testing_movie_id_invalid_use() {

        Movie movie = new Movie(
                "Titanic",
                "X123",
                List.of("romance")
        );

        assertFalse(movie.isValidMovieID());
    }

    @Test
    void testing_category_definition_to_use() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance","drama")
        );

        assertTrue(movie.isValidCategory());
    }

    @Test
    void testing_duplicate_category_data_flow() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                Arrays.asList("romance","Romance")
        );

        assertTrue(movie.hasDuplicateCategories());
    }

    @Test
    void testing_null_category_data_flow() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                null
        );

        assertFalse(movie.hasDuplicateCategories());
    }

    @Test
    void testing_save_definition_to_movies_map_use() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        movie.save();

        assertTrue(Movie.movies.containsKey("romance"));
    }

    @Test
    void testing_used_ids_definition_to_use() {

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

    @Test
    void testing_to_string_data_usage() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        assertEquals("T123-Titanic",movie.toString());
    }
}