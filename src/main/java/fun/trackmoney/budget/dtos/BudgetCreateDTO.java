package fun.trackmoney.budget.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetCreateDTO(Integer categoryId,
                              UUID userId,
                              Integer accountId,
                              BigDecimal targetAmount,
                              Integer resetDay) {
}
