package fun.trackmoney.pots.dtos;

public record CreatePotsDTO(String name,
                            String description,
                            Integer accountId,
                            Long currentAmount,
                            Long targetAmount) {
}
