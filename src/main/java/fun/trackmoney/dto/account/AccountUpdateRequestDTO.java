package fun.trackmoney.dto.account;

import jakarta.validation.constraints.NotBlank;

public record AccountUpdateRequestDTO(
    @NotBlank(message = "Account name is required")
    String name
) {}
