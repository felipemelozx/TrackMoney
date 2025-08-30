package fun.trackmoney.utils;

import java.util.regex.Pattern;

public abstract class PasswordCheck {

  private PasswordCheck() { }

  static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
  static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
  static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
  static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@#$%^&+=!].*");
  static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
}