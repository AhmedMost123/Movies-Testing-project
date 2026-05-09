package org.example;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//Data Flow Testing for App.java
/*
DFT Techniques Used:

- Definition-Use Paths
- Variable Lifecycle Testing
- All Definitions Coverage
- All Uses Coverage
- Data Propagation Testing

Variables Tested:

- outputFile
- usersFile
- moviesFile
- usersData
- moviesData
- movie
- user
- suggestions
- buffer

*/

class AppDataFlowTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field movieIds =
                Movie.class.getDeclaredField("USED_IDS");

        movieIds.setAccessible(true);

        ((Set<?>) movieIds.get(null)).clear();

        Field userIds =
                User.class.getDeclaredField("UID_SET");

        userIds.setAccessible(true);

        ((Set<?>) userIds.get(null)).clear();
    }

    @Test
    void testing_invalid_arguments_data_flow() throws Exception {

        System.setIn(
                new ByteArrayInputStream("\n".getBytes())
        );

        App.main(new String[]{});
    }

    @Test
    void testing_file_loading_data_flow() throws Exception {

        App.main(new String[]{
                "wrong.txt",
                "wrong2.txt"
        });
    }

    @Test
    void testing_movie_data_definition_to_use()
            throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Titanic,T123",
                "romance"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance"
        ));

        App.main(new String[]{
                movies.toString(),
                users.toString(),
                output.toString()
        });

        String content = Files.readString(output);

        assertTrue(content.contains("Titanic"));
    }

    @Test
    void testing_user_data_definition_to_use()
            throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "Avengers,A123",
                "action"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "action"
        ));

        App.main(new String[]{
                movies.toString(),
                users.toString(),
                output.toString()
        });

        String content = Files.readString(output);

        assertTrue(content.contains("Ahmed"));
    }

    @Test
    void testing_buffer_data_flow() throws Exception {

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

        App.main(new String[]{
                movies.toString(),
                users.toString(),
                output.toString()
        });

        String content = Files.readString(output);

        assertTrue(content.contains("Titanic"));
        assertTrue(content.contains("Avengers"));
    }

    @Test
    void testing_error_message_data_flow()
            throws Exception {

        Path movies = tempDir.resolve("movies.txt");
        Path users = tempDir.resolve("users.txt");
        Path output = tempDir.resolve("output.txt");

        Files.write(movies,List.of(
                "titanic,T123",
                "romance"
        ));

        Files.write(users,List.of(
                "Ahmed,12345678A",
                "romance"
        ));

        App.main(new String[]{
                movies.toString(),
                users.toString(),
                output.toString()
        });

        String content = Files.readString(output);

        assertTrue(content.contains("Movie Title ERROR"));
    }
}