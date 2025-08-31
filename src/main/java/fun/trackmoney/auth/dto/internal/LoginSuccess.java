package fun.trackmoney.auth.dto.internal;

import fun.trackmoney.auth.dto.LoginResponseDTO;

public record LoginSuccess(LoginResponseDTO tokens) implements LoginResult { }