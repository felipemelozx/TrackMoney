package fun.trackmoney.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
    UUID userId,
    String name,
    BigDecimal balance
) {}