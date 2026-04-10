package fun.trackmoney.dto.budget;

import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.dto.transaction.TransactionSimpleDTO;
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
