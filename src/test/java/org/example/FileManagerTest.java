package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileManagerTest 
{

    private static final String TEST_FILE = "test_input.txt";
    private static final String OUTPUT_FILE = "test_output.txt";

    @BeforeEach
    void setup() throws Exception 
    {
        Files.deleteIfExists(Path.of(TEST_FILE));
        Files.deleteIfExists(Path.of(OUTPUT_FILE));
    }

    @AfterEach
    void cleanup() throws Exception 
    {
        Files.deleteIfExists(Path.of(TEST_FILE));
        Files.deleteIfExists(Path.of(OUTPUT_FILE));
    }
    //UNIT TESTING----------------------------------
    //unit testing for movie file validation
    @Test
    void testValidMovieFileFormat() throws Exception 
    {
        Files.writeString(Path.of(TEST_FILE), "The Matrix, TM123\nAction,SciFi");
        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
        assertEquals(1, data.size());
        assertEquals("The Matrix", data.get(0).get("label"));
    }

    @Test
    void testMovieMissingCategoryLine() {
        assertThrows(Exception.class, () -> 
        {
            Files.writeString(Path.of(TEST_FILE),
                    "The Matrix, TM123\n");
            FileManager.readFile(TEST_FILE);
        });
    }

    @Test
    void testMovieWrongLineFormat_NoComma() {
        assertThrows(Exception.class, () -> 
        {
            Files.writeString(Path.of(TEST_FILE),
                    "The Matrix TM123\nAction,SciFi");
            FileManager.readFile(TEST_FILE);
        });
    }

    //unit tests for users file validation

    @Test
    void testValidUserFileFormat() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "John Doe, 12345678A\nAction,Drama");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals(1, data.size());
        assertEquals("John Doe", data.get(0).get("label"));
    }

    //testing boundary cases for files
    @Test
    void testEmptyFile() throws Exception {
        Files.writeString(Path.of(TEST_FILE), "");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertTrue(data.isEmpty());
    }

    @Test
    void testExtraSpacesHandled() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "  Inception  ,  IN456  \n  Action , SciFi ");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals("Inception", data.get(0).get("label"));
        assertEquals("IN456", data.get(0).get("id"));
    }

    //unit testing for output file
    @Test
    void testWriteRecommendationOutput() {
        assertDoesNotThrow(() -> 
        {
            FileManager.writeFile(OUTPUT_FILE,
                    "For User: John,12345678A\nAction: TM123-The Matrix");

            assertTrue(Files.exists(Path.of(OUTPUT_FILE)));
        });
    }

    //testing behaviour on errors
    @Test
    void testStopOnFirstErrorBehavior() {
        assertThrows(Exception.class, () -> 
        {
            Files.writeString(Path.of(TEST_FILE),
                    "BadMovieFormat TM123\nAction,SciFi");

            FileManager.readFile(TEST_FILE);
        });
    }
    @Test
    void unit_validMultipleCategoriesParsing() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "Inception, IN123\nAction,SciFi,Thriller");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals(3, ((List<?>) data.get(0).get("category")).size());
    }
    @Test
    void unit_trimValidation() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "  Movie X  ,  MX123  \nAction ");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals("Movie X", data.get(0).get("label"));
        assertEquals("MX123", data.get(0).get("id"));
    }
  
}