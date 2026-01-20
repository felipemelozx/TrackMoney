package fun.trackmoney.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionSimpleDTO(
    Integer transactionId,
    String transactionName,
    BigDecimal amount,
    LocalDateTime transactionDate
) {}
