package fun.trackmoney.auth.dto.internal;

public record LoginSuccess(String accessToken, String refreshToken) implements LoginResult { }