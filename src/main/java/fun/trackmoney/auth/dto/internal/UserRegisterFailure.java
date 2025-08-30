package fun.trackmoney.auth.dto.internal;

public record UserRegisterFailure(AuthError errorList) implements UserRegisterResult {
}
