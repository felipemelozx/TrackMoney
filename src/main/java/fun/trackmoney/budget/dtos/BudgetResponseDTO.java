package fun.trackmoney.budget.dtos;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.category.entity.CategoryEntity;

import java.math.BigDecimal;

public record BudgetResponseDTO (Integer budgetId,
                                 CategoryEntity category,
                                 AccountResponseDTO account,
                                 BigDecimal targetAmount,
                                 Integer resetDay) {
}
