package fun.trackmoney.budget.dtos;

import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.category.entity.CategoryEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetHistoryResponseDTO(
    Integer historyId,
    Integer budgetId,
    CategoryEntity category,
    Short referenceMonth,
    Integer referenceYear,
    Short percent,
    BigDecimal targetAmount,
    BigDecimal spentAmount,
    BigDecimal remainingAmount,
    BigDecimal totalIncome,
    BigDecimal percentageUsed,
    BudgetStatus status,
    LocalDateTime createdAt
) {}
