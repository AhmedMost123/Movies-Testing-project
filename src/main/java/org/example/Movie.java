package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie {

    public String movieTitle;
    public String movieID;
    public List<String> category;
    public static Map<String, ArrayList<Movie>> movies = new HashMap<String, ArrayList<Movie>>(); // <category, movies list>

    public Movie(String movieTitle, String movieID, List<String> category) {
        this.movieTitle = movieTitle;
        this.movieID = movieID;
        this.category = category;
    }

    public boolean isValidMovieID() {
        if (movieID == null) {return false;}

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

        return letters.equals(capitalLetter.toString());
    }

    public boolean isUniqueMovieID() {

        String numbers = movieID.substring(movieID.length() - 3);
        //each digit from the  movi ID.
        char n1 = numbers.charAt(0);
        char n2 = numbers.charAt(1);
        char n3 = numbers.charAt(2);

        return n1 != n2 && n1 != n3 && n2 != n3;
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
        for (String cat : category) {
            if (!movies.containsKey(cat)) {
                movies.put(cat, new ArrayList<Movie>());
            }

            movies.get(cat).add(this);
        }
    }

    public String toString() {
        return movieID + "-" + movieTitle;
    }
}