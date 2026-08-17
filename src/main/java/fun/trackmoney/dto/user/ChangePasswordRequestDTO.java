package fun.trackmoney.dto.user;

import fun.trackmoney.utils.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDTO(
    @NotBlank(message = "Current password is required")
    String currentPassword,
    @NotBlank(message = "Password is required")
    @ValidPassword
    String newPassword) {
}