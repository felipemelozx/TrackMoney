package fun.trackmoney.service;

import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TransactionRollbackHelperTest {

  @Test
  void rollbackAndErr_withErrorCode_shouldReturnResultErrWithoutThrowingWhenNoActiveTransaction() {
    Result<String> result = TransactionRollbackHelper.rollbackAndErr(ErrorCode.TRANSACTION_ACCOUNT_NOT_FOUND);

    assertInstanceOf(Result.Err.class, result);
    Result.Err<String> err = (Result.Err<String>) result;
    assertEquals(ErrorCode.TRANSACTION_ACCOUNT_NOT_FOUND, err.error().code());
  }

  @Test
  void rollbackAndErr_withFieldAndMessage_shouldReturnResultErrWithDetails() {
    Result<String> result = TransactionRollbackHelper.rollbackAndErr(
        ErrorCode.POT_NOT_FOUND, "id", "Must not be empty");

    assertInstanceOf(Result.Err.class, result);
    Result.Err<String> err = (Result.Err<String>) result;
    assertEquals(ErrorCode.POT_NOT_FOUND, err.error().code());
    assertEquals("id", err.error().field());
    assertEquals("Must not be empty", err.error().message());
  }

  @Test
  void markRollbackOnlyIfActive_shouldNotThrowWhenNoTransactionActive() {
    TransactionRollbackHelper.markRollbackOnlyIfActive();
  }
}
