# White Box Testing Analysis
## Movie Recommendation System - Software Testing Project

### 1. Overview

This document provides comprehensive White Box Testing analysis for the Movie Recommendation System. White Box Testing focuses on testing the internal structure, logic, and implementation details of the code, ensuring thorough coverage of execution paths, conditions, and statements.

---

## 2. Code Structure Analysis

### 2.1 Main Classes and Methods

| Class | Public Methods | Private Methods | Static Members |
|-------|----------------|----------------|----------------|
| **App** | main() | - | - |
| **User** | getUsername(), getRecommendations(), isValidUserName(), isValidUserID(), isUniqueUserId(), save() | - | UID_SET (static Set<String>) |
| **Movie** | isValidMovieID(), isUniqueMovieID(), isValidMovieTitle(), isValidCategory(), hasDuplicateCategories(), save(), toString() | - | movies (static Map<String, ArrayList<Movie>>), USED_IDS (static Set<String>), ALLOWED_CATEGORIES (static Set<String>) |
| **FileManager** | readFile(), writeFile() | - | - |

---

## 3. Statement Coverage Analysis

### 3.1 App.main() Method

**Lines to Cover: 14-107**

| Statement Type | Line Range | Description | Test Case Required |
|----------------|------------|-------------|-------------------|
| Input validation | 16-22 | Check arguments length | Test with 0, 1, 2, 3 arguments |
| File reading | 28-36 | Read movies and users files | Test with valid/invalid files |
| Movie validation loop | 38-69 | Process each movie entry | Test various movie validation scenarios |
| User processing loop | 71-92 | Process each user entry | Test various user validation scenarios |
| Recommendation generation | 94-104 | Generate and write recommendations | Test with valid data |
| Error handling | Multiple | Write error messages and return | Test each error condition |

**Coverage Strategy:**
- Test with different argument counts
- Test with valid and invalid files
- Test each validation error path
- Test successful processing path

### 3.2 User Class Methods

#### isValidUserName() - Lines 46-48
```java
public boolean isValidUserName() {
    return Pattern.matches("^[a-zA-Z]( |[a-zA-Z])*$", userName);
}
```

**Statement Coverage Requirements:**
- Valid username (returns true)
- Invalid username (returns false)
- Null username (if applicable)

#### isValidUserID() - Lines 50-54
```java
public boolean isValidUserID() {
    return Pattern.matches("^[0-9]{8}([0-9]|[a-zA-Z])$",userID) && uniqueUserID;
}
```

**Statement Coverage Requirements:**
- Valid ID format + unique (returns true)
- Invalid format (returns false)
- Valid format but not unique (returns false)

#### getRecommendations() - Lines 34-44
```java
public Map<String, ArrayList<Movie>> getRecommendations() {
    Map<String, ArrayList<Movie>> recommendations = new HashMap<>();
    for(String category : likedCategories) {
        if(Movie.movies.containsKey(category)) {
            recommendations.put(category, Movie.movies.get(category));
        }
    }
    return recommendations;
}
```

**Statement Coverage Requirements:**
- Empty likedCategories (returns empty map)
- Non-empty categories with matches (populates map)
- Non-empty categories with no matches (returns empty map)

### 3.3 Movie Class Methods

#### isValidMovieTitle() - Lines 104-125
```java
public boolean isValidMovieTitle() {
    if (movieTitle == null || movieTitle.isEmpty()) {return false;}
    String[] words = movieTitle.split(" ");
    for (String word : words) {
        if (word.isEmpty()) { return false;  }
        if (!Character.isUpperCase(word.charAt(0))) {return false; }
        for (int i = 1; i < word.length(); i++) {
            if (!Character.isLetter(word.charAt(i))) {
                return false;
            }
        }
    }
    return true;
}
```

**Statement Coverage Requirements:**
- Null title (returns false)
- Empty title (returns false)
- Valid single word (returns true)
- Valid multiple words (returns true)
- Word starting with lowercase (returns false)
- Word with non-letter characters (returns false)
- Empty word in split result (returns false)

