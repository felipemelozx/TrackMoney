package fun.trackmoney.auth.dto.internal;

public enum AuthError {
  EMAIL_NOT_VERIFIED("User email has not been verified."),
  INVALID_CREDENTIALS("Invalid credentials, please check and try again."),
  REFRESH_TOKEN_INVALID("The refresh token is invalid or has expired."),
  USER_NOT_REGISTER("User is not registered in the system."),
  EMAIL_ALREADY_EXISTS("The provided email is already registered."),
  USER_IS_VERIFIED("The user has already been verified.");

  private final String message;

  AuthError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
