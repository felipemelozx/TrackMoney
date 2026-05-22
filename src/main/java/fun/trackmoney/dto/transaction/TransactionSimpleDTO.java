package fun.trackmoney.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionSimpleDTO(
    Integer transactionId,
    String transactionName,
    BigDecimal amount,
    LocalDateTime transactionDate
) {}
