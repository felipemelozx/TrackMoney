package fun.trackmoney.transaction.dto.internal;

import fun.trackmoney.transaction.enums.TransactionsError;

public record TransactionFailure(TransactionsError error) implements TransactionResult {
}
