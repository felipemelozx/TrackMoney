package fun.trackmoney.common.result;

/** Business errors shared by services; HTTP concerns belong to HttpErrorMapper. */
public enum ErrorCode {
  POT_NOT_FOUND("Pot not found", "id"),
  POT_INCOME_EXCEEDS_TARGET("Money is greater than target amount!", "Money"),
  POT_EXPENSE_EXCEEDS_BALANCE("Money is greater than current amount!", "Money"),
  POT_TARGET_BELOW_CURRENT("Target amount cannot be less than current amount!", "targetAmount"),
  BUDGET_ALREADY_EXISTS("A budget already exists for this category.", "category"),
  BUDGET_CREATE_CATEGORY_NOT_FOUND("Category not found.", "categoryId"),
  BUDGET_CATEGORY_NOT_FOUND("Category not found.", "categoryId"),
  BUDGET_PERCENT_LIMIT_EXCEEDED("Budget percentage limit exceeded.", "percent"),
  BUDGET_NOT_FOUND("Budget not found.", "id"),
  TRANSACTION_CATEGORY_NOT_FOUND("Category not found.", "Category"),
  TRANSACTION_ACCOUNT_NOT_FOUND("Account not found to update balance", "Account"),
  USER_EMAIL_ALREADY_EXISTS("The provided email is already registered.", "Email"),
  USER_NOT_REGISTERED("User is not registered in the system.", "Email"),
  AUTH_INVALID_CREDENTIALS("Invalid credentials, please check and try again.", "Credentials"),
  AUTH_EMAIL_NOT_VERIFIED("User email has not been verified.", "User"),
  AUTH_REFRESH_TOKEN_INVALID("The refresh token is invalid or has expired.", "User"),
  AUTH_USER_ALREADY_VERIFIED("The user has already been verified.", "User"),
  AUTH_ERROR_SENDING_EMAIL("Error sending email.", "User");

  private final String defaultMessage;
  private final String defaultField;

  ErrorCode(String defaultMessage, String defaultField) {
    this.defaultMessage = defaultMessage;
    this.defaultField = defaultField;
  }

  public String defaultMessage() {
    return defaultMessage;
  }

  public String defaultField() {
    return defaultField;
  }
}
