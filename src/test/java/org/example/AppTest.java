package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

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

    private void write(Path p, String c) throws Exception {
        Files.writeString(p, c);
    }

    private String read() throws Exception {
        return Files.readString(outputFile);
    }
    @Test
    //testing for both missing arguments where 1st arg is movies file and 2nd is users file
    void bothArgsFilesMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        App.main(new String[]{});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        String actual = baos.toString().replace("\r\n", "\n").trim();
        String exp = expected.replace("\r\n", "\n").trim();

        assertEquals(exp, actual,"Both Files Missing Test Case Failed");
    }
    @Test
    //testing missing movies file args
    void moviesFileArgsMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // only users file provided (missing movies file)
        App.main(new String[]{"users.txt"});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        String actual = baos.toString().replace("\r\n", "\n").trim();
        String exp = expected.replace("\r\n", "\n").trim();

        assertEquals(exp, actual,"Missing Movies File Test Case Failed");
    }
    @Test
    //testing missing users file args
    void usersFileArgsMissing() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // only movies file provided (missing users file)
        App.main(new String[]{"movies.txt"});

        String expected =
                "wrong launching arguments, make sure the following arguments are passed in order:\n" +
                "<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]";

        String actual = baos.toString().replace("\r\n", "\n").trim();
        String exp = expected.replace("\r\n", "\n").trim();

        assertEquals(exp, actual,"Missing Users File Test Case Failed");
    }
    @Test
    //testing users file doesnt exist
    void usersFileDoesNotExist() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        //we will only create movies file, no users file created
        Path fakeMovies = Files.createTempFile("movies", ".txt");
        Files.writeString(fakeMovies,"Avatar,AVT123\nAction\n");
        assertThrows(Exception.class, () -> {
            App.main(new String[]{
                    fakeMovies.toString(),
                    "users.txt"
            });
        }, "Users File Doesnt Exist Test Case Failed");
    }
    @Test
  //testing movies file doesnt exist
  void moviesFileDoesNotExist() throws Exception {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      System.setIn(new ByteArrayInputStream("\n".getBytes()));

    //we will only create users file, no movies file created
      Path fakeUsers = Files.createTempFile("users", ".txt");
      Files.writeString(fakeUsers, "Mariam,12345678A\nAction\n");

      assertThrows(Exception.class, () -> {
          App.main(new String[]{
                  "movies.txt",
                  fakeUsers.toString()
          });
      }, "Users File Doesnt Exist Test Case Failed");
  }
    @Test
    //testing users and movies files both dont exist
    void usersAndMoviesFilesDoNotExist() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        //we will only create movies file, no users file created
        assertThrows(Exception.class, () -> {
            App.main(new String[]{
                    "movies.txt",
                    "users.txt"
            });
        }, "Users and Movies Files Dont Exist Test Case Failed");
    }
    
    @Test
    //testing invalid movie title, 1st word not starting with capital letter
    void movieTitleStartsLowerCase() throws Exception {

        Files.writeString(moviesFile,
                "avatar,AVT123\n" +  
                "Action\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertEquals("Movie Title ERROR: avatar is wrong", result);
    }
    @Test
    //testing invalid movie title, 2nd word not starting with capital letter
    void movieTitleStartsLowerCase2() throws Exception {

        Files.writeString(moviesFile,
                "Avatar movie,AVT123\n" +  
                "Action\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertEquals("Movie Title ERROR: Avatar movie is wrong", result);
    }
    @Test
    //testing invalid movie title, both words not starting with capital letter
    void movieTitleStartsLowerCase3() throws Exception {

        Files.writeString(moviesFile,
                "avatar movie,AVT123\n" +  
                "Action\n");

        Files.writeString(usersFile,
                "John,12345678A\nAction\n");

        App.main(new String[]{moviesFile.toString(), usersFile.toString(), outputFile.toString()});

        String result = Files.readString(outputFile).trim();

        assertEquals("Movie Title ERROR: avatar movie is wrong", result);
    }
    
    @Test
    void invalidUsername() throws Exception {

        Files.writeString(moviesFile,
                "Avatar,A123\nAction\n");

        // truly invalid username
        Files.writeString(usersFile,
                "john2123,12345678A\nAction\n");

        App.main(new String[]{
                moviesFile.toString(),
                usersFile.toString(),
                outputFile.toString()
        });

        String result = Files.readString(outputFile).trim();

        assertEquals(
                "Username ERROR: john2123 is wrong",
                result
        );
    }
}
    
