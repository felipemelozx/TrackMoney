package fun.trackmoney.dto.budget.internal;

import fun.trackmoney.dto.budget.BudgetResponseDTO;

public record BudgetSuccess(BudgetResponseDTO response) implements BudgetResult {
}
