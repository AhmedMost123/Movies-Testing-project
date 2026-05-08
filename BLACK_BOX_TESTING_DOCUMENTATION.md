# Black Box Testing Documentation
## Movie Recommendation System - Software Testing Project

### 1. Overview

This document presents comprehensive Black Box Testing scenarios for the Movie Recommendation System. Black Box Testing focuses on testing the system's functionality without knowledge of internal implementation details, based solely on requirements and specifications.

### 2. Testing Methodologies Applied

#### 2.1 Equivalence Partitioning
Dividing input data into valid and invalid partitions:
- **Valid Partitions**: Data that meets all validation rules
- **Invalid Partitions**: Data that violates specific validation rules

#### 2.2 Boundary Value Analysis
Testing at the boundaries of input domains:
- Minimum and maximum values
- Just inside/outside boundaries
- Edge cases and limits

#### 2.3 Decision Table Testing
Testing combinations of conditions and actions:
- Multiple validation rules combinations
- System behavior under different scenarios

#### 2.4 Error Guessing
Based on experience and common error patterns:
- Null values, empty strings
- Special characters
- Format violations

---

## 3. Functional Test Cases

### 3.1 Movie Data Processing

| Test Case ID | Description | Preconditions | Test Steps | Test Data | Expected Result | Actual Result | Status |
|-------------|-------------|----------------|------------|-----------|----------------|---------------|---------|
| MOV-001 | Valid single movie entry | movies.txt exists with valid format | 1. Run App with valid movie file | "The Matrix,TM123\naction" | Movie processed successfully, no error | | |
| MOV-002 | Valid multiple movie entries | movies.txt exists with multiple entries | 1. Run App with multiple movies | "Movie1,M1123\naction\nMovie2,M2456\ndrama" | All movies processed successfully | | |
| MOV-003 | Movie with valid title capitalization | movies.txt exists | 1. Run App with properly capitalized title | "The Dark Knight,TDK123\naction" | Movie processed successfully | | |
| MOV-004 | Movie with invalid title capitalization | movies.txt exists | 1. Run App with lowercase title | "the matrix,TM123\naction" | Error: "Movie Title ERROR: the matrix is wrong" | | |
| MOV-005 | Movie with valid ID format | movies.txt exists | 1. Run App with valid movie ID | "The Matrix,TM123\naction" | Movie processed successfully | | |
| MOV-006 | Movie with invalid ID letters | movies.txt exists | 1. Run App with wrong ID letters | "The Matrix,TX123\naction" | Error: "Movie Id letters ERROR: TX123 are wrong" | | |
| MOV-007 | Movie with duplicate ID digits | movies.txt exists | 1. Run App with duplicate digits | "The Matrix,TM113\naction" | Error: "Movie Id numbers ERROR: TM113 aren't unique" | | |
| MOV-008 | Movie with invalid category | movies.txt exists | 1. Run App with invalid category | "The Matrix,TM123\ninvalid" | Error: "Movie Category ERROR" | | |
| MOV-009 | Movie with duplicate categories | movies.txt exists | 1. Run App with duplicate categories | "The Matrix,TM123\naction,action" | Error: "Duplicate Category ERROR" | | |
| MOV-010 | Empty movie file | movies.txt exists but empty | 1. Run App with empty movie file | "" | System completes without errors | | |

### 3.2 User Data Processing