#### isValidMovieID() - Lines 57-83
```java
public boolean isValidMovieID() {
    if (movieID == null) {return false;}
    if (movieID.length() < 4) {return false;}
    String letters = movieID.substring(0, movieID.length() - 3);
    String numbers = movieID.substring(movieID.length() - 3);
    if (!numbers.matches("\\d{3}")) {return false;}
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
```

**Statement Coverage Requirements:**
- Null ID (returns false)
- ID too short (returns false)
- Invalid number format (returns false)
- Valid ID with matching letters (returns true)
- Valid ID with mismatching letters (returns false)

#### isUniqueMovieID() - Lines 85-102
```java
public boolean isUniqueMovieID() {
    String numbers = movieID.substring(movieID.length() - 3);
    char n1 = numbers.charAt(0);
    char n2 = numbers.charAt(1);
    char n3 = numbers.charAt(2);
    if(n1 == n2 || n1 == n3 || n2 == n3) { 
        return false; 
    } 
    else if(USED_IDS.contains(movieID)) { 
        return false; 
    } 
    return true;
}
```

**Statement Coverage Requirements:**
- Duplicate digits (n1==n2) (returns false)
- Duplicate digits (n1==n3) (returns false)
- Duplicate digits (n2==n3) (returns false)
- ID already used (returns false)
- Unique digits and not used (returns true)

### 3.4 FileManager Methods

#### readFile() - Lines 11-58
```java
public static List<Map<String, Object>> readFile(String path) throws Exception {
    List<Map<String, Object>> data = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line1, line2;
        while ((line1 = reader.readLine()) != null) {
            line2 = reader.readLine();
            if (line2 == null) {
                throw new Exception("Missing second line for entry: " + line1);
            }
            String[] parts = line1.split(",");
            if (parts.length != 2) {
                throw new Exception("Line format error: " + line1);
            }
            String label = parts[0].trim();
            String id = parts[1].trim();
            List<String> categories = new ArrayList<>();
            for (String c : line2.split(",")) {
                categories.add(c.trim());
            }
            Map<String, Object> item = new HashMap<>();
            item.put("label", label);
            item.put("id", id);
            item.put("category", categories);
            data.add(item);
        }
    } catch (Exception e) {
        return null;
    }
    return data;
}
```

**Statement Coverage Requirements:**
- Successful file reading (returns data)
- File not found (returns null)
- Missing second line (throws exception)
- Line format error (throws exception)
- Exception in try block (returns null)

---

## 4. Branch Coverage Analysis

### 4.1 App.main() Branch Coverage

| Branch | Condition | True Path | False Path | Test Case |
|--------|-----------|-----------|------------|-----------|
| B1 | args.length < 2 | Display usage | Continue | Test with 0,1,2,3 args |
| B2 | usersData == null || moviesData == null | Error return | Continue | Test with invalid files |
| B3 | !movie.isValidMovieTitle() | Write error | Continue | Test with invalid title |
| B4 | !movie.isValidMovieID() | Write error | Continue | Test with invalid ID |
| B5 | !movie.isUniqueMovieID() | Write error | Continue | Test with duplicate ID |
| B6 | !movie.isValidCategory() | Write error | Continue | Test with invalid category |
| B7 | !movie.hasDuplicateCategories() | Write error | Continue | Test with duplicate categories |
| B8 | !user.isValidUserName() | Write error | Continue | Test with invalid username |
| B9 | !user.isValidUserID() | Write error | Continue | Test with invalid user ID |

### 4.2 User Class Branch Coverage

#### isValidUserName()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | Pattern matches | Valid username |
| B2 | Pattern doesn't match | Invalid username |

#### isValidUserID()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | Pattern matches AND uniqueUserID | Valid unique ID |
| B2 | Pattern doesn't match | Invalid format |
| B3 | Pattern matches but not unique | Duplicate ID |

#### getRecommendations()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | likedCategories empty | Empty recommendations |
| B2 | Movie.movies.containsKey(category) | Category exists |
| B3 | !Movie.movies.containsKey(category) | Category doesn't exist |

### 4.3 Movie Class Branch Coverage

