package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class UserTest {

    // reset static UID_SET before each test so tests don't interfere with each other
    @BeforeEach
    void resetStaticState() throws Exception {
        Field uidSet = User.class.getDeclaredField("UID_SET");
        uidSet.setAccessible(true);
        ((Set<?>) uidSet.get(null)).clear();

        // Also clear Movie static map to avoid recommendation bleed
        Movie.movies.clear();
    }


    // helper method to quickly build a user
    private User makeUser(String name, String id) 
    {
        return new User(name, id, new ArrayList<>());
    }

    private User makeUser(String name, String id, List<String> categories) 
    {
        return new User(name, id, categories);
    }


    // test isValidUserName() function
    @Nested
    @DisplayName("1 - isValidUserName()")
    class ValidUserNameTests {
        @Test
        @DisplayName("Single word name should be valid")
        void singleWordName() {
            assertTrue(makeUser("Ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Two word name with space should be valid")
        void twoWordName() {
            assertTrue(makeUser("Ahmed Ali", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("All lowercase name should be valid")
        void allLowercaseName() {
            assertTrue(makeUser("ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Mixed case name should be valid")
        void mixedCaseName() {
            assertTrue(makeUser("aHmEd", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name starting with a space should be invalid")
        void nameStartingWithSpace() {
            assertFalse(makeUser(" Ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name with numbers should be invalid")
        void nameWithNumbers() {
            assertFalse(makeUser("Ahmed123", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name with special characters should be invalid")
        void nameWithSpecialChars() {
            assertFalse(makeUser("Ahmed@Ali", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Empty name should be invalid")
        void emptyName() {
            assertFalse(makeUser("", "123456789").isValidUserName());
        }
        @Test
        @DisplayName("Null username should be invalid")
        void nullUsername() {
            User user = makeUser(null, "123456789");
            assertFalse(user.isValidUserName());
        }
        @Test
        @DisplayName("Username with only spaces should be invalid")
        void usernameOnlySpaces() {
            assertFalse(makeUser("   ", "123456789").isValidUserName());
        }
    }


    // test isValidUserID() function
    @Nested
    @DisplayName("2 - isValidUserID()")
    class ValidUserIDTests {

        @Test
        @DisplayName("8 digits + 1 digit (all numeric) should be valid")
        void validIDAllNumeric() {
            assertTrue(makeUser("Ahmed", "123456789").isValidUserID());
        }

        @Test
        @DisplayName(" 8 digits + 1 uppercase letter should be valid")
        void validIDWithUppercaseLetter() {
            assertTrue(makeUser("Ahmed", "12345678A").isValidUserID());
        }

        @Test
        @DisplayName(" 8 digits + 1 lowercase letter should be valid")
        void validIDWithLowercaseLetter() {
            assertTrue(makeUser("Ahmed", "12345678a").isValidUserID());
        }

        @Test
        @DisplayName("ID shorter than 9 characters should be invalid")
        void tooShortID() {
            assertFalse(makeUser("Ahmed", "1234567").isValidUserID());
        }

        @Test
        @DisplayName(" ID longer than 9 characters should be invalid")
        void tooLongID() {
            assertFalse(makeUser("Ahmed", "1234567890A").isValidUserID());
        }

        @Test
        @DisplayName(" ID with special character at end should be invalid")
        void idWithSpecialCharAtEnd() {
            assertFalse(makeUser("Ahmed", "12345678@").isValidUserID());
        }

        @Test
        @DisplayName(" ID starting with a letter should be invalid")
        void idStartingWithLetter() {
            assertFalse(makeUser("Ahmed", "A23456789").isValidUserID());
        }

        @Test
        @DisplayName(" Duplicate ID should be invalid after first user saved")
        void duplicateIDAfterSave() {
            User user1 = makeUser("Ahmed", "12345678A");
            user1.save(); // saves ID to static set

            User user2 = makeUser("Ali", "12345678A"); // same ID
            assertFalse(user2.isValidUserID());
        }
        @Test
        @DisplayName("Null user ID should be invalid")
        void nullUserId() {
            User user = makeUser("Ahmed", null);
            assertFalse(user.isValidUserID());
        }
        @Test
        @DisplayName("ID with spaces should be invalid")
        void idWithSpaces() {
            assertFalse(makeUser("Ahmed", "1234 5678").isValidUserID());
        }
    }
    // getRecommendations() Tests
    @Nested
    @DisplayName("3 - getRecommendations()")
    class RecommendationTests {

        @Test
        @DisplayName("User with no liked categories should get empty recommendations")
        void userWithNoLikedCategories() {
            User user = makeUser("Ahmed", "123456789", new ArrayList<>());
            Map<String, ArrayList<Movie>> recommendations = user.getRecommendations();
            assertTrue(recommendations.isEmpty());
        }

        @Test
        @DisplayName("User with liked categories should get recommendations when movies exist")
        void userWithLikedCategoriesAndMovies() {
            // Setup movies in static map
            Movie actionMovie = new Movie("Action Movie", "AM123", Arrays.asList("action"));
            Movie dramaMovie = new Movie("Drama Movie", "DM456", Arrays.asList("drama"));
            actionMovie.save();
            dramaMovie.save();

            User user = makeUser("Ahmed", "123456789", Arrays.asList("action", "drama"));
            Map<String, ArrayList<Movie>> recommendations = user.getRecommendations();

            assertEquals(2, recommendations.size());
            assertTrue(recommendations.containsKey("action"));
            assertTrue(recommendations.containsKey("drama"));
            assertEquals(1, recommendations.get("action").size());
            assertEquals(1, recommendations.get("drama").size());
        }

        @Test
        @DisplayName("User with liked categories but no movies should get empty recommendations")
        void userWithLikedCategoriesButNoMovies() {
            User user = makeUser("Ahmed", "123456789", Arrays.asList("action", "drama"));
            Map<String, ArrayList<Movie>> recommendations = user.getRecommendations();
            assertTrue(recommendations.isEmpty());
        }

        @Test
        @DisplayName("User with mixed valid and invalid categories should get only valid recommendations")
        void userWithMixedCategories() {
            // Setup only action movies
            Movie actionMovie = new Movie("Action Movie", "AM123", Arrays.asList("action"));
            actionMovie.save();

            User user = makeUser("Ahmed", "123456789", Arrays.asList("action", "nonexistent"));
            Map<String, ArrayList<Movie>> recommendations = user.getRecommendations();

            assertEquals(1, recommendations.size());
            assertTrue(recommendations.containsKey("action"));
            assertFalse(recommendations.containsKey("nonexistent"));
        }
        @Test
        @DisplayName("Multiple movies in same category should all be recommended")
        void multipleMoviesSameCategory() {
            Movie m1 = new Movie("Action One", "AO123", Arrays.asList("action"));
            Movie m2 = new Movie("Action Two", "AT456", Arrays.asList("action"));

            m1.save();
            m2.save();

            User user = makeUser("Ahmed", "123456789", Arrays.asList("action"));

            Map<String, ArrayList<Movie>> recs = user.getRecommendations();

            assertEquals(2, recs.get("action").size());
        }
        @Test
        @DisplayName("Duplicate categories should not duplicate recommendations")
        void duplicateCategories() {
            Movie movie = new Movie("Action", "AC123", Arrays.asList("action"));
            movie.save();

            User user = makeUser(
                "Ahmed",
                "123456789",
                Arrays.asList("action", "action")
            );

            Map<String, ArrayList<Movie>> recs = user.getRecommendations();

            assertEquals(1, recs.size());
        }
        @Test
        @DisplayName("Null liked categories should return empty recommendations")
        void nullLikedCategoriesRecommendations() {
            User user = new User("Ahmed", "123456789", null);

            Map<String, ArrayList<Movie>> recommendations =
                    user.getRecommendations();

            assertTrue(recommendations.isEmpty());
        }
    }

    // save() Tests
    @Nested
    @DisplayName("4 - save()")
    class SaveTests {

        @Test
        @DisplayName("save() should add user ID to static set")
        void saveAddsToStaticSet() throws Exception {
            User user = makeUser("Ahmed", "123456789");
            
            // Verify ID not in set initially
            Field uidSet = User.class.getDeclaredField("UID_SET");
            uidSet.setAccessible(true);
            Set<String> uidSetValue = (Set<String>) uidSet.get(null);
            assertFalse(uidSetValue.contains("123456789"));

            user.save();

            // Verify ID is now in set
            assertTrue(uidSetValue.contains("123456789"));
        }

        @Test
        @DisplayName("Multiple saves should not duplicate IDs")
        void multipleSavesNoDuplication() throws Exception {
            User user = makeUser("Ahmed", "123456789");
            
            Field uidSet = User.class.getDeclaredField("UID_SET");
            uidSet.setAccessible(true);
            Set<String> uidSetValue = (Set<String>) uidSet.get(null);

            user.save();
            int initialSize = uidSetValue.size();
            
            user.save(); // Save again
            assertEquals(initialSize, uidSetValue.size());
        }
        @Test
        @DisplayName("Saving multiple users should increase UID set size")
        void multipleUsersIncreaseSetSize() throws Exception {

            Field uidSet = User.class.getDeclaredField("UID_SET");
            uidSet.setAccessible(true);

            Set<String> uidSetValue = (Set<String>) uidSet.get(null);

            User u1 = makeUser("Ahmed", "123456789");
            User u2 = makeUser("Ali", "987654321");

            u1.save();
            u2.save();

            assertEquals(2, uidSetValue.size());
}
    }

    // isUniqueUserId() Tests
    @Nested
    @DisplayName("5 - isUniqueUserId()")
    class UniqueUserIdTests {

        @Test
        @DisplayName("User should be unique in empty list")
        void uniqueInEmptyList() {
            User user = makeUser("Ahmed", "123456789");
            assertTrue(user.isUniqueUserId(new ArrayList<>()));
        }

        @Test
        @DisplayName("User should be unique when no duplicates exist")
        void uniqueWhenNoDuplicates() {
            User user1 = makeUser("Ahmed", "123456789");
            User user2 = makeUser("Ali", "987654321");
            User user3 = makeUser("Sara", "555555555");
            
            List<User> users = Arrays.asList(user1, user2, user3);
            assertTrue(user1.isUniqueUserId(users));
            assertTrue(user2.isUniqueUserId(users));
            assertTrue(user3.isUniqueUserId(users));
        }

        @Test
        @DisplayName("User should not be unique when duplicate exists")
        void notUniqueWhenDuplicateExists() {
            User user1 = makeUser("Ahmed", "123456789");
            User user2 = makeUser("Ali", "987654321");
            User user3 = makeUser("Sara", "123456789"); // Duplicate ID
            
            List<User> users = Arrays.asList(user1, user2, user3);
            assertFalse(user1.isUniqueUserId(users));
            assertFalse(user3.isUniqueUserId(users));
            assertTrue(user2.isUniqueUserId(users));
        }

        @Test
        @DisplayName("User should be unique when comparing with self only")
        void uniqueWhenComparingWithSelf() {
            User user = makeUser("Ahmed", "123456789");
            List<User> users = Arrays.asList(user);
            assertTrue(user.isUniqueUserId(users));
        }
    }

    // Parameterized Tests for Edge Cases
    @Nested
    @DisplayName("6 - Parameterized Edge Case Tests")
    class ParameterizedEdgeCaseTests {

        static Stream<Arguments> usernameTestCases() {
            return Stream.of(
                Arguments.of("A", true), // Single character
                Arguments.of("A B C D E F G H I J", true), // Long name with spaces
                Arguments.of("Mary Jane Watson", true), // Three words
                Arguments.of("Jean-Claude", false), // Hyphen
                Arguments.of("O'Connor", false), // Apostrophe
                Arguments.of("Dr. Strange", false), // Period
                Arguments.of("Mr Smith", true), // two valid words
                Arguments.of("John Doe ", false), // Trailing space
                Arguments.of(" John Doe", false), // Leading space
                Arguments.of("John  Doe", false), // Double space
                Arguments.of("", false) // Empty string
            );
        }

        @ParameterizedTest
        @MethodSource("usernameTestCases")
        @DisplayName("Username validation with various edge cases")
        void testUsernameEdgeCases(String username, boolean expected) {
            User user = makeUser(username, "123456789");
            assertEquals(expected, user.isValidUserName());
        }

        static Stream<Arguments> userIdTestCases() {
            return Stream.of(
                Arguments.of("000000000", true), // All zeros
                Arguments.of("999999999", true), // All nines
                Arguments.of("123456780", true), // Ends with zero
                Arguments.of("12345678Z", true), // Ends with uppercase
                Arguments.of("12345678z", true), // Ends with lowercase
                Arguments.of("12345678", false), // Too short
                Arguments.of("1234567890", false), // Too long
                Arguments.of("12345678@", false), // Special character
                Arguments.of("12345678#", false), // Special character
                Arguments.of("A23456789", false), // Starts with letter
                Arguments.of("1234567 8", false), // Contains space
                Arguments.of("", false) // Empty string
            );
        }

        @ParameterizedTest
        @MethodSource("userIdTestCases")
        @DisplayName("User ID validation with various edge cases")
        void testUserIdEdgeCases(String userId, boolean expected) {
            User user = makeUser("Ahmed", userId);
            assertEquals(expected, user.isValidUserID());
        }
    }

    // Exception and Boundary Tests
    @Nested
    @DisplayName("7 - Exception and Boundary Tests")
    class ExceptionAndBoundaryTests {

        @Test
        @DisplayName("Constructor should handle null categories gracefully")
        void constructorWithNullCategories() {
            assertDoesNotThrow(() -> {
                User user = new User("Ahmed", "123456789", null);
                assertNotNull(user.likedCategories);
            });
        }

        @Test
        @DisplayName("Constructor should handle empty categories list")
        void constructorWithEmptyCategories() {
            User user = new User("Ahmed", "123456789", new ArrayList<>());
            assertNotNull(user.likedCategories);
            assertTrue(user.likedCategories.isEmpty());
        }

        @Test
        @DisplayName("getUsername() should return correct username")
        void getUsernameReturnsCorrectValue() {
            User user = makeUser("Ahmed Ali", "123456789");
            assertEquals("Ahmed Ali", user.getUsername());
        }

        @Test
        @DisplayName("User object should maintain immutability of categories")
        void userCategoriesImmutability() {
            List<String> originalCategories = new ArrayList<>(Arrays.asList("action", "drama"));
            User user = makeUser("Ahmed", "123456789", originalCategories);
            
            // Modify original list
            originalCategories.add("comedy");
            
            // User's categories should not be affected
            assertEquals(2, user.likedCategories.size());
            assertFalse(user.likedCategories.contains("comedy"));
        }
    }
    @Nested
    @DisplayName("8 - Boundary Value Analysis")
    class BoundaryValueTests {

        @Test
        @DisplayName("Username length boundary")
        void usernameLengthBoundary() {
            assertTrue(makeUser("A", "123456789").isValidUserName());

            assertFalse(makeUser("", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("User ID exact boundary")
        void userIdBoundary() {
            assertTrue(makeUser("Ahmed", "123456789").isValidUserID());

            assertFalse(makeUser("Ahmed", "12345678").isValidUserID());

            assertFalse(makeUser("Ahmed", "1234567890").isValidUserID());
        }
        
    }
    @Nested
    @DisplayName("9 - State Transition Testing")
    class StateTransitionTests {

        @Test
        @DisplayName("User ID becomes non-unique after save")
        void idStateTransition() {
            User user1 = makeUser("Ahmed", "123456789");

            assertTrue(user1.isValidUserID());

            user1.save();

            User user2 = makeUser("Ali", "123456789");

            assertFalse(user2.isValidUserID());
        }
    }

}

