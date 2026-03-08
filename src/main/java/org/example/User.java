package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class User {

    public String userName;
    public String userID;
    public Set<String> likedCategories;
    private boolean uniqueUserID;
    
    private static Set<String> UID_SET = new HashSet<String>();

    public User(String userName, String userID, String[] likedCategories) {
        this.userName = userName;
        this.userID = userID;
        this.likedCategories = new HashSet<String>(Arrays.asList(likedCategories));

        this.uniqueUserID = !UID_SET.contains(userID);

    }

    public ArrayList<Movie> getRecommendations() {

        ArrayList<Movie> recommendations = new ArrayList<>();
        for(String category : likedCategories) {
            if(Movie.movies.containsKey(category)) recommendations.addAll(Movie.movies.get(category));
        }
        return recommendations;
    }

    public boolean isValidUserName() {

        return Pattern.matches("^[a-zA-Z](\s|[a-zA-Z])*$", userName);
    }

    public boolean isValidUserID() {
        if(!Pattern.matches("^[0-9]([0-9]|[a-zA-Z]){8}$",userID) || !uniqueUserID) return false;

        if(Character.isLetter(userID.charAt(8)) && Character.isLetter((userID.charAt((7))))) return false;

        return true;

    }

    public void save() {
        UID_SET.add(userID);
    }

}
