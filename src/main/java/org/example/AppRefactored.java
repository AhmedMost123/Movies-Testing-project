package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Refactored main application class
 * Uses dependency injection and service layer for better testability
 */
public class AppRefactored {
    
    private final FileManager fileManager;
    private final MovieService movieService;
    private final UserService userService;
    private final RecommendationService recommendationService;
    
    // Default constructor for production use
    public AppRefactored() {
        this(new FileManager(), new MovieService(), new UserService(), 
             new RecommendationService(new MovieService(), new UserService()));
    }
    
    // Constructor for dependency injection (testing)
    public AppRefactored(FileManager fileManager, MovieService movieService, 
                        UserService userService, RecommendationService recommendationService) {
        this.fileManager = fileManager;
        this.movieService = movieService;
        this.userService = userService;
        this.recommendationService = recommendationService;
    }
    
    /**
     * Main entry point
     * args[0] => movies file
     * args[1] => users file
     * args[2] => optional output file
     */
    public static void main(String[] args) throws Exception {
        AppRefactored app = new AppRefactored();
        app.run(args);
    }
    
    /**
     * Runs the application with given arguments
     */
    public void run(String[] args) throws Exception {
        // Validate arguments
        if (args.length < 2) {
            printUsageMessage();
            return;
        }
        
        String outputFile = args.length > 2 ? args[2] : "output.txt";
        String usersFile = args[1];
        String moviesFile = args[0];
        
        // Read input files
        List<Map<String, Object>> usersData = fileManager.readFile(usersFile);
        List<Map<String, Object>> moviesData = fileManager.readFile(moviesFile);
        
        if (usersData == null || moviesData == null) {
            System.out.println("unable to access file");
            return;
        }
        
        // Process movies
        ProcessingResult movieResult = processMovies(moviesData);
        if (!movieResult.isSuccess()) {
            fileManager.writeFile(outputFile, movieResult.getErrorMessage());
            return;
        }
        
        // Process users
        ProcessingResult userResult = processUsers(usersData);
        if (!userResult.isSuccess()) {
            fileManager.writeFile(outputFile, userResult.getErrorMessage());
            return;
        }
        
        // Generate and write recommendations
        String recommendations = generateRecommendations(userResult.getUsers());
        fileManager.writeFile(outputFile, recommendations);
    }
    
    /**
     * Processes all movies from file data
     */
    private ProcessingResult processMovies(List<Map<String, Object>> moviesData) {
        List<Movie> processedMovies = new ArrayList<>();
        
        for (Map<String, Object> movieData : moviesData) {
            String id = (String) movieData.get("id");
            String title = (String) movieData.get("label");
            @SuppressWarnings("unchecked")
            List<String> category = (List<String>) movieData.get("category");
            
            Movie movie = new Movie(title, id, category);
            MovieService.ValidationResult result = movieService.saveMovie(movie);
            
            if (!result.isSuccess()) {
                return ProcessingResult.error(result.getErrorMessage());
            }
            
            processedMovies.add(movie);
        }
        
        return ProcessingResult.success(processedMovies);
    }
    
    /**
     * Processes all users from file data
     */
    private ProcessingResult processUsers(List<Map<String, Object>> usersData) {
        List<User> processedUsers = new ArrayList<>();
        
        for (Map<String, Object> userData : usersData) {
            String id = (String) userData.get("id");
            String name = (String) userData.get("label");
            @SuppressWarnings("unchecked")
            List<String> likedCategories = (List<String>) userData.get("category");
            
            User user = new User(name, id, likedCategories);
            UserService.ValidationResult result = userService.saveUser(user);
            
            if (!result.isSuccess()) {
                return ProcessingResult.error(result.getErrorMessage());
            }
            
            processedUsers.add(user);
        }
        
        return ProcessingResult.success(processedUsers);
    }
    
    /**
     * Generates recommendations for all users
     */
    private String generateRecommendations(List<User> users) {
        List<RecommendationService.RecommendationResult> results = 
            recommendationService.generateRecommendations(users);
        return recommendationService.formatRecommendations(results);
    }
    
    /**
     * Prints usage message to console
     */
    private void printUsageMessage() {
        System.out.println("wrong launching arguments, make sure the following arguments are passed in order:");
        System.out.println("<movies_file.txt> <users_file.txt> [optional: <output_file.txt>]");
        try {
            // Wait for user input before continuing (original behavior)
            new java.util.Scanner(System.in).nextLine();
        } catch (Exception e) {
            // Ignore scanner exceptions
        }
    }
    
    /**
     * Result of processing operation
     */
    private static class ProcessingResult {
        private final boolean success;
        private final String errorMessage;
        private final List<?> processedItems;
        
        private ProcessingResult(boolean success, String errorMessage, List<?> processedItems) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.processedItems = processedItems;
        }
        
        public static ProcessingResult success(List<?> items) {
            return new ProcessingResult(true, null, items);
        }
        
        public static ProcessingResult error(String message) {
            return new ProcessingResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @SuppressWarnings("unchecked")
        public List<User> getUsers() {
            return success ? (List<User>) processedItems : null;
        }
    }
}
