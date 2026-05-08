package org.example;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {

    @TempDir
    Path tempDir;

    //Black Box testing for App.java

    @BeforeEach
    void resetStaticData() {
        // clear static movie storage
        Movie.movies.clear();
        // reset used movie ids indirectly
        // by using reflection because USED_IDS is private static
        try {
            java.lang.reflect.Field field =Movie.class.getDeclaredField("USED_IDS");
            field.setAccessible(true);
            ((java.util.Set<?>) field.get(null)).clear();

        }
        catch (Exception e) 
        {

        }
        // clear user ids
        try {
            java.lang.reflect.Field field =User.class.getDeclaredField("UID_SET");
        field.setAccessible(true);
        ((java.util.Set<?>) field.get(null)).clear();

        } 
        catch (Exception e) 
        {
        }
    }


    @Test
    void testing_valid_execution() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T123","romance,drama","Avengers,A123","action,thriller"));
        Files.write(users, List.of("Ahmed,12345678A","romance,action"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("For User: Ahmed,12345678A"));
        assertTrue(content.contains("Titanic"));
        assertTrue(content.contains("Avengers"));
    }

    @Test
    void testing_missing_arguments() throws Exception {
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        App.main(new String[]{});
    }
    @Test
    void testing_one_argument_only() throws Exception {
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        App.main(new String[]{"movies.txt"});
    }

    @Test
    void testing_missing_movies_file() throws Exception {
        Path users = tempDir.resolve("users.txt");
        Files.write(users, List.of("Ahmed,12345678A","action"));
        App.main(new String[]{"missing.txt",users.toString()});
    }
    @Test
    void testing_missing_users_file() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Files.write(movies, List.of("Titanic,T123","romance"));
        App.main(new String[]{movies.toString(),"missing.txt"});
    }


    @Test
    void testing_invalid_movie_title() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("titanic,T123","romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance"
        ));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Movie Title ERROR"));
    }

    @Test
    void testing_invalid_movie_id_letters() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        // Titanic capitals => T
        // invalid id letters => X123
        Files.write(movies, List.of("Titanic,X123","romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Movie Id letters ERROR"));
    }

    @Test
    void testing_duplicate_movie_id_numbers() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T111","romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Movie Id numbers ERROR"));
    }

    @Test
    void testing_invalid_movie_category() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        // valid movie id first
        Files.write(movies, List.of("Titanic,T123","fantasy"));
        Files.write(users, List.of("Ahmed,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Movie Category ERROR"));
    }

    @Test
    void testing__duplicate_categories() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T123","romance,Romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Duplicate Category ERROR"));
    }

    @Test
    void testing_invalid_username() throws Exception 
    {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T123","romance"));
        // invalid because contains numbers
        Files.write(users, List.of("Ahmed123,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Username ERROR"));
}

    @Test
    void testing_invalid_user_id() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T123","romance"));
        Files.write(users, List.of("Ahmed,123","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("User Id ERROR"));
    }

    @Test
    void testing_duplicate_user_id() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("Titanic,T123","romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance","Ali,12345678A","action"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("User Id ERROR"));
    }


    @Test
    void testing_default_output_file() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Files.write(movies, List.of("Titanic,T123","romance"));
        Files.write(users, List.of("Ahmed,12345678A","romance"));
        App.main(new String[]{movies.toString(),users.toString()});
        Path output = Path.of("output.txt");
        assertTrue(Files.exists(output));
        Files.deleteIfExists(output);
    }

    @Test
    void testing_first_error_only() throws Exception {
        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");
        Files.write(movies, List.of("titanic,T123","fantasy"));
        Files.write(users, List.of("Ahmed123,123","romance"));
        App.main(new String[]{movies.toString(),users.toString(),output.toString()});
        String content = Files.readString(output);
        assertTrue(content.contains("Movie Title ERROR"));
    }
}