package fun.trackmoney.transaction.dto;

import fun.trackmoney.enums.TransactionType;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record CreateTransactionDTO(String transactionName,
                                   Integer categoryId,
                                   TransactionType transactionType,
                                   BigDecimal amount,
                                   @Length(max = 255)
                                   String description,
                                   Timestamp transactionDate) {
}
