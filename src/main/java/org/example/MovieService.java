package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for movie management
 * Handles movie storage and retrieval operations
 */
public class MovieService {
    
    private final Map<String, ArrayList<Movie>> moviesByCategory;
    private final Set<String> usedIds;
    private final ValidationService validationService;
    
    public MovieService() {
        this(new ValidationService());
    }
    
    public MovieService(ValidationService validationService) {
        this.validationService = validationService;
        this.moviesByCategory = new ConcurrentHashMap<>();
        this.usedIds = ConcurrentHashMap.newKeySet();
    }
    
    /**
     * Validates and saves a movie
     * @param movie the movie to save
     * @return ValidationResult indicating success or failure with error message
     */
    public ValidationResult saveMovie(Movie movie) {
        if (!validationService.isValidMovieTitle(movie.movieTitle)) {
            return ValidationResult.error("Movie Title ERROR: " + movie.movieTitle + " is wrong");
        }
        
        if (!validationService.isValidMovieIdFormat(movie.movieID, movie.movieTitle)) {
            return ValidationResult.error("Movie Id letters ERROR: " + movie.movieID + " are wrong");
        }
        
        if (!validationService.hasUniqueMovieIdDigits(movie.movieID)) {
            return ValidationResult.error("Movie Id numbers ERROR: " + movie.movieID + " aren't unique");
        }
        
        if (!validationService.isUniqueMovieId(movie.movieID, usedIds)) {
            return ValidationResult.error("Movie Id numbers ERROR: " + movie.movieID + " aren't unique");
        }
        
        if (!validationService.isValidCategories(movie.category)) {
            return ValidationResult.error("Movie Category ERROR");
        }
        
        if (validationService.hasDuplicateCategories(movie.category)) {
            return ValidationResult.error("Duplicate Category ERROR");
        }
        
        // All validations passed, save the movie
        addMovieToCollections(movie);
        return ValidationResult.success();
    }
    
    /**
     * Gets movies by category
     * @param category the category to search for
     * @return list of movies in the category, or empty list if none found
     */
    public List<Movie> getMoviesByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }
        return moviesByCategory.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }
    
    /**
     * Gets all movies by their categories
     * @return map of category to list of movies
     */
    public Map<String, ArrayList<Movie>> getAllMoviesByCategory() {
        return new ConcurrentHashMap<>(moviesByCategory);
    }
    
    /**
     * Clears all stored movies (for testing purposes)
     */
    public void clearAllMovies() {
        moviesByCategory.clear();
        usedIds.clear();
    }
    
    /**
     * Gets the set of used movie IDs
     * @return set of used IDs
     */
    public Set<String> getUsedIds() {
        return new java.util.HashSet<>(usedIds);
    }
    
    /**
     * Adds movie to internal collections
     */
    private void addMovieToCollections(Movie movie) {
        usedIds.add(movie.movieID);
        
        for (String category : movie.category) {
            if (category == null || category.trim().isEmpty()) {
                continue;
            }
            String normalizedCategory = category.toLowerCase().trim();
            moviesByCategory.computeIfAbsent(normalizedCategory, k -> new ArrayList<>()).add(movie);
        }
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
