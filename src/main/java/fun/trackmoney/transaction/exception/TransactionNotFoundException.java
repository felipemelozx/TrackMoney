package fun.trackmoney.transaction.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class TransactionNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors = new ArrayList<>();

  public TransactionNotFoundException(String message) {
    super(message);
    this.errors.add(new CustomFieldError("Transaction", message));
  }

  public TransactionNotFoundException(String message, List<CustomFieldError> errors) {
    super(message);
    this.errors.addAll(errors);
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}

