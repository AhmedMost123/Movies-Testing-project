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

//Data Flow Testing for User.java
/*
DFT Techniques Used:

- All Definitions Coverage
- All Uses Coverage
- DU Path Testing
- Variable Propagation Testing

Variables Tested:

- userName
- userID
- likedCategories
- UID_SET

*/

class UserDataFlowTest {

    @BeforeEach
    void resetStaticData() throws Exception {

        Movie.movies.clear();

        Field ids = User.class.getDeclaredField("UID_SET");
        ids.setAccessible(true);

        ((Set<?>) ids.get(null)).clear();
    }

    @Test
    void testing_username_definition_to_use() {

        User user = new User(
                "Ahmed",
                "12345678A",
                List.of("action")
        );

        assertEquals("Ahmed",user.getUsername());
    }

    @Test
    void testing_valid_username_data_flow() {

        User user = new User(
                "Ahmed Ali",
                "12345678A",
                List.of("action")
        );

        assertTrue(user.isValidUserName());
    }

    @Test
    void testing_invalid_username_data_flow() {

        User user = new User(
                "Ahmed123",
                "12345678A",
                List.of("action")
        );

        assertFalse(user.isValidUserName());
    }

    @Test
    void testing_valid_user_id_definition_to_use() {

        User user = new User(
                "Ahmed",
                "12345678A",
                List.of("action")
        );

        assertTrue(user.isValidUserID());
    }

    @Test
    void testing_duplicate_user_id_data_flow() {

        User user1 = new User(
                "Ahmed",
                "12345678A",
                List.of("action")
        );

        user1.save();

        User user2 = new User(
                "Ali",
                "12345678A",
                List.of("drama")
        );

        assertFalse(user2.isValidUserID());
    }

    @Test
    void testing_liked_categories_definition_to_use() {

        Movie movie = new Movie(
                "Titanic",
                "T123",
                List.of("romance")
        );

        movie.save();

        User user = new User(
                "Ahmed",
                "12345678A",
                List.of("romance")
        );

        Map<String, ArrayList<Movie>> result =
                user.getRecommendations();

        assertTrue(result.containsKey("romance"));
    }

    @Test
    void testing_uid_set_definition_to_use() throws Exception {

        User user = new User(
                "Ahmed",
                "12345678A",
                List.of("action")
        );

        user.save();

        Field ids = User.class.getDeclaredField("UID_SET");
        ids.setAccessible(true);

        Set<?> set = (Set<?>) ids.get(null);

        assertTrue(set.contains("12345678A"));
    }

    @Test
    void testing_unique_user_id_definition_use_path() {

        User user1 = new User(
                "Ahmed",
                "12345678A",
                List.of("action")
        );

        User user2 = new User(
                "Ali",
                "87654321B",
                List.of("drama")
        );

        List<User> users = List.of(user1,user2);

        assertTrue(user1.isUniqueUserId(users));
    }
}