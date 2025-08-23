package fun.trackmoney.utils.response;

import fun.trackmoney.utils.CustomFieldError;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {

  private boolean success;
  private String message;
  private final T data;
  private final List<CustomFieldError> errors;
  private final LocalDateTime timestamp;

  private ApiResponse(Builder<T> builder) {
    this.success = builder.success;
    this.message = builder.message;
    this.data = builder.data;
    this.errors = builder.errors;
    this.timestamp = LocalDateTime.now();
  }

  public boolean isSuccess() { return success; }
  public String getMessage() { return message; }
  public T getData() { return data; }
  public List<CustomFieldError> getErrors() { return errors; }
  public LocalDateTime getTimestamp() { return timestamp; }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static <T> Builder<T> success() {
    return new Builder<T>().success(true).errors(Collections.emptyList());
  }

  public static <T> Builder<T> successWithNoContent() {
    return new Builder<T>() .success(true)
        .data(null)
        .message("Operação realizada com sucesso.")
        .errors(Collections.emptyList());
  }

  public static <T> Builder<T> failure() {
    return new Builder<T>().success(false);
  }

  public static class Builder<T> {
    private boolean success;
    private String message;
    private T data;
    private List<CustomFieldError> errors;

    public Builder<T> success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder<T> message(String message) {
      this.message = message;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public Builder<T> errors(List<CustomFieldError> errors) {
      this.errors = errors;
      return this;
    }

    public ApiResponse<T> build() {
      return new ApiResponse<>(this);
    }
  }
}
