package fun.trackmoney.budget.repository;

public interface BudgetCheckProjection {
  Boolean getCategoryExists();
  Integer getTotalPercent();
}