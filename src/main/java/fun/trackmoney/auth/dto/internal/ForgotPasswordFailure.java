package fun.trackmoney.auth.dto.internal;

public record ForgotPasswordFailure(AuthError error) implements ForgotPasswordResult {
}
