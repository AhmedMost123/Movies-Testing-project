package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    public static List<Map<String, Object>> readFile(String path) 
    {

        List<Map<String, Object>> data = new ArrayList<>();
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line1;
            String line2;

            //each item consists of two lines
            while ((line1 = reader.readLine()) != null) 
            {

                line2 = reader.readLine(); // categories line
                if (line2 == null) 
                {
                    break;
                }

                //split first line at the comma (label,id)
                String[] firstParts = line1.split(",");
                String label = firstParts[0].trim();
                String id = firstParts[1].trim();

                //split categories at the comma
                String[] cats = line2.split(",");
                List<String> categories = new ArrayList<>();

                for (String c : cats) 
                {
                    categories.add(c.trim());
                }

                // create map
                Map<String, Object> item = new HashMap<>();
                item.put("label", label);
                item.put("id", id);
                item.put("category", categories);
                data.add(item);
            }

            reader.close();
            return data;

        } 
        catch (Exception e) 
        {
            return null;
        }
    }

    public static void writeFile(String path, String content) {

        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing file.");
        }

    }
}