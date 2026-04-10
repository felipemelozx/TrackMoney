package fun.trackmoney.dto.auth.internal.email.verification;

import fun.trackmoney.dto.auth.internal.AuthError;

public record VerificationEmailFailure(AuthError error) implements VerificationEmailResult {
}
