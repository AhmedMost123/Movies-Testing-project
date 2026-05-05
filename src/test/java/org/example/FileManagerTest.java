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
import org.junit.jupiter.api.Test;


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
    // BLACK BOX TESTING-----------------------------------------
    @Test
    void blackBox_validInputStructure() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "The Matrix, TM123\nAction,SciFi");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals("The Matrix", data.get(0).get("label"));
        assertEquals("TM123", data.get(0).get("id"));
    }
    @Test
    void blackBox_invalidFormatMissingComma() {
        assertThrows(Exception.class, () -> {
            Files.writeString(Path.of(TEST_FILE),
                    "The Matrix TM123\nAction,SciFi");

            FileManager.readFile(TEST_FILE);
        });
    }
    @Test
    void blackBox_missingCategoryLine() {
        assertThrows(Exception.class, () -> {
            Files.writeString(Path.of(TEST_FILE),
                    "The Matrix, TM123\n");

            FileManager.readFile(TEST_FILE);
        });
    }
    @Test
    void blackBox_inputWithExtraSpaces() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "  Inception  ,  IN456  \n  Action , SciFi ");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals("Inception", data.get(0).get("label"));
        assertEquals("IN456", data.get(0).get("id"));
    }
    @Test
    void blackBox_emptyFileBehavior() throws Exception {
        Files.writeString(Path.of(TEST_FILE), "");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertTrue(data.isEmpty());
    }
    @Test
    void blackBox_multipleEntries() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "Movie1, M1123\nAction\n" +
                "Movie2, M2456\nDrama");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals(2, data.size());
    }
    @Test
    void blackBox_writeFileOutputCheck() throws Exception {
        String content = "Hello Output";

        FileManager.writeFile(OUTPUT_FILE, content);

        String result = Files.readString(Path.of(OUTPUT_FILE));

        assertEquals(content, result);
    }
    @Test
    void blackBox_invalidFilePath() {
        assertThrows(Exception.class, () -> {
            FileManager.readFile("non_existing_file.txt");
        });
    }
    @Test
    void blackBox_emptyCategoryLine() {
        Exception ex = assertThrows(Exception.class, () -> {
            Files.writeString(Path.of(TEST_FILE),
                    "Movie, ID\n"); // no category line

            FileManager.readFile(TEST_FILE);
        });

        assertTrue(ex.getMessage().contains("Missing second line"));
    }
    //WHITEBOX TESTING-----------------------------------------
    @Test
    void whiteBox_multipleEntriesProcessing() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "Movie One, MO123\nAction\n" +
                "Movie Two, MT456\nDrama");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        assertEquals(2, data.size());
    }
    @Test
    void whiteBox_missingSecondLineTriggersException() {
        Exception ex = assertThrows(Exception.class, () -> {
            Files.writeString(Path.of(TEST_FILE),
                    "Movie Only, MO123\n"); // missing second line
            FileManager.readFile(TEST_FILE);
        });

        assertTrue(ex.getMessage().contains("Missing second line"));
    }
    @Test
    void whiteBox_invalidCommaSplitBranch() {
        Exception ex = assertThrows(Exception.class, () -> {
            Files.writeString(Path.of(TEST_FILE),
                    "InvalidFormatLineWithoutComma\nAction");
            FileManager.readFile(TEST_FILE);
        });

        assertTrue(ex.getMessage().contains("Line format error"));
    }
    @Test
    void whiteBox_categoryLoopExecution() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "Inception, IN123\nAction,SciFi,Thriller");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        Map<String, Object> entry = data.get(0);
        List<String> categories = (List<String>) entry.get("category");

        assertEquals(3, categories.size());
    }
    @Test
    void whiteBox_fileNotFoundExceptionPath() {
        Exception ex = assertThrows(Exception.class, () -> {
            FileManager.readFile("invalid_path_file.txt");
        });

        assertTrue(ex.getMessage().contains("Error reading file"));
    }
    @Test
    void whiteBox_writeFileSuccessPath() {
        assertDoesNotThrow(() -> {
            FileManager.writeFile(OUTPUT_FILE, "Test Content");
            assertTrue(Files.exists(Path.of(OUTPUT_FILE)));
        });
    }
    @Test
    void whiteBox_writeFileExceptionPath() {
        Exception ex = assertThrows(Exception.class, () -> {
            FileManager.writeFile("/invalid_path/output.txt", "data");
        });

        assertTrue(ex.getMessage().contains("Error writing file"));
    }
    @Test
    void whiteBox_singleCategoryLoopBoundary() throws Exception {
        Files.writeString(Path.of(TEST_FILE),
                "MovieA, MA123\nAction");

        List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);

        List<String> categories =
                (List<String>) data.get(0).get("category");

        assertEquals(1, categories.size());
    }
    @Test
    void whiteBox_fullExceptionMessageCheck() {
        Exception ex = assertThrows(Exception.class, () -> {
            FileManager.readFile("fake_file_123.txt");
        });

        assertTrue(ex.getMessage().contains("Error reading file"));
        assertTrue(ex.getMessage().contains("fake_file_123.txt"));
    }
    @Test
    void whiteBox_writeFilePathValidation() {
        Exception ex = assertThrows(Exception.class, () -> {
            FileManager.writeFile("/invalid////path.txt", "data");
        });

        assertTrue(ex.getMessage().contains("Error writing file"));
    }
    
}