package fun.trackmoney.common.result;

import java.util.function.Function;

public sealed interface Result<T> permits Result.Ok, Result.Err {
  record Ok<T>(T value) implements Result<T> {
  }

  record Err<T>(AppError error) implements Result<T> {
  }

  static <T> Result<T> ok(T value) {
    return new Ok<>(value);
  }

  static <T> Result<T> err(ErrorCode code) {
    return new Err<>(new AppError(code));
  }

  static <T> Result<T> err(ErrorCode code, String message) {
    return new Err<>(new AppError(code, message));
  }

  static <T> Result<T> err(ErrorCode code, String field, String message) {
    return new Err<>(new AppError(code, field, message == null ? code.defaultMessage() : message));
  }

  default boolean isOk() {
    return this instanceof Ok<?>;
  }

  default boolean isErr() {
    return this instanceof Err<?>;
  }

  default <R> R fold(Function<? super T, ? extends R> onOk,
                     Function<? super AppError, ? extends R> onErr) {
    if (this instanceof Ok<T> ok) {
      return onOk.apply(ok.value());
    }
    return onErr.apply(((Err<T>) this).error());
  }
}
