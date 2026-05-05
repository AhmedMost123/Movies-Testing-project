package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {

    /*
    args[0] => movies file
    args[1] => users file
    */
    public static void main(String[] args) throws Exception{
        
        if(args.length < 2) {

            System.out.println("wrong launching arguments, make sure the following arguments are passed in order:");    
            System.out.println("<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]");
            (new Scanner(System.in)).nextLine();
            return;
        }

        String outputFile = args.length > 2 ? args[2] : "output.txt",
        usersFile = args[1],
        moviesFile = args[0];

        List<Map<String, Object>> usersData = FileManager.readFile(usersFile),
        moviesData = FileManager.readFile(moviesFile);

        if(usersData == null || moviesData == null) {
            System.out.println("unable to access file"); 
            
            
            return;
        }

        for(Map<String, Object> movieData : moviesData) {
            String id = (String) movieData.get("id"),
            title = (String) movieData.get("label");
            List<String> category = (List<String>) movieData.get("category");

            Movie movie = new Movie(title, id, category);
            if(!movie.isValidMovieTitle()) {
                FileManager.writeFile(outputFile, "Movie Title ERROR: "+title+" is wrong");
                return;
            }

            if(!movie.isValidMovieID()) {
                FileManager.writeFile(outputFile, "Movie Id letters ERROR: "+id+" are wrong");
                return;
            }

            if(!movie.isUniqueMovieID()) {
                FileManager.writeFile(outputFile, "Movie Id numbers ERROR: "+id+" aren't unique");
                return;
            }
            movie.save();
        }

        ArrayList<User> users = new ArrayList();

        for(Map<String, Object> userData : usersData) {
            String id = (String) userData.get("id"),
            name = (String) userData.get("label");
            List<String> likedCategories = (List<String>) userData.get("category");

            User user = new User(name, id, likedCategories);
            
            if(!user.isValidUserName()) {
                FileManager.writeFile(outputFile, "Username ERROR: "+name+" is wrong");
                return;
            }

            if(!user.isValidUserID()) {
                FileManager.writeFile(outputFile, "User Id ERROR: "+id+" is wrong");
                return;
            }

            user.save();
            users.add(user);
        }
    
        StringBuffer buffer = new StringBuffer();
        for(User user : users) {

            Map<String, ArrayList<Movie>> suggestions = user.getRecommendations();
            buffer.append(String.format("For User: %s,%s\n", user.userName, user.userID));

            for(Map.Entry<String, ArrayList<Movie>> suggestion : suggestions.entrySet()) {
                buffer.append(String.format("%s: %s\n", suggestion.getKey(), 
                String.join(",", suggestion.getValue().stream().map(Movie::toString).toList())));
            }
        }

        FileManager.writeFile(outputFile, buffer.toString());
    }

}

