package org.example;

import java.util.List;
import java.util.Map;

public class FileManager {

    public static List<Map<String, Object>> readFile(String path) {
        /*
            if file couldnt be found or access rejected or any other issue occur, return null.
            returned value should be in the following format:
            [
            {
             "id": "<id1 value>",
             "label": "<label1 value>",
             "category": List(["<category1>", "<category2>", ....])
            },
            {
             "id": "<id2 value>",
             "label": "<label2 value>",
             "category": List(["<category1>", "<category2>", ....])
            },
            ....]
        */ 

        return null;
    }

    public static void writeFile(String path, String content) {

    }

}
