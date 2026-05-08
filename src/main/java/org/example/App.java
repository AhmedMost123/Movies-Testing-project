package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {

    /*
    args[0] => movies file
    args[1] => users file
    args[2] => optional output file
    */
    public static void main(String[] args) {
        
        if(args.length < 2) {

            System.out.println("wrong launching arguments, make sure the following arguments are passed in order:");    
            System.out.println("<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]");
            (new Scanner(System.in)).nextLine();
            return;
        }

        String outputFile = args.length > 2 ? args[2] : "output.txt",
        usersFile = args[1],
        moviesFile = args[0];

        // Initialize services
        MovieService movieService = new MovieService();
        UserService userService = new UserService();
        userService.setMovieService(movieService);

        List<Map<String, Object>> usersData, moviesData;
        try {
            usersData = FileManager.readFile(usersFile);
            moviesData = FileManager.readFile(moviesFile);
        } catch (Exception e) {
            System.out.println("unable to access file"); 
            return;
        }

        // Process movies using service layer
        for(Map<String, Object> movieData : moviesData) {
            String id = (String) movieData.get("id"),
            title = (String) movieData.get("label");
            List<String> category = (List<String>) movieData.get("category");

            Movie movie = new Movie(title, id, category);
            MovieService.ValidationResult result = movieService.saveMovie(movie);
            
            if (!result.isSuccess()) {
                try {
                    FileManager.writeFile(outputFile, result.getErrorMessage());
                } catch (Exception e) {
                    System.out.println("Error writing to output file: " + e.getMessage());
                }
                return;
            }
        }

        ArrayList<User> users = new ArrayList();

        // Process users using service layer
        for(Map<String, Object> userData : usersData) {
            String id = (String) userData.get("id"),
            name = (String) userData.get("label");
            List<String> likedCategories = (List<String>) userData.get("category");

            User user = new User(name, id, likedCategories);
            
            UserService.ValidationResult result = userService.saveUser(user);
            if (!result.isSuccess()) {
                try {
                    FileManager.writeFile(outputFile, result.getErrorMessage());
                } catch (Exception e) {
                    System.out.println("Error writing to output file: " + e.getMessage());
                }
                return;
            }

            users.add(user);
        }
    
        StringBuffer buffer = new StringBuffer();
        for(User user : users) {

            Map<String, ArrayList<Movie>> suggestions = userService.getRecommendations(user);
            buffer.append(String.format("For User: %s,%s\n", user.userName, user.userID));

            for(Map.Entry<String, ArrayList<Movie>> suggestion : suggestions.entrySet()) {
                buffer.append(String.format("%s: %s\n", suggestion.getKey(), 
                String.join(",", suggestion.getValue().stream().map(Movie::toString).toList())));
            }
        }

        try {
            FileManager.writeFile(outputFile, buffer.toString());
        } catch (Exception e) {
            System.out.println("Error writing recommendations to output file: " + e.getMessage());
        }
    }

}

