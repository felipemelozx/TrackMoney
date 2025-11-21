package fun.trackmoney.budget.dtos;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record BudgetResponseDTO (Integer budgetId,
                                 CategoryEntity category,
                                 AccountResponseDTO account,
                                 short percent,
                                 BigDecimal targetAmount,
                                 BigDecimal currentAmount,
                                 List<TransactionResponseDTO> lastTransactions) {
}
