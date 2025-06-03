package fun.trackmoney.transaction.dto;

import java.math.BigDecimal;

public record BillResponseDTO(BigDecimal bill,
                              BigDecimal totalUpcoming,
                              BigDecimal bueSoon) {
}
