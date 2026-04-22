package fun.trackmoney.dto.pots;


import java.math.BigDecimal;

public record PotsResponseDTO(Long potId,
                              String name,
                              BigDecimal targetAmount,
                              BigDecimal currentAmount,
                              String color) {
}
