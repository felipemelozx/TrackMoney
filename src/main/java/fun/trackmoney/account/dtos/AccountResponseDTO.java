package fun.trackmoney.account.dtos;

import fun.trackmoney.user.dtos.UserResponseDTO;

import java.math.BigDecimal;

public record AccountResponseDTO(
    Integer accountId,
    UserResponseDTO user,
    String name,
    BigDecimal balance
) {}
