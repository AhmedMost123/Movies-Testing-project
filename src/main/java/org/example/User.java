package org.example;
import java.util.List;

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

    public String getUsername() {
        return username;
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


        return Pattern.matches("^[a-zA-Z]( |[a-zA-Z])*$", userName);
    }

    public boolean isValidUserID() {
        
        return Pattern.matches("^[0-9]{8}([0-9]|[a-zA-Z])$",userID) && uniqueUserID;

    }

    public void save() {
        UID_SET.add(userID);
    }


    public boolean isUniqueUserId(List<User> allUsers) {


        // loop through every user in the list to check that there is no similar User id
        for (int i = 0; i < allUsers.size(); i++) {

            // get the current user from the list
            User checkedUser = allUsers.get(i);

            // Condition 2: skip comparing the user with itself
            // if checkedUser is the exact same object as this user → skip it
            if (checkedUser == this) {
                continue;
            }

            // Condition 3: compare the two userIds
            String checkedUserId = checkedUser.getUserId();

            // Condition 4: if they match → userId is not unique
            if (checkedUserId.equals(this.userId)) {
                return false;
            }
        }

        // Condition 5: if no duplicate found so the  userId is unique
        return true;

    }
    
}
