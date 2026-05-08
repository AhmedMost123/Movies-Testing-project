package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for generating recommendations
 * Handles the business logic for movie recommendations
 */
public class RecommendationService {
    
    private final MovieService movieService;
    private final UserService userService;
    
    public RecommendationService(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
        // Set up the movie service dependency to avoid circular dependency issues
        this.userService.setMovieService(movieService);
    }
    
    /**
     * Generates recommendations for a list of users
     * @param users list of users to generate recommendations for
     * @return list of recommendation results
     */
    public List<RecommendationResult> generateRecommendations(List<User> users) {
        List<RecommendationResult> results = new ArrayList<>();
        
        for (User user : users) {
            Map<String, ArrayList<Movie>> recommendations = userService.getRecommendations(user);
            results.add(new RecommendationResult(user, recommendations));
        }
        
        return results;
    }
    
    /**
     * Formats recommendation results into output string
     * @param results list of recommendation results
     * @return formatted output string
     */
    public String formatRecommendations(List<RecommendationResult> results) {
        StringBuilder buffer = new StringBuilder();
        
        for (RecommendationResult result : results) {
            User user = result.getUser();
            Map<String, ArrayList<Movie>> recommendations = result.getRecommendations();
            
            buffer.append(String.format("For User: %s,%s\n", user.userName, user.userID));
            
            for (Map.Entry<String, ArrayList<Movie>> suggestion : recommendations.entrySet()) {
                String moviesString = String.join(",", 
                    suggestion.getValue().stream()
                        .map(Movie::toString)
                        .toList());
                buffer.append(String.format("%s: %s\n", suggestion.getKey(), moviesString));
            }
        }
        
        return buffer.toString();
    }
    
    /**
     * Result of recommendation generation for a single user
     */
    public static class RecommendationResult {
        private final User user;
        private final Map<String, ArrayList<Movie>> recommendations;
        
        public RecommendationResult(User user, Map<String, ArrayList<Movie>> recommendations) {
            this.user = user;
            this.recommendations = recommendations;
        }
        
        public User getUser() {
            return user;
        }
        
        public Map<String, ArrayList<Movie>> getRecommendations() {
            return recommendations;
        }
    }
}
