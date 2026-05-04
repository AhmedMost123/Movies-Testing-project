package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class FileManager 
{
     
    public static List<Map<String, Object>> readFile(String path) throws Exception 
    {
        //file reader for movies.txt or users.txt and it returns a List of Maps: each map has keys which are label, id, category
        List<Map<String, Object>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) 
        {
            String line1, line2;

            while ((line1 = reader.readLine()) != null) 
            {
                line2 = reader.readLine(); //line 2 is categories line
                if (line2 == null) 
                {
                    throw new Exception("Missing second line for entry: " + line1);
                }
                //split line 1 at the comma
                String[] parts = line1.split(",");
                if (parts.length != 2) 
                {
                    throw new Exception("Line format error: " + line1);
                }
                //now we extract label and id
                String label = parts[0].trim();
                String id = parts[1].trim();

                //split line 2 whcih is for categories
                List<String> categories = new ArrayList<>();
                for (String c : line2.split(",")) 
                {
                    categories.add(c.trim());
                }

                //place in map
                Map<String, Object> item = new HashMap<>();
                item.put("label", label);
                item.put("id", id);
                item.put("category", categories);
                data.add(item);
            }
        } 
        catch (Exception e) 
        {
            return null;
        }

        return data;
    }

   
    public static void writeFile(String path, String content) throws Exception 
    {
    	//fn to write content to output file
        try (FileWriter writer = new FileWriter(path)) 
        {
            writer.write(content);
        } 
        catch (Exception e) 
        {
            throw new Exception("Error writing file: " + path + " - " + e.getMessage());
        }
    }
}