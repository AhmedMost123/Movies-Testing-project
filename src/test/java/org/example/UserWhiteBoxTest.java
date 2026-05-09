package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//White Box Testing for User.java
/*
White Box Techniques Used:

- Statement Coverage
- Branch Coverage
- Condition Coverage
- Loop Testing
- Path Coverage

Methods Tested:

- getRecommendations()
- isValidUserName()
- isValidUserID()
- save()
- isUniqueUserId()

*/

class UserWhiteBoxTest {

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field ids = User.class.getDeclaredField("UID_SET");
        ids.setAccessible(true);
        ((Set<?>) ids.get(null)).clear();
    }

    @Test
    void testing_valid_username_true_branch() {

        User user = new User("Ahmed Ali","12345678A",List.of("action"));

        assertTrue(user.isValidUserName());
    }

    @Test
    void testing_valid_username_false_branch() {

        User user = new User("Ahmed123","12345678A",List.of("action"));

        assertFalse(user.isValidUserName());
    }

    @Test
    void testing_valid_user_id_true_branch() {

        User user = new User("Ahmed","12345678A",List.of("action"));

        assertTrue(user.isValidUserID());
    }

    @Test
    void testing_valid_user_id_false_branch() {

        User user = new User("Ahmed","123",List.of("action"));

        assertFalse(user.isValidUserID());
    }

    @Test
    void testing_duplicate_user_id_branch() {

        User user1 = new User("Ahmed","12345678A",List.of("action"));
        user1.save();

        User user2 = new User("Ali","12345678A",List.of("drama"));

        assertFalse(user2.isValidUserID());
    }

    @Test
    void testing_get_recommendations_true_branch() {

        Movie movie = new Movie("Titanic","T123",List.of("romance"));
        movie.save();

        User user = new User("Ahmed","12345678A",List.of("romance"));

        Map<String, ArrayList<Movie>> result = user.getRecommendations();

        assertTrue(result.containsKey("romance"));
    }

    @Test
    void testing_get_recommendations_false_branch() {

        Movie movie = new Movie("Titanic","T123",List.of("romance"));
        movie.save();

        User user = new User("Ahmed","12345678A",List.of("action"));

        Map<String, ArrayList<Movie>> result = user.getRecommendations();

        assertFalse(result.containsKey("action"));
    }

    @Test
    void testing_save_method() throws Exception {

        User user = new User("Ahmed","12345678A",List.of("action"));

        user.save();

        Field ids = User.class.getDeclaredField("UID_SET");
        ids.setAccessible(true);

        Set<?> set = (Set<?>) ids.get(null);

        assertTrue(set.contains("12345678A"));
    }

    @Test
    void testing_unique_user_id_true_branch() {

        User user1 = new User("Ahmed","12345678A",List.of("action"));
        User user2 = new User("Ali","87654321B",List.of("drama"));

        List<User> users = List.of(user1,user2);

        assertTrue(user1.isUniqueUserId(users));
    }

    @Test
    void testing_unique_user_id_false_branch() {

        User user1 = new User("Ahmed","12345678A",List.of("action"));
        User user2 = new User("Ali","12345678A",List.of("drama"));

        List<User> users = List.of(user1,user2);

        assertFalse(user1.isUniqueUserId(users));
    }

    @Test
    void testing_get_username_method() {

        User user = new User("Ahmed","12345678A",List.of("action"));

        assertEquals("Ahmed",user.getUsername());
    }
}