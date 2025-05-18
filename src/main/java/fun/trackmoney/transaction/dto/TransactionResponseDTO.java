package fun.trackmoney.transaction.dto;

import fun.trackmoney.account.dtos.AccountResponseDTO;

import java.math.BigDecimal;

public record TransactionResponseDTO(Integer transactionId,
                                     String description,
                                     BigDecimal amount,
                                     AccountResponseDTO account) {
}
