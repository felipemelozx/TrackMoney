package fun.trackmoney.budget.dtos.internal;

import fun.trackmoney.budget.enums.BudgetError;

public record BudgetFailure(BudgetError reason, String message) implements BudgetResult {
}
