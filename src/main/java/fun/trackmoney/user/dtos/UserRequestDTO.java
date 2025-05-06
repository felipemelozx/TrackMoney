package fun.trackmoney.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank(message = "Name is required")
    String name,
    @Email(message = "Email inlaid")
    @NotBlank
    String email,
    @NotBlank(message = "Password is required")
    String password) {
}
