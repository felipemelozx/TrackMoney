package fun.trackmoney.dto.pots.internal;

import fun.trackmoney.enums.PotsErrorType;

public record PotsFailure(PotsErrorType type, String field, String message) implements PotsResult {
}
