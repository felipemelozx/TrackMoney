package fun.trackmoney.dto.transaction.internal;

public sealed interface TransactionResult permits TransactionSuccess, TransactionFailure {
}
