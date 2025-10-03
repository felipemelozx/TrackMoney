package fun.trackmoney.transaction.dto.internal;

import fun.trackmoney.transaction.dto.TransactionsError;

public record TransactionFailure(TransactionsError error) implements TransactionResult {
}
