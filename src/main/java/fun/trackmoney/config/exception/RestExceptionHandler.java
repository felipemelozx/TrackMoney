package fun.trackmoney.config.exception;

import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.category.exception.CategoryNotFoundException;
import fun.trackmoney.goal.exception.GoalsNotFoundException;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.user.exception.UserNotFoundException;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> handleValidationExceptions(
                                                                  MethodArgumentNotValidException ex) {
    List<CustomFieldError> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof FieldError fieldError) {
            return new CustomFieldError(fieldError.getField(), fieldError.getDefaultMessage());
          } else {
             return new CustomFieldError("object", error.getDefaultMessage());
          }
        }).toList();

    return new ResponseEntity<>(
        ApiResponse.<List<CustomFieldError>>failure()
            .message("Validation failed")
            .errors(errors)
            .build(),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> handlePathVariableValidationExceptions(
      HandlerMethodValidationException ex) {

    List<CustomFieldError> errors = ex.getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof FieldError fieldError) {
            return new CustomFieldError(fieldError.getField(), fieldError.getDefaultMessage());
          } else {
            return new CustomFieldError("param", error.getDefaultMessage());
          }
        })
        .toList();

    return new ResponseEntity<>(
        ApiResponse.<List<CustomFieldError>>failure()
            .message("Validation failed")
            .errors(errors)
            .build(),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> emailNotFound(UserNotFoundException ex) {
    return ResponseEntity.badRequest().body(
        ApiResponse.<List<CustomFieldError>>failure()
            .message(ex.getMessage())
            .errors(ex.getErrors())
            .build()
    );
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> categoryNotFound(CategoryNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.<List<CustomFieldError>>failure()
                .message(ex.getMessage())
                .errors(List.of(new CustomFieldError("Category", ex.getMessage())))
                .build()
        );
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> accountNotFound(AccountNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.<List<CustomFieldError>>failure()
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build()
        );
  }

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> transactionNotFound(TransactionNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.<List<CustomFieldError>>failure()
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build()
        );
  }

  @ExceptionHandler(GoalsNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> goalsNotFound(GoalsNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.<List<CustomFieldError>>failure()
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build()
        );
  }

  @ExceptionHandler(BudgetsNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> budgetsNotFound(BudgetsNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.<List<CustomFieldError>>failure()
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build()
        );
  }
}