package fun.trackmoney.dto.pots.internal;

import fun.trackmoney.dto.pots.PotsResponseDTO;

public record PotsSuccess(PotsResponseDTO responseDTO) implements PotsResult {
}
