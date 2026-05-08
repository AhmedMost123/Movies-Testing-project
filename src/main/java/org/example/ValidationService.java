package org.example;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service class for validation logic
 * Separates validation concerns from domain objects
 */
public class ValidationService {
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z]( |[a-zA-Z])*$");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[0-9]{8}([0-9]|[a-zA-Z])$");
    private static final Pattern MOVIE_ID_NUMBERS_PATTERN = Pattern.compile("\\d{3}");
    
    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
        "horror", "action", "drama", "comedy", "romance", "thriller"
    );
    
    /**
     * Validates username format
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validates user ID format and uniqueness
     * @param userId the user ID to validate
     * @param usedIds set of already used IDs
     * @return true if valid and unique, false otherwise
     */
    public boolean isValidUserId(String userId, Set<String> usedIds) {
        if (userId == null) {
            return false;
        }
        boolean formatValid = USER_ID_PATTERN.matcher(userId).matches();
        boolean unique = usedIds == null || !usedIds.contains(userId);
        return formatValid && unique;
    }
    
    /**
     * Validates movie title format
     * @param title the movie title to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidMovieTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        
        String[] words = title.split(" ");
        for (String word : words) {
            if (word.isEmpty()) {
                return false;
            }
            if (!Character.isUpperCase(word.charAt(0))) {
                return false;
            }
            for (int i = 1; i < word.length(); i++) {
                if (!Character.isLetter(word.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Validates movie ID format
     * @param movieId the movie ID to validate
     * @param movieTitle the movie title to extract letters from
     * @return true if valid format, false otherwise
     */
    public boolean isValidMovieIdFormat(String movieId, String movieTitle) {
        if (movieId == null || movieTitle == null) {
            return false;
        }
        
        if (movieId.length() < 4) {
            return false;
        }
        
        String letters = movieId.substring(0, movieId.length() - 3);
        String numbers = movieId.substring(movieId.length() - 3);
        
        if (!MOVIE_ID_NUMBERS_PATTERN.matcher(numbers).matches()) {
            return false;
        }
        
        return doIdLettersMatchTitle(letters, movieTitle);
    }
    
    /**
     * Validates that movie ID has unique digits
     * @param movieId the movie ID to validate
     * @return true if digits are unique, false otherwise
     */
    public boolean hasUniqueMovieIdDigits(String movieId) {
        if (movieId == null || movieId.length() < 3) {
            return false;
        }
        
        String numbers = movieId.substring(movieId.length() - 3);
        char n1 = numbers.charAt(0);
        char n2 = numbers.charAt(1);
        char n3 = numbers.charAt(2);
        
        return n1 != n2 && n1 != n3 && n2 != n3;
    }
    
    /**
     * Validates movie ID uniqueness
     * @param movieId the movie ID to validate
     * @param usedIds set of already used IDs
     * @return true if unique, false otherwise
     */
    public boolean isUniqueMovieId(String movieId, Set<String> usedIds) {
        if (movieId == null || usedIds == null) {
            return false;
        }
        return !usedIds.contains(movieId);
    }
    
    /**
     * Validates movie categories
     * @param categories list of categories to validate
     * @return true if all categories are valid, false otherwise
     */
    public boolean isValidCategories(List<String> categories) {
        if (categories == null) {
            return true; // Empty categories are considered valid
        }
        
        for (String category : categories) {
            if (category == null || category.trim().isEmpty()) {
                continue; // Skip empty categories
            }
            if (!ALLOWED_CATEGORIES.contains(category.toLowerCase().trim())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks for duplicate categories (case-insensitive)
     * @param categories list of categories to check
     * @return true if duplicates exist, false otherwise
     */
    public boolean hasDuplicateCategories(List<String> categories) {
        if (categories == null || categories.size() <= 1) {
            return false;
        }
        
        Set<String> seen = new java.util.HashSet<>();
        for (String category : categories) {
            if (category == null) {
                continue;
            }
            String normalized = category.toLowerCase().trim();
            if (seen.contains(normalized)) {
                return true;
            }
            seen.add(normalized);
        }
        return false;
    }
    
    /**
     * Helper method to check if ID letters match title letters
     */
    private boolean doIdLettersMatchTitle(String idLetters, String movieTitle) {
        StringBuilder titleCapitalLetters = new StringBuilder();
        for (int i = 0; i < movieTitle.length(); i++) {
            char c = movieTitle.charAt(i);
            if (Character.isUpperCase(c)) {
                titleCapitalLetters.append(c);
            }
        }
        
        char[] idChars = idLetters.toCharArray();
        char[] titleChars = titleCapitalLetters.toString().toCharArray();
        
        java.util.Arrays.sort(idChars);
        java.util.Arrays.sort(titleChars);
        
        return java.util.Arrays.equals(idChars, titleChars);
    }
}
