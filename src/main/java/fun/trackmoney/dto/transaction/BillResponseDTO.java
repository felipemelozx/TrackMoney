package fun.trackmoney.dto.transaction;

import java.math.BigDecimal;

public record BillResponseDTO(BigDecimal bill,
                              BigDecimal totalUpcoming,
                              BigDecimal bueSoon) {
}
