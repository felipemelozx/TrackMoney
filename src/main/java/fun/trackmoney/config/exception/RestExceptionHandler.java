package fun.trackmoney.config.exception;

import fun.trackmoney.exception.AccountHasRelatedDataException;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.exception.BudgetsNotFoundException;
import fun.trackmoney.exception.CategoryNotFoundException;
import fun.trackmoney.exception.InvalidAccountRequestException;
import fun.trackmoney.exception.UserNotFoundException;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import fun.trackmoney.utils.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> details = new LinkedHashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String field = error instanceof FieldError fieldError ? fieldError.getField() : "object";
      details.put(field, error.getDefaultMessage());
    });
    return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiResponse<Void>> handlePathVariableValidationExceptions(
      HandlerMethodValidationException ex) {
    Map<String, String> details = new LinkedHashMap<>();
    ex.getAllErrors().forEach(error -> {
      String field = error instanceof FieldError fieldError ? fieldError.getField() : "param";
      details.put(field, error.getDefaultMessage());
    });
    return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> emailNotFound(UserNotFoundException ex) {
    return errorResponse(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", ex.getMessage(), toDetails(ex.getErrors()));
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> categoryNotFound(CategoryNotFoundException ex) {
    return errorResponse(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", ex.getMessage(),
        Map.of("Category", ex.getMessage()));
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> accountNotFound(AccountNotFoundException ex) {
    return errorResponse(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage(), toDetails(ex.getErrors()));
  }

  @ExceptionHandler(AccountHasRelatedDataException.class)
  public ResponseEntity<ApiResponse<Void>> accountHasRelatedData(AccountHasRelatedDataException ex) {
    return errorResponse(HttpStatus.CONFLICT, "ACCOUNT_HAS_RELATED_DATA", ex.getMessage(),
        Map.of("Account", ex.getMessage()));
  }

  @ExceptionHandler(InvalidAccountRequestException.class)
  public ResponseEntity<ApiResponse<Void>> invalidAccountRequest(InvalidAccountRequestException ex) {
    return errorResponse(HttpStatus.BAD_REQUEST, "INVALID_ACCOUNT_REQUEST", ex.getMessage(),
        Map.of("Account", ex.getMessage()));
  }

  @ExceptionHandler(BudgetsNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> budgetsNotFound(BudgetsNotFoundException ex) {
    return errorResponse(HttpStatus.NOT_FOUND, "BUDGET_NOT_FOUND", ex.getMessage(), toDetails(ex.getErrors()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> unexpectedException(Exception ex) {
    LOGGER.error("Unexpected error while processing request", ex);
    return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
        "An unexpected error occurred.", null);
  }

  private ResponseEntity<ApiResponse<Void>> errorResponse(HttpStatus status, String code, String message,
                                                           Map<String, String> details) {
    return ResponseEntity.status(status)
        .body(ApiResponse.error(new ErrorResponse(code, message, details)));
  }

  private Map<String, String> toDetails(List<CustomFieldError> errors) {
    Map<String, String> details = new LinkedHashMap<>();
    errors.forEach(error -> details.put(error.getField(), error.getMessage()));
    return details.isEmpty() ? null : details;
  }
}
