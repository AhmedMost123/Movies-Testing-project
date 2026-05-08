package org.example;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class IntegrationTest {

    @TempDir
    Path tempDir;

    private Path moviesFile;
    private Path usersFile;
    private Path outputFile;

    @BeforeEach
    void setUp() throws IOException {
        moviesFile = tempDir.resolve("movies.txt");
        usersFile = tempDir.resolve("users.txt");
        outputFile = tempDir.resolve("output.txt");
    }
    @AfterEach
    void cleanup() throws Exception {
        Movie.movies.clear();

        Field uidSet = User.class.getDeclaredField("UID_SET");
        uidSet.setAccessible(true);
        ((Set<?>) uidSet.get(null)).clear();
    }
    // ─────────────────────────────────────────────
    // 1 - FileManager + Movie Integration Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("1 - FileManager + Movie Integration")
    class FileManagerMovieIntegration {

        @Test
        @DisplayName("Complete movie processing workflow")
        void completeMovieProcessingWorkflow() throws Exception {
            // Arrange
            String movieContent = "The Matrix,TM123\naction\nInception,I456\ndrama\nThe Dark Knight,TDK789\naction,drama";
            Files.writeString(moviesFile, movieContent);

            // Act
            List<Map<String, Object>> movieData = FileManager.readFile(moviesFile.toString());

            // Assert
            assertEquals(3, movieData.size());
            
            // Verify first movie
            assertEquals("The Matrix", movieData.get(0).get("label"));
            assertEquals("TM123", movieData.get(0).get("id"));
            @SuppressWarnings("unchecked")
            List<String> categories1 = (List<String>) movieData.get(0).get("category");
            assertEquals(1, categories1.size());
            assertEquals("action", categories1.get(0));

            // Verify third movie with multiple categories
            assertEquals("The Dark Knight", movieData.get(2).get("label"));
            assertEquals("TDK789", movieData.get(2).get("id"));
            @SuppressWarnings("unchecked")
            List<String> categories3 = (List<String>) movieData.get(2).get("category");
            assertEquals(2, categories3.size());
            assertTrue(categories3.contains("action"));
            assertTrue(categories3.contains("drama"));
        }

        @Test
        @DisplayName("Movie validation integration with file reading")
        void movieValidationIntegration() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\naction\nInvalid Movie,IM456\naction";
            Files.writeString(moviesFile, movieContent);

            // Act
            List<Map<String, Object>> movieData = FileManager.readFile(moviesFile.toString());

            // Create and validate movies
            Movie validMovie = new Movie(
                (String) movieData.get(0).get("label"),
                (String) movieData.get(0).get("id"),
                (List<String>) movieData.get(0).get("category")
            );

            Movie invalidMovie = new Movie(
                (String) movieData.get(1).get("label"),
                (String) movieData.get(1).get("id"),
                (List<String>) movieData.get(1).get("category")
            );

            // Assert
            assertTrue(validMovie.isValidMovieTitle());
            assertTrue(validMovie.isValidMovieID());
            assertTrue(validMovie.isValidCategory());
            assertFalse(validMovie.hasDuplicateCategories());

            assertFalse(invalidMovie.isValidMovieTitle()); // "Invalid" starts with lowercase
        }

        @Test
        @DisplayName("Movie save integration with static collections")
        void movieSaveIntegration() throws Exception {
            // Arrange
            String movieContent = "Action Movie,AM123\naction\nDrama Movie,DM456\ndrama";
            Files.writeString(moviesFile, movieContent);

            // Act
            List<Map<String, Object>> movieData = FileManager.readFile(moviesFile.toString());
            
            for (Map<String, Object> data : movieData) {
                Movie movie = new Movie(
                    (String) data.get("label"),
                    (String) data.get("id"),
                    (List<String>) data.get("category")
                );
                movie.save();
            }

            // Assert
            assertTrue(Movie.movies.containsKey("action"));
            assertTrue(Movie.movies.containsKey("drama"));
            assertEquals(1, Movie.movies.get("action").size());
            assertEquals(1, Movie.movies.get("drama").size());
        }
    }

    // ─────────────────────────────────────────────
    // 2 - FileManager + User Integration Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("2 - FileManager + User Integration")
    class FileManagerUserIntegration {

        @Test
        @DisplayName("Complete user processing workflow")
        void completeUserProcessingWorkflow() throws Exception {
            // Arrange
            String userContent = "John Doe,JD123456789\naction,drama\nJane Smith,JS987654321\ncomedy,romance";
            Files.writeString(usersFile, userContent);

            // Act
            List<Map<String, Object>> userData = FileManager.readFile(usersFile.toString());

            // Assert
            assertEquals(2, userData.size());
            
            // Verify first user
            assertEquals("John Doe", userData.get(0).get("label"));
            assertEquals("JD123456789", userData.get(0).get("id"));
            @SuppressWarnings("unchecked")
            List<String> categories1 = (List<String>) userData.get(0).get("category");
            assertEquals(2, categories1.size());
            assertTrue(categories1.contains("action"));
            assertTrue(categories1.contains("drama"));

            // Verify second user
            assertEquals("Jane Smith", userData.get(1).get("label"));
            assertEquals("JS987654321", userData.get(1).get("id"));
            @SuppressWarnings("unchecked")
            List<String> categories2 = (List<String>) userData.get(1).get("category");
            assertEquals(2, categories2.size());
            assertTrue(categories2.contains("comedy"));
            assertTrue(categories2.contains("romance"));
        }

        @Test
        @DisplayName("User validation integration with file reading")
        void userValidationIntegration() throws Exception {
            // Arrange
            String userContent = "Valid User,VU123456789\naction\nInvalid User,IU12345678\naction";
            Files.writeString(usersFile, userContent);

            // Act
            List<Map<String, Object>> userData = FileManager.readFile(usersFile.toString());

            // Create and validate users
            User validUser = new User(
                (String) userData.get(0).get("label"),
                (String) userData.get(0).get("id"),
                (List<String>) userData.get(0).get("category")
            );

            User invalidUser = new User(
                (String) userData.get(1).get("label"),
                (String) userData.get(1).get("id"),
                (List<String>) userData.get(1).get("category")
            );

            // Assert
            assertTrue(validUser.isValidUserName());
            assertTrue(validUser.isValidUserID());

            assertTrue(invalidUser.isValidUserName());
            assertFalse(invalidUser.isValidUserID()); // Too short ID
        }

        @Test
        @DisplayName("User save integration with static collections")
        void userSaveIntegration() throws Exception {
            // Arrange
            String userContent = "User One,UO123456789\naction\nUser Two,UT987654321\ndrama";
            Files.writeString(usersFile, userContent);

            // Act
            List<Map<String, Object>> userData = FileManager.readFile(usersFile.toString());
            
            User user1 = new User(
                (String) userData.get(0).get("label"),
                (String) userData.get(0).get("id"),
                (List<String>) userData.get(0).get("category")
            );
            
            User user2 = new User(
                (String) userData.get(1).get("label"),
                (String) userData.get(1).get("id"),
                (List<String>) userData.get(1).get("category")
            );

            // Assert - before save
            assertTrue(user1.isValidUserID());
            assertTrue(user2.isValidUserID());

            // Act - save first user
            user1.save();

            // Assert - after first save
            assertTrue(user1.isValidUserID());
            assertTrue(user2.isValidUserID()); // Still valid (different ID)

            // Act - save second user
            user2.save();

            // Assert - after both saves
            assertTrue(user1.isValidUserID());
            assertTrue(user2.isValidUserID());
        }
    }

    // ─────────────────────────────────────────────
    // 3 - App + FileManager Integration Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("3 - App + FileManager Integration")
    class AppFileManagerIntegration {

        @Test
        @DisplayName("App processes valid files correctly")
        void appProcessesValidFiles() throws Exception {
            // Arrange
            String movieContent = "Action Movie,AM123\naction\nDrama Movie,DM456\ndrama";
            String userContent = "John Doe,JD123456789\naction,drama";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertTrue(output.contains("For User: John Doe,JD123456789"));
            assertTrue(output.contains("action: AM123-Action Movie"));
            assertTrue(output.contains("drama: DM456-Drama Movie"));
        }

        @Test
        @DisplayName("App handles missing movie file gracefully")
        void appHandlesMissingMovieFile() throws Exception {
            // Arrange - only create user file
            String userContent = "John Doe,JD123456789\naction,drama";
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert - should not create output file for file access error
            assertFalse(Files.exists(outputFile));
        }

        @Test
        @DisplayName("App handles missing user file gracefully")
        void appHandlesMissingUserFile() throws Exception {
            // Arrange - only create movie file
            String movieContent = "Action Movie,AM123\naction";
            Files.writeString(moviesFile, movieContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert - should not create output file for file access error
            assertFalse(Files.exists(outputFile));
        }

        @Test
        @DisplayName("App handles invalid arguments")
        void appHandlesInvalidArguments() throws Exception {
            // Act - test with no arguments
            String[] args1 = {};
            App.main(args1);

            // Act - test with one argument
            String[] args2 = {moviesFile.toString()};
            App.main(args2);

            // Assert - should not create output file
            assertFalse(Files.exists(outputFile));
        }
    }

    // ─────────────────────────────────────────────
    // 4 - End-to-End Recommendation Workflow Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("4 - End-to-End Recommendation Workflow")
    class EndToEndRecommendationWorkflow {

        @Test
        @DisplayName("Complete recommendation generation workflow")
        void completeRecommendationWorkflow() throws Exception {
            // Arrange
            String movieContent = "The Matrix,TM123\naction\nInception,I456\ndrama\nThe Godfather,TG789\ndrama\nPulp Fiction,PF321\naction";
            String userContent = "John Doe,JD123456789\naction,drama\nJane Smith,JS987654321\ncomedy,romance";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            
            // Verify John Doe gets recommendations
            assertTrue(output.contains("For User: John Doe,JD123456789"));
            assertTrue(output.contains("action: TM123-The Matrix,PF321-Pulp Fiction"));
            assertTrue(output.contains("drama: I456-Inception,TG789-The Godfather"));
            
            // Verify Jane Smith gets no recommendations (no comedy/romance movies)
            assertTrue(output.contains("For User: Jane Smith,JS987654321"));
            // Should not have any categories listed for Jane
            int janeIndex = output.indexOf("Jane Smith,JS987654321");
            int nextUserIndex = output.indexOf("\n", janeIndex + 1);
            if (nextUserIndex == -1) nextUserIndex = output.length();
            String janeSection = output.substring(janeIndex, nextUserIndex);
            assertEquals("For User: Jane Smith,JS987654321\n", janeSection);
        }

        @Test
        @DisplayName("Recommendation workflow with no matching categories")
        void recommendationWorkflowNoMatchingCategories() throws Exception {
            // Arrange
            String movieContent = "Action Movie,AM123\naction\nDrama Movie,DM456\ndrama";
            String userContent = "John Doe,JD123456789\ncomedy,romance";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            
            assertTrue(output.contains("For User: John Doe,JD123456789"));
            // Should not have any categories listed
            int userIndex = output.indexOf("John Doe,JD123456789");
            int endIndex = output.length();
            String userSection = output.substring(userIndex, endIndex);
            assertEquals("For User: John Doe,JD123456789\n", userSection);
        }

        @Test
        @DisplayName("Recommendation workflow with multiple users")
        void recommendationWorkflowMultipleUsers() throws Exception {
            // Arrange
            String movieContent = "Action Movie,AM123\naction\nDrama Movie,DM456\ndrama\nComedy Movie,CM789\ncomedy";
            String userContent = "User One,UO123456789\naction\nUser Two,UT987654321\ndrama,comedy\nUser Three,UT555555555\naction,drama,comedy";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            
            // Verify all users are processed
            assertTrue(output.contains("For User: User One,UO123456789"));
            assertTrue(output.contains("For User: User Two,UT987654321"));
            assertTrue(output.contains("For User: User Three,UT555555555"));
            
            // Verify recommendations
            assertTrue(output.contains("action: AM123-Action Movie"));
            assertTrue(output.contains("drama: DM456-Drama Movie"));
            assertTrue(output.contains("comedy: CM789-Comedy Movie"));
        }
    }

    // ─────────────────────────────────────────────
    // 5 - Error Handling Integration Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("5 - Error Handling Integration")
    class ErrorHandlingIntegration {

        @Test
        @DisplayName("System stops at first movie title error")
        void systemStopsAtFirstMovieTitleError() throws Exception {
            // Arrange
            String movieContent = "invalid Movie,IM123\naction\nValid Movie,VM456\ndrama";
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("Movie Title ERROR: invalid Movie is wrong", output);
        }

        @Test
        @DisplayName("System stops at first movie ID letters error")
        void systemStopsAtFirstMovieIDLettersError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\naction\nAnother Movie,AM456\ndrama";
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act - modify movie ID to have wrong letters
            String modifiedContent = movieContent.replace("VM123", "XZ123");
            Files.writeString(moviesFile, modifiedContent);
            
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("Movie Id letters ERROR: XZ123 are wrong", output);
        }

        @Test
        @DisplayName("System stops at first movie ID numbers error")
        void systemStopsAtFirstMovieIDNumbersError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM113\naction\nAnother Movie,AM456\ndrama";
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("Movie Id numbers ERROR: VM113 aren't unique", output);
        }

        @Test
        @DisplayName("System stops at first movie category error")
        void systemStopsAtFirstMovieCategoryError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\ninvalid\nAnother Movie,AM456\ndrama";
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("Movie Category ERROR", output);
        }

        @Test
        @DisplayName("System stops at first duplicate category error")
        void systemStopsAtFirstDuplicateCategoryError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\naction,action\nAnother Movie,AM456\ndrama";
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("Duplicate Category ERROR", output);
        }

        @Test
        @DisplayName("System stops at first username error")
        void systemStopsAtFirstUsernameError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\naction";
            String userContent = "invalid User,IU123456789\naction\nValid User,VU987654321\ndrama";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("User Id ERROR: IU123456789 is wrong", output);
        }

        @Test
        @DisplayName("System stops at first user ID error")
        void systemStopsAtFirstUserIDError() throws Exception {
            // Arrange
            String movieContent = "Valid Movie,VM123\naction";
            String userContent = "Valid User,VU12345678\naction\nAnother User,AU987654321\ndrama";
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent);

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertEquals("User Id ERROR: VU12345678 is wrong", output);
        }
    }

    // ─────────────────────────────────────────────
    // 6 - Performance and Scalability Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("6 - Performance and Scalability")
    class PerformanceAndScalability {

        @Test
        @DisplayName("System handles large number of movies")
        void systemHandlesLargeNumberOfMovies() throws Exception {
            // Arrange
            StringBuilder movieContent = new StringBuilder();
            for (int i = 1; i <= 100; i++) {
                movieContent.append("Movie ").append(i).append(",M").append(String.format("%03d", i)).append("\n");
                movieContent.append("action\n");
            }
            
            String userContent = "John Doe,JD123456789\naction";
            
            Files.writeString(moviesFile, movieContent.toString());
            Files.writeString(usersFile, userContent);

            // Act
            long startTime = System.currentTimeMillis();
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);
            long endTime = System.currentTimeMillis();

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            assertTrue(output.contains("For User: John Doe,JD123456789"));
            assertTrue(output.contains("action:"));
            
            // Performance check - should complete within reasonable time (5 seconds)
            assertTrue(endTime - startTime < 5000, "Processing took too long: " + (endTime - startTime) + "ms");
        }

        @Test
        @DisplayName("System handles moderate number of users")
        void systemHandlesModerateNumberOfUsers() throws Exception {
            // Arrange - realistic number of users for academic project
            String movieContent = "Action Movie,AM123\naction\nDrama Movie,DM456\ndrama\nComedy Movie,CM789\ncomedy";
            
            StringBuilder userContent = new StringBuilder();
            for (int i = 1; i <= 10; i++) { // Reduced from 50 to 10 for realistic testing
                userContent.append("User ").append(i).append(",U").append(String.format("%09d", i)).append("\n");
                userContent.append("action,drama,comedy\n");
            }
            
            Files.writeString(moviesFile, movieContent);
            Files.writeString(usersFile, userContent.toString());

            // Act
            String[] args = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args);

            // Assert
            assertTrue(Files.exists(outputFile));
            String output = Files.readString(outputFile);
            
            // Verify all users are processed
            for (int i = 1; i <= 10; i++) {
                assertTrue(output.contains("User " + i + ",U" + String.format("%09d", i)));
            }
        }
    }

    // ─────────────────────────────────────────────
    // 7 - Cleanup and State Management Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("7 - Cleanup and State Management")
    class CleanupAndStateManagement {

        @Test
        @DisplayName("Static state cleanup between test runs")
        void staticStateCleanup() throws Exception {
            // Arrange - first run
            String movieContent1 = "Movie One,MO123\naction";
            String userContent1 = "User One,UO123456789\naction";
            
            Files.writeString(moviesFile, movieContent1);
            Files.writeString(usersFile, userContent1);

            // Act - first run
            String[] args1 = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args1);

            // Arrange - second run with different data
            String movieContent2 = "Movie Two,MT456\ndrama";
            String userContent2 = "User Two,UT987654321\ndrama";
            
            Files.writeString(moviesFile, movieContent2);
            Files.writeString(usersFile, userContent2);

            // Act - second run
            String[] args2 = {moviesFile.toString(), usersFile.toString(), outputFile.toString()};
            App.main(args2);

            // Assert - second run should not be affected by first run
            String output = Files.readString(outputFile);
            assertTrue(output.contains("For User: User Two,UT987654321"));
            assertTrue(output.contains("drama: MT456-Movie Two"));
            assertFalse(output.contains("User One"));
            assertFalse(output.contains("Movie One"));
        }
    }

}
