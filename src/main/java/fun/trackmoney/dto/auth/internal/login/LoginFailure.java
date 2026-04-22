package fun.trackmoney.dto.auth.internal.login;

import fun.trackmoney.dto.auth.internal.AuthError;

public record LoginFailure(AuthError error) implements LoginResult { }