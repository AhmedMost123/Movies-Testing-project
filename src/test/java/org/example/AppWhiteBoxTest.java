package org.example;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//White Box Testing for App.java
/*
White Box Techniques Used:

- Statement Coverage
- Branch Coverage
- Decision Coverage
- Path Coverage
- Loop Coverage
- Error Path Testing

Paths Tested:

- Invalid arguments path
- File access failure path
- Movie validation error paths
- User validation error paths
- Full successful execution path

*/

class AppWhiteBoxTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field movieIds = Movie.class.getDeclaredField("USED_IDS");
        movieIds.setAccessible(true);
        ((Set<?>) movieIds.get(null)).clear();

        Field userIds = User.class.getDeclaredField("UID_SET");
        userIds.setAccessible(true);
        ((Set<?>) userIds.get(null)).clear();
    }

    @Test
    void testing_invalid_arguments_path() throws Exception {

        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        App.main(new String[]{});
    }

    @Test
    void testing_file_access_failure_path() throws Exception {

        App.main(new String[]{"wrong.txt","wrong2.txt"});
    }

    @Test
    void testing_movie_title_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("titanic,T123","romance"));
        Files.write(users,List.of("Ahmed,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Title ERROR"));
    }

    @Test
    void testing_movie_id_letters_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("Titanic,X123","romance"));
        Files.write(users,List.of("Ahmed,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Id letters ERROR"));
    }

    @Test
    void testing_movie_category_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("Titanic,T123","fantasy"));
        Files.write(users,List.of("Ahmed,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Category ERROR"));
    }

    @Test
    void testing_duplicate_category_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("Titanic,T123","romance,Romance"));
        Files.write(users,List.of("Ahmed,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Duplicate Category ERROR"));
    }

    @Test
    void testing_username_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("Titanic,T123","romance"));
        Files.write(users,List.of("Ahmed123,12345678A","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Username ERROR"));
    }

    @Test
    void testing_user_id_error_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("Titanic,T123","romance"));
        Files.write(users,List.of("Ahmed,123","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("User Id ERROR"));
    }

    @Test
    void testing_full_successful_execution_path() throws Exception {

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
    void testing_first_error_only_path() throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of("titanic,T123","fantasy"));
        Files.write(users,List.of("Ahmed123,123","romance"));

        App.main(new String[]{movies.toString(),users.toString(),output.toString()});

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Title ERROR"));
        assertFalse(content.contains("Movie Category ERROR"));
    }
}