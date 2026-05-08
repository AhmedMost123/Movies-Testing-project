package org.example;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simple test runner without JUnit dependencies
 * Tests the core functionality of the movie recommendation system
 */
public class SimpleTestRunner {
    
    private static int testsPassed = 0;
    private static int testsTotal = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Simple Tests ===");
        
        testMovieValidation();
        testUserValidation();
        testRecommendations();
        testFileManager();
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests Passed: " + testsPassed + "/" + testsTotal);
        
        if (testsPassed == testsTotal) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
        }
    }
    
    private static void testMovieValidation() {
        System.out.println("\n--- Testing Movie Validation ---");
        
        // Test valid movie
        Movie validMovie = new Movie("The Matrix", "TM123", Arrays.asList("action"));
        assertTrue(validMovie.isValidMovieTitle(), "Valid movie title should pass");
        assertTrue(validMovie.isValidMovieID(), "Valid movie ID should pass");
        assertTrue(validMovie.isValidCategory(), "Valid category should pass");
        assertFalse(validMovie.hasDuplicateCategories(), "No duplicates should be found");
        
        // Test invalid movie title
        Movie invalidTitle = new Movie("invalid movie", "IM123", Arrays.asList("action"));
        assertFalse(invalidTitle.isValidMovieTitle(), "Invalid movie title should fail");
        
        // Test duplicate categories
        Movie duplicateCats = new Movie("Test Movie", "TM123", Arrays.asList("action", "ACTION"));
        assertTrue(duplicateCats.hasDuplicateCategories(), "Duplicate categories should be detected");
        
        // Test null safety
        Movie nullCats = new Movie("Test Movie", "TM123", null);
        assertTrue(nullCats.isValidCategory(), "Null categories should be handled");
    }
    
    private static void testUserValidation() {
        System.out.println("\n--- Testing User Validation ---");
        
        // Test valid user
        User validUser = new User("John Doe", "12345678A", Arrays.asList("action", "drama"));
        assertTrue(validUser.isValidUserName(), "Valid username should pass");
        assertTrue(validUser.isValidUserID(), "Valid user ID should pass");
        
        // Test invalid username
        User invalidName = new User("john123", "12345678A", Arrays.asList("action"));
        assertFalse(invalidName.isValidUserName(), "Invalid username should fail");
        
        // Test invalid user ID
        User invalidID = new User("John Doe", "12345", Arrays.asList("action"));
        assertFalse(invalidID.isValidUserID(), "Invalid user ID should fail");
        
        // Test null safety
        User nullCats = new User("John Doe", "12345678A", null);
        assertNotNull(nullCats.likedCategories, "Null categories should be handled");
    }
    
    private static void testRecommendations() {
        System.out.println("\n--- Testing Recommendations ---");
        
        // Clear static state
        Movie.movies.clear();
        
        // Setup test movies
        Movie actionMovie = new Movie("Action Movie", "AM123", Arrays.asList("action"));
        Movie dramaMovie = new Movie("Drama Movie", "DM456", Arrays.asList("drama"));
        actionMovie.save();
        dramaMovie.save();
        
        // Create user with matching categories
        User user = new User("Test User", "12345678A", Arrays.asList("action", "drama"));
        
        // Test recommendations
        Map<String, ArrayList<Movie>> recommendations = user.getRecommendations();
        assertEquals(2, recommendations.size(), "Should get recommendations for 2 categories");
        assertTrue(recommendations.containsKey("action"), "Should contain action recommendations");
        assertTrue(recommendations.containsKey("drama"), "Should contain drama recommendations");
    }
    
    private static void testFileManager() {
        System.out.println("\n--- Testing File Manager ---");
        
        try {
            // Test file writing and reading
            String testContent = "Test Movie,TM123\naction,drama";
            String testFile = "test_file_manager.txt";
            
            FileManager.writeFile(testFile, testContent);
            
            List<Map<String, Object>> data = FileManager.readFile(testFile);
            assertNotNull(data, "File reading should work");
            assertEquals(1, data.size(), "Should read one entry");
            assertEquals("Test Movie", data.get(0).get("label"), "Should read correct label");
            assertEquals("TM123", data.get(0).get("id"), "Should read correct ID");
            
            // Cleanup
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(testFile));
            
        } catch (Exception e) {
            fail("FileManager test failed: " + e.getMessage());
        }
    }
    
    // Simple assertion methods
    private static void assertTrue(boolean condition, String message) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("✅ PASS: " + message);
        } else {
            System.out.println("❌ FAIL: " + message);
        }
    }
    
    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }
    
    private static void assertEquals(Object expected, Object actual, String message) {
        testsTotal++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("✅ PASS: " + message);
        } else {
            System.out.println("❌ FAIL: " + message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }
    
    private static void assertNotNull(Object obj, String message) {
        testsTotal++;
        if (obj != null) {
            testsPassed++;
            System.out.println("✅ PASS: " + message);
        } else {
            System.out.println("❌ FAIL: " + message + " (object was null)");
        }
    }
    
    private static void fail(String message) {
        testsTotal++;
        System.out.println("❌ FAIL: " + message);
    }
}
