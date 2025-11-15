package fun.trackmoney.budget.dtos.internal;

public sealed interface BudgetResult permits  BudgetSuccess, BudgetFailure{
}
