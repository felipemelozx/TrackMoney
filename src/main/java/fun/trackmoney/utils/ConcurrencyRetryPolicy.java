package fun.trackmoney.utils;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ConcurrencyRetryPolicy {

  private static final Logger LOG = LoggerFactory.getLogger(ConcurrencyRetryPolicy.class);

  public static final int DEFAULT_MAX_ATTEMPTS = 3;
  public static final long DEFAULT_INITIAL_DELAY_MS = 50L;
  public static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0;
  public static final long DEFAULT_MAX_DELAY_MS = 500L;

  private final int maxAttempts;
  private final long initialDelayMs;
  private final double backoffMultiplier;
  private final long maxDelayMs;

  @Autowired
  public ConcurrencyRetryPolicy() {
    this(DEFAULT_MAX_ATTEMPTS, DEFAULT_INITIAL_DELAY_MS, DEFAULT_BACKOFF_MULTIPLIER, DEFAULT_MAX_DELAY_MS);
  }

  public ConcurrencyRetryPolicy(int maxAttempts, long initialDelayMs, double backoffMultiplier, long maxDelayMs) {
    if (maxAttempts < 1) {
      throw new IllegalArgumentException("maxAttempts must be at least 1");
    }
    this.maxAttempts = maxAttempts;
    this.initialDelayMs = initialDelayMs;
    this.backoffMultiplier = backoffMultiplier;
    this.maxDelayMs = maxDelayMs;
  }

  public <T> T execute(Supplier<T> operation) {
    int attempt = 0;
    long currentDelay = initialDelayMs;

    while (true) {
      attempt++;
      try {
        return operation.get();
      } catch (Exception ex) {
        if (!isConcurrencyException(ex) || attempt >= maxAttempts) {
          throw ex;
        }

        LOG.warn("Concurrency conflict detected on attempt {}/{}. Retrying after {}ms. Reason: {}",
            attempt, maxAttempts, currentDelay, ex.getMessage());

        sleep(currentDelay);
        currentDelay = Math.min((long) (currentDelay * backoffMultiplier), maxDelayMs);
      }
    }
  }

  public void run(Runnable operation) {
    execute(() -> {
      operation.run();
      return null;
    });
  }

  public static boolean isConcurrencyException(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof ConcurrencyFailureException
          || current instanceof OptimisticLockException
          || current instanceof PessimisticLockException) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Retry backoff interrupted", ie);
    }
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public long getInitialDelayMs() {
    return initialDelayMs;
  }

  public double getBackoffMultiplier() {
    return backoffMultiplier;
  }

  public long getMaxDelayMs() {
    return maxDelayMs;
  }
}
