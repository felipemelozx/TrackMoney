package fun.trackmoney.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PasswordCheck {

  private PasswordCheck() { }

  private static final String LOWERCASE_REGEX = "(?=.*[a-z])";
  private static final String UPPERCASE_REGEX = "(?=.*[A-Z])";
  private static final String DIGIT_REGEX = "(?=.*\\d)";
  private static final String SPECIAL_CHAR_REGEX = "(?=.*[@#$%^&+=!])";
  private static final String LENGTH_REGEX = ".{8,}";

  private static final String LOWERCASE_ERROR = "Password must contain at least one lowercase letter.";
  private static final String UPPERCASE_ERROR = "Password must contain at least one uppercase letter.";
  private static final String DIGIT_ERROR = "Password must contain at least one number.";
  private static final String SPECIAL_CHAR_ERROR = "Password must contain at least one special character.";
  private static final String LENGTH_ERROR = "Password must be at least 8 characters long.";
  private static final String FIELD = "Password";

  public static List<CustomFieldError> validatePassword(String password) {
    List<CustomFieldError> errors = new ArrayList<>();
    if (!Pattern.compile(LOWERCASE_REGEX).matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, LOWERCASE_ERROR));
    }
    if (!Pattern.compile(UPPERCASE_REGEX).matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, UPPERCASE_ERROR));
    }
    if (!Pattern.compile(DIGIT_REGEX).matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, DIGIT_ERROR));
    }
    if (!Pattern.compile(SPECIAL_CHAR_REGEX).matcher(password).find()) {
      errors.add(new CustomFieldError(FIELD, SPECIAL_CHAR_ERROR));
    }
    if (!Pattern.compile(LENGTH_REGEX).matcher(password).matches()) {
      errors.add(new CustomFieldError(FIELD, LENGTH_ERROR));
    }
    return errors;
  }
}
