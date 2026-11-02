package fun.trackmoney.utils.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fun.trackmoney.utils.CustomFieldError;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Uniform HTTP envelope: success/data/error/timestamp. */
public final class ApiResponse<T> {

  private final boolean success;
  private final T data;
  private final ErrorResponse error;
  private final Instant timestamp;
  private final String compatibilityMessage;
  private final List<CustomFieldError> compatibilityErrors;

  private ApiResponse(boolean success, T data, ErrorResponse error, String compatibilityMessage,
                      List<CustomFieldError> compatibilityErrors) {
    this.success = success;
    this.data = data;
    this.error = error;
    this.compatibilityMessage = compatibilityMessage;
    this.compatibilityErrors = compatibilityErrors;
    this.timestamp = Instant.now();
  }

  public boolean isSuccess() {
    return success;
  }

  public T getData() {
    return data;
  }

  public ErrorResponse getError() {
    return error;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  /** Compatibility accessor for code/tests transitioning from the old envelope. */
  @JsonIgnore
  public String getMessage() {
    return error == null ? compatibilityMessage : error.message();
  }

  /** Compatibility accessor; serialized errors now live in ErrorResponse.details. */
  @JsonIgnore
  public List<CustomFieldError> getErrors() {
    if (compatibilityErrors != null) {
      return compatibilityErrors;
    }
    if (error == null || error.details() == null) {
      return Collections.emptyList();
    }
    return error.details().entrySet().stream()
        .map(entry -> new CustomFieldError(entry.getKey(), entry.getValue()))
        .toList();
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null, null, null);
  }

  public static <T> ApiResponse<T> error(ErrorResponse error) {
    return new ApiResponse<>(false, null, error, null, null);
  }

  /* Transitional builder keeps existing non-Result controllers source-compatible. */
  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static <T> Builder<T> success() {
    return new Builder<T>().success(true);
  }

  public static <T> Builder<T> successWithNoContent() {
    return new Builder<T>().success(true).message("Operação realizada com sucesso.");
  }

  public static <T> Builder<T> failure() {
    return new Builder<T>().success(false);
  }

  public static class Builder<T> {
    private boolean success;
    private T data;
    private String message;
    private List<CustomFieldError> errors = Collections.emptyList();
    private ErrorResponse error;

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
      this.errors = errors == null ? Collections.emptyList() : errors;
      return this;
    }

    public Builder<T> errors(CustomFieldError error) {
      this.errors = List.of(error);
      return this;
    }

    public Builder<T> error(ErrorResponse error) {
      this.error = error;
      return this;
    }

    public ApiResponse<T> build() {
      if (success) {
        return new ApiResponse<>(true, data, null, message, errors);
      }
      ErrorResponse failure = error == null
          ? new ErrorResponse("REQUEST_ERROR", message, toDetails(errors))
          : error;
      return new ApiResponse<>(false, null, failure, message, errors);
    }

    private Map<String, String> toDetails(List<CustomFieldError> fieldErrors) {
      if (fieldErrors.isEmpty()) {
        return null;
      }
      Map<String, String> details = new LinkedHashMap<>();
      fieldErrors.forEach(fieldError -> details.put(fieldError.getField(), fieldError.getMessage()));
      return details;
    }
  }
}
