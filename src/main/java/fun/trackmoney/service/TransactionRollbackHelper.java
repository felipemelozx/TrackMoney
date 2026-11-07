package fun.trackmoney.service;

import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Utility to manage transactional rollback semantics when returning {@link Result.Err}.
 *
 * <p>Spring's {@code @Transactional} commits by default when a method completes normally,
 * even if it returns a {@code Result.err(...)}. This helper allows transactional methods
 * that encounter errors after partial writes to explicitly mark the active transaction
 * as rollback-only before returning a {@code Result.err(...)}, preventing partial commits.</p>
 */
public final class TransactionRollbackHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRollbackHelper.class);

  private TransactionRollbackHelper() {
  }

  /**
   * Marks the current transaction as rollback-only if one is active,
   * and returns a {@link Result.Err} with the specified error code.
   *
   * @param errorCode the error code to include in the Result
   * @param <T>       the result value type
   * @return {@link Result.Err} wrapping the error code
   */
  public static <T> Result<T> rollbackAndErr(ErrorCode errorCode) {
    markRollbackOnlyIfActive();
    return Result.err(errorCode);
  }

  /**
   * Marks the current transaction as rollback-only if one is active,
   * and returns a {@link Result.Err} with the specified error code, field, and message.
   *
   * @param errorCode the error code
   * @param field     the invalid field name
   * @param message   the error description
   * @param <T>       the result value type
   * @return {@link Result.Err} wrapping the error details
   */
  public static <T> Result<T> rollbackAndErr(ErrorCode errorCode, String field, String message) {
    markRollbackOnlyIfActive();
    return Result.err(errorCode, field, message);
  }

  /**
   * Sets the current Spring transaction status to rollback-only if active.
   * Safely ignores when called outside an active transaction.
   */
  public static void markRollbackOnlyIfActive() {
    try {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      LOGGER.debug("Current transaction marked as rollback-only");
    } catch (NoTransactionException ex) {
      LOGGER.trace("No active transaction found to mark as rollback-only: {}", ex.getMessage());
    }
  }
}
