package org.example;

import java.util.ArrayList;
import java.util.Map;

public class Movie {

    public String movieTitle;
    public String movieID;
    public String[] category;
    public static Map<String, ArrayList<Movie>> movies; // <category, movies list>
    

    public Movie(String movieTitle, String movieID, String category) {

    }

    public boolean isValidMovieID() {

        return false;
    }

    public boolean isValidMovieTitle() {
        return false;
    }
    
}
