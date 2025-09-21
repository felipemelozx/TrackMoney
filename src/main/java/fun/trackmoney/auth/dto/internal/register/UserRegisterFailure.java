package fun.trackmoney.auth.dto.internal.register;

import fun.trackmoney.auth.dto.internal.AuthError;

public record UserRegisterFailure(AuthError errorList) implements UserRegisterResult {
}
