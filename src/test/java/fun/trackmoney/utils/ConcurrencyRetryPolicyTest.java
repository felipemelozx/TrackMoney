package fun.trackmoney.utils;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrencyRetryPolicyTest {

  @Test
  void execute_shouldReturnResult_whenSuccessfulOnFirstAttempt() {
    ConcurrencyRetryPolicy policy = new ConcurrencyRetryPolicy(3, 5, 2.0, 50);
    String result = policy.execute(() -> "success");
    assertEquals("success", result);
  }

  @Test
  void execute_shouldRetryAndSucceed_whenTransientConcurrencyExceptionOccurs() {
    ConcurrencyRetryPolicy policy = new ConcurrencyRetryPolicy(3, 5, 2.0, 50);
    AtomicInteger attempts = new AtomicInteger(0);

    String result = policy.execute(() -> {
      if (attempts.incrementAndGet() < 2) {
        throw new ObjectOptimisticLockingFailureException("Stale object", new Throwable());
      }
      return "recovered";
    });

    assertEquals("recovered", result);
    assertEquals(2, attempts.get());
  }

  @Test
  void execute_shouldThrowException_whenMaxAttemptsExceeded() {
    ConcurrencyRetryPolicy policy = new ConcurrencyRetryPolicy(3, 5, 2.0, 50);
    AtomicInteger attempts = new AtomicInteger(0);

    assertThrows(CannotAcquireLockException.class, () -> policy.execute(() -> {
      attempts.incrementAndGet();
      throw new CannotAcquireLockException("Lock timed out");
    }));

    assertEquals(3, attempts.get());
  }

  @Test
  void execute_shouldNotRetry_whenNonConcurrencyExceptionOccurs() {
    ConcurrencyRetryPolicy policy = new ConcurrencyRetryPolicy(3, 5, 2.0, 50);
    AtomicInteger attempts = new AtomicInteger(0);

    assertThrows(IllegalArgumentException.class, () -> policy.execute(() -> {
      attempts.incrementAndGet();
      throw new IllegalArgumentException("Invalid argument");
    }));

    assertEquals(1, attempts.get());
  }

  @Test
  void constructor_shouldValidateParameters() {
    assertThrows(IllegalArgumentException.class, () -> new ConcurrencyRetryPolicy(0, 5, 2.0, 50));
  }

  @Test
  void isConcurrencyException_shouldDetectNestedExceptions() {
    assertTrue(ConcurrencyRetryPolicy.isConcurrencyException(
        new RuntimeException(new OptimisticLockException("nested optimistic lock"))));
    assertTrue(ConcurrencyRetryPolicy.isConcurrencyException(
        new RuntimeException(new PessimisticLockException("nested pessimistic lock"))));
    assertTrue(ConcurrencyRetryPolicy.isConcurrencyException(
        new CannotAcquireLockException("lock error")));
    assertFalse(ConcurrencyRetryPolicy.isConcurrencyException(
        new NullPointerException("null pointer")));
  }

  @Test
  void run_shouldExecuteRunnableSuccessfully() {
    ConcurrencyRetryPolicy policy = new ConcurrencyRetryPolicy(3, 5, 2.0, 50);
    AtomicInteger count = new AtomicInteger(0);
    policy.run(count::incrementAndGet);
    assertEquals(1, count.get());
  }
}
