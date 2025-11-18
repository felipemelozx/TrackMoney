package fun.trackmoney.budget.repository;

public interface BudgetCheckProjection {
  boolean getCategoryExists();

  Integer getTotalPercent();
}