package fun.trackmoney.utils.response;

import fun.trackmoney.utils.CustomFieldError;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

  @Test
  void defaultConstructorShouldSetTimestamp() {
    ApiResponse<?> response = new ApiResponse<>();
    assertNotNull(response.getTimestamp());
  }

  @Test
  void allArgsConstructorShouldSetAllFieldsCorrectly() {
    boolean success = true;
    String message = "Operation successful";
    String data = "Some data";
    List<CustomFieldError> errors = Arrays.asList(
        new CustomFieldError("field1", "Error message 1"),
        new CustomFieldError("field2", "Error message 2")
    );

    ApiResponse<String> response = new ApiResponse<>(success, message, data, errors);

    assertTrue(response.isSuccess());
    assertEquals(message, response.getMessage());
    assertEquals(data, response.getData());
    assertEquals(errors, response.getErrors());
    assertNotNull(response.getTimestamp());
  }

  @Test
  void settersAndGettersShouldWorkCorrectly() {
    ApiResponse<Integer> response = new ApiResponse<>();

    boolean newSuccess = false;
    String newMessage = "Operation failed";
    Integer newData = 123;
    List<CustomFieldError> newErrors = Collections.singletonList(new CustomFieldError("field3", "Another error"));
    LocalDateTime newTimestamp = LocalDateTime.now().plusDays(1);

    response.setSuccess(newSuccess);
    response.setMessage(newMessage);
    response.setData(newData);
    response.setErrors(newErrors);
    response.setTimestamp(newTimestamp);

    assertFalse(response.isSuccess());
    assertEquals(newMessage, response.getMessage());
    assertEquals(newData, response.getData());
    assertEquals(newErrors, response.getErrors());
    assertEquals(newTimestamp, response.getTimestamp());
  }
}