package fun.trackmoney.user.dtos;

import fun.trackmoney.utils.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank(message = "Name is required")
    String name,
    @Email(message = "Email is invalid")
    @NotBlank
    String email,
    @NotBlank(message = "Password is required")
    @ValidPassword
    String password) {
}
