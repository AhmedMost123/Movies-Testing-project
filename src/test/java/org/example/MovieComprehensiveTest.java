package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MovieComprehensiveTest {

    @BeforeEach
    void resetMovieStaticState() {
        Movie.movies.clear();
        // Reset USED_IDS using reflection
        try {
            var usedIdsField = Movie.class.getDeclaredField("USED_IDS");
            usedIdsField.setAccessible(true);
            ((java.util.Set<?>) usedIdsField.get(null)).clear();
        } catch (Exception e) {
            // If reflection fails, continue with test
        }
    }

    // ─────────────────────────────────────────────
    // 1 - Category Validation Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("1 - Category Validation")
    class CategoryValidationTests {

        @Test
        @DisplayName("Valid categories should pass validation")
        void validCategoriesPassValidation() {
            List<String> validCategories = Arrays.asList("action", "drama", "comedy", "horror", "romance", "thriller");
            Movie movie = new Movie("Test Movie", "TM123", validCategories);
            assertTrue(movie.isValidCategory());
        }

        @Test
        @DisplayName("Mixed case categories should pass validation")
        void mixedCaseCategoriesPassValidation() {
            List<String> mixedCategories = Arrays.asList("Action", "DRAMA", "Comedy", "HORROR");
            Movie movie = new Movie("Test Movie", "TM123", mixedCategories);
            assertTrue(movie.isValidCategory());
        }

        @Test
        @DisplayName("Invalid category should fail validation")
        void invalidCategoryFailsValidation() {
            List<String> invalidCategories = Arrays.asList("action", "invalid", "drama");
            Movie movie = new Movie("Test Movie", "TM123", invalidCategories);
            assertFalse(movie.isValidCategory());
        }

        @Test
        @DisplayName("All invalid categories should fail validation")
        void allInvalidCategoriesFailValidation() {
            List<String> invalidCategories = Arrays.asList("invalid1", "invalid2", "invalid3");
            Movie movie = new Movie("Test Movie", "TM123", invalidCategories);
            assertFalse(movie.isValidCategory());
        }

        @Test
        @DisplayName("Empty categories list should pass validation")
        void emptyCategoriesPassValidation() {
            List<String> emptyCategories = new ArrayList<>();
            Movie movie = new Movie("Test Movie", "TM123", emptyCategories);
            assertTrue(movie.isValidCategory());
        }

        @Test
        @DisplayName("Null categories should not cause exception")
        void nullCategoriesNoException() {
            assertDoesNotThrow(() -> {
                Movie movie = new Movie("Test Movie", "TM123", null);
                // Should not throw exception, though behavior depends on implementation
            });
        }
    }

    // ─────────────────────────────────────────────
    // 2 - Duplicate Category Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("2 - Duplicate Category Detection")
    class DuplicateCategoryTests {

        @Test
        @DisplayName("Unique categories should pass duplicate check")
        void uniqueCategoriesPassDuplicateCheck() {
            List<String> uniqueCategories = Arrays.asList("action", "drama", "comedy");
            Movie movie = new Movie("Test Movie", "TM123", uniqueCategories);
            assertFalse(movie.hasDuplicateCategories());
        }

        @Test
        @DisplayName("Exact duplicate categories should be detected")
        void exactDuplicateCategoriesDetected() {
            List<String> duplicateCategories = Arrays.asList("action", "action", "drama");
            Movie movie = new Movie("Test Movie", "TM123", duplicateCategories);
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        @DisplayName("Case insensitive duplicates should be detected")
        void caseInsensitiveDuplicatesDetected() {
            List<String> caseDuplicates = Arrays.asList("action", "ACTION", "drama");
            Movie movie = new Movie("Test Movie", "TM123", caseDuplicates);
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        @DisplayName("Multiple duplicates should be detected")
        void multipleDuplicatesDetected() {
            List<String> multipleDuplicates = Arrays.asList("action", "ACTION", "drama", "DRAMA", "comedy");
            Movie movie = new Movie("Test Movie", "TM123", multipleDuplicates);
            assertTrue(movie.hasDuplicateCategories());
        }

        @Test
        @DisplayName("Single category should not have duplicates")
        void singleCategoryNoDuplicates() {
            List<String> singleCategory = Arrays.asList("action");
            Movie movie = new Movie("Test Movie", "TM123", singleCategory);
            assertFalse(movie.hasDuplicateCategories());
        }

        @Test
        @DisplayName("Empty categories should not have duplicates")
        void emptyCategoriesNoDuplicates() {
            List<String> emptyCategories = new ArrayList<>();
            Movie movie = new Movie("Test Movie", "TM123", emptyCategories);
            assertFalse(movie.hasDuplicateCategories());
        }
    }

    // ─────────────────────────────────────────────
    // 3 - save() Method Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("3 - save() Method")
    class SaveMethodTests {

        @Test
        @DisplayName("save() should add movie to category map")
        void saveAddsToCategoryMap() {
            Movie movie = new Movie("Action Movie", "AM123", Arrays.asList("action"));
            movie.save();
            
            assertTrue(Movie.movies.containsKey("action"));
            assertEquals(1, Movie.movies.get("action").size());
            assertEquals(movie, Movie.movies.get("action").get(0));
        }

        @Test
        @DisplayName("save() should add movie to multiple categories")
        void saveAddsToMultipleCategories() {
            Movie movie = new Movie("Action Drama", "AD123", Arrays.asList("action", "drama"));
            movie.save();
            
            assertTrue(Movie.movies.containsKey("action"));
            assertTrue(Movie.movies.containsKey("drama"));
            assertEquals(1, Movie.movies.get("action").size());
            assertEquals(1, Movie.movies.get("drama").size());
        }

        @Test
        @DisplayName("save() should append to existing category lists")
        void saveAppendsToExistingCategories() {
            Movie movie1 = new Movie("Action Movie 1", "AM123", Arrays.asList("action"));
            Movie movie2 = new Movie("Action Movie 2", "AM456", Arrays.asList("action"));
            
            movie1.save();
            movie2.save();
            
            assertEquals(2, Movie.movies.get("action").size());
        }

        @Test
        @DisplayName("save() should add ID to USED_IDS set")
        void saveAddsToUsedIds() throws Exception {
            Movie movie = new Movie("Test Movie", "TM123", Arrays.asList("action"));
            
            var usedIdsField = Movie.class.getDeclaredField("USED_IDS");
            usedIdsField.setAccessible(true);
            java.util.Set<String> usedIds = (java.util.Set<String>) usedIdsField.get(null);
            
            assertFalse(usedIds.contains("TM123"));
            movie.save();
            assertTrue(usedIds.contains("TM123"));
        }
    }

    // ─────────────────────────────────────────────
    // 4 - toString() Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("4 - toString() Method")
    class ToStringTests {

        @Test
        @DisplayName("toString() should return correct format")
        void toStringReturnsCorrectFormat() {
            Movie movie = new Movie("Test Movie", "TM123", Arrays.asList("action"));
            assertEquals("TM123-Test Movie", movie.toString());
        }

        @Test
        @DisplayName("toString() should handle special characters in title")
        void toStringHandlesSpecialCharacters() {
            Movie movie = new Movie("Movie: With, Special! Characters", "MW123", Arrays.asList("action"));
            assertEquals("MW123-Movie: With, Special! Characters", movie.toString());
        }

        @Test
        @DisplayName("toString() should handle empty title")
        void toStringHandlesEmptyTitle() {
            Movie movie = new Movie("", "TM123", Arrays.asList("action"));
            assertEquals("TM123-", movie.toString());
        }

        @Test
        @DisplayName("toString() should handle null ID")
        void toStringHandlesNullId() {
            Movie movie = new Movie("Test Movie", null, Arrays.asList("action"));
            assertEquals("null-Test Movie", movie.toString());
        }
    }

    // ─────────────────────────────────────────────
    // 5 - Parameterized Tests for Edge Cases
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("5 - Parameterized Edge Case Tests")
    class ParameterizedEdgeCaseTests {

        static Stream<Arguments> movieTitleTestCases() {
            return Stream.of(
                Arguments.of("A", true), // Single character
                Arguments.of("The Lord Of The Rings", true), // Long title
                Arguments.of("Spider-Man: No Way Home", true), // With hyphen and colon
                Arguments.of("Game Of Thrones", true), // Mixed case
                Arguments.of("the matrix", false), // Lowercase first letter
                Arguments.of("The Matrix", false), // Lowercase word
                Arguments.of("", false), // Empty string
                Arguments.of("123 Movie", false), // Starts with number
                Arguments.of("Movie@Home", false), // Special character
                Arguments.of("Movie 2023", false), // Number in title
                Arguments.of("Movie With Space ", false), // Trailing space
                Arguments.of(" Movie With Space", false) // Leading space
            );
        }

        @ParameterizedTest
        @MethodSource("movieTitleTestCases")
        @DisplayName("Movie title validation with various edge cases")
        void testMovieTitleEdgeCases(String title, boolean expected) {
            Movie movie = new Movie(title, "TM123", Arrays.asList("action"));
            assertEquals(expected, movie.isValidMovieTitle());
        }

        static Stream<Arguments> movieIdTestCases() {
            return Stream.of(
                Arguments.of("The Matrix", "TM123", true), // Valid case
                Arguments.of("Inception", "I123", true), // Single letter
                Arguments.of("The Godfather", "TG123", true), // Multiple letters
                Arguments.of("The Matrix", "TM12", false), // Too short
                Arguments.of("The Matrix", "TM1234", false), // Too long
                Arguments.of("The Matrix", "TM12A", false), // Non-digit ending
                Arguments.of(null, "TM123", false) // Null title
            );
        }

        @ParameterizedTest
        @MethodSource("movieIdTestCases")
        @DisplayName("Movie ID validation with various edge cases")
        void testMovieIdEdgeCases(String title, String id, boolean expected) {
            Movie movie = new Movie(title, id, Arrays.asList("action"));
            assertEquals(expected, movie.isValidMovieID());
        }
    }

    // ─────────────────────────────────────────────
    // 6 - Constructor and Edge Case Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("6 - Constructor and Edge Cases")
    class ConstructorAndEdgeCaseTests {

        @Test
        @DisplayName("Constructor should handle null categories")
        void constructorHandlesNullCategories() {
            assertDoesNotThrow(() -> {
                Movie movie = new Movie("Test Movie", "TM123", null);
                assertNotNull(movie.category);
            });
        }

        @Test
        @DisplayName("Constructor should handle empty categories")
        void constructorHandlesEmptyCategories() {
            Movie movie = new Movie("Test Movie", "TM123", new ArrayList<>());
            assertNotNull(movie.category);
            assertTrue(movie.category.isEmpty());
        }

        @Test
        @DisplayName("Constructor should handle null title")
        void constructorHandlesNullTitle() {
            Movie movie = new Movie(null, "TM123", Arrays.asList("action"));
            assertNull(movie.movieTitle);
        }

        @Test
        @DisplayName("Constructor should handle null ID")
        void constructorHandlesNullId() {
            Movie movie = new Movie("Test Movie", null, Arrays.asList("action"));
            assertNull(movie.movieID);
        }

        @Test
        @DisplayName("Movie object should maintain immutability of categories")
        void movieCategoriesImmutability() {
            List<String> originalCategories = new ArrayList<>(Arrays.asList("action", "drama"));
            Movie movie = new Movie("Test Movie", "TM123", originalCategories);
            
            // Modify original list
            originalCategories.add("comedy");
            
            // Movie's categories should not be affected
            assertEquals(2, movie.category.size());
            assertFalse(movie.category.contains("comedy"));
        }
    }

    // ─────────────────────────────────────────────
    // 7 - Comprehensive Movie ID Uniqueness Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("7 - Movie ID Uniqueness Tests")
    class MovieIdUniquenessTests {

        @Test
        @DisplayName("Unique digits should pass validation")
        void uniqueDigitsPassValidation() {
            Movie movie = new Movie("Test", "T123", Arrays.asList("action"));
            assertTrue(movie.isUniqueMovieID());
        }

        @Test
        @DisplayName("Duplicate digits should fail validation")
        void duplicateDigitsFailValidation() {
            Movie movie = new Movie("Test", "T112", Arrays.asList("action"));
            assertFalse(movie.isUniqueMovieID());
        }

        @Test
        @DisplayName("All same digits should fail validation")
        void allSameDigitsFailValidation() {
            Movie movie = new Movie("Test", "T111", Arrays.asList("action"));
            assertFalse(movie.isUniqueMovieID());
        }

        @Test
        @DisplayName("Previously used ID should fail validation")
        void previouslyUsedIdFailsValidation() {
            Movie movie1 = new Movie("Test1", "T123", Arrays.asList("action"));
            Movie movie2 = new Movie("Test2", "T123", Arrays.asList("drama"));
            
            movie1.save();
            assertFalse(movie2.isUniqueMovieID());
        }

        @Test
        @DisplayName("New unique ID should pass validation after save")
        void newUniqueIdPassesValidationAfterSave() {
            Movie movie1 = new Movie("Test1", "T123", Arrays.asList("action"));
            Movie movie2 = new Movie("Test2", "T456", Arrays.asList("drama"));
            
            movie1.save();
            assertTrue(movie2.isUniqueMovieID());
        }
    }

}
