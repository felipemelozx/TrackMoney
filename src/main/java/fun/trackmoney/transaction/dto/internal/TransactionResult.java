package fun.trackmoney.transaction.dto.internal;

public sealed interface TransactionResult permits TransactionSuccess, TransactionFailure {
}
