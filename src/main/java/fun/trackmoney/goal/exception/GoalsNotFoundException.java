package fun.trackmoney.goal.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class GoalsNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors = new ArrayList<>();

  public GoalsNotFoundException(String message) {
    super(message);
    this.errors.add(new CustomFieldError("Goal", message));
  }

  public GoalsNotFoundException(List<CustomFieldError> errors) {
    super("Account not found!");
    this.errors.addAll(errors);
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
