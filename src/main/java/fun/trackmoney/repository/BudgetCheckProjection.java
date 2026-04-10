package fun.trackmoney.repository;

public interface BudgetCheckProjection {
  boolean getCategoryExists();

  Integer getTotalPercent();
}