package fun.trackmoney.account.dtos;

import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;

import java.math.BigDecimal;

public record AccountResponseDTO(
    Integer accountId,
    UserResponseDTO user,
    String name,
    BigDecimal balance,
    Boolean isAccountDefault
) {}
