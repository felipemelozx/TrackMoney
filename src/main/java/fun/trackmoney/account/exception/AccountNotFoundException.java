package fun.trackmoney.account.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class AccountNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors;

  public AccountNotFoundException(String message) {
    super(message);
    this.errors = new ArrayList<>();
    this.errors.add(new CustomFieldError("Email: ", message));
  }

  public AccountNotFoundException(List<CustomFieldError> errors) {
    super("Email not found!");
    this.errors = errors;
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
