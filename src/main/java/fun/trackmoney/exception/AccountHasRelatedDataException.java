package fun.trackmoney.exception;

public class AccountHasRelatedDataException extends RuntimeException {

  public AccountHasRelatedDataException(Integer accountId) {
    super("Account " + accountId + " has related financial data and cannot be deleted independently.");
  }
}
