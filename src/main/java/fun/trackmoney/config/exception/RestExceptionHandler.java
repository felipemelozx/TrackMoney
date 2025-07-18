package fun.trackmoney.config.exception;

import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.auth.exception.LoginException;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.category.exception.CategoryNotFoundException;
import fun.trackmoney.goal.exception.GoalsNotFoundException;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.user.exception.EmailAlreadyExistsException;
import fun.trackmoney.user.exception.UserNotFoundException;
import fun.trackmoney.user.exception.PasswordNotValid;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(PasswordNotValid.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> passwordNotValid(PasswordNotValid ex) {
    return ResponseEntity.badRequest().body(
        new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
    );
  }

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
        new ApiResponse<>(false, "Validation failed", null, errors),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ApiResponse<>(false, ex.getMessage(), null,
            List.of(new CustomFieldError("Email", ex.getMessage()))));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> emailNotFound(UserNotFoundException ex) {
    return ResponseEntity.badRequest().body(
        new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
    );
  }

  @ExceptionHandler(LoginException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> loginException(LoginException ex) {
    return ResponseEntity.badRequest().body(
        new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
    );
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> categoryNotFound(CategoryNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(false, ex.getMessage(), null,
            List.of(new CustomFieldError("Category", ex.getMessage()))));
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> accountNotFound(AccountNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
    );
  }

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> transactionNotFound(TransactionNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
        );
  }

  @ExceptionHandler(GoalsNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> goalsNotFound(GoalsNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
        );
  }

  @ExceptionHandler(BudgetsNotFoundException.class)
  public ResponseEntity<ApiResponse<List<CustomFieldError>>> budgetsNotFound(BudgetsNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(false, ex.getMessage(), null, ex.getErrors())
        );
  }
}