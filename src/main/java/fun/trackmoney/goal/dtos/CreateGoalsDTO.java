package fun.trackmoney.goal.dtos;

import java.math.BigDecimal;

public record CreateGoalsDTO(String goal,
                             Integer accountId,
                             BigDecimal targetAmount,
                             BigDecimal currentAmount) {
}

