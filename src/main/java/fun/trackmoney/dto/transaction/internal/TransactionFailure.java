package fun.trackmoney.dto.transaction.internal;

import fun.trackmoney.transaction.enums.TransactionsError;

public record TransactionFailure(TransactionsError error) implements TransactionResult {
}
