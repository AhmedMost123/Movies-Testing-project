package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileManagerTest {

    @TempDir
    Path tempDir;

    //black box testing for methods: readFile and writeFile
    
    
    // now we start with readFile() method
    @Test
    void testing_valid_inputs() throws Exception {
        // valid input file and should return parsed data correctly

        Path file = tempDir.resolve("movies.txt");
        Files.write(file, List.of("Titanic,T123","romance,drama"));
        List<Map<String, Object>> result = FileManager.readFile(file.toString());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Titanic",result.get(0).get("label"));
        assertEquals("T123",result.get(0).get("id"));
    }

    @Test
    void testing_empty_file() throws Exception {
        // Empty file boundary case
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testing_missing_category_line() throws Exception {
        Path file = tempDir.resolve("missingLine.txt");
        Files.write(file, List.of("Titanic,T123"));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNull(result);
    }

    @Test
    void testing_missing_comma_1st_line() throws Exception {
        // Invalid first line format (missing comma)
        Path file = tempDir.resolve("invalidFormat.txt");
        Files.write(file, List.of("TitanicT123","romance"));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNull(result);
    }

    @Test
    void testing_non_existing_file() throws Exception {
        // Non-existing file
        Path missing =tempDir.resolve("missing.txt");
        List<Map<String, Object>> result =FileManager.readFile(missing.toString());
        assertNull(result);
    }

    @Test
    void testing_passing_dir() throws Exception {
        // passing directory instead of file
        Path dir = tempDir.resolve("folder");
        Files.createDirectory(dir);
        List<Map<String, Object>> result =FileManager.readFile(dir.toString());
        assertNull(result);
    }

    @Test
    void testing_file_with_extra_spaces() throws Exception {
        // File with extra spaces
        Path file = tempDir.resolve("spaces.txt");
        Files.write(file, List.of("  Titanic  ,  T123  ","  romance  , drama  "));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNotNull(result);
        assertEquals("Titanic",result.get(0).get("label"));
        assertEquals("T123",result.get(0).get("id"));
    }

    @Test
    void testing_multiple_movie_entries() throws Exception {
        // Multiple movie entries
        Path file = tempDir.resolve("multiple.txt");

        Files.write(file, List.of(
                "Titanic,T123",
                "romance,drama",
                "Avengers,A321",
                "action,thriller"
        ));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testing_single_category() throws Exception {
        // Single category boundary case
        Path file = tempDir.resolve("singleCategory.txt");
        Files.write(file, List.of("Titanic,T123","romance"));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        List<String> categories =(List<String>) result.get(0).get("category");
        assertEquals(1, categories.size());
        assertEquals("romance",categories.get(0));
    }

    @Test
    void testing_null_path() throws Exception {
        // Null path
        List<Map<String, Object>> result =FileManager.readFile(null);
        assertNull(result);
    }

    @Test
    void testing_extra_commas_1st_line() throws Exception {
        // Extra commas in first line
        Path file = tempDir.resolve("extraComma.txt");
        Files.write(file, List.of("Titanic,T123,EXTRA","romance"));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNull(result);
    }

    @Test
    void testing_minimal_valid_input() throws Exception {
        // Minimal valid input boundary case
        Path file = tempDir.resolve("minimal.txt");
        Files.write(file, List.of("A,A123","action"));
        List<Map<String, Object>> result =FileManager.readFile(file.toString());
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // now for writeFile() method
    @Test
    void testing_valid_writing_operation() throws Exception {
        // Valid writing operation
        Path file = tempDir.resolve("output.txt");
        FileManager.writeFile(file.toString(),"Hello World");
        String content =Files.readString(file);
        assertEquals("Hello World",content);
    }
    @Test
    void testing_empty_content() throws Exception {
        // Empty content boundary case
        Path file = tempDir.resolve("emptyContent.txt");
        FileManager.writeFile(file.toString(),"");
        String content =Files.readString(file);
        assertEquals("", content);
    }

    @Test
    void testing_invalid_path() {
        // Invalid path
        assertThrows(Exception.class, () -> {FileManager.writeFile("Z:/invalid/path/output.txt","data");
        });
    }

    @Test
    void testing_file_overwriting() throws Exception {
        // Overwrite existing file
        Path file = tempDir.resolve("overwrite.txt");
        Files.writeString(file, "OLD DATA");
        FileManager.writeFile(file.toString(),"NEW DATA");
        String content =Files.readString(file);
        assertEquals("NEW DATA",content);
    }

    @Test
    void testing_null_path_writing() {
        // Null path writing
        assertThrows(Exception.class, () -> {
            FileManager.writeFile(
                    null,
                    "content"
            );
        });
    }
    @Test
    void testing_null_file_content_writing() {
        // Null content writing
        assertThrows(Exception.class, () -> {
            Path file =
                    tempDir.resolve("null.txt");

            FileManager.writeFile(
                    file.toString(),
                    null
            );
        });
    }

    @Test
    void testing_large_file_contents() throws Exception {
        // Large content writing
        Path file = tempDir.resolve("large.txt");
        String largeText ="A".repeat(10000);
        FileManager.writeFile(file.toString(),largeText);
        String content = Files.readString(file);
        assertEquals(10000,content.length());
    }

    @Test
    void testing_writing_to_dir() {
        // Writing into directory instead of file
        assertThrows(Exception.class, () -> {
            Path dir =
                    tempDir.resolve("folder");

            Files.createDirectory(dir);

            FileManager.writeFile(
                    dir.toString(),
                    "data"
            );

        });
    }
}