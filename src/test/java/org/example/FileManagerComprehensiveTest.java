package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class FileManagerComprehensiveTest {

    private static final String TEST_FILE = "test_input.txt";
    private static final String OUTPUT_FILE = "test_output.txt";

    @BeforeEach
    void setup() throws Exception {
        Files.deleteIfExists(Path.of(TEST_FILE));
        Files.deleteIfExists(Path.of(OUTPUT_FILE));
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(Path.of(TEST_FILE));
        Files.deleteIfExists(Path.of(OUTPUT_FILE));
    }

    // ─────────────────────────────────────────────
    // 1 - Positive Test Cases for readFile()
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("1 - Positive readFile() Tests")
    class PositiveReadFileTests {

        @Test
        @DisplayName("Valid single movie entry should be read correctly")
        void testValidSingleMovieEntry() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "The Matrix, TM123\nAction,SciFi");
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(1, data.size());
            assertEquals("The Matrix", data.get(0).get("label"));
            assertEquals("TM123", data.get(0).get("id"));
            
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) data.get(0).get("category");
            assertEquals(2, categories.size());
            assertTrue(categories.contains("Action"));
            assertTrue(categories.contains("SciFi"));
        }

        @Test
        @DisplayName("Valid single user entry should be read correctly")
        void testValidSingleUserEntry() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "John Doe, JD123456\nAction,Drama");
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(1, data.size());
            assertEquals("John Doe", data.get(0).get("label"));
            assertEquals("JD123456", data.get(0).get("id"));
            
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) data.get(0).get("category");
            assertEquals(2, categories.size());
            assertTrue(categories.contains("Action"));
            assertTrue(categories.contains("Drama"));
        }

        @Test
        @DisplayName("Multiple entries should be read correctly")
        void testMultipleEntries() throws Exception {
            String content = "Movie1, M123\nAction\nMovie2, M456\nDrama,Comedy";
            Files.writeString(Path.of(TEST_FILE), content);
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(2, data.size());
            assertEquals("Movie1", data.get(0).get("label"));
            assertEquals("Movie2", data.get(1).get("label"));
            
            @SuppressWarnings("unchecked")
            List<String> categories1 = (List<String>) data.get(0).get("category");
            assertEquals(1, categories1.size());
            
            @SuppressWarnings("unchecked")
            List<String> categories2 = (List<String>) data.get(1).get("category");
            assertEquals(2, categories2.size());
        }

        @Test
        @DisplayName("Entry with single category should be read correctly")
        void testSingleCategory() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "Single Movie, SM123\nAction");
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(1, data.size());
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) data.get(0).get("category");
            assertEquals(1, categories.size());
            assertEquals("Action", categories.get(0));
        }

        @Test
        @DisplayName("Entry with extra spaces should be trimmed correctly")
        void testTrimmingOfSpaces() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "  The Matrix  ,  TM123  \n  Action  ,  SciFi  ");
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(1, data.size());
            assertEquals("The Matrix", data.get(0).get("label"));
            assertEquals("TM123", data.get(0).get("id"));
            
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) data.get(0).get("category");
            assertEquals("Action", categories.get(0));
            assertEquals("SciFi", categories.get(1));
        }
    }

    // ─────────────────────────────────────────────
    // 2 - Negative Test Cases for readFile()
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("2 - Negative readFile() Tests")
    class NegativeReadFileTests {

        @Test
        @DisplayName("Non-existent file should throw exception")
        void testNonExistentFile() {
            assertThrows(Exception.class, () -> {
                FileManager.readFile("non_existent_file.txt");
            });
        }

        @Test
        @DisplayName("File with missing category line should throw exception")
        void testMissingCategoryLine() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "Movie Title, M123\nMovie2, M456");
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }

        @Test
        @DisplayName("File with malformed first line should throw exception")
        void testMalformedFirstLine() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "Movie Title M123\nAction");
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }

        @Test
        @DisplayName("File with empty first line should throw exception")
        void testEmptyFirstLine() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "\nAction");
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }

        @Test
        @DisplayName("File with too many commas in first line should throw exception")
        void testTooManyCommas() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "Movie, Title, M123\nAction");
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }

        @Test
        @DisplayName("Empty file should return empty list")
        void testEmptyFile() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "");
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(0, data.size());
        }
    }

    // ─────────────────────────────────────────────
    // 3 - writeFile() Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("3 - writeFile() Tests")
    class WriteFileTests {

        @Test
        @DisplayName("Writing simple content should work correctly")
        void testSimpleWrite() throws Exception {
            String content = "Hello World";
            FileManager.writeFile(OUTPUT_FILE, content);
            
            String readContent = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals(content, readContent);
        }

        @Test
        @DisplayName("Writing multiline content should work correctly")
        void testMultilineWrite() throws Exception {
            String content = "Line 1\nLine 2\nLine 3";
            FileManager.writeFile(OUTPUT_FILE, content);
            
            String readContent = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals(content, readContent);
        }

        @Test
        @DisplayName("Writing empty content should work correctly")
        void testEmptyWrite() throws Exception {
            String content = "";
            FileManager.writeFile(OUTPUT_FILE, content);
            
            String readContent = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals(content, readContent);
        }

        @Test
        @DisplayName("Writing to invalid path should throw exception")
        void testWriteToInvalidPath() {
            String invalidPath = "/invalid/path/file.txt";
            String content = "Test content";
            
            assertThrows(Exception.class, () -> FileManager.writeFile(invalidPath, content));
        }

        @Test
        @DisplayName("Overwriting existing file should work correctly")
        void testOverwriteFile() throws Exception {
            // Write initial content
            FileManager.writeFile(OUTPUT_FILE, "Original content");
            String firstRead = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals("Original content", firstRead);
            
            // Overwrite with new content
            FileManager.writeFile(OUTPUT_FILE, "New content");
            String secondRead = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals("New content", secondRead);
        }
    }

    // ─────────────────────────────────────────────
    // 4 - Parameterized Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("4 - Parameterized Tests")
    class ParameterizedTests {

        static Stream<Arguments> validFileContentProvider() {
            return Stream.of(
                Arguments.of("Movie, M123\nAction", 1, "Movie"),
                Arguments.of("Movie1, M123\nAction\nMovie2, M456\nDrama", 2, "Movie1"),
                Arguments.of("User, U123\nCategory1,Category2,Category3", 1, "User"),
                Arguments.of("A, A123\nB", 1, "A")
            );
        }

        @ParameterizedTest
        @MethodSource("validFileContentProvider")
        @DisplayName("Valid file content should be parsed correctly")
        void testValidFileContent(String content, int expectedSize, String firstLabel) throws Exception {
            Files.writeString(Path.of(TEST_FILE), content);
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            
            assertEquals(expectedSize, data.size());
            if (expectedSize > 0) {
                assertEquals(firstLabel, data.get(0).get("label"));
            }
        }

        static Stream<String> invalidFileContentProvider() {
            return Stream.of(
                "Movie M123\nAction", // Missing comma
                "Movie, M123, Extra\nAction", // Too many commas
                "Movie\nAction", // Missing ID
                ", M123\nAction", // Missing label
                "Movie, \nAction", // Empty ID
                "Movie, M123", // Missing category line
                "Movie, M123\n" // Empty category line
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "Movie M123\nAction", // Missing comma
            "Movie, M123, Extra\nAction", // Too many commas
            "Movie\nAction", // Missing ID
            ", M123\nAction", // Missing label
            "Movie, \nAction", // Empty ID
            "Movie, M123", // Missing category line
            "Movie, M123\n" // Empty category line
        })
        @DisplayName("Invalid file content should throw exception")
        void testInvalidFileContent(String content) throws Exception {
            Files.writeString(Path.of(TEST_FILE), content);
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }
    }

    // ─────────────────────────────────────────────
    // 5 - Edge Case Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("5 - Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("File with very long lines should be handled correctly")
        void testVeryLongLines() throws Exception {
            String longTitle = "A".repeat(1000);
            String longId = "B".repeat(100);
            String longCategories = String.join(",", "C".repeat(100), "D".repeat(100), "E".repeat(100));
            
            String content = longTitle + ", " + longId + "\n" + longCategories;
            Files.writeString(Path.of(TEST_FILE), content);
            
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(1, data.size());
            assertEquals(longTitle, data.get(0).get("label"));
            assertEquals(longId, data.get(0).get("id"));
        }

        @Test
        @DisplayName("File with special characters should be handled correctly")
        void testSpecialCharacters() throws Exception {
            String content = "Movie: With Special! Characters@#$%, MSC123\nAction, Drama, Comedy";
            Files.writeString(Path.of(TEST_FILE), content);
            
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(1, data.size());
            assertEquals("Movie: With Special! Characters@#$%", data.get(0).get("label"));
            assertEquals("MSC123", data.get(0).get("id"));
        }

        @Test
        @DisplayName("File with unicode characters should be handled correctly")
        void testUnicodeCharacters() throws Exception {
            String content = "电影名称, MOV123\n动作, 剧情";
            Files.writeString(Path.of(TEST_FILE), content);
            
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(1, data.size());
            assertEquals("电影名称", data.get(0).get("label"));
            assertEquals("MOV123", data.get(0).get("id"));
        }

        @Test
        @DisplayName("File with only whitespace should throw exception")
        void testOnlyWhitespace() throws Exception {
            Files.writeString(Path.of(TEST_FILE), "   \n   \n   ");
            assertThrows(Exception.class, () -> FileManager.readFile(TEST_FILE));
        }

        @Test
        @DisplayName("File with empty category entries should handle them correctly")
        void testEmptyCategoryEntries() throws Exception {
            String content = "Movie, M123\nAction,Drama";
            Files.writeString(Path.of(TEST_FILE), content);
            
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(1, data.size());
            
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) data.get(0).get("category");
            assertEquals(2, categories.size());
            assertEquals("Action", categories.get(0));
            assertEquals("Drama", categories.get(1));
        }
    }

    // ─────────────────────────────────────────────
    // 6 - Integration-like Tests
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("6 - Integration-like Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Read and write cycle should preserve content")
        void testReadWriteCycle() throws Exception {
            String originalContent = "Movie1, M123\nAction,Drama\nMovie2, M456\nComedy";
            Files.writeString(Path.of(TEST_FILE), originalContent);
            
            // Read the file
            List<Map<String, Object>> data = FileManager.readFile(TEST_FILE);
            assertEquals(2, data.size());
            
            // Create output content based on read data
            StringBuilder outputContent = new StringBuilder();
            for (Map<String, Object> entry : data) {
                outputContent.append(entry.get("label")).append(",").append(entry.get("id")).append("\n");
                @SuppressWarnings("unchecked")
                List<String> categories = (List<String>) entry.get("category");
                outputContent.append(String.join(",", categories)).append("\n");
            }
            
            // Write to output file
            FileManager.writeFile(OUTPUT_FILE, outputContent.toString());
            
            // Verify written content
            String writtenContent = Files.readString(Path.of(OUTPUT_FILE));
            assertTrue(writtenContent.contains("Movie1,M123"));
            assertTrue(writtenContent.contains("Action,Drama"));
            assertTrue(writtenContent.contains("Movie2,M456"));
            assertTrue(writtenContent.contains("Comedy"));
        }

        @Test
        @DisplayName("Error message writing should work correctly")
        void testErrorMessageWriting() throws Exception {
            String errorMessage = "Movie Title ERROR: Invalid Title is wrong";
            FileManager.writeFile(OUTPUT_FILE, errorMessage);
            
            String readContent = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals(errorMessage, readContent);
        }

        @Test
        @DisplayName("Recommendation output writing should work correctly")
        void testRecommendationOutputWriting() throws Exception {
            String recommendationOutput = "For User: John Doe,JD123456\n" +
                                         "action: AM123-Action Movie,DM456-Drama Movie\n" +
                                         "drama: DM456-Drama Movie";
            FileManager.writeFile(OUTPUT_FILE, recommendationOutput);
            
            String readContent = Files.readString(Path.of(OUTPUT_FILE));
            assertEquals(recommendationOutput, readContent);
        }
    }

}
