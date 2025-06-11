package fun.trackmoney.transaction.dto;


import java.math.BigDecimal;

public record TransactionDTO(
    Integer transactionId,
    String description,
    BigDecimal amount,
    Integer accountId
) {
    public static TransactionDTO from(TransactionResponseDTO t) {
        return new TransactionDTO(
            t.transactionId(),
            t.description(),
            t.amount(),
            t.account().accountId()
        );
    }
}
