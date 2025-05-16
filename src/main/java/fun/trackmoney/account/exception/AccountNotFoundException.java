package fun.trackmoney.account.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class AccountNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors = new ArrayList<>();

  public AccountNotFoundException(String message) {
    super(message);
    this.errors.add(new CustomFieldError("Account", message));
  }

  public AccountNotFoundException(List<CustomFieldError> errors) {
    super("Account not found!");
    this.errors.addAll(errors);
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