| Test Case ID | Description | Preconditions | Test Steps | Test Data | Expected Result | Actual Result | Status |
|-------------|-------------|----------------|------------|-----------|----------------|---------------|---------|
| USR-001 | Valid single user entry | users.txt exists with valid format | 1. Run App with valid user file | "John Doe,JD123456789\naction,drama" | User processed successfully | | |
| USR-002 | Valid multiple user entries | users.txt exists with multiple entries | 1. Run App with multiple users | "User1,U112345678\naction\nUser2,U298765432\ndrama" | All users processed successfully | | |
| USR-003 | User with valid username | users.txt exists | 1. Run App with valid username | "John Smith,JS123456789\naction" | User processed successfully | | |
| USR-004 | User with invalid username (numbers) | users.txt exists | 1. Run App with username containing numbers | "John123,JS123456789\naction" | Error: "Username ERROR: John123 is wrong" | | |
| USR-005 | User with invalid username (special chars) | users.txt exists | 1. Run App with username containing special chars | "John@Smith,JS123456789\naction" | Error: "Username ERROR: John@Smith is wrong" | | |
| USR-006 | User with valid ID (9 digits) | users.txt exists | 1. Run App with 9-digit ID | "John Doe,JD123456789\naction" | User processed successfully | | |
| USR-007 | User with valid ID (8 digits + 1 letter) | users.txt exists | 1. Run App with mixed ID | "John Doe,JD12345678A\naction" | User processed successfully | | |
| USR-008 | User with invalid ID (too short) | users.txt exists | 1. Run App with short ID | "John Doe,JD1234567\naction" | Error: "User Id ERROR: JD1234567 is wrong" | | |
| USR-009 | User with invalid ID (too long) | users.txt exists | 1. Run App with long ID | "John Doe,JD1234567890\naction" | Error: "User Id ERROR: JD1234567890 is wrong" | | |
| USR-010 | User with invalid ID (starts with letter) | users.txt exists | 1. Run App with ID starting with letter | "John Doe,AJD1234567\naction" | Error: "User Id ERROR: AJD1234567 is wrong" | | |
| USR-011 | Duplicate user IDs | users.txt exists with duplicate IDs | 1. Run App with duplicate user IDs | "User1,U112345678\naction\nUser2,U112345678\ndrama" | Second user fails validation | | |

### 3.3 Recommendation Generation

| Test Case ID | Description | Preconditions | Test Steps | Test Data | Expected Result | Actual Result | Status |
|-------------|-------------|----------------|------------|-----------|----------------|---------------|---------|
| REC-001 | User gets recommendations for liked categories | Valid movies and users exist | 1. Run App with matching categories | User likes action, action movies exist | Recommendations generated for action | | |
| REC-002 | User gets no recommendations for non-existent categories | Valid users, no matching movies | 1. Run App with non-matching categories | User likes sci-fi, no sci-fi movies | Empty recommendations | | |
| REC-003 | User gets recommendations for multiple categories | Valid movies and users exist | 1. Run App with multiple categories | User likes action,drama, both exist | Recommendations for both categories | | |
| REC-004 | User with no liked categories | Valid user with empty categories | 1. Run App with empty categories | User has no liked categories | Empty recommendations | | |
| REC-005 | Multiple users with different preferences | Valid movies and multiple users | 1. Run App with diverse preferences | Users with different category preferences | Individual recommendations for each user | | |

### 3.4 File Handling

| Test Case ID | Description | Preconditions | Test Steps | Test Data | Expected Result | Actual Result | Status |
|-------------|-------------|----------------|------------|-----------|----------------|---------------|---------|
| FILE-001 | Valid movies file format | movies.txt with correct format | 1. Run App with properly formatted file | "Movie Title,ID123\ncategory1,category2" | File processed successfully | | |
| FILE-002 | Missing movies file | movies.txt doesn't exist | 1. Run App without movies file | N/A | Error: "unable to access file" | | |
| FILE-003 | Malformed movies file (missing category line) | movies.txt with incomplete entry | 1. Run App with malformed file | "Movie Title,ID123" | System error or exception | | |
| FILE-004 | Valid users file format | users.txt with correct format | 1. Run App with properly formatted file | "User Name,ID123456789\ncategory1,category2" | File processed successfully | | |
| FILE-005 | Missing users file | users.txt doesn't exist | 1. Run App without users file | N/A | Error: "unable to access file" | | |
| FILE-006 | Output file creation | Valid processing completed | 1. Run App with valid inputs | Any valid data | Output file created with results | | |
| FILE-007 | Custom output file path | Valid processing with custom output | 1. Run App with output parameter | Valid data + custom output path | Custom output file created | | |

---

## 4. Equivalence Partitioning Analysis

