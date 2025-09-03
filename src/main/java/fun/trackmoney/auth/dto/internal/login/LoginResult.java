package fun.trackmoney.auth.dto.internal.login;

public sealed interface LoginResult permits LoginSuccess, LoginFailure { }