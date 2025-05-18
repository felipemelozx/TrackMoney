package fun.trackmoney.transaction.dto;

import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record CreateTransactionDTO(Integer accountId,
                                   Integer categoryId,
                                   TransactionType transactionType,
                                   BigDecimal amount,
                                   String description,
                                   Timestamp transactionDate) {
}
