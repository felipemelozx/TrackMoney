package fun.trackmoney.pots.dtos;

public record PotsResponseDTO(Long potId,
                              String name,
                              String description,
                              Long targetAmount,
                              Long currentAmount) {
}
