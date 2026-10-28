package fun.trackmoney.common.result;

import fun.trackmoney.utils.response.ApiResponse;
import fun.trackmoney.utils.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public final class HttpErrorMapper {
  private HttpErrorMapper() { }

  public static HttpStatus statusFor(ErrorCode code) {
    return switch (code) {
      case POT_NOT_FOUND, BUDGET_CATEGORY_NOT_FOUND, BUDGET_NOT_FOUND,
           TRANSACTION_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case AUTH_ERROR_SENDING_EMAIL -> HttpStatus.INTERNAL_SERVER_ERROR;
      case POT_INCOME_EXCEEDS_TARGET, POT_EXPENSE_EXCEEDS_BALANCE, POT_TARGET_BELOW_CURRENT,
           BUDGET_ALREADY_EXISTS, BUDGET_CREATE_CATEGORY_NOT_FOUND, BUDGET_PERCENT_LIMIT_EXCEEDED,
           TRANSACTION_CATEGORY_NOT_FOUND, TRANSACTION_ACCOUNT_NOT_FOUND, USER_EMAIL_ALREADY_EXISTS,
           USER_NOT_REGISTERED, AUTH_INVALID_CREDENTIALS, AUTH_EMAIL_NOT_VERIFIED,
           AUTH_REFRESH_TOKEN_INVALID, AUTH_USER_ALREADY_VERIFIED -> HttpStatus.BAD_REQUEST;
    };
  }

  public static <T> ResponseEntity<ApiResponse<T>> toResponse(AppError error) {
    HttpStatus status = statusFor(error.code());
    String field = error.field() == null ? defaultField(error.code()) : error.field();
    String message = error.message() == null ? error.code().defaultMessage() : error.message();
    ErrorResponse body = new ErrorResponse(error.code().name(), message,
        Map.of(field, message));
    return ResponseEntity.status(status).body(ApiResponse.error(body));
  }

  private static String defaultField(ErrorCode code) {
    return code.defaultField();
  }
}
