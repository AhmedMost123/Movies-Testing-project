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

//Data Flow Testing for FileManager.java
/*
DFT Techniques Used:

- Definition-Use Paths
- All Definitions Coverage
- All Uses Coverage
- File Data Propagation Testing

Variables Tested:

- line1
- line2
- parts
- label
- id
- categories
- item
- content

*/

class FileManagerDataFlowTest {

    @TempDir
    Path tempDir;

    @Test
    void testing_read_file_data_flow() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123",
                "romance"
        ));

        List<Map<String,Object>> result =
                FileManager.readFile(file.toString());

        assertEquals("Titanic",
                result.get(0).get("label"));
    }

    @Test
    void testing_multiple_entries_data_flow() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123",
                "romance",
                "Avengers,A123",
                "action"
        ));

        List<Map<String,Object>> result =
                FileManager.readFile(file.toString());

        assertEquals(2,result.size());
    }

    @Test
    void testing_invalid_format_data_flow() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic-T123",
                "romance"
        ));

        List<Map<String,Object>> result =
                FileManager.readFile(file.toString());

        assertNull(result);
    }

    @Test
    void testing_missing_second_line_data_flow() throws Exception {

        Path file = tempDir.resolve("movies.txt");

        Files.write(file,List.of(
                "Titanic,T123"
        ));

        List<Map<String,Object>> result =
                FileManager.readFile(file.toString());

        assertNull(result);
    }

    @Test
    void testing_write_file_definition_to_use() throws Exception {

        Path output = tempDir.resolve("output.txt");

        FileManager.writeFile(
                output.toString(),
                "HELLO"
        );

        String content = Files.readString(output);

        assertEquals("HELLO",content);
    }

    @Test
    void testing_empty_content_data_flow() throws Exception {

        Path output = tempDir.resolve("output.txt");

        FileManager.writeFile(
                output.toString(),
                ""
        );

        String content = Files.readString(output);

        assertTrue(content.isEmpty());
    }
}