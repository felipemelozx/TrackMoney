package fun.trackmoney.config.exception;

import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.category.exception.CategoryNotFoundException;
import fun.trackmoney.goal.exception.GoalsNotFoundException;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.user.exception.UserNotFoundException;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {
  @Spy
  private final RestExceptionHandler restExceptionHandler = new RestExceptionHandler();


  @Test
  void handleValidationExceptions_shouldReturnBadRequestWithDetailedFieldErrors() {
    BindingResult bindingResult = mock(BindingResult.class);
    List<org.springframework.validation.ObjectError> mockedErrors = List.of(
        new FieldError("userRequest", "name", "The name must not be blank."),
        new FieldError("userRequest", "password", "The password must be between 5 and 20 characters."),
        new org.springframework.validation.ObjectError("global", "Global error")
    );
    when(bindingResult.getAllErrors()).thenReturn(mockedErrors);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.handleValidationExceptions(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return status 400 BAD_REQUEST");

    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse, "Response body must not be null");
    assertFalse(apiResponse.isSuccess(), "'success' field should be false");
    assertEquals("Validation failed", apiResponse.getMessage(), "The message should be 'Validation failed'");
    assertNull(apiResponse.getData(), "'data' field should be null");
    assertNotNull(apiResponse.getErrors(), "'errors' field should not be null");
    assertEquals(3, apiResponse.getErrors().size(), "Should contain exactly 3 errors");

    assertTrue(apiResponse.getErrors().stream()
            .anyMatch(error -> error.getField().equals("name") && error.getMessage().equals("The name must not be blank.")),
        "Validation error for field 'name' is missing or incorrect");

    assertTrue(apiResponse.getErrors().stream()
            .anyMatch(error -> error.getField().equals("password") && error.getMessage().equals("The password must be between 5 and 20 characters.")),
        "Validation error for field 'password' is missing or incorrect");

    assertTrue(apiResponse.getErrors().stream()
            .anyMatch(error -> error.getField().equals("object") && error.getMessage().equals("Global error")),
        "Global validation error is missing or incorrect");
  }

  @Test
  void handleEmailNotFound_shouldReturnBadRequestWithErrorMessage() {
    List<CustomFieldError> errors = List.of(
        new CustomFieldError("user", "Email not found!")
    );
    UserNotFoundException exception = new UserNotFoundException("Email not found!",errors);
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.emailNotFound(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();

    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Email not found!", apiResponse.getMessage());
    assertNotNull(apiResponse.getErrors());
    assertEquals(errors.size(), apiResponse.getErrors().size());
  }


  @Test
  void categoryNotFound_shouldReturnNotFoundWithError() {
    CategoryNotFoundException exception = new CategoryNotFoundException("Category with id 99 not found.");
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.categoryNotFound(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Category with id 99 not found.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = apiResponse.getErrors().get(0);
    assertEquals("Category", error.getField());
    assertEquals("Category with id 99 not found.", error.getMessage());
  }

  @Test
  void AccountNotFound_shouldReturnNotFoundWithError() {
    AccountNotFoundException exception = new AccountNotFoundException("Account not found.");
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.accountNotFound(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Account not found.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = apiResponse.getErrors().get(0);
    assertEquals("Account", error.getField());
    assertEquals("Account not found.", error.getMessage());
  }

  @Test
  void TransactionNotFound_shouldReturnNotFoundWithError() {
    TransactionNotFoundException exception = new TransactionNotFoundException("Transaction not found.");
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.transactionNotFound(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Transaction not found.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = apiResponse.getErrors().get(0);
    assertEquals("Transaction", error.getField());
    assertEquals("Transaction not found.", error.getMessage());
  }

  @Test
  void goalsNotFound_shouldReturnNotFoundWithError() {
    GoalsNotFoundException exception = new GoalsNotFoundException("Goals not found.");
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.goalsNotFound(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Goals not found.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = apiResponse.getErrors().get(0);
    assertEquals("Goal", error.getField());
    assertEquals("Goals not found.", error.getMessage());
  }

  @Test
  void budgetsNotFound_shouldReturnNotFoundWithError() {
    BudgetsNotFoundException exception = new BudgetsNotFoundException("Budgets not found.");
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response = restExceptionHandler.budgetsNotFound(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiResponse<List<CustomFieldError>> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Budgets not found.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = apiResponse.getErrors().get(0);
    assertEquals("Budgets", error.getField());
    assertEquals("Budgets not found.", error.getMessage());
  }

  @Test
  void shouldReturnBadRequestResponseWithErrors() {
    HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);

    FieldError fieldError = new FieldError("user", "email", "must not be null");
    ObjectError globalError = new ObjectError("user", "invalid user data");
    List<? extends MessageSourceResolvable> mocklist = List.of(fieldError, globalError);
    doReturn(mocklist).when(ex).getAllErrors();
    // when
    ResponseEntity<ApiResponse<List<CustomFieldError>>> response =
        restExceptionHandler.handlePathVariableValidationExceptions(ex);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());

    List<CustomFieldError> errors = response.getBody().getErrors();
    assertEquals(2, errors.size());

    CustomFieldError firstError = errors.get(0);
    assertEquals("email", firstError.getField());
    assertEquals("must not be null", firstError.getMessage());

    CustomFieldError secondError = errors.get(1);
    assertEquals("param", secondError.getField());
    assertEquals("invalid user data", secondError.getMessage());
  }
}
