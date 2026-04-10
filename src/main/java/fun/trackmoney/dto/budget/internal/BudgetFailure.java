package fun.trackmoney.dto.budget.internal;

import fun.trackmoney.budget.enums.BudgetError;

public record BudgetFailure(BudgetError reason, String message) implements BudgetResult {
}
