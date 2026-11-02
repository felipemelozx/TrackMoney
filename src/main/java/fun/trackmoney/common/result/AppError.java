package fun.trackmoney.common.result;

public record AppError(ErrorCode code, String field, String message) {
  public AppError(ErrorCode code) {
    this(code, null, code.defaultMessage());
  }

  public AppError(ErrorCode code, String message) {
    this(code, null, message == null ? code.defaultMessage() : message);
  }
}
