package fun.trackmoney.dto.account;

import fun.trackmoney.dto.user.UserResponseDTO;

import java.math.BigDecimal;

public record AccountResponseDTO(
    Integer accountId,
    UserResponseDTO user,
    String name,
    BigDecimal balance
) {}
