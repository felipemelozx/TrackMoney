package fun.trackmoney.budget.exception;

import fun.trackmoney.utils.CustomFieldError;

import java.util.ArrayList;
import java.util.List;

public class BudgetsNotFoundException extends RuntimeException {
  private final List<CustomFieldError> errors = new ArrayList<>();

  public BudgetsNotFoundException(String message) {
    super(message);
    this.errors.add(new CustomFieldError("Budgets", message));
  }

  public BudgetsNotFoundException(List<CustomFieldError> errors) {
    super("Budgets not found!");
    this.errors.addAll(errors);
  }

  public List<CustomFieldError> getErrors() {
    return errors;
  }
}
