package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//White Box Testing for FileManager.java
/*
White Box Techniques Used:

- Statement Coverage
- Branch Coverage
- Exception Path Testing
- Loop Testing
- File Handling Testing

Methods Tested:

- readFile()
- writeFile()

*/

class FileManagerWhiteBoxTest {

    @TempDir
    Path tempDir;

    @Test
    void testing_read_file_true_branch() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123",
                "romance"
        ));

        List<Map<String,Object>> result = FileManager.readFile(file.toString());

        assertEquals(1,result.size());
    }

    @Test
    void testing_read_file_multiple_entries() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123",
                "romance",
                "Avengers,A123",
                "action"
        ));

        List<Map<String,Object>> result = FileManager.readFile(file.toString());

        assertEquals(2,result.size());
    }

    @Test
    void testing_read_file_missing_second_line() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123"
        ));

        List<Map<String,Object>> result = FileManager.readFile(file.toString());

        assertNull(result);
    }

    @Test
    void testing_read_file_invalid_format() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic-T123",
                "romance"
        ));

        List<Map<String,Object>> result = FileManager.readFile(file.toString());

        assertNull(result);
    }

    @Test
    void testing_read_file_non_existing_path() throws Exception {

        List<Map<String,Object>> result = FileManager.readFile("wrong.txt");

        assertNull(result);
    }

    @Test
    void testing_write_file_true_branch() throws Exception {

        Path output = tempDir.resolve("output.txt");

        FileManager.writeFile(output.toString(),"HELLO");

        String content = Files.readString(output);

        assertEquals("HELLO",content);
    }

    @Test
    void testing_write_file_overwrite_branch() throws Exception {

        Path output = tempDir.resolve("output.txt");

        FileManager.writeFile(output.toString(),"OLD");
        FileManager.writeFile(output.toString(),"NEW");

        String content = Files.readString(output);

        assertEquals("NEW",content);
    }

    @Test
    void testing_write_file_empty_content() throws Exception {

        Path output = tempDir.resolve("output.txt");

        FileManager.writeFile(output.toString(),"");

        String content = Files.readString(output);

        assertTrue(content.isEmpty());
    }
}