### 4.1 Movie Title Validation

| Partition | Representative Values | Expected Outcome |
|-----------|---------------------|------------------|
| Valid Titles | "The Matrix", "A", "Game Of Thrones" | Accept |
| Invalid - Lowercase Start | "the matrix", "game of thrones" | Reject |
| Invalid - Numbers | "Movie 2023", "123 Movie" | Reject |
| Invalid - Special Chars | "Movie@Home", "Movie#1" | Reject |
| Invalid - Empty | "", " " | Reject |

### 4.2 Movie ID Validation

| Partition | Representative Values | Expected Outcome |
|-----------|---------------------|------------------|
| Valid IDs | "TM123", "A456", "TLTR789" | Accept |
| Invalid - Wrong Letters | "TX123", "T123" | Reject |
| Invalid - Duplicate Digits | "TM113", "A122" | Reject |
| Invalid - Too Short | "TM12", "A45" | Reject |
| Invalid - Too Long | "TM1234", "A4567" | Reject |

### 4.3 User ID Validation

| Partition | Representative Values | Expected Outcome |
|-----------|---------------------|------------------|
| Valid - 9 Digits | "123456789", "000000000" | Accept |
| Valid - 8 Digits + Letter | "12345678A", "98765432z" | Accept |
| Invalid - Too Short | "12345678", "1234567" | Reject |
| Invalid - Too Long | "1234567890", "123456789A" | Reject |
| Invalid - Starts with Letter | "A12345678", "Z98765432" | Reject |
| Invalid - Special Chars | "12345678@", "12345678#" | Reject |

### 4.4 Username Validation

| Partition | Representative Values | Expected Outcome |
|-----------|---------------------|------------------|
| Valid Names | "John", "John Doe", "Mary Jane Watson" | Accept |
| Invalid - Starts with Space | " John", "  John Doe" | Reject |
| Invalid - Contains Numbers | "John123", "John Doe 456" | Reject |
| Invalid - Special Chars | "John@Doe", "John-Doe", "O'Connor" | Reject |
| Invalid - Empty | "", " " | Reject |

---

## 5. Boundary Value Analysis

### 5.1 Movie Title Length Boundaries

| Test Case | Input | Expected Result |
|-----------|-------|-----------------|
| Minimum Length | "A" | Valid |
| Minimum Length - 1 | "" | Invalid |
| Maximum Practical | "A".repeat(1000) | Valid (system dependent) |
| Single Word | "Matrix" | Valid |
| Multiple Words | "The Lord Of The Rings" | Valid |

### 5.2 User ID Length Boundaries

| Test Case | Input | Expected Result |
|-----------|-------|-----------------|
| Valid Minimum | "123456789" | Valid |
| Invalid Minimum - 1 | "12345678" | Invalid |
| Valid Maximum | "12345678A" | Valid |
| Invalid Maximum + 1 | "123456789A" | Invalid |

### 5.3 Movie ID Digit Boundaries

| Test Case | Input | Expected Result |
|-----------|-------|-----------------|
| Unique Digits | "TM123" | Valid |
| Duplicate Digits | "TM113" | Invalid |
| All Same Digits | "TM111" | Invalid |
| Sequential Digits | "TM123" | Valid |

---

## 6. Decision Table Testing

### 6.1 Movie Validation Decision Table

| Rule | Valid Title | Valid ID Letters | Unique Digits | Valid Categories | No Duplicates | Expected Result |
|------|-------------|------------------|---------------|------------------|---------------|-----------------|
| 1 | Y | Y | Y | Y | Y | Success |
| 2 | N | Y | Y | Y | Y | Title Error |
| 3 | Y | N | Y | Y | Y | ID Letters Error |
| 4 | Y | Y | N | Y | Y | ID Numbers Error |
| 5 | Y | Y | Y | N | Y | Category Error |
| 6 | Y | Y | Y | Y | N | Duplicate Category Error |

### 6.2 User Validation Decision Table

