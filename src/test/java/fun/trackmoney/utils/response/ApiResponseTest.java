package fun.trackmoney.utils.response;

import fun.trackmoney.utils.CustomFieldError;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

  @Test
  void shouldConstructorWithDefaultTimestampWhenUseBuild() {
    ApiResponse response = ApiResponse.builder().build();
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldConstructClassWhenUseAllArgumentBuild() {
    String message = "Operation successful";
    String data = "Some data";

    ApiResponse<String> response = ApiResponse
        .<String>success()
        .data(data)
        .message(message)
        .build();

    assertTrue(response.isSuccess());
    assertEquals(message, response.getMessage());
    assertEquals(data, response.getData());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldCreateSuccessResponseWithEmptyErrors_whenUseSuccessBuilder() {
    ApiResponse<String> response = ApiResponse.<String>success().build();

    assertTrue(response.isSuccess());
    assertTrue(response.getErrors().isEmpty());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldCreateFailureResponse_whenUseFailureBuilder() {
    String errorMessage = "Operation failed";
    List<CustomFieldError> errors = Arrays.asList(
        new CustomFieldError("field1", "Error 1"),
        new CustomFieldError("field2", "Error 2")
    );

    ApiResponse<String> response = ApiResponse.<String>failure()
        .message(errorMessage)
        .errors(errors)
        .build();

    assertFalse(response.isSuccess());
    assertEquals(errorMessage, response.getMessage());
    assertEquals(errors, response.getErrors());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldCreateSuccessResponseWithNoContent_whenUseSuccessWithNoContentBuilder() {
    ApiResponse<String> response = ApiResponse.<String>successWithNoContent().build();

    assertTrue(response.isSuccess());
    assertNull(response.getData());
    assertEquals("Operação realizada com sucesso.", response.getMessage());
    assertTrue(response.getErrors().isEmpty());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldCreateCustomSuccessResponse_whenUseSuccessBuilderWithCustomData() {
    String data = "Custom data";
    String message = "Custom success message";

    ApiResponse<String> response = ApiResponse.<String>success()
        .data(data)
        .message(message)
        .build();

    assertTrue(response.isSuccess());
    assertEquals(data, response.getData());
    assertEquals(message, response.getMessage());
    assertTrue(response.getErrors().isEmpty());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void shouldSetSingleCustomFieldErrorAsListInApiResponse() {
    CustomFieldError error = new CustomFieldError("fieldName", "errorMessage");

    ApiResponse<String> response = ApiResponse.<String>failure()
        .errors(error)
        .build();

    assertNotNull(response);
    assertFalse(response.isSuccess());
    assertNotNull(response.getErrors());
    assertEquals(1, response.getErrors().size());
    assertEquals("fieldName", response.getErrors().get(0).getField());
    assertEquals("errorMessage", response.getErrors().get(0).getMessage());
    assertNull(response.getData());
    assertEquals("ApiResponse", response.getClass().getSimpleName());
  }

  @Test
  void shouldCreateBasicResponse_whenUseGenericBuilder() {
    boolean success = true;
    String message = "Test message";
    String data = "Test data";
    List<CustomFieldError> errors = Collections.singletonList(
        new CustomFieldError("field", "Error message")
    );

    ApiResponse<String> response = ApiResponse.<String>builder()
        .success(success)
        .message(message)
        .data(data)
        .errors(errors)
        .build();

    assertEquals(success, response.isSuccess());
    assertEquals(message, response.getMessage());
    assertEquals(data, response.getData());
    assertEquals(errors, response.getErrors());
    assertNotNull(response.getTimestamp());
  }
}