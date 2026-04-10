package fun.trackmoney.dto.auth.internal.register;

import fun.trackmoney.dto.auth.internal.AuthError;

public record UserRegisterFailure(AuthError errorList) implements UserRegisterResult {
}
