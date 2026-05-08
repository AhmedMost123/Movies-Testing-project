package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for user management
 * Handles user storage and recommendation operations
 */
public class UserService {
    
    private final Set<String> usedIds;
    private final ValidationService validationService;
    private MovieService movieService;
    
    public UserService() {
        this(new ValidationService());
    }
    
    public UserService(ValidationService validationService) {
        this.validationService = validationService;
        this.movieService = null; // Will be set later if needed
        this.usedIds = ConcurrentHashMap.newKeySet();
    }
    
    // Setter for MovieService to avoid circular dependency
    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }
    
    /**
     * Validates and saves a user
     * @param user user to save
     * @return ValidationResult indicating success or failure with error message
     */
    public ValidationResult saveUser(User user) {
        if (!validationService.isValidUsername(user.userName)) {
            return ValidationResult.error("Username ERROR: " + user.userName + " is wrong");
        }
        
        if (!validationService.isValidUserId(user.userID, usedIds)) {
            return ValidationResult.error("User Id ERROR: " + user.userID + " is wrong");
        }
        
        // All validations passed, save user
        usedIds.add(user.userID);
        return ValidationResult.success();
    }
    
    /**
     * Gets recommendations for a user
     * @param user user to get recommendations for
     * @return map of category to list of recommended movies
     */
    public Map<String, ArrayList<Movie>> getRecommendations(User user) {
        Map<String, ArrayList<Movie>> recommendations = new ConcurrentHashMap<>();
        Map<String, ArrayList<Movie>> allMovies;
        
        // Use movieService if available, otherwise fall back to static Movie.movies
        if (movieService != null) {
            allMovies = movieService.getAllMoviesByCategory();
        } else {
            allMovies = Movie.movies;
        }
        
        for (String category : user.likedCategories) {
            if (category == null || category.trim().isEmpty()) {
                continue;
            }
            String normalizedCategory = category.toLowerCase().trim();
            if (allMovies.containsKey(normalizedCategory)) {
                recommendations.put(normalizedCategory, allMovies.get(normalizedCategory));
            }
        }
        
        return recommendations;
    }
    
    /**
     * Validates user uniqueness among a list of users
     * @param user user to check
     * @param allUsers list of all users
     * @return true if user ID is unique, false otherwise
     */
    public boolean isUniqueUserId(User user, List<User> allUsers) {
        if (user == null || allUsers == null) {
            return false;
        }
        
        for (User checkedUser : allUsers) {
            if (checkedUser == user) {
                continue;
            }
            if (user.userID.equals(checkedUser.userID)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Clears all stored users (for testing purposes)
     */
    public void clearAllUsers() {
        usedIds.clear();
    }
    
    /**
     * Gets set of used user IDs
     * @return set of used IDs
     */
    public Set<String> getUsedIds() {
        return new java.util.HashSet<>(usedIds);
    }
    
    /**
     * Result of a validation operation
     */
    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        
        private ValidationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
