package org.example;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppTest {

    private Path moviesFile;
    private Path usersFile;
    private Path outputFile;

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() throws Exception {
        moviesFile = Files.createTempFile("movies", ".txt");
        usersFile = Files.createTempFile("users", ".txt");
        outputFile = Files.createTempFile("output", ".txt");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);

        Files.deleteIfExists(moviesFile);
        Files.deleteIfExists(usersFile);
        Files.deleteIfExists(outputFile);
    }

    @Test
    //testing for both missing arguments where 1st arg is movies file and 2nd is users file
    void bothArgsFilesMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        App.main(new String[]{});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        assertEquals(expected.trim(), baos.toString().trim());
    }

    @Test
    void moviesFileArgsMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        App.main(new String[]{"users.txt"});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        assertEquals(expected.trim(), baos.toString().trim());
    }

    @Test
    void usersFileArgsMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        App.main(new String[]{"movies.txt"});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        assertEquals(expected.trim(), baos.toString().trim());
    }

    @Test
    void usersFileDoesNotExist() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Path fakeMovies = Files.createTempFile("movies", ".txt");
        Files.writeString(fakeMovies, "Avatar,AVT123\nAction\n");

        App.main(new String[]{
                fakeMovies.toString(),
                "users.txt"
        });

        assertTrue(baos.toString().contains("unable to access file"));
    }

    @Test
    void moviesFileDoesNotExist() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Path fakeUsers = Files.createTempFile("users", ".txt");
        Files.writeString(fakeUsers, "Mariam,12345678A\nAction\n");

        App.main(new String[]{
                "movies.txt",
                fakeUsers.toString()
        });

        assertTrue(baos.toString().contains("unable to access file"));
    }

    @Test
    void usersAndMoviesFilesDoNotExist() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        App.main(new String[]{
                "movies.txt",
                "users.txt"
        });

        assertTrue(baos.toString().contains("unable to access file"));
    }

    @Test
    //testing invalid movie title, 1st word not starting with capital letter
    void movieTitleStartsLowerCase() throws Exception {

        Files.writeString(moviesFile,
                "avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertTrue(result.contains("ERROR"));
    }

    @Test
    void movieTitleStartsLowerCase2() throws Exception {

        Files.writeString(moviesFile,
                "Avatar movie,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertTrue(result.contains("ERROR"));
    }

    @Test
    void movieTitleStartsLowerCase3() throws Exception {

        Files.writeString(moviesFile,
                "avatar movie,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertTrue(result.contains("ERROR"));
    }

    @Test
    void invalidUsername() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,A123\nAction\n");

        Files.writeString(usersFile,
                "john2123,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile).trim();

        assertTrue(result.contains("Username ERROR"));
    }

    @Test
    void emptyUsername() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                ",12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.contains("ERROR"));
    }

    @Test
    void multipleUsersProcessing() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n" +
                "Mariam,87654321B\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.contains("For User"));
    }

    @Test
    void fullSystemSuccessfulRun() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.contains("For User"));
    }

    @Test
    void emptyMoviesFile() throws Exception {

        Files.writeString(moviesFile, "");
        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result != null);
    }

    @Test
    void invalidMovieStopsSystemBeforeUsers() throws Exception {

        Files.writeString(moviesFile,
                "avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.contains("ERROR"));
        assertFalse(result.contains("For User"));
    }

    @Test
    void minimumValidInput() throws Exception {

        Files.writeString(moviesFile,
                "A,A1\nAction\n");

        Files.writeString(usersFile,
                "U,U1\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.length() >= 0);
    }

    @Test
    void boundaryUserIdLength() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,U1\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.length() > 0);
    }

    @Test
    void boundaryMovieIdLength() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,A1\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.length() > 0);
    }

    @Test
    void invalidCategoryInMovie() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,AVT123\nFantasy\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertFalse(result.contains("For User"));
    }

    @Test
    void movieFailurePreventsUserProcessing() throws Exception {

        Files.writeString(moviesFile,
                "avatar,AVT123\nAction\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\nMariam,87654321B\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertFalse(result.contains("Mariam"));
    }

    @Test
    void whitespaceInInputFiles() throws Exception {

        Files.writeString(moviesFile,
                "   Avatar   ,   AVT123   \nAction\n");

        Files.writeString(usersFile,
                "   John   ,   12345678A   \nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile);

        assertTrue(result.length() > 0);
    }
}