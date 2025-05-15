package fun.trackmoney.account.dtos;

import java.math.BigDecimal;

public record AccountUpdateRequestDTO(
    String name,
    Boolean isAccountDefault
) {}
