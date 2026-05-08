package org.example;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//Bottom-Up Integration Testing
/*
Integration Order:

Movie.java
↓
User.java
↓
FileManager.java
↓
App.java

Drivers Used:

Level 1 Driver:
Simulates higher modules while testing Movie.java

Level 2 Driver:
Simulates App.java while testing User.java integration

Level 3 Driver:
Simulates App.java interaction with FileManager.java

Level 4:
Real App.java full integration

*/

class BottomUpIntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field movieIds = Movie.class.getDeclaredField("USED_IDS");
        movieIds.setAccessible(true);
        ((java.util.Set<?>) movieIds.get(null)).clear();

        Field userIds = User.class.getDeclaredField("UID_SET");
        userIds.setAccessible(true);
        ((java.util.Set<?>) userIds.get(null)).clear();
    }

    // Driver for Movie.java testing
    private void movieDriver(Movie movie) {
        movie.save();
    }

    // Driver for User.java testing
    private Map<String, ArrayList<Movie>> userDriver(User user) {
        return user.getRecommendations();
    }

    // Driver for FileManager.java testing
    private List<Map<String, Object>> fileReadDriver(String path) throws Exception {
        return FileManager.readFile(path);
    }

    // Driver for FileManager write testing
    private void fileWriteDriver(String path, String content) throws Exception {
        FileManager.writeFile(path, content);
    }

    // Level 1: Testing Movie.java first using drivers
    @Test
    void testing_movie_save_integration() {

        Movie movie = new Movie("Titanic","T123",List.of("romance","drama"));

        movieDriver(movie);

        assertTrue(Movie.movies.containsKey("romance"));
        assertEquals(1, Movie.movies.get("romance").size());
    }

    @Test
    void testing_movie_multiple_categories_integration() {

        Movie movie = new Movie("Avengers","A123",List.of("action","thriller"));

        movieDriver(movie);

        assertTrue(Movie.movies.containsKey("action"));
        assertTrue(Movie.movies.containsKey("thriller"));
    }

    // Level 2: Integrating User.java with Movie.java
    @Test
    void testing_user_recommendations_integration() {

        Movie movie1 = new Movie("Titanic","T123",List.of("romance"));
        Movie movie2 = new Movie("Avengers","A123",List.of("action"));

        movieDriver(movie1);
        movieDriver(movie2);

        User user = new User("Ahmed","12345678A",List.of("romance"));

        Map<String, ArrayList<Movie>> recommendations = userDriver(user);

        assertTrue(recommendations.containsKey("romance"));
        assertEquals(1,recommendations.get("romance").size());
    }

    @Test
    void testing_user_multiple_category_recommendations() {

        Movie movie1 = new Movie("Titanic","T123",List.of("romance"));
        Movie movie2 = new Movie("Avengers","A123",List.of("action"));

        movieDriver(movie1);
        movieDriver(movie2);

        User user = new User("Ahmed","12345678A",List.of("romance","action"));

        Map<String, ArrayList<Movie>> recommendations = userDriver(user);

        assertTrue(recommendations.containsKey("romance"));
        assertTrue(recommendations.containsKey("action"));
    }

    // Level 3: Integrating FileManager.java
    @Test
    void testing_file_manager_read_integration() throws Exception {

        Path movies = tempDir.resolve("movies.txt");

        Files.write(movies,List.of("Titanic,T123","romance"));

        List<Map<String, Object>> result = fileReadDriver(movies.toString());

        assertEquals(1,result.size());
        assertEquals("Titanic",result.get(0).get("label"));
    }

    @Test
    void testing_file_manager_write_integration() throws Exception {

        Path output = tempDir.resolve("output.txt");

        fileWriteDriver(output.toString(),"HELLO");

        String content = Files.readString(output);

        assertEquals("HELLO",content);
    }

    // Level 4: Full integration with App.java
    @Test
    void testing_full_application_flow() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,T123",
                "romance",
                "Avengers,A123",
                "action"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance,action"
        ));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Titanic"));
        assertTrue(content.contains("Avengers"));
    }

    @Test
    void testing_movie_id_error_propagation() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,X123",
                "romance"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance"
        ));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Id letters ERROR"));
    }

    @Test
    void testing_duplicate_user_id_integration() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,T123",
                "romance"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance",
                "Ali,12345678A",
                "action"
        ));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("User Id ERROR"));
    }

    @Test
    void testing_duplicate_categories_integration() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,T123",
                "romance,Romance"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance"
        ));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Duplicate Category ERROR"));
    }
}