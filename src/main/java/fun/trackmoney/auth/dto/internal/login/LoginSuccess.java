package fun.trackmoney.auth.dto.internal.login;

import fun.trackmoney.auth.dto.LoginResponseDTO;

public record LoginSuccess(LoginResponseDTO tokens) implements LoginResult { }