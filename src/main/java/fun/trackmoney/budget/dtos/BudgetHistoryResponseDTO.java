package fun.trackmoney.budget.dtos;

import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.transaction.dto.TransactionSimpleDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    List<TransactionSimpleDTO> transactions,
    BudgetStatus status,
    LocalDateTime createdAt
) {}
