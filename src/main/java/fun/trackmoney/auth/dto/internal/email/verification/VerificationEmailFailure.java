package fun.trackmoney.auth.dto.internal.email.verification;

import fun.trackmoney.auth.dto.internal.AuthError;

public record VerificationEmailFailure(AuthError error) implements VerificationEmailResult {
}
