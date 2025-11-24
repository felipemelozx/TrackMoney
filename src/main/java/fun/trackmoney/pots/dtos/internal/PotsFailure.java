package fun.trackmoney.pots.dtos.internal;

import fun.trackmoney.pots.enums.PotsErrorType;

public record PotsFailure(PotsErrorType type, String field, String message) implements PotsResult {
}
