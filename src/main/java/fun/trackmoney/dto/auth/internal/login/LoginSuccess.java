package fun.trackmoney.dto.auth.internal.login;

import fun.trackmoney.dto.auth.LoginResponseDTO;

public record LoginSuccess(LoginResponseDTO tokens) implements LoginResult { }