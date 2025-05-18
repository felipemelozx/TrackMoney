package fun.trackmoney.transaction.dto;

import java.math.BigDecimal;

public record TransactionUpdateDTO(String description,
                                   BigDecimal amount,
                                   Integer accountId,
                                   Integer categoryId) {
}