#### isValidMovieTitle()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | movieTitle == null | Null title |
| B2 | movieTitle.isEmpty() | Empty title |
| B3 | word.isEmpty() | Empty word after split |
| B4 | !Character.isUpperCase(word.charAt(0)) | Lowercase first letter |
| B5 | !Character.isLetter(word.charAt(i)) | Non-letter character |

#### isValidMovieID()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | movieID == null | Null ID |
| B2 | movieID.length() < 4 | Too short ID |
| B3 | !numbers.matches("\\d{3}") | Invalid number format |
| B4 | Character.isUpperCase(c) | Uppercase character in title |
| B5 | Arrays.equals(idChars, titleChars) | Matching letters |

#### isUniqueMovieID()
| Branch | Condition | Test Case |
|--------|-----------|-----------|
| B1 | n1 == n2 | First two digits same |
| B2 | n1 == n3 | First and third digits same |
| B3 | n2 == n3 | Last two digits same |
| B4 | USED_IDS.contains(movieID) | ID already used |

---

## 5. Condition Coverage Analysis

### 5.1 Complex Conditions Analysis

#### App.main() Line 31
```java
if(usersData == null || moviesData == null) {
```
**Conditions:**
- C1: usersData == null
- C2: moviesData == null

**Test Cases:**
- C1=true, C2=false (usersData null, moviesData valid)
- C1=false, C2=true (usersData valid, moviesData null)
- C1=true, C2=true (both null)
- C1=false, C2=false (both valid)

#### User.isValidUserID() Line 52
```java
return Pattern.matches("^[0-9]{8}([0-9]|[a-zA-Z])$",userID) && uniqueUserID;
```
**Conditions:**
- C1: Pattern matches
- C2: uniqueUserID

**Test Cases:**
- C1=true, C2=true (valid format, unique)
- C1=true, C2=false (valid format, not unique)
- C1=false, C2=true (invalid format, unique)
- C1=false, C2=false (invalid format, not unique)

#### Movie.isUniqueMovieID() Lines 93-101
```java
if(n1 == n2 || n1 == n3 || n2 == n3) { 
    return false; 
} 
else if(USED_IDS.contains(movieID)) { 
    return false; 
}
```
**Conditions:**
- C1: n1 == n2
- C2: n1 == n3
- C3: n2 == n3
- C4: USED_IDS.contains(movieID)

**Test Cases:**
- C1=true, others=false (n1==n2)
- C2=true, others=false (n1==n3)
- C3=true, others=false (n2==n3)
- C1=false, C2=false, C3=false, C4=true (unique digits, already used)
- C1=false, C2=false, C3=false, C4=false (completely unique)

---

## 6. Path Coverage Analysis

### 6.1 App.main() Execution Paths

**Path 1: Success Path**
1. Valid arguments (>=2)
2. Valid files read
3. All movies valid
4. All users valid
5. Recommendations generated and written

**Path 2: Arguments Error**
1. Invalid arguments (<2)
2. Usage message displayed
3. System exits

**Path 3: File Access Error**
1. Valid arguments
2. File reading fails (returns null)
3. Error message displayed
4. System exits

**Path 4: Movie Title Error**
1. Valid arguments and files
2. Movie with invalid title encountered
3. Error message written
4. System exits

**Path 5: Movie ID Letters Error**
1. Valid arguments and files
2. Movie with invalid ID letters encountered
3. Error message written
4. System exits

**Path 6: Movie ID Numbers Error**
1. Valid arguments and files
2. Movie with duplicate ID digits encountered
3. Error message written
4. System exits

**Path 7: Movie Category Error**
1. Valid arguments and files
2. Movie with invalid category encountered
3. Error message written
4. System exits

**Path 8: Duplicate Category Error**
1. Valid arguments and files
2. Movie with duplicate categories encountered
3. Error message written
4. System exits

**Path 9: Username Error**
1. Valid arguments and files
2. Valid movies processed
3. User with invalid username encountered
4. Error message written
5. System exits

**Path 10: User ID Error**
1. Valid arguments and files
2. Valid movies processed
3. User with invalid ID encountered
4. Error message written
5. System exits

