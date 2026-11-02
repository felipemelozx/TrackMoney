package fun.trackmoney.common.result;

import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpErrorMapperTest {

  @Test
  void everyErrorCodeHasAnExplicitHttpClassification() {
    for (ErrorCode code : ErrorCode.values()) {
      assertNotNull(HttpErrorMapper.statusFor(code), code.name());
    }

    assertEquals(HttpStatus.NOT_FOUND, HttpErrorMapper.statusFor(ErrorCode.POT_NOT_FOUND));
    assertEquals(HttpStatus.BAD_REQUEST, HttpErrorMapper.statusFor(ErrorCode.POT_TARGET_BELOW_CURRENT));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorMapper.statusFor(ErrorCode.AUTH_ERROR_SENDING_EMAIL));
  }

  @Test
  void mapsDomainErrorToFailureEnvelopeAndPreservesField() {
    var response = HttpErrorMapper.toResponse(
        new AppError(ErrorCode.POT_TARGET_BELOW_CURRENT, "targetAmount", "Target too low"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ApiResponse<?> body = response.getBody();
    assertNotNull(body);
    assertFalse(body.isSuccess());
    assertEquals("Target too low", body.getMessage());
    assertNull(body.getData());
    assertEquals(1, body.getErrors().size());
    assertEquals("targetAmount", body.getErrors().get(0).getField());
    assertEquals("Target too low", body.getErrors().get(0).getMessage());
  }

  @Test
  void successAndFailureFactoriesHaveConsistentErrorCollections() {
    assertTrue(ApiResponse.success().build().getErrors().isEmpty());
    assertTrue(ApiResponse.failure().build().getErrors().isEmpty());
  }
}
