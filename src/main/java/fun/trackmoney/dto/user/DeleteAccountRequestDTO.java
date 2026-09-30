package fun.trackmoney.dto.user;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequestDTO(
    @NotBlank(message = "Password is required")
    String password) {
}
