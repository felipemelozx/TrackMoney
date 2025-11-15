package fun.trackmoney.transaction.dto;

import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateDTO(String transactionName,
                                   String description,
                                   BigDecimal amount,
                                   Integer categoryId,
                                   TransactionType transactionType,
                                   LocalDateTime transactionDate) {
}
