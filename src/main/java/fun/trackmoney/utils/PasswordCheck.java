package fun.trackmoney.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PasswordCheck {

  private PasswordCheck() { }

  private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
  private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
  private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
  private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@#$%^&+=!].*");
  private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");

  private static final String LOWERCASE_ERROR = "Password must contain at least one lowercase letter.";
  private static final String UPPERCASE_ERROR = "Password must contain at least one uppercase letter.";
  private static final String DIGIT_ERROR = "Password must contain at least one number.";
  private static final String SPECIAL_CHAR_ERROR = "Password must contain at least one special character.";
  private static final String LENGTH_ERROR = "Password must be at least 8 characters long.";
  private static final String FIELD = "Password";

  public static List<CustomFieldError> validatePassword(String password) {
    List<CustomFieldError> errors = new ArrayList<>();
    if (!LOWERCASE_PATTERN.matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, LOWERCASE_ERROR));
    }
    if (!UPPERCASE_PATTERN.matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, UPPERCASE_ERROR));
    }
    if (!DIGIT_PATTERN.matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, DIGIT_ERROR));
    }
    if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, SPECIAL_CHAR_ERROR));
    }
    if (!LENGTH_PATTERN.matcher(password).matches()) {
      errors.add(new CustomFieldError(FIELD, LENGTH_ERROR));
    }
    return errors;
  }
}