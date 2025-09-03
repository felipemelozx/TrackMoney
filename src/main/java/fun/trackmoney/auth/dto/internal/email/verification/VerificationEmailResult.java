package fun.trackmoney.auth.dto.internal.email.verification;

public sealed interface VerificationEmailResult permits VerificationEmailSuccess, VerificationEmailFailure{
}
