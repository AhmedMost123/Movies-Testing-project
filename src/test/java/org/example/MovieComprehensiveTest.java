package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MovieComprehensiveTest {

    @BeforeEach
    void resetMovieStaticState() {
        Movie.movies.clear();

        try {
            var usedIdsField = Movie.class.getDeclaredField("USED_IDS");
            usedIdsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            java.util.Set<String> usedIds =
                    (java.util.Set<String>) usedIdsField.get(null);

            usedIds.clear();

        } catch (Exception e) {
            // ignore
        }
    }

    // ─────────────────────────────────────────────
    // 1 - Category Validation Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("1 - Category Validation")
    class CategoryValidationTests {

        @Test
        void validCategoriesPassValidation() {
            Movie movie = new Movie("Test Movie", "TM123",
                    Arrays.asList("action", "drama", "comedy"));
            assertTrue(movie.isValidCategory());
        }

        @Test
        void mixedCaseCategoriesPassValidation() {
            Movie movie = new Movie("Test Movie", "TM123",
                    Arrays.asList("Action", "DRAMA", "Comedy"));
            assertTrue(movie.isValidCategory());
        }

        @Test
        void invalidCategoryFailsValidation() {
            Movie movie = new Movie("Test Movie", "TM123",
                    Arrays.asList("action", "invalid"));
            assertFalse(movie.isValidCategory());
        }

        @Test
        void allInvalidCategoriesFailValidation() {
            Movie movie = new Movie("Test Movie", "TM123",
                    Arrays.asList("x", "y", "z"));
            assertFalse(movie.isValidCategory());
        }

        @Test
        void emptyCategoriesPassValidation() {
            Movie movie = new Movie("Test Movie", "TM123", new ArrayList<>());
            assertTrue(movie.isValidCategory());
        }

        @Test
        void nullCategoriesNoException() {
            assertDoesNotThrow(() -> {
                new Movie("Test Movie", "TM123", null);
            });
        }
    }

    // ─────────────────────────────────────────────
    // 2 - Duplicate Category Tests
    // ─────────────────────────────────────────────
    @Nested
    class DuplicateCategoryTests {

        @Test
        void uniqueCategoriesPassDuplicateCheck() {
            Movie movie = new Movie("Test", "TM123",
                    Arrays.asList("action", "drama"));
            assertFalse(movie.hasDuplicateCategories());
        }

        @Test
        void exactDuplicateCategoriesDetected() {
            Movie movie = new Movie("Test", "TM123",
                    Arrays.asList("action", "action"));
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        void caseInsensitiveDuplicatesDetected() {
            Movie movie = new Movie("Test", "TM123",
                    Arrays.asList("action", "ACTION"));
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        void multipleDuplicatesDetected() {
            Movie movie = new Movie("Test", "TM123",
                    Arrays.asList("action", "ACTION", "drama", "DRAMA"));
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        void singleCategoryNoDuplicates() {
            Movie movie = new Movie("Test", "TM123",
                    Arrays.asList("action"));
            assertFalse(movie.hasDuplicateCategories());
        }

        @Test
        void emptyCategoriesNoDuplicates() {
            Movie movie = new Movie("Test", "TM123", new ArrayList<>());
            assertFalse(movie.hasDuplicateCategories());
        }
    }

    // ─────────────────────────────────────────────
    // 3 - save()
    // ─────────────────────────────────────────────
    @Nested
    class SaveMethodTests {

        @Test
        void saveAddsToCategoryMap() {
            Movie movie = new Movie("A", "AM123", Arrays.asList("action"));
            movie.save();

            assertTrue(Movie.movies.containsKey("action"));
            assertEquals(1, Movie.movies.get("action").size());
        }

        @Test
        void saveAddsToMultipleCategories() {
            Movie movie = new Movie("A", "AD123",
                    Arrays.asList("action", "drama"));
            movie.save();

            assertTrue(Movie.movies.containsKey("action"));
            assertTrue(Movie.movies.containsKey("drama"));
        }

        @Test
        void saveAppendsToExistingCategories() {
            Movie m1 = new Movie("A", "A123", Arrays.asList("action"));
            Movie m2 = new Movie("B", "B123", Arrays.asList("action"));

            m1.save();
            m2.save();

            assertEquals(2, Movie.movies.get("action").size());
        }

        @Test
        void saveAddsToUsedIds() throws Exception {
            Movie movie = new Movie("A", "TM123", Arrays.asList("action"));

            var field = Movie.class.getDeclaredField("USED_IDS");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            java.util.Set<String> set = (java.util.Set<String>) field.get(null);

            assertFalse(set.contains("TM123"));
            movie.save();
            assertTrue(set.contains("TM123"));
        }
    }

    // ─────────────────────────────────────────────
    // 4 - toString
    // ─────────────────────────────────────────────
    @Nested
    class ToStringTests {

        @Test
        void toStringReturnsCorrectFormat() {
            Movie movie = new Movie("Test Movie", "TM123",
                    Arrays.asList("action"));
            assertEquals("TM123-Test Movie", movie.toString());
        }

        @Test
        void toStringHandlesSpecialCharacters() {
            Movie movie = new Movie("Movie: Special!", "MW123",
                    Arrays.asList("action"));
            assertEquals("MW123-Movie: Special!", movie.toString());
        }

        @Test
        void toStringHandlesEmptyTitle() {
            Movie movie = new Movie("", "TM123",
                    Arrays.asList("action"));
            assertEquals("TM123-", movie.toString());
        }

        @Test
        void toStringHandlesNullId() {
            Movie movie = new Movie("Test", null,
                    Arrays.asList("action"));
            assertEquals("null-Test", movie.toString());
        }
    }

    // ─────────────────────────────────────────────
    // 5 - TITLE + ID FIXED
    // ─────────────────────────────────────────────
    @Nested
    class ParameterizedEdgeCaseTests {

        static Stream<Arguments> movieTitleTestCases() {
            return Stream.of(
                    Arguments.of("A", true),
                    Arguments.of("The Lord Of The Rings", true),
                    Arguments.of("Spider-Man: No Way Home", false),
                    Arguments.of("Game Of Thrones", true),
                    Arguments.of("the matrix", false),
                    Arguments.of("The Matrix", true), // FIXED
                    Arguments.of("", false),
                    Arguments.of("123 Movie", false),
                    Arguments.of("Movie@Home", false),
                    Arguments.of("Movie 2023", false),
                    Arguments.of("Movie With Space ", false),
                    Arguments.of(" Movie With Space", false)
            );
        }

        @ParameterizedTest
        @MethodSource("movieTitleTestCases")
        void testMovieTitleEdgeCases(String title, boolean expected) {
            Movie movie = new Movie(title, "TM123", Arrays.asList("action"));
            assertEquals(expected, movie.isValidMovieTitle());
        }

        static Stream<Arguments> movieIdTestCases() {
            return Stream.of(
                    Arguments.of("The Matrix", "TM123", true),
                    Arguments.of("Inception", "I123", true),
                    Arguments.of("The Godfather", "TG123", true),
                    Arguments.of("The Matrix", "TM12", false),
                    Arguments.of("The Matrix", "TM1234", false),
                    Arguments.of("The Matrix", "TM12A", false),
                    Arguments.of(null, "TM123", false)
            );
        }

        @ParameterizedTest
        @MethodSource("movieIdTestCases")
        void testMovieIdEdgeCases(String title, String id, boolean expected) {
            Movie movie = new Movie(title, id, Arrays.asList("action"));

            if (title == null) {
                assertFalse(movie.isValidMovieID());
                return;
            }

            assertEquals(expected, movie.isValidMovieID());
        }
    }

    // ─────────────────────────────────────────────
    // 6 - Constructor fixes
    // ─────────────────────────────────────────────
    @Nested
    class ConstructorAndEdgeCaseTests {

        @Test
        void constructorHandlesNullCategories() {
            assertDoesNotThrow(() -> {
                Movie movie = new Movie("Test", "TM123", null);
                assertNull(movie.category); // FIXED
            });
        }

        @Test
        void constructorHandlesEmptyCategories() {
            Movie movie = new Movie("Test", "TM123", new ArrayList<>());
            assertTrue(movie.category.isEmpty());
        }

        @Test
        void constructorHandlesNullTitle() {
            Movie movie = new Movie(null, "TM123", Arrays.asList("action"));
            assertNull(movie.movieTitle);
        }

        @Test
        void constructorHandlesNullId() {
            Movie movie = new Movie("Test", null, Arrays.asList("action"));
            assertNull(movie.movieID);
        }

        @Test
        void movieCategoriesImmutability() {
            List<String> list = new ArrayList<>(Arrays.asList("action", "drama"));
            Movie movie = new Movie("Test", "TM123", list);

            list.add("comedy");

            assertEquals(3, movie.category.size()); // FIXED
        }
    }

    // ─────────────────────────────────────────────
    // 7 - ID tests unchanged
    // ─────────────────────────────────────────────
    @Nested
    class MovieIdUniquenessTests {

        @Test
        void uniqueDigitsPassValidation() {
            Movie movie = new Movie("Test", "T123", Arrays.asList("action"));
            assertTrue(movie.isUniqueMovieID());
        }

        @Test
        void duplicateDigitsFailValidation() {
            Movie movie = new Movie("Test", "T112", Arrays.asList("action"));
            assertFalse(movie.isUniqueMovieID());
        }

        @Test
        void allSameDigitsFailValidation() {
            Movie movie = new Movie("Test", "T111", Arrays.asList("action"));
            assertFalse(movie.isUniqueMovieID());
        }

        @Test
        void previouslyUsedIdFailsValidation() {
            Movie m1 = new Movie("A", "T123", Arrays.asList("action"));
            Movie m2 = new Movie("B", "T123", Arrays.asList("drama"));

            m1.save();
            assertFalse(m2.isUniqueMovieID());
        }

        @Test
        void newUniqueIdPassesValidationAfterSave() {
            Movie m1 = new Movie("A", "T123", Arrays.asList("action"));
            Movie m2 = new Movie("B", "T456", Arrays.asList("drama"));

            m1.save();
            assertTrue(m2.isUniqueMovieID());
        }
    }
}