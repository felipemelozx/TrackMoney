package fun.trackmoney.config.exception;

import fun.trackmoney.user.exception.EmailAlreadyExistsException;
import fun.trackmoney.user.exception.PasswordNotValid;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {

  private final RestExceptionHandler restExceptionHandler = new RestExceptionHandler();

  @Test
  void passwordNotValidShouldReturnBadRequestWithCorrectErrors() {
    List<CustomFieldError> errors = List.of(
        new CustomFieldError("password", "must contain at least one digit"),
        new CustomFieldError("password", "must contain at least one lowercase letter"),
        new CustomFieldError("password", "must contain at least one uppercase letter"),
        new CustomFieldError("password", "must be at least 8 characters long")
    );
    PasswordNotValid exception = new PasswordNotValid(errors);
    ResponseEntity<ApiResponse<List<String>>> response = restExceptionHandler.passwordNotValid(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ApiResponse<List<String>> apiResponse = response.getBody();

    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("Password is invalid", apiResponse.getMessage());
    assertNotNull(apiResponse.getErrors());
    assertEquals(errors.size(), apiResponse.getErrors().size());

    for (int i = 0; i < errors.size(); i++) {
      assertEquals(errors.get(i).getMessage(), apiResponse.getErrors().get(i).getMessage());
    }
  }

  @Test
  void handleEmailAlreadyExistsShouldReturnConflictWithError() {
    EmailAlreadyExistsException exception = new EmailAlreadyExistsException("This email is already in use.");
    ResponseEntity<ApiResponse<Object>> response = restExceptionHandler.handleEmailAlreadyExists(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    ApiResponse<Object> apiResponse = response.getBody();
    assertNotNull(apiResponse);
    assertFalse(apiResponse.isSuccess());
    assertEquals("This email is already in use.", apiResponse.getMessage());
    assertNull(apiResponse.getData());
    assertNotNull(apiResponse.getErrors());
    assertEquals(1, (apiResponse.getErrors()).size());
    CustomFieldError error = (CustomFieldError) ((List<?>) apiResponse.getErrors()).get(0);
    assertEquals("Email", error.getField());
    assertEquals("This email is already in use.", error.getMessage());
  }

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
}
