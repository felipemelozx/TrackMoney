package fun.trackmoney.utils;

/**
 * Represents a custom error associated with a specific field.
 */
public class CustomFieldError {
  private String field;
  private String message;

  /**
   * Constructs an empty CustomFieldError.
   */
  public CustomFieldError() {}

  /**
   * Constructs a CustomFieldError with the specified field and message.
   *
   * @param field the name of the field with the error
   * @param message the error message describing the issue
   */
  public CustomFieldError(String field, String message) {
    this.field = field;
    this.message = message;
  }

  /**
   * Returns the name of the field with the error.
   *
   * @return the field name
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the name of the field with the error.
   *
   * @param field the field name to set
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * Returns the error message describing the issue.
   *
   * @return the error message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the error message describing the issue.
   *
   * @param message the error message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}