package fun.trackmoney.auth.dto.internal;

public record LoginFailure(AuthError error) implements LoginResult { }