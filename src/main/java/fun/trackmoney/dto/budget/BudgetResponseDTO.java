package fun.trackmoney.dto.budget;

import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;

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
