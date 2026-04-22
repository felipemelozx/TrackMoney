package fun.trackmoney.dto.auth.internal;

public sealed interface ForgotPasswordResult permits ForgotPasswordSuccess, ForgotPasswordFailure {
}
