package fun.trackmoney.auth.dto.internal;

public sealed interface LoginResult permits LoginSuccess, LoginFailure { }