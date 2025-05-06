package fun.trackmoney.utils.response;

import fun.trackmoney.utils.CustomFieldError;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A class representing a standardized API response.
 *
 * @param <T> The type of data included in the response.
 */
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private List<CustomFieldError> errors;
  private LocalDateTime timestamp;

  /**
   * Default constructor. Initializes the timestamp to the current time.
   */
  public ApiResponse() {
    this.timestamp = LocalDateTime.now();
  }

  /**
   * Constructor to initialize all fields of the response.
   *
   * @param success  Indicates whether the operation was successful or not.
   * @param message  A message describing the result of the operation.
   * @param data     The data returned by the operation.
   * @param errors   A list of errors encountered during the operation, if any.
   */
  public ApiResponse(boolean success, String message, T data, List<CustomFieldError> errors) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.errors = errors;
    this.timestamp = LocalDateTime.now();
  }

  /**
   * Gets the success status of the response.
   *
   * @return true if the operation was successful, false otherwise.
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the success status of the response.
   *
   * @param success true if the operation was successful, false otherwise.
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Gets the message of the response.
   *
   * @return A message describing the result of the operation.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message of the response.
   *
   * @param message A message describing the result of the operation.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the data of the response.
   *
   * @return The data returned by the operation.
   */
  public T getData() {
    return data;
  }

  /**
   * Sets the data of the response.
   *
   * @param data The data to be included in the response.
   */
  public void setData(T data) {
    this.data = data;
  }

  /**
   * Gets the list of errors encountered during the operation, if any.
   *
   * @return A list of {@link CustomFieldError} objects representing the errors.
   */
  public List<CustomFieldError> getErrors() {
    return errors;
  }

  /**
   * Sets the list of errors for the response.
   *
   * @param errors A list of {@link CustomFieldError} objects representing the errors.
   */
  public void setErrors(List<CustomFieldError> errors) {
    this.errors = errors;
  }

  /**
   * Gets the timestamp of when the response was created.
   *
   * @return The timestamp indicating when the response was generated.
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp for the response.
   *
   * @param timestamp The timestamp to set.
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
