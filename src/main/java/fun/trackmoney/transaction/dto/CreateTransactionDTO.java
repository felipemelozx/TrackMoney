package fun.trackmoney.transaction.dto;

import fun.trackmoney.enums.TransactionType;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTransactionDTO(String transactionName,
                                   Integer categoryId,
                                   TransactionType transactionType,
                                   BigDecimal amount,
                                   @Length(max = 255)
                                   String description,
                                   LocalDateTime transactionDate) {
}
