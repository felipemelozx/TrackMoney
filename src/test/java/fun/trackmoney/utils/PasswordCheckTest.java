package fun.trackmoney.utils;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordCheckTest {

  private static final String FIELD_PASSWORD = "Password";
  private static final String ERROR_LOWERCASE = "Password must contain at least one lowercase letter.";
  private static final String ERROR_UPPERCASE = "Password must contain at least one uppercase letter.";
  private static final String ERROR_DIGIT = "Password must contain at least one number.";
  private static final String ERROR_SPECIAL = "Password must contain at least one special character.";
  private static final String ERROR_LENGTH = "Password must be at least 8 characters long.";

  private static final String VALID_PASSWORD = "StrongPwd123!";
  private static final String ONLY_UPPERCASE = "UPPER";
  private static final String ONLY_LOWERCASE = "lower";
  private static final String ONLY_DIGITS = "12345";
  private static final String ONLY_SPECIAL = "!@#$%";
  private static final String MISSING_LOWERCASE = "STRONGPWD123!";
  private static final String MISSING_UPPERCASE = "strongpwd123!";
  private static final String MISSING_DIGIT = "StrongPwd!";
  private static final String MISSING_SPECIAL = "StrongPwd123";
  private static final String TOO_SHORT = "Str1!";
  private static final String LOWER_UPPER_SHORT = "lowerUPPER";
  private static final String LOWER_DIGIT = "lower123";
  private static final String LOWER_SPECIAL = "lower!";
  private static final String UPPER_DIGIT = "UPPER123";
  private static final String UPPER_SPECIAL = "UPPER!";
  private static final String DIGIT_SPECIAL = "123!";

  @Test
  void validatePasswordEmail_shouldPassForValidPassword() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(VALID_PASSWORD);
    assertTrue(errors.isEmpty(), "Should not have errors for a valid password");
  }

  @Test
  void validatePasswordEmail_shouldFailForMissingLowercase() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(MISSING_LOWERCASE);
    assertEquals(1, errors.size(), "Should have one error for missing lowercase");
    assertEquals(FIELD_PASSWORD, errors.get(0).getField());
    assertEquals(ERROR_LOWERCASE, errors.get(0).getMessage());
  }

  @Test
  void validatePasswordEmail_shouldFailForMissingUppercase() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(MISSING_UPPERCASE);
    assertEquals(1, errors.size(), "Should have one error for missing uppercase");
    assertEquals(FIELD_PASSWORD, errors.get(0).getField());
    assertEquals(ERROR_UPPERCASE, errors.get(0).getMessage());
  }

  @Test
  void validatePasswordEmail_shouldFailForMissingDigit() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(MISSING_DIGIT);
    assertEquals(1, errors.size(), "Should have one error for missing digit");
    assertEquals(FIELD_PASSWORD, errors.get(0).getField());
    assertEquals(ERROR_DIGIT, errors.get(0).getMessage());
  }

  @Test
  void validatePasswordEmail_shouldFailForMissingSpecialCharacter() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(MISSING_SPECIAL);
    assertEquals(1, errors.size(), "Should have one error for missing special character");
    assertEquals(FIELD_PASSWORD, errors.get(0).getField());
    assertEquals(ERROR_SPECIAL, errors.get(0).getMessage());
  }

  @Test
  void validatePasswordEmail_shouldFailForTooShortPassword() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(TOO_SHORT);
    assertEquals(1, errors.size(), "Should have one error for too short password");
    assertEquals(FIELD_PASSWORD, errors.get(0).getField());
    assertEquals(ERROR_LENGTH, errors.get(0).getMessage());
  }

  @Test
  void validatePasswordEmail_shouldFailForMultipleMissingCriteria() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(ONLY_LOWERCASE);
    assertEquals(4, errors.size(), "Should have four errors for multiple missing criteria");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_UPPERCASE)), "Missing uppercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_DIGIT)), "Missing digit error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_SPECIAL)), "Missing special character error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LENGTH)), "Too short error");
    assertTrue(errors.stream().allMatch(e -> e.getField().equals(FIELD_PASSWORD)), "All errors should be for the Password field");
  }

  @Test
  void validatePasswordEmail_shouldFailForOnlyUppercaseAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(ONLY_UPPERCASE);
    assertEquals(4, errors.size(), "Should have four errors");
  }

  @Test
  void validatePasswordEmail_shouldFailForOnlyDigitsAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(ONLY_DIGITS);
    assertEquals(4, errors.size(), "Should have four errors");
  }

  @Test
  void validatePasswordEmail_shouldFailForOnlySpecialCharsAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(ONLY_SPECIAL);
    assertEquals(4, errors.size(), "Should have four errors");
  }

  @Test
  void validatePasswordEmail_shouldFailForLowercaseAndUppercaseOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(LOWER_UPPER_SHORT);
    assertEquals(2, errors.size(), "Should have two errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_DIGIT)), "Missing digit error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_SPECIAL)), "Missing special character error");
  }

  @Test
  void validatePasswordEmail_shouldFailForLowercaseAndDigitOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(LOWER_DIGIT);
    assertEquals(2, errors.size(), "Should have two errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_UPPERCASE)), "Missing uppercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_SPECIAL)), "Missing special character error");
  }

  @Test
  void validatePasswordEmail_shouldFailForLowercaseAndSpecialOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(LOWER_SPECIAL);
    assertEquals(3, errors.size(), "Should have three errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_UPPERCASE)), "Missing uppercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_DIGIT)), "Missing digit error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LENGTH)), "Too short error");
  }

  @Test
  void validatePasswordEmail_shouldFailForUppercaseAndDigitOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(UPPER_DIGIT);
    assertEquals(2, errors.size(), "Should have two errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LOWERCASE)), "Missing lowercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_SPECIAL)), "Missing special character error");
  }

  @Test
  void validatePasswordEmail_shouldFailForUppercaseAndSpecialOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(UPPER_SPECIAL);
    assertEquals(3, errors.size(), "Should have three errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LOWERCASE)), "Missing lowercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_DIGIT)), "Missing digit error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LENGTH)), "Too short error");
  }

  @Test
  void validatePasswordEmail_shouldFailForDigitAndSpecialOnlyAndShort() {
    List<CustomFieldError> errors = PasswordCheck.validatePassword(DIGIT_SPECIAL);
    assertEquals(3, errors.size(), "Should have three errors");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LOWERCASE)), "Missing lowercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_UPPERCASE)), "Missing uppercase error");
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(ERROR_LENGTH)), "Too short error");
  }
}
