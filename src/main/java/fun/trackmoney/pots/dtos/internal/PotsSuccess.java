package fun.trackmoney.pots.dtos.internal;

import fun.trackmoney.pots.dtos.PotsResponseDTO;

public record PotsSuccess(PotsResponseDTO responseDTO) implements PotsResult {
}
