package fun.trackmoney.dto.auth.internal;

public record ForgotPasswordFailure(AuthError error) implements ForgotPasswordResult {
}
