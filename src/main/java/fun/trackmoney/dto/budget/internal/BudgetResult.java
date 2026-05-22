package fun.trackmoney.dto.budget.internal;

public sealed interface BudgetResult permits  BudgetSuccess, BudgetFailure{
}