### 6.2 User.getRecommendations() Paths

**Path 1: Empty Categories**
1. likedCategories is empty
2. Loop doesn't execute
3. Returns empty map

**Path 2: Categories with Matches**
1. likedCategories has items
2. Some categories exist in Movie.movies
3. Recommendations populated
4. Returns populated map

**Path 3: Categories without Matches**
1. likedCategories has items
2. No categories exist in Movie.movies
3. Map remains empty
4. Returns empty map

### 6.3 Movie.isValidMovieTitle() Paths

**Path 1: Null/Empty Title**
1. movieTitle is null or empty
2. Returns false immediately

**Path 2: Valid Single Word**
1. Title has one word
2. Word starts with uppercase
3. All other characters are letters
4. Returns true

**Path 3: Valid Multiple Words**
1. Title has multiple words
2. All words start with uppercase
3. All other characters are letters
4. Returns true

**Path 4: Invalid - Lowercase Start**
1. Title has word starting with lowercase
2. Returns false

**Path 5: Invalid - Non-letter Characters**
1. Title has word with non-letter characters
2. Returns false

---

## 7. Test Cases for High Coverage

### 7.1 Statement Coverage Test Cases

| Test Case | Method | Statements Covered |
|-----------|--------|-------------------|
| TC-SC-001 | App.main() | Valid arguments, valid files, successful processing |
| TC-SC-002 | App.main() | Invalid arguments count |
| TC-SC-003 | App.main() | Invalid file access |
| TC-SC-004 | App.main() | Invalid movie title |
| TC-SC-005 | App.main() | Invalid movie ID letters |
| TC-SC-006 | App.main() | Invalid movie ID numbers |
| TC-SC-007 | App.main() | Invalid movie category |
| TC-SC-008 | App.main() | Duplicate movie categories |
| TC-SC-009 | App.main() | Invalid username |
| TC-SC-010 | App.main() | Invalid user ID |

### 7.2 Branch Coverage Test Cases

| Test Case | Branches Covered | Description |
|-----------|------------------|-------------|
| TC-BC-001 | App B1-B9 | All error paths in main |
| TC-BC-002 | User B1-B3 | All user validation branches |
| TC-BC-003 | Movie B1-B5 | All movie title validation branches |
| TC-BC-004 | Movie B6-B10 | All movie ID validation branches |
| TC-BC-005 | Movie B11-B14 | All movie uniqueness branches |

### 7.3 Condition Coverage Test Cases

| Test Case | Conditions | Test Data |
|-----------|------------|-----------|
| TC-CC-001 | usersData==null, moviesData==null | Both files invalid |
| TC-CC-002 | usersData==null, moviesData!=null | Only users file invalid |
| TC-CC-003 | usersData!=null, moviesData==null | Only movies file invalid |
| TC-CC-004 | Pattern matches, uniqueUserID | Valid unique user ID |
| TC-CC-005 | Pattern matches, !uniqueUserID | Valid format but duplicate ID |
| TC-CC-006 | !Pattern matches, uniqueUserID | Invalid format but unique |
| TC-CC-007 | !Pattern matches, !uniqueUserID | Invalid format and duplicate |

### 7.4 Path Coverage Test Cases

| Test Case | Execution Path | Test Scenario |
|-----------|----------------|---------------|
| TC-PC-001 | App Success Path | All valid data |
| TC-PC-002 | App Error Path 1 | Invalid arguments |
| TC-PC-003 | App Error Path 2 | File access error |
| TC-PC-004 | App Error Path 3 | Movie title error |
| TC-PC-005 | App Error Path 4 | Movie ID error |
| TC-PC-006 | App Error Path 5 | User validation error |
| TC-PC-007 | User Rec Path 1 | Empty categories |
| TC-PC-008 | User Rec Path 2 | Categories with matches |
| TC-PC-009 | User Rec Path 3 | Categories without matches |
| TC-PC-010 | Movie Title Path 1 | Valid single word |
| TC-PC-011 | Movie Title Path 2 | Valid multiple words |
| TC-PC-012 | Movie Title Path 3 | Invalid lowercase start |
| TC-PC-013 | Movie Title Path 4 | Invalid non-letter chars |

