package fun.trackmoney.transaction.dto.internal;

import fun.trackmoney.transaction.dto.TransactionResponseDTO;

public record TransactionSuccess(TransactionResponseDTO response) implements TransactionResult {
}
