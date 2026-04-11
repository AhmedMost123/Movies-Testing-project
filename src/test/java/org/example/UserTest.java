package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserTest {

    // ─────────────────────────────────────────────
    // HELPER: reset static UID_SET before each test
    // so tests don't interfere with each other
    // ─────────────────────────────────────────────
    @BeforeEach
    void resetStaticState() throws Exception {
        Field uidSet = User.class.getDeclaredField("UID_SET");
        uidSet.setAccessible(true);
        ((Set<?>) uidSet.get(null)).clear();

        // Also clear Movie static map to avoid recommendation bleed
        Movie.movies.clear();
    }


    // helper to quickly build a User
    private User makeUser(String name, String id) {
        return new User(name, id, new ArrayList<>());
    }

    private User makeUser(String name, String id, List<String> categories) {
        return new User(name, id, categories);
    }


    // test isValidUserName() function :

    @Nested
    @DisplayName("1 - isValidUserName()")
    class ValidUserNameTests {

        @Test
        @DisplayName("Single word name should be valid")
        void singleWordName() {
            assertTrue(makeUser("Ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Two word name with space should be valid")
        void twoWordName() {
            assertTrue(makeUser("Ahmed Ali", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("All lowercase name should be valid")
        void allLowercaseName() {
            assertTrue(makeUser("ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Mixed case name should be valid")
        void mixedCaseName() {
            assertTrue(makeUser("aHmEd", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name starting with a space should be invalid")
        void nameStartingWithSpace() {
            assertFalse(makeUser(" Ahmed", "123456789").isValidUserName());
        }

        @Test
        @DisplayName(" Name ending with a space should be invalid")
        void nameEndingWithSpace() {
            assertFalse(makeUser("Ahmed ", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name with numbers should be invalid")
        void nameWithNumbers() {
            assertFalse(makeUser("Ahmed123", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Name with special characters should be invalid")
        void nameWithSpecialChars() {
            assertFalse(makeUser("Ahmed@Ali", "123456789").isValidUserName());
        }

        @Test
        @DisplayName("Empty name should be invalid")
        void emptyName() {
            assertFalse(makeUser("", "123456789").isValidUserName());
        }
    }


    // test isValidUserID() function

    @Nested
    @DisplayName("2 - isValidUserID()")
    class ValidUserIDTests {

        @Test
        @DisplayName("8 digits + 1 digit (all numeric) should be valid")
        void validIDAllNumeric() {
            assertTrue(makeUser("Ahmed", "123456789").isValidUserID());
        }

        @Test
        @DisplayName(" 8 digits + 1 uppercase letter should be valid")
        void validIDWithUppercaseLetter() {
            assertTrue(makeUser("Ahmed", "12345678A").isValidUserID());
        }

        @Test
        @DisplayName(" 8 digits + 1 lowercase letter should be valid")
        void validIDWithLowercaseLetter() {
            assertTrue(makeUser("Ahmed", "12345678a").isValidUserID());
        }

        @Test
        @DisplayName("ID shorter than 9 characters should be invalid")
        void tooShortID() {
            assertFalse(makeUser("Ahmed", "1234567").isValidUserID());
        }

        @Test
        @DisplayName(" ID longer than 9 characters should be invalid")
        void tooLongID() {
            assertFalse(makeUser("Ahmed", "1234567890A").isValidUserID());
        }

        @Test
        @DisplayName(" ID with special character at end should be invalid")
        void idWithSpecialCharAtEnd() {
            assertFalse(makeUser("Ahmed", "12345678@").isValidUserID());
        }

        @Test
        @DisplayName(" ID starting with a letter should be invalid")
        void idStartingWithLetter() {
            assertFalse(makeUser("Ahmed", "A23456789").isValidUserID());
        }

        @Test
        @DisplayName(" Duplicate ID should be invalid after first user saved")
        void duplicateIDAfterSave() {
            User user1 = makeUser("Ahmed", "12345678A");
            user1.save(); // saves ID to static set

            User user2 = makeUser("Ali", "12345678A"); // same ID
            assertFalse(user2.isValidUserID());
        }


    }
}
