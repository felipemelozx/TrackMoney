package fun.trackmoney.auth.dto.internal.login;

import fun.trackmoney.auth.dto.internal.AuthError;

public record LoginFailure(AuthError error) implements LoginResult { }