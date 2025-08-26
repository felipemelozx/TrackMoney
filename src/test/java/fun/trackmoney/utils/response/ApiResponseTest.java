package fun.trackmoney.utils.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}