package fun.trackmoney.dto.transaction.internal;

import fun.trackmoney.dto.transaction.TransactionResponseDTO;

public record TransactionSuccess(TransactionResponseDTO response) implements TransactionResult {
}
