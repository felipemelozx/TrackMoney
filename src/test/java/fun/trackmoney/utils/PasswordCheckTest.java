package fun.trackmoney.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordCheckTest {

  @Test
  void shouldMatchLowercase_whenStringContainsLowercase() {
    assertTrue(PasswordCheck.LOWERCASE_PATTERN.matcher("abc").matches());
  }

  @Test
  void shouldNotMatchLowercase_whenStringDoesNotContainLowercase() {
    assertFalse(PasswordCheck.LOWERCASE_PATTERN.matcher("ABC").matches());
  }

  @Test
  void shouldMatchUppercase_whenStringContainsUppercase() {
    assertTrue(PasswordCheck.UPPERCASE_PATTERN.matcher("ABC").matches());
  }

  @Test
  void shouldNotMatchUppercase_whenStringDoesNotContainUppercase() {
    assertFalse(PasswordCheck.UPPERCASE_PATTERN.matcher("abc").matches());
  }

  @Test
  void shouldMatchDigit_whenStringContainsDigit() {
    assertTrue(PasswordCheck.DIGIT_PATTERN.matcher("123").matches());
  }

  @Test
  void shouldNotMatchDigit_whenStringDoesNotContainDigit() {
    assertFalse(PasswordCheck.DIGIT_PATTERN.matcher("abc").matches());
  }

  @Test
  void shouldMatchSpecialChar_whenStringContainsSpecialChar() {
    assertTrue(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("@!#").matches());
  }

  @Test
  void shouldNotMatchSpecialChar_whenStringDoesNotContainSpecialChar() {
    assertFalse(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("abc123").matches());
  }

  @Test
  void shouldMatchLength_whenStringLengthIsValid() {
    assertTrue(PasswordCheck.LENGTH_PATTERN.matcher("abcdefgh").matches());
  }

  @Test
  void shouldNotMatchLength_whenStringLengthIsInvalid() {
    assertFalse(PasswordCheck.LENGTH_PATTERN.matcher("abc").matches());
  }
}
