package fun.trackmoney.auth.dto.internal.register;

import fun.trackmoney.user.dtos.UserResponseDTO;

public record UserRegisterSuccess(UserResponseDTO user) implements UserRegisterResult {
}
