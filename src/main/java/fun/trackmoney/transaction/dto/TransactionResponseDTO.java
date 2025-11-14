package fun.trackmoney.transaction.dto;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(Integer transactionId,
                                     String transactionName,
                                     String description,
                                     BigDecimal amount,
                                     AccountResponseDTO account,
                                     TransactionType transactionType,
                                     CategoryEntity category,
                                     LocalDateTime transactionDate) {
}
