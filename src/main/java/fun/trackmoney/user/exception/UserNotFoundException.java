package fun.trackmoney.user.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class UserNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors = new ArrayList<>();

  public UserNotFoundException(String message) {
    super(message);
    this.errors.add(new CustomFieldError("Email: ", message));
  }

  public UserNotFoundException(String message, List<CustomFieldError> errors) {
    super(message);
    this.errors.addAll(errors);
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
