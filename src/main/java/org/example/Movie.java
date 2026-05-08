package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Movie {

    public String movieTitle;
    public String movieID;
    public List<String> category;
    public static Map<String, ArrayList<Movie>> movies = new HashMap<String, ArrayList<Movie>>(); 
    private static Set<String> USED_IDS = new HashSet<>();
    private static final Set<String> ALLOWED_CATEGORIES =
    new HashSet<>(List.of(
            "horror",
            "action",
            "drama",
            "comedy",
            "romance",
            "thriller"
    ));
    public Movie(String movieTitle, String movieID, List<String> category) {
        this.movieTitle = movieTitle;
        this.movieID = movieID;
        this.category = category != null ? new ArrayList<>(category) : new ArrayList<>();
    }
    public boolean hasDuplicateCategories() 
    { 
        if (category == null || category.isEmpty()) {
            return false;
        }
        Set<String> set = new HashSet<>(); 
        for(String cat : category) 
        { 
            if (cat == null) continue;
            if(set.contains(cat.toLowerCase())) 
            { 
                return true; 
            } 
            set.add(cat.toLowerCase()); 
        } 
        return false; 
    }
    public boolean isValidCategory() 
    { 
        if (category == null || category.isEmpty()) {
            return true; // Empty categories are considered valid
        }
        for(String cat : category) 
        { 
            if (cat == null || !ALLOWED_CATEGORIES.contains(cat.toLowerCase())) 
            { 
                return false;
            } 
        } 
        return true; 
    }

    public boolean isValidMovieID() {
        if (movieID == null) {return false;}
        if (movieTitle == null) {return false;}

        if (movieID.length() < 4) {return false;}

        String letters = movieID.substring(0, movieID.length() - 3);
        String numbers = movieID.substring(movieID.length() - 3);

        if (!numbers.matches("\\d{3}")) {return false;}////if numbers is not  3 digits

        StringBuilder capitalLetter = new StringBuilder();
        for (int i = 0; i < movieTitle.length(); i++) {
            char c = movieTitle.charAt(i);
            if (Character.isUpperCase(c)) {
                capitalLetter.append(c);
            }
        }

        char[] idChars = letters.toCharArray();
        char[] titleChars = capitalLetter.toString().toCharArray();

        java.util.Arrays.sort(idChars);
        java.util.Arrays.sort(titleChars);

        return java.util.Arrays.equals(idChars, titleChars);
        
    }

    public boolean isUniqueMovieID() {

        String numbers = movieID.substring(movieID.length() - 3);
        //each digit from the  movi ID.
        char n1 = numbers.charAt(0);
        char n2 = numbers.charAt(1);
        char n3 = numbers.charAt(2);

        if(n1 == n2 || n1 == n3 || n2 == n3) 
        { 
            return false; 
        } 
        else if(USED_IDS.contains(movieID)) 
        { 
            return false; 
        } 
        return true;
    }

    public boolean isValidMovieTitle() {
        if (movieTitle == null || movieTitle.isEmpty()) {return false;}


        ///"The Matrix" = ["The","Matrix"]
        String[] words = movieTitle.split(" ");

        for (String word : words) {
            if (word.isEmpty()) { return false;  }

            if (!Character.isUpperCase(word.charAt(0))) {return false; } ///first char of every word
            /////start at i=1 bcuz  we checked the first already.
            for (int i = 1; i < word.length(); i++) {
                ///split word to list of chars and check each isLetter() 
                if (!Character.isLetter(word.charAt(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    public void save() {
        if (category == null) return;
        
        for (String cat : category) {
            if (cat == null || cat.trim().isEmpty()) {
                continue;
            }
            String normalizedCat = cat.toLowerCase().trim();
            if (!movies.containsKey(normalizedCat)) {
                movies.put(normalizedCat, new ArrayList<Movie>());
            }

            movies.get(normalizedCat).add(this);
            if (movieID != null) {
                USED_IDS.add(movieID);
            }
        }
    }

    public String toString() {
        return movieID + "-" + movieTitle;
    }
}