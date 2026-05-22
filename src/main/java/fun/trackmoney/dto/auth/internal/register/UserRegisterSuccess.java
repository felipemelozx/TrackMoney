package fun.trackmoney.dto.auth.internal.register;

import fun.trackmoney.dto.user.UserResponseDTO;

public record UserRegisterSuccess(UserResponseDTO user) implements UserRegisterResult {
}
