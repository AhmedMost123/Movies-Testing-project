package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class User {

    public String userName;
    public String userID;
    public Set<String> likedCategories;
    private boolean uniqueUserID;
    
    private static Set<String> UID_SET = new HashSet<String>();

    public User(String userName, String userID, List<String> likedCategories) {
        this.userName = userName;
        this.userID = userID;
        this.likedCategories = new HashSet<String>(likedCategories);

        this.uniqueUserID = !UID_SET.contains(userID);

    }

    public Map<String, ArrayList<Movie>> getRecommendations() {
        
        Map<String, ArrayList<Movie>> recommendations = new HashMap<>();
        for(String category : likedCategories) {
            if(Movie.movies.containsKey(category)) {
                recommendations.put(category, Movie.movies.get(category));
            }
        }

        return recommendations;
    }

    public boolean isValidUserName() {

        return Pattern.matches("^[a-zA-Z](\s|[a-zA-Z])*$", userName);
    }

    public boolean isValidUserID() {
        
        return Pattern.matches("^[0-9]{8}([0-9]|[a-zA-Z])$",userID) && uniqueUserID;

    }

    public void save() {
        UID_SET.add(userID);
    }

}
