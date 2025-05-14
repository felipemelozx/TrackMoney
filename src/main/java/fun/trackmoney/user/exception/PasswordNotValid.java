package fun.trackmoney.user.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class PasswordNotValid extends RuntimeException {
  private final List<CustomFieldError> errors;

  public PasswordNotValid(String message) {
    super(message);
    this.errors = new ArrayList<>();
    this.errors.add(new CustomFieldError("Error: ", message));
  }

  public PasswordNotValid(List<CustomFieldError> errors) {
    super("Password is invalid");
    this.errors = errors;
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
