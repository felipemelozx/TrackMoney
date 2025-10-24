package fun.trackmoney.account.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
    UUID userId,
    String name,
    BigDecimal balance
) {}