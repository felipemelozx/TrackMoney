package fun.trackmoney.pots.dtos;

public record PotsResponseDTO(String name,
                              String description,
                              Long targetAmount,
                              Long currentAmount) {
}
