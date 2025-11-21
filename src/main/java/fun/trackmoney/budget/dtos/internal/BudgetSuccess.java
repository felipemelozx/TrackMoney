package fun.trackmoney.budget.dtos.internal;

import fun.trackmoney.budget.dtos.BudgetResponseDTO;

public record BudgetSuccess(BudgetResponseDTO response) implements BudgetResult {
}
