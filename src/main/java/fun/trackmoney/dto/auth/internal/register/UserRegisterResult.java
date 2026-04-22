package fun.trackmoney.dto.auth.internal.register;

public sealed interface UserRegisterResult permits UserRegisterSuccess, UserRegisterFailure {
}
