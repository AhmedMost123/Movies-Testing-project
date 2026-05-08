package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Movie {

    public String movieTitle;
    public String movieID;
    public List<String> category;

    public static Map<String, ArrayList<Movie>> movies = new HashMap<>();
    private static Set<String> USED_IDS = new HashSet<>();

    private static final Set<String> ALLOWED_CATEGORIES =
            new HashSet<>(List.of(
                    "horror", "action", "drama", "comedy", "romance", "thriller"
            ));

    public Movie(String movieTitle, String movieID, List<String> category) {
        this.movieTitle = movieTitle;
        this.movieID = movieID;

        this.category = category;
    }

    public boolean hasDuplicateCategories() {
        if (category == null) return false; // 

        Set<String> set = new HashSet<>();
        for (String cat : category) {
            if (cat == null) continue;

            String c = cat.toLowerCase();
            if (set.contains(c)) return true;
            set.add(c);
        }
        return false;
    }

    public boolean isValidCategory() {
        if (category == null) return true; // 

        for (String cat : category) {
            if (cat == null) return false;
            if (!ALLOWED_CATEGORIES.contains(cat.toLowerCase())) return false;
        }
        return true;
    }

    public boolean isValidMovieID() {

        if (movieID == null || movieTitle == null) return false;

        if (movieID.length() < 4) return false;

        String letters = movieID.substring(0, movieID.length() - 3);
        String numbers = movieID.substring(movieID.length() - 3);

        if (!numbers.matches("\\d{3}")) return false;

        StringBuilder capitalLetter = new StringBuilder();

        for (int i = 0; i < movieTitle.length(); i++) {
            char c = movieTitle.charAt(i);
            if (Character.isUpperCase(c)) {
                capitalLetter.append(c);
            }
        }

        char[] idChars = letters.toCharArray();
        char[] titleChars = capitalLetter.toString().toCharArray();

        Arrays.sort(idChars);
        Arrays.sort(titleChars);

        return Arrays.equals(idChars, titleChars);
    }

    public boolean isUniqueMovieID() {

        if (movieID == null || movieID.length() < 3) return false; 

        String numbers = movieID.substring(movieID.length() - 3);

        char n1 = numbers.charAt(0);
        char n2 = numbers.charAt(1);
        char n3 = numbers.charAt(2);

        if (n1 == n2 || n1 == n3 || n2 == n3) return false;

        return !USED_IDS.contains(movieID);
    }

    public boolean isValidMovieTitle() {

        if (movieTitle == null || movieTitle.isEmpty()) return false;

        if (movieTitle.startsWith(" ") || movieTitle.endsWith(" ")) return false;

        String[] words = movieTitle.split(" ");

        for (String word : words) {
            if (word.isEmpty()) return false;

            if (!Character.isUpperCase(word.charAt(0))) return false;

            for (int i = 1; i < word.length(); i++) {
                char c = word.charAt(i);
                if (!Character.isLetter(c) && c != '-') return false;
            }
        }

        return true;
    }

    public void save() {

        if (category == null) return;
        for (String cat : category) {
            if (cat == null) continue;

            String key = cat.toLowerCase();

            movies.putIfAbsent(key, new ArrayList<>());
            movies.get(key).add(this);
        }

        if (movieID != null) {
            USED_IDS.add(movieID);
        }
    }

    public String toString() {
        return movieID + "-" + movieTitle;
    }
}