package fun.trackmoney.auth.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class LoginException extends RuntimeException {
  private final List<CustomFieldError> errors;

  public LoginException(String message) {
    super(message);
    this.errors = new ArrayList<>();
    this.errors.add(new CustomFieldError("Email: ", message));
  }

  public LoginException(List<CustomFieldError> errors) {
    super("Email not found!");
    this.errors = errors;
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
