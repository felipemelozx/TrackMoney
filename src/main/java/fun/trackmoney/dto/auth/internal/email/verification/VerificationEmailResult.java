package fun.trackmoney.dto.auth.internal.email.verification;

public sealed interface VerificationEmailResult permits VerificationEmailSuccess, VerificationEmailFailure{
}
