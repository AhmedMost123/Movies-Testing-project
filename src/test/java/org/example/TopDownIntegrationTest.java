package org.example;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//Top-Down Integration Testing Using Stubs
/*
Integration Order:
App.java
↓
FileManager Stub
↓
Movie Stub
↓
User Stub

*/

class TopDownIntegrationTest {

    @TempDir
    Path tempDir;

    // Stub for FileManager.java
    static class FileManagerStub {

        static List<Map<String, Object>> readFile(String path) {

            List<Map<String, Object>> data = new ArrayList<>();

            Map<String, Object> item = new HashMap<>();

            item.put("label", "Titanic");
            item.put("id", "T123");
            item.put("category", List.of("romance"));

            data.add(item);

            return data;
        }

        static String writtenContent = "";

        static void writeFile(String path, String content) {
            writtenContent = content;
        }
    }

    // Stub for Movie.java
    static class MovieStub {

        String title;
        String id;
        List<String> categories;

        MovieStub(String title, String id, List<String> categories) {
            this.title = title;
            this.id = id;
            this.categories = categories;
        }

        boolean isValidMovieTitle() {
            return true;
        }

        boolean isValidMovieID() {
            return true;
        }

        boolean isUniqueMovieID() {
            return true;
        }

        boolean isValidCategory() {
            return true;
        }

        boolean hasDuplicateCategories() {
            return false;
        }

        void save() {
        }

        @Override
        public String toString() {
            return id + "-" + title;
        }
    }

    // Stub for User.java
    static class UserStub {

        String name;
        String id;
        List<String> categories;

        UserStub(String name, String id, List<String> categories) {
            this.name = name;
            this.id = id;
            this.categories = categories;
        }

        boolean isValidUserName() {
            return true;
        }

        boolean isValidUserID() {
            return true;
        }

        void save() {
        }

        Map<String, ArrayList<MovieStub>> getRecommendations() {

            Map<String, ArrayList<MovieStub>> result = new HashMap<>();

            ArrayList<MovieStub> movies = new ArrayList<>();

            movies.add(new MovieStub("Titanic","T123",List.of("romance")));

            result.put("romance", movies);

            return result;
        }
    }

    // Level 1: Testing App.java only
    @Test
    void testing_app_argument_validation_using_stub() throws Exception {

        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        App.main(new String[]{});
    }

    // Level 2: Testing Movie stub integration
    @Test
    void testing_movie_stub_validation() {

        MovieStub movie = new MovieStub("Titanic","T123",List.of("romance"));

        assertTrue(movie.isValidMovieTitle());
        assertTrue(movie.isValidMovieID());
        assertTrue(movie.isUniqueMovieID());
    }

    // Level 3: Testing User stub integration
    @Test
    void testing_user_stub_recommendations() {

        UserStub user = new UserStub("Ahmed","12345678A",List.of("romance"));

        Map<String, ArrayList<MovieStub>> recommendations = user.getRecommendations();

        assertTrue(recommendations.containsKey("romance"));

        assertEquals(1,recommendations.get("romance").size());
    }

    // Level 4: Integrating real FileManager
    @Test
    void testing_real_file_manager_integration() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of("Titanic,T123","romance"));

        List<Map<String, Object>> result = FileManager.readFile(file.toString());

        assertEquals(1,result.size());
    }

    // Level 5: Full system integration
    @Test
    void testing_full_system_execution() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,T123",
                "romance,drama",
                "Avengers,A123",
                "action,thriller"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance,action"
        ));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("For User: Ahmed,12345678A"));
        assertTrue(content.contains("Titanic"));
        assertTrue(content.contains("Avengers"));
    }

    // Testing error propagation in integrated system
    @Test
    void testing_error_propagation_in_full_system() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("titanic,T123","romance"));

        Files.write(users,List.of("Ahmed,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Title ERROR"));
    }
}