package fun.trackmoney.auth.dto.internal;

public sealed interface ForgotPasswordResult permits ForgotPasswordSuccess, ForgotPasswordFailure {
}