| Rule | Valid Username | Valid ID Format | Unique ID | Expected Result |
|------|----------------|-----------------|-----------|-----------------|
| 1 | Y | Y | Y | Success |
| 2 | N | Y | Y | Username Error |
| 3 | Y | N | Y | User ID Error |
| 4 | Y | Y | N | User ID Error (duplicate) |

---

## 7. Error Guessing Test Cases

### 7.1 Common Error Scenarios

| Test Case | Description | Rationale |
|-----------|-------------|-----------|
| NULL-001 | Null values in input fields | Common null pointer issues |
| EMPTY-001 | Empty strings in all fields | Boundary condition |
| SPACE-001 | Strings with only spaces | Often overlooked validation |
| SPECIAL-001 | Unicode and special characters | Encoding issues |
| FORMAT-001 | Mixed line endings (\n, \r\n) | Cross-platform compatibility |
| LARGE-001 | Extremely long input strings | Buffer overflow/memory issues |
| MALFORMED-001 | Incomplete file structures | File corruption scenarios |

### 7.2 System Integration Errors

| Test Case | Description | Expected Behavior |
|-----------|-------------|-------------------|
| SYS-001 | No command line arguments | Display usage message |
| SYS-002 | Insufficient arguments | Display error message |
| SYS-003 | File permission issues | Graceful error handling |
| SYS-004 | Disk space full | Appropriate error message |
| SYS-005 | Concurrent file access | Handle file locking |

---

## 8. Test Execution Strategy

### 8.1 Test Execution Order

1. **File Access Tests** - Verify basic file handling
2. **Data Validation Tests** - Test individual validation rules
3. **Integration Tests** - Test complete workflows
4. **Error Handling Tests** - Verify error conditions
5. **Performance Tests** - Check system responsiveness
6. **Recovery Tests** - Test system recovery from errors

### 8.2 Test Data Management

- **Positive Test Data**: Valid inputs covering all valid scenarios
- **Negative Test Data**: Invalid inputs covering error conditions
- **Boundary Test Data**: Edge cases and limit values
- **Special Test Data**: Unicode, special characters, large datasets

### 8.3 Test Environment Setup

- **Test Files**: Pre-configured test data files
- **Output Directory**: Dedicated location for test outputs
- **Log Files**: Capture system behavior during testing
- **Backup Strategy**: Preserve original test data

---

## 9. Expected Outcomes and Success Criteria

### 9.1 Functional Requirements

- ✅ All valid data processed correctly
- ✅ All invalid data rejected with appropriate error messages
- ✅ System stops at first error encountered
- ✅ Output files generated correctly
- ✅ Recommendations generated based on valid data

### 9.2 Non-Functional Requirements

- ✅ System handles various file sizes
- ✅ Error messages are clear and informative
- ✅ System recovers gracefully from errors
- ✅ Performance acceptable for expected data volumes

### 9.3 Quality Metrics

- **Test Coverage**: All functional requirements tested
- **Defect Detection**: All known error conditions identified
- **Usability**: Error messages user-friendly
- **Reliability**: System behaves consistently

---

## 10. Test Summary and Recommendations

### 10.1 Test Coverage Summary

- **Total Test Cases**: 50+ comprehensive test scenarios
- **Coverage Areas**: Movie validation, User validation, File handling, Recommendations
- **Testing Techniques**: Equivalence partitioning, Boundary analysis, Decision tables, Error guessing

### 10.2 Risk Assessment

| Risk Level | Area | Mitigation Strategy |
|------------|------|-------------------|
| High | File corruption | Robust error handling and validation |
| Medium | Data format changes | Flexible parsing with clear error messages |
| Low | Performance issues | Efficient algorithms and data structures |

### 10.3 Recommendations

1. **Implement comprehensive input validation**
2. **Add detailed logging for debugging**
3. **Create automated test suite for regression testing**
4. **Implement graceful error recovery**
5. **Add performance monitoring for large datasets**

---

*This Black Box Testing documentation provides a comprehensive foundation for testing the Movie Recommendation System. All test cases should be executed systematically, and results should be documented for quality assurance purposes.*
