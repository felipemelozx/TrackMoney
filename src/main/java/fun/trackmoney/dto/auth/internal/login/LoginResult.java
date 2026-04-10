package fun.trackmoney.dto.auth.internal.login;

public sealed interface LoginResult permits LoginSuccess, LoginFailure { }