---

## 8. Coverage Metrics Summary

### 8.1 Target Coverage Goals

| Metric Type | Target | Current | Gap |
|-------------|--------|---------|-----|
| Statement Coverage | 100% | TBD | TBD |
| Branch Coverage | 100% | TBD | TBD |
| Condition Coverage | 100% | TBD | TBD |
| Path Coverage | 95% | TBD | TBD |

### 8.2 Coverage Achievement Strategy

1. **Statement Coverage**: Ensure every line of executable code is tested
2. **Branch Coverage**: Test both true and false outcomes of every decision
3. **Condition Coverage**: Test all possible combinations of conditions
4. **Path Coverage**: Test all feasible execution paths

### 8.3 Unreachable Code Analysis

Based on code analysis, the following potential unreachable code scenarios have been identified:

| Location | Potential Issue | Analysis |
|----------|----------------|----------|
| User.isUniqueUserId() | Self-comparison check | The `if (checkedUser == this)` check might be redundant in typical usage |
| Movie.isValidMovieID() | Complex letter matching | Some edge cases in letter extraction might be unreachable |
| FileManager.readFile() | Exception handling | Some exception paths might be unreachable under normal conditions |

---

## 9. Refactoring Recommendations

### 9.1 Methods Difficult to Test

| Method | Issue | Refactoring Suggestion |
|--------|-------|-----------------------|
| App.main() | Static method, multiple responsibilities | Extract validation logic to separate classes |
| User.isValidUserID() | Depends on static state | Inject dependency for uniqueness checking |
| Movie.isValidMovieID() | Complex logic with multiple responsibilities | Split into smaller, focused methods |
| FileManager.readFile() | Mixed concerns (parsing + validation) | Separate parsing from validation logic |

### 9.2 Suggested Refactoring

#### Extract Validation Classes
```java
public class MovieValidator {
    public boolean isValidTitle(String title) { ... }
    public boolean isValidID(String id, String title) { ... }
    public boolean isValidCategory(List<String> categories) { ... }
}

public class UserValidator {
    public boolean isValidUsername(String username) { ... }
    public boolean isValidUserID(String id, Set<String> usedIds) { ... }
}
```

#### Extract Service Classes
```java
public class MovieService {
    private final MovieValidator validator;
    private final MovieRepository repository;
    
    public ValidationResult processMovie(MovieData data) { ... }
}

public class UserService {
    private final UserValidator validator;
    private final UserRepository repository;
    
    public ValidationResult processUser(UserData data) { ... }
}
```

---

## 10. Testing Tools and Techniques

### 10.1 Coverage Analysis Tools

- **JaCoCo**: Java Code Coverage library
- **Cobertura**: Free coverage tool for Java
- **Emma**: Code coverage for Java
- **IntelliJ IDEA**: Built-in coverage tools

### 10.2 Test Execution Strategy

1. **Unit Test Coverage**: Test each method in isolation
2. **Integration Coverage**: Test method interactions
3. **System Coverage**: Test complete workflows
4. **Regression Coverage**: Ensure new changes don't break existing functionality

### 10.3 Coverage Reporting

Generate comprehensive coverage reports including:
- **Statement Coverage**: Percentage of statements executed
- **Branch Coverage**: Percentage of branches taken
- **Method Coverage**: Percentage of methods tested
- **Class Coverage**: Percentage of classes tested

---

## 11. Conclusion

This White Box Testing analysis provides a comprehensive foundation for achieving high code coverage in the Movie Recommendation System. The analysis identifies:

1. **All executable statements** requiring test coverage
2. **All decision branches** requiring both true/false testing
3. **All conditions** requiring individual testing
4. **All execution paths** requiring traversal
5. **Potential refactoring opportunities** to improve testability

By implementing the recommended test cases and refactoring suggestions, the project can achieve comprehensive code coverage and maintainable, testable code structure.

---

*This White Box Testing analysis should be used in conjunction with the Black Box Testing documentation to ensure thorough testing of both internal and external system behavior.*
