package fun.trackmoney.transaction.dto;

import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionUpdateDTO(String description,
                                   BigDecimal amount,
                                   Integer accountId,
                                   Integer categoryId,
                                   TransactionType transactionType) {
}
