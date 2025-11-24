package fun.trackmoney.pots.dtos;


import java.math.BigDecimal;

public record PotsResponseDTO(Long potId,
                              String name,
                              BigDecimal targetAmount,
                              BigDecimal currentAmount,
                              String color) {
